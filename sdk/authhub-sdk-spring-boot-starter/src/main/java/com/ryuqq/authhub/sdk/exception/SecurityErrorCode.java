package com.ryuqq.authhub.sdk.exception;

/**
 * SecurityErrorCode - 보안 관련 에러 코드
 *
 * <p>인증/인가 실패 시 사용되는 표준 에러 코드입니다. RFC 7807 Problem Details와 함께 사용할 수 있습니다.
 *
 * <p><strong>코드 체계:</strong>
 *
 * <ul>
 *   <li>AUTH_001 ~ AUTH_099: 인증 관련 오류
 *   <li>AUTHZ_001 ~ AUTHZ_099: 인가 관련 오류
 *   <li>TOKEN_001 ~ TOKEN_099: 토큰 관련 오류
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public enum SecurityErrorCode {

    // ===== 인증 오류 (401) =====

    /** 인증되지 않은 요청 */
    UNAUTHENTICATED("AUTH_001", "Authentication required", 401),

    /** 잘못된 인증 정보 */
    INVALID_CREDENTIALS("AUTH_002", "Invalid credentials", 401),

    /** 계정 비활성화 */
    ACCOUNT_DISABLED("AUTH_003", "Account is disabled", 401),

    /** 계정 잠김 */
    ACCOUNT_LOCKED("AUTH_004", "Account is locked", 401),

    /** 인증 세션 만료 */
    SESSION_EXPIRED("AUTH_005", "Session has expired", 401),

    // ===== 인가 오류 (403) =====

    /** 접근 권한 없음 */
    ACCESS_DENIED("AUTHZ_001", "Access denied", 403),

    /** 권한 부족 */
    INSUFFICIENT_PERMISSION("AUTHZ_002", "Insufficient permission", 403),

    /** 리소스 접근 불가 */
    RESOURCE_FORBIDDEN("AUTHZ_003", "Resource access forbidden", 403),

    /** 테넌트 접근 불가 */
    TENANT_ACCESS_DENIED("AUTHZ_004", "Tenant access denied", 403),

    /** 조직 접근 불가 */
    ORGANIZATION_ACCESS_DENIED("AUTHZ_005", "Organization access denied", 403),

    // ===== 토큰 오류 =====

    /** 토큰 없음 */
    TOKEN_MISSING("TOKEN_001", "Token is missing", 401),

    /** 잘못된 토큰 형식 */
    TOKEN_MALFORMED("TOKEN_002", "Token is malformed", 401),

    /** 만료된 토큰 */
    TOKEN_EXPIRED("TOKEN_003", "Token has expired", 401),

    /** 유효하지 않은 토큰 */
    TOKEN_INVALID("TOKEN_004", "Token is invalid", 401),

    /** 서비스 토큰 오류 */
    SERVICE_TOKEN_INVALID("TOKEN_005", "Service token is invalid", 401);

    private final String code;
    private final String defaultMessage;
    private final int httpStatus;

    SecurityErrorCode(String code, String defaultMessage, int httpStatus) {
        this.code = code;
        this.defaultMessage = defaultMessage;
        this.httpStatus = httpStatus;
    }

    /**
     * 에러 코드 반환
     *
     * @return 에러 코드 (예: AUTH_001)
     */
    public String getCode() {
        return code;
    }

    /**
     * 기본 에러 메시지 반환
     *
     * @return 기본 메시지
     */
    public String getDefaultMessage() {
        return defaultMessage;
    }

    /**
     * HTTP 상태 코드 반환
     *
     * @return HTTP 상태 코드 (401 또는 403)
     */
    public int getHttpStatus() {
        return httpStatus;
    }

    /**
     * 인증 오류인지 확인 (401)
     *
     * @return 인증 오류면 true
     */
    public boolean isAuthenticationError() {
        return httpStatus == 401;
    }

    /**
     * 인가 오류인지 확인 (403)
     *
     * @return 인가 오류면 true
     */
    public boolean isAuthorizationError() {
        return httpStatus == 403;
    }
}
