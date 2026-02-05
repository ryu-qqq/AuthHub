-- =====================================================
-- AuthHub Database Schema V3 - Service 기반 RBAC 확장
-- 서비스 테이블 추가 및 Role/Permission에 serviceId 컬럼 추가
-- =====================================================

-- -----------------------------------------------------
-- 1. services - 서비스 테이블 (신규)
-- PK: service_id (BIGINT AUTO_INCREMENT)
-- UK: service_code (비즈니스 식별자)
-- BaseAuditEntity (SoftDelete 미사용)
-- -----------------------------------------------------
CREATE TABLE services (
    service_id BIGINT NOT NULL AUTO_INCREMENT,
    service_code VARCHAR(50) NOT NULL,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500) NULL,
    status VARCHAR(20) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,

    PRIMARY KEY (service_id),
    UNIQUE KEY uk_services_service_code (service_code),
    INDEX idx_services_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- 2. tenant_services - 테넌트-서비스 구독 테이블 (신규)
-- PK: id (BIGINT AUTO_INCREMENT)
-- FK: tenant_id -> tenants, service_id -> services
-- UK: (tenant_id, service_id) 복합 유니크
-- BaseAuditEntity (SoftDelete 미사용)
-- -----------------------------------------------------
CREATE TABLE tenant_services (
    id BIGINT NOT NULL AUTO_INCREMENT,
    tenant_id VARCHAR(36) NOT NULL,
    service_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL,
    subscribed_at DATETIME(6) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,

    PRIMARY KEY (id),
    UNIQUE KEY uk_tenant_service (tenant_id, service_id),
    INDEX idx_tenant_services_tenant_id (tenant_id),
    INDEX idx_tenant_services_service_id (service_id),
    INDEX idx_tenant_services_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- 3. roles 테이블 - service_id, scope 컬럼 추가 + UK 변경
-- 기존: uk_roles_name(name)
-- 변경: uk_role_tenant_service_name(tenant_id, service_id, name)
-- -----------------------------------------------------
ALTER TABLE roles
    ADD COLUMN service_id BIGINT NULL AFTER tenant_id,
    ADD COLUMN scope VARCHAR(20) NOT NULL DEFAULT 'GLOBAL' AFTER type,
    DROP INDEX uk_roles_name,
    ADD UNIQUE KEY uk_role_tenant_service_name (tenant_id, service_id, name),
    ADD INDEX idx_roles_service_id (service_id),
    ADD INDEX idx_roles_scope (scope);

-- -----------------------------------------------------
-- 4. permissions 테이블 - service_id 컬럼 추가 + UK 변경
-- 기존: uk_permissions_key(permission_key)
-- 변경: uk_permission_service_key(service_id, permission_key)
-- -----------------------------------------------------
ALTER TABLE permissions
    ADD COLUMN service_id BIGINT NULL AFTER permission_id,
    DROP INDEX uk_permissions_key,
    ADD UNIQUE KEY uk_permission_service_key (service_id, permission_key),
    ADD INDEX idx_permissions_service_id (service_id);
