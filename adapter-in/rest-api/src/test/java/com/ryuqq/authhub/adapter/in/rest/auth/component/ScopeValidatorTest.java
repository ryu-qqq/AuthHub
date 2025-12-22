package com.ryuqq.authhub.adapter.in.rest.auth.component;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.auth.common.constant.Roles;
import com.ryuqq.auth.common.constant.Scopes;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * ScopeValidator 단위 테스트
 *
 * <p>Scope 기반 접근 제어 검증 로직을 테스트합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("ScopeValidator 단위 테스트")
class ScopeValidatorTest {

    private ScopeValidator scopeValidator;

    private static final String TENANT_A = "tenant-a-uuid";
    private static final String TENANT_B = "tenant-b-uuid";
    private static final String ORG_1 = "org-1-uuid";
    private static final String ORG_2 = "org-2-uuid";
    private static final String USER_ID = "user-uuid";

    @BeforeEach
    void setUp() {
        scopeValidator = new ScopeValidator();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Nested
    @DisplayName("getCurrentScope() 테스트")
    class GetCurrentScopeTest {

        @Test
        @DisplayName("SUPER_ADMIN 역할이면 GLOBAL scope를 반환한다")
        void getCurrentScope_withSuperAdmin_shouldReturnGlobal() {
            // given
            setSecurityContext(Set.of(Roles.SUPER_ADMIN), TENANT_A, ORG_1);

            // when
            String scope = scopeValidator.getCurrentScope();

            // then
            assertThat(scope).isEqualTo(Scopes.GLOBAL);
        }

        @Test
        @DisplayName("TENANT_ADMIN 역할이면 TENANT scope를 반환한다")
        void getCurrentScope_withTenantAdmin_shouldReturnTenant() {
            // given
            setSecurityContext(Set.of(Roles.TENANT_ADMIN), TENANT_A, ORG_1);

            // when
            String scope = scopeValidator.getCurrentScope();

            // then
            assertThat(scope).isEqualTo(Scopes.TENANT);
        }

        @Test
        @DisplayName("ORG_ADMIN 역할이면 ORGANIZATION scope를 반환한다")
        void getCurrentScope_withOrgAdmin_shouldReturnOrganization() {
            // given
            setSecurityContext(Set.of(Roles.ORG_ADMIN), TENANT_A, ORG_1);

            // when
            String scope = scopeValidator.getCurrentScope();

            // then
            assertThat(scope).isEqualTo(Scopes.ORGANIZATION);
        }

        @Test
        @DisplayName("USER 역할이면 ORGANIZATION scope를 반환한다")
        void getCurrentScope_withUser_shouldReturnOrganization() {
            // given
            setSecurityContext(Set.of(Roles.USER), TENANT_A, ORG_1);

            // when
            String scope = scopeValidator.getCurrentScope();

            // then
            assertThat(scope).isEqualTo(Scopes.ORGANIZATION);
        }

        @Test
        @DisplayName("여러 역할 보유 시 가장 높은 scope를 반환한다")
        void getCurrentScope_withMultipleRoles_shouldReturnHighestScope() {
            // given
            setSecurityContext(Set.of(Roles.TENANT_ADMIN, Roles.USER), TENANT_A, ORG_1);

            // when
            String scope = scopeValidator.getCurrentScope();

            // then
            assertThat(scope).isEqualTo(Scopes.TENANT);
        }

        @Test
        @DisplayName("역할이 없으면 ORGANIZATION scope를 반환한다")
        void getCurrentScope_withNoRoles_shouldReturnOrganization() {
            // given
            setSecurityContext(Set.of(), TENANT_A, ORG_1);

            // when
            String scope = scopeValidator.getCurrentScope();

            // then
            assertThat(scope).isEqualTo(Scopes.ORGANIZATION);
        }
    }

    @Nested
    @DisplayName("canAccessTenant() 테스트")
    class CanAccessTenantTest {

        @Test
        @DisplayName("GLOBAL scope는 모든 테넌트에 접근 가능하다")
        void canAccessTenant_withGlobalScope_shouldReturnTrue() {
            // given
            setSecurityContext(Set.of(Roles.SUPER_ADMIN), TENANT_A, ORG_1);

            // when & then
            assertThat(scopeValidator.canAccessTenant(TENANT_A)).isTrue();
            assertThat(scopeValidator.canAccessTenant(TENANT_B)).isTrue();
        }

        @Test
        @DisplayName("TENANT scope는 본인 테넌트에만 접근 가능하다")
        void canAccessTenant_withTenantScope_shouldAccessOwnTenantOnly() {
            // given
            setSecurityContext(Set.of(Roles.TENANT_ADMIN), TENANT_A, ORG_1);

            // when & then
            assertThat(scopeValidator.canAccessTenant(TENANT_A)).isTrue();
            assertThat(scopeValidator.canAccessTenant(TENANT_B)).isFalse();
        }

        @Test
        @DisplayName("ORGANIZATION scope는 본인 테넌트에만 접근 가능하다")
        void canAccessTenant_withOrganizationScope_shouldAccessOwnTenantOnly() {
            // given
            setSecurityContext(Set.of(Roles.USER), TENANT_A, ORG_1);

            // when & then
            assertThat(scopeValidator.canAccessTenant(TENANT_A)).isTrue();
            assertThat(scopeValidator.canAccessTenant(TENANT_B)).isFalse();
        }

        @Test
        @DisplayName("대상 테넌트 ID가 null이면 false를 반환한다")
        void canAccessTenant_withNullTargetTenantId_shouldReturnFalse() {
            // given
            setSecurityContext(Set.of(Roles.SUPER_ADMIN), TENANT_A, ORG_1);

            // when & then
            assertThat(scopeValidator.canAccessTenant(null)).isFalse();
        }

        @Test
        @DisplayName("본인 테넌트 ID가 null이면 false를 반환한다")
        void canAccessTenant_withNullCurrentTenantId_shouldReturnFalse() {
            // given
            setSecurityContext(Set.of(Roles.TENANT_ADMIN), null, ORG_1);

            // when & then
            assertThat(scopeValidator.canAccessTenant(TENANT_A)).isFalse();
        }
    }

    @Nested
    @DisplayName("canAccessOrganization() 테스트")
    class CanAccessOrganizationTest {

        @Test
        @DisplayName("GLOBAL scope는 모든 조직에 접근 가능하다")
        void canAccessOrganization_withGlobalScope_shouldReturnTrue() {
            // given
            setSecurityContext(Set.of(Roles.SUPER_ADMIN), TENANT_A, ORG_1);

            // when & then
            assertThat(scopeValidator.canAccessOrganization(ORG_1, TENANT_A)).isTrue();
            assertThat(scopeValidator.canAccessOrganization(ORG_2, TENANT_B)).isTrue();
        }

        @Test
        @DisplayName("TENANT scope는 본인 테넌트 내 모든 조직에 접근 가능하다")
        void canAccessOrganization_withTenantScope_shouldAccessOwnTenantOrgsOnly() {
            // given
            setSecurityContext(Set.of(Roles.TENANT_ADMIN), TENANT_A, ORG_1);

            // when & then
            assertThat(scopeValidator.canAccessOrganization(ORG_1, TENANT_A)).isTrue();
            assertThat(scopeValidator.canAccessOrganization(ORG_2, TENANT_A)).isTrue();
            assertThat(scopeValidator.canAccessOrganization(ORG_2, TENANT_B)).isFalse();
        }

        @Test
        @DisplayName("ORGANIZATION scope는 본인 조직에만 접근 가능하다")
        void canAccessOrganization_withOrganizationScope_shouldAccessOwnOrgOnly() {
            // given
            setSecurityContext(Set.of(Roles.USER), TENANT_A, ORG_1);

            // when & then
            assertThat(scopeValidator.canAccessOrganization(ORG_1, TENANT_A)).isTrue();
            assertThat(scopeValidator.canAccessOrganization(ORG_2, TENANT_A)).isFalse();
        }

        @Test
        @DisplayName("대상 조직 ID가 null이면 false를 반환한다")
        void canAccessOrganization_withNullTargetOrgId_shouldReturnFalse() {
            // given
            setSecurityContext(Set.of(Roles.SUPER_ADMIN), TENANT_A, ORG_1);

            // when & then
            assertThat(scopeValidator.canAccessOrganization(null, TENANT_A)).isFalse();
        }

        @Test
        @DisplayName("본인 조직 ID가 null이면 false를 반환한다")
        void canAccessOrganization_withNullCurrentOrgId_shouldReturnFalse() {
            // given
            setSecurityContext(Set.of(Roles.USER), TENANT_A, null);

            // when & then
            assertThat(scopeValidator.canAccessOrganization(ORG_1, TENANT_A)).isFalse();
        }
    }

    @Nested
    @DisplayName("canAccessGlobal() 테스트")
    class CanAccessGlobalTest {

        @Test
        @DisplayName("GLOBAL scope는 전역 접근이 가능하다")
        void canAccessGlobal_withGlobalScope_shouldReturnTrue() {
            // given
            setSecurityContext(Set.of(Roles.SUPER_ADMIN), TENANT_A, ORG_1);

            // when & then
            assertThat(scopeValidator.canAccessGlobal()).isTrue();
        }

        @Test
        @DisplayName("TENANT scope는 전역 접근이 불가능하다")
        void canAccessGlobal_withTenantScope_shouldReturnFalse() {
            // given
            setSecurityContext(Set.of(Roles.TENANT_ADMIN), TENANT_A, ORG_1);

            // when & then
            assertThat(scopeValidator.canAccessGlobal()).isFalse();
        }

        @Test
        @DisplayName("ORGANIZATION scope는 전역 접근이 불가능하다")
        void canAccessGlobal_withOrganizationScope_shouldReturnFalse() {
            // given
            setSecurityContext(Set.of(Roles.USER), TENANT_A, ORG_1);

            // when & then
            assertThat(scopeValidator.canAccessGlobal()).isFalse();
        }
    }

    @Nested
    @DisplayName("별칭 메서드 테스트")
    class AliasMethodsTest {

        @Test
        @DisplayName("tenant() 메서드는 canAccessTenant()과 동일하게 동작한다")
        void tenant_shouldBehaveAsCanAccessTenant() {
            // given
            setSecurityContext(Set.of(Roles.TENANT_ADMIN), TENANT_A, ORG_1);

            // when & then
            assertThat(scopeValidator.tenant(TENANT_A)).isTrue();
            assertThat(scopeValidator.tenant(TENANT_B)).isFalse();
        }

        @Test
        @DisplayName("organization() 메서드는 canAccessOrganization()과 동일하게 동작한다")
        void organization_shouldBehaveAsCanAccessOrganization() {
            // given
            setSecurityContext(Set.of(Roles.USER), TENANT_A, ORG_1);

            // when & then
            assertThat(scopeValidator.organization(ORG_1, TENANT_A)).isTrue();
            assertThat(scopeValidator.organization(ORG_2, TENANT_A)).isFalse();
        }

        @Test
        @DisplayName("global() 메서드는 canAccessGlobal()과 동일하게 동작한다")
        void global_shouldBehaveAsCanAccessGlobal() {
            // given
            setSecurityContext(Set.of(Roles.SUPER_ADMIN), TENANT_A, ORG_1);

            // when & then
            assertThat(scopeValidator.global()).isTrue();
        }

        @Test
        @DisplayName("ownTenant() 메서드는 canAccessTenant()과 동일하게 동작한다")
        void ownTenant_shouldBehaveAsCanAccessTenant() {
            // given
            setSecurityContext(Set.of(Roles.TENANT_ADMIN), TENANT_A, ORG_1);

            // when & then
            assertThat(scopeValidator.ownTenant(TENANT_A)).isTrue();
            assertThat(scopeValidator.ownTenant(TENANT_B)).isFalse();
        }

        @Test
        @DisplayName("ownOrganization() 메서드는 본인 조직 접근을 확인한다")
        void ownOrganization_shouldCheckOwnOrganization() {
            // given
            setSecurityContext(Set.of(Roles.USER), TENANT_A, ORG_1);

            // when & then
            assertThat(scopeValidator.ownOrganization(ORG_1)).isTrue();
            assertThat(scopeValidator.ownOrganization(ORG_2)).isFalse();
        }
    }

    private void setSecurityContext(Set<String> roles, String tenantId, String organizationId) {
        SecurityContext context =
                SecurityContext.builder()
                        .userId(USER_ID)
                        .tenantId(tenantId)
                        .organizationId(organizationId)
                        .roles(roles)
                        .build();
        SecurityContextHolder.setContext(context);
    }
}
