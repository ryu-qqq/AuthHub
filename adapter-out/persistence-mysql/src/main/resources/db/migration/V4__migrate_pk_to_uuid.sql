-- =====================================================
-- AuthHub Database Schema - PK Migration to UUID
-- Version: V4
-- Description: BIGINT AUTO_INCREMENT PK → UUID PK 마이그레이션
--
-- 변경 사항:
--   - 모든 테이블의 `id BIGINT` 서로게이트 키 제거
--   - 기존 UUID 컬럼을 Primary Key로 승격
--   - 중간 테이블에 새 UUID PK 컬럼 추가
--
-- ⚠️ 주의사항:
--   - 이 마이그레이션은 되돌릴 수 없습니다
--   - 반드시 백업 후 실행하세요
--   - 운영 환경에서는 점검 시간에 실행 권장
-- =====================================================

-- =====================================================
-- 1. user_roles 테이블 - 새 UUID PK 컬럼 추가
-- =====================================================
-- 1.1 새 컬럼 추가
ALTER TABLE user_roles
    ADD COLUMN user_role_id BINARY(16) NULL COMMENT '사용자 역할 매핑 UUID (UUIDv7)' AFTER id;

-- 1.2 기존 데이터에 UUID 값 생성 (MySQL 8.0+ UUID_TO_BIN 사용)
UPDATE user_roles SET user_role_id = UUID_TO_BIN(UUID(), TRUE) WHERE user_role_id IS NULL;

-- 1.3 NOT NULL 제약 추가
ALTER TABLE user_roles MODIFY COLUMN user_role_id BINARY(16) NOT NULL;

-- 1.4 AUTO_INCREMENT 제거 (PK 제거 전 필수)
ALTER TABLE user_roles MODIFY COLUMN id BIGINT NOT NULL;

-- 1.5 기존 PK 제거
ALTER TABLE user_roles DROP PRIMARY KEY;

-- 1.6 새 PK 설정
ALTER TABLE user_roles ADD PRIMARY KEY (user_role_id);

-- 1.7 기존 id 컬럼 삭제
ALTER TABLE user_roles DROP COLUMN id;

-- =====================================================
-- 2. role_permissions 테이블 - 새 UUID PK 컬럼 추가
-- =====================================================
-- 2.1 새 컬럼 추가
ALTER TABLE role_permissions
    ADD COLUMN role_permission_id BINARY(16) NULL COMMENT '역할 권한 매핑 UUID (UUIDv7)' AFTER id;

-- 2.2 기존 데이터에 UUID 값 생성
UPDATE role_permissions SET role_permission_id = UUID_TO_BIN(UUID(), TRUE) WHERE role_permission_id IS NULL;

-- 2.3 NOT NULL 제약 추가
ALTER TABLE role_permissions MODIFY COLUMN role_permission_id BINARY(16) NOT NULL;

-- 2.4 AUTO_INCREMENT 제거
ALTER TABLE role_permissions MODIFY COLUMN id BIGINT NOT NULL;

-- 2.5 기존 PK 제거
ALTER TABLE role_permissions DROP PRIMARY KEY;

-- 2.6 새 PK 설정
ALTER TABLE role_permissions ADD PRIMARY KEY (role_permission_id);

-- 2.7 기존 id 컬럼 삭제
ALTER TABLE role_permissions DROP COLUMN id;

-- =====================================================
-- 3. refresh_tokens 테이블 - 새 UUID PK 컬럼 추가
-- =====================================================
-- 3.1 새 컬럼 추가
ALTER TABLE refresh_tokens
    ADD COLUMN refresh_token_id BINARY(16) NULL COMMENT 'RefreshToken UUID (UUIDv7)' AFTER id;

-- 3.2 기존 데이터에 UUID 값 생성
UPDATE refresh_tokens SET refresh_token_id = UUID_TO_BIN(UUID(), TRUE) WHERE refresh_token_id IS NULL;

-- 3.3 NOT NULL 제약 추가
ALTER TABLE refresh_tokens MODIFY COLUMN refresh_token_id BINARY(16) NOT NULL;

-- 3.4 AUTO_INCREMENT 제거
ALTER TABLE refresh_tokens MODIFY COLUMN id BIGINT NOT NULL;

-- 3.5 기존 PK 제거
ALTER TABLE refresh_tokens DROP PRIMARY KEY;

-- 3.6 새 PK 설정
ALTER TABLE refresh_tokens ADD PRIMARY KEY (refresh_token_id);

-- 3.7 기존 id 컬럼 삭제
ALTER TABLE refresh_tokens DROP COLUMN id;

-- =====================================================
-- 4. tenants 테이블 - UUID를 PK로 승격
-- =====================================================
-- 4.1 AUTO_INCREMENT 제거
ALTER TABLE tenants MODIFY COLUMN id BIGINT NOT NULL;

-- 4.2 기존 PK 제거
ALTER TABLE tenants DROP PRIMARY KEY;

-- 4.3 새 PK 설정 (tenant_id)
ALTER TABLE tenants ADD PRIMARY KEY (tenant_id);

-- 4.4 기존 id 컬럼 삭제
ALTER TABLE tenants DROP COLUMN id;

-- 4.5 기존 UK 인덱스 제거 (이제 PK가 됨)
ALTER TABLE tenants DROP INDEX uk_tenants_tenant_id;

-- =====================================================
-- 5. organizations 테이블 - UUID를 PK로 승격
-- =====================================================
-- 5.1 AUTO_INCREMENT 제거
ALTER TABLE organizations MODIFY COLUMN id BIGINT NOT NULL;

-- 5.2 기존 PK 제거
ALTER TABLE organizations DROP PRIMARY KEY;

-- 5.3 새 PK 설정 (organization_id)
ALTER TABLE organizations ADD PRIMARY KEY (organization_id);

-- 5.4 기존 id 컬럼 삭제
ALTER TABLE organizations DROP COLUMN id;

-- 5.5 기존 UK 인덱스 제거 (이제 PK가 됨)
ALTER TABLE organizations DROP INDEX uk_organizations_organization_id;

-- =====================================================
-- 6. users 테이블 - UUID를 PK로 승격
-- =====================================================
-- 6.1 AUTO_INCREMENT 제거
ALTER TABLE users MODIFY COLUMN id BIGINT NOT NULL;

-- 6.2 기존 PK 제거
ALTER TABLE users DROP PRIMARY KEY;

-- 6.3 새 PK 설정 (user_id)
ALTER TABLE users ADD PRIMARY KEY (user_id);

-- 6.4 기존 id 컬럼 삭제
ALTER TABLE users DROP COLUMN id;

-- 6.5 기존 UK 인덱스 제거 (이제 PK가 됨)
ALTER TABLE users DROP INDEX uk_users_user_id;

-- =====================================================
-- 7. roles 테이블 - UUID를 PK로 승격
-- =====================================================
-- 7.1 AUTO_INCREMENT 제거
ALTER TABLE roles MODIFY COLUMN id BIGINT NOT NULL;

-- 7.2 기존 PK 제거
ALTER TABLE roles DROP PRIMARY KEY;

-- 7.3 새 PK 설정 (role_id)
ALTER TABLE roles ADD PRIMARY KEY (role_id);

-- 7.4 기존 id 컬럼 삭제
ALTER TABLE roles DROP COLUMN id;

-- 7.5 기존 UK 인덱스 제거 (이제 PK가 됨)
ALTER TABLE roles DROP INDEX uk_roles_role_id;

-- =====================================================
-- 8. permissions 테이블 - UUID를 PK로 승격
-- =====================================================
-- 8.1 AUTO_INCREMENT 제거
ALTER TABLE permissions MODIFY COLUMN id BIGINT NOT NULL;

-- 8.2 기존 PK 제거
ALTER TABLE permissions DROP PRIMARY KEY;

-- 8.3 새 PK 설정 (permission_id)
ALTER TABLE permissions ADD PRIMARY KEY (permission_id);

-- 8.4 기존 id 컬럼 삭제
ALTER TABLE permissions DROP COLUMN id;

-- 8.5 기존 UK 인덱스 제거 (이제 PK가 됨)
ALTER TABLE permissions DROP INDEX uk_permissions_permission_id;

-- =====================================================
-- 9. endpoint_permissions 테이블 - UUID를 PK로 승격
-- =====================================================
-- 9.1 AUTO_INCREMENT 제거
ALTER TABLE endpoint_permissions MODIFY COLUMN id BIGINT NOT NULL;

-- 9.2 기존 PK 제거
ALTER TABLE endpoint_permissions DROP PRIMARY KEY;

-- 9.3 새 PK 설정 (endpoint_permission_id)
ALTER TABLE endpoint_permissions ADD PRIMARY KEY (endpoint_permission_id);

-- 9.4 기존 id 컬럼 삭제
ALTER TABLE endpoint_permissions DROP COLUMN id;

-- 9.5 기존 UK 인덱스 제거 (이제 PK가 됨)
ALTER TABLE endpoint_permissions DROP INDEX uk_endpoint_permissions_id;

-- =====================================================
-- 10. permission_usages 테이블 - UUID를 PK로 승격
-- =====================================================
-- 10.1 AUTO_INCREMENT 제거
ALTER TABLE permission_usages MODIFY COLUMN id BIGINT NOT NULL;

-- 10.2 기존 PK 제거
ALTER TABLE permission_usages DROP PRIMARY KEY;

-- 10.3 새 PK 설정 (usage_id)
ALTER TABLE permission_usages ADD PRIMARY KEY (usage_id);

-- 10.4 기존 id 컬럼 삭제
ALTER TABLE permission_usages DROP COLUMN id;

-- 10.5 기존 UK 제약 조건 제거 (이제 PK가 됨)
ALTER TABLE permission_usages DROP INDEX uk_permission_usages_uuid;
