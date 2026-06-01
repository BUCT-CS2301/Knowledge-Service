# -*- coding: utf-8 -*-
"""
知识图谱构建脚本 - 使用 Morph-KGC
将 MySQL 数据库中的文物数据映射为 RDF 图谱

使用方法:
    python run_kgc.py

依赖安装:
    pip install morph-kgc[mysql] rdflib neo4j
"""

import morph_kgc
import pymysql
from rdflib import Graph, Namespace, URIRef, Literal
from rdflib.namespace import RDF
from pathlib import Path

# 配置
MYSQL_CONFIG = {
    'host': '127.0.0.1',
    'port': 3306,
    'user': 'knowledge',
    'password': 'knowledge123',
    'database': 'muse'
}

# 图谱命名空间
RELIC_NS = Namespace('http://example.org/relic/')
MUSEUM_NS = Namespace('http://example.org/museum/')
PERIOD_NS = Namespace('http://example.org/period/')
SCHEMA = Namespace('http://schema.org/')
BASE_DIR = Path(__file__).parent

def create_config_file():
    """创建 Morph-KGC 配置文件"""
    config_path = BASE_DIR / 'config.ini'
    mapping_path = BASE_DIR / 'mapping.rml.ttl'
    output_path = BASE_DIR / 'output.nt'

    config_content = f"""[CONFIGURATION]
output_format = N-TRIPLES
output_file = {output_path}

[DataSource1]
mappings = {mapping_path}
db_url = mysql+pymysql://{MYSQL_CONFIG['user']}:{MYSQL_CONFIG['password']}@{MYSQL_CONFIG['host']}:{MYSQL_CONFIG['port']}/{MYSQL_CONFIG['database']}
"""

    with open(config_path, 'w', encoding='utf-8') as f:
        f.write(config_content)

    print(f"配置文件已创建: {config_path}")
    return config_path

def build_graph_from_mysql():
    """当 Morph-KGC 未产出数据时，直接从 MySQL 构建 RDF 图谱。"""
    graph = Graph()
    graph.bind("schema", SCHEMA)
    collected_by = URIRef("http://example.org/ontology/collectedBy")
    belongs_to = URIRef("http://example.org/ontology/belongsTo")

    conn = pymysql.connect(
        host=MYSQL_CONFIG["host"],
        port=MYSQL_CONFIG["port"],
        user=MYSQL_CONFIG["user"],
        password=MYSQL_CONFIG["password"],
        database=MYSQL_CONFIG["database"],
        charset="utf8mb4",
    )
    try:
        with conn.cursor(pymysql.cursors.DictCursor) as cursor:
            cursor.execute(
                "SELECT object_id, title, description, period, type, material, image_url, detail_url, museum_id "
                "FROM artifact WHERE is_deleted = 0"
            )
            relics = cursor.fetchall()
            cursor.execute("SELECT object_id, name, location, website FROM museum")
            museums = cursor.fetchall()
    finally:
        conn.close()

    for museum in museums:
        subject = URIRef(f"http://example.org/museum/{museum['object_id']}")
        graph.add((subject, RDF.type, SCHEMA.Museum))
        graph.add((subject, SCHEMA.name, Literal(museum["name"] or "")))
        if museum.get("location"):
            graph.add((subject, SCHEMA.location, Literal(museum["location"])))
        if museum.get("website"):
            graph.add((subject, SCHEMA.url, Literal(museum["website"])))

    for relic in relics:
        subject = URIRef(f"http://example.org/relic/{relic['object_id']}")
        graph.add((subject, RDF.type, SCHEMA.CreativeWork))
        graph.add((subject, SCHEMA.name, Literal(relic["title"] or "")))
        if relic.get("description"):
            graph.add((subject, SCHEMA.description, Literal(relic["description"])))
        if relic.get("period"):
            graph.add((subject, SCHEMA.alternateName, Literal(relic["period"])))
            period_subject = URIRef(f"http://example.org/period/{relic['period']}")
            graph.add((period_subject, RDF.type, SCHEMA.Thing))
            graph.add((period_subject, SCHEMA.name, Literal(relic["period"])))
            graph.add((subject, belongs_to, period_subject))
        if relic.get("type"):
            graph.add((subject, SCHEMA.additionalType, Literal(relic["type"])))
        if relic.get("material"):
            graph.add((subject, SCHEMA.material, Literal(relic["material"])))
        if relic.get("image_url"):
            graph.add((subject, SCHEMA.image, Literal(relic["image_url"])))
        if relic.get("detail_url"):
            graph.add((subject, SCHEMA.url, Literal(relic["detail_url"])))
        if relic.get("museum_id"):
            museum_subject = URIRef(f"http://example.org/museum/{relic['museum_id']}")
            graph.add((subject, collected_by, museum_subject))

    print(f"MySQL 回退构建完成: {len(graph)} 条三元组")
    return graph

def save_graph(graph):
    output_path = BASE_DIR / 'output.nt'
    graph.serialize(destination=str(output_path), format='nt')
    print(f"RDF 数据已保存: {output_path}")
    print("\n=== 样例三元组 ===")
    for i, triple in enumerate(graph):
        if i >= 10:
            print("... (更多三元组)")
            break
        print(f"  {triple}")
    return graph

def materialize_kg():
    """使用 Morph-KGC 将数据库数据映射为 RDF 图谱"""
    config_path = create_config_file()

    print("开始构建知识图谱...")
    print(f"数据源: MySQL @ {MYSQL_CONFIG['host']}/{MYSQL_CONFIG['database']}")
    print("-" * 50)

    try:
        graph = morph_kgc.materialize(str(config_path))
        if graph is None or len(graph) == 0:
            print("警告: Morph-KGC 未生成三元组，改用 MySQL 直接构建 RDF")
            graph = build_graph_from_mysql()
        else:
            print(f"Morph-KGC 成功生成 {len(graph)} 条三元组")

        return save_graph(graph)

    except Exception as e:
        print(f"Morph-KGC 错误: {e}")
        print("改用 MySQL 直接构建 RDF ...")
        try:
            return save_graph(build_graph_from_mysql())
        except Exception as inner:
            print(f"MySQL 回退构建失败: {inner}")
            print("\n可能的解决方案:")
            print("1. 确认 MySQL 数据库连接信息正确")
            print("2. 确认 artifact 和 museum 表存在且有数据")
            print("3. 安装依赖: pip install morph-kgc[mysql] rdflib pymysql")
            return None

def export_to_neo4j_csv():
    """将 RDF 图谱导出为 Neo4j 可导入的 CSV 格式"""
    output_nt = BASE_DIR / 'output.nt'

    if not output_nt.exists():
        print("错误: output.nt 文件不存在，请先运行 materialize_kg()")
        return

    print("\n正在导出为 Neo4j CSV 格式...")

    g = Graph()
    g.parse(str(output_nt), format='nt')

    # 导出节点 CSV
    nodes = set()
    for s, p, o in g:
        nodes.add(s)
        if isinstance(o, str) and o.startswith('http'):
            nodes.add(o)

    nodes_file = BASE_DIR / 'neo4j_nodes.csv'
    with open(nodes_file, 'w', encoding='utf-8') as f:
        f.write(":ID,:LABEL\n")
        for node in nodes:
            label = 'Relic' if '/relic/' in str(node) else \
                    'Museum' if '/museum/' in str(node) else \
                    'Period' if '/period/' in str(node) else 'Entity'
            f.write(f"{node},{label}\n")

    # 导出关系 CSV
    rels_file = BASE_DIR / 'neo4j_rels.csv'
    with open(rels_file, 'w', encoding='utf-8') as f:
        f.write(":START_ID,:END_ID,:TYPE\n")
        for s, p, o in g:
            rel_type = p.split('/')[-1]
            if isinstance(o, str) and o.startswith('http'):
                f.write(f"{s},{o},{rel_type}\n")

    print(f"Neo4j 节点文件: {nodes_file}")
    print(f"Neo4j 关系文件: {rels_file}")
    print("\n导入 Neo4j 命令:")
    print(f"  neo4j-admin import --nodes {nodes_file} --relationships {rels_file}")

def query_sample(graph):
    """查询样例"""
    if graph is None:
        return

    print("\n=== 查询样例 ===")

    # 查询所有文物
    query1 = """
    SELECT ?relic ?name WHERE {
        ?relic <http://schema.org/name> ?name .
        FILTER(CONTAINS(STR(?relic), '/relic/'))
    } LIMIT 5
    """
    print("\n文物列表:")
    for row in graph.query(query1):
        print(f"  {row[1]}")

    # 查询所有博物馆
    query2 = """
    SELECT ?museum ?name WHERE {
        ?museum <http://schema.org/name> ?name .
        FILTER(CONTAINS(STR(?museum), '/museum/'))
    } LIMIT 5
    """
    print("\n博物馆列表:")
    for row in graph.query(query2):
        print(f"  {row[1]}")

if __name__ == '__main__':
    print("=" * 50)
    print("Morph-KGC 知识图谱构建工具")
    print("=" * 50)

    # 构建图谱
    graph = materialize_kg()

    if graph:
        # 查询样例
        query_sample(graph)

        # 导出为 Neo4j 格式
        export_to_neo4j_csv()
    else:
        print("\n图谱构建失败，请检查配置和数据源")
