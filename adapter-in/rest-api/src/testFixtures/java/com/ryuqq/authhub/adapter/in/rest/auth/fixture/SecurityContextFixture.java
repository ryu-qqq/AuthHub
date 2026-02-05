package com.ryuqq.authhub.adapter.in.rest.auth.fixture;

import com.ryuqq.authhub.adapter.in.rest.auth.component.SecurityContext;
import java.util.Set;

/**
 * SecurityContext 테스트용 Fixture
 *
 * @author development-team
 * @since 1.0.0
 */
public final class SecurityContextFixture {

    private static final String DEFAULT_USER_ID = "550e8400-e29b-41d4-a716-446655440001";
    private static final String DEFAULT_TENANT_ID = "550e8400-e29b-41d4-a716-446655440000";
    private static final String DEFAULT_ORG_ID = "550e8400-e29b-41d4-a716-446655440002";

    private SecurityContextFixture() {}

    /** SUPER_ADMIN 역할 컨텍스트 */
    public static SecurityContext superAdminContext() {
        return SecurityContext.builder()
                .userId(DEFAULT_USER_ID)
                .tenantId(DEFAULT_TENANT_ID)
                .organizationId(DEFAULT_ORG_ID)
                .roles(Set.of(SecurityContext.ROLE_SUPER_ADMIN))
                .permissions(Set.of("*:*"))
                .traceId("trace-super-admin")
                .build();
    }

    /** TENANT_ADMIN 역할 컨텍스트 */
    public static SecurityContext tenantAdminContext() {
        return SecurityContext.builder()
                .userId(DEFAULT_USER_ID)
                .tenantId(DEFAULT_TENANT_ID)
                .organizationId(DEFAULT_ORG_ID)
                .roles(Set.of(SecurityContext.ROLE_TENANT_ADMIN))
                .permissions(
                        Set.of("tenant:read", "tenant:create", "tenant:update", "tenant:delete"))
                .traceId("trace-tenant-admin")
                .build();
    }

    /** ORG_ADMIN 역할 컨텍스트 */
    public static SecurityContext orgAdminContext() {
        return SecurityContext.builder()
                .userId(DEFAULT_USER_ID)
                .tenantId(DEFAULT_TENANT_ID)
                .organizationId(DEFAULT_ORG_ID)
                .roles(Set.of(SecurityContext.ROLE_ORG_ADMIN))
                .permissions(Set.of("organization:read", "organization:create", "user:read"))
                .traceId("trace-org-admin")
                .build();
    }

    /** MEMBER(일반 사용자) 역할 컨텍스트 */
    public static SecurityContext memberContext() {
        return SecurityContext.builder()
                .userId(DEFAULT_USER_ID)
                .tenantId(DEFAULT_TENANT_ID)
                .organizationId(DEFAULT_ORG_ID)
                .roles(Set.of(SecurityContext.ROLE_MEMBER))
                .permissions(Set.of("user:read"))
                .traceId("trace-member")
                .build();
    }

    /** 지정된 권한을 가진 컨텍스트 */
    public static SecurityContext contextWithPermissions(String... permissions) {
        return SecurityContext.builder()
                .userId(DEFAULT_USER_ID)
                .tenantId(DEFAULT_TENANT_ID)
                .organizationId(DEFAULT_ORG_ID)
                .roles(Set.of(SecurityContext.ROLE_MEMBER))
                .permissions(Set.of(permissions))
                .traceId("trace-permissions")
                .build();
    }

    /** 지정된 역할을 가진 컨텍스트 */
    public static SecurityContext contextWithRoles(String... roles) {
        return SecurityContext.builder()
                .userId(DEFAULT_USER_ID)
                .tenantId(DEFAULT_TENANT_ID)
                .organizationId(DEFAULT_ORG_ID)
                .roles(Set.of(roles))
                .permissions(Set.of())
                .traceId("trace-roles")
                .build();
    }

    /** 익명 컨텍스트 */
    public static SecurityContext anonymousContext() {
        return SecurityContext.anonymous();
    }
}
