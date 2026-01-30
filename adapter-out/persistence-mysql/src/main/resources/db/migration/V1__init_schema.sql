-- =====================================================
-- AuthHub Database Schema V1 - Initial Schema
-- Based on JPA Entity definitions (RBAC Redesign)
-- =====================================================

-- -----------------------------------------------------
-- 1. tenants - 테넌트 테이블
-- PK: tenant_id (UUIDv7, VARCHAR(36))
-- -----------------------------------------------------
CREATE TABLE tenants (
    tenant_id VARCHAR(36) NOT NULL,
    name VARCHAR(100) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    deleted_at DATETIME(6) NULL,

    PRIMARY KEY (tenant_id),
    UNIQUE KEY uk_tenants_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- 2. organizations - 조직 테이블
-- PK: organization_id (UUIDv7, VARCHAR(36))
-- FK: tenant_id (VARCHAR(36)) -> tenants
-- -----------------------------------------------------
CREATE TABLE organizations (
    organization_id VARCHAR(36) NOT NULL,
    tenant_id VARCHAR(36) NOT NULL,
    name VARCHAR(100) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    deleted_at DATETIME(6) NULL,

    PRIMARY KEY (organization_id),
    UNIQUE KEY uk_organizations_tenant_name (tenant_id, name),
    INDEX idx_organizations_tenant_id (tenant_id),
    INDEX idx_organizations_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- 3. users - 사용자 테이블
-- PK: user_id (UUIDv7, VARCHAR(36))
-- FK: organization_id (VARCHAR(36)) -> organizations
-- -----------------------------------------------------
CREATE TABLE users (
    user_id VARCHAR(36) NOT NULL,
    organization_id VARCHAR(36) NOT NULL,
    identifier VARCHAR(100) NOT NULL,
    phone_number VARCHAR(20) NULL,
    hashed_password VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    deleted_at DATETIME(6) NULL,

    PRIMARY KEY (user_id),
    UNIQUE KEY uk_users_org_identifier (organization_id, identifier),
    INDEX idx_users_organization_id (organization_id),
    INDEX idx_users_identifier (identifier),
    INDEX idx_users_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- 4. roles - 역할 테이블
-- PK: role_id (BIGINT AUTO_INCREMENT)
-- FK: tenant_id (VARCHAR(36), nullable) -> tenants
-- -----------------------------------------------------
CREATE TABLE roles (
    role_id BIGINT NOT NULL AUTO_INCREMENT,
    tenant_id VARCHAR(36) NULL,
    name VARCHAR(50) NOT NULL,
    display_name VARCHAR(100) NULL,
    description VARCHAR(500) NULL,
    type VARCHAR(20) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    deleted_at DATETIME(6) NULL,

    PRIMARY KEY (role_id),
    UNIQUE KEY uk_roles_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- 5. permissions - 권한 테이블 (Global Only)
-- PK: permission_id (BIGINT AUTO_INCREMENT)
-- -----------------------------------------------------
CREATE TABLE permissions (
    permission_id BIGINT NOT NULL AUTO_INCREMENT,
    permission_key VARCHAR(100) NOT NULL,
    resource VARCHAR(50) NOT NULL,
    action VARCHAR(50) NOT NULL,
    description VARCHAR(500) NULL,
    type VARCHAR(20) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    deleted_at DATETIME(6) NULL,

    PRIMARY KEY (permission_id),
    UNIQUE KEY uk_permissions_key (permission_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- 6. permission_endpoints - 권한-엔드포인트 매핑 테이블
-- PK: permission_endpoint_id (BIGINT AUTO_INCREMENT)
-- FK: permission_id (BIGINT) -> permissions
-- -----------------------------------------------------
CREATE TABLE permission_endpoints (
    permission_endpoint_id BIGINT NOT NULL AUTO_INCREMENT,
    permission_id BIGINT NOT NULL,
    url_pattern VARCHAR(500) NOT NULL,
    http_method VARCHAR(10) NOT NULL,
    description VARCHAR(500) NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    deleted_at DATETIME(6) NULL,

    PRIMARY KEY (permission_endpoint_id),
    UNIQUE KEY uk_permission_endpoints_url_method (url_pattern, http_method),
    INDEX idx_permission_endpoints_permission_id (permission_id),
    INDEX idx_permission_endpoints_url_pattern (url_pattern),
    INDEX idx_permission_endpoints_http_method (http_method)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- 7. user_roles - 사용자-역할 관계 테이블
-- PK: user_role_id (BIGINT AUTO_INCREMENT)
-- FK: user_id (VARCHAR(36)) -> users
-- FK: role_id (BIGINT) -> roles
-- -----------------------------------------------------
CREATE TABLE user_roles (
    user_role_id BIGINT NOT NULL AUTO_INCREMENT,
    user_id VARCHAR(36) NOT NULL,
    role_id BIGINT NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,

    PRIMARY KEY (user_role_id),
    UNIQUE KEY uk_user_roles_user_role (user_id, role_id),
    INDEX idx_user_roles_user_id (user_id),
    INDEX idx_user_roles_role_id (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- 8. role_permissions - 역할-권한 관계 테이블
-- PK: role_permission_id (BIGINT AUTO_INCREMENT)
-- FK: role_id (BIGINT) -> roles
-- FK: permission_id (BIGINT) -> permissions
-- Hard Delete (Soft Delete 미적용)
-- -----------------------------------------------------
CREATE TABLE role_permissions (
    role_permission_id BIGINT NOT NULL AUTO_INCREMENT,
    role_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    created_at DATETIME(6) NOT NULL,

    PRIMARY KEY (role_permission_id),
    UNIQUE KEY uk_role_permission (role_id, permission_id),
    INDEX idx_role_permission_role_id (role_id),
    INDEX idx_role_permission_permission_id (permission_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- 9. refresh_tokens - 리프레시 토큰 테이블
-- PK: refresh_token_id (BINARY(16) - UUID)
-- FK: user_id (BINARY(16) - UUID)
-- -----------------------------------------------------
CREATE TABLE refresh_tokens (
    refresh_token_id BINARY(16) NOT NULL,
    user_id BINARY(16) NOT NULL,
    token VARCHAR(2000) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,

    PRIMARY KEY (refresh_token_id),
    UNIQUE KEY uk_refresh_tokens_user_id (user_id),
    INDEX idx_refresh_tokens_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
