package com.ryuqq.authhub.adapter.in.rest.auth.component;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.domain.auth.vo.Role;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * SecurityContext 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("SecurityContext 단위 테스트")
class SecurityContextTest {

    @Nested
    @DisplayName("Builder 테스트")
    class BuilderTest {

        @Test
        @DisplayName("모든 필드가 설정된 SecurityContext를 생성한다")
        void builder_withAllFields_shouldCreateContext() {
            // given
            String userId = "01912f58-7b9c-7d4e-8b4a-6c5d4e3f2a1b";
            String tenantId = "01912f58-7b9c-7d4e-8b4a-6c5d4e3f2a1c";
            String organizationId = "01912f58-7b9c-7d4e-8b4a-6c5d4e3f2a1d";
            Set<String> roles = Set.of(Role.TENANT_ADMIN, Role.USER);
            Set<String> permissions = Set.of("user:read", "order:write");
            String traceId = "trace-123";

            // when
            SecurityContext context =
                    SecurityContext.builder()
                            .userId(userId)
                            .tenantId(tenantId)
                            .organizationId(organizationId)
                            .roles(roles)
                            .permissions(permissions)
                            .traceId(traceId)
                            .build();

            // then
            assertThat(context.getUserId()).isEqualTo(userId);
            assertThat(context.getTenantId()).isEqualTo(tenantId);
            assertThat(context.getOrganizationId()).isEqualTo(organizationId);
            assertThat(context.getRoles()).containsExactlyInAnyOrderElementsOf(roles);
            assertThat(context.getPermissions()).containsExactlyInAnyOrderElementsOf(permissions);
            assertThat(context.getTraceId()).isEqualTo(traceId);
        }

        @Test
        @DisplayName("역할이 없는 SecurityContext를 생성한다")
        void builder_withoutRoles_shouldCreateContextWithEmptyRoles() {
            // given & when
            SecurityContext context =
                    SecurityContext.builder()
                            .userId("user-1")
                            .tenantId("01912f58-7b9c-7d4e-8b4a-6c5d4e3f2a1c")
                            .build();

            // then
            assertThat(context.getRoles()).isEmpty();
            assertThat(context.getPermissions()).isEmpty();
        }

        @Test
        @DisplayName("생성된 SecurityContext는 불변이다")
        void builder_shouldCreateImmutableContext() {
            // given
            Set<String> originalRoles = Set.of(Role.USER);
            Set<String> originalPermissions = Set.of("user:read");
            SecurityContext context =
                    SecurityContext.builder()
                            .userId("user-1")
                            .roles(originalRoles)
                            .permissions(originalPermissions)
                            .build();

            // when & then
            assertThat(context.getRoles()).isUnmodifiable();
            assertThat(context.getPermissions()).isUnmodifiable();
        }
    }

    @Nested
    @DisplayName("anonymous() 테스트")
    class AnonymousTest {

        @Test
        @DisplayName("익명 컨텍스트는 userId가 null이다")
        void anonymous_shouldHaveNullUserId() {
            // given & when
            SecurityContext anonymous = SecurityContext.anonymous();

            // then
            assertThat(anonymous.getUserId()).isNull();
        }

        @Test
        @DisplayName("익명 컨텍스트는 tenantId가 null이다")
        void anonymous_shouldHaveNullTenantId() {
            // given & when
            SecurityContext anonymous = SecurityContext.anonymous();

            // then
            assertThat(anonymous.getTenantId()).isNull();
        }

        @Test
        @DisplayName("익명 컨텍스트는 organizationId가 null이다")
        void anonymous_shouldHaveNullOrganizationId() {
            // given & when
            SecurityContext anonymous = SecurityContext.anonymous();

            // then
            assertThat(anonymous.getOrganizationId()).isNull();
        }

        @Test
        @DisplayName("익명 컨텍스트는 역할이 비어있다")
        void anonymous_shouldHaveEmptyRoles() {
            // given & when
            SecurityContext anonymous = SecurityContext.anonymous();

            // then
            assertThat(anonymous.getRoles()).isEmpty();
        }

        @Test
        @DisplayName("익명 컨텍스트는 인증되지 않은 상태이다")
        void anonymous_shouldNotBeAuthenticated() {
            // given & when
            SecurityContext anonymous = SecurityContext.anonymous();

            // then
            assertThat(anonymous.isAuthenticated()).isFalse();
        }
    }

    @Nested
    @DisplayName("isAuthenticated() 테스트")
    class IsAuthenticatedTest {

        @Test
        @DisplayName("userId가 있으면 인증된 상태이다")
        void isAuthenticated_withUserId_shouldReturnTrue() {
            // given
            SecurityContext context = SecurityContext.builder().userId("user-1").build();

            // when & then
            assertThat(context.isAuthenticated()).isTrue();
        }

        @Test
        @DisplayName("userId가 없으면 인증되지 않은 상태이다")
        void isAuthenticated_withoutUserId_shouldReturnFalse() {
            // given
            SecurityContext context =
                    SecurityContext.builder()
                            .tenantId("01912f58-7b9c-7d4e-8b4a-6c5d4e3f2a1c")
                            .build();

            // when & then
            assertThat(context.isAuthenticated()).isFalse();
        }
    }

    @Nested
    @DisplayName("hasRole() 테스트")
    class HasRoleTest {

        @Test
        @DisplayName("보유한 역할이면 true를 반환한다")
        void hasRole_withMatchingRole_shouldReturnTrue() {
            // given
            SecurityContext context =
                    SecurityContext.builder()
                            .userId("user-1")
                            .roles(Set.of(Role.TENANT_ADMIN))
                            .build();

            // when & then
            assertThat(context.hasRole(Role.TENANT_ADMIN)).isTrue();
        }

        @Test
        @DisplayName("보유하지 않은 역할이면 false를 반환한다")
        void hasRole_withNonMatchingRole_shouldReturnFalse() {
            // given
            SecurityContext context =
                    SecurityContext.builder().userId("user-1").roles(Set.of(Role.USER)).build();

            // when & then
            assertThat(context.hasRole(Role.SUPER_ADMIN)).isFalse();
        }
    }

    @Nested
    @DisplayName("hasAnyRole() 테스트")
    class HasAnyRoleTest {

        @Test
        @DisplayName("여러 역할 중 하나라도 보유하면 true를 반환한다")
        void hasAnyRole_withOneMatchingRole_shouldReturnTrue() {
            // given
            SecurityContext context =
                    SecurityContext.builder()
                            .userId("user-1")
                            .roles(Set.of(Role.TENANT_ADMIN))
                            .build();

            // when & then
            assertThat(context.hasAnyRole(Role.SUPER_ADMIN, Role.TENANT_ADMIN)).isTrue();
        }

        @Test
        @DisplayName("어떤 역할도 보유하지 않으면 false를 반환한다")
        void hasAnyRole_withNoMatchingRoles_shouldReturnFalse() {
            // given
            SecurityContext context =
                    SecurityContext.builder().userId("user-1").roles(Set.of(Role.USER)).build();

            // when & then
            assertThat(context.hasAnyRole(Role.SUPER_ADMIN, Role.TENANT_ADMIN)).isFalse();
        }
    }

    @Nested
    @DisplayName("역할 확인 헬퍼 메서드 테스트")
    class RoleHelperMethodsTest {

        @Test
        @DisplayName("SUPER_ADMIN 역할이면 isSuperAdmin()이 true를 반환한다")
        void isSuperAdmin_withSuperAdminRole_shouldReturnTrue() {
            // given
            SecurityContext context =
                    SecurityContext.builder()
                            .userId("user-1")
                            .roles(Set.of(Role.SUPER_ADMIN))
                            .build();

            // when & then
            assertThat(context.isSuperAdmin()).isTrue();
        }

        @Test
        @DisplayName("TENANT_ADMIN 역할이면 isTenantAdmin()이 true를 반환한다")
        void isTenantAdmin_withTenantAdminRole_shouldReturnTrue() {
            // given
            SecurityContext context =
                    SecurityContext.builder()
                            .userId("user-1")
                            .roles(Set.of(Role.TENANT_ADMIN))
                            .build();

            // when & then
            assertThat(context.isTenantAdmin()).isTrue();
        }

        @Test
        @DisplayName("ORG_ADMIN 역할이면 isOrgAdmin()이 true를 반환한다")
        void isOrgAdmin_withOrgAdminRole_shouldReturnTrue() {
            // given
            SecurityContext context =
                    SecurityContext.builder()
                            .userId("user-1")
                            .roles(Set.of(Role.ORG_ADMIN))
                            .build();

            // when & then
            assertThat(context.isOrgAdmin()).isTrue();
        }
    }

    @Nested
    @DisplayName("equals() / hashCode() 테스트")
    class EqualsHashCodeTest {

        @Test
        @DisplayName("같은 값을 가진 두 SecurityContext는 동등하다")
        void equals_withSameValues_shouldBeEqual() {
            // given
            SecurityContext context1 =
                    SecurityContext.builder()
                            .userId("user-1")
                            .tenantId("01912f58-7b9c-7d4e-8b4a-6c5d4e3f2a1c")
                            .organizationId("01912f58-7b9c-7d4e-8b4a-6c5d4e3f2a1d")
                            .roles(Set.of(Role.USER))
                            .permissions(Set.of("user:read"))
                            .traceId("trace-123")
                            .build();

            SecurityContext context2 =
                    SecurityContext.builder()
                            .userId("user-1")
                            .tenantId("01912f58-7b9c-7d4e-8b4a-6c5d4e3f2a1c")
                            .organizationId("01912f58-7b9c-7d4e-8b4a-6c5d4e3f2a1d")
                            .roles(Set.of(Role.USER))
                            .permissions(Set.of("user:read"))
                            .traceId("trace-123")
                            .build();

            // when & then
            assertThat(context1).isEqualTo(context2);
            assertThat(context1.hashCode()).isEqualTo(context2.hashCode());
        }

        @Test
        @DisplayName("다른 값을 가진 두 SecurityContext는 동등하지 않다")
        void equals_withDifferentValues_shouldNotBeEqual() {
            // given
            SecurityContext context1 = SecurityContext.builder().userId("user-1").build();

            SecurityContext context2 = SecurityContext.builder().userId("user-2").build();

            // when & then
            assertThat(context1).isNotEqualTo(context2);
        }
    }

    @Nested
    @DisplayName("hasPermission() 테스트")
    class HasPermissionTest {

        @Test
        @DisplayName("보유한 권한이면 true를 반환한다")
        void hasPermission_withMatchingPermission_shouldReturnTrue() {
            // given
            SecurityContext context =
                    SecurityContext.builder()
                            .userId("user-1")
                            .permissions(Set.of("user:read", "order:write"))
                            .build();

            // when & then
            assertThat(context.hasPermission("user:read")).isTrue();
            assertThat(context.hasPermission("order:write")).isTrue();
        }

        @Test
        @DisplayName("보유하지 않은 권한이면 false를 반환한다")
        void hasPermission_withNonMatchingPermission_shouldReturnFalse() {
            // given
            SecurityContext context =
                    SecurityContext.builder()
                            .userId("user-1")
                            .permissions(Set.of("user:read"))
                            .build();

            // when & then
            assertThat(context.hasPermission("user:write")).isFalse();
        }

        @Test
        @DisplayName("와일드카드 *:* 권한이 있으면 모든 권한을 보유한다")
        void hasPermission_withAllWildcard_shouldReturnTrueForAnyPermission() {
            // given
            SecurityContext context =
                    SecurityContext.builder().userId("user-1").permissions(Set.of("*:*")).build();

            // when & then
            assertThat(context.hasPermission("user:read")).isTrue();
            assertThat(context.hasPermission("order:write")).isTrue();
            assertThat(context.hasPermission("anything:any")).isTrue();
        }

        @Test
        @DisplayName("리소스 와일드카드 권한 (예: user:*)이 해당 리소스의 모든 액션을 포함한다")
        void hasPermission_withResourceWildcard_shouldReturnTrueForResourceActions() {
            // given
            SecurityContext context =
                    SecurityContext.builder()
                            .userId("user-1")
                            .permissions(Set.of("user:*"))
                            .build();

            // when & then
            assertThat(context.hasPermission("user:read")).isTrue();
            assertThat(context.hasPermission("user:write")).isTrue();
            assertThat(context.hasPermission("user:delete")).isTrue();
            assertThat(context.hasPermission("order:read")).isFalse();
        }
    }

    @Nested
    @DisplayName("hasAnyPermission() 테스트")
    class HasAnyPermissionTest {

        @Test
        @DisplayName("여러 권한 중 하나라도 보유하면 true를 반환한다")
        void hasAnyPermission_withOneMatchingPermission_shouldReturnTrue() {
            // given
            SecurityContext context =
                    SecurityContext.builder()
                            .userId("user-1")
                            .permissions(Set.of("user:read"))
                            .build();

            // when & then
            assertThat(context.hasAnyPermission("user:read", "user:write")).isTrue();
        }

        @Test
        @DisplayName("어떤 권한도 보유하지 않으면 false를 반환한다")
        void hasAnyPermission_withNoMatchingPermissions_shouldReturnFalse() {
            // given
            SecurityContext context =
                    SecurityContext.builder()
                            .userId("user-1")
                            .permissions(Set.of("user:read"))
                            .build();

            // when & then
            assertThat(context.hasAnyPermission("order:read", "order:write")).isFalse();
        }
    }

    @Nested
    @DisplayName("hasAllPermissions() 테스트")
    class HasAllPermissionsTest {

        @Test
        @DisplayName("모든 권한을 보유하면 true를 반환한다")
        void hasAllPermissions_withAllMatchingPermissions_shouldReturnTrue() {
            // given
            SecurityContext context =
                    SecurityContext.builder()
                            .userId("user-1")
                            .permissions(Set.of("user:read", "user:write", "order:read"))
                            .build();

            // when & then
            assertThat(context.hasAllPermissions("user:read", "user:write")).isTrue();
        }

        @Test
        @DisplayName("하나라도 보유하지 않으면 false를 반환한다")
        void hasAllPermissions_withMissingPermission_shouldReturnFalse() {
            // given
            SecurityContext context =
                    SecurityContext.builder()
                            .userId("user-1")
                            .permissions(Set.of("user:read"))
                            .build();

            // when & then
            assertThat(context.hasAllPermissions("user:read", "user:write")).isFalse();
        }

        @Test
        @DisplayName("와일드카드 권한으로 모든 권한을 충족한다")
        void hasAllPermissions_withWildcard_shouldReturnTrue() {
            // given
            SecurityContext context =
                    SecurityContext.builder()
                            .userId("user-1")
                            .permissions(Set.of("user:*"))
                            .build();

            // when & then
            assertThat(context.hasAllPermissions("user:read", "user:write", "user:delete"))
                    .isTrue();
        }
    }
}
