package com.ryuqq.authhub.adapter.in.rest.auth.component;

import static org.assertj.core.api.Assertions.assertThat;

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
    @DisplayName("anonymous() 메서드는")
    class AnonymousMethod {

        @Test
        @DisplayName("익명 컨텍스트를 반환한다")
        void shouldReturnAnonymousContext() {
            // When
            SecurityContext context = SecurityContext.anonymous();

            // Then
            assertThat(context).isNotNull();
            assertThat(context.getUserId()).isNull();
            assertThat(context.isAuthenticated()).isFalse();
        }
    }

    @Nested
    @DisplayName("builder()로 생성한 컨텍스트는")
    class BuilderContext {

        @Test
        @DisplayName("userId, tenantId, roles, permissions를 올바르게 반환한다")
        void shouldReturnBuilderValues() {
            // Given
            SecurityContext context =
                    SecurityContext.builder()
                            .userId("user-123")
                            .tenantId("tenant-456")
                            .organizationId("org-789")
                            .roles(Set.of("ROLE_ADMIN"))
                            .permissions(Set.of("user:read", "user:write"))
                            .traceId("trace-001")
                            .build();

            // Then
            assertThat(context.getUserId()).isEqualTo("user-123");
            assertThat(context.getTenantId()).isEqualTo("tenant-456");
            assertThat(context.getOrganizationId()).isEqualTo("org-789");
            assertThat(context.getRoles()).containsExactlyInAnyOrder("ROLE_ADMIN");
            assertThat(context.getPermissions())
                    .containsExactlyInAnyOrder("user:read", "user:write");
            assertThat(context.getTraceId()).isEqualTo("trace-001");
            assertThat(context.isAuthenticated()).isTrue();
        }

        @Test
        @DisplayName("hasRole()이 역할 보유 시 true를 반환한다")
        void shouldReturnTrueWhenHasRole() {
            // Given
            SecurityContext context =
                    SecurityContext.builder()
                            .userId("user-1")
                            .roles(Set.of("ROLE_ADMIN", "ROLE_USER"))
                            .build();

            // Then
            assertThat(context.hasRole("ROLE_ADMIN")).isTrue();
            assertThat(context.hasRole("ROLE_USER")).isTrue();
            assertThat(context.hasRole("ROLE_UNKNOWN")).isFalse();
        }

        @Test
        @DisplayName("hasAnyRole()이 하나라도 보유 시 true를 반환한다")
        void shouldReturnTrueWhenHasAnyRole() {
            // Given
            SecurityContext context =
                    SecurityContext.builder().userId("user-1").roles(Set.of("ROLE_ADMIN")).build();

            // Then
            assertThat(context.hasAnyRole("ROLE_ADMIN", "ROLE_USER")).isTrue();
            assertThat(context.hasAnyRole("ROLE_UNKNOWN", "ROLE_OTHER")).isFalse();
        }

        @Test
        @DisplayName("hasPermission()이 권한 보유 시 true를 반환한다")
        void shouldReturnTrueWhenHasPermission() {
            // Given
            SecurityContext context =
                    SecurityContext.builder()
                            .userId("user-1")
                            .permissions(Set.of("user:read", "user:*"))
                            .build();

            // Then
            assertThat(context.hasPermission("user:read")).isTrue();
            assertThat(context.hasPermission("user:write")).isTrue(); // user:* 와일드카드
            assertThat(context.hasPermission("order:read")).isFalse();
        }

        @Test
        @DisplayName("*:* 와일드카드가 모든 권한을 허용한다")
        void shouldAllowAllWithWildcard() {
            // Given
            SecurityContext context =
                    SecurityContext.builder().userId("user-1").permissions(Set.of("*:*")).build();

            // Then
            assertThat(context.hasPermission("user:read")).isTrue();
            assertThat(context.hasPermission("order:write")).isTrue();
        }

        @Test
        @DisplayName("isSuperAdmin()이 SUPER_ADMIN 역할 시 true를 반환한다")
        void shouldReturnTrueWhenSuperAdmin() {
            // Given
            SecurityContext context =
                    SecurityContext.builder()
                            .userId("user-1")
                            .roles(Set.of("ROLE_SUPER_ADMIN"))
                            .build();

            // Then
            assertThat(context.isSuperAdmin()).isTrue();
        }

        @Test
        @DisplayName("equals와 hashCode가 올바르게 동작한다")
        void shouldSupportEqualsAndHashCode() {
            // Given
            SecurityContext a =
                    SecurityContext.builder().userId("user-1").tenantId("tenant-1").build();
            SecurityContext b =
                    SecurityContext.builder().userId("user-1").tenantId("tenant-1").build();

            // Then
            assertThat(a).isEqualTo(b);
            assertThat(a.hashCode()).isEqualTo(b.hashCode());
        }
    }
}
