-- V7__endpoint_permissions_table.sql
-- EndpointPermission 테이블 생성
-- 엔드포인트별 권한 관리를 위한 테이블

-- ===============================================
-- EndpointPermissions 테이블
-- ===============================================
-- serviceName + path + method 조합이 유니크
-- requiredPermissions, requiredRoles는 콤마(,) 구분 문자열로 저장
-- path에 와일드카드(**)나 Path Variable({id}) 포함 가능

CREATE TABLE endpoint_permissions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    endpoint_permission_id BINARY(16) NOT NULL,
    service_name VARCHAR(100) NOT NULL COMMENT '서비스 이름 (예: auth-hub, user-service)',
    path VARCHAR(500) NOT NULL COMMENT '엔드포인트 경로 (예: /api/v1/users/{userId})',
    method VARCHAR(10) NOT NULL COMMENT 'HTTP 메서드 (GET, POST, PUT, DELETE 등)',
    description VARCHAR(500) COMMENT '엔드포인트 설명',
    is_public BOOLEAN NOT NULL DEFAULT FALSE COMMENT '공개 여부 (true: 인증 불필요)',
    required_permissions VARCHAR(2000) COMMENT '필요 권한 목록 (콤마 구분, OR 조건)',
    required_roles VARCHAR(1000) COMMENT '필요 역할 목록 (콤마 구분, OR 조건)',
    version BIGINT NOT NULL DEFAULT 0 COMMENT '낙관적 락 버전',
    deleted BOOLEAN NOT NULL DEFAULT FALSE COMMENT '삭제 여부 (Soft Delete)',
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,

    -- 유니크 제약조건: 서비스명 + 경로 + 메서드
    UNIQUE INDEX uk_endpoint_permissions_service_path_method (service_name, path, method),
    -- 외부 식별자 유니크
    UNIQUE INDEX uk_endpoint_permissions_id (endpoint_permission_id),
    -- 검색 인덱스
    INDEX idx_endpoint_permissions_service_name (service_name),
    INDEX idx_endpoint_permissions_method (method),
    INDEX idx_endpoint_permissions_is_public (is_public),
    INDEX idx_endpoint_permissions_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ===============================================
-- 초기 데이터 삽입 (Public 엔드포인트)
-- ===============================================
-- 헬스체크 및 인증 관련 공개 엔드포인트

-- 헬스체크
INSERT INTO endpoint_permissions (
    endpoint_permission_id, service_name, path, method, description, is_public,
    required_permissions, required_roles, version, deleted, created_at, updated_at
) VALUES (
    UUID_TO_BIN(UUID()), 'auth-hub', '/api/v1/health', 'GET', '헬스체크 엔드포인트', TRUE,
    NULL, NULL, 0, FALSE, NOW(6), NOW(6)
);

-- 로그인
INSERT INTO endpoint_permissions (
    endpoint_permission_id, service_name, path, method, description, is_public,
    required_permissions, required_roles, version, deleted, created_at, updated_at
) VALUES (
    UUID_TO_BIN(UUID()), 'auth-hub', '/api/v1/auth/login', 'POST', '로그인 엔드포인트', TRUE,
    NULL, NULL, 0, FALSE, NOW(6), NOW(6)
);

-- 토큰 갱신
INSERT INTO endpoint_permissions (
    endpoint_permission_id, service_name, path, method, description, is_public,
    required_permissions, required_roles, version, deleted, created_at, updated_at
) VALUES (
    UUID_TO_BIN(UUID()), 'auth-hub', '/api/v1/auth/refresh', 'POST', '토큰 갱신 엔드포인트', TRUE,
    NULL, NULL, 0, FALSE, NOW(6), NOW(6)
);

-- ===============================================
-- 초기 데이터 삽입 (Protected 엔드포인트)
-- ===============================================
-- 사용자 관리 엔드포인트 (권한 필요)

-- 사용자 목록 조회
INSERT INTO endpoint_permissions (
    endpoint_permission_id, service_name, path, method, description, is_public,
    required_permissions, required_roles, version, deleted, created_at, updated_at
) VALUES (
    UUID_TO_BIN(UUID()), 'auth-hub', '/api/v1/users', 'GET', '사용자 목록 조회', FALSE,
    'user:read', 'ADMIN,USER_MANAGER', 0, FALSE, NOW(6), NOW(6)
);

-- 사용자 상세 조회
INSERT INTO endpoint_permissions (
    endpoint_permission_id, service_name, path, method, description, is_public,
    required_permissions, required_roles, version, deleted, created_at, updated_at
) VALUES (
    UUID_TO_BIN(UUID()), 'auth-hub', '/api/v1/users/{userId}', 'GET', '사용자 상세 조회', FALSE,
    'user:read', 'ADMIN,USER_MANAGER', 0, FALSE, NOW(6), NOW(6)
);

-- 사용자 생성
INSERT INTO endpoint_permissions (
    endpoint_permission_id, service_name, path, method, description, is_public,
    required_permissions, required_roles, version, deleted, created_at, updated_at
) VALUES (
    UUID_TO_BIN(UUID()), 'auth-hub', '/api/v1/users', 'POST', '사용자 생성', FALSE,
    'user:create', 'ADMIN,USER_MANAGER', 0, FALSE, NOW(6), NOW(6)
);

-- 사용자 수정
INSERT INTO endpoint_permissions (
    endpoint_permission_id, service_name, path, method, description, is_public,
    required_permissions, required_roles, version, deleted, created_at, updated_at
) VALUES (
    UUID_TO_BIN(UUID()), 'auth-hub', '/api/v1/users/{userId}', 'PUT', '사용자 수정', FALSE,
    'user:update', 'ADMIN,USER_MANAGER', 0, FALSE, NOW(6), NOW(6)
);

-- 사용자 삭제
INSERT INTO endpoint_permissions (
    endpoint_permission_id, service_name, path, method, description, is_public,
    required_permissions, required_roles, version, deleted, created_at, updated_at
) VALUES (
    UUID_TO_BIN(UUID()), 'auth-hub', '/api/v1/users/{userId}', 'DELETE', '사용자 삭제', FALSE,
    'user:delete', 'ADMIN', 0, FALSE, NOW(6), NOW(6)
);

-- 테넌트 관리 엔드포인트
INSERT INTO endpoint_permissions (
    endpoint_permission_id, service_name, path, method, description, is_public,
    required_permissions, required_roles, version, deleted, created_at, updated_at
) VALUES (
    UUID_TO_BIN(UUID()), 'auth-hub', '/api/v1/tenants', 'GET', '테넌트 목록 조회', FALSE,
    'tenant:read', 'ADMIN,TENANT_MANAGER', 0, FALSE, NOW(6), NOW(6)
);

INSERT INTO endpoint_permissions (
    endpoint_permission_id, service_name, path, method, description, is_public,
    required_permissions, required_roles, version, deleted, created_at, updated_at
) VALUES (
    UUID_TO_BIN(UUID()), 'auth-hub', '/api/v1/tenants/{tenantId}', 'GET', '테넌트 상세 조회', FALSE,
    'tenant:read', 'ADMIN,TENANT_MANAGER', 0, FALSE, NOW(6), NOW(6)
);

INSERT INTO endpoint_permissions (
    endpoint_permission_id, service_name, path, method, description, is_public,
    required_permissions, required_roles, version, deleted, created_at, updated_at
) VALUES (
    UUID_TO_BIN(UUID()), 'auth-hub', '/api/v1/tenants', 'POST', '테넌트 생성', FALSE,
    'tenant:create', 'ADMIN', 0, FALSE, NOW(6), NOW(6)
);
