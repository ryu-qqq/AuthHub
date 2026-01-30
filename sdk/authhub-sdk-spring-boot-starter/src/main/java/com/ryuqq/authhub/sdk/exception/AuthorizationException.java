package com.ryuqq.authhub.sdk.exception;

/**
 * AuthorizationException - 인가 실패 예외
 *
 * <p>권한이 부족하여 리소스 접근이 거부된 경우 발생하는 예외입니다. HTTP 403 Forbidden에 매핑됩니다.
 *
 * <p><strong>발생 상황:</strong>
 *
 * <ul>
 *   <li>필요한 권한이 없는 경우
 *   <li>다른 테넌트/조직의 리소스 접근 시도
 *   <li>자기 자신이 아닌 사용자 데이터 접근
 * </ul>
 *
 * <p><strong>사용 예시:</strong>
 *
 * <pre>{@code
 * if (!accessChecker.hasPermission("user:delete")) {
 *     throw AuthorizationException.insufficientPermission("user:delete");
 * }
 *
 * if (!accessChecker.sameTenant(targetTenantId)) {
 *     throw AuthorizationException.tenantAccessDenied(targetTenantId);
 * }
 * }</pre>
 *
 * @author development-team
 * @since 1.0.0
 */
public class AuthorizationException extends SecurityException {

    private final String requiredPermission;
    private final String targetResource;

    /**
     * 에러 코드로 예외 생성
     *
     * @param errorCode 에러 코드
     */
    public AuthorizationException(SecurityErrorCode errorCode) {
        super(errorCode);
        this.requiredPermission = null;
        this.targetResource = null;
    }

    /**
     * 에러 코드와 상세 메시지로 예외 생성
     *
     * @param errorCode 에러 코드
     * @param message 상세 메시지
     */
    public AuthorizationException(SecurityErrorCode errorCode, String message) {
        super(errorCode, message);
        this.requiredPermission = null;
        this.targetResource = null;
    }

    /**
     * 에러 코드, 필요 권한, 대상 리소스로 예외 생성
     *
     * @param errorCode 에러 코드
     * @param requiredPermission 필요한 권한
     * @param targetResource 접근 시도한 리소스
     */
    public AuthorizationException(
            SecurityErrorCode errorCode, String requiredPermission, String targetResource) {
        super(errorCode, buildMessage(errorCode, requiredPermission, targetResource));
        this.requiredPermission = requiredPermission;
        this.targetResource = targetResource;
    }

    /**
     * 에러 코드와 원인 예외로 예외 생성
     *
     * @param errorCode 에러 코드
     * @param cause 원인 예외
     */
    public AuthorizationException(SecurityErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
        this.requiredPermission = null;
        this.targetResource = null;
    }

    /**
     * 필요한 권한 반환
     *
     * @return 필요한 권한 (설정되지 않은 경우 null)
     */
    public String getRequiredPermission() {
        return requiredPermission;
    }

    /**
     * 접근 시도한 대상 리소스 반환
     *
     * @return 대상 리소스 (설정되지 않은 경우 null)
     */
    public String getTargetResource() {
        return targetResource;
    }

    /**
     * 기본 접근 거부 예외 생성 (ACCESS_DENIED)
     *
     * @return AuthorizationException
     */
    public static AuthorizationException accessDenied() {
        return new AuthorizationException(SecurityErrorCode.ACCESS_DENIED);
    }

    /**
     * 접근 거부 예외 생성 (메시지 포함)
     *
     * @param message 상세 메시지
     * @return AuthorizationException
     */
    public static AuthorizationException accessDenied(String message) {
        return new AuthorizationException(SecurityErrorCode.ACCESS_DENIED, message);
    }

    /**
     * 권한 부족 예외 생성
     *
     * @param requiredPermission 필요한 권한
     * @return AuthorizationException
     */
    public static AuthorizationException insufficientPermission(String requiredPermission) {
        return new AuthorizationException(
                SecurityErrorCode.INSUFFICIENT_PERMISSION, requiredPermission, null);
    }

    /**
     * 리소스 접근 금지 예외 생성
     *
     * @param resourceId 리소스 ID
     * @return AuthorizationException
     */
    public static AuthorizationException resourceForbidden(String resourceId) {
        return new AuthorizationException(SecurityErrorCode.RESOURCE_FORBIDDEN, null, resourceId);
    }

    /**
     * 테넌트 접근 거부 예외 생성
     *
     * @param tenantId 테넌트 ID
     * @return AuthorizationException
     */
    public static AuthorizationException tenantAccessDenied(String tenantId) {
        return new AuthorizationException(
                SecurityErrorCode.TENANT_ACCESS_DENIED, "Tenant access denied: " + tenantId);
    }

    /**
     * 조직 접근 거부 예외 생성
     *
     * @param organizationId 조직 ID
     * @return AuthorizationException
     */
    public static AuthorizationException organizationAccessDenied(String organizationId) {
        return new AuthorizationException(
                SecurityErrorCode.ORGANIZATION_ACCESS_DENIED,
                "Organization access denied: " + organizationId);
    }

    private static String buildMessage(
            SecurityErrorCode errorCode, String permission, String resource) {
        StringBuilder sb = new StringBuilder(errorCode.getDefaultMessage());
        if (permission != null) {
            sb.append(" - Required: ").append(permission);
        }
        if (resource != null) {
            sb.append(" - Resource: ").append(resource);
        }
        return sb.toString();
    }
}
