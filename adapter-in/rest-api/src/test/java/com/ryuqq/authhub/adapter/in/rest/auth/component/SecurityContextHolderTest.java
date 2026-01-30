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
}
