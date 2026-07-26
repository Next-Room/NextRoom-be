#!/usr/bin/env bash
# 자체 호스팅 MySQL 일일 백업 → S3 (호스트 cron 으로 실행)
# 예) crontab:  0 4 * * *  /home/ubuntu/NextRoom-be/deploy/backup/backup.sh >> /var/log/nextroom-backup.log 2>&1
#
# 필요: 호스트에 aws cli, deploy/.env 에 MYSQL_ROOT_PASSWORD, 아래 S3_BUCKET 설정
set -euo pipefail

DEPLOY_DIR="$(cd "$(dirname "$0")/.." && pwd)"
# shellcheck disable=SC1091
set -a; source "${DEPLOY_DIR}/.env"; set +a

S3_BUCKET="${BACKUP_S3_BUCKET:-nextroom-image}"
S3_PREFIX="${BACKUP_S3_PREFIX:-db-backups}"
RETENTION_DAYS="${BACKUP_RETENTION_DAYS:-14}"
TS="$(date +%Y%m%d-%H%M%S)"
TMP_DIR="$(mktemp -d)"
trap 'rm -rf "${TMP_DIR}"' EXIT

for DB in nextroom_prod nextroom_dev; do
  FILE="${TMP_DIR}/${DB}-${TS}.sql.gz"
  docker exec nextroom-mysql \
    mysqldump -uroot -p"${MYSQL_ROOT_PASSWORD}" \
      --single-transaction --routines --triggers "${DB}" \
    | gzip > "${FILE}"
  aws s3 cp "${FILE}" "s3://${S3_BUCKET}/${S3_PREFIX}/${DB}/${DB}-${TS}.sql.gz"
  echo "[backup] uploaded ${DB}-${TS}.sql.gz"
done

# 오래된 백업 정리 (S3 lifecycle 정책으로 대체 가능)
CUTOFF="$(date -d "-${RETENTION_DAYS} days" +%Y%m%d 2>/dev/null || date -v-"${RETENTION_DAYS}"d +%Y%m%d)"
echo "[backup] done (retention cutoff ${CUTOFF}; S3 lifecycle 권장)."
