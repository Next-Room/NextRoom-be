-- 회원 탈퇴 소프트 딜리트 전환
-- 적용 순서: 이 DDL을 먼저 적용한 뒤 애플리케이션을 배포한다.
-- 컬럼이 nullable 이므로 기존 애플리케이션과 호환되어 선적용해도 안전하다.
-- 주의: 이 디렉터리는 mysql/init 과 달리 컨테이너 초기화 시 자동 실행되지 않는다. 수동으로 적용한다.

ALTER TABLE shop ADD COLUMN deleted_at DATETIME(6) NULL;

-- 조회 진입점이 deleted_at IS NULL 조건을 항상 함께 태우므로 복합 인덱스를 건다.
CREATE INDEX idx_shop_email_deleted_at ON shop (email, deleted_at);
