# -*- coding: utf-8 -*-
"""
测试数据生成脚本
向 MySQL 数据库插入文物、博物馆测试数据

使用方法:
    python init_test_data.py
"""

import pymysql
import uuid
from datetime import datetime, timedelta
import random

# MySQL 配置
MYSQL_CONFIG = {
    'host': 'rm-2zewpqd4a1y90c2uujo.mysql.rds.aliyuncs.com',
    'port': 3306,
    'user': 'chenling',
    'password': '1849929630@qqcom',
    'database': 'muse',
    'charset': 'utf8mb4'
}

# 测试博物馆数据
MUSEUMS = [
    {
        'object_id': str(uuid.uuid4()),
        'name': 'The British Museum',
        'name_cn': '大英博物馆',
        'location': 'London, United Kingdom',
        'website': 'https://www.britishmuseum.org'
    },
    {
        'object_id': str(uuid.uuid4()),
        'name': 'The Metropolitan Museum of Art',
        'name_cn': '大都会艺术博物馆',
        'location': 'New York, USA',
        'website': 'https://www.metmuseum.org'
    },
    {
        'object_id': str(uuid.uuid4()),
        'name': 'Musée du Louvre',
        'name_cn': '卢浮宫',
        'location': 'Paris, France',
        'website': 'https://www.louvre.fr'
    },
    {
        'object_id': str(uuid.uuid4()),
        'name': 'The Palace Museum',
        'name_cn': '故宫博物院',
        'location': 'Beijing, China',
        'website': 'https://www.dpm.org.cn'
    },
    {
        'object_id': str(uuid.uuid4()),
        'name': 'State Hermitage Museum',
        'name_cn': '艾尔米塔什博物馆',
        'location': 'St. Petersburg, Russia',
        'website': 'https://www.hermitagemuseum.org'
    },
    {
        'object_id': str(uuid.uuid4()),
        'name': 'Freer Gallery of Art',
        'name_cn': '弗利尔美术馆',
        'location': 'Washington D.C., USA',
        'website': 'https://www.freersackler.si.edu'
    }
]

# 测试文物数据
RELICS = [
    {
        'title': '商代青铜鼎',
        'period': '商代',
        'type': '青铜器',
        'material': '青铜',
        'description': '商代晚期青铜祭祀用鼎，造型庄重，纹饰精美，代表了商代青铜文明的最高成就。',
        'dimensions': '高62cm，口径47cm',
        'detail_url': 'https://www.britishmuseum.org/collection/object/bronze-vessel',
        'image_url': 'https://upload.wikimedia.org/wikipedia/commons/ding.jpg',
        'credit_line': 'British Museum Collection',
        'accession_number': '1924.0714'
    },
    {
        'title': '唐代三彩骆驼',
        'period': '唐代',
        'type': '陶器',
        'material': '陶器',
        'description': '唐代巩义窑烧制的三彩骆驼，姿态生动，色彩艳丽，是丝绸之路东西方文化交流的见证。',
        'dimensions': '高78cm，长90cm',
        'detail_url': 'https://www.metmuseum.org/collection/sancai-camel',
        'image_url': 'https://upload.wikimedia.org/wikipedia/commons/camel.jpg',
        'credit_line': 'Metropolitan Museum of Art',
        'accession_number': '39.25'
    },
    {
        'title': '宋代青瓷碗',
        'period': '宋代',
        'type': '瓷器',
        'material': '瓷器',
        'description': '南宋龙泉窑青瓷碗，釉色青翠欲滴，胎质细腻，是龙泉窑精品。',
        'dimensions': '高8.5cm，口径16cm',
        'detail_url': 'https://www.louvre.fr/collection/celadon-bowl',
        'image_url': 'https://upload.wikimedia.org/wikipedia/commons/celadon.jpg',
        'credit_line': 'Musée du Louvre',
        'accession_number': 'MNB 1234'
    },
    {
        'title': '明代青花瓷瓶',
        'period': '明代',
        'type': '瓷器',
        'material': '瓷器',
        'description': '明代永乐年间青花瓷瓶，绘制缠枝莲纹，青花发色浓艳，是明代青花瓷器的代表作。',
        'dimensions': '高38cm',
        'detail_url': 'https://www.palace-museum.org/collection/blue-white-vase',
        'image_url': 'https://upload.wikimedia.org/wikipedia/commons/blue_white.jpg',
        'credit_line': 'The Palace Museum',
        'accession_number': '故00001234'
    },
    {
        'title': '清代珐琅彩碗',
        'period': '清代',
        'type': '瓷器',
        'material': '瓷器',
        'description': '清代乾隆年间珐琅彩碗，彩绘花鸟纹饰，色彩丰富，制作精湛，体现了清代宫廷工艺的高超水平。',
        'dimensions': '高6cm，口径14cm',
        'detail_url': 'https://www.hermitage.org/collection/enamel-bowl',
        'image_url': 'https://upload.wikimedia.org/wikipedia/commons/enamel.jpg',
        'credit_line': 'State Hermitage Museum',
        'accession_number': 'HE-12345'
    },
    {
        'title': '战国玉璧',
        'period': '战国',
        'type': '玉器',
        'material': '玉',
        'description': '战国时期玉璧，玉质温润，雕工精细，表面饰有云纹和谷纹，是战国玉器的典型代表。',
        'dimensions': '直径25cm，厚0.8cm',
        'detail_url': 'https://www.freer-sackler.org/collection/jade-bi',
        'image_url': 'https://upload.wikimedia.org/wikipedia/commons/jade_bi.jpg',
        'credit_line': 'Freer Gallery of Art, Smithsonian Institution',
        'accession_number': 'F1936.13'
    },
    {
        'title': '东汉陶俑',
        'period': '汉代',
        'type': '陶器',
        'material': '陶器',
        'description': '东汉时期彩绘陶俑，造型生动，表现了汉代贵族生活的场景。',
        'dimensions': '高35cm',
        'detail_url': 'https://www.britishmuseum.org/collection/han-tomb-figures',
        'image_url': 'https://upload.wikimedia.org/wikipedia/commons/han_figurine.jpg',
        'credit_line': 'British Museum',
        'accession_number': '1975.0214.1'
    },
    {
        'title': '元青花梅瓶',
        'period': '元代',
        'type': '瓷器',
        'material': '瓷器',
        'description': '元代青花人物故事梅瓶，绘制人物故事图案，青花发色浓艳，是元代青花瓷的珍品。',
        'dimensions': '高42cm',
        'detail_url': 'https://www.metmuseum.org/collection/yuan-meiping',
        'image_url': 'https://upload.wikimedia.org/wikipedia/commons/meiping.jpg',
        'credit_line': 'Metropolitan Museum of Art',
        'accession_number': '1995.21'
    },
    {
        'title': '西周青铜簋',
        'period': '西周',
        'type': '青铜器',
        'material': '青铜',
        'description': '西周早期青铜簋，器身饰有兽面纹，是研究西周礼制的重要实物资料。',
        'dimensions': '高31cm，口径26cm',
        'detail_url': 'https://www.louvre.fr/collection/bronze-gui',
        'image_url': 'https://upload.wikimedia.org/wikipedia/commons/bronze_gui.jpg',
        'credit_line': 'Musée du Louvre',
        'accession_number': 'MNB 5678'
    },
    {
        'title': '唐代银茶碾',
        'period': '唐代',
        'type': '金银器',
        'material': '银',
        'description': '唐代宫廷御用银茶碾，造型精美，工艺精湛，反映了唐代茶文化的繁荣。',
        'dimensions': '长35cm，宽12cm',
        'detail_url': 'https://www.palace-museum.org/collection/silver-tea',
        'image_url': 'https://upload.wikimedia.org/wikipedia/commons/silver_tea.jpg',
        'credit_line': 'The Palace Museum',
        'accession_number': '故00123456'
    }
]


def create_tables(cursor):
    """创建必要的表"""
    # 创建 museum 表
    cursor.execute("""
        CREATE TABLE IF NOT EXISTS museum (
            object_id VARCHAR(36) PRIMARY KEY,
            name VARCHAR(200) NOT NULL,
            name_cn VARCHAR(200),
            location VARCHAR(200),
            website VARCHAR(500)
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
    """)

    # 创建 artifact 表
    cursor.execute("""
        CREATE TABLE IF NOT EXISTS artifact (
            object_id VARCHAR(36) PRIMARY KEY,
            title VARCHAR(500) NOT NULL,
            period VARCHAR(200),
            type VARCHAR(100),
            material VARCHAR(200),
            description TEXT,
            dimensions VARCHAR(300),
            museum_id VARCHAR(36),
            detail_url VARCHAR(1000) NOT NULL,
            image_url VARCHAR(1000),
            image_path VARCHAR(500),
            credit_line VARCHAR(500),
            accession_number VARCHAR(100),
            crawl_date DATE NOT NULL,
            create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
            update_time DATETIME ON UPDATE CURRENT_TIMESTAMP,
            is_deleted TINYINT DEFAULT 0,
            INDEX idx_museum (museum_id),
            INDEX idx_type (type),
            INDEX idx_period (period)
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
    """)

    print("表创建完成")


def insert_museums(cursor):
    """插入博物馆数据"""
    print("\n插入博物馆数据...")

    for museum in MUSEUMS:
        try:
            cursor.execute("""
                INSERT IGNORE INTO museum (object_id, name, name_cn, location, website)
                VALUES (%s, %s, %s, %s, %s)
            """, (museum['object_id'], museum['name'], museum['name_cn'],
                  museum['location'], museum['website']))
            print(f"  + {museum['name_cn']}")
        except Exception as e:
            print(f"  ! {museum['name_cn']}: {e}")

    return {m['name']: m['object_id'] for m in MUSEUMS}


def insert_relics(cursor, museum_ids):
    """插入文物数据"""
    print("\n插入文物数据...")

    # 博物馆名称到object_id的映射
    museum_name_to_id = museum_ids

    for i, relic in enumerate(RELICS):
        object_id = str(uuid.uuid4())

        # 随机分配博物馆（循环分配）
        museum_names = list(museum_name_to_id.keys())
        museum_name = museum_names[i % len(museum_names)]
        museum_id = museum_name_to_id[museum_name]

        # 随机日期
        days_ago = random.randint(1, 365)
        crawl_date = (datetime.now() - timedelta(days=days_ago)).strftime('%Y-%m-%d')

        try:
            cursor.execute("""
                INSERT IGNORE INTO artifact (
                    object_id, title, period, type, material, description,
                    dimensions, museum_id, detail_url, image_url,
                    credit_line, accession_number, crawl_date
                ) VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
            """, (
                object_id,
                relic['title'],
                relic['period'],
                relic['type'],
                relic['material'],
                relic['description'],
                relic['dimensions'],
                museum_id,
                relic['detail_url'],
                relic['image_url'],
                relic['credit_line'],
                relic['accession_number'],
                crawl_date
            ))
            print(f"  + {relic['title']} ({museum_name})")
        except Exception as e:
            print(f"  ! {relic['title']}: {e}")


def check_data(cursor):
    """检查现有数据"""
    print("\n检查现有数据...")

    # 检查 museum
    cursor.execute("SELECT COUNT(*) FROM museum")
    museum_count = cursor.fetchone()[0]
    print(f"  博物馆: {museum_count} 条")

    # 检查 artifact
    cursor.execute("SELECT COUNT(*) FROM artifact WHERE is_deleted = 0")
    artifact_count = cursor.fetchone()[0]
    print(f"  文物: {artifact_count} 条")

    return museum_count, artifact_count


def main():
    print("=" * 50)
    print("测试数据生成脚本")
    print("=" * 50)
    print(f"\n目标数据库: {MYSQL_CONFIG['host']}/{MYSQL_CONFIG['database']}")

    try:
        connection = pymysql.connect(**MYSQL_CONFIG)
        cursor = connection.cursor()

        # 检查现有数据
        museum_count, artifact_count = check_data(cursor)

        if museum_count > 0 or artifact_count > 0:
            response = input("\n数据库中已有数据，是否清空并重新插入？(y/N): ")
            if response.lower() == 'y':
                print("\n清空现有数据...")
                cursor.execute("DELETE FROM artifact")
                cursor.execute("DELETE FROM museum")
                connection.commit()
            else:
                print("保留现有数据，添加新数据...")

        # 创建表
        create_tables(cursor)

        # 插入数据
        museum_ids = insert_museums(cursor)
        insert_relics(cursor, museum_ids)

        connection.commit()

        # 再次检查
        print("\n" + "-" * 50)
        museum_count, artifact_count = check_data(cursor)

        print("\n" + "=" * 50)
        print("数据生成完成！")
        print("=" * 50)
        print("\n下一步:")
        print("  1. 运行 python run_kgc.py 构建知识图谱")
        print("  2. 访问前端查看知识图谱可视化")

        cursor.close()
        connection.close()

    except Exception as e:
        print(f"\n错误: {e}")
        print("\n请检查:")
        print("  1. MySQL 服务器是否可达")
        print("  2. 用户名密码是否正确")
        print("  3. 数据库是否存在")


if __name__ == '__main__':
    main()
