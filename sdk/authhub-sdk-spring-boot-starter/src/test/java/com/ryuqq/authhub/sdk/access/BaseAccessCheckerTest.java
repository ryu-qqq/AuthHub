package com.ryuqq.authhub.sdk.access;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.sdk.constant.Roles;
import com.ryuqq.authhub.sdk.context.UserContext;
import com.ryuqq.authhub.sdk.context.UserContextHolder;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("BaseAccessChecker")
class BaseAccessCheckerTest {

    private TestAccessChecker accessChecker;

    @BeforeEach
    void setUp() {
        accessChecker = new TestAccessChecker();
    }

    @AfterEach
    void tearDown() {
        UserContextHolder.clearContext();
    }

    /** 테스트용 구현체 */
    static class TestAccessChecker extends BaseAccessChecker {}

    @Nested
    @DisplayName("authenticated")
    class Authenticated {

        @Test
        @DisplayName("인증된 사용자")
        void authenticatedUser() {
            UserContextHolder.setContext(UserContext.builder().userId("user-123").build());

            assertThat(accessChecker.authenticated()).isTrue();
        }

        @Test
        @DisplayName("미인증 사용자")
        void unauthenticatedUser() {
            assertThat(accessChecker.authenticated()).isFalse();
        }
    }

    @Nested
    @DisplayName("superAdmin")
    class SuperAdmin {

        @Test
        @DisplayName("SUPER_ADMIN 역할")
        void isSuperAdmin() {
            UserContextHolder.setContext(
                    UserContext.builder().roles(Set.of(Roles.SUPER_ADMIN)).build());

            assertThat(accessChecker.superAdmin()).isTrue();
        }

        @Test
        @DisplayName("일반 사용자")
        void isNotSuperAdmin() {
            UserContextHolder.setContext(UserContext.builder().roles(Set.of(Roles.USER)).build());

            assertThat(accessChecker.superAdmin()).isFalse();
        }
    }

    @Nested
    @DisplayName("admin")
    class Admin {

        @Test
        @DisplayName("SUPER_ADMIN은 admin")
        void superAdminIsAdmin() {
            UserContextHolder.setContext(
                    UserContext.builder().roles(Set.of(Roles.SUPER_ADMIN)).build());

            assertThat(accessChecker.admin()).isTrue();
        }

        @Test
        @DisplayName("TENANT_ADMIN은 admin")
        void tenantAdminIsAdmin() {
            UserContextHolder.setContext(
                    UserContext.builder().roles(Set.of(Roles.TENANT_ADMIN)).build());

            assertThat(accessChecker.admin()).isTrue();
        }

        @Test
        @DisplayName("ORG_ADMIN은 admin")
        void orgAdminIsAdmin() {
            UserContextHolder.setContext(
                    UserContext.builder().roles(Set.of(Roles.ORG_ADMIN)).build());

            assertThat(accessChecker.admin()).isTrue();
        }

        @Test
        @DisplayName("일반 USER는 admin 아님")
        void userIsNotAdmin() {
            UserContextHolder.setContext(UserContext.builder().roles(Set.of(Roles.USER)).build());

            assertThat(accessChecker.admin()).isFalse();
        }
    }

    @Nested
    @DisplayName("hasRole")
    class HasRole {

        @Test
        @DisplayName("역할 보유")
        void hasRole() {
            UserContextHolder.setContext(
                    UserContext.builder().roles(Set.of("ROLE_MANAGER")).build());

            assertThat(accessChecker.hasRole("ROLE_MANAGER")).isTrue();
        }

        @Test
        @DisplayName("역할 미보유")
        void doesNotHaveRole() {
            UserContextHolder.setContext(UserContext.builder().roles(Set.of(Roles.USER)).build());

            assertThat(accessChecker.hasRole("ROLE_MANAGER")).isFalse();
        }
    }

    @Nested
    @DisplayName("hasAnyRole")
    class HasAnyRole {

        @Test
        @DisplayName("여러 역할 중 하나 보유")
        void hasOneOfRoles() {
            UserContextHolder.setContext(UserContext.builder().roles(Set.of("ROLE_B")).build());

            assertThat(accessChecker.hasAnyRole("ROLE_A", "ROLE_B", "ROLE_C")).isTrue();
        }

        @Test
        @DisplayName("역할 미보유")
        void hasNoneOfRoles() {
            UserContextHolder.setContext(UserContext.builder().roles(Set.of("ROLE_X")).build());

            assertThat(accessChecker.hasAnyRole("ROLE_A", "ROLE_B")).isFalse();
        }

        @Test
        @DisplayName("null 또는 빈 배열")
        void handlesNullOrEmpty() {
            assertThat(accessChecker.hasAnyRole((String[]) null)).isFalse();
            assertThat(accessChecker.hasAnyRole()).isFalse();
        }
    }

    @Nested
    @DisplayName("hasPermission")
    class HasPermission {

        @Test
        @DisplayName("권한 보유")
        void hasPermission() {
            UserContextHolder.setContext(
                    UserContext.builder().permissions(Set.of("user:read")).build());

            assertThat(accessChecker.hasPermission("user:read")).isTrue();
        }

        @Test
        @DisplayName("권한 미보유")
        void doesNotHavePermission() {
            UserContextHolder.setContext(
                    UserContext.builder().permissions(Set.of("user:read")).build());

            assertThat(accessChecker.hasPermission("user:write")).isFalse();
        }

        @Test
        @DisplayName("SUPER_ADMIN은 모든 권한 보유")
        void superAdminHasAllPermissions() {
            UserContextHolder.setContext(
                    UserContext.builder().roles(Set.of(Roles.SUPER_ADMIN)).build());

            assertThat(accessChecker.hasPermission("any:permission")).isTrue();
        }
    }

    @Nested
    @DisplayName("hasAnyPermission")
    class HasAnyPermission {

        @Test
        @DisplayName("여러 권한 중 하나 보유")
        void hasOneOfPermissions() {
            UserContextHolder.setContext(
                    UserContext.builder().permissions(Set.of("user:read")).build());

            assertThat(accessChecker.hasAnyPermission("user:read", "user:write")).isTrue();
        }

        @Test
        @DisplayName("권한 미보유")
        void hasNoneOfPermissions() {
            UserContextHolder.setContext(
                    UserContext.builder().permissions(Set.of("product:read")).build());

            assertThat(accessChecker.hasAnyPermission("user:read", "user:write")).isFalse();
        }

        @Test
        @DisplayName("SUPER_ADMIN은 모든 권한 보유")
        void superAdminHasAllPermissions() {
            UserContextHolder.setContext(
                    UserContext.builder().roles(Set.of(Roles.SUPER_ADMIN)).build());

            assertThat(accessChecker.hasAnyPermission("any:permission")).isTrue();
        }
    }

    @Nested
    @DisplayName("hasAllPermissions")
    class HasAllPermissions {

        @Test
        @DisplayName("모든 권한 보유")
        void hasAllPermissions() {
            UserContextHolder.setContext(
                    UserContext.builder()
                            .permissions(Set.of("user:read", "user:write", "user:delete"))
                            .build());

            assertThat(accessChecker.hasAllPermissions("user:read", "user:write")).isTrue();
        }

        @Test
        @DisplayName("일부 권한 미보유")
        void missingOnePermission() {
            UserContextHolder.setContext(
                    UserContext.builder().permissions(Set.of("user:read")).build());

            assertThat(accessChecker.hasAllPermissions("user:read", "user:write")).isFalse();
        }

        @Test
        @DisplayName("빈 배열은 true")
        void emptyPermissionsReturnsTrue() {
            assertThat(accessChecker.hasAllPermissions()).isTrue();
        }
    }

    @Nested
    @DisplayName("sameTenant")
    class SameTenant {

        @Test
        @DisplayName("같은 테넌트")
        void sameTenant() {
            UserContextHolder.setContext(UserContext.builder().tenantId("tenant-123").build());

            assertThat(accessChecker.sameTenant("tenant-123")).isTrue();
        }

        @Test
        @DisplayName("다른 테넌트")
        void differentTenant() {
            UserContextHolder.setContext(UserContext.builder().tenantId("tenant-123").build());

            assertThat(accessChecker.sameTenant("tenant-456")).isFalse();
        }

        @Test
        @DisplayName("SUPER_ADMIN은 모든 테넌트 접근")
        void superAdminAccessesAllTenants() {
            UserContextHolder.setContext(
                    UserContext.builder().roles(Set.of(Roles.SUPER_ADMIN)).build());

            assertThat(accessChecker.sameTenant("any-tenant")).isTrue();
        }

        @Test
        @DisplayName("null tenantId")
        void nullTenantId() {
            assertThat(accessChecker.sameTenant(null)).isFalse();
        }
    }

    @Nested
    @DisplayName("sameOrganization")
    class SameOrganization {

        @Test
        @DisplayName("같은 조직")
        void sameOrganization() {
            UserContextHolder.setContext(UserContext.builder().organizationId("org-123").build());

            assertThat(accessChecker.sameOrganization("org-123")).isTrue();
        }

        @Test
        @DisplayName("다른 조직")
        void differentOrganization() {
            UserContextHolder.setContext(UserContext.builder().organizationId("org-123").build());

            assertThat(accessChecker.sameOrganization("org-456")).isFalse();
        }

        @Test
        @DisplayName("SUPER_ADMIN은 모든 조직 접근")
        void superAdminAccessesAllOrgs() {
            UserContextHolder.setContext(
                    UserContext.builder().roles(Set.of(Roles.SUPER_ADMIN)).build());

            assertThat(accessChecker.sameOrganization("any-org")).isTrue();
        }

        @Test
        @DisplayName("TENANT_ADMIN은 모든 조직 접근")
        void tenantAdminAccessesAllOrgs() {
            UserContextHolder.setContext(
                    UserContext.builder().roles(Set.of(Roles.TENANT_ADMIN)).build());

            assertThat(accessChecker.sameOrganization("any-org")).isTrue();
        }
    }

    @Nested
    @DisplayName("myself")
    class Myself {

        @Test
        @DisplayName("본인")
        void isMyself() {
            UserContextHolder.setContext(UserContext.builder().userId("user-123").build());

            assertThat(accessChecker.myself("user-123")).isTrue();
        }

        @Test
        @DisplayName("다른 사용자")
        void isNotMyself() {
            UserContextHolder.setContext(UserContext.builder().userId("user-123").build());

            assertThat(accessChecker.myself("user-456")).isFalse();
        }

        @Test
        @DisplayName("null userId")
        void nullUserId() {
            assertThat(accessChecker.myself(null)).isFalse();
        }
    }

    @Nested
    @DisplayName("myselfOr")
    class MyselfOr {

        @Test
        @DisplayName("본인이면 true")
        void isMyselfReturnsTrue() {
            UserContextHolder.setContext(UserContext.builder().userId("user-123").build());

            assertThat(accessChecker.myselfOr("user-123", "user:read")).isTrue();
        }

        @Test
        @DisplayName("권한 있으면 true")
        void hasPermissionReturnsTrue() {
            UserContextHolder.setContext(
                    UserContext.builder()
                            .userId("user-123")
                            .permissions(Set.of("user:read"))
                            .build());

            assertThat(accessChecker.myselfOr("user-456", "user:read")).isTrue();
        }

        @Test
        @DisplayName("본인 아니고 권한 없으면 false")
        void notMyselfAndNoPermission() {
            UserContextHolder.setContext(UserContext.builder().userId("user-123").build());

            assertThat(accessChecker.myselfOr("user-456", "user:read")).isFalse();
        }
    }

    @Nested
    @DisplayName("serviceAccount")
    class ServiceAccount {

        @Test
        @DisplayName("서비스 계정")
        void isServiceAccount() {
            UserContextHolder.setContext(UserContext.builder().serviceAccount(true).build());

            assertThat(accessChecker.serviceAccount()).isTrue();
        }

        @Test
        @DisplayName("일반 사용자")
        void isNotServiceAccount() {
            UserContextHolder.setContext(UserContext.builder().serviceAccount(false).build());

            assertThat(accessChecker.serviceAccount()).isFalse();
        }
    }

    @Nested
    @DisplayName("헬퍼 메서드")
    class HelperMethods {

        @Test
        @DisplayName("getCurrentUserId")
        void getCurrentUserId() {
            UserContextHolder.setContext(UserContext.builder().userId("user-123").build());

            assertThat(accessChecker.getCurrentUserId()).isEqualTo("user-123");
        }

        @Test
        @DisplayName("getCurrentTenantId")
        void getCurrentTenantId() {
            UserContextHolder.setContext(UserContext.builder().tenantId("tenant-456").build());

            assertThat(accessChecker.getCurrentTenantId()).isEqualTo("tenant-456");
        }

        @Test
        @DisplayName("getCurrentOrganizationId")
        void getCurrentOrganizationId() {
            UserContextHolder.setContext(UserContext.builder().organizationId("org-789").build());

            assertThat(accessChecker.getCurrentOrganizationId()).isEqualTo("org-789");
        }
    }
}
