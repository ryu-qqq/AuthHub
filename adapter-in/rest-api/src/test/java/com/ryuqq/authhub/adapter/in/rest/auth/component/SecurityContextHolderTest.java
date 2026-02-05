package com.ryuqq.authhub.adapter.in.rest.auth.component;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * SecurityContextHolder 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("SecurityContextHolder 단위 테스트")
class SecurityContextHolderTest {

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Nested
    @DisplayName("getContext() 메서드는")
    class GetContextMethod {

        @Test
        @DisplayName("컨텍스트 미설정 시 익명 컨텍스트를 반환한다")
        void shouldReturnAnonymousWhenNotSet() {
            // When
            SecurityContext context = SecurityContextHolder.getContext();

            // Then
            assertThat(context).isNotNull();
            assertThat(context.isAuthenticated()).isFalse();
        }

        @Test
        @DisplayName("설정된 컨텍스트를 반환한다")
        void shouldReturnSetContext() {
            // Given
            SecurityContext expected =
                    SecurityContext.builder().userId("user-1").tenantId("tenant-1").build();
            SecurityContextHolder.setContext(expected);

            // When
            SecurityContext actual = SecurityContextHolder.getContext();

            // Then
            assertThat(actual).isSameAs(expected);
        }
    }

    @Nested
    @DisplayName("setContext() 메서드는")
    class SetContextMethod {

        @Test
        @DisplayName("null 전달 시 NullPointerException을 던진다")
        void shouldThrowWhenContextIsNull() {
            assertThatThrownBy(() -> SecurityContextHolder.setContext(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("SecurityContext must not be null");
        }
    }

    @Nested
    @DisplayName("clearContext() 메서드는")
    class ClearContextMethod {

        @Test
        @DisplayName("설정된 컨텍스트를 제거한다")
        void shouldClearContext() {
            // Given
            SecurityContextHolder.setContext(SecurityContext.builder().userId("user-1").build());

            // When
            SecurityContextHolder.clearContext();

            // Then
            SecurityContext context = SecurityContextHolder.getContext();
            assertThat(context.isAuthenticated()).isFalse();
        }
    }

    @Nested
    @DisplayName("getCurrentUserId() 메서드는")
    class GetCurrentUserIdMethod {

        @Test
        @DisplayName("설정된 컨텍스트의 userId를 반환한다")
        void shouldReturnCurrentUserId() {
            // Given
            SecurityContextHolder.setContext(SecurityContext.builder().userId("user-123").build());

            // When
            String userId = SecurityContextHolder.getCurrentUserId();

            // Then
            assertThat(userId).isEqualTo("user-123");
        }
    }

    @Nested
    @DisplayName("getCurrentTenantId() 메서드는")
    class GetCurrentTenantIdMethod {

        @Test
        @DisplayName("설정된 컨텍스트의 tenantId를 반환한다")
        void shouldReturnCurrentTenantId() {
            // Given
            SecurityContextHolder.setContext(
                    SecurityContext.builder().userId("user-1").tenantId("tenant-456").build());

            // When
            String tenantId = SecurityContextHolder.getCurrentTenantId();

            // Then
            assertThat(tenantId).isEqualTo("tenant-456");
        }
    }

    @Nested
    @DisplayName("hasRole() 메서드는")
    class HasRoleMethod {

        @Test
        @DisplayName("컨텍스트의 hasRole을 위임한다")
        void shouldDelegateToContext() {
            // Given
            SecurityContextHolder.setContext(
                    SecurityContext.builder().userId("user-1").roles(Set.of("ROLE_ADMIN")).build());

            // Then
            assertThat(SecurityContextHolder.hasRole("ROLE_ADMIN")).isTrue();
            assertThat(SecurityContextHolder.hasRole("ROLE_USER")).isFalse();
        }
    }

    @Nested
    @DisplayName("hasPermission() 메서드는")
    class HasPermissionMethod {

        @Test
        @DisplayName("컨텍스트의 hasPermission을 위임한다")
        void shouldDelegateToContext() {
            // Given
            SecurityContextHolder.setContext(
                    SecurityContext.builder()
                            .userId("user-1")
                            .permissions(Set.of("user:read"))
                            .build());

            // Then
            assertThat(SecurityContextHolder.hasPermission("user:read")).isTrue();
            assertThat(SecurityContextHolder.hasPermission("user:write")).isFalse();
        }
    }

    @Nested
    @DisplayName("getCurrentOrganizationId() 메서드는")
    class GetCurrentOrganizationIdMethod {

        @Test
        @DisplayName("설정된 컨텍스트의 organizationId를 반환한다")
        void shouldReturnCurrentOrganizationId() {
            // Given
            SecurityContextHolder.setContext(
                    SecurityContext.builder()
                            .userId("user-1")
                            .tenantId("tenant-1")
                            .organizationId("org-789")
                            .build());

            // When
            String organizationId = SecurityContextHolder.getCurrentOrganizationId();

            // Then
            assertThat(organizationId).isEqualTo("org-789");
        }
    }

    @Nested
    @DisplayName("hasAnyRole() 메서드는")
    class HasAnyRoleMethod {

        @Test
        @DisplayName("컨텍스트의 hasAnyRole을 위임한다")
        void shouldDelegateToContext() {
            // Given
            SecurityContextHolder.setContext(
                    SecurityContext.builder().userId("user-1").roles(Set.of("ROLE_USER")).build());

            // Then
            assertThat(SecurityContextHolder.hasAnyRole("ROLE_ADMIN", "ROLE_USER")).isTrue();
            assertThat(SecurityContextHolder.hasAnyRole("ROLE_ADMIN", "ROLE_OTHER")).isFalse();
        }
    }

    @Nested
    @DisplayName("hasAnyPermission() 메서드는")
    class HasAnyPermissionMethod {

        @Test
        @DisplayName("컨텍스트의 hasAnyPermission을 위임한다")
        void shouldDelegateToContext() {
            // Given
            SecurityContextHolder.setContext(
                    SecurityContext.builder()
                            .userId("user-1")
                            .permissions(Set.of("user:read"))
                            .build());

            // Then
            assertThat(SecurityContextHolder.hasAnyPermission("user:write", "user:read")).isTrue();
            assertThat(SecurityContextHolder.hasAnyPermission("order:read", "order:write"))
                    .isFalse();
        }
    }

    @Nested
    @DisplayName("hasAllPermissions() 메서드는")
    class HasAllPermissionsMethod {

        @Test
        @DisplayName("컨텍스트의 hasAllPermissions을 위임한다")
        void shouldDelegateToContext() {
            // Given
            SecurityContextHolder.setContext(
                    SecurityContext.builder()
                            .userId("user-1")
                            .permissions(Set.of("user:read", "user:write"))
                            .build());

            // Then
            assertThat(SecurityContextHolder.hasAllPermissions("user:read", "user:write")).isTrue();
            assertThat(SecurityContextHolder.hasAllPermissions("user:read", "user:delete"))
                    .isFalse();
        }
    }

    @Nested
    @DisplayName("isSuperAdmin() 메서드는")
    class IsSuperAdminMethod {

        @Test
        @DisplayName("SUPER_ADMIN 역할 시 true를 반환한다")
        void shouldReturnTrueWhenSuperAdmin() {
            // Given
            SecurityContextHolder.setContext(
                    SecurityContext.builder()
                            .userId("admin")
                            .roles(Set.of(SecurityContext.ROLE_SUPER_ADMIN))
                            .build());

            // Then
            assertThat(SecurityContextHolder.isSuperAdmin()).isTrue();
        }

        @Test
        @DisplayName("SUPER_ADMIN 아닌 경우 false를 반환한다")
        void shouldReturnFalseWhenNotSuperAdmin() {
            // Given
            SecurityContextHolder.setContext(
                    SecurityContext.builder().userId("user-1").roles(Set.of("ROLE_USER")).build());

            // Then
            assertThat(SecurityContextHolder.isSuperAdmin()).isFalse();
        }
    }

    @Nested
    @DisplayName("isAuthenticated() 메서드는")
    class IsAuthenticatedMethod {

        @Test
        @DisplayName("인증된 사용자 시 true를 반환한다")
        void shouldReturnTrueWhenAuthenticated() {
            // Given
            SecurityContextHolder.setContext(SecurityContext.builder().userId("user-1").build());

            // Then
            assertThat(SecurityContextHolder.isAuthenticated()).isTrue();
        }

        @Test
        @DisplayName("익명 사용자 시 false를 반환한다")
        void shouldReturnFalseWhenAnonymous() {
            // Given - clearContext 후 getContext는 anonymous 반환
            SecurityContextHolder.clearContext();

            // Then
            assertThat(SecurityContextHolder.isAuthenticated()).isFalse();
        }
    }

    @Nested
    @DisplayName("getTraceId() 메서드는")
    class GetTraceIdMethod {

        @Test
        @DisplayName("설정된 컨텍스트의 traceId를 반환한다")
        void shouldReturnTraceId() {
            // Given
            SecurityContextHolder.setContext(
                    SecurityContext.builder().userId("user-1").traceId("trace-abc-123").build());

            // When
            String traceId = SecurityContextHolder.getTraceId();

            // Then
            assertThat(traceId).isEqualTo("trace-abc-123");
        }
    }

    @Nested
    @DisplayName("Thread Safety")
    class ThreadSafety {

        @Test
        @DisplayName("clearContext() 후 getContext()는 anonymous를 반환한다")
        void shouldReturnAnonymousAfterClearContext() {
            // Given
            SecurityContextHolder.setContext(SecurityContext.builder().userId("user-1").build());

            // When
            SecurityContextHolder.clearContext();
            SecurityContext context = SecurityContextHolder.getContext();

            // Then
            assertThat(context).isNotNull();
            assertThat(context.isAuthenticated()).isFalse();
            assertThat(context.getUserId()).isNull();
        }
    }
}
