#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""从 MySQL artifact/museum 表导入数据到本地 Neo4j。"""

import pymysql
from neo4j import GraphDatabase

MYSQL_CONFIG = {
    "host": "127.0.0.1",
    "port": 3306,
    "user": "knowledge",
    "password": "knowledge123",
    "database": "muse",
    "charset": "utf8mb4",
}

NEO4J_URI = "bolt://127.0.0.1:7687"
NEO4J_AUTH = ("neo4j", "knowledge123")


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


def import_graph(relics, museums):
    driver = GraphDatabase.driver(NEO4J_URI, auth=NEO4J_AUTH)
    with driver.session() as session:
        session.run("MATCH (n) DETACH DELETE n")
        session.run(
            """
            UNWIND $rows AS row
            CREATE (:Museum {
                objectId: row.object_id,
                name: row.name,
                nameCn: row.name_cn,
                location: row.location,
                website: row.website
            })
            """,
            rows=museums,
        )
        session.run(
            """
            UNWIND $rows AS row
            CREATE (:Relic {
                objectId: row.object_id,
                title: row.title,
                period: row.period,
                type: row.type,
                material: row.material,
                description: row.description,
                museumId: row.museum_id,
                imageUrl: row.image_url
            })
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
            MERGE (:Period {name: name})
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
            f"Neo4j 导入完成: Relic={relic_count}, "
            f"Museum={museum_count}, Period={period_count}"
        )
    driver.close()


if __name__ == "__main__":
    relics, museums = fetch_rows()
    print(f"MySQL 读取: artifact={len(relics)}, museum={len(museums)}")
    import_graph(relics, museums)
