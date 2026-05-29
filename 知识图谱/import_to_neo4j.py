#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
从 MySQL artifact/museum 表同步数据到 Neo4j（仅用于本地开发库）。

安全策略：
  - 默认连接本地 Neo4j，禁止对远程/生产库执行全量清空
  - 默认使用 MERGE 增量同步，不删除已有节点
  - 全量清空（DETACH DELETE）必须同时满足：--wipe + --i-understand-delete-all
  - 远程地址需额外加 --allow-remote 才会连接（仍禁止 --wipe）

用法：
  python import_to_neo4j.py                  # 本地增量同步（推荐）
  python import_to_neo4j.py --dry-run          # 仅预览，不写库
  python import_to_neo4j.py --wipe --i-understand-delete-all   # 仅本地库全量重建
"""

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

# 默认仅本地；远程请用环境变量 NEO4J_URI 显式指定，且不要配合 --wipe
NEO4J_URI = os.getenv("NEO4J_URI", "bolt://127.0.0.1:7687")
NEO4J_USER = os.getenv("NEO4J_USER", "neo4j")
NEO4J_PASSWORD = os.getenv("NEO4J_PASSWORD", "knowledge123")

LOCAL_HOSTS = {"127.0.0.1", "localhost", "::1"}


def _host_from_uri(uri: str) -> str:
    parsed = urlparse(uri.replace("bolt+routing://", "bolt://").replace("neo4j://", "bolt://"))
    return (parsed.hostname or "").lower()


def _is_local_uri(uri: str) -> bool:
    return _host_from_uri(uri) in LOCAL_HOSTS


def _validate_args(args: argparse.Namespace) -> None:
    host = _host_from_uri(NEO4J_URI)

    if not _is_local_uri(NEO4J_URI) and not args.allow_remote:
        print(
            f"[拒绝] 目标 Neo4j 为远程地址 ({host})，禁止误操作。\n"
            f"       若仅需连接远程库做只读检查，请设置环境变量并加 --allow-remote（仍禁止 --wipe）。\n"
            f"       当前 URI: {NEO4J_URI}"
        )
        sys.exit(1)

    if args.wipe:
        if not _is_local_uri(NEO4J_URI):
            print(f"[拒绝] 禁止对远程库 ({host}) 执行全量清空。")
            sys.exit(1)
        if not args.i_understand_delete_all:
            print(
                "[拒绝] 全量清空需要显式确认：\n"
                "       python import_to_neo4j.py --wipe --i-understand-delete-all"
            )
            sys.exit(1)


def fetch_rows():
    conn = pymysql.connect(**MYSQL_CONFIG)
    try:
        with conn.cursor(pymysql.cursors.DictCursor) as cursor:
            cursor.execute(
                "SELECT object_id, title, period, type, material, description, museum_id, image_url "
                "FROM artifact WHERE is_deleted = 0"
            )
            relics = cursor.fetchall()
            cursor.execute(
                "SELECT object_id, name, name_cn, location, website FROM museum"
            )
            museums = cursor.fetchall()
            return relics, museums
    finally:
        conn.close()


def import_graph(relics, museums, *, wipe: bool = False, dry_run: bool = False):
    driver = GraphDatabase.driver(NEO4J_URI, auth=(NEO4J_USER, NEO4J_PASSWORD))
    try:
        with driver.session() as session:
            existing = session.run("MATCH (n) RETURN count(n) AS c").single()["c"]
            print(f"目标库当前节点数: {existing}  ({NEO4J_URI})")

            if dry_run:
                print(
                    f"[dry-run] 将同步 Relic={len(relics)}, Museum={len(museums)}, "
                    f"wipe={wipe}"
                )
                return

            if wipe:
                print("[警告] 正在清空本地 Neo4j 全部数据…")
                session.run("MATCH (n) DETACH DELETE n")

            # 增量 MERGE，按 objectId 去重，不破坏远程库已有的大规模图谱
            session.run(
                """
                UNWIND $rows AS row
                MERGE (m:Museum {objectId: row.object_id})
                SET m.name = row.name,
                    m.nameCn = row.name_cn,
                    m.location = row.location,
                    m.website = row.website
                """,
                rows=museums,
            )
            session.run(
                """
                UNWIND $rows AS row
                MERGE (r:Relic {objectId: row.object_id})
                SET r.title = row.title,
                    r.period = row.period,
                    r.type = row.type,
                    r.material = row.material,
                    r.description = row.description,
                    r.museumId = row.museum_id,
                    r.imageUrl = row.image_url
                """,
                rows=relics,
            )
            session.run(
                """
                MATCH (r:Relic), (m:Museum)
                WHERE r.museumId = m.objectId
                MERGE (r)-[:COLLECTED_BY]->(m)
                """
            )
            periods = sorted({row["period"] for row in relics if row.get("period")})
            session.run(
                """
                UNWIND $rows AS name
                MERGE (p:Period {name: name})
                """,
                rows=periods,
            )
            session.run(
                """
                MATCH (r:Relic), (p:Period)
                WHERE r.period = p.name
                MERGE (r)-[:BELONGS_TO]->(p)
                """
            )
            relic_count = session.run("MATCH (r:Relic) RETURN count(r) AS c").single()["c"]
            museum_count = session.run("MATCH (m:Museum) RETURN count(m) AS c").single()["c"]
            period_count = session.run("MATCH (p:Period) RETURN count(p) AS c").single()["c"]
            print(
                f"Neo4j 同步完成: Relic={relic_count}, "
                f"Museum={museum_count}, Period={period_count}"
            )
    finally:
        driver.close()


def main():
    parser = argparse.ArgumentParser(description="MySQL → Neo4j 安全同步（默认仅本地、增量）")
    parser.add_argument("--dry-run", action="store_true", help="只预览，不写入 Neo4j")
    parser.add_argument(
        "--wipe",
        action="store_true",
        help="清空后重建（仅允许本地库，且需配合 --i-understand-delete-all）",
    )
    parser.add_argument(
        "--i-understand-delete-all",
        action="store_true",
        help="确认理解全量清空不可恢复",
    )
    parser.add_argument(
        "--allow-remote",
        action="store_true",
        help="允许连接远程 Neo4j（仍禁止 --wipe）",
    )
    args = parser.parse_args()
    _validate_args(args)

    relics, museums = fetch_rows()
    print(f"MySQL 读取: artifact={len(relics)}, museum={len(museums)}")
    import_graph(relics, museums, wipe=args.wipe, dry_run=args.dry_run)


if __name__ == "__main__":
    main()
