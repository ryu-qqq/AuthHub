-- =====================================================
-- AuthHub Database Schema - Initial Migration
-- Version: V1
-- Description: JPA Entity 기반 스키마 생성
-- =====================================================

-- =====================================================
-- 1. Tenants Table
-- =====================================================
CREATE TABLE tenants (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '내부 기본 키',
    tenant_id BINARY(16) NOT NULL COMMENT '테넌트 UUID (UUIDv7)',
    name VARCHAR(100) NOT NULL COMMENT '테넌트 이름',
    status VARCHAR(20) NOT NULL COMMENT '테넌트 상태',
    created_at DATETIME(6) NOT NULL COMMENT '생성 일시',
    updated_at DATETIME(6) NOT NULL COMMENT '수정 일시',

    UNIQUE INDEX uk_tenants_tenant_id (tenant_id),
    UNIQUE INDEX uk_tenants_name (name),
    INDEX idx_tenants_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- 2. Organizations Table
-- =====================================================
CREATE TABLE organizations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '내부 기본 키',
    organization_id BINARY(16) NOT NULL COMMENT '조직 UUID (UUIDv7)',
    tenant_id BINARY(16) NOT NULL COMMENT '테넌트 UUID',
    name VARCHAR(100) NOT NULL COMMENT '조직 이름',
    status VARCHAR(20) NOT NULL COMMENT '조직 상태',
    created_at DATETIME(6) NOT NULL COMMENT '생성 일시',
    updated_at DATETIME(6) NOT NULL COMMENT '수정 일시',

    UNIQUE INDEX uk_organizations_organization_id (organization_id),
    UNIQUE INDEX uk_organizations_tenant_name (tenant_id, name),
    INDEX idx_organizations_tenant_id (tenant_id),
    INDEX idx_organizations_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- 3. Users Table
-- =====================================================
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '내부 기본 키',
    user_id BINARY(16) NOT NULL COMMENT '사용자 UUID (UUIDv7)',
    tenant_id BINARY(16) NOT NULL COMMENT '테넌트 UUID',
    organization_id BINARY(16) NOT NULL COMMENT '조직 UUID',
    identifier VARCHAR(255) NOT NULL COMMENT '사용자 식별자 (이메일 등)',
    hashed_password VARCHAR(255) NOT NULL COMMENT '해시된 비밀번호',
    status VARCHAR(20) NOT NULL COMMENT '사용자 상태',
    created_at DATETIME(6) NOT NULL COMMENT '생성 일시',
    updated_at DATETIME(6) NOT NULL COMMENT '수정 일시',

    UNIQUE INDEX uk_users_user_id (user_id),
    UNIQUE INDEX uk_users_tenant_org_identifier (tenant_id, organization_id, identifier),
    INDEX idx_users_tenant_id (tenant_id),
    INDEX idx_users_organization_id (organization_id),
    INDEX idx_users_identifier (identifier),
    INDEX idx_users_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- 4. Roles Table
-- =====================================================
CREATE TABLE roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '내부 기본 키',
    role_id BINARY(16) NOT NULL COMMENT '역할 UUID (UUIDv7)',
    tenant_id BINARY(16) NULL COMMENT '테넌트 UUID (GLOBAL일 경우 NULL)',
    name VARCHAR(100) NOT NULL COMMENT '역할 이름',
    description VARCHAR(500) NULL COMMENT '역할 설명',
    scope VARCHAR(20) NOT NULL COMMENT '역할 범위 (GLOBAL, TENANT, ORGANIZATION)',
    type VARCHAR(20) NOT NULL COMMENT '역할 유형 (SYSTEM, CUSTOM)',
    deleted BOOLEAN NOT NULL DEFAULT FALSE COMMENT '삭제 여부',
    created_at DATETIME(6) NOT NULL COMMENT '생성 일시',
    updated_at DATETIME(6) NOT NULL COMMENT '수정 일시',

    UNIQUE INDEX uk_roles_role_id (role_id),
    UNIQUE INDEX uk_roles_tenant_name (tenant_id, name),
    INDEX idx_roles_tenant_id (tenant_id),
    INDEX idx_roles_scope (scope),
    INDEX idx_roles_type (type),
    INDEX idx_roles_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- 5. Permissions Table
-- =====================================================
CREATE TABLE permissions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '내부 기본 키',
    permission_id BINARY(16) NOT NULL COMMENT '권한 UUID (UUIDv7)',
    permission_key VARCHAR(100) NOT NULL COMMENT '권한 키 (resource:action)',
    resource VARCHAR(50) NOT NULL COMMENT '리소스명',
    action VARCHAR(50) NOT NULL COMMENT '액션명',
    description VARCHAR(500) NULL COMMENT '권한 설명',
    type VARCHAR(20) NOT NULL COMMENT '권한 유형 (SYSTEM, CUSTOM)',
    deleted BOOLEAN NOT NULL DEFAULT FALSE COMMENT '삭제 여부',
    created_at DATETIME(6) NOT NULL COMMENT '생성 일시',
    updated_at DATETIME(6) NOT NULL COMMENT '수정 일시',

    UNIQUE INDEX uk_permissions_permission_id (permission_id),
    UNIQUE INDEX uk_permissions_key (permission_key),
    INDEX idx_permissions_resource (resource),
    INDEX idx_permissions_type (type),
    INDEX idx_permissions_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- 6. User-Role Mapping Table
-- =====================================================
CREATE TABLE user_roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '내부 기본 키',
    user_id BINARY(16) NOT NULL COMMENT '사용자 UUID',
    role_id BINARY(16) NOT NULL COMMENT '역할 UUID',
    assigned_at DATETIME(6) NOT NULL COMMENT '할당 시간',

    UNIQUE INDEX uk_user_roles_user_role (user_id, role_id),
    INDEX idx_user_roles_user_id (user_id),
    INDEX idx_user_roles_role_id (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- 7. Role-Permission Mapping Table
-- =====================================================
CREATE TABLE role_permissions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '내부 기본 키',
    role_id BINARY(16) NOT NULL COMMENT '역할 UUID',
    permission_id BINARY(16) NOT NULL COMMENT '권한 UUID',
    granted_at DATETIME(6) NOT NULL COMMENT '부여 시간',

    UNIQUE INDEX uk_role_permissions_role_permission (role_id, permission_id),
    INDEX idx_role_permissions_role_id (role_id),
    INDEX idx_role_permissions_permission_id (permission_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- 8. Refresh Tokens Table
-- =====================================================
CREATE TABLE refresh_tokens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '내부 기본 키',
    user_id BINARY(16) NOT NULL COMMENT '사용자 UUID',
    token VARCHAR(500) NOT NULL COMMENT 'Refresh Token',
    created_at DATETIME(6) NOT NULL COMMENT '생성 일시',
    updated_at DATETIME(6) NOT NULL COMMENT '수정 일시',

    UNIQUE INDEX uk_refresh_tokens_user_id (user_id),
    INDEX idx_refresh_tokens_token (token(255))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- 9. Endpoint Permissions Table
-- =====================================================
CREATE TABLE endpoint_permissions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '내부 기본 키',
    endpoint_permission_id BINARY(16) NOT NULL COMMENT '엔드포인트 권한 UUID (UUIDv7)',
    service_name VARCHAR(100) NOT NULL COMMENT '서비스명',
    path VARCHAR(500) NOT NULL COMMENT '엔드포인트 경로',
    method VARCHAR(10) NOT NULL COMMENT 'HTTP 메서드',
    description VARCHAR(500) NULL COMMENT '설명',
    is_public BOOLEAN NOT NULL DEFAULT FALSE COMMENT '공개 여부',
    required_permissions VARCHAR(2000) NULL COMMENT '필요 권한 목록',
    required_roles VARCHAR(1000) NULL COMMENT '필요 역할 목록',
    version BIGINT NOT NULL DEFAULT 0 COMMENT '버전 (낙관적 락)',
    deleted BOOLEAN NOT NULL DEFAULT FALSE COMMENT '삭제 여부',
    created_at DATETIME(6) NOT NULL COMMENT '생성 일시',
    updated_at DATETIME(6) NOT NULL COMMENT '수정 일시',

    UNIQUE INDEX uk_endpoint_permissions_id (endpoint_permission_id),
    UNIQUE INDEX uk_endpoint_permissions_service_path_method (service_name, path(255), method),
    INDEX idx_endpoint_permissions_service_name (service_name),
    INDEX idx_endpoint_permissions_method (method),
    INDEX idx_endpoint_permissions_is_public (is_public),
    INDEX idx_endpoint_permissions_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
