#!/usr/bin/env bash
# OCX 一键修复脚本 - 绕过 Spring Boot 3.5 JarLauncher 嵌套JAR bug
# 使用解压 + classpath 方式启动
set -euo pipefail

INSTALL_DIR="/opt/ocx-worker"
APP_DIR="${INSTALL_DIR}/app"
JAR_NAME="ocx-worker.jar"
SERVICE_FILE="/etc/systemd/system/ocx-worker.service"

echo "=== OCX 一键修复 ==="

# 1. 安装 unzip（最小化 Ubuntu 可能没有）
if ! command -v unzip >/dev/null 2>&1; then
    echo "[1/5] 安装 unzip..."
    apt-get update -qq 2>/dev/null && apt-get install -y -qq unzip 2>/dev/null || {
        echo "ERROR: 无法安装 unzip，请手动执行: apt-get install unzip"
        exit 1
    }
else
    echo "[1/5] unzip 已安装"
fi

# 2. 停服务
echo "[2/5] 停止 ocx-worker 服务..."
systemctl stop ocx-worker 2>/dev/null || true

# 3. 解压 JAR
echo "[3/5] 解压 JAR 到 ${APP_DIR}..."
if [ ! -f "${INSTALL_DIR}/${JAR_NAME}" ]; then
    echo "ERROR: ${INSTALL_DIR}/${JAR_NAME} 不存在，请先运行 install.sh"
    exit 1
fi
rm -rf "${APP_DIR}"
mkdir -p "${APP_DIR}"
cd "${APP_DIR}" && unzip -qo "${INSTALL_DIR}/${JAR_NAME}"
chown -R ocxworker:ocxworker "${APP_DIR}"
echo "  解压完成 ($(du -sh "${APP_DIR}" | cut -f1))"

# 4. 写入新的 service 文件
echo "[4/5] 更新 systemd 服务..."
cat > "${SERVICE_FILE}" << 'SVCEOF'
[Unit]
Description=OCX
After=network.target docker.service

[Service]
Type=simple
User=ocxworker
Group=ocxworker
WorkingDirectory=/opt/ocx-worker
ExecStart=/usr/local/bin/java -Xmx256m -Duser.timezone=Asia/Shanghai -Duser.dir=/opt/ocx-worker -cp "/opt/ocx-worker/app/BOOT-INF/classes:/opt/ocx-worker/app/BOOT-INF/lib/*" com.ocxworker.OcxWorkerApplication --spring.config.additional-location=file:/opt/ocx-worker/application.yml
Restart=on-failure
RestartSec=10
TimeoutStopSec=45

[Install]
WantedBy=multi-user.target
SVCEOF
systemctl daemon-reload

# 5. 启动
echo "[5/5] 启动 ocx-worker 服务..."
systemctl start ocx-worker

sleep 5
if systemctl is-active --quiet ocx-worker; then
    echo ""
    echo "=== ✅ 修复成功！==="
    systemctl --no-pager --lines=0 status ocx-worker 2>/dev/null || true
    PORT=$(grep -oP 'server:\s*\n\s*port:\s*\K\d+' /opt/ocx-worker/application.yml 2>/dev/null || echo "8818")
    echo ""
    echo "访问地址: http://$(hostname -I | awk '{print $1}'):${PORT}"
else
    echo ""
    echo "=== ❌ 启动失败 ==="
    journalctl -u ocx-worker -n 30 --no-pager
fi
