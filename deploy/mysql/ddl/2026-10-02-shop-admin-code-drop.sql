-- adminCode 제거 2단계: 컬럼 삭제
-- 적용 시점: 새 버전이 dev 와 prod 모두에서 안정화되어 이전 버전으로 롤백할 일이 없을 때.
-- 이 DDL 을 적용한 뒤에는 adminCode 를 쓰는 이전 버전으로 롤백할 수 없다.
-- admin_code 에 걸린 인덱스(있다면)는 컬럼과 함께 삭제된다.

ALTER TABLE shop DROP COLUMN admin_code;
