#!/bin/bash
# MySQL 최초 기동 시 1회 실행 (데이터 볼륨이 비어 있을 때만).
# live/dev 스키마 2개 + 공용 앱 계정 생성. 실데이터는 컷오버 시 mysqldump 로 복원.
set -euo pipefail

mysql -uroot -p"${MYSQL_ROOT_PASSWORD}" <<SQL
CREATE DATABASE IF NOT EXISTS nextroom_prod CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS nextroom_dev  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE USER IF NOT EXISTS '${APP_DB_USER}'@'%' IDENTIFIED BY '${APP_DB_PASSWORD}';
GRANT ALL PRIVILEGES ON nextroom_prod.* TO '${APP_DB_USER}'@'%';
GRANT ALL PRIVILEGES ON nextroom_dev.*  TO '${APP_DB_USER}'@'%';
FLUSH PRIVILEGES;
SQL

echo "[init] nextroom_prod / nextroom_dev databases and app user created."
