-- -----------------------------------------------------
-- V2: permission_endpoints 테이블에 service_name, is_public 컬럼 추가
-- Gateway 권한 필터 지원을 위한 스키마 확장
-- -----------------------------------------------------

-- service_name 컬럼 추가 (서비스 이름)
ALTER TABLE permission_endpoints
    ADD COLUMN service_name VARCHAR(100) NOT NULL DEFAULT 'default-service' AFTER permission_id;

-- is_public 컬럼 추가 (공개 엔드포인트 여부)
ALTER TABLE permission_endpoints
    ADD COLUMN is_public TINYINT(1) NOT NULL DEFAULT 0 AFTER description;

-- service_name 인덱스 추가 (서비스별 조회 최적화)
CREATE INDEX idx_permission_endpoints_service_name ON permission_endpoints (service_name);

-- 기존 unique constraint 삭제 후 service_name 포함하여 재생성
ALTER TABLE permission_endpoints
    DROP INDEX uk_permission_endpoints_url_method;

ALTER TABLE permission_endpoints
    ADD CONSTRAINT uk_permission_endpoints_service_url_method
    UNIQUE (service_name, url_pattern, http_method);
