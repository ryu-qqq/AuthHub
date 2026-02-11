-- =====================================================
-- AuthHub Database Schema V6 - 마스터 테넌트 시드 데이터
-- Tenant: connectly (개발사)
-- Organization: 개발팀
-- Users: 류상원, 심명보, 권용진 (전원 SUPER_ADMIN)
--
-- ID: UUIDv7 (타임스탬프 기반 정렬 보장)
-- FK 제약조건 없음 (인덱스 기반 조인)
-- =====================================================

-- UUIDv7 (시간순 정렬 보장, 2026-02-06 생성)
SET @tenant_id  = '019c3173-30cd-7b7e-b09d-c2ce4dc16974';
SET @org_id     = '019c3173-30cf-78b7-a6f7-4fa959dbc649';
SET @user_rsw   = '019c3173-30d0-7262-a395-4e7429084068';
SET @user_smb   = '019c3173-30d1-77ae-bcf5-1b3e68ee5480';
SET @user_kyj   = '019c3173-30d3-7c2f-8c88-ab5f40d0d380';

-- 1. 마스터 테넌트
INSERT IGNORE INTO tenants (tenant_id, name, status, created_at, updated_at)
VALUES (@tenant_id, 'connectly', 'ACTIVE', NOW(6), NOW(6));

-- 2. 조직: 개발팀
INSERT IGNORE INTO organizations (organization_id, tenant_id, name, status, created_at, updated_at)
VALUES (@org_id, @tenant_id, '개발팀', 'ACTIVE', NOW(6), NOW(6));

-- 3. 사용자 (비밀번호: 12345, BCrypt 해시)
INSERT IGNORE INTO users (user_id, organization_id, identifier, phone_number, hashed_password, status, created_at, updated_at)
VALUES
    (@user_rsw, @org_id, 'rsw2@connectly.co.kr', NULL, '$2b$12$lehKGxDN/XpNS.Zbui9Io.6lKqy32vrDTLRjxkjqsxdNAwtQTq2Z.', 'ACTIVE', NOW(6), NOW(6)),
    (@user_smb, @org_id, 'mugbo1410@connectly.co.kr', NULL, '$2b$12$lehKGxDN/XpNS.Zbui9Io.6lKqy32vrDTLRjxkjqsxdNAwtQTq2Z.', 'ACTIVE', NOW(6), NOW(6)),
    (@user_kyj, @org_id, 'kyj@connectly.co.kr', NULL, '$2b$12$lehKGxDN/XpNS.Zbui9Io.6lKqy32vrDTLRjxkjqsxdNAwtQTq2Z.', 'ACTIVE', NOW(6), NOW(6));

-- 4. 사용자-역할 매핑 (전원 SUPER_ADMIN, role_id=1)
INSERT IGNORE INTO user_roles (user_id, role_id, created_at, updated_at)
VALUES
    (@user_rsw, 1, NOW(6), NOW(6)),
    (@user_smb, 1, NOW(6), NOW(6)),
    (@user_kyj, 1, NOW(6), NOW(6));

-- 5. 테넌트-서비스 구독 (전체 6개 서비스)
INSERT IGNORE INTO tenant_services (tenant_id, service_id, status, subscribed_at, created_at, updated_at)
VALUES
    (@tenant_id, 1, 'ACTIVE', NOW(6), NOW(6), NOW(6)),
    (@tenant_id, 2, 'ACTIVE', NOW(6), NOW(6), NOW(6)),
    (@tenant_id, 3, 'ACTIVE', NOW(6), NOW(6), NOW(6)),
    (@tenant_id, 4, 'ACTIVE', NOW(6), NOW(6), NOW(6)),
    (@tenant_id, 5, 'ACTIVE', NOW(6), NOW(6), NOW(6)),
    (@tenant_id, 6, 'ACTIVE', NOW(6), NOW(6), NOW(6));
