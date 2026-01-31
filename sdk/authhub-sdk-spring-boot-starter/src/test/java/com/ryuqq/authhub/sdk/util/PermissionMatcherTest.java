package com.ryuqq.authhub.sdk.util;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.sdk.constant.Permissions;
import com.ryuqq.authhub.sdk.context.UserContext;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("PermissionMatcher")
class PermissionMatcherTest {

    @Nested
    @DisplayName("hasPermission")
    class HasPermission {

        @Test
        @DisplayName("정확히 일치하는 권한 보유 시 true")
        void exactMatchReturnsTrue() {
            UserContext context = createContext(Set.of(Permissions.USER_READ));

            assertThat(PermissionMatcher.hasPermission(context, Permissions.USER_READ)).isTrue();
        }

        @Test
        @DisplayName("권한이 없으면 false")
        void noPermissionReturnsFalse() {
            UserContext context = createContext(Set.of(Permissions.USER_READ));

            assertThat(PermissionMatcher.hasPermission(context, Permissions.USER_WRITE)).isFalse();
        }

        @Test
        @DisplayName("*:* 권한은 모든 권한 포함")
        void wildcardAllIncludesEverything() {
            UserContext context = createContext(Set.of(Permissions.ALL));

            assertThat(PermissionMatcher.hasPermission(context, Permissions.USER_READ)).isTrue();
            assertThat(PermissionMatcher.hasPermission(context, Permissions.ROLE_WRITE)).isTrue();
            assertThat(PermissionMatcher.hasPermission(context, "any:permission")).isTrue();
        }

        @Test
        @DisplayName("domain:* 권한은 해당 도메인의 모든 액션 포함")
        void wildcardActionIncludesAllActionsInDomain() {
            UserContext context = createContext(Set.of("user:*"));

            assertThat(PermissionMatcher.hasPermission(context, Permissions.USER_READ)).isTrue();
            assertThat(PermissionMatcher.hasPermission(context, Permissions.USER_WRITE)).isTrue();
            assertThat(PermissionMatcher.hasPermission(context, Permissions.USER_DELETE)).isTrue();
            assertThat(PermissionMatcher.hasPermission(context, Permissions.ROLE_READ)).isFalse();
        }

        @Test
        @DisplayName("*:action 권한은 모든 도메인의 해당 액션 포함")
        void wildcardDomainIncludesAllDomainsForAction() {
            UserContext context = createContext(Set.of("*:read"));

            assertThat(PermissionMatcher.hasPermission(context, Permissions.USER_READ)).isTrue();
            assertThat(PermissionMatcher.hasPermission(context, Permissions.ROLE_READ)).isTrue();
            assertThat(PermissionMatcher.hasPermission(context, Permissions.USER_WRITE)).isFalse();
        }

        @Test
        @DisplayName("null 입력 시 false 반환")
        void nullInputReturnsFalse() {
            UserContext context = createContext(Set.of(Permissions.USER_READ));

            assertThat(PermissionMatcher.hasPermission((UserContext) null, Permissions.USER_READ))
                    .isFalse();
            assertThat(PermissionMatcher.hasPermission(context, null)).isFalse();
        }

        @Test
        @DisplayName("빈 권한 집합은 false 반환")
        void emptyPermissionsReturnFalse() {
            UserContext context = createContext(Set.of());

            assertThat(PermissionMatcher.hasPermission(context, Permissions.USER_READ)).isFalse();
        }
    }

    @Nested
    @DisplayName("hasAllPermissions")
    class HasAllPermissions {

        @Test
        @DisplayName("모든 필요 권한 보유 시 true")
        void hasAllReturnsTrueWhenAllPresent() {
            UserContext context =
                    createContext(Set.of(Permissions.USER_READ, Permissions.USER_WRITE));

            assertThat(
                            PermissionMatcher.hasAllPermissions(
                                    context,
                                    List.of(Permissions.USER_READ, Permissions.USER_WRITE)))
                    .isTrue();
        }

        @Test
        @DisplayName("하나라도 없으면 false")
        void hasAllReturnsFalseWhenMissing() {
            UserContext context = createContext(Set.of(Permissions.USER_READ));

            assertThat(
                            PermissionMatcher.hasAllPermissions(
                                    context,
                                    List.of(Permissions.USER_READ, Permissions.USER_WRITE)))
                    .isFalse();
        }

        @Test
        @DisplayName("*:* 권한은 모든 권한 충족")
        void wildcardAllSatisfiesAll() {
            UserContext context = createContext(Set.of(Permissions.ALL));

            assertThat(
                            PermissionMatcher.hasAllPermissions(
                                    context,
                                    List.of(
                                            Permissions.USER_READ,
                                            Permissions.ROLE_WRITE,
                                            Permissions.ORGANIZATION_DELETE)))
                    .isTrue();
        }
    }

    @Nested
    @DisplayName("hasAnyPermission")
    class HasAnyPermission {

        @Test
        @DisplayName("하나라도 보유 시 true")
        void hasAnyReturnsTrueWhenOnePresent() {
            UserContext context = createContext(Set.of(Permissions.USER_READ));

            assertThat(
                            PermissionMatcher.hasAnyPermission(
                                    context,
                                    List.of(Permissions.USER_READ, Permissions.ROLE_WRITE)))
                    .isTrue();
        }

        @Test
        @DisplayName("아무것도 없으면 false")
        void hasAnyReturnsFalseWhenNonePresent() {
            UserContext context = createContext(Set.of(Permissions.PRODUCT_READ));

            assertThat(
                            PermissionMatcher.hasAnyPermission(
                                    context,
                                    List.of(Permissions.USER_READ, Permissions.ROLE_WRITE)))
                    .isFalse();
        }

        @Test
        @DisplayName("빈 목록은 false 반환")
        void emptyRequiredReturnsFalse() {
            UserContext context = createContext(Set.of(Permissions.USER_READ));

            assertThat(PermissionMatcher.hasAnyPermission(context, List.of())).isFalse();
        }
    }

    @Nested
    @DisplayName("hasDomainPermission")
    class HasDomainPermission {

        @Test
        @DisplayName("해당 도메인 권한 보유 시 true")
        void returnsTrueWhenHasDomainPermission() {
            UserContext context =
                    createContext(Set.of(Permissions.USER_READ, Permissions.USER_WRITE));

            assertThat(PermissionMatcher.hasDomainPermission(context, "user")).isTrue();
        }

        @Test
        @DisplayName("해당 도메인 권한 미보유 시 false")
        void returnsFalseWhenNoDomainPermission() {
            UserContext context = createContext(Set.of(Permissions.USER_READ));

            assertThat(PermissionMatcher.hasDomainPermission(context, "role")).isFalse();
        }

        @Test
        @DisplayName("*:* 권한은 모든 도메인 포함")
        void wildcardAllIncludesAllDomains() {
            UserContext context = createContext(Set.of(Permissions.ALL));

            assertThat(PermissionMatcher.hasDomainPermission(context, "user")).isTrue();
            assertThat(PermissionMatcher.hasDomainPermission(context, "anything")).isTrue();
        }

        @Test
        @DisplayName("*:action 권한은 모든 도메인 포함")
        void wildcardDomainIncludesAllDomains() {
            UserContext context = createContext(Set.of("*:read"));

            assertThat(PermissionMatcher.hasDomainPermission(context, "user")).isTrue();
            assertThat(PermissionMatcher.hasDomainPermission(context, "role")).isTrue();
        }
    }

    @Nested
    @DisplayName("hasActionPermission")
    class HasActionPermission {

        @Test
        @DisplayName("해당 액션 권한 보유 시 true")
        void returnsTrueWhenHasActionPermission() {
            UserContext context =
                    createContext(Set.of(Permissions.USER_READ, Permissions.ROLE_READ));

            assertThat(PermissionMatcher.hasActionPermission(context, "read")).isTrue();
        }

        @Test
        @DisplayName("해당 액션 권한 미보유 시 false")
        void returnsFalseWhenNoActionPermission() {
            UserContext context = createContext(Set.of(Permissions.USER_READ));

            assertThat(PermissionMatcher.hasActionPermission(context, "delete")).isFalse();
        }

        @Test
        @DisplayName("domain:* 권한은 모든 액션 포함")
        void wildcardActionIncludesAllActions() {
            UserContext context = createContext(Set.of("user:*"));

            assertThat(PermissionMatcher.hasActionPermission(context, "read")).isTrue();
            assertThat(PermissionMatcher.hasActionPermission(context, "write")).isTrue();
            assertThat(PermissionMatcher.hasActionPermission(context, "delete")).isTrue();
        }
    }

    private UserContext createContext(Set<String> permissions) {
        return UserContext.builder()
                .userId("user-123")
                .tenantId("tenant-123")
                .organizationId("org-123")
                .permissions(permissions)
                .roles(Set.of())
                .build();
    }
}
