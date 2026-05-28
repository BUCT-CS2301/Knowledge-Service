# 知识图谱构建 (Morph-KGC)

本目录使用 Morph-KGC 将 MySQL 数据库中的文物数据映射为 RDF 知识图谱。

## 目录结构

```
知识图谱/
├── mapping.rml.ttl    # RML 映射规则
├── run_kgc.py         # Python 调用脚本
├── requirements.txt   # Python 依赖
├── config.ini         # Morph-KGC 配置 (自动生成)
├── output.nt          # 生成的 RDF 数据 (自动生成)
├── neo4j_nodes.csv    # Neo4j 节点导入文件 (自动生成)
└── neo4j_rels.csv     # Neo4j 关系导入文件 (自动生成)
```

## 安装依赖

```bash
cd 知识图谱
python3 -m venv .venv
source .venv/bin/activate
pip install -r requirements.txt
```

## 使用方法

### 1. 直接运行（自动完成所有步骤）

```bash
cd 知识图谱
source .venv/bin/activate
python run_kgc.py
```

### 1.1 导入 Neo4j（仅本地开发库）

在完成 MySQL 初始化后，可将 `artifact` / `museum` 数据**增量同步**到**本地** Neo4j：

```bash
cd 知识图谱
source .venv/bin/activate
python import_to_neo4j.py              # 默认本地，MERGE 增量同步
python import_to_neo4j.py --dry-run    # 预览，不写库
```

**禁止**对远程/生产 Neo4j 运行本脚本。远程库由部署方维护，误跑可能导致数据丢失。

### 2. 分步执行

```bash
# 步骤1: 安装依赖
pip install -r requirements.txt

# 步骤2: 运行图谱构建
python run_kgc.py

# 步骤3: 生成的 RDF 数据在 output.nt
# 步骤4: 导出为 Neo4j CSV 格式
```

## 配置说明

编辑 `run_kgc.py` 中的 MySQL 配置（服务器本地部署默认值）：

```python
MYSQL_CONFIG = {
    'host': '127.0.0.1',
    'port': 3306,
    'user': 'knowledge',
    'password': 'knowledge123',
    'database': 'muse'
}
```

## RML 映射规则说明

`mapping.rml.ttl` 定义了以下映射：

| 源表 | 目标节点 | 属性 |
|------|----------|------|
| artifact | relic:{id} | name, description, period, type, material, image |
| museum | museum:{id} | name, nameCn, location, website |
| artifact.period | period:{name} | name |

关系映射：
- relic -> museum: `collectedBy` (收藏于)
- relic -> period: `belongsTo` (属于)

## Neo4j 导入

生成的 CSV 文件可使用以下命令导入 Neo4j：

```bash
neo4j-admin import --nodes=neo4j_nodes.csv --relationships=neo4j_rels.csv
```

或在 Neo4j Browser 中执行：

```cypher
// 导入节点
LOAD CSV WITH HEADERS FROM 'file:///neo4j_nodes.csv' AS row
CREATE (:Entity {id: row.id, label: row.label});

// 导入关系
LOAD CSV WITH HEADERS FROM 'file:///neo4j_rels.csv' AS row
MATCH (s {id: row.start}), (e {id: row.end})
CREATE (s)-[r:REL {type: row.type}]->(e);
```

## 常见问题

1. **连接 MySQL 失败**
   - 检查数据库地址、用户名、密码是否正确
   - 确认数据库允许远程连接

2. **表不存在**
   - 确认 `artifact` 和 `museum` 表存在于 `muse` 数据库中

3. **Morph-KGC 安装问题**
   ```bash
   # 尝试安装编译依赖
   pip install wheel
   pip install morph-kgc[mysql]
   ```
