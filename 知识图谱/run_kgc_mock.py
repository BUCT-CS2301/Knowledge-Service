# -*- coding: utf-8 -*-
"""
知识图谱构建脚本 - Mock 数据版
使用内存中的 Mock 数据构建知识图谱，无需数据库连接

使用方法:
    python run_kgc_mock.py
"""

import pandas as pd
import morph_kgc
from rdflib import Graph, Namespace, Literal, URIRef
from pathlib import Path
import os

# 图谱命名空间
RELIC_NS = Namespace('http://example.org/relic/')
MUSEUM_NS = Namespace('http://example.org/museum/')
PERIOD_NS = Namespace('http://example.org/period/')
SCHEMA_NS = Namespace('http://schema.org/')
EX_NS = Namespace('http://example.org/ontology/')

# ========================================
# Mock 数据
# ========================================

MOCK_MUSEUMS = [
    {
        'object_id': 'm-001',
        'name': 'The British Museum',
        'name_cn': '大英博物馆',
        'location': 'London, United Kingdom',
        'website': 'https://www.britishmuseum.org'
    },
    {
        'object_id': 'm-002',
        'name': 'The Metropolitan Museum of Art',
        'name_cn': '大都会艺术博物馆',
        'location': 'New York, USA',
        'website': 'https://www.metmuseum.org'
    },
    {
        'object_id': 'm-003',
        'name': 'Musée du Louvre',
        'name_cn': '卢浮宫',
        'location': 'Paris, France',
        'website': 'https://www.louvre.fr'
    },
    {
        'object_id': 'm-004',
        'name': 'The Palace Museum',
        'name_cn': '故宫博物院',
        'location': 'Beijing, China',
        'website': 'https://www.dpm.org.cn'
    },
    {
        'object_id': 'm-005',
        'name': 'State Hermitage Museum',
        'name_cn': '艾尔米塔什博物馆',
        'location': 'St. Petersburg, Russia',
        'website': 'https://www.hermitagemuseum.org'
    },
    {
        'object_id': 'm-006',
        'name': 'Freer Gallery of Art',
        'name_cn': '弗利尔美术馆',
        'location': 'Washington D.C., USA',
        'website': 'https://www.freersackler.si.edu'
    }
]

MOCK_RELICS = [
    {
        'object_id': 'r-001',
        'title': '商代青铜鼎',
        'period': '商代',
        'type': '青铜器',
        'material': '青铜',
        'description': '商代晚期青铜祭祀用鼎，造型庄重，纹饰精美，代表了商代青铜文明的最高成就。',
        'dimensions': '高62cm，口径47cm',
        'museum_id': 'm-001',
        'detail_url': 'https://www.britishmuseum.org/collection/object/bronze-vessel',
        'image_url': 'https://upload.wikimedia.org/wikipedia/commons/ding.jpg',
        'credit_line': 'British Museum Collection',
        'accession_number': '1924.0714'
    },
    {
        'object_id': 'r-002',
        'title': '唐代三彩骆驼',
        'period': '唐代',
        'type': '陶器',
        'material': '陶器',
        'description': '唐代巩义窑烧制的三彩骆驼，姿态生动，色彩艳丽，是丝绸之路东西方文化交流的见证。',
        'dimensions': '高78cm，长90cm',
        'museum_id': 'm-002',
        'detail_url': 'https://www.metmuseum.org/collection/sancai-camel',
        'image_url': 'https://upload.wikimedia.org/wikipedia/commons/camel.jpg',
        'credit_line': 'Metropolitan Museum of Art',
        'accession_number': '39.25'
    },
    {
        'object_id': 'r-003',
        'title': '宋代青瓷碗',
        'period': '宋代',
        'type': '瓷器',
        'material': '瓷器',
        'description': '南宋龙泉窑青瓷碗，釉色青翠欲滴，胎质细腻，是龙泉窑精品。',
        'dimensions': '高8.5cm，口径16cm',
        'museum_id': 'm-003',
        'detail_url': 'https://www.louvre.fr/collection/celadon-bowl',
        'image_url': 'https://upload.wikimedia.org/wikipedia/commons/celadon.jpg',
        'credit_line': 'Musée du Louvre',
        'accession_number': 'MNB 1234'
    },
    {
        'object_id': 'r-004',
        'title': '明代青花瓷瓶',
        'period': '明代',
        'type': '瓷器',
        'material': '瓷器',
        'description': '明代永乐年间青花瓷瓶，绘制缠枝莲纹，青花发色浓艳，是明代青花瓷器的代表作。',
        'dimensions': '高38cm',
        'museum_id': 'm-004',
        'detail_url': 'https://www.palace-museum.org/collection/blue-white-vase',
        'image_url': 'https://upload.wikimedia.org/wikipedia/commons/blue_white.jpg',
        'credit_line': 'The Palace Museum',
        'accession_number': '故00001234'
    },
    {
        'object_id': 'r-005',
        'title': '清代珐琅彩碗',
        'period': '清代',
        'type': '瓷器',
        'material': '瓷器',
        'description': '清代乾隆年间珐琅彩碗，彩绘花鸟纹饰，色彩丰富，制作精湛。',
        'dimensions': '高6cm，口径14cm',
        'museum_id': 'm-005',
        'detail_url': 'https://www.hermitage.org/collection/enamel-bowl',
        'image_url': 'https://upload.wikimedia.org/wikipedia/commons/enamel.jpg',
        'credit_line': 'State Hermitage Museum',
        'accession_number': 'HE-12345'
    },
    {
        'object_id': 'r-006',
        'title': '战国玉璧',
        'period': '战国',
        'type': '玉器',
        'material': '玉',
        'description': '战国时期玉璧，玉质温润，雕工精细，表面饰有云纹和谷纹。',
        'dimensions': '直径25cm，厚0.8cm',
        'museum_id': 'm-006',
        'detail_url': 'https://www.freer-sackler.org/collection/jade-bi',
        'image_url': 'https://upload.wikimedia.org/wikipedia/commons/jade_bi.jpg',
        'credit_line': 'Freer Gallery of Art',
        'accession_number': 'F1936.13'
    },
    {
        'object_id': 'r-007',
        'title': '东汉陶俑',
        'period': '汉代',
        'type': '陶器',
        'material': '陶器',
        'description': '东汉时期彩绘陶俑，造型生动，表现了汉代贵族生活的场景。',
        'dimensions': '高35cm',
        'museum_id': 'm-001',
        'detail_url': 'https://www.britishmuseum.org/collection/han-tomb-figures',
        'image_url': 'https://upload.wikimedia.org/wikipedia/commons/han_figurine.jpg',
        'credit_line': 'British Museum',
        'accession_number': '1975.0214.1'
    },
    {
        'object_id': 'r-008',
        'title': '元青花梅瓶',
        'period': '元代',
        'type': '瓷器',
        'material': '瓷器',
        'description': '元代青花人物故事梅瓶，绘制人物故事图案，青花发色浓艳。',
        'dimensions': '高42cm',
        'museum_id': 'm-002',
        'detail_url': 'https://www.metmuseum.org/collection/yuan-meiping',
        'image_url': 'https://upload.wikimedia.org/wikipedia/commons/meiping.jpg',
        'credit_line': 'Metropolitan Museum of Art',
        'accession_number': '1995.21'
    },
    {
        'object_id': 'r-009',
        'title': '西周青铜簋',
        'period': '西周',
        'type': '青铜器',
        'material': '青铜',
        'description': '西周早期青铜簋，器身饰有兽面纹，是研究西周礼制的重要实物资料。',
        'dimensions': '高31cm，口径26cm',
        'museum_id': 'm-003',
        'detail_url': 'https://www.louvre.fr/collection/bronze-gui',
        'image_url': 'https://upload.wikimedia.org/wikipedia/commons/bronze_gui.jpg',
        'credit_line': 'Musée du Louvre',
        'accession_number': 'MNB 5678'
    },
    {
        'object_id': 'r-010',
        'title': '唐代银茶碾',
        'period': '唐代',
        'type': '金银器',
        'material': '银',
        'description': '唐代宫廷御用银茶碾，造型精美，工艺精湛，反映了唐代茶文化的繁荣。',
        'dimensions': '长35cm，宽12cm',
        'museum_id': 'm-004',
        'detail_url': 'https://www.palace-museum.org/collection/silver-tea',
        'image_url': 'https://upload.wikimedia.org/wikipedia/commons/silver_tea.jpg',
        'credit_line': 'The Palace Museum',
        'accession_number': '故00123456'
    }
]


def build_graph_with_rdflib():
    """使用 rdflib 直接构建 RDF 图谱"""
    print("使用 rdflib 构建 RDF 图谱...")

    g = Graph()

    # 绑定命名空间
    g.bind('relic', RELIC_NS)
    g.bind('museum', MUSEUM_NS)
    g.bind('period', PERIOD_NS)
    g.bind('schema', SCHEMA_NS)
    g.bind('ex', EX_NS)

    # 1. 添加博物馆节点
    print("  添加博物馆节点...")
    for museum in MOCK_MUSEUMS:
        museum_uri = MUSEUM_NS[museum['object_id']]

        g.add((museum_uri, SCHEMA_NS['name'], Literal(museum['name'])))
        g.add((museum_uri, SCHEMA_NS['name'], Literal(museum['name_cn'], lang='zh')))
        g.add((museum_uri, SCHEMA_NS['location'], Literal(museum['location'])))
        g.add((museum_uri, SCHEMA_NS['url'], URIRef(museum['website'])))

    # 2. 添加朝代节点
    print("  添加朝代节点...")
    periods = set(r['period'] for r in MOCK_RELICS)
    for period in periods:
        period_uri = PERIOD_NS[period]
        g.add((period_uri, SCHEMA_NS['name'], Literal(period, lang='zh')))

    # 3. 添加文物节点及其关系
    print("  添加文物节点和关系...")
    museum_map = {m['object_id']: m for m in MOCK_MUSEUMS}

    for relic in MOCK_RELICS:
        relic_uri = RELIC_NS[relic['object_id']]

        # 文物属性
        g.add((relic_uri, SCHEMA_NS['name'], Literal(relic['title'])))
        g.add((relic_uri, SCHEMA_NS['description'], Literal(relic['description'])))
        g.add((relic_uri, EX_NS['period'], Literal(relic['period'])))
        g.add((relic_uri, EX_NS['type'], Literal(relic['type'])))
        g.add((relic_uri, EX_NS['material'], Literal(relic['material'])))
        g.add((relic_uri, EX_NS['dimensions'], Literal(relic['dimensions'])))

        if relic['image_url']:
            g.add((relic_uri, SCHEMA_NS['image'], URIRef(relic['image_url'])))

        if relic['detail_url']:
            g.add((relic_uri, SCHEMA_NS['url'], URIRef(relic['detail_url'])))

        # 文物 -> 博物馆 (收藏关系)
        museum_uri = MUSEUM_NS[relic['museum_id']]
        g.add((relic_uri, EX_NS['collectedBy'], museum_uri))

        # 文物 -> 朝代 (属于关系)
        period_uri = PERIOD_NS[relic['period']]
        g.add((relic_uri, EX_NS['belongsTo'], period_uri))

    return g


def save_output(g):
    """保存输出文件"""
    output_dir = Path(__file__).parent

    # 保存为 NT 格式
    nt_file = output_dir / 'output.nt'
    g.serialize(destination=str(nt_file), format='nt')
    print(f"\nRDF 数据已保存: {nt_file}")

    # 保存为 Turtle 格式 (更易读)
    ttl_file = output_dir / 'output.ttl'
    g.serialize(destination=str(ttl_file), format='turtle')
    print(f"Turtle 数据已保存: {ttl_file}")

    # 保存为 Neo4j CSV 导入格式
    save_neo4j_csv(g, output_dir)

    return nt_file, ttl_file


def save_neo4j_csv(g, output_dir):
    """导出为 Neo4j CSV 格式"""
    print("\n导出 Neo4j CSV 格式...")

    nodes = {}
    labels = {}

    # 收集所有节点
    for s, p, o in g:
        s_str = str(s)
        if s_str.startswith('http://example.org/'):
            if s_str not in nodes:
                nodes[s_str] = set()
            nodes[s_str].add(p)

            # 确定标签
            if '/relic/' in s_str:
                labels[s_str] = 'Relic'
            elif '/museum/' in s_str:
                labels[s_str] = 'Museum'
            elif '/period/' in s_str:
                labels[s_str] = 'Period'

        if isinstance(o, URIRef) and str(o).startswith('http://example.org/'):
            o_str = str(o)
            if o_str not in nodes:
                nodes[o_str] = set()
            nodes[o_str].add(p)

            if '/relic/' in o_str:
                labels[o_str] = 'Relic'
            elif '/museum/' in o_str:
                labels[o_str] = 'Museum'
            elif '/period/' in o_str:
                labels[o_str] = 'Period'

    # 写节点 CSV
    nodes_file = output_dir / 'neo4j_nodes.csv'
    with open(nodes_file, 'w', encoding='utf-8') as f:
        f.write(':ID,:LABEL,name\n')
        for node_id, props in nodes.items():
            label = labels.get(node_id, 'Entity')
            # 提取名称
            name = ''
            for prop in props:
                if str(prop) == 'http://schema.org/name':
                    # 重新查询获取name
                    for s, p, o in g:
                        if str(s) == node_id and str(p) == 'http://schema.org/name':
                            if isinstance(o, Literal) and (not hasattr(o, 'lang') or o.lang == 'zh'):
                                name = str(o)
                                break
                            elif isinstance(o, Literal):
                                name = str(o)
                    break
            f.write(f'{node_id},{label},"{name}"\n')

    # 写关系 CSV
    rels_file = output_dir / 'neo4j_rels.csv'
    with open(rels_file, 'w', encoding='utf-8') as f:
        f.write(':START_ID,:END_ID,:TYPE\n')
        for s, p, o in g:
            if isinstance(o, URIRef) and str(o).startswith('http://example.org/'):
                rel_type = str(p).split('/')[-1]
                f.write(f'{s},{o},{rel_type}\n')

    print(f"Neo4j 节点文件: {nodes_file}")
    print(f"Neo4j 关系文件: {rels_file}")


def query_sample(g):
    """查询样例"""
    print("\n" + "=" * 50)
    print("查询样例")
    print("=" * 50)

    # 查询文物数量
    relics_query = """
    SELECT (COUNT(?relic) AS ?count) WHERE {
        ?relic <http://example.org/ontology/period> ?period .
    }
    """
    result = list(g.query(relics_query))
    print(f"\n文物总数: {result[0][0] if result else 0}")

    # 查询博物馆
    print("\n博物馆列表:")
    museum_query = """
    SELECT ?museum ?name WHERE {
        ?museum <http://schema.org/name> ?name .
        FILTER(LANG(?name) = 'zh')
    }
    """
    for row in g.query(museum_query):
        print(f"  {row[1]}")

    # 查询朝代
    print("\n朝代列表:")
    period_query = """
    SELECT ?period WHERE {
        ?period <http://schema.org/name> ?name .
    }
    """
    periods = set()
    for row in g.query(period_query):
        periods.add(str(row[0]).split('/')[-1])
    for p in sorted(periods):
        print(f"  {p}")

    # 查询关系
    print("\n关系示例:")
    rel_query = """
    SELECT ?relic ?museum WHERE {
        ?relic <http://example.org/ontology/collectedBy> ?museum .
    } LIMIT 5
    """
    for i, row in enumerate(g.query(rel_query)):
        relic_name = str(row[0]).split('/')[-1]
        museum_name = str(row[1]).split('/')[-1]
        print(f"  {relic_name} -> {museum_name}")


def print_statistics(g):
    """打印统计信息"""
    print("\n" + "=" * 50)
    print("图谱统计")
    print("=" * 50)

    # 节点统计
    nodes = {}
    for s, p, o in g:
        s_str = str(s)
        if s_str.startswith('http://example.org/'):
            if '/relic/' in s_str:
                nodes['Relic'] = nodes.get('Relic', 0) + 1
            elif '/museum/' in s_str:
                nodes['Museum'] = nodes.get('Museum', 0) + 1
            elif '/period/' in s_str:
                nodes['Period'] = nodes.get('Period', 0) + 1

    print("\n节点数量:")
    for label, count in sorted(nodes.items()):
        print(f"  {label}: {count}")

    # 关系统计
    rels = {}
    for s, p, o in g:
        rel_type = str(p).split('/')[-1]
        rels[rel_type] = rels.get(rel_type, 0) + 1

    print("\n关系数量:")
    for rel_type, count in sorted(rels.items()):
        print(f"  {rel_type}: {count}")

    print(f"\n三元组总数: {len(g)}")


def main():
    print("=" * 50)
    print("Morph-KGC 知识图谱构建工具 (Mock 数据版)")
    print("=" * 50)
    print(f"\nMock 数据:")
    print(f"  博物馆: {len(MOCK_MUSEUMS)} 个")
    print(f"  文物: {len(MOCK_RELICS)} 件")
    print(f"  朝代: {len(set(r['period'] for r in MOCK_RELICS))} 个")

    # 构建图谱
    graph = build_graph_with_rdflib()

    # 保存输出
    save_output(graph)

    # 打印统计
    print_statistics(graph)

    # 查询样例
    query_sample(graph)

    print("\n" + "=" * 50)
    print("构建完成！")
    print("=" * 50)
    print("\n生成的文件:")
    print("  output.nt       - RDF N-Triples 格式")
    print("  output.ttl      - RDF Turtle 格式 (可读)")
    print("  neo4j_nodes.csv - Neo4j 节点导入文件")
    print("  neo4j_rels.csv  - Neo4j 关系导入文件")


if __name__ == '__main__':
    main()
