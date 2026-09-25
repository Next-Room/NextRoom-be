#!/usr/bin/env bash
# ZeroSSL 인증서 발급 + nginx 컨테이너로 설치 (acme.sh 사용)
#
# 사전:
#   1) acme.sh 설치:  curl https://get.acme.sh | sh -s email=nextroom.official@gmail.com
#   2) ZeroSSL 계정 등록: acme.sh --register-account -m nextroom.official@gmail.com --server zerossl
#   3) DNS-01 을 쓰면 컷오버 전에 트래픽 전환 없이 미리 발급 가능 (권장, 다운타임↓)
#      - DNS 제공자 API 키를 환경변수로 설정 (예: Cloudflare CF_Token 등)
#      - 미지원 시 --webroot 로 HTTP-01 (컷오버로 DNS 가 새 서버를 가리킨 뒤 실행)
set -euo pipefail

DEPLOY_DIR="$(cd "$(dirname "$0")/.." && pwd)"
CERT_DIR="${DEPLOY_DIR}/nginx/certs"
ACME="${HOME}/.acme.sh/acme.sh"

LIVE_DOMAIN="${LIVE_DOMAIN:-api.nextroom.co.kr}"     # TODO: 실제 도메인
DEV_DOMAIN="${DEV_DOMAIN:-api-dev.nextroom.co.kr}"   # TODO: 실제 도메인

# 발급 방식 선택: DNS-01 (예: dns_cf) 또는 webroot
#   DNS-01 예:   ISSUE_METHOD="--dns dns_cf"
#   webroot 예:  ISSUE_METHOD="--webroot /var/www/acme"  (nginx acme-webroot 볼륨과 연결)
ISSUE_METHOD="${ISSUE_METHOD:---webroot ${DEPLOY_DIR}/nginx/acme-webroot}"

issue_and_install() {
  local domain="$1" target="$2"
  "${ACME}" --issue --server zerossl -d "${domain}" ${ISSUE_METHOD}
  mkdir -p "${CERT_DIR}/${target}"
  "${ACME}" --install-cert -d "${domain}" \
    --fullchain-file "${CERT_DIR}/${target}/fullchain.pem" \
    --key-file       "${CERT_DIR}/${target}/privkey.pem" \
    --reloadcmd      "docker exec nextroom-nginx nginx -s reload"
}

issue_and_install "${LIVE_DOMAIN}" live
issue_and_install "${DEV_DOMAIN}"  dev

echo "[acme] 인증서 발급/설치 완료. acme.sh cron 이 자동 갱신합니다 (acme.sh --install-cronjob)."
