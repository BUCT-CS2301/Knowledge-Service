# 数据可视化模块

海外流失文物知识服务系统的数据可视化模块，提供知识图谱、地图分布、时间轴等多种可视化展示。

## 目录结构

```
数据可视化/
├── docker-compose.yml          # Docker 环境配置
├── .env.example                # 环境变量示例
├── README.md                   # 本文档
│
├── Overseas_cultural_relics_knowledge_service subsystem/
│   └── myvue/                 # Vue 前端项目
│       └── src/
│           └── views/
│               └── visualization/
│                   ├── KnowledgeGraph.vue  # 知识图谱
│                   ├── Map.vue            # 地图分布
│                   ├── Timeline.vue       # 时间轴
│                   ├── Dashboard.vue      # 数据看板
│                   └── Index.vue         # 可视化首页
│
├── museum/                     # Spring Boot 后端
│   └── src/main/java/com/
│       └── service/
│           └── Impl/
│               ├── KnowledgeGraphServiceImpl.java  # 知识图谱服务
│               ├── RelicServiceImpl.java          # 文物数据服务
│               └── MuseumServiceImpl.java         # 博物馆数据服务
│
└── 知识图谱/                   # 知识图谱构建工具
    ├── kg_data/               # 知识图谱数据
    │   ├── museums.csv       # 博物馆数据
    │   ├── relics.csv        # 文物数据
    │   ├── periods.csv       # 朝代数据
    │   └── knowledge_graph.json  # 知识图谱JSON
    ├── build_kg_simple.py    # 简化版知识图谱构建
    ├── build_kg_with_morph.py # Morph-KGC 知识图谱构建
    ├── config.ini            # Morph-KGC 配置文件
    ├── mapping.rml.ttl       # RML 映射规则
    ├── requirements.txt     # Python 依赖
    └── output/              # RDF 输出目录
        ├── output.nt        # N-Triples 格式
        └── output.ttl       # Turtle 格式
```

## 快速开始

### 方式一：Docker 部署（推荐）

```bash
# 1. 复制环境变量文件
cp .env.example .env

# 2. 编辑 .env 文件，填入数据库密码

# 3. 启动所有服务
docker-compose up -d

# 4. 查看服务状态
docker-compose ps
```

### 方式二：本地开发

#### 前端启动

```bash
cd Overseas_cultural_relics_knowledge_service subsystem/myvue
npm install
npm run dev
```

访问 http://localhost:3000

#### 后端启动

```bash
cd Overseas_cultural_relics_knowledge_service subsystem/museum
mvn spring-boot:run
```

API 地址：http://localhost:8085

#### 知识图谱数据构建

```bash
cd 知识图谱

# 安装依赖
pip install -r requirements.txt

# 运行知识图谱构建
python build_kg_simple.py
```

## 功能模块

### 1. 知识图谱 (Knowledge Graph)

展示文物、博物馆、朝代之间的关系网络。

**特性：**
- 支持节点拖拽
- 支持缩放和平移
- 点击节点查看详情
- 节点分类着色（文物/博物馆/朝代）
- 关系类型区分（收藏于/属于/相关）

**数据来源：**
- 主要：Neo4j 图数据库
- 备用：本地 JSON 文件 (`knowledge_graph.json`)

**API 端点：**
```
GET /api/v1/data/knowledge-graph
```

### 2. 地图分布 (Map)

展示海外流失文物的地理分布。

**特性：**
- 全球地图展示
- 博物馆位置标记
- 点击查看详情
- 按洲际筛选

### 3. 时间轴 (Timeline)

展示文物历史时间线。

**特性：**
- 按朝代分期
- 文物时间标注
- 缩放浏览
- 点击查看详情

### 4. 数据看板 (Dashboard)

综合数据统计和可视化。

**特性：**
- 文物数量统计
- 博物馆分布
- 朝代分布饼图
- 实时数据更新

## 技术栈

### 前端
- Vue 3
- Vite
- SVG (原生图形渲染)
- CSS3 (渐变、动画)

### 后端
- Spring Boot
- Spring Data JPA
- Neo4j (图数据库)
- MySQL (关系数据库)

### 知识图谱
- Morph-KGC (RML 映射)
- RDF (知识表示)
- rdflib (Python RDF 库)

## 数据模型

### 实体类型

| 类型 | 说明 | 属性 |
|------|------|------|
| Relic (文物) | 历史文物实体 | id, title, period, type, material, description |
| Museum (博物馆) | 收藏机构 | id, name, nameCn, location, website |
| Period (朝代) | 历史时期 | id, name, startYear, endYear, description |

### 关系类型

| 关系 | 方向 | 说明 |
|------|------|------|
| 收藏于 | Relic → Museum | 文物被某博物馆收藏 |
| 属于 | Relic → Period | 文物属于某朝代 |
| 相关 | Relic ↔ Relic | 文物之间相关 |

## 环境变量

| 变量名 | 说明 | 默认值 |
|--------|------|--------|
| MYSQL_USER | MySQL 用户名 | root |
| MYSQL_PASSWORD | MySQL 密码 | - |
| NEO4J_USER | Neo4j 用户名 | neo4j |
| NEO4J_PASSWORD | Neo4j 密码 | - |
| BACKEND_PORT | 后端端口 | 8085 |
| FRONTEND_PORT | 前端端口 | 3000 |

## 常见问题

### Q: 知识图谱显示空白？
A: 检查后端服务是否启动，Neo4j 数据库是否有数据。可以查看浏览器控制台日志。

### Q: 节点拖动有抖动？
A: 确保使用 transform 方式定位，避免使用带滤镜的渐变填充。

### Q: 数据更新后没有变化？
A: 清除浏览器缓存，或刷新页面。API 调用有缓存机制。

### Q: Docker 启动失败？
A: 检查 Docker 是否运行，检查端口是否被占用，查看日志：`docker-compose logs`

## API 文档

### 知识图谱 API

```http
GET /api/v1/data/knowledge-graph?limit=30
```

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "nodes": [
      {
        "id": "relic_1",
        "label": "商代青铜鼎",
        "type": "文物",
        "description": "商代晚期青铜祭祀用鼎",
        "x": 200,
        "y": 100
      }
    ],
    "links": [
      {
        "source": "relic_1",
        "target": "museum_1",
        "relationType": "收藏于"
      }
    ]
  }
}
```

## 开发指南

### 添加新的可视化组件

1. 在 `Overseas_cultural_relics_knowledge_service subsystem/myvue/src/views/visualization/` 创建 Vue 组件
2. 在路由配置中添加路由
3. 在侧边栏添加导航入口

### 修改知识图谱数据

1. 修改 `知识图谱/kg_data/` 下的 CSV 文件
2. 运行 `python build_kg_simple.py` 重新生成 JSON
3. 或者直接修改 `knowledge_graph.json`

### 扩展关系类型

1. 在前端 `getLinkColor()` 添加新的颜色映射
2. 在后端 `KnowledgeGraphServiceImpl.java` 添加新的关系处理逻辑

## 许可证

MIT License
