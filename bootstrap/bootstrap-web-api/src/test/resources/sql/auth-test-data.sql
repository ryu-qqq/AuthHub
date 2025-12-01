-- ========================================
-- Auth Integration Test Data
-- ========================================
-- 목적: 사용자 등록 E2E 테스트를 위한 데이터
-- Flyway 마이그레이션 후 실행됨 (DDL 작성 금지)
-- ========================================

-- Clean up existing test data (역순 삭제: FK 관계 고려)
DELETE FROM users WHERE tenant_id = 1;

-- ========================================
-- PUBLIC Tenant (id=1) 기본 데이터
-- ========================================
-- Note: tenants 테이블은 V1 마이그레이션에서 생성되고,
-- V2에서 PUBLIC Tenant가 INSERT 됨

-- ========================================
-- 중복 테스트용 기존 사용자
-- ========================================
INSERT INTO users (
    user_id, tenant_id, organization_id, email, phone_number, password,
    name, profile_image_url, user_type, status, credential_type,
    phone_login_enabled, email_login_enabled, created_at, updated_at
) VALUES (
    'existing-user-uuid-0001', 1, NULL, NULL, '+82-10-9999-8888',
    '$2a$10$dummyhashedpassword12345', '기존사용자', NULL,
    'PUBLIC', 'ACTIVE', 'PHONE', true, false,
    NOW(), NOW()
);
