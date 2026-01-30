-- ============================================================
-- Integration Test Cleanup Script
-- ============================================================
-- 모든 테스트 데이터 정리
-- FK 순서 고려: 자식 테이블 → 부모 테이블
-- ============================================================

-- 1. 관계 테이블 먼저 삭제 (FK 참조)
DELETE FROM role_permissions;
DELETE FROM user_roles;
DELETE FROM permission_endpoints;

-- 2. 토큰 테이블
DELETE FROM refresh_tokens;

-- 3. 주요 엔티티 테이블
DELETE FROM permissions;
DELETE FROM roles;
DELETE FROM users;
DELETE FROM organizations;
DELETE FROM tenants;
