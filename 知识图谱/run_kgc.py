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
import os
from rdflib import Graph, Namespace
from pathlib import Path

# 配置
MYSQL_CONFIG = {
    'host': 'rm-2zewpqd4a1y90c2uujo.mysql.rds.aliyuncs.com',
    'port': 3306,
    'user': 'chenling',
    'password': '1849929630@qqcom',
    'database': 'muse'
}

# 图谱命名空间
RELIC_NS = Namespace('http://example.org/relic/')
MUSEUM_NS = Namespace('http://example.org/museum/')
PERIOD_NS = Namespace('http://example.org/period/')

def create_config_file():
    """创建 Morph-KGC 配置文件"""
    config_path = Path(__file__).parent / 'config.ini'

    config_content = f"""[CONFIGURATION]
output_format = N-TRIPLES
output_file = output.nt

[DataSource]
mappings = mapping.rml.ttl
db_url = mysql+pymysql://{MYSQL_CONFIG['user']}:{MYSQL_CONFIG['password']}@{MYSQL_CONFIG['host']}:{MYSQL_CONFIG['port']}/{MYSQL_CONFIG['database']}
"""

    with open(config_path, 'w', encoding='utf-8') as f:
        f.write(config_content)

    print(f"配置文件已创建: {config_path}")
    return config_path

def materialize_kg():
    """使用 Morph-KGC 将数据库数据映射为 RDF 图谱"""
    config_path = create_config_file()

    print("开始构建知识图谱...")
    print(f"数据源: MySQL @ {MYSQL_CONFIG['host']}/{MYSQL_CONFIG['database']}")
    print("-" * 50)

    try:
        # 调用 Morph-KGC 进行映射
        graph = morph_kgc.materialize(str(config_path))

        if graph is None:
            print("警告: Morph-KGC 返回空图谱")
            return None

        # 获取三元组数量
        triple_count = len(list(graph))
        print(f"成功生成 {triple_count} 条三元组")

        # 保存输出
        output_path = Path(__file__).parent / 'output.nt'
        graph.serialize(destination=str(output_path), format='nt')
        print(f"RDF 数据已保存: {output_path}")

        # 打印部分样例
        print("\n=== 样例三元组 ===")
        for i, triple in enumerate(graph):
            if i >= 10:
                print("... (更多三元组)")
                break
            print(f"  {triple}")

        return graph

    except Exception as e:
        print(f"错误: {e}")
        print("\n可能的解决方案:")
        print("1. 确认 MySQL 数据库连接信息正确")
        print("2. 确认 artifact 和 museum 表存在且有数据")
        print("3. 安装依赖: pip install morph-kgc[mysql] rdflib")
        return None

def export_to_neo4j_csv():
    """将 RDF 图谱导出为 Neo4j 可导入的 CSV 格式"""
    output_nt = Path(__file__).parent / 'output.nt'

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

    nodes_file = Path(__file__).parent / 'neo4j_nodes.csv'
    with open(nodes_file, 'w', encoding='utf-8') as f:
        f.write(":ID,:LABEL\n")
        for node in nodes:
            label = 'Relic' if '/relic/' in str(node) else \
                    'Museum' if '/museum/' in str(node) else \
                    'Period' if '/period/' in str(node) else 'Entity'
            f.write(f"{node},{label}\n")

    # 导出关系 CSV
    rels_file = Path(__file__).parent / 'neo4j_rels.csv'
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
