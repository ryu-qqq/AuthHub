-- V3__add_tenant_id_to_organizations.sql
-- Organizations 테이블에 tenant_id 컬럼 추가

-- ===============================================
-- Organizations 테이블에 tenant_id 추가
-- ===============================================
ALTER TABLE organizations
    ADD COLUMN tenant_id BIGINT NOT NULL DEFAULT 1 AFTER name;

-- 인덱스 추가
CREATE INDEX idx_organizations_tenant_id ON organizations(tenant_id);

-- description 컬럼 제거 (Entity에서 사용하지 않음)
ALTER TABLE organizations DROP COLUMN description;
