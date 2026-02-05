package com.ryuqq.authhub.adapter.in.rest.auth.component;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * ResourceAccessChecker 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("ResourceAccessChecker 단위 테스트")
class ResourceAccessCheckerTest {

    private final ResourceAccessChecker checker = new ResourceAccessChecker();

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Nested
    @DisplayName("SUPER_ADMIN 컨텍스트에서는")
    class SuperAdminContext {

        @Test
        @DisplayName("superAdmin()이 true를 반환한다")
        void shouldReturnTrueForSuperAdmin() {
            SecurityContextHolder.setContext(
                    SecurityContext.builder()
                            .userId("admin")
                            .roles(Set.of(SecurityContext.ROLE_SUPER_ADMIN))
                            .build());

            assertThat(checker.superAdmin()).isTrue();
        }

        @Test
        @DisplayName("hasPermission()이 모든 권한에서 true를 반환한다")
        void shouldReturnTrueForAnyPermission() {
            SecurityContextHolder.setContext(
                    SecurityContext.builder()
                            .userId("admin")
                            .roles(Set.of(SecurityContext.ROLE_SUPER_ADMIN))
                            .build());

            assertThat(checker.hasPermission("user:read")).isTrue();
            assertThat(checker.hasPermission("tenant:delete")).isTrue();
        }

        @Test
        @DisplayName("hasAnyPermission()이 true를 반환한다")
        void shouldReturnTrueForHasAnyPermission() {
            SecurityContextHolder.setContext(
                    SecurityContext.builder()
                            .userId("admin")
                            .roles(Set.of(SecurityContext.ROLE_SUPER_ADMIN))
                            .build());

            assertThat(checker.hasAnyPermission("user:read", "order:write")).isTrue();
        }

        @Test
        @DisplayName("hasAllPermissions()이 true를 반환한다")
        void shouldReturnTrueForHasAllPermissions() {
            SecurityContextHolder.setContext(
                    SecurityContext.builder()
                            .userId("admin")
                            .roles(Set.of(SecurityContext.ROLE_SUPER_ADMIN))
                            .build());

            assertThat(checker.hasAllPermissions("user:read", "user:write")).isTrue();
        }

        @Test
        @DisplayName("sameTenant()이 모든 tenantId에서 true를 반환한다")
        void shouldReturnTrueForSameTenant() {
            SecurityContextHolder.setContext(
                    SecurityContext.builder()
                            .userId("admin")
                            .roles(Set.of(SecurityContext.ROLE_SUPER_ADMIN))
                            .build());

            assertThat(checker.sameTenant("any-tenant")).isTrue();
        }

        @Test
        @DisplayName("sameOrganization()이 모든 organizationId에서 true를 반환한다")
        void shouldReturnTrueForSameOrganization() {
            SecurityContextHolder.setContext(
                    SecurityContext.builder()
                            .userId("admin")
                            .roles(Set.of(SecurityContext.ROLE_SUPER_ADMIN))
                            .build());

            assertThat(checker.sameOrganization("any-org")).isTrue();
        }

        @Test
        @DisplayName("tenant()이 모든 tenantId/action에서 true를 반환한다")
        void shouldReturnTrueForTenant() {
            SecurityContextHolder.setContext(
                    SecurityContext.builder()
                            .userId("admin")
                            .roles(Set.of(SecurityContext.ROLE_SUPER_ADMIN))
                            .build());

            assertThat(checker.tenant("tenant-1", "delete")).isTrue();
        }

        @Test
        @DisplayName("organization()이 모든 organizationId/action에서 true를 반환한다")
        void shouldReturnTrueForOrganization() {
            SecurityContextHolder.setContext(
                    SecurityContext.builder()
                            .userId("admin")
                            .roles(Set.of(SecurityContext.ROLE_SUPER_ADMIN))
                            .build());

            assertThat(checker.organization("org-1", "delete")).isTrue();
        }

        @Test
        @DisplayName("user()이 모든 userId/action에서 true를 반환한다")
        void shouldReturnTrueForUser() {
            SecurityContextHolder.setContext(
                    SecurityContext.builder()
                            .userId("admin")
                            .roles(Set.of(SecurityContext.ROLE_SUPER_ADMIN))
                            .build());

            assertThat(checker.user("other-user", "delete")).isTrue();
        }

        @Test
        @DisplayName("role()이 true를 반환한다")
        void shouldReturnTrueForRole() {
            SecurityContextHolder.setContext(
                    SecurityContext.builder()
                            .userId("admin")
                            .roles(Set.of(SecurityContext.ROLE_SUPER_ADMIN))
                            .build());

            assertThat(checker.role("role-1", "delete")).isTrue();
        }

        @Test
        @DisplayName("permission()이 true를 반환한다")
        void shouldReturnTrueForPermission() {
            SecurityContextHolder.setContext(
                    SecurityContext.builder()
                            .userId("admin")
                            .roles(Set.of(SecurityContext.ROLE_SUPER_ADMIN))
                            .build());

            assertThat(checker.permission("perm-1", "delete")).isTrue();
        }
    }

    @Nested
    @DisplayName("역할별 접근 제어")
    class RoleBasedAccess {

        @Test
        @DisplayName("tenantAdmin()이 TENANT_ADMIN 역할 시 true를 반환한다")
        void shouldReturnTrueForTenantAdmin() {
            SecurityContextHolder.setContext(
                    SecurityContext.builder()
                            .userId("user-1")
                            .roles(Set.of(SecurityContext.ROLE_TENANT_ADMIN))
                            .build());

            assertThat(checker.tenantAdmin()).isTrue();
        }

        @Test
        @DisplayName("orgAdmin()이 ORG_ADMIN 역할 시 true를 반환한다")
        void shouldReturnTrueForOrgAdmin() {
            SecurityContextHolder.setContext(
                    SecurityContext.builder()
                            .userId("user-1")
                            .roles(Set.of(SecurityContext.ROLE_ORG_ADMIN))
                            .build());

            assertThat(checker.orgAdmin()).isTrue();
        }

        @Test
        @DisplayName("hasRole()이 역할 보유 시 true를 반환한다")
        void shouldReturnTrueWhenHasRole() {
            SecurityContextHolder.setContext(
                    SecurityContext.builder().userId("user-1").roles(Set.of("ROLE_ADMIN")).build());

            assertThat(checker.hasRole("ROLE_ADMIN")).isTrue();
        }

        @Test
        @DisplayName("hasAnyRole()이 하나라도 보유 시 true를 반환한다")
        void shouldReturnTrueWhenHasAnyRole() {
            SecurityContextHolder.setContext(
                    SecurityContext.builder().userId("user-1").roles(Set.of("ROLE_USER")).build());

            assertThat(checker.hasAnyRole("ROLE_ADMIN", "ROLE_USER")).isTrue();
        }
    }

    @Nested
    @DisplayName("권한 기반 접근 제어")
    class PermissionBasedAccess {

        @Test
        @DisplayName("hasPermission()이 권한 보유 시 true를 반환한다")
        void shouldReturnTrueWhenHasPermission() {
            SecurityContextHolder.setContext(
                    SecurityContext.builder()
                            .userId("user-1")
                            .permissions(Set.of("user:read"))
                            .build());

            assertThat(checker.hasPermission("user:read")).isTrue();
        }

        @Test
        @DisplayName("hasAnyPermission()이 하나라도 보유 시 true를 반환한다")
        void shouldReturnTrueWhenHasAnyPermission() {
            SecurityContextHolder.setContext(
                    SecurityContext.builder()
                            .userId("user-1")
                            .permissions(Set.of("user:read"))
                            .build());

            assertThat(checker.hasAnyPermission("user:write", "user:read")).isTrue();
        }

        @Test
        @DisplayName("hasAllPermissions()이 모두 보유 시 true를 반환한다")
        void shouldReturnTrueWhenHasAllPermissions() {
            SecurityContextHolder.setContext(
                    SecurityContext.builder()
                            .userId("user-1")
                            .permissions(Set.of("user:read", "user:write"))
                            .build());

            assertThat(checker.hasAllPermissions("user:read", "user:write")).isTrue();
        }

        @Test
        @DisplayName("hasAllPermissions()이 일부 미보유 시 false를 반환한다")
        void shouldReturnFalseWhenMissingAnyPermission() {
            SecurityContextHolder.setContext(
                    SecurityContext.builder()
                            .userId("user-1")
                            .permissions(Set.of("user:read"))
                            .build());

            assertThat(checker.hasAllPermissions("user:read", "user:write")).isFalse();
        }
    }

    @Nested
    @DisplayName("리소스 격리")
    class ResourceIsolation {

        @Test
        @DisplayName("myself()이 본인 userId 시 true를 반환한다")
        void shouldReturnTrueForMyself() {
            SecurityContextHolder.setContext(SecurityContext.builder().userId("user-123").build());

            assertThat(checker.myself("user-123")).isTrue();
        }

        @Test
        @DisplayName("myself()이 다른 userId 시 false를 반환한다")
        void shouldReturnFalseForOthers() {
            SecurityContextHolder.setContext(SecurityContext.builder().userId("user-123").build());

            assertThat(checker.myself("user-456")).isFalse();
        }

        @Test
        @DisplayName("myselfOr()이 본인이면 true를 반환한다")
        void shouldReturnTrueForMyselfOrWhenSelf() {
            SecurityContextHolder.setContext(SecurityContext.builder().userId("user-123").build());

            assertThat(checker.myselfOr("user-123", "user:read")).isTrue();
        }

        @Test
        @DisplayName("myselfOr()이 권한 보유 시 true를 반환한다")
        void shouldReturnTrueForMyselfOrWhenHasPermission() {
            SecurityContextHolder.setContext(
                    SecurityContext.builder()
                            .userId("user-123")
                            .permissions(Set.of("user:read"))
                            .build());

            assertThat(checker.myselfOr("user-456", "user:read")).isTrue();
        }

        @Test
        @DisplayName("sameTenant()이 동일 tenantId 시 true를 반환한다")
        void shouldReturnTrueForSameTenant() {
            SecurityContextHolder.setContext(
                    SecurityContext.builder().userId("user-1").tenantId("tenant-456").build());

            assertThat(checker.sameTenant("tenant-456")).isTrue();
        }

        @Test
        @DisplayName("sameTenant()이 다른 tenantId 시 false를 반환한다")
        void shouldReturnFalseForDifferentTenant() {
            SecurityContextHolder.setContext(
                    SecurityContext.builder().userId("user-1").tenantId("tenant-456").build());

            assertThat(checker.sameTenant("tenant-789")).isFalse();
        }

        @Test
        @DisplayName("sameOrganization()이 동일 organizationId 시 true를 반환한다")
        void shouldReturnTrueForSameOrganization() {
            SecurityContextHolder.setContext(
                    SecurityContext.builder()
                            .userId("user-1")
                            .tenantId("tenant-1")
                            .organizationId("org-456")
                            .build());

            assertThat(checker.sameOrganization("org-456")).isTrue();
        }

        @Test
        @DisplayName("tenant()이 동일 테넌트 + 권한 보유 시 true를 반환한다")
        void shouldReturnTrueForTenantWithPermission() {
            SecurityContextHolder.setContext(
                    SecurityContext.builder()
                            .userId("user-1")
                            .tenantId("tenant-456")
                            .permissions(Set.of("tenant:read"))
                            .build());

            assertThat(checker.tenant("tenant-456", "read")).isTrue();
        }

        @Test
        @DisplayName("tenant()이 다른 테넌트 시 false를 반환한다")
        void shouldReturnFalseForTenantDifferentTenant() {
            SecurityContextHolder.setContext(
                    SecurityContext.builder()
                            .userId("user-1")
                            .tenantId("tenant-456")
                            .permissions(Set.of("tenant:read"))
                            .build());

            assertThat(checker.tenant("tenant-789", "read")).isFalse();
        }

        @Test
        @DisplayName("user()이 본인 + read 액션 시 true를 반환한다")
        void shouldReturnTrueForUserSelfRead() {
            SecurityContextHolder.setContext(SecurityContext.builder().userId("user-123").build());

            assertThat(checker.user("user-123", "read")).isTrue();
        }

        @Test
        @DisplayName("user()이 본인 + update 액션 시 true를 반환한다")
        void shouldReturnTrueForUserSelfUpdate() {
            SecurityContextHolder.setContext(SecurityContext.builder().userId("user-123").build());

            assertThat(checker.user("user-123", "update")).isTrue();
        }

        @Test
        @DisplayName("user()이 본인 + delete 액션 시 false를 반환한다")
        void shouldReturnFalseForUserSelfDelete() {
            SecurityContextHolder.setContext(SecurityContext.builder().userId("user-123").build());

            assertThat(checker.user("user-123", "delete")).isFalse();
        }
    }

    @Nested
    @DisplayName("엣지 케이스")
    class EdgeCases {

        @Test
        @DisplayName("myself()이 userId null 시 false를 반환한다")
        void shouldReturnFalseWhenUserIdIsNull() {
            SecurityContextHolder.setContext(SecurityContext.anonymous());

            assertThat(checker.myself("user-123")).isFalse();
        }

        @Test
        @DisplayName("sameTenant()이 tenantId null 시 false를 반환한다")
        void shouldReturnFalseWhenTenantIdIsNull() {
            SecurityContextHolder.setContext(SecurityContext.builder().userId("user-1").build());

            assertThat(checker.sameTenant("tenant-456")).isFalse();
        }

        @Test
        @DisplayName("authenticated()이 인증된 사용자 시 true를 반환한다")
        void shouldReturnTrueWhenAuthenticated() {
            SecurityContextHolder.setContext(SecurityContext.builder().userId("user-1").build());

            assertThat(checker.authenticated()).isTrue();
        }

        @Test
        @DisplayName("authenticated()이 익명 사용자 시 false를 반환한다")
        void shouldReturnFalseWhenAnonymous() {
            SecurityContextHolder.setContext(SecurityContext.anonymous());

            assertThat(checker.authenticated()).isFalse();
        }

        @Test
        @DisplayName("permission()의 read 액션은 인증된 사용자면 true를 반환한다")
        void shouldReturnTrueForPermissionReadWhenAuthenticated() {
            SecurityContextHolder.setContext(
                    SecurityContext.builder().userId("user-1").permissions(Set.of()).build());

            assertThat(checker.permission("perm-1", "read")).isTrue();
        }

        @Test
        @DisplayName("permission()의 create 액션은 SUPER_ADMIN만 true를 반환한다")
        void shouldReturnFalseForPermissionCreateWhenNotSuperAdmin() {
            SecurityContextHolder.setContext(
                    SecurityContext.builder()
                            .userId("user-1")
                            .permissions(Set.of("permission:read"))
                            .build());

            assertThat(checker.permission("perm-1", "create")).isFalse();
        }
    }
}
