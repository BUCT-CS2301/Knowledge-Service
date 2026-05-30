#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
将 MySQL artifact/museum 数据同步到 Neo4j 生产图谱（中文标签/关系）。

用法：
  NEO4J_URI=bolt://39.106.231.119:7687 NEO4J_PASSWORD=password123 \\
    python sync_mysql_to_neo4j.py --allow-remote

  python sync_mysql_to_neo4j.py --dry-run --allow-remote
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

NEO4J_URI = os.getenv("NEO4J_URI", "bolt://127.0.0.1:7687")
NEO4J_USER = os.getenv("NEO4J_USER", "neo4j")
NEO4J_PASSWORD = os.getenv("NEO4J_PASSWORD", "knowledge123")
LOCAL_HOSTS = {"127.0.0.1", "localhost", "::1"}
BATCH_SIZE = 200


def _host_from_uri(uri: str) -> str:
    parsed = urlparse(uri.replace("bolt+routing://", "bolt://").replace("neo4j://", "bolt://"))
    return (parsed.hostname or "").lower()


def _is_local_uri(uri: str) -> bool:
    return _host_from_uri(uri) in LOCAL_HOSTS


def validate_args(args: argparse.Namespace) -> None:
    if not _is_local_uri(NEO4J_URI) and not args.allow_remote:
        print(f"[拒绝] 远程 Neo4j ({_host_from_uri(NEO4J_URI)}) 需加 --allow-remote")
        sys.exit(1)


def fetch_rows():
    conn = pymysql.connect(**MYSQL_CONFIG)
    try:
        with conn.cursor(pymysql.cursors.DictCursor) as cursor:
            cursor.execute(
                """
                SELECT a.object_id, a.title, a.period, a.type, a.material, a.description,
                       a.image_url, a.detail_url, a.museum_id,
                       m.name AS museum_name, m.name_cn AS museum_name_cn, m.location AS museum_location
                FROM artifact a
                LEFT JOIN museum m ON a.museum_id = m.object_id
                WHERE a.is_deleted = 0
                """
            )
            relics = cursor.fetchall()
            cursor.execute(
                "SELECT object_id, name, name_cn, location, website FROM museum"
            )
            museums = cursor.fetchall()
            return relics, museums
    finally:
        conn.close()


def sync_graph(relics, museums, *, dry_run: bool = False) -> None:
    driver = GraphDatabase.driver(NEO4J_URI, auth=(NEO4J_USER, NEO4J_PASSWORD))
    try:
        with driver.session() as session:
            existing = session.run("MATCH (n) RETURN count(n) AS c").single()["c"]
            print(f"目标 Neo4j 当前节点数: {existing} ({NEO4J_URI})")

            if dry_run:
                with_image = sum(1 for r in relics if (r.get("image_url") or "").strip())
                print(f"[dry-run] museum={len(museums)}, artifact={len(relics)}, with_image={with_image}")
                return

            session.run(
                """
                UNWIND $rows AS row
                MERGE (m:Museum {name: row.name})
                SET m.name_en = row.name,
                    m.name_cn = coalesce(row.name_cn, m.name_cn),
                    m.location = coalesce(row.location, m.location),
                    m.website = coalesce(row.website, m.website)
                """,
                rows=museums,
            )

            for i in range(0, len(relics), BATCH_SIZE):
                batch = relics[i : i + BATCH_SIZE]
                session.run(
                    """
                    UNWIND $rows AS row
                    MERGE (a:Artifact {object_id: row.object_id})
                    SET a.title = row.title,
                        a.description = coalesce(row.description, a.description),
                        a.imageUrl = CASE
                            WHEN row.image_url IS NULL OR trim(row.image_url) = '' THEN a.imageUrl
                            ELSE row.image_url
                        END
                    WITH a, row
                    FOREACH (_ IN CASE WHEN row.museum_name IS NULL OR trim(row.museum_name) = '' THEN [] ELSE [1] END |
                        MERGE (m:Museum {name: row.museum_name})
                        SET m.name_en = row.museum_name,
                            m.name_cn = coalesce(row.museum_name_cn, m.name_cn),
                            m.location = coalesce(row.museum_location, m.location)
                        MERGE (a)-[:收藏馆藏]->(m)
                    )
                    WITH a, row
                    FOREACH (_ IN CASE WHEN row.period IS NULL OR trim(row.period) = '' THEN [] ELSE [1] END |
                        MERGE (p:Period {name: row.period})
                        MERGE (a)-[:所属朝代]->(p)
                    )
                    WITH a, row
                    FOREACH (_ IN CASE WHEN row.material IS NULL OR trim(row.material) = '' THEN [] ELSE [1] END |
                        MERGE (mat:Material {name: row.material})
                        MERGE (a)-[:制作材质]->(mat)
                    )
                    WITH a, row
                    FOREACH (_ IN CASE WHEN row.type IS NULL OR trim(row.type) = '' THEN [] ELSE [1] END |
                        MERGE (t:ArtifactType {name: row.type})
                        MERGE (a)-[:文物品类]->(t)
                    )
                    WITH a, row
                    FOREACH (_ IN CASE WHEN row.image_url IS NULL OR trim(row.image_url) = '' THEN [] ELSE [1] END |
                        MERGE (img:Image {url: row.image_url})
                        MERGE (a)-[:展示图片]->(img)
                    )
                    """,
                    rows=batch,
                )
                print(f"  已同步 artifact 批次 {i // BATCH_SIZE + 1}/{(len(relics) + BATCH_SIZE - 1) // BATCH_SIZE}")

            synced = session.run(
                """
                MATCH (a:Artifact)
                WHERE a.object_id IN $ids
                RETURN count(a) AS c
                """,
                ids=[r["object_id"] for r in relics],
            ).single()["c"]
            with_img = session.run(
                """
                MATCH (a:Artifact)-[:展示图片]->(img:Image)
                WHERE a.object_id IN $ids AND img.url IS NOT NULL AND trim(img.url) <> '' AND img.url <> 'unknown'
                RETURN count(DISTINCT a) AS c
                """,
                ids=[r["object_id"] for r in relics],
            ).single()["c"]
            print(f"Neo4j 同步完成: 匹配 Artifact={synced}, 含展示图片={with_img}")
    finally:
        driver.close()


def main() -> None:
    parser = argparse.ArgumentParser(description="MySQL artifact → Neo4j 中文图谱同步")
    parser.add_argument("--dry-run", action="store_true")
    parser.add_argument("--allow-remote", action="store_true")
    args = parser.parse_args()
    validate_args(args)

    relics, museums = fetch_rows()
    print(f"MySQL 读取: artifact={len(relics)}, museum={len(museums)}")
    if not relics:
        print("MySQL 无 artifact 数据，请先运行 import_csv_data.py")
        sys.exit(1)
    sync_graph(relics, museums, dry_run=args.dry_run)


if __name__ == "__main__":
    main()
