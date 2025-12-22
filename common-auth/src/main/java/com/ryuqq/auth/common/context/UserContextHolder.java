package com.ryuqq.auth.common.context;

/**
 * UserContextHolder - ThreadLocal 기반 사용자 컨텍스트 관리
 *
 * <p>요청 스레드별로 UserContext를 관리합니다. 필터에서 설정하고 요청 처리 완료 후 반드시 정리해야 합니다.
 *
 * <p><strong>사용 예시:</strong>
 *
 * <pre>{@code
 * // 필터에서 컨텍스트 설정
 * try {
 *     UserContext context = GatewayHeaderParser.parse(request);
 *     UserContextHolder.setContext(context);
 *     filterChain.doFilter(request, response);
 * } finally {
 *     UserContextHolder.clearContext();
 * }
 *
 * // Service/Controller에서 컨텍스트 조회
 * UserContext context = UserContextHolder.getContext();
 * String userId = context.getUserId();
 * }</pre>
 *
 * <p><strong>주의사항:</strong>
 *
 * <ul>
 *   <li>요청 처리 완료 후 반드시 {@link #clearContext()} 호출 (메모리 누수 방지)
 *   <li>비동기 작업 시 컨텍스트가 전파되지 않음 - 필요시 명시적 전달
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public final class UserContextHolder {

    private static final ThreadLocal<UserContext> CONTEXT_HOLDER = new ThreadLocal<>();

    /** Anonymous 컨텍스트 (미인증 상태) */
    private static final UserContext ANONYMOUS = UserContext.builder().build();

    private UserContextHolder() {
        throw new AssertionError("Utility class - cannot instantiate");
    }

    /**
     * 현재 스레드의 UserContext 조회
     *
     * @return UserContext (없으면 anonymous 컨텍스트)
     */
    public static UserContext getContext() {
        UserContext context = CONTEXT_HOLDER.get();
        return context != null ? context : ANONYMOUS;
    }

    /**
     * 현재 스레드의 UserContext 조회 (Optional)
     *
     * @return UserContext를 담은 Optional (없으면 empty)
     */
    public static java.util.Optional<UserContext> getContextOptional() {
        return java.util.Optional.ofNullable(CONTEXT_HOLDER.get());
    }

    /**
     * 현재 스레드에 UserContext 설정
     *
     * @param context 설정할 UserContext (null이면 무시)
     */
    public static void setContext(UserContext context) {
        if (context != null) {
            CONTEXT_HOLDER.set(context);
        }
    }

    /**
     * 현재 스레드의 UserContext 제거
     *
     * <p>요청 처리 완료 후 반드시 호출해야 합니다.
     */
    public static void clearContext() {
        CONTEXT_HOLDER.remove();
    }

    /**
     * 현재 사용자가 인증되었는지 확인
     *
     * @return 인증 여부
     */
    public static boolean isAuthenticated() {
        return getContext().isAuthenticated();
    }

    /**
     * 현재 사용자 ID 조회
     *
     * @return 사용자 ID (미인증 시 null)
     */
    public static String getCurrentUserId() {
        return getContext().getUserId();
    }

    /**
     * 현재 테넌트 ID 조회
     *
     * @return 테넌트 ID (없으면 null)
     */
    public static String getCurrentTenantId() {
        return getContext().getTenantId();
    }

    /**
     * 현재 조직 ID 조회
     *
     * @return 조직 ID (없으면 null)
     */
    public static String getCurrentOrganizationId() {
        return getContext().getOrganizationId();
    }

    /**
     * 현재 사용자가 서비스 계정인지 확인
     *
     * @return 서비스 계정 여부
     */
    public static boolean isServiceAccount() {
        return getContext().isServiceAccount();
    }

    /**
     * 특정 권한 보유 여부 확인
     *
     * @param permission 확인할 권한
     * @return 보유 여부
     */
    public static boolean hasPermission(String permission) {
        return getContext().hasPermission(permission);
    }

    /**
     * 특정 역할 보유 여부 확인
     *
     * @param role 확인할 역할
     * @return 보유 여부
     */
    public static boolean hasRole(String role) {
        return getContext().hasRole(role);
    }

    /**
     * 컨텍스트 실행 헬퍼 - 지정된 컨텍스트로 작업 실행 후 자동 정리
     *
     * @param context 사용할 UserContext
     * @param runnable 실행할 작업
     */
    public static void runWithContext(UserContext context, Runnable runnable) {
        UserContext previous = CONTEXT_HOLDER.get();
        try {
            setContext(context);
            runnable.run();
        } finally {
            if (previous != null) {
                setContext(previous);
            } else {
                clearContext();
            }
        }
    }

    /**
     * 컨텍스트 실행 헬퍼 - 지정된 컨텍스트로 작업 실행 후 결과 반환
     *
     * @param context 사용할 UserContext
     * @param supplier 실행할 작업
     * @param <T> 반환 타입
     * @return 작업 결과
     */
    public static <T> T callWithContext(
            UserContext context, java.util.function.Supplier<T> supplier) {
        UserContext previous = CONTEXT_HOLDER.get();
        try {
            setContext(context);
            return supplier.get();
        } finally {
            if (previous != null) {
                setContext(previous);
            } else {
                clearContext();
            }
        }
    }
}
