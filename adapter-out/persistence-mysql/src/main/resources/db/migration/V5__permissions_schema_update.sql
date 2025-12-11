-- V5__permissions_schema_update.sql
-- Permission 테이블 스키마 업데이트
-- 새로운 도메인 모델에 맞게 permissions 테이블 변경

-- ===============================================
-- 기존 permissions 테이블 삭제 및 재생성
-- (데이터 손실 주의: 개발 환경에서만 사용)
-- ===============================================

-- 기존 role_permissions 관계 삭제
DROP TABLE IF EXISTS role_permissions;

-- 기존 permissions 테이블 삭제
DROP TABLE IF EXISTS permissions;

-- ===============================================
-- 새로운 permissions 테이블 생성
-- ===============================================
CREATE TABLE permissions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    permission_id BINARY(16) NOT NULL,
    permission_key VARCHAR(100) NOT NULL,
    resource VARCHAR(50) NOT NULL,
    action VARCHAR(50) NOT NULL,
    description VARCHAR(500),
    type VARCHAR(20) NOT NULL,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,

    UNIQUE INDEX uk_permissions_permission_id (permission_id),
    UNIQUE INDEX uk_permissions_key (permission_key),
    INDEX idx_permissions_resource (resource),
    INDEX idx_permissions_type (type),
    INDEX idx_permissions_deleted (deleted)
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
    UNIQUE INDEX idx_role_permissions_role_permission (role_id, permission_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
