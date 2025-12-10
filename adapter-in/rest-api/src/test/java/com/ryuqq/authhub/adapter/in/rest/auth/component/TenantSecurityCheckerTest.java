package com.ryuqq.authhub.adapter.in.rest.auth.component;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.domain.auth.vo.Role;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * TenantSecurityChecker 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("TenantSecurityChecker 단위 테스트")
class TenantSecurityCheckerTest {

    private TenantSecurityChecker tenantSecurityChecker;

    @BeforeEach
    void setUp() {
        tenantSecurityChecker = new TenantSecurityChecker();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Nested
    @DisplayName("canAccess() 테스트")
    class CanAccessTest {

        @Test
        @DisplayName("SUPER_ADMIN은 모든 테넌트에 접근할 수 있다")
        void canAccess_superAdmin_shouldReturnTrueForAnyTenant() {
            // given
            SecurityContext context =
                    SecurityContext.builder()
                            .userId("user-1")
                            .tenantId("100")
                            .roles(Set.of(Role.SUPER_ADMIN))
                            .build();
            SecurityContextHolder.setContext(context);

            // when & then
            assertThat(tenantSecurityChecker.canAccess("100")).isTrue();
            assertThat(tenantSecurityChecker.canAccess("200")).isTrue();
            assertThat(tenantSecurityChecker.canAccess("999")).isTrue();
        }

        @Test
        @DisplayName("TENANT_ADMIN은 자신의 테넌트에만 접근할 수 있다")
        void canAccess_tenantAdmin_shouldReturnTrueForOwnTenant() {
            // given
            String ownTenantId = "100";
            SecurityContext context =
                    SecurityContext.builder()
                            .userId("user-1")
                            .tenantId(ownTenantId)
                            .roles(Set.of(Role.TENANT_ADMIN))
                            .build();
            SecurityContextHolder.setContext(context);

            // when & then
            assertThat(tenantSecurityChecker.canAccess(ownTenantId)).isTrue();
        }

        @Test
        @DisplayName("TENANT_ADMIN은 다른 테넌트에 접근할 수 없다")
        void canAccess_tenantAdmin_shouldReturnFalseForOtherTenant() {
            // given
            SecurityContext context =
                    SecurityContext.builder()
                            .userId("user-1")
                            .tenantId("100")
                            .roles(Set.of(Role.TENANT_ADMIN))
                            .build();
            SecurityContextHolder.setContext(context);

            // when & then
            assertThat(tenantSecurityChecker.canAccess("200")).isFalse();
        }

        @Test
        @DisplayName("일반 USER는 자신의 테넌트에만 접근할 수 있다")
        void canAccess_user_shouldReturnTrueForOwnTenant() {
            // given
            String ownTenantId = "100";
            SecurityContext context =
                    SecurityContext.builder()
                            .userId("user-1")
                            .tenantId(ownTenantId)
                            .roles(Set.of(Role.USER))
                            .build();
            SecurityContextHolder.setContext(context);

            // when & then
            assertThat(tenantSecurityChecker.canAccess(ownTenantId)).isTrue();
            assertThat(tenantSecurityChecker.canAccess("200")).isFalse();
        }

        @Test
        @DisplayName("인증되지 않은 요청은 접근할 수 없다")
        void canAccess_anonymous_shouldReturnFalse() {
            // given - Anonymous context (기본값)

            // when & then
            assertThat(tenantSecurityChecker.canAccess("100")).isFalse();
        }
    }

    @Nested
    @DisplayName("getFilterTenantId() 테스트")
    class GetFilterTenantIdTest {

        @Test
        @DisplayName("SUPER_ADMIN은 null을 반환한다 (전체 조회 허용)")
        void getFilterTenantId_superAdmin_shouldReturnNull() {
            // given
            SecurityContext context =
                    SecurityContext.builder()
                            .userId("user-1")
                            .tenantId("100")
                            .roles(Set.of(Role.SUPER_ADMIN))
                            .build();
            SecurityContextHolder.setContext(context);

            // when
            String filterTenantId = tenantSecurityChecker.getFilterTenantId();

            // then
            assertThat(filterTenantId).isNull();
        }

        @Test
        @DisplayName("TENANT_ADMIN은 자신의 tenantId를 반환한다")
        void getFilterTenantId_tenantAdmin_shouldReturnOwnTenantId() {
            // given
            String ownTenantId = "100";
            SecurityContext context =
                    SecurityContext.builder()
                            .userId("user-1")
                            .tenantId(ownTenantId)
                            .roles(Set.of(Role.TENANT_ADMIN))
                            .build();
            SecurityContextHolder.setContext(context);

            // when
            String filterTenantId = tenantSecurityChecker.getFilterTenantId();

            // then
            assertThat(filterTenantId).isEqualTo(ownTenantId);
        }

        @Test
        @DisplayName("일반 USER는 자신의 tenantId를 반환한다")
        void getFilterTenantId_user_shouldReturnOwnTenantId() {
            // given
            String ownTenantId = "100";
            SecurityContext context =
                    SecurityContext.builder()
                            .userId("user-1")
                            .tenantId(ownTenantId)
                            .roles(Set.of(Role.USER))
                            .build();
            SecurityContextHolder.setContext(context);

            // when
            String filterTenantId = tenantSecurityChecker.getFilterTenantId();

            // then
            assertThat(filterTenantId).isEqualTo(ownTenantId);
        }
    }

    @Nested
    @DisplayName("isTenantBound() 테스트")
    class IsTenantBoundTest {

        @Test
        @DisplayName("SUPER_ADMIN은 테넌트 바운드가 아니다")
        void isTenantBound_superAdmin_shouldReturnFalse() {
            // given
            SecurityContext context =
                    SecurityContext.builder()
                            .userId("user-1")
                            .tenantId("100")
                            .roles(Set.of(Role.SUPER_ADMIN))
                            .build();
            SecurityContextHolder.setContext(context);

            // when & then
            assertThat(tenantSecurityChecker.isTenantBound()).isFalse();
        }

        @Test
        @DisplayName("TENANT_ADMIN은 테넌트 바운드이다")
        void isTenantBound_tenantAdmin_shouldReturnTrue() {
            // given
            SecurityContext context =
                    SecurityContext.builder()
                            .userId("user-1")
                            .tenantId("100")
                            .roles(Set.of(Role.TENANT_ADMIN))
                            .build();
            SecurityContextHolder.setContext(context);

            // when & then
            assertThat(tenantSecurityChecker.isTenantBound()).isTrue();
        }

        @Test
        @DisplayName("인증되지 않은 사용자는 테넌트 바운드가 아니다")
        void isTenantBound_anonymous_shouldReturnFalse() {
            // given - Anonymous context

            // when & then
            assertThat(tenantSecurityChecker.isTenantBound()).isFalse();
        }
    }

    @Nested
    @DisplayName("getCurrentTenantId() 테스트")
    class GetCurrentTenantIdTest {

        @Test
        @DisplayName("현재 테넌트 ID를 반환한다")
        void getCurrentTenantId_shouldReturnCurrentTenantId() {
            // given
            String expectedTenantId = "100";
            SecurityContext context =
                    SecurityContext.builder().userId("user-1").tenantId(expectedTenantId).build();
            SecurityContextHolder.setContext(context);

            // when & then
            assertThat(tenantSecurityChecker.getCurrentTenantId()).isEqualTo(expectedTenantId);
        }
    }

    @Nested
    @DisplayName("getCurrentUserId() 테스트")
    class GetCurrentUserIdTest {

        @Test
        @DisplayName("현재 사용자 ID를 반환한다 (UUID 문자열)")
        void getCurrentUserId_shouldReturnCurrentUserId() {
            // given
            String expectedUserId = "01912f58-7b9c-7d4e-8b4a-6c5d4e3f2a1b";
            SecurityContext context = SecurityContext.builder().userId(expectedUserId).build();
            SecurityContextHolder.setContext(context);

            // when & then
            assertThat(tenantSecurityChecker.getCurrentUserId()).isEqualTo(expectedUserId);
        }
    }
}
