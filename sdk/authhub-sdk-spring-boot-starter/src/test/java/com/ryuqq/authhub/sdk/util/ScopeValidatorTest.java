package com.ryuqq.authhub.sdk.util;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.sdk.constant.Scopes;
import com.ryuqq.authhub.sdk.context.UserContext;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("ScopeValidator")
class ScopeValidatorTest {

    private static final String TENANT_ID = "tenant-123";
    private static final String OTHER_TENANT_ID = "tenant-456";
    private static final String ORG_ID = "org-123";
    private static final String OTHER_ORG_ID = "org-456";
    private static final String USER_ID = "user-123";
    private static final String OTHER_USER_ID = "user-456";

    @Nested
    @DisplayName("canAccessTenant")
    class CanAccessTenant {

        @Test
        @DisplayName("GLOBAL 범위 사용자는 모든 테넌트에 접근 가능")
        void globalScopeCanAccessAnyTenant() {
            UserContext context = createContext(Scopes.GLOBAL, TENANT_ID, ORG_ID);

            assertThat(ScopeValidator.canAccessTenant(context, OTHER_TENANT_ID)).isTrue();
        }

        @Test
        @DisplayName("TENANT 범위 사용자는 자신의 테넌트에만 접근 가능")
        void tenantScopeCanAccessOwnTenant() {
            UserContext context = createContext(Scopes.TENANT, TENANT_ID, ORG_ID);

            assertThat(ScopeValidator.canAccessTenant(context, TENANT_ID)).isTrue();
            assertThat(ScopeValidator.canAccessTenant(context, OTHER_TENANT_ID)).isFalse();
        }

        @Test
        @DisplayName("ORGANIZATION 범위 사용자는 자신의 테넌트에만 접근 가능")
        void organizationScopeCanAccessOwnTenant() {
            UserContext context = createContext(Scopes.ORGANIZATION, TENANT_ID, ORG_ID);

            assertThat(ScopeValidator.canAccessTenant(context, TENANT_ID)).isTrue();
            assertThat(ScopeValidator.canAccessTenant(context, OTHER_TENANT_ID)).isFalse();
        }

        @Test
        @DisplayName("null 입력 시 false 반환")
        void nullInputReturnsFalse() {
            UserContext context = createContext(Scopes.GLOBAL, TENANT_ID, ORG_ID);

            assertThat(ScopeValidator.canAccessTenant(null, TENANT_ID)).isFalse();
            assertThat(ScopeValidator.canAccessTenant(context, null)).isFalse();
        }
    }

    @Nested
    @DisplayName("canAccessOrganization")
    class CanAccessOrganization {

        @Test
        @DisplayName("GLOBAL 범위 사용자는 모든 조직에 접근 가능")
        void globalScopeCanAccessAnyOrganization() {
            UserContext context = createContext(Scopes.GLOBAL, TENANT_ID, ORG_ID);

            assertThat(ScopeValidator.canAccessOrganization(context, OTHER_TENANT_ID, OTHER_ORG_ID))
                    .isTrue();
        }

        @Test
        @DisplayName("TENANT 범위 사용자는 자신의 테넌트 내 모든 조직에 접근 가능")
        void tenantScopeCanAccessOrganizationsInOwnTenant() {
            UserContext context = createContext(Scopes.TENANT, TENANT_ID, ORG_ID);

            assertThat(ScopeValidator.canAccessOrganization(context, TENANT_ID, ORG_ID)).isTrue();
            assertThat(ScopeValidator.canAccessOrganization(context, TENANT_ID, OTHER_ORG_ID))
                    .isTrue();
            assertThat(ScopeValidator.canAccessOrganization(context, OTHER_TENANT_ID, OTHER_ORG_ID))
                    .isFalse();
        }

        @Test
        @DisplayName("ORGANIZATION 범위 사용자는 자신의 조직에만 접근 가능")
        void organizationScopeCanAccessOwnOrganizationOnly() {
            UserContext context = createContext(Scopes.ORGANIZATION, TENANT_ID, ORG_ID);

            assertThat(ScopeValidator.canAccessOrganization(context, TENANT_ID, ORG_ID)).isTrue();
            assertThat(ScopeValidator.canAccessOrganization(context, TENANT_ID, OTHER_ORG_ID))
                    .isFalse();
            assertThat(ScopeValidator.canAccessOrganization(context, OTHER_TENANT_ID, ORG_ID))
                    .isFalse();
        }

        @Test
        @DisplayName("null 입력 시 false 반환")
        void nullInputReturnsFalse() {
            UserContext context = createContext(Scopes.GLOBAL, TENANT_ID, ORG_ID);

            assertThat(ScopeValidator.canAccessOrganization(null, TENANT_ID, ORG_ID)).isFalse();
            assertThat(ScopeValidator.canAccessOrganization(context, null, ORG_ID)).isFalse();
            assertThat(ScopeValidator.canAccessOrganization(context, TENANT_ID, null)).isFalse();
        }
    }

    @Nested
    @DisplayName("canAccessUser")
    class CanAccessUser {

        @Test
        @DisplayName("자기 자신의 데이터에는 항상 접근 가능")
        void canAlwaysAccessOwnData() {
            UserContext context = createContext(Scopes.ORGANIZATION, TENANT_ID, ORG_ID);

            assertThat(
                            ScopeValidator.canAccessUser(
                                    context, USER_ID, OTHER_TENANT_ID, OTHER_ORG_ID))
                    .isTrue();
        }

        @Test
        @DisplayName("GLOBAL 범위 사용자는 모든 사용자에 접근 가능")
        void globalScopeCanAccessAnyUser() {
            UserContext context = createContext(Scopes.GLOBAL, TENANT_ID, ORG_ID);

            assertThat(
                            ScopeValidator.canAccessUser(
                                    context, OTHER_USER_ID, OTHER_TENANT_ID, OTHER_ORG_ID))
                    .isTrue();
        }

        @Test
        @DisplayName("ORGANIZATION 범위 사용자는 같은 조직 사용자에만 접근 가능")
        void organizationScopeCanAccessUsersInSameOrganization() {
            UserContext context = createContext(Scopes.ORGANIZATION, TENANT_ID, ORG_ID);

            assertThat(ScopeValidator.canAccessUser(context, OTHER_USER_ID, TENANT_ID, ORG_ID))
                    .isTrue();
            assertThat(
                            ScopeValidator.canAccessUser(
                                    context, OTHER_USER_ID, TENANT_ID, OTHER_ORG_ID))
                    .isFalse();
        }

        @Test
        @DisplayName("null 입력 시 false 반환")
        void nullInputReturnsFalse() {
            UserContext context = createContext(Scopes.GLOBAL, TENANT_ID, ORG_ID);

            assertThat(ScopeValidator.canAccessUser(null, USER_ID, TENANT_ID, ORG_ID)).isFalse();
            assertThat(ScopeValidator.canAccessUser(context, null, TENANT_ID, ORG_ID)).isFalse();
        }
    }

    @Nested
    @DisplayName("hasSufficientScope")
    class HasSufficientScope {

        @Test
        @DisplayName("GLOBAL은 모든 범위를 포함")
        void globalIncludesAll() {
            assertThat(ScopeValidator.hasSufficientScope(Scopes.GLOBAL, Scopes.GLOBAL)).isTrue();
            assertThat(ScopeValidator.hasSufficientScope(Scopes.GLOBAL, Scopes.TENANT)).isTrue();
            assertThat(ScopeValidator.hasSufficientScope(Scopes.GLOBAL, Scopes.ORGANIZATION))
                    .isTrue();
        }

        @Test
        @DisplayName("TENANT은 ORGANIZATION만 포함")
        void tenantIncludesOrganization() {
            assertThat(ScopeValidator.hasSufficientScope(Scopes.TENANT, Scopes.GLOBAL)).isFalse();
            assertThat(ScopeValidator.hasSufficientScope(Scopes.TENANT, Scopes.TENANT)).isTrue();
            assertThat(ScopeValidator.hasSufficientScope(Scopes.TENANT, Scopes.ORGANIZATION))
                    .isTrue();
        }

        @Test
        @DisplayName("ORGANIZATION은 자기 자신만 포함")
        void organizationIncludesOnlySelf() {
            assertThat(ScopeValidator.hasSufficientScope(Scopes.ORGANIZATION, Scopes.GLOBAL))
                    .isFalse();
            assertThat(ScopeValidator.hasSufficientScope(Scopes.ORGANIZATION, Scopes.TENANT))
                    .isFalse();
            assertThat(ScopeValidator.hasSufficientScope(Scopes.ORGANIZATION, Scopes.ORGANIZATION))
                    .isTrue();
        }
    }

    @Nested
    @DisplayName("isGlobalScope / isTenantScopeOrHigher")
    class ScopeChecks {

        @Test
        @DisplayName("isGlobalScope는 GLOBAL인 경우만 true")
        void isGlobalScope() {
            assertThat(
                            ScopeValidator.isGlobalScope(
                                    createContext(Scopes.GLOBAL, TENANT_ID, ORG_ID)))
                    .isTrue();
            assertThat(
                            ScopeValidator.isGlobalScope(
                                    createContext(Scopes.TENANT, TENANT_ID, ORG_ID)))
                    .isFalse();
            assertThat(ScopeValidator.isGlobalScope(null)).isFalse();
        }

        @Test
        @DisplayName("isTenantScopeOrHigher는 TENANT 이상인 경우 true")
        void isTenantScopeOrHigher() {
            assertThat(
                            ScopeValidator.isTenantScopeOrHigher(
                                    createContext(Scopes.GLOBAL, TENANT_ID, ORG_ID)))
                    .isTrue();
            assertThat(
                            ScopeValidator.isTenantScopeOrHigher(
                                    createContext(Scopes.TENANT, TENANT_ID, ORG_ID)))
                    .isTrue();
            assertThat(
                            ScopeValidator.isTenantScopeOrHigher(
                                    createContext(Scopes.ORGANIZATION, TENANT_ID, ORG_ID)))
                    .isFalse();
            assertThat(ScopeValidator.isTenantScopeOrHigher(null)).isFalse();
        }
    }

    private UserContext createContext(String scope, String tenantId, String organizationId) {
        return UserContext.builder()
                .userId(USER_ID)
                .tenantId(tenantId)
                .organizationId(organizationId)
                .scope(scope)
                .roles(Set.of())
                .permissions(Set.of())
                .build();
    }
}
