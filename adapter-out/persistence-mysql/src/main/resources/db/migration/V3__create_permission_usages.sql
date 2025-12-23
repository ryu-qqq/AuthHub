-- =============================================
-- V3: permission_usages 테이블 생성
-- 권한 사용 이력 추적 (CI/CD 권한 검증 후 승인 시 등록)
-- =============================================

CREATE TABLE permission_usages (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '내부 기본 키',
    usage_id            BINARY(16) NOT NULL COMMENT '사용 이력 UUID (UUIDv7)',
    permission_key      VARCHAR(100) NOT NULL COMMENT '권한 키 (예: product:read)',
    service_name        VARCHAR(100) NOT NULL COMMENT '서비스명 (예: product-service)',
    locations           JSON COMMENT '코드 위치 목록 (JSON 배열)',
    last_scanned_at     DATETIME(6) NOT NULL COMMENT '마지막 스캔 시간',
    created_at          DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) COMMENT '생성 일시',
    updated_at          DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '수정 일시',

    -- 유니크 제약 조건: 동일 권한+서비스 조합은 하나만 존재
    CONSTRAINT uk_permission_usages_key_service UNIQUE (permission_key, service_name),

    -- usage_id 유니크 인덱스
    CONSTRAINT uk_permission_usages_uuid UNIQUE (usage_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='권한 사용 이력';

-- 조회 성능을 위한 인덱스
-- Note: permission_key 인덱스는 uk_permission_usages_key_service 복합 인덱스가 커버
CREATE INDEX idx_permission_usages_service ON permission_usages (service_name);
CREATE INDEX idx_permission_usages_scanned ON permission_usages (last_scanned_at);
