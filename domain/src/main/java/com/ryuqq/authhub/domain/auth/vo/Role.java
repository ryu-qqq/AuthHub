package com.ryuqq.authhub.domain.auth.vo;

/**
 * Role - 시스템 역할 상수
 *
 * <p>시스템에서 사용되는 역할 이름 상수를 정의합니다.
 *
 * <p><strong>역할 계층:</strong>
 *
 * <ul>
 *   <li>SUPER_ADMIN: 시스템 전체 관리자
 *   <li>TENANT_ADMIN: 테넌트 관리자
 *   <li>ORG_ADMIN: 조직 관리자
 *   <li>MEMBER: 일반 사용자
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public final class Role {

    /** 시스템 전체 관리자 */
    public static final String SUPER_ADMIN = "ROLE_SUPER_ADMIN";

    /** 테넌트 관리자 */
    public static final String TENANT_ADMIN = "ROLE_TENANT_ADMIN";

    /** 조직 관리자 */
    public static final String ORG_ADMIN = "ROLE_ORG_ADMIN";

    /** 일반 사용자 */
    public static final String MEMBER = "ROLE_MEMBER";

    private Role() {
        // Utility class - prevent instantiation
    }
}
