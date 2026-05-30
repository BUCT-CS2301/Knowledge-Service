#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
按 accession_number（馆藏编号）把 MySQL artifact.image_url 补到远程 Neo4j 现有
Artifact 节点上，并建立 (:Artifact)-[:展示图片]->(:Image {url}) 关系。

只新增 a.imageUrl 属性与 展示图片 关系，不改动现有中文标题/朝代/材质等数据。

用法：
  NEO4J_URI=bolt://39.106.231.119:7687 NEO4J_PASSWORD=password123 \
    python add_images_to_neo4j.py --allow-remote
  python add_images_to_neo4j.py --dry-run --allow-remote
"""

from __future__ import annotations

import argparse
import os
import sys
from urllib.parse import urlparse

import pymysql
from neo4j import GraphDatabase

MYSQL_CONFIG = {
    "host": os.getenv("MYSQL_HOST", "127.0.0.1"),
    "port": int(os.getenv("MYSQL_PORT", "3306")),
    "user": os.getenv("MYSQL_USER", "knowledge"),
    "password": os.getenv("MYSQL_PASSWORD", "knowledge123"),
    "database": os.getenv("MYSQL_DATABASE", "muse"),
    "charset": "utf8mb4",
}

NEO4J_URI = os.getenv("NEO4J_URI", "bolt://39.106.231.119:7687")
NEO4J_USER = os.getenv("NEO4J_USER", "neo4j")
NEO4J_PASSWORD = os.getenv("NEO4J_PASSWORD", "password123")
LOCAL_HOSTS = {"127.0.0.1", "localhost", "::1"}
BATCH_SIZE = 500


def _host_from_uri(uri: str) -> str:
    parsed = urlparse(uri.replace("bolt+routing://", "bolt://").replace("neo4j://", "bolt://"))
    return (parsed.hostname or "").lower()


def fetch_image_map() -> dict[str, dict]:
    conn = pymysql.connect(**MYSQL_CONFIG)
    try:
        with conn.cursor() as cur:
            cur.execute(
                """
                SELECT accession_number, image_url, detail_url
                FROM artifact
                WHERE accession_number IS NOT NULL AND accession_number <> ''
                  AND ((image_url IS NOT NULL AND image_url <> '')
                       OR (detail_url IS NOT NULL AND detail_url <> ''))
                """
            )
            mapping: dict[str, dict] = {}
            for acc, url, detail in cur.fetchall():
                acc = (acc or "").strip()
                if acc and acc not in mapping:
                    mapping[acc] = {
                        "url": (url or "").strip(),
                        "detail": (detail or "").strip(),
                    }
            return mapping
    finally:
        conn.close()


def apply_images(mapping: dict[str, dict], *, dry_run: bool) -> None:
    driver = GraphDatabase.driver(NEO4J_URI, auth=(NEO4J_USER, NEO4J_PASSWORD))
    try:
        with driver.session() as session:
            accs = session.run(
                """
                MATCH (a:Artifact)
                WHERE a.accession_number IS NOT NULL AND trim(a.accession_number) <> ''
                RETURN a.accession_number AS acc
                """
            ).value()
            pairs = []
            for acc in accs:
                key = acc.strip()
                if key in mapping:
                    info = mapping[key]
                    pairs.append({"acc": key, "url": info["url"], "detail": info["detail"]})
            with_img = sum(1 for p in pairs if p["url"])
            with_detail = sum(1 for p in pairs if p["detail"])
            print(f"Neo4j 含 accession 节点: {len(accs)}，可匹配: {len(pairs)}（图片 {with_img} / 原地址 {with_detail}）")

            if dry_run:
                for p in pairs[:5]:
                    print(f"  [dry-run] {p['acc']} -> img={p['url'][:50]} detail={p['detail'][:50]}")
                return

            updated = 0
            for i in range(0, len(pairs), BATCH_SIZE):
                batch = pairs[i : i + BATCH_SIZE]
                session.run(
                    """
                    UNWIND $rows AS row
                    MATCH (a:Artifact {accession_number: row.acc})
                    SET a.imageUrl = CASE WHEN row.url <> '' THEN row.url ELSE a.imageUrl END,
                        a.detailUrl = CASE WHEN row.detail <> '' THEN row.detail ELSE a.detailUrl END
                    FOREACH (_ IN CASE WHEN row.url <> '' THEN [1] ELSE [] END |
                        MERGE (img:Image {url: row.url})
                        MERGE (a)-[:展示图片]->(img)
                    )
                    """,
                    rows=batch,
                )
                updated += len(batch)
                print(f"  已处理批次 {i // BATCH_SIZE + 1}/{(len(pairs) + BATCH_SIZE - 1) // BATCH_SIZE}（累计 {updated}）")

            with_img = session.run(
                """
                MATCH (a:Artifact)-[:展示图片]->(img:Image)
                WHERE img.url IS NOT NULL AND trim(img.url) <> ''
                RETURN count(DISTINCT a) AS c
                """
            ).single()["c"]
            print(f"补图完成：当前含展示图片的 Artifact = {with_img}")
    finally:
        driver.close()


def main() -> None:
    parser = argparse.ArgumentParser(description="按 accession_number 给 Neo4j 补图片")
    parser.add_argument("--dry-run", action="store_true")
    parser.add_argument("--allow-remote", action="store_true")
    args = parser.parse_args()

    if _host_from_uri(NEO4J_URI) not in LOCAL_HOSTS and not args.allow_remote:
        print(f"[拒绝] 远程 Neo4j ({_host_from_uri(NEO4J_URI)}) 需加 --allow-remote")
        sys.exit(1)

    mapping = fetch_image_map()
    print(f"MySQL 可用图片映射: {len(mapping)} 条")
    if not mapping:
        print("MySQL 无可用 image_url，请先运行 import_csv_data.py")
        sys.exit(1)
    apply_images(mapping, dry_run=args.dry_run)


if __name__ == "__main__":
    main()
