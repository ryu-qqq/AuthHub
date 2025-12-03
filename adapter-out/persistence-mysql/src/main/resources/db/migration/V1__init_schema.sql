-- V1__init_schema.sql
-- AuthHub 초기 스키마 생성
-- Entity 기반 스키마 (Long FK 전략)

-- ===============================================
-- Organizations 테이블
-- ===============================================
CREATE TABLE organizations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    status VARCHAR(20) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,

    INDEX idx_organizations_status (status),
    INDEX idx_organizations_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ===============================================
-- Tenants 테이블
-- ===============================================
CREATE TABLE tenants (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    organization_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,

    INDEX idx_tenants_organization_id (organization_id),
    INDEX idx_tenants_status (status),
    INDEX idx_tenants_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ===============================================
-- Users 테이블 (UUID PK, Long FK 전략)
-- ===============================================
CREATE TABLE users (
    id BINARY(16) PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    organization_id BIGINT,
    user_type VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    identifier VARCHAR(255) NOT NULL,
    hashed_password VARCHAR(255) NOT NULL,
    name VARCHAR(100),
    phone_number VARCHAR(20),
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,

    UNIQUE INDEX idx_users_identifier (identifier),
    INDEX idx_users_tenant_id (tenant_id),
    INDEX idx_users_organization_id (organization_id),
    INDEX idx_users_user_type (user_type),
    INDEX idx_users_status (status),
    INDEX idx_users_name (name),
    INDEX idx_users_phone_number (phone_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ===============================================
-- Roles 테이블 (Long FK 전략)
-- ===============================================
CREATE TABLE roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    is_system BOOLEAN NOT NULL DEFAULT FALSE,

    INDEX idx_roles_tenant_id (tenant_id),
    INDEX idx_roles_name (name),
    INDEX idx_roles_is_system (is_system)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ===============================================
-- Permissions 테이블
-- ===============================================
CREATE TABLE permissions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(100) NOT NULL,
    description VARCHAR(500),

    UNIQUE INDEX idx_permissions_code (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ===============================================
-- User-Role 매핑 테이블 (Long FK 전략)
-- ===============================================
CREATE TABLE user_roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BINARY(16) NOT NULL,
    role_id BIGINT NOT NULL,
    assigned_at DATETIME(6) NOT NULL,

    INDEX idx_user_roles_user_id (user_id),
    INDEX idx_user_roles_role_id (role_id),
    UNIQUE INDEX idx_user_roles_user_role (user_id, role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ===============================================
-- Role-Permission 매핑 테이블 (Long FK 전략)
-- ===============================================
CREATE TABLE role_permissions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,

    INDEX idx_role_permissions_role_id (role_id),
    INDEX idx_role_permissions_permission_id (permission_id),
    UNIQUE INDEX idx_role_permissions_role_permission (role_id, permission_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ===============================================
-- RefreshToken 테이블 (Long FK 전략)
-- ===============================================
CREATE TABLE refresh_tokens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BINARY(16) NOT NULL,
    token VARCHAR(500) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,

    UNIQUE INDEX idx_refresh_tokens_user_id (user_id),
    INDEX idx_refresh_tokens_token (token(255))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
