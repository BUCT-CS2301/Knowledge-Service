"""
知识图谱构建工具 - 使用 Morph-KGC
从 CSV 数据生成 RDF 知识图谱
"""

import morph_kgc
from rdflib import Graph, Namespace, URIRef, Literal
import pandas as pd
from pathlib import Path
import json

# 定义命名空间
RELIC = Namespace('http://example.org/relic/')
MUSEUM = Namespace('http://example.org/museum/')
PERIOD = Namespace('http://example.org/period/')
SCHEMA = Namespace('http://schema.org/')
EX = Namespace('http://example.org/ontology/')

def create_mock_data():
    """创建模拟的文物和博物馆数据"""
    print("📊 创建模拟数据...")
    
    # 博物馆数据
    museums_data = pd.DataFrame({
        'id': ['m1', 'm2', 'm3', 'm4', 'm5', 'm6'],
        'name': [
            'The British Museum',
            'The Metropolitan Museum of Art',
            'Musée du Louvre',
            'The Palace Museum',
            'State Hermitage Museum',
            'Freer Gallery of Art'
        ],
        'name_cn': [
            '大英博物馆',
            '大都会艺术博物馆',
            '卢浮宫',
            '故宫博物院',
            '艾尔米塔什博物馆',
            '弗利尔美术馆'
        ],
        'location': [
            'London, United Kingdom',
            'New York, USA',
            'Paris, France',
            'Beijing, China',
            'St. Petersburg, Russia',
            'Washington D.C., USA'
        ],
        'website': [
            'https://www.britishmuseum.org',
            'https://www.metmuseum.org',
            'https://www.louvre.fr',
            'https://www.dpm.org.cn',
            'https://www.hermitagemuseum.org',
            'https://www.freersackler.si.edu'
        ]
    })
    
    # 文物数据
    relics_data = pd.DataFrame({
        'id': ['r1', 'r2', 'r3', 'r4', 'r5', 'r6', 'r7', 'r8', 'r9', 'r10'],
        'object_id': ['obj-001', 'obj-002', 'obj-003', 'obj-004', 'obj-005', 'obj-006', 'obj-007', 'obj-008', 'obj-009', 'obj-010'],
        'title': [
            '商代青铜鼎', '唐代三彩骆驼', '宋代青瓷碗',
            '明代青花瓷瓶', '清代珐琅彩碗', '战国玉璧',
            '东汉陶俑', '元青花梅瓶', '西周青铜簋', '唐代银茶碾'
        ],
        'period': ['商代', '唐代', '宋代', '明代', '清代', '战国', '汉代', '元代', '西周', '唐代'],
        'type': ['青铜器', '陶器', '瓷器', '瓷器', '瓷器', '玉器', '陶器', '瓷器', '青铜器', '金银器'],
        'material': ['青铜', '陶器', '瓷器', '瓷器', '瓷器', '玉', '陶器', '瓷器', '青铜', '银'],
        'description': [
            '商代晚期青铜祭祀用鼎，造型庄重，纹饰精美',
            '唐代巩义窑烧制的三彩骆驼，姿态生动',
            '南宋龙泉窑青瓷碗，釉色青翠欲滴',
            '明代永乐年间青花瓷瓶，绘制缠枝莲纹',
            '清代乾隆年间珐琅彩碗，彩绘花鸟纹饰',
            '战国时期玉璧，玉质温润，雕工精细',
            '东汉时期彩绘陶俑，造型生动',
            '元代青花人物故事梅瓶',
            '西周早期青铜簋，器身饰有兽面纹',
            '唐代宫廷御用银茶碾'
        ],
        'museum_id': ['m1', 'm2', 'm3', 'm4', 'm5', 'm6', 'm1', 'm2', 'm3', 'm4']
    })
    
    # 朝代数据
    periods_data = pd.DataFrame({
        'id': ['p1', 'p2', 'p3', 'p4', 'p5', 'p6', 'p7', 'p8', 'p9'],
        'name': ['商代', '西周', '战国', '汉代', '唐代', '宋代', '元代', '明代', '清代'],
        'start_year': ['-1600', '-1046', '-475', '-206', '618', '960', '1271', '1368', '1636'],
        'end_year': ['-1046', '-771', '-221', '220', '907', '1279', '1368', '1644', '1912'],
        'description': [
            '中国历史上第一个有直接文字记载的王朝',
            '中国历史上继商朝之后的第三个王朝',
            '中国历史上继春秋时期之后的大变革时期',
            '中国历史上最强盛的朝代之一',
            '中国历史上最繁荣的朝代之一',
            '中国历史上文化最昌盛的朝代',
            '中国历史上首个由少数民族建立的大一统王朝',
            '中国历史上最后一个汉族建立的大一统王朝',
            '中国历史上最后一个封建王朝'
        ]
    })
    
    # 保存为 CSV
    base_dir = Path(__file__).parent / 'kg_data'
    base_dir.mkdir(exist_ok=True)
    
    museums_data.to_csv(base_dir / 'museums.csv', index=False)
    relics_data.to_csv(base_dir / 'relics.csv', index=False)
    periods_data.to_csv(base_dir / 'periods.csv', index=False)
    
    print(f"✅ 数据已保存到 {base_dir}")
    return base_dir

def create_rml_mapping(base_dir):
    """创建 RML 映射规则"""
    print("📝 创建 RML 映射规则...")
    
    mapping_content = '''@prefix rr: <http://www.w3.org/ns/r2rml#> .
@prefix rml: <http://semweb.mmlab.be/ns/rml#> .
@prefix ql: <http://semweb.mmlab.be/ns/ql#> .
@prefix xsd: <http://www.w3.org/2001/XMLSchema#> .
@prefix schema: <http://schema.org/> .
@prefix ex: <http://example.org/ontology/> .
@prefix relic: <http://example.org/relic/> .
@prefix museum: <http://example.org/museum/> .
@prefix period: <http://example.org/period/> .

# 文物映射
<#RelicMapping>
  a rr:TriplesMap ;
  rml:logicalSource [
    rml:source "relics.csv" ;
    rml:referenceFormulation ql:CSV
  ] ;
  rr:subjectMap [
    rr:template "http://example.org/relic/{id}" ;
    rr:class schema:CreativeWork
  ] ;
  rr:predicateObjectMap [
    rr:predicate schema:name ;
    rr:objectMap [ rml:reference "title" ]
  ] ;
  rr:predicateObjectMap [
    rr:predicate schema:description ;
    rr:objectMap [ rml:reference "description" ]
  ] ;
  rr:predicateObjectMap [
    rr:predicate ex:type ;
    rr:objectMap [ rml:reference "type" ]
  ] ;
  rr:predicateObjectMap [
    rr:predicate ex:material ;
    rr:objectMap [ rml:reference "material" ]
  ] ;
  rr:predicateObjectMap [
    rr:predicate ex:collectedBy ;
    rr:objectMap [ rr:template "http://example.org/museum/{museum_id}" ]
  ] ;
  rr:predicateObjectMap [
    rr:predicate ex:belongsTo ;
    rr:objectMap [ rr:template "http://example.org/period/{period}" ]
  ] .

# 博物馆映射
<#MuseumMapping>
  a rr:TriplesMap ;
  rml:logicalSource [
    rml:source "museums.csv" ;
    rml:referenceFormulation ql:CSV
  ] ;
  rr:subjectMap [
    rr:template "http://example.org/museum/{id}" ;
    rr:class schema:Organization
  ] ;
  rr:predicateObjectMap [
    rr:predicate schema:name ;
    rr:objectMap [ rml:reference "name" ]
  ] ;
  rr:predicateObjectMap [
    rr:predicate schema:name ;
    rr:objectMap [ rml:reference "name_cn" ; rr:language "zh" ]
  ] ;
  rr:predicateObjectMap [
    rr:predicate schema:location ;
    rr:objectMap [ rml:reference "location" ]
  ] ;
  rr:predicateObjectMap [
    rr:predicate schema:url ;
    rr:objectMap [ rml:reference "website" ]
  ] .

# 朝代映射
<#PeriodMapping>
  a rr:TriplesMap ;
  rml:logicalSource [
    rml:source "periods.csv" ;
    rml:referenceFormulation ql:CSV
  ] ;
  rr:subjectMap [
    rr:template "http://example.org/period/{name}" ;
    rr:class schema:Period
  ] ;
  rr:predicateObjectMap [
    rr:predicate schema:name ;
    rr:objectMap [ rml:reference "name" ; rr:language "zh" ]
  ] ;
  rr:predicateObjectMap [
    rr:predicate schema:description ;
    rr:objectMap [ rml:reference "description" ]
  ] .'''
    
    mapping_path = base_dir / 'mapping.rml.ttl'
    mapping_path.write_text(mapping_content, encoding='utf-8')
    print(f"✅ RML 映射已保存到 {mapping_path}")
    return mapping_path

def build_kg_with_morph_kgc(base_dir):
    """使用 Morph-KGC 构建知识图谱"""
    print("🔄 使用 Morph-KGC 构建知识图谱...")
    
    config = f'''[CONFIGURATION]
output_format = N-TRIPLES
output_file = {base_dir}/output.nt

[DataSource1]
mappings = {base_dir}/mapping.rml.ttl
file_path = {base_dir}/relics.csv

[DataSource2]
mappings = {base_dir}/mapping.rml.ttl
file_path = {base_dir}/museums.csv

[DataSource3]
mappings = {base_dir}/mapping.rml.ttl
file_path = {base_dir}/periods.csv'''
    
    try:
        graph = morph_kgc.materialize(config)
        print(f"✅ 知识图谱构建完成！共 {len(graph)} 条三元组")
        return graph
    except Exception as e:
        print(f"⚠️ Morph-KGC 未安装，使用 rdflib 直接构建")
        return build_kg_with_rdflib(base_dir)

def build_kg_with_rdflib(base_dir):
    """备选方案：使用 rdflib 直接构建"""
    print("🔄 使用 rdflib 构建知识图谱...")
    
    g = Graph()
    
    # 读取数据
    relics_df = pd.read_csv(base_dir / 'relics.csv')
    museums_df = pd.read_csv(base_dir / 'museums.csv')
    periods_df = pd.read_csv(base_dir / 'periods.csv')
    
    # 添加博物馆
    for _, row in museums_df.iterrows():
        museum_uri = MUSEUM[row['id']]
        g.add((museum_uri, SCHEMA['name'], Literal(row['name'])))
        g.add((museum_uri, SCHEMA['name'], Literal(row['name_cn'], lang='zh')))
        g.add((museum_uri, SCHEMA['location'], Literal(row['location'])))
        g.add((museum_uri, SCHEMA['url'], URIRef(row['website'])))
    
    # 添加朝代
    for _, row in periods_df.iterrows():
        period_uri = PERIOD[row['name']]
        g.add((period_uri, SCHEMA['name'], Literal(row['name'], lang='zh')))
        g.add((period_uri, SCHEMA['description'], Literal(row['description'])))
    
    # 添加文物及关系
    for _, row in relics_df.iterrows():
        relic_uri = RELIC[row['id']]
        g.add((relic_uri, SCHEMA['name'], Literal(row['title'])))
        g.add((relic_uri, SCHEMA['description'], Literal(row['description'])))
        g.add((relic_uri, EX['type'], Literal(row['type'])))
        g.add((relic_uri, EX['material'], Literal(row['material'])))
        
        # 文物-博物馆关系
        museum_uri = MUSEUM[row['museum_id']]
        g.add((relic_uri, EX['collectedBy'], museum_uri))
        
        # 文物-朝代关系
        period_uri = PERIOD[row['period']]
        g.add((relic_uri, EX['belongsTo'], period_uri))
    
    print(f"✅ 知识图谱构建完成！共 {len(g)} 条三元组")
    return g

def graph_to_json(graph):
    """将 RDF 图谱转换为前端可使用的 JSON 格式"""
    print("📋 转换为 JSON 格式...")
    
    nodes = {}
    edges = []
    
    # 提取所有节点
    for s, p, o in graph:
        s_str = str(s)
        
        if s_str.startswith('http://example.org/relic/'):
            node_id = s_str.split('/')[-1]
            if node_id not in nodes:
                nodes[node_id] = {
                    'id': node_id,
                    'type': '文物',
                    'label': '',
                    'x': 0,
                    'y': 0
                }
        
        elif s_str.startswith('http://example.org/museum/'):
            node_id = s_str.split('/')[-1]
            if node_id not in nodes:
                nodes[node_id] = {
                    'id': node_id,
                    'type': '博物馆',
                    'label': '',
                    'x': 0,
                    'y': 0
                }
        
        elif s_str.startswith('http://example.org/period/'):
            node_id = s_str.split('/')[-1]
            if node_id not in nodes:
                nodes[node_id] = {
                    'id': node_id,
                    'type': '朝代',
                    'label': '',
                    'x': 0,
                    'y': 0
                }
    
    # 提取属性和关系
    for s, p, o in graph:
        s_str = str(s)
        node_id = s_str.split('/')[-1]
        
        if node_id in nodes:
            p_str = str(p)
            
            if p_str == 'http://schema.org/name':
                if isinstance(o, Literal):
                    if not hasattr(o, 'lang') or o.lang == 'zh':
                        nodes[node_id]['label'] = str(o)
            
            if p_str == 'http://example.org/ontology/collectedBy':
                target_id = str(o).split('/')[-1]
                edges.append({
                    'source': node_id,
                    'target': target_id,
                    'relationType': '收藏于'
                })
            
            if p_str == 'http://example.org/ontology/belongsTo':
                target_id = str(o).split('/')[-1]
                edges.append({
                    'source': node_id,
                    'target': target_id,
                    'relationType': '属于'
                })
    
    # 计算节点位置 - 力导向布局
    node_list = list(nodes.values())
    calculate_positions(node_list)
    
    result = {
        'nodes': node_list,
        'edges': edges
    }
    
    output_path = base_dir / 'knowledge_graph.json'
    with open(output_path, 'w', encoding='utf-8') as f:
        json.dump(result, f, ensure_ascii=False, indent=2)
    
    print(f"✅ JSON 数据已保存到 {output_path}")
    return result

def calculate_positions(nodes):
    """为节点计算位置"""
    relics = [n for n in nodes if n['type'] == '文物']
    museums = [n for n in nodes if n['type'] == '博物馆']
    periods = [n for n in nodes if n['type'] == '朝代']
    
    # 文物 - 顶部弧形
    for i, node in enumerate(relics):
        angle = (i / len(relics)) * 3.1416 - 1.5708
        node['x'] = 400 + 250 * np.cos(angle)
        node['y'] = 150 + 50 * np.sin(angle)
    
    # 博物馆 - 中间环形
    for i, node in enumerate(museums):
        angle = (i / len(museums)) * 6.2832 - 1.5708
        node['x'] = 400 + 200 * np.cos(angle)
        node['y'] = 320 + 100 * np.sin(angle)
    
    # 朝代 - 底部弧形
    for i, node in enumerate(periods):
        angle = (i / len(periods)) * 3.1416 - 1.5708
        node['x'] = 400 + 250 * np.cos(angle)
        node['y'] = 500 + 50 * np.sin(angle)

import numpy as np

def main():
    print("=" * 60)
    print("Knowledge Graph Builder - Morph-KGC Version")
    print("=" * 60)
    
    # 创建数据
    base_dir = create_mock_data()
    
    # 创建映射
    create_rml_mapping(base_dir)
    
    # 构建知识图谱
    graph = build_kg_with_morph_kgc(base_dir)
    
    # 转换为 JSON
    graph_data = graph_to_json(graph)
    
    print("\n" + "=" * 60)
    print("✨ 知识图谱构建完成！")
    print("=" * 60)
    print(f"\n节点数量: {len(graph_data['nodes'])}")
    print(f"关系数量: {len(graph_data['edges'])}")
    print(f"\n文件位置: {base_dir}/knowledge_graph.json")
    print("\n现在可以启动后端服务来加载这个知识图谱了！")

if __name__ == '__main__':
    main()