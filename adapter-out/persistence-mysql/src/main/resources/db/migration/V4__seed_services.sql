-- =====================================================
-- AuthHub Database Schema V4 - 서비스 시드 데이터
-- 초기 서비스 등록
-- =====================================================

INSERT INTO services (service_code, name, description, status, created_at, updated_at)
VALUES
    ('SVC_SETOF', '세토프', '자사몰 커머스 서비스', 'ACTIVE', NOW(6), NOW(6)),
    ('SVC_MARKETPLACE', '마켓플레이스', 'OMS 마켓플레이스 서비스', 'ACTIVE', NOW(6), NOW(6)),
    ('SVC_FILEFLOW', 'FileFlow', '파일 관리 서비스', 'ACTIVE', NOW(6), NOW(6)),
    ('SVC_CRAWLINGHUB', 'CrawlingHub', '크롤링 허브 서비스', 'ACTIVE', NOW(6), NOW(6)),
    ('SVC_AUTHHUB', 'AuthHub', '인증/인가 서비스', 'ACTIVE', NOW(6), NOW(6)),
    ('SVC_CONVENTIONHUB', 'ConventionHub', '코딩 컨벤션 관리 서비스', 'ACTIVE', NOW(6), NOW(6));
