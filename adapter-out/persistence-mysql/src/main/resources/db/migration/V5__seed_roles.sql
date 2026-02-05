-- =====================================================
-- AuthHub Database Schema V5 - Role 시드 데이터
-- GLOBAL: SUPER_ADMIN
-- SERVICE: 서비스별 ADMIN / EDITOR / VIEWER
-- =====================================================

-- 1. GLOBAL 시스템 역할
INSERT INTO roles (tenant_id, service_id, name, display_name, description, type, scope, created_at, updated_at)
VALUES
    (NULL, NULL, 'SUPER_ADMIN', '슈퍼 관리자', '전체 시스템 관리자. 모든 서비스와 테넌트에 대한 완전한 접근 권한', 'SYSTEM', 'GLOBAL', NOW(6), NOW(6));

-- 2. SVC_SETOF (service_id=1) - 자사몰
INSERT INTO roles (tenant_id, service_id, name, display_name, description, type, scope, created_at, updated_at)
VALUES
    (NULL, 1, 'ADMIN', '관리자', '자사몰 서비스 전체 관리. 설정, 사용자, 모든 데이터에 대한 완전한 접근 권한', 'SYSTEM', 'SERVICE', NOW(6), NOW(6)),
    (NULL, 1, 'EDITOR', '편집자', '자사몰 데이터 생성/수정/조회. 서비스 설정 및 사용자 관리 제외', 'SYSTEM', 'SERVICE', NOW(6), NOW(6)),
    (NULL, 1, 'VIEWER', '뷰어', '자사몰 데이터 조회만 가능. 생성/수정/삭제 불가', 'SYSTEM', 'SERVICE', NOW(6), NOW(6));

-- 3. SVC_MARKETPLACE (service_id=2) - 마켓플레이스
INSERT INTO roles (tenant_id, service_id, name, display_name, description, type, scope, created_at, updated_at)
VALUES
    (NULL, 2, 'ADMIN', '관리자', '마켓플레이스 서비스 전체 관리. 설정, 사용자, 모든 데이터에 대한 완전한 접근 권한', 'SYSTEM', 'SERVICE', NOW(6), NOW(6)),
    (NULL, 2, 'EDITOR', '편집자', '마켓플레이스 데이터 생성/수정/조회. 서비스 설정 및 사용자 관리 제외', 'SYSTEM', 'SERVICE', NOW(6), NOW(6)),
    (NULL, 2, 'VIEWER', '뷰어', '마켓플레이스 데이터 조회만 가능. 생성/수정/삭제 불가', 'SYSTEM', 'SERVICE', NOW(6), NOW(6));

-- 4. SVC_FILEFLOW (service_id=3) - 파일 관리
INSERT INTO roles (tenant_id, service_id, name, display_name, description, type, scope, created_at, updated_at)
VALUES
    (NULL, 3, 'ADMIN', '관리자', 'FileFlow 서비스 전체 관리. 설정, 사용자, 모든 데이터에 대한 완전한 접근 권한', 'SYSTEM', 'SERVICE', NOW(6), NOW(6)),
    (NULL, 3, 'EDITOR', '편집자', 'FileFlow 데이터 생성/수정/조회. 서비스 설정 및 사용자 관리 제외', 'SYSTEM', 'SERVICE', NOW(6), NOW(6)),
    (NULL, 3, 'VIEWER', '뷰어', 'FileFlow 데이터 조회만 가능. 생성/수정/삭제 불가', 'SYSTEM', 'SERVICE', NOW(6), NOW(6));

-- 5. SVC_CRAWLINGHUB (service_id=4) - 크롤링
INSERT INTO roles (tenant_id, service_id, name, display_name, description, type, scope, created_at, updated_at)
VALUES
    (NULL, 4, 'ADMIN', '관리자', 'CrawlingHub 서비스 전체 관리. 설정, 사용자, 모든 데이터에 대한 완전한 접근 권한', 'SYSTEM', 'SERVICE', NOW(6), NOW(6)),
    (NULL, 4, 'EDITOR', '편집자', 'CrawlingHub 데이터 생성/수정/조회. 서비스 설정 및 사용자 관리 제외', 'SYSTEM', 'SERVICE', NOW(6), NOW(6)),
    (NULL, 4, 'VIEWER', '뷰어', 'CrawlingHub 데이터 조회만 가능. 생성/수정/삭제 불가', 'SYSTEM', 'SERVICE', NOW(6), NOW(6));

-- 6. SVC_AUTHHUB (service_id=5) - 인증/인가
INSERT INTO roles (tenant_id, service_id, name, display_name, description, type, scope, created_at, updated_at)
VALUES
    (NULL, 5, 'ADMIN', '관리자', 'AuthHub 서비스 전체 관리. 설정, 사용자, 모든 데이터에 대한 완전한 접근 권한', 'SYSTEM', 'SERVICE', NOW(6), NOW(6)),
    (NULL, 5, 'EDITOR', '편집자', 'AuthHub 데이터 생성/수정/조회. 서비스 설정 및 사용자 관리 제외', 'SYSTEM', 'SERVICE', NOW(6), NOW(6)),
    (NULL, 5, 'VIEWER', '뷰어', 'AuthHub 데이터 조회만 가능. 생성/수정/삭제 불가', 'SYSTEM', 'SERVICE', NOW(6), NOW(6));

-- 7. SVC_CONVENTIONHUB (service_id=6) - 코딩 컨벤션
INSERT INTO roles (tenant_id, service_id, name, display_name, description, type, scope, created_at, updated_at)
VALUES
    (NULL, 6, 'ADMIN', '관리자', 'ConventionHub 서비스 전체 관리. 설정, 사용자, 모든 데이터에 대한 완전한 접근 권한', 'SYSTEM', 'SERVICE', NOW(6), NOW(6)),
    (NULL, 6, 'EDITOR', '편집자', 'ConventionHub 데이터 생성/수정/조회. 서비스 설정 및 사용자 관리 제외', 'SYSTEM', 'SERVICE', NOW(6), NOW(6)),
    (NULL, 6, 'VIEWER', '뷰어', 'ConventionHub 데이터 조회만 가능. 생성/수정/삭제 불가', 'SYSTEM', 'SERVICE', NOW(6), NOW(6));
