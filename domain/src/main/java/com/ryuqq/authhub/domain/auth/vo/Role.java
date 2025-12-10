package com.ryuqq.authhub.domain.auth.vo;

/**
 * 역할 상수 정의
 *
 * <p>시스템에서 사용되는 역할 상수를 정의합니다. Spring Security에서 사용하는 ROLE_ 접두사를 포함합니다.
 *
 * <p>역할 계층:
 *
 * <pre>
 * SUPER_ADMIN: 모든 테넌트, 모든 조직 접근 가능
 *      ↓
 * TENANT_ADMIN: 자신의 테넌트 내 모든 조직 접근 가능
 *      ↓
 * ORG_ADMIN: 자신의 조직만 접근 가능
 *      ↓
 * USER: 자신의 데이터만 접근 가능
 * </pre>
 *
 * @author development-team
 * @since 1.0.0
 */
public final class Role {

    /** 슈퍼 관리자 - 모든 테넌트 및 조직 접근 가능 */
    public static final String SUPER_ADMIN = "ROLE_SUPER_ADMIN";

    /** 테넌트 관리자 - 자신의 테넌트 내 모든 조직 접근 가능 */
    public static final String TENANT_ADMIN = "ROLE_TENANT_ADMIN";

    /** 조직 관리자 - 자신의 조직만 접근 가능 */
    public static final String ORG_ADMIN = "ROLE_ORG_ADMIN";

    /** 일반 사용자 - 자신의 데이터만 접근 가능 */
    public static final String USER = "ROLE_USER";

    private Role() {}
}
