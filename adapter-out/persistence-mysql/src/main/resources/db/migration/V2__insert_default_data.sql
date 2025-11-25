-- V2__insert_default_data.sql
-- AuthHub 기본 데이터 삽입

-- 기본 Organization 생성
INSERT INTO organizations (name, description, status, created_at, updated_at)
VALUES ('Default Organization', 'System default organization', 'ACTIVE', NOW(6), NOW(6));

-- 기본 Tenant 생성 (organization_id = 1)
INSERT INTO tenants (name, description, organization_id, status, created_at, updated_at)
VALUES ('Default Tenant', 'System default tenant', 1, 'ACTIVE', NOW(6), NOW(6));

-- 기본 Admin User 생성 (organization_id = 1, tenant_id = 1)
-- 비밀번호는 BCrypt 인코딩된 'admin123' (실제 운영에서는 변경 필요)
INSERT INTO users (email, password, username, organization_id, tenant_id, user_type, status, created_at, updated_at)
VALUES (
    'admin@authhub.com',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGc6ttMA4hOKZdPR.kS4ygqBqE3C',
    'admin',
    1,
    1,
    'INTERNAL',
    'ACTIVE',
    NOW(6),
    NOW(6)
);
