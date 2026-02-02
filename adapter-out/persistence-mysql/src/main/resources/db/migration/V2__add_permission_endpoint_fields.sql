-- -----------------------------------------------------
-- V2: permission_endpoints 테이블에 service_name, is_public 컬럼 추가
-- Gateway 권한 필터 지원을 위한 스키마 확장
-- -----------------------------------------------------

-- 단일 ALTER TABLE 문으로 성능 최적화 (테이블 락 시간 최소화)
ALTER TABLE permission_endpoints
    ADD COLUMN service_name VARCHAR(100) NOT NULL DEFAULT 'default-service' AFTER permission_id,
    ADD COLUMN is_public TINYINT(1) NOT NULL DEFAULT 0 AFTER description,
    ADD INDEX idx_permission_endpoints_service_name (service_name),
    DROP INDEX uk_permission_endpoints_url_method,
    ADD CONSTRAINT uk_permission_endpoints_service_url_method
        UNIQUE (service_name, url_pattern, http_method);
