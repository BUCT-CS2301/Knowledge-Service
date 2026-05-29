"""
Knowledge Graph Builder
Build knowledge graph from CSV data
"""

from rdflib import Graph, Namespace, URIRef, Literal
import pandas as pd
from pathlib import Path
import json

# Define namespaces
RELIC = Namespace('http://example.org/relic/')
MUSEUM = Namespace('http://example.org/museum/')
PERIOD = Namespace('http://example.org/period/')
SCHEMA = Namespace('http://schema.org/')
EX = Namespace('http://example.org/ontology/')

def create_mock_data():
    """Create mock data for relics and museums"""
    print("Creating mock data...")
    
    # Museum data
    museums_data = pd.DataFrame({
        'id': ['m1', 'm2', 'm3', 'm4', 'm5', 'm6'],
        'name': [
            'The British Museum',
            'The Metropolitan Museum of Art',
            'Musee du Louvre',
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
    
    # Relic data
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
    
    # Period data
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
    
    # Save as CSV
    base_dir = Path(__file__).parent / 'kg_data'
    base_dir.mkdir(exist_ok=True)
    
    museums_data.to_csv(base_dir / 'museums.csv', index=False)
    relics_data.to_csv(base_dir / 'relics.csv', index=False)
    periods_data.to_csv(base_dir / 'periods.csv', index=False)
    
    print(f"Data saved to {base_dir}")
    return base_dir

def build_kg_with_rdflib(base_dir):
    """Build knowledge graph using rdflib directly"""
    print("Building knowledge graph...")
    
    g = Graph()
    
    # Read data
    relics_df = pd.read_csv(base_dir / 'relics.csv')
    museums_df = pd.read_csv(base_dir / 'museums.csv')
    periods_df = pd.read_csv(base_dir / 'periods.csv')
    
    # Add museums
    for _, row in museums_df.iterrows():
        museum_uri = MUSEUM[row['id']]
        g.add((museum_uri, SCHEMA['name'], Literal(row['name'])))
        g.add((museum_uri, SCHEMA['name'], Literal(row['name_cn'], lang='zh')))
        g.add((museum_uri, SCHEMA['location'], Literal(row['location'])))
        g.add((museum_uri, SCHEMA['url'], URIRef(row['website'])))
    
    # Add periods
    for _, row in periods_df.iterrows():
        period_uri = PERIOD[row['name']]
        g.add((period_uri, SCHEMA['name'], Literal(row['name'], lang='zh')))
        g.add((period_uri, SCHEMA['description'], Literal(row['description'])))
    
    # Add relics and relations
    for _, row in relics_df.iterrows():
        relic_uri = RELIC[row['id']]
        g.add((relic_uri, SCHEMA['name'], Literal(row['title'])))
        g.add((relic_uri, SCHEMA['description'], Literal(row['description'])))
        g.add((relic_uri, EX['type'], Literal(row['type'])))
        g.add((relic_uri, EX['material'], Literal(row['material'])))
        
        # Relic-museum relation
        museum_uri = MUSEUM[row['museum_id']]
        g.add((relic_uri, EX['collectedBy'], museum_uri))
        
        # Relic-period relation
        period_uri = PERIOD[row['period']]
        g.add((relic_uri, EX['belongsTo'], period_uri))
    
    print(f"Knowledge graph built! Total {len(g)} triples")
    return g

def calculate_positions(nodes):
    """Calculate positions for nodes"""
    import numpy as np
    
    relics = [n for n in nodes if n['type'] == '文物']
    museums = [n for n in nodes if n['type'] == '博物馆']
    periods = [n for n in nodes if n['type'] == '朝代']
    
    # Relics - top arc
    for i, node in enumerate(relics):
        angle = (i / len(relics)) * 3.1416 - 1.5708
        node['x'] = 400 + 250 * np.cos(angle)
        node['y'] = 150 + 50 * np.sin(angle)
    
    # Museums - middle circle
    for i, node in enumerate(museums):
        angle = (i / len(museums)) * 6.2832 - 1.5708
        node['x'] = 400 + 200 * np.cos(angle)
        node['y'] = 320 + 100 * np.sin(angle)
    
    # Periods - bottom arc
    for i, node in enumerate(periods):
        angle = (i / len(periods)) * 3.1416 - 1.5708
        node['x'] = 400 + 250 * np.cos(angle)
        node['y'] = 500 + 50 * np.sin(angle)

def graph_to_json(graph, base_dir):
    """Convert RDF graph to JSON format for frontend"""
    print("Converting to JSON format...")
    
    nodes = {}
    edges = []
    
    # Extract all nodes
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
    
    # Extract properties and relations
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
    
    # Calculate node positions
    node_list = list(nodes.values())
    calculate_positions(node_list)
    
    result = {
        'nodes': node_list,
        'edges': edges
    }
    
    output_path = base_dir / 'knowledge_graph.json'
    with open(output_path, 'w', encoding='utf-8') as f:
        json.dump(result, f, ensure_ascii=False, indent=2)
    
    print(f"JSON data saved to {output_path}")
    return result

def main():
    print("=" * 60)
    print("Knowledge Graph Builder")
    print("=" * 60)
    
    # Create data
    base_dir = create_mock_data()
    
    # Build knowledge graph
    graph = build_kg_with_rdflib(base_dir)
    
    # Convert to JSON
    graph_data = graph_to_json(graph, base_dir)
    
    print("\n" + "=" * 60)
    print("Knowledge graph building completed!")
    print("=" * 60)
    print(f"\nNodes: {len(graph_data['nodes'])}")
    print(f"Edges: {len(graph_data['edges'])}")
    print(f"\nFile location: {base_dir}/knowledge_graph.json")
    print("\nNow you can use this data in your knowledge graph visualization!")

if __name__ == '__main__':
    main()