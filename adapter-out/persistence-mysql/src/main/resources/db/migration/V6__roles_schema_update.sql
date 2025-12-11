-- V6__roles_schema_update.sql
-- Role 테이블 스키마 업데이트
-- 새로운 도메인 모델에 맞게 roles 테이블 변경

-- ===============================================
-- 기존 roles 테이블 삭제 및 재생성
-- (데이터 손실 주의: 개발 환경에서만 사용)
-- ===============================================

-- 기존 user_roles 관계 삭제 (임시)
DROP TABLE IF EXISTS user_roles;

-- 기존 role_permissions 관계 삭제
DROP TABLE IF EXISTS role_permissions;

-- 기존 roles 테이블 삭제
DROP TABLE IF EXISTS roles;

-- ===============================================
-- 새로운 roles 테이블 생성
-- ===============================================
CREATE TABLE roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_id BINARY(16) NOT NULL,
    tenant_id BINARY(16),
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    scope VARCHAR(20) NOT NULL,
    type VARCHAR(20) NOT NULL,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,

    UNIQUE INDEX uk_roles_role_id (role_id),
    UNIQUE INDEX uk_roles_tenant_name (tenant_id, name),
    INDEX idx_roles_tenant_id (tenant_id),
    INDEX idx_roles_scope (scope),
    INDEX idx_roles_type (type),
    INDEX idx_roles_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ===============================================
-- role_permissions 테이블 재생성
-- ===============================================
CREATE TABLE role_permissions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,

    INDEX idx_role_permissions_role_id (role_id),
    INDEX idx_role_permissions_permission_id (permission_id),
    UNIQUE INDEX uk_role_permissions_role_permission (role_id, permission_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ===============================================
-- user_roles 테이블 재생성
-- ===============================================
CREATE TABLE user_roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BINARY(16) NOT NULL,
    role_id BIGINT NOT NULL,
    assigned_at DATETIME(6) NOT NULL,

    INDEX idx_user_roles_user_id (user_id),
    INDEX idx_user_roles_role_id (role_id),
    UNIQUE INDEX uk_user_roles_user_role (user_id, role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
