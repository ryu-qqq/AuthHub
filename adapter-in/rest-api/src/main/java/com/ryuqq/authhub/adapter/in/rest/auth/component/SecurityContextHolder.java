package com.ryuqq.authhub.adapter.in.rest.auth.component;

import java.util.Objects;

/**
 * SecurityContext를 ThreadLocal로 관리
 *
 * <p>요청 스레드 내에서 SecurityContext에 접근할 수 있게 합니다. Filter에서 설정하고, 요청 완료 시 반드시 clear() 호출이 필요합니다.
 *
 * <p>사용 예시:
 *
 * <pre>{@code
 * // Filter에서 설정
 * SecurityContextHolder.setContext(context);
 *
 * // Service/Adapter에서 조회
 * Long tenantId = SecurityContextHolder.getCurrentTenantId();
 *
 * // Filter finally 블록에서 정리
 * SecurityContextHolder.clearContext();
 * }</pre>
 *
 * @author development-team
 * @since 1.0.0
 */
public final class SecurityContextHolder {

    private static final ThreadLocal<SecurityContext> CONTEXT_HOLDER = new ThreadLocal<>();

    private SecurityContextHolder() {}

    /**
     * 현재 스레드의 SecurityContext 반환
     *
     * <p>컨텍스트가 설정되지 않은 경우 Anonymous 컨텍스트를 반환합니다.
     *
     * @return SecurityContext (null이 아님)
     */
    public static SecurityContext getContext() {
        SecurityContext context = CONTEXT_HOLDER.get();
        return context != null ? context : SecurityContext.anonymous();
    }

    /**
     * 현재 스레드에 SecurityContext 설정
     *
     * @param context 설정할 SecurityContext (null 불가)
     * @throws NullPointerException context가 null인 경우
     */
    public static void setContext(SecurityContext context) {
        Objects.requireNonNull(context, "SecurityContext must not be null");
        CONTEXT_HOLDER.set(context);
    }

    /**
     * 현재 스레드의 SecurityContext 정리
     *
     * <p>메모리 누수 방지를 위해 요청 완료 시 반드시 호출해야 합니다.
     */
    public static void clearContext() {
        CONTEXT_HOLDER.remove();
    }

    /**
     * 현재 사용자 ID 반환 (UUID 문자열 형식)
     *
     * @return 사용자 ID (익명이면 null)
     */
    public static String getCurrentUserId() {
        return getContext().getUserId();
    }

    /**
     * 현재 테넌트 ID 반환 (UUIDv7 문자열 형식)
     *
     * @return 테넌트 ID (없으면 null)
     */
    public static String getCurrentTenantId() {
        return getContext().getTenantId();
    }

    /**
     * 현재 조직 ID 반환 (UUIDv7 문자열 형식)
     *
     * @return 조직 ID (없으면 null)
     */
    public static String getCurrentOrganizationId() {
        return getContext().getOrganizationId();
    }

    /**
     * 특정 역할 보유 여부 확인
     *
     * @param role 확인할 역할
     * @return 역할 보유 시 true
     */
    public static boolean hasRole(String role) {
        return getContext().hasRole(role);
    }

    /**
     * 주어진 역할 중 하나라도 보유 여부 확인
     *
     * @param roles 확인할 역할들
     * @return 하나라도 보유 시 true
     */
    public static boolean hasAnyRole(String... roles) {
        return getContext().hasAnyRole(roles);
    }

    /**
     * SUPER_ADMIN 역할 보유 여부
     *
     * @return SUPER_ADMIN 보유 시 true
     */
    public static boolean isSuperAdmin() {
        return getContext().isSuperAdmin();
    }

    /**
     * 인증 여부 확인
     *
     * @return 인증된 요청이면 true
     */
    public static boolean isAuthenticated() {
        return getContext().isAuthenticated();
    }

    /**
     * 추적 ID 반환
     *
     * @return 추적 ID (없으면 null)
     */
    public static String getTraceId() {
        return getContext().getTraceId();
    }

    /**
     * 특정 권한 보유 여부 확인
     *
     * <p>권한 형식: {resource}:{action} (예: user:read, order:write)
     *
     * @param permission 확인할 권한
     * @return 권한 보유 시 true
     */
    public static boolean hasPermission(String permission) {
        return getContext().hasPermission(permission);
    }

    /**
     * 주어진 권한 중 하나라도 보유 여부 확인
     *
     * @param permissions 확인할 권한들
     * @return 하나라도 보유 시 true
     */
    public static boolean hasAnyPermission(String... permissions) {
        return getContext().hasAnyPermission(permissions);
    }

    /**
     * 주어진 권한을 모두 보유 여부 확인
     *
     * @param permissions 확인할 권한들
     * @return 모두 보유 시 true
     */
    public static boolean hasAllPermissions(String... permissions) {
        return getContext().hasAllPermissions(permissions);
    }

    /**
     * 서비스 계정 여부 확인
     *
     * <p>서버간 통신(Service Token 인증)인 경우 true
     *
     * @return 서비스 계정이면 true
     */
    public static boolean isServiceAccount() {
        return getContext().isServiceAccount();
    }

    /**
     * 호출 서비스명 반환
     *
     * @return 호출 서비스명 (없으면 null)
     */
    public static String getRequestSource() {
        return getContext().getRequestSource();
    }

    /**
     * 분산 추적 ID 반환
     *
     * @return Correlation ID (없으면 null)
     */
    public static String getCorrelationId() {
        return getContext().getCorrelationId();
    }
}
