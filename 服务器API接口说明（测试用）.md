# 服务器 API 接口说明（测试用）

> **文档版本**：v1.2  
> **编写日期**：2026-05-31（v1.2 修正应用服务器公网 IP 为 `43.138.39.44`；v1.1 补充文物数据 API 详细规格）  
> **适用对象**：测试同学、联调同学  
> **对照旧文档**：《数据库接口与后端接口.md》《接口设计文档.md》

---

## 1. 服务器与访问方式

### 1.1 应用服务器

| 项目 | 实际配置 |
|------|----------|
| 公网 IP | `43.138.39.44` |
| 内网 IP | `10.2.20.17` |
| 前端（Nginx 静态页） | `http://43.138.39.44/`（端口 **80**） |
| 后端（Spring Boot） | 端口 **8085**（由 Nginx 反代，**推荐走 80 同域**） |
| systemd 服务名 | `knowledge-service` |

**推荐 API 根地址（测试时用）：**

```text
http://43.138.39.44
```

若 80 端口不通，可在**服务器本机**或内网尝试直连后端（公网通常未开放 8085）：

```text
http://127.0.0.1:8085
```

**常用前端页面：**

| 页面 | 地址 |
|------|------|
| 首页 | http://43.138.39.44/ |
| 登录 | http://43.138.39.44/login |
| 搜索结果 | http://43.138.39.44/fore/result |
| 知识图谱 | http://43.138.39.44/visualization/knowledge-graph |

> ⚠️ **不要用 `8080` 作为生产后端地址。** 8080 是 Vue 本地开发端口，不是已部署后端端口。

### 1.2 数据库（供对照，非 HTTP API）

| 类型 | 地址 | 账号 | 用途 |
|------|------|------|------|
| Neo4j 浏览器 | http://39.106.231.119:7474 | neo4j / password123 | 图谱可视化、搜索数据源 |
| Neo4j Bolt | bolt://39.106.231.119:7687 | neo4j / password123 | 后端连接 |
| MySQL | 39.106.231.119:3306 | root / （见《数据库接口与后端接口.md》） | 文物原始数据、用户/收藏/评论 |

| 数据用途 | 存储 | 对应 HTTP 接口 |
|----------|------|----------------|
| 文物搜索 / 详情 / 图谱 | Neo4j `39.106.231.119:7687`（约 7510 条 Artifact） | `/search/*`、`/api/v1/data/knowledge-graph` 等 |
| 用户 / 收藏 / 评论 | MySQL | `/users/*`、`/user_admin/*`、`/search/searchById/*` |
| CSV 静态文件 | 本地 `data.csv`（仅 `museum/index.html` 旧版浏览页） | **无 HTTP 接口**，勿与后端 API 混用 |

---

## 2. 通用约定（与旧文档的差异）

### 2.1 响应格式

**实际后端**（museum 模块）统一返回：

```json
{
  "state": 200,
  "message": "说明文字",
  "data": { ... }
}
```

| 字段 | 含义 |
|------|------|
| `state` | 业务状态码（200 成功，4000 无结果/用户不存在，6000 文物不存在等） |
| `message` | 提示信息 |
| `data` | 业务数据 |

**《接口设计文档》** 写的是 `code` 字段（后台管理子系统规范），**当前已部署后端未采用该格式**。

### 2.2 请求方式

- 带 `@RequestBody` 的接口必须用 **POST + JSON**，不能 GET 带 Query 当列表接口用。
- 登录成功后部分接口在响应里带 `token` 字段（JWT），非全站强制鉴权。

### 2.3 演示账号

| 字段 | 值 |
|------|-----|
| 用户 ID（username 字段传数字字符串） | `1001` |
| 密码 | `123456` |
| 用户名 | `demo` |

### 2.4 文物数据业务状态码（`state`）

| state | 含义 | 常见接口 |
|-------|------|----------|
| 200 | 成功 | 全部 |
| 4000 | 无查询结果 / 用户不存在 / 更新失败 | `/search/obscure`、`/users/login` |
| 5000 | 密码错误 / 注册失败 | `/users/login`、`/users/register` |
| 6000 | 文物不存在 | `/search/searchById`、`/search/detailByObjectId`（objectId 无效时） |

> 搜索接口**无结果时也可能返回 `state: 4000`**，前端应把 `data` 当空数组处理，不要当作服务器故障。

---

## 3. 文物数据 API 详细说明（已实现）

> **结论**：文物数据的 HTTP API **已经写了并部署**，入口在 `/search/*` 与 `/api/v1/data/*`，**不是** `/api/v1/relic/list` 或 `/api/v1/data/relics`。  
> 实现类：`Neo4jArtifactSearchService.java`、`CartController.java`、`ProductController.java`。

### 3.0 数据流说明

```
测试请求
   │
   ▼
Nginx :80  ──反代──▶  Spring Boot :8085
                           │
              ┌────────────┴────────────┐
              ▼                         ▼
        Neo4j 图谱查询              MySQL 遗留表
   （搜索/详情/图谱主路径）      （searchById/收藏/评论）
              │
              ▼
   返回 JSON：state + message + data
```

- **单次搜索最多返回 100 条**（Neo4j `DEFAULT_LIMIT = 100`）。
- 列表项里的 `id` 是 `objectId.hashCode()` 生成的**伪数字 id**，仅用于展示；**详情查询必须用 `objectId`（UUID）**。

---

### 3.1 搜索列表 — `POST /search/obscure`（主接口）

**用途**：关键词模糊搜索，等价于「文物列表 / 按名称查文物」。**替代旧文档中的 `/api/v1/relic/list`。**

| 项目 | 说明 |
|------|------|
| URL | `POST /search/obscure` |
| Content-Type | `application/json` |
| 数据源 | Neo4j `Artifact` 节点 |

**请求 Body**

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| keyword | string | 是 | 关键词；会在标题、描述、朝代、博物馆、材质、品类中 CONTAINS 匹配 |

**请求示例**

```bash
curl -X POST "http://43.138.39.44/search/obscure" \
  -H "Content-Type: application/json" \
  -d '{"keyword":"瓷"}'
```

**成功响应示例**

```json
{
  "state": 200,
  "message": "查找到以下内容：",
  "data": [
    {
      "id": 2847361921,
      "objectId": "005ddca2-12c4-4c13-ae40-5bb6e8f00c87",
      "object_name": "龙与凤凰杯",
      "cat1": "瓷",
      "cat2": "1522—1566",
      "cat3": "釉下蓝饰瓷器",
      "makers_name": "克利夫兰艺术博物馆",
      "img_url": "https://openaccess-cdn.clevelandart.org/1964.171/1964.171_web.jpg"
    }
  ]
}
```

**列表项字段说明（`Cart` 对象）**

| 字段 | 含义 | Neo4j 来源 |
|------|------|------------|
| objectId | 文物唯一 ID（UUID） | `Artifact.object_id` |
| id | 伪数字 ID（hashCode） | 勿用于详情接口 |
| object_name | 文物名称 | `Artifact.title` |
| cat1 | 材质 | 关系 `制作材质→Material` |
| cat2 | 年代/时期 | 关系 `所属朝代→Period` |
| cat3 | 文物品类 | 关系 `文物品类→ArtifactType` |
| makers_name | 收藏博物馆（字段名历史遗留） | 关系 `收藏馆藏→Museum` |
| img_url | 图片 URL | `展示图片→Image` 或 `Artifact.imageUrl` |

**无结果响应**

```json
{
  "state": 4000,
  "message": "未查询到相关文物",
  "data": null
}
```

---

### 3.2 文物详情 — `POST /search/detailByObjectId`（推荐）

**用途**：按 UUID 查完整文物信息。**替代 `/api/v1/data/relics/{objectId}`。**

| 项目 | 说明 |
|------|------|
| URL | `POST /search/detailByObjectId` |
| Content-Type | `application/json` |

**请求 Body**

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| objectId | string | 是 | 从搜索结果的 `objectId` 字段取值 |

**请求示例**

```bash
curl -X POST "http://43.138.39.44/search/detailByObjectId" \
  -H "Content-Type: application/json" \
  -d '{"objectId":"005ddca2-12c4-4c13-ae40-5bb6e8f00c87"}'
```

**成功响应示例**

```json
{
  "state": 200,
  "message": "找到以下内容：",
  "data": {
    "objectId": "005ddca2-12c4-4c13-ae40-5bb6e8f00c87",
    "object_name": "龙与凤凰杯",
    "time_period": "1522—1566",
    "material": "瓷",
    "type": "釉下蓝饰瓷器",
    "museum": "克利夫兰艺术博物馆",
    "description": "……",
    "dimensions": "高 12.7 cm",
    "credit_line": "……",
    "accession_number": "1964.171",
    "url": "https://www.clevelandart.org/art/1964.171",
    "img_url": "https://openaccess-cdn.clevelandart.org/1964.171/1964.171_web.jpg"
  }
}
```

**详情字段说明**

| 字段 | 含义 |
|------|------|
| objectId | UUID 主键 |
| object_name | 文物名称 |
| time_period | 年代 |
| material | 材质（多种时用「、」连接） |
| type | 品类 |
| museum | 收藏博物馆 |
| description | 简介 |
| dimensions | 尺寸 |
| credit_line | 版权/来源说明 |
| accession_number | 馆藏编号 |
| url | 博物馆官网详情页 |
| img_url | 图片地址 |

**失败响应（objectId 不存在）**

```json
{
  "state": 6000,
  "message": "抱歉，您查询的文物不存在！",
  "data": null
}
```

---

### 3.3 分类查询 — `POST /search/classification`

**用途**：按单一维度筛选（博物馆 / 材质 / 朝代 / 品类）。

**请求 Body**

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| c | string | 是 | 分类维度，见下表 |
| v_1 ~ v_4 | string | 其一 | 只取**最后一个非空**的 v 值作为关键词 |

**`c` 取值（与前端路由一致）**

| c 值 | 含义 | 前端页面 | 示例 Body |
|------|------|----------|-----------|
| museum | 按博物馆 | classify 博物馆 | `{"c":"museum","v_4":"克利夫兰"}` |
| mart | 按材质 | classify_mart | `{"c":"mart","v_2":"瓷"}` |
| dynasty | 按朝代 | classify_dynasty | `{"c":"dynasty","v_1":"宋"}` |
| use | 按品类 | classify_use | `{"c":"use","v_3":"瓷器"}` |

**请求示例（按朝代）**

```bash
curl -X POST "http://43.138.39.44/search/classification" \
  -H "Content-Type: application/json" \
  -d '{"c":"dynasty","v_1":"宋"}'
```

> ⚠️ 旧版文档示例 `{"c":"cat2","v_1":"瓷器"}` **是错误的**，`cat2` 不是合法 `c` 值。

**响应**：与 §3.1 相同，`data` 为 `Cart[]`。

---

### 3.4 组合查询 — `POST /search/multiFind`

**用途**：博物馆 + 材质 + 朝代 + 品类多条件 AND 组合（空字段不参与过滤）。

**请求 Body**

| 字段 | 实际含义 | 说明 |
|------|----------|------|
| v_4 | 博物馆 | 博物馆名称模糊匹配 |
| v_2 | 材质 | Material |
| v_1 | 朝代 | Period |
| v_3 | 品类 | ArtifactType |

> 字段名 `v_1`~`v_4` 为历史遗留，与 MySQL 表字段 cat1/cat2 命名不一致，**以本表为准**。

**请求示例**

```bash
curl -X POST "http://43.138.39.44/search/multiFind" \
  -H "Content-Type: application/json" \
  -d '{"v_4":"克利夫兰","v_2":"瓷","v_1":"","v_3":""}'
```

**响应**：与 §3.1 相同，`data` 为 `Cart[]`。

---

### 3.5 排序列表 — `GET /search/sort`

**用途**：按名称或年代排序，返回最多 100 条文物列表。

| Query | 说明 |
|-------|------|
| way=wordUp | 名称 A→Z |
| way=wordDown | 名称 Z→A |
| way=timeUp | 年代升序 |
| way=timeDown | 年代降序 |

**请求示例**

```bash
curl "http://43.138.39.44/search/sort?way=wordUp"
```

**响应**：与 §3.1 相同，`data` 为 `Cart[]`。

---

### 3.6 旧版详情 — `POST /search/searchById`（不推荐）

**用途**：按 MySQL 表 `cultural_relics_data` 的**数字主键**查详情，含评论、推荐、收藏状态。

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| rid | string | 是 | MySQL 数字 id（**不是** objectId） |
| uid | string | 否 | 用户 id，用于判断是否已收藏 |

**与 Neo4j 搜索的关系**：Neo4j 搜索返回的 `id` 是 hashCode，**不能**直接作为 `rid` 使用。从搜索结果进详情请用 §3.2。

---

### 3.7 字段对照：实际 API vs 《接口设计文档》

| 接口设计文档（未实现） | 实际已部署 API | 说明 |
|------------------------|----------------|------|
| `GET /api/v1/data/relics?page=&pageSize=` | `POST /search/obscure` 或 `/search/sort` | 无分页参数，固定最多 100 条 |
| `GET /api/v1/data/relics/{objectId}` | `POST /search/detailByObjectId` | 方法、路径均不同 |
| `RelicObject.title` | `object_name` 或详情里同名 | 命名风格不同 |
| `RelicObject.period` | `cat2`（列表）/ `time_period`（详情） | |
| `RelicObject.type` | `cat3`（列表）/ `type`（详情） | |
| `RelicObject.material` | `cat1`（列表）/ `material`（详情） | |
| `RelicObject.museumId` | `makers_name`（列表）/ `museum`（详情） | 返回博物馆**名称**而非 UUID |
| `RelicObject.imageUrl` | `img_url` | snake_case |
| 响应 `code: 200` | 响应 `state: 200` | |

---

### 3.8 数据可视化 API

#### 3.8.1 知识图谱 — `GET /api/v1/data/knowledge-graph`

| Query | 默认 | 说明 |
|-------|------|------|
| limit | 25 | 演示子图文物数，最大 40 |

**响应 `data` 结构**

```json
{
  "nodes": [
    { "id": "relic_xxx", "label": "文物名", "type": "文物", "description": "…", "imageUrl": null }
  ],
  "links": [
    { "source": "relic_xxx", "target": "museum_大英博物馆", "relationType": "收藏于" }
  ]
}
```

#### 3.8.2 时间轴 — `GET /api/v1/data/timeline`

**响应 `data`**：数组，每项结构：

| 字段 | 类型 | 说明 |
|------|------|------|
| dynasty | string | 朝代名 |
| year | string | 年代范围 |
| description | string | 描述 |
| relics | array | 该朝代下的文物 `{ name, type, museum, image }` |

> Neo4j 不可用时返回内置 mock 数据。

#### 3.8.3 地理分布 — `GET /api/v1/data/geo-map`

**响应 `data`**：数组，每项：

| 字段 | 类型 | 说明 |
|------|------|------|
| name | string | 博物馆中文名 |
| city | string | 城市 |
| country | string | 国家 |
| lat | number | 纬度 |
| lng | number | 经度 |
| count | number | 馆藏文物数量（或默认值） |

#### 3.8.4 数据看板 — `GET /api/v1/data/dashboard`

**响应 `data` 主要字段**

| 字段 | 类型 | 说明 |
|------|------|------|
| stats | object | `{ totalRelics, museumCount, categoryCount, countryCount }` |
| typeDistribution | array | 文物类型占比 `{ name, percentage, color }` |
| dynastyDistribution | array | 朝代分布 `{ dynasty, count, percentage, color }` |
| museumRanking | array | 博物馆排名 `{ name, location, count }` |
| trendYears / trendData | array | 年度趋势 |
| materialDistribution | array | 材质分布 |

**curl 示例**

```bash
curl "http://43.138.39.44/api/v1/data/knowledge-graph?limit=25"
curl "http://43.138.39.44/api/v1/data/timeline"
curl "http://43.138.39.44/api/v1/data/geo-map"
curl "http://43.138.39.44/api/v1/data/dashboard"
```

---

### 3.9 明确未实现的文物 REST API

以下路径在《接口设计文档.md》中有定义，**当前服务器未部署**，测试时预期失败：

| 路径 | 说明 |
|------|------|
| `GET /api/v1/relic/list` | 从未实现（旧 HTML 误写） |
| `GET /api/v1/data/relics` | 文物分页 CRUD |
| `GET /api/v1/data/relics/{objectId}` | 文物详情 REST 版 |
| `POST /api/v1/data/relics/import-csv` | CSV 批量导入 |
| `GET /api/v1/data/museums` | 博物馆 CRUD |

---

## 4. 用户与个人中心 API

| 接口 | 方法 | 说明 |
|------|------|------|
| `/users/login` | POST | 登录（`username` 传**数字用户 ID** 字符串） |
| `/users/register` | POST | 注册 |
| `/users/get_detail` | POST | 按用户 id 查资料 |
| `/users/{objectId}/logs` | GET | 用户行为日志（分页） |
| `/user_admin/update` | POST | 修改个人资料（需原密码） |
| `/user_admin/comment` | POST | 查看我的评论 |
| `/user_admin/collect` | POST | 查看我的收藏 |
| `/user_admin/deleteCollect` | POST | 取消收藏 |
| `/user_admin/deleteComment` | POST | 删除评论 |

**登录示例**

```bash
curl -X POST "http://43.138.39.44/users/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"1001","password":"123456"}'
```

**注册示例**

```bash
curl -X POST "http://43.138.39.44/users/register" \
  -H "Content-Type: application/json" \
  -d '{"username":"myname","password":"123456","sex":"1","tele":"13800000000"}'
```

**收藏 Neo4j 文物（按 objectId）**

```bash
curl -X POST "http://43.138.39.44/search/searchById/collect" \
  -H "Content-Type: application/json" \
  -d '{"uid":"1013","objectId":"f03910bd-0e85-4d88-bf21-c06c41fc7a13","relicName":"服装碎片"}'
```

**取消收藏（可传 objectId）**

```bash
curl -X POST "http://43.138.39.44/user_admin/deleteCollect" \
  -H "Content-Type: application/json" \
  -d '{"uid":"1013","objectId":"f03910bd-0e85-4d88-bf21-c06c41fc7a13"}'
```

**详情页查询是否已收藏**

```bash
curl -X POST "http://43.138.39.44/search/detailByObjectId" \
  -H "Content-Type: application/json" \
  -d '{"objectId":"f03910bd-0e85-4d88-bf21-c06c41fc7a13","uid":"1013"}'
# 响应 data.if_collect: 0 | 1
```

> 收藏/取消收藏时 `uid` 为**数字用户 ID** 字符串。Neo4j 文物须传 `objectId`（UUID），后端会写入 `collect.relic_object_id`。

**用户详情**

```bash
curl -X POST "http://43.138.39.44/users/get_detail" \
  -H "Content-Type: application/json" \
  -d '{"id":"1013"}'
```

**修改资料**

```bash
curl -X POST "http://43.138.39.44/user_admin/update" \
  -H "Content-Type: application/json" \
  -d '{"id":"1013","oldPassword":"123456","newPassword":"123456","name":"新昵称","sex":"1","tel":"13800000000"}'
```

**我的收藏 / 评论**

```bash
curl -X POST "http://43.138.39.44/user_admin/collect" \
  -H "Content-Type: application/json" -d '{"id":"1013"}'

curl -X POST "http://43.138.39.44/user_admin/comment" \
  -H "Content-Type: application/json" -d '{"username":"1013"}'
```

---

## 5. 新旧文档对比（测试必读）

### 5.1 与《数据库接口与后端接口.md》对比

| 项目 | 旧文档写法 | 实际服务器 | 测试结论 |
|------|------------|------------|----------|
| 后端地址 | `60.205.14.101:8080`（旧文档误写） | `43.138.39.44`（80 反代）；8085 仅本机 | ❌ 旧文档 IP/端口均有误 |
| 示例接口 | `GET /api/v1/auth/current-user` | **未实现** | ❌ 会 404 或无响应 |
| 文物列表 | （未写，但代码里有人测 `/api/v1/relic/list`） | 应使用 `POST /search/obscure` | ❌ 旧路径不存在 |
| Neo4j / MySQL | 39.106.231.119 | 一致 | ✅ 可用 |

### 5.2 与《接口设计文档.md》（后台管理 v1.0.5）对比

该文档描述的是**后台管理子系统** REST 规范，**大部分尚未在已部署的 museum 后端实现**。

| 接口（设计文档） | 设计文档 | 已部署后端 | 状态 |
|------------------|----------|------------|------|
| `POST /api/v1/auth/login` | 登录 | 实际为 `POST /users/login` | ⚠️ 路径不同 |
| `GET /api/v1/auth/current-user` | 当前用户 | 无 | ❌ 未实现 |
| `GET /api/v1/data/relics` | 文物分页列表 | 无 | ❌ 未实现 |
| `GET /api/v1/data/relics/{objectId}` | 文物详情 | 无（可用 `POST /search/detailByObjectId` 替代） | ⚠️ 替代方案 |
| `POST /api/v1/data/relics/import-csv` | CSV 导入 | 无 | ❌ 未实现 |
| `GET /api/v1/data/museums` | 博物馆 CRUD | 无 | ❌ 未实现 |
| `GET /api/v1/data/knowledge-graph` | 知识图谱 | 有 | ✅ 已实现 |
| 响应字段 `code` | 200 | 实际为 `state` | ⚠️ 字段名不同 |

### 5.3 前端与文档路径对照（2026-05-31 已对齐）

个人中心 / 登录注册相关页面已改为调用实际后端，**勿再使用** `/api/v1/auth/*`、`/api/v1/users/*`：

| 功能 | 正确路径 | 前端封装 |
|------|----------|----------|
| 登录 | `POST /users/login` | `myvue/src/api/user.js` → `login()` |
| 注册 | `POST /users/register` | `register()` |
| 当前用户资料 | `POST /users/get_detail` | `fetchCurrentUserProfile()` |
| 修改资料 | `POST /user_admin/update` | `updateUserProfile()` |
| 我的收藏 | `POST /user_admin/collect` | `getUserCollections()` |
| 我的评论 | `POST /user_admin/comment` | `getUserComments()` |

仍使用旧路径、后端未实现的接口：

| 前端调用路径 | 文件 | 应改用 |
|--------------|------|--------|
| `/api/v1/data/relics/{id}` | `relicDetail.vue` | `/search/detailByObjectId` |
| `/api/v1/relic/list` | 旧 HTML 浏览页 | `/search/obscure` |

---

## 6. 测试同学快速验收清单

在**能访问服务器的网络环境**下逐项执行：

| 序号 | 测试项 | 命令 / 操作 | 期望结果 |
|------|--------|-------------|----------|
| 1 | 前端可访问 | 浏览器打开 `http://43.138.39.44/` | 页面正常加载 |
| 2 | 文物搜索 | `POST /search/obscure` + `{"keyword":"瓷"}` | `state: 200`，`data` 为数组 |
| 3 | 文物详情 | `POST /search/detailByObjectId` + 有效 `objectId` | `state: 200`，含 title/图片等 |
| 4 | 登录 | `POST /users/login` + 1001/123456 | `state: 200`，返回 `token` |
| 5 | 知识图谱 | `GET /api/v1/data/knowledge-graph` | `state: 200` |
| 6 | **负向** 旧错误路径 | `GET /api/v1/relic/list` | 404 或连接失败（**预期失败**） |
| 7 | **负向** 旧错误路径 | `GET /api/v1/auth/current-user` | 404 或连接失败（**预期失败**） |

---

## 7. 常见问题（报 bug 时可引用）

| 现象 | 原因 | 处理 |
|------|------|------|
| `/api/v1/relic/list` 无响应 / 404 | 该接口从未实现 | 改用 `POST /search/obscure` |
| 文档示例 `/api/v1/auth/current-user` 失败 | 未实现 | 改用 `/users/login` + `/users/get_detail` |
| 端口 8080 能打开页面但 API 报错 | 8080 可能是前端开发端口，不是后端 | 改用 80 或 8085 |
| 返回字段是 `state` 不是 `code` | 两套规范混用 | 断言时用 `state === 200` |
| 搜索有结果但详情为空 | 用了 MySQL 数字 id 调 `searchById`，与 Neo4j UUID 不一致 | 详情用 `detailByObjectId` |
| T-03 | Neo4j 文物的收藏/评论 | 现收藏已支持 objectId；评论仍依赖 MySQL 数字 rid | 收藏已做友好对接；评论待 T-03b |

---

## 8. 相关文件索引

| 文件 | 说明 |
|------|------|
| `数据库接口与后端接口.md` | **旧文档**（端口与示例接口有误，勿再作为 API 唯一依据） |
| `接口设计文档.md` | 后台管理规范（**规划文档**，多数接口未部署） |
| `deploy/部署问题与解决方案备案.md` | 部署架构与已验证接口（公网 `43.138.39.44`） |
| `deploy/修复记录/5.28修改记录.md` | 前端页面地址与登录联调记录 |
| `deploy/修复记录/5.30修改记录.md` | 搜索/详情/图片联调修复说明 |
| `deploy/monitor.sh` | 服务器健康巡检脚本 |

---

## 9. 报告问题建议格式

提交测试问题时请包含：

1. **请求 URL**（完整，含端口）  
2. **HTTP 方法**（GET / POST）  
3. **请求 Body**（JSON 原文）  
4. **实际响应**（状态码 + 响应体）  
5. **期望行为**  
6. **对照本文档章节**（如 §5.1 旧文档对比）

---

> **维护说明**：本文档以仓库内 `museum` 模块 Controller 源码为准。若后端新增 `/api/v1/data/relics` 等接口，需同步更新本文档与《接口设计文档》的对照表。
