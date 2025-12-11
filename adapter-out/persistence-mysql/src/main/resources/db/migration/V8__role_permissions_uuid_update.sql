-- V8__role_permissions_uuid_update.sql
-- role_permissions 테이블 UUID 기반으로 업데이트
-- JPA Entity와 스키마 동기화

-- ===============================================
-- role_permissions 테이블 재생성
-- (기존 데이터 삭제됨 - 개발 단계이므로 허용)
-- ===============================================

-- 기존 테이블 삭제
DROP TABLE IF EXISTS role_permissions;

-- UUID 기반으로 재생성
CREATE TABLE role_permissions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_id BINARY(16) NOT NULL COMMENT 'Role UUID',
    permission_id BINARY(16) NOT NULL COMMENT 'Permission UUID',
    granted_at DATETIME(6) NOT NULL COMMENT '권한 부여 시간',

    INDEX idx_role_permissions_role_id (role_id),
    INDEX idx_role_permissions_permission_id (permission_id),
    UNIQUE INDEX uk_role_permissions_role_permission (role_id, permission_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
