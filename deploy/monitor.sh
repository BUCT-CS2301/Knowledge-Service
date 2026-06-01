#!/usr/bin/env bash
# -----------------------------------------------------------------------------
# 海外文物知识服务系统 —— 运维健康监控脚本
#
# 功能：周期性巡检关键组件，输出一行汇总 + 明细到运维日志，任一关键项异常时退出码非 0
#   - systemd 服务：nginx / knowledge-service / mysql / neo4j（本机若有）
#   - 应用前端：GET http://127.0.0.1/                （Nginx 静态站点）
#   - 后端接口：POST /search/obscure                 （Spring Boot 业务可用性）
#   - 远程图库：bolt/HTTP 探测 Neo4j 39.106.231.119
#
# 用法：
#   bash deploy/monitor.sh                 # 单次巡检，结果写入日志并打印
#   LOG_FILE=/var/log/ks-monitor.log bash deploy/monitor.sh
#   watch -n 60 bash deploy/monitor.sh     # 简易持续监控
#   亦可配 cron / systemd timer 定时执行（见 deploy/monitor.service / monitor.timer）
# -----------------------------------------------------------------------------
set -uo pipefail

# ---- 可配置项（支持环境变量覆盖）-------------------------------------------
SITE_URL="${SITE_URL:-http://127.0.0.1/}"
API_BASE="${API_BASE:-http://127.0.0.1:8085}"
NEO4J_HTTP="${NEO4J_HTTP:-http://39.106.231.119:7474}"
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
LOG_FILE="${LOG_FILE:-$SCRIPT_DIR/logs/ops-monitor.log}"
SERVICES="${SERVICES:-nginx knowledge-service mysql neo4j}"
CURL_TIMEOUT="${CURL_TIMEOUT:-5}"

mkdir -p "$(dirname "$LOG_FILE")" 2>/dev/null || true

TS="$(date '+%Y-%m-%d %H:%M:%S')"
problems=()
details=()

# ---- 1. systemd 服务状态 ----------------------------------------------------
if command -v systemctl >/dev/null 2>&1; then
  for svc in $SERVICES; do
    # 单元不存在则跳过（不同主机安装组件可能不同）
    if ! systemctl cat "$svc" >/dev/null 2>&1; then
      continue
    fi
    state="$(systemctl is-active "$svc" 2>/dev/null)"
    details+=("svc:$svc=$state")
    [ "$state" = "active" ] || problems+=("$svc($state)")
  done
else
  details+=("systemctl:不可用(跳过服务检查)")
fi

# ---- 2. 前端站点 ------------------------------------------------------------
fe_code="$(curl -s -o /dev/null -m "$CURL_TIMEOUT" -w '%{http_code}' "$SITE_URL" 2>/dev/null)"
details+=("frontend=$fe_code")
[ "$fe_code" = "200" ] || problems+=("frontend(HTTP $fe_code)")

# ---- 3. 后端业务接口（实际查询，验证 DB 链路）-------------------------------
api_code="$(curl -s -o /dev/null -m "$CURL_TIMEOUT" -w '%{http_code}' \
  -X POST "$API_BASE/search/obscure" \
  -H 'Content-Type: application/json' -d '{"keyword":"瓷"}' 2>/dev/null)"
details+=("backend_api=$api_code")
[ "$api_code" = "200" ] || problems+=("backend_api(HTTP $api_code)")

# ---- 4. 远程 Neo4j 可达性 ---------------------------------------------------
neo_code="$(curl -s -o /dev/null -m "$CURL_TIMEOUT" -w '%{http_code}' "$NEO4J_HTTP" 2>/dev/null)"
details+=("neo4j_remote=$neo_code")
case "$neo_code" in
  200|401|404) : ;;                       # 有响应即视为可达（401 为需鉴权，正常）
  *) problems+=("neo4j_remote(HTTP $neo_code)") ;;
esac

# ---- 汇总并写日志 -----------------------------------------------------------
if [ "${#problems[@]}" -eq 0 ]; then
  status="OK"
else
  status="ALERT"
fi

line="[$TS] $status | ${details[*]}"
[ "${#problems[@]}" -gt 0 ] && line="$line | 异常: ${problems[*]}"

echo "$line" | tee -a "$LOG_FILE"

[ "$status" = "OK" ] && exit 0 || exit 1
