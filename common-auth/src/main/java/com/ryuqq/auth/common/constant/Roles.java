package com.ryuqq.auth.common.constant;

/**
 * Roles - 역할 상수 정의
 *
 * <p>시스템 전역에서 사용되는 역할 이름 상수입니다. Spring Security의 GrantedAuthority와 호환되는 형식으로 정의됩니다.
 *
 * <p><strong>역할 계층:</strong>
 *
 * <ul>
 *   <li>SUPER_ADMIN: 전역 관리자 (모든 테넌트/조직 접근)
 *   <li>TENANT_ADMIN: 테넌트 관리자 (해당 테넌트 내 모든 조직 접근)
 *   <li>ORG_ADMIN: 조직 관리자 (해당 조직만 접근)
 *   <li>USER: 일반 사용자
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public final class Roles {

    /** 전역 관리자 - 모든 테넌트/조직에 대한 완전한 접근 권한 */
    public static final String SUPER_ADMIN = "ROLE_SUPER_ADMIN";

    /** 테넌트 관리자 - 해당 테넌트 내 모든 조직에 대한 관리 권한 */
    public static final String TENANT_ADMIN = "ROLE_TENANT_ADMIN";

    /** 조직 관리자 - 해당 조직에 대한 관리 권한 */
    public static final String ORG_ADMIN = "ROLE_ORG_ADMIN";

    /** 일반 사용자 - 기본 권한 */
    public static final String USER = "ROLE_USER";

    /** 서비스 계정 - 서버간 통신용 */
    public static final String SERVICE = "ROLE_SERVICE";

    private Roles() {
        throw new AssertionError("Utility class - cannot instantiate");
    }

    /**
     * 역할 이름이 유효한 시스템 역할인지 확인
     *
     * @param role 역할 이름
     * @return 유효한 시스템 역할이면 true
     */
    public static boolean isSystemRole(String role) {
        return SUPER_ADMIN.equals(role)
                || TENANT_ADMIN.equals(role)
                || ORG_ADMIN.equals(role)
                || USER.equals(role)
                || SERVICE.equals(role);
    }

    /**
     * 관리자 역할인지 확인
     *
     * @param role 역할 이름
     * @return 관리자 역할이면 true
     */
    public static boolean isAdminRole(String role) {
        return SUPER_ADMIN.equals(role) || TENANT_ADMIN.equals(role) || ORG_ADMIN.equals(role);
    }
}
