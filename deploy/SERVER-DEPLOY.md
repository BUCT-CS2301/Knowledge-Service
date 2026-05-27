# 服务器部署说明（Knowledge-Service）

对应仓库：[BUCT-CS2301/Knowledge-Service](https://github.com/BUCT-CS2301/Knowledge-Service)。

## 1. 先解决本机 SSH 登录

在 Mac 上执行（需已配置与服务器匹配的私钥，或 `ssh-copy-id` 上传公钥）：

```bash
ssh tengxunserver
```

若出现 `Permission denied (publickey)`，请检查 `~/.ssh/config` 里是否为该 Host 指定了正确的 `IdentityFile`，或是否在 `ssh-agent` 中加载了私钥：`ssh-add -l`。

本文档无法替你完成密钥认证；只有 SSH 能通过后，下文步骤才能在服务器上执行。

## 2. 服务器环境建议

- Ubuntu 22.04+（你当前用户为 `ubuntu`）
- Java **21**、Maven **3.9+**（后端 `museum` 为 Spring Boot 3.2）
- Node.js **≥ 18**、npm **≥ 9**（前端 `myvue`）
- Nginx（推荐：前端静态站点 + API 反向代理）

## 3. 拉取代码

```bash
sudo mkdir -p /opt/knowledge-service
sudo chown $USER:$USER /opt/knowledge-service
cd /opt/knowledge-service
git clone https://github.com/BUCT-CS2301/Knowledge-Service.git .
```

（也可用 SSH 克隆地址；二选一即可。）

## 4. 配置后端（必读）

后端默认配置在：

`Overseas_cultural_relics_knowledge_service subsystem/museum/src/main/resources/application.yml`

其中包含 **数据库与 Neo4j 等敏感信息**。若仓库为公开仓库，请务必：

- **不要将真实密码提交到 Git**；改用环境变量或服务器本地覆盖配置；
- **立即轮换已泄露的数据库／云服务密码**。

生产环境建议使用 `application-prod.yml`（自行添加且加入 `.gitignore`）或通过环境变量注入数据源，不要使用开发环境明文密码直连生产库。

编译并打包：

```bash
cd "/opt/knowledge-service/Overseas_cultural_relics_knowledge_service subsystem/museum"
mvn -q -DskipTests package
```

JAR 一般在 `target/*.jar`。可用 `systemd` 托管，例如 ExecStart：

```bash
/usr/bin/java -jar /path/to/demo-0.0.1-SNAPSHOT.jar
```

确保云安全组/防火墙放行 **8085**（若仅走 Nginx 反代则可只放行 80/443，8085 仅本机访问）。

## 5. 构建前端

前端已支持：**生产构建默认使用「同域名相对路径」访问后端**，与下面 Nginx 反代一致，无需强制写死 `localhost:8085`。

```bash
cd "/opt/knowledge-service/Overseas_cultural_relics_knowledge_service subsystem/myvue"
npm ci
npm run build
```

若前后端域名不同（跨域单独部署后端），请在构建前设置：

```bash
export VITE_API_BASE=https://你的后端域名
npm run build
```

产物目录：`myvue/dist/`。

将 `dist` 拷到 Web 目录，例如：

```bash
sudo mkdir -p /var/www/knowledge-service/dist
sudo cp -r dist/* /var/www/knowledge-service/dist/
sudo chown -R www-data:www-data /var/www/knowledge-service
```

（路径需与下面 Nginx `root` 一致。）

## 6. Nginx

参考同目录下的 `nginx-knowledge-service.example.conf`，把 `server_name`、`root` 改成你的域名和 `dist` 路径后启用：

```bash
sudo nginx -t && sudo systemctl reload nginx
```

建议使用 **HTTPS**（`certbot` 等），本示例仅为 HTTP 极简演示。

## 7. 更新发布

```bash
cd /opt/knowledge-service
git pull
# 按需重新 mvn package、npm run build、复制 dist、重启 java 服务
sudo systemctl restart knowledge-service   # 若你创建了同名 systemd 单元
sudo systemctl reload nginx
```

## 8. 本仓库中的辅助配置

- `deploy/nginx-knowledge-service.example.conf`：Nginx 示例
- 前端统一 API 根路径：`myvue/src/config/api.js`

部署完成后可在浏览器访问你的域名验证页面与接口。
