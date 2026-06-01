#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
将 museum/data.csv 导入 MySQL 的 museum / artifact 表，并规范化图片字段。

用法：
  python import_csv_data.py
  python import_csv_data.py --csv data.csv --dry-run
"""

from __future__ import annotations

import argparse
import csv
import os
import sys
from pathlib import Path

import pymysql

MYSQL_CONFIG = {
    "host": os.getenv("MYSQL_HOST", "127.0.0.1"),
    "port": int(os.getenv("MYSQL_PORT", "3306")),
    "user": os.getenv("MYSQL_USER", "knowledge"),
    "password": os.getenv("MYSQL_PASSWORD", "knowledge123"),
    "database": os.getenv("MYSQL_DATABASE", "muse"),
    "charset": "utf8mb4",
}

MUSEUMS = {
    "The Cleveland Museum of Art": {
        "object_id": "m-cma",
        "name_cn": "克利夫兰艺术博物馆",
        "location": "Cleveland, Ohio, United States",
        "website": "https://clevelandart.org",
    },
    "The Nelson-Atkins Museum of Art": {
        "object_id": "m-nelson",
        "name_cn": "纳尔逊-阿特金斯艺术博物馆",
        "location": "Kansas City, Missouri, United States",
        "website": "https://art.nelson-atkins.org",
    },
    "University of Pennsylvania Museum of Archaeology and Anthropology": {
        "object_id": "m-penn",
        "name_cn": "宾夕法尼亚大学考古学与人类学博物馆",
        "location": "Philadelphia, Pennsylvania, United States",
        "website": "https://www.penn.museum",
    },
}


def normalize_image_path(path: str) -> str:
    if not path:
        return ""
    return path.strip().replace("\\", "/")


def resolve_image_url(row: dict) -> str:
    url = (row.get("image_url") or "").strip()
    if url and url not in ("null", "undefined"):
        return url

    museum = row.get("museum") or ""
    accession = (row.get("accession_number") or "").strip()
    if accession and "Cleveland" in museum:
        return f"https://openaccess-cdn.clevelandart.org/{accession}/{accession}_web.jpg"

    return ""


def load_csv(csv_path: Path) -> list[dict]:
    with csv_path.open(encoding="utf-8", newline="") as f:
        return list(csv.DictReader(f))


def upsert_museums(cursor, dry_run: bool) -> None:
    for name, meta in MUSEUMS.items():
        if dry_run:
            print(f"[dry-run] museum: {name}")
            continue
        cursor.execute(
            """
            INSERT INTO museum (object_id, name, name_cn, location, website)
            VALUES (%s, %s, %s, %s, %s)
            ON DUPLICATE KEY UPDATE
              name = VALUES(name),
              name_cn = VALUES(name_cn),
              location = VALUES(location),
              website = VALUES(website)
            """,
            (meta["object_id"], name, meta["name_cn"], meta["location"], meta["website"]),
        )


def truncate(value: str | None, max_len: int) -> str | None:
    if value is None:
        return None
    text = value.strip()
    if not text:
        return None
    return text if len(text) <= max_len else text[:max_len]


def upsert_artifacts(cursor, rows: list[dict], dry_run: bool) -> tuple[int, int]:
    inserted = 0
    skipped = 0

    for row in rows:
        museum_name = (row.get("museum") or "").strip()
        museum_meta = MUSEUMS.get(museum_name)
        if not museum_meta:
            skipped += 1
            continue

        object_id = (row.get("object_id") or "").strip()
        title = (row.get("title") or "").strip()
        detail_url = (row.get("detail_url") or "").strip()
        crawl_date = (row.get("crawl_date") or "2026-05-11").strip()
        if not object_id or not title or not detail_url:
            skipped += 1
            continue

        image_url = resolve_image_url(row)
        image_path = normalize_image_path(row.get("image_path") or "")

        payload = (
            object_id,
            truncate(title, 500),
            truncate((row.get("period") or "").strip(), 200),
            truncate((row.get("type") or "").strip(), 100),
            truncate((row.get("material") or "").strip(), 200),
            (row.get("description") or "").strip() or None,
            truncate((row.get("dimensions") or "").strip(), 300),
            museum_meta["object_id"],
            truncate(detail_url, 1000),
            truncate(image_url, 1000) or "",
            truncate(image_path, 500),
            truncate((row.get("credit_line") or "").strip(), 500),
            truncate((row.get("accession_number") or "").strip(), 100),
            crawl_date,
        )

        if dry_run:
            inserted += 1
            continue

        cursor.execute(
            """
            INSERT INTO artifact (
                object_id, title, period, type, material, description, dimensions,
                museum_id, detail_url, image_url, image_path, credit_line,
                accession_number, crawl_date, is_deleted
            ) VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, 0)
            ON DUPLICATE KEY UPDATE
                title = VALUES(title),
                period = VALUES(period),
                type = VALUES(type),
                material = VALUES(material),
                description = VALUES(description),
                dimensions = VALUES(dimensions),
                museum_id = VALUES(museum_id),
                detail_url = VALUES(detail_url),
                image_url = VALUES(image_url),
                image_path = VALUES(image_path),
                credit_line = VALUES(credit_line),
                accession_number = VALUES(accession_number),
                crawl_date = VALUES(crawl_date),
                is_deleted = 0,
                update_time = CURRENT_TIMESTAMP
            """,
            payload,
        )
        inserted += 1

    return inserted, skipped


def main() -> None:
    parser = argparse.ArgumentParser(description="导入 data.csv 到 MySQL")
    parser.add_argument(
        "--csv",
        default=str(Path(__file__).with_name("data.csv")),
        help="CSV 文件路径",
    )
    parser.add_argument("--dry-run", action="store_true", help="仅预览，不写入数据库")
    args = parser.parse_args()

    csv_path = Path(args.csv)
    if not csv_path.is_file():
        print(f"找不到 CSV 文件: {csv_path}", file=sys.stderr)
        sys.exit(1)

    rows = load_csv(csv_path)
    with_url = sum(1 for r in rows if resolve_image_url(r))
    print(f"读取 CSV: {len(rows)} 条，可解析图片 URL: {with_url} 条")

    if args.dry_run:
        upsert_museums(None, True)
        inserted, skipped = upsert_artifacts(None, rows, True)
        print(f"[dry-run] 预计写入/更新 artifact: {inserted}，跳过: {skipped}")
        return

    conn = pymysql.connect(**MYSQL_CONFIG)
    try:
        with conn.cursor() as cursor:
            upsert_museums(cursor, False)
            inserted, skipped = upsert_artifacts(cursor, rows, False)
        conn.commit()
        print(f"导入完成: artifact 写入/更新 {inserted} 条，跳过 {skipped} 条")
    finally:
        conn.close()


if __name__ == "__main__":
    main()
