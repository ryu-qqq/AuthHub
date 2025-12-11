package com.ryuqq.authhub.adapter.in.rest.auth.component;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.authhub.domain.auth.vo.Role;
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
    @DisplayName("getContext() 테스트")
    class GetContextTest {

        @Test
        @DisplayName("컨텍스트가 설정되지 않으면 Anonymous를 반환한다")
        void getContext_whenNotSet_shouldReturnAnonymous() {
            // given & when
            SecurityContext context = SecurityContextHolder.getContext();

            // then
            assertThat(context.isAuthenticated()).isFalse();
            assertThat(context.getUserId()).isNull();
        }

        @Test
        @DisplayName("설정된 컨텍스트를 반환한다")
        void getContext_whenSet_shouldReturnSetContext() {
            // given
            SecurityContext expected =
                    SecurityContext.builder()
                            .userId("user-1")
                            .tenantId("01912f58-7b9c-7d4e-8b4a-6c5d4e3f2a1c")
                            .roles(Set.of(Role.TENANT_ADMIN))
                            .build();
            SecurityContextHolder.setContext(expected);

            // when
            SecurityContext actual = SecurityContextHolder.getContext();

            // then
            assertThat(actual).isEqualTo(expected);
        }
    }

    @Nested
    @DisplayName("setContext() 테스트")
    class SetContextTest {

        @Test
        @DisplayName("null 컨텍스트를 설정하면 NullPointerException이 발생한다")
        void setContext_withNull_shouldThrowException() {
            // given & when & then
            assertThatThrownBy(() -> SecurityContextHolder.setContext(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("SecurityContext must not be null");
        }

        @Test
        @DisplayName("컨텍스트를 설정하면 ThreadLocal에 저장된다")
        void setContext_shouldStoreInThreadLocal() {
            // given
            SecurityContext context = SecurityContext.builder().userId("user-1").build();

            // when
            SecurityContextHolder.setContext(context);

            // then
            assertThat(SecurityContextHolder.getContext()).isEqualTo(context);
        }
    }

    @Nested
    @DisplayName("clearContext() 테스트")
    class ClearContextTest {

        @Test
        @DisplayName("컨텍스트를 정리하면 Anonymous 상태가 된다")
        void clearContext_shouldResetToAnonymous() {
            // given
            SecurityContext context = SecurityContext.builder().userId("user-1").build();
            SecurityContextHolder.setContext(context);

            // when
            SecurityContextHolder.clearContext();

            // then
            assertThat(SecurityContextHolder.getContext().isAuthenticated()).isFalse();
        }
    }

    @Nested
    @DisplayName("편의 메서드 테스트")
    class ConvenienceMethodsTest {

        @Test
        @DisplayName("getCurrentUserId()는 현재 사용자 ID를 반환한다 (UUID 문자열)")
        void getCurrentUserId_shouldReturnUserId() {
            // given
            String expectedUserId = "01912f58-7b9c-7d4e-8b4a-6c5d4e3f2a1b";
            SecurityContext context = SecurityContext.builder().userId(expectedUserId).build();
            SecurityContextHolder.setContext(context);

            // when & then
            assertThat(SecurityContextHolder.getCurrentUserId()).isEqualTo(expectedUserId);
        }

        @Test
        @DisplayName("getCurrentTenantId()는 현재 테넌트 ID를 반환한다 (UUIDv7 문자열)")
        void getCurrentTenantId_shouldReturnTenantId() {
            // given
            String expectedTenantId = "01912f58-7b9c-7d4e-8b4a-6c5d4e3f2a1c";
            SecurityContext context =
                    SecurityContext.builder().userId("user-1").tenantId(expectedTenantId).build();
            SecurityContextHolder.setContext(context);

            // when & then
            assertThat(SecurityContextHolder.getCurrentTenantId()).isEqualTo(expectedTenantId);
        }

        @Test
        @DisplayName("getCurrentOrganizationId()는 현재 조직 ID를 반환한다 (UUIDv7 문자열)")
        void getCurrentOrganizationId_shouldReturnOrganizationId() {
            // given
            String expectedOrganizationId = "01912f58-7b9c-7d4e-8b4a-6c5d4e3f2a1d";
            SecurityContext context =
                    SecurityContext.builder()
                            .userId("user-1")
                            .organizationId(expectedOrganizationId)
                            .build();
            SecurityContextHolder.setContext(context);

            // when & then
            assertThat(SecurityContextHolder.getCurrentOrganizationId())
                    .isEqualTo(expectedOrganizationId);
        }

        @Test
        @DisplayName("getCurrentOrganizationId()는 조직이 설정되지 않으면 null을 반환한다")
        void getCurrentOrganizationId_whenNotSet_shouldReturnNull() {
            // given
            SecurityContext context = SecurityContext.builder().userId("user-1").build();
            SecurityContextHolder.setContext(context);

            // when & then
            assertThat(SecurityContextHolder.getCurrentOrganizationId()).isNull();
        }

        @Test
        @DisplayName("hasRole()은 역할 보유 여부를 반환한다")
        void hasRole_shouldReturnRoleStatus() {
            // given
            SecurityContext context =
                    SecurityContext.builder()
                            .userId("user-1")
                            .roles(Set.of(Role.TENANT_ADMIN))
                            .build();
            SecurityContextHolder.setContext(context);

            // when & then
            assertThat(SecurityContextHolder.hasRole(Role.TENANT_ADMIN)).isTrue();
            assertThat(SecurityContextHolder.hasRole(Role.SUPER_ADMIN)).isFalse();
        }

        @Test
        @DisplayName("hasAnyRole()은 여러 역할 중 하나라도 보유하면 true를 반환한다")
        void hasAnyRole_shouldReturnTrueIfAnyRoleMatches() {
            // given
            SecurityContext context =
                    SecurityContext.builder().userId("user-1").roles(Set.of(Role.USER)).build();
            SecurityContextHolder.setContext(context);

            // when & then
            assertThat(SecurityContextHolder.hasAnyRole(Role.USER, Role.TENANT_ADMIN)).isTrue();
            assertThat(SecurityContextHolder.hasAnyRole(Role.SUPER_ADMIN, Role.TENANT_ADMIN))
                    .isFalse();
        }

        @Test
        @DisplayName("isSuperAdmin()은 SUPER_ADMIN 여부를 반환한다")
        void isSuperAdmin_shouldReturnSuperAdminStatus() {
            // given
            SecurityContext context =
                    SecurityContext.builder()
                            .userId("user-1")
                            .roles(Set.of(Role.SUPER_ADMIN))
                            .build();
            SecurityContextHolder.setContext(context);

            // when & then
            assertThat(SecurityContextHolder.isSuperAdmin()).isTrue();
        }

        @Test
        @DisplayName("isAuthenticated()는 인증 여부를 반환한다")
        void isAuthenticated_shouldReturnAuthenticationStatus() {
            // given
            SecurityContext context = SecurityContext.builder().userId("user-1").build();
            SecurityContextHolder.setContext(context);

            // when & then
            assertThat(SecurityContextHolder.isAuthenticated()).isTrue();
        }

        @Test
        @DisplayName("getTraceId()는 추적 ID를 반환한다")
        void getTraceId_shouldReturnTraceId() {
            // given
            String expectedTraceId = "trace-123";
            SecurityContext context =
                    SecurityContext.builder().userId("user-1").traceId(expectedTraceId).build();
            SecurityContextHolder.setContext(context);

            // when & then
            assertThat(SecurityContextHolder.getTraceId()).isEqualTo(expectedTraceId);
        }

        @Test
        @DisplayName("hasPermission()은 권한 보유 여부를 반환한다")
        void hasPermission_shouldReturnPermissionStatus() {
            // given
            SecurityContext context =
                    SecurityContext.builder()
                            .userId("user-1")
                            .permissions(Set.of("user:read", "order:write"))
                            .build();
            SecurityContextHolder.setContext(context);

            // when & then
            assertThat(SecurityContextHolder.hasPermission("user:read")).isTrue();
            assertThat(SecurityContextHolder.hasPermission("user:write")).isFalse();
        }

        @Test
        @DisplayName("hasAnyPermission()은 여러 권한 중 하나라도 보유하면 true를 반환한다")
        void hasAnyPermission_shouldReturnTrueIfAnyPermissionMatches() {
            // given
            SecurityContext context =
                    SecurityContext.builder()
                            .userId("user-1")
                            .permissions(Set.of("user:read"))
                            .build();
            SecurityContextHolder.setContext(context);

            // when & then
            assertThat(SecurityContextHolder.hasAnyPermission("user:read", "user:write")).isTrue();
            assertThat(SecurityContextHolder.hasAnyPermission("order:read", "order:write"))
                    .isFalse();
        }

        @Test
        @DisplayName("hasAllPermissions()은 모든 권한을 보유해야 true를 반환한다")
        void hasAllPermissions_shouldReturnTrueOnlyIfAllPermissionsMatch() {
            // given
            SecurityContext context =
                    SecurityContext.builder()
                            .userId("user-1")
                            .permissions(Set.of("user:read", "user:write", "order:read"))
                            .build();
            SecurityContextHolder.setContext(context);

            // when & then
            assertThat(SecurityContextHolder.hasAllPermissions("user:read", "user:write")).isTrue();
            assertThat(SecurityContextHolder.hasAllPermissions("user:read", "tenant:read"))
                    .isFalse();
        }
    }

    @Nested
    @DisplayName("스레드 격리 테스트")
    class ThreadIsolationTest {

        @Test
        @DisplayName("다른 스레드에서 설정한 컨텍스트는 현재 스레드에 영향을 주지 않는다")
        void context_shouldBeIsolatedBetweenThreads() throws InterruptedException {
            // given
            SecurityContext mainThreadContext = SecurityContext.builder().userId("user-1").build();
            SecurityContextHolder.setContext(mainThreadContext);

            // when
            Thread otherThread =
                    new Thread(
                            () -> {
                                SecurityContext otherContext =
                                        SecurityContext.builder().userId("user-2").build();
                                SecurityContextHolder.setContext(otherContext);
                            });
            otherThread.start();
            otherThread.join();

            // then - 메인 스레드의 컨텍스트는 변경되지 않음
            assertThat(SecurityContextHolder.getCurrentUserId()).isEqualTo("user-1");
        }
    }
}
