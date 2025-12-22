package com.ryuqq.auth.common.header;

/**
 * SecurityHeaders - 보안 관련 HTTP 헤더 상수
 *
 * <p>Gateway와 서비스 간 통신에서 사용되는 보안 헤더 이름을 정의합니다.
 *
 * <p><strong>헤더 카테고리:</strong>
 *
 * <ul>
 *   <li>사용자 컨텍스트: 인증된 사용자 정보
 *   <li>서비스 인증: 서버간 통신 인증
 *   <li>추적: 분산 추적 및 감사
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public final class SecurityHeaders {

    // ===== 사용자 컨텍스트 헤더 (Gateway → Backend) =====

    /** 사용자 ID (UUID) */
    public static final String USER_ID = "X-User-Id";

    /** 테넌트 ID (UUID) */
    public static final String TENANT_ID = "X-Tenant-Id";

    /** 조직 ID (UUID) */
    public static final String ORGANIZATION_ID = "X-Organization-Id";

    /** 사용자 역할 목록 (쉼표 구분) */
    public static final String ROLES = "X-User-Roles";

    /** 사용자 권한 목록 (쉼표 구분) */
    public static final String PERMISSIONS = "X-User-Permissions";

    /** 사용자 이메일 */
    public static final String USER_EMAIL = "X-User-Email";

    // ===== 서비스 인증 헤더 (Service → Service) =====

    /** 서비스 토큰 */
    public static final String SERVICE_TOKEN = "X-Service-Token";

    /** 호출 서비스 이름 */
    public static final String SERVICE_NAME = "X-Service-Name";

    // ===== 서비스간 통신 확장 헤더 =====

    /** 원본 사용자 ID (서비스 호출 시 원본 요청자) */
    public static final String ORIGINAL_USER_ID = "X-Original-User-Id";

    /** 원본 테넌트 ID (서비스 호출 시 원본 테넌트) */
    public static final String ORIGINAL_TENANT_ID = "X-Original-Tenant-Id";

    /** 원본 조직 ID (서비스 호출 시 원본 조직) */
    public static final String ORIGINAL_ORGANIZATION_ID = "X-Original-Organization-Id";

    // ===== 추적 헤더 =====

    /** 분산 추적 ID (OpenTelemetry 호환) */
    public static final String CORRELATION_ID = "X-Correlation-Id";

    /** 요청 출처 서비스 */
    public static final String REQUEST_SOURCE = "X-Request-Source";

    /** 요청 ID (요청별 고유 ID) */
    public static final String REQUEST_ID = "X-Request-Id";

    // ===== Gateway 내부 헤더 =====

    /** 인증 여부 플래그 */
    public static final String AUTHENTICATED = "X-Authenticated";

    /** 인증 방식 (JWT, ServiceToken 등) */
    public static final String AUTH_TYPE = "X-Auth-Type";

    private SecurityHeaders() {
        throw new AssertionError("Utility class - cannot instantiate");
    }

    /**
     * 헤더 이름이 보안 관련 헤더인지 확인
     *
     * @param headerName 헤더 이름
     * @return 보안 헤더이면 true
     */
    public static boolean isSecurityHeader(String headerName) {
        if (headerName == null) {
            return false;
        }
        return headerName.startsWith("X-User-")
                || headerName.startsWith("X-Service-")
                || headerName.startsWith("X-Original-")
                || CORRELATION_ID.equals(headerName)
                || REQUEST_SOURCE.equals(headerName)
                || REQUEST_ID.equals(headerName)
                || AUTHENTICATED.equals(headerName)
                || AUTH_TYPE.equals(headerName);
    }

    /**
     * 민감한 헤더인지 확인 (로깅 시 마스킹 필요)
     *
     * @param headerName 헤더 이름
     * @return 민감한 헤더이면 true
     */
    public static boolean isSensitiveHeader(String headerName) {
        return SERVICE_TOKEN.equals(headerName);
    }
}
