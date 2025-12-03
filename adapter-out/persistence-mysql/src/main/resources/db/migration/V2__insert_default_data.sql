-- V2__insert_default_data.sql
-- AuthHub 기본 데이터 삽입

-- ===============================================
-- 기본 Organization 생성
-- ===============================================
INSERT INTO organizations (name, description, status, created_at, updated_at)
VALUES ('Default Organization', 'System default organization', 'ACTIVE', NOW(6), NOW(6));

-- ===============================================
-- 기본 Tenant 생성 (organization_id = 1)
-- ===============================================
INSERT INTO tenants (name, description, organization_id, status, created_at, updated_at)
VALUES ('Default Tenant', 'System default tenant', 1, 'ACTIVE', NOW(6), NOW(6));

-- ===============================================
-- 기본 System Roles 생성
-- ===============================================
-- System Admin Role (tenant_id = NULL for system-wide)
INSERT INTO roles (tenant_id, name, description, is_system)
VALUES (NULL, 'SYSTEM_ADMIN', 'System Administrator with full access', TRUE);

-- Tenant Admin Role
INSERT INTO roles (tenant_id, name, description, is_system)
VALUES (1, 'TENANT_ADMIN', 'Tenant Administrator', TRUE);

-- Default User Role
INSERT INTO roles (tenant_id, name, description, is_system)
VALUES (1, 'USER', 'Default User Role', TRUE);

-- ===============================================
-- 기본 Permissions 생성
-- ===============================================
-- User Management Permissions
INSERT INTO permissions (code, description) VALUES ('USER_READ', 'View users');
INSERT INTO permissions (code, description) VALUES ('USER_CREATE', 'Create users');
INSERT INTO permissions (code, description) VALUES ('USER_UPDATE', 'Update users');
INSERT INTO permissions (code, description) VALUES ('USER_DELETE', 'Delete users');

-- Role Management Permissions
INSERT INTO permissions (code, description) VALUES ('ROLE_READ', 'View roles');
INSERT INTO permissions (code, description) VALUES ('ROLE_CREATE', 'Create roles');
INSERT INTO permissions (code, description) VALUES ('ROLE_UPDATE', 'Update roles');
INSERT INTO permissions (code, description) VALUES ('ROLE_DELETE', 'Delete roles');

-- Tenant Management Permissions
INSERT INTO permissions (code, description) VALUES ('TENANT_READ', 'View tenants');
INSERT INTO permissions (code, description) VALUES ('TENANT_CREATE', 'Create tenants');
INSERT INTO permissions (code, description) VALUES ('TENANT_UPDATE', 'Update tenants');
INSERT INTO permissions (code, description) VALUES ('TENANT_DELETE', 'Delete tenants');

-- Organization Management Permissions
INSERT INTO permissions (code, description) VALUES ('ORGANIZATION_READ', 'View organizations');
INSERT INTO permissions (code, description) VALUES ('ORGANIZATION_CREATE', 'Create organizations');
INSERT INTO permissions (code, description) VALUES ('ORGANIZATION_UPDATE', 'Update organizations');
INSERT INTO permissions (code, description) VALUES ('ORGANIZATION_DELETE', 'Delete organizations');

-- ===============================================
-- Role-Permission 매핑
-- ===============================================
-- SYSTEM_ADMIN: All permissions (1-16)
INSERT INTO role_permissions (role_id, permission_id) VALUES (1, 1);
INSERT INTO role_permissions (role_id, permission_id) VALUES (1, 2);
INSERT INTO role_permissions (role_id, permission_id) VALUES (1, 3);
INSERT INTO role_permissions (role_id, permission_id) VALUES (1, 4);
INSERT INTO role_permissions (role_id, permission_id) VALUES (1, 5);
INSERT INTO role_permissions (role_id, permission_id) VALUES (1, 6);
INSERT INTO role_permissions (role_id, permission_id) VALUES (1, 7);
INSERT INTO role_permissions (role_id, permission_id) VALUES (1, 8);
INSERT INTO role_permissions (role_id, permission_id) VALUES (1, 9);
INSERT INTO role_permissions (role_id, permission_id) VALUES (1, 10);
INSERT INTO role_permissions (role_id, permission_id) VALUES (1, 11);
INSERT INTO role_permissions (role_id, permission_id) VALUES (1, 12);
INSERT INTO role_permissions (role_id, permission_id) VALUES (1, 13);
INSERT INTO role_permissions (role_id, permission_id) VALUES (1, 14);
INSERT INTO role_permissions (role_id, permission_id) VALUES (1, 15);
INSERT INTO role_permissions (role_id, permission_id) VALUES (1, 16);

-- TENANT_ADMIN: User/Role management within tenant
INSERT INTO role_permissions (role_id, permission_id) VALUES (2, 1);
INSERT INTO role_permissions (role_id, permission_id) VALUES (2, 2);
INSERT INTO role_permissions (role_id, permission_id) VALUES (2, 3);
INSERT INTO role_permissions (role_id, permission_id) VALUES (2, 4);
INSERT INTO role_permissions (role_id, permission_id) VALUES (2, 5);
INSERT INTO role_permissions (role_id, permission_id) VALUES (2, 6);
INSERT INTO role_permissions (role_id, permission_id) VALUES (2, 7);
INSERT INTO role_permissions (role_id, permission_id) VALUES (2, 8);

-- USER: Basic read permissions
INSERT INTO role_permissions (role_id, permission_id) VALUES (3, 1);
INSERT INTO role_permissions (role_id, permission_id) VALUES (3, 5);

-- ===============================================
-- 기본 Admin User 생성
-- UUID: 550e8400-e29b-41d4-a716-446655440000 (고정 UUID for admin)
-- 비밀번호: BCrypt 인코딩된 'admin123' (운영 환경에서는 반드시 변경!)
-- ===============================================
INSERT INTO users (
    id,
    tenant_id,
    organization_id,
    user_type,
    status,
    identifier,
    hashed_password,
    name,
    phone_number,
    created_at,
    updated_at
) VALUES (
    UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440000', '-', '')),
    1,
    1,
    'INTERNAL',
    'ACTIVE',
    'admin@authhub.com',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGc6ttMA4hOKZdPR.kS4ygqBqE3C',
    'System Admin',
    NULL,
    NOW(6),
    NOW(6)
);

-- ===============================================
-- Admin User에 SYSTEM_ADMIN Role 할당
-- ===============================================
INSERT INTO user_roles (user_id, role_id, assigned_at)
VALUES (
    UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440000', '-', '')),
    1,
    NOW(6)
);
