-- V4__organizations_uuid_migration.sql
-- Organizations 테이블 UUID 마이그레이션

-- ===============================================
-- 기존 인덱스 및 데이터 정리
-- ===============================================
DROP INDEX idx_organizations_tenant_id ON organizations;
DROP INDEX idx_organizations_status ON organizations;
DROP INDEX idx_organizations_name ON organizations;

-- 기존 테이블 삭제 (테스트 환경용 - 프로덕션에서는 데이터 마이그레이션 필요)
DROP TABLE IF EXISTS organizations;

-- ===============================================
-- Organizations 테이블 재생성 (UUID 기반)
-- ===============================================
CREATE TABLE organizations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    organization_id BINARY(16) NOT NULL COMMENT 'UUIDv7 외부 식별자',
    tenant_id BINARY(16) NOT NULL COMMENT 'FK to tenants (Long FK 전략)',
    name VARCHAR(100) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,

    -- 유니크 제약: organization_id 단독
    UNIQUE INDEX uk_organizations_organization_id (organization_id),

    -- 유니크 제약: tenant + name 복합 (테넌트 내 조직명 중복 방지)
    UNIQUE INDEX uk_organizations_tenant_name (tenant_id, name),

    -- 조회 인덱스
    INDEX idx_organizations_tenant_id (tenant_id),
    INDEX idx_organizations_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
