-- adminCode 제거 1단계: NOT NULL 해제
-- 적용 시점: 새 버전 배포 "전". dev 는 develop merge 전, prod 는 develop → main merge 전.
-- 구버전 앱은 계속 admin_code 를 채워 넣으므로 이 DDL 을 먼저 적용해도 안전하다.
--
-- MODIFY 는 컬럼 정의 전체를 다시 쓰므로, 적용 전에 현재 정의를 확인하고 타입/문자셋을 그대로 유지한다.
--   SHOW CREATE TABLE shop;
-- 아래는 엔티티 매핑(@Column(length = 5))과 같은 VARCHAR(5) 를 가정한다. 다르면 타입만 맞춰 고친다.

ALTER TABLE shop MODIFY admin_code VARCHAR(5) NULL;
