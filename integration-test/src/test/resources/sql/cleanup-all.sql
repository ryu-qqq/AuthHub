-- ============================================================
-- Integration Test Cleanup Script
-- ============================================================
-- 모든 테스트 데이터 정리
-- FK 순서 고려: 자식 테이블 → 부모 테이블
-- ============================================================

-- User-Role 관계 테이블
DELETE FROM user_roles WHERE 1=1;

-- Role-Permission 관계 테이블
DELETE FROM role_permissions WHERE 1=1;

-- Permission Usage 테이블
DELETE FROM permission_usages WHERE 1=1;

-- Refresh Token 테이블
DELETE FROM refresh_tokens WHERE 1=1;

-- User 테이블
DELETE FROM users WHERE 1=1;

-- Role 테이블
DELETE FROM roles WHERE 1=1;

-- Permission 테이블
DELETE FROM permissions WHERE 1=1;

-- Organization 테이블
DELETE FROM organizations WHERE 1=1;

-- Tenant 테이블
DELETE FROM tenants WHERE 1=1;
