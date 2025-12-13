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
 * ResourceAccessChecker 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("ResourceAccessChecker 단위 테스트")
class ResourceAccessCheckerTest {

    private ResourceAccessChecker checker;

    private static final String USER_ID = "01912f58-7b9c-7d4e-8b4a-6c5d4e3f2a1b";
    private static final String TENANT_ID = "01912f58-7b9c-7d4e-8b4a-6c5d4e3f2a1c";
    private static final String ORG_ID = "01912f58-7b9c-7d4e-8b4a-6c5d4e3f2a1d";
    private static final String OTHER_USER_ID = "01912f58-7b9c-7d4e-8b4a-000000000001";
    private static final String OTHER_TENANT_ID = "01912f58-7b9c-7d4e-8b4a-000000000002";
    private static final String OTHER_ORG_ID = "01912f58-7b9c-7d4e-8b4a-000000000003";

    @BeforeEach
    void setUp() {
        checker = new ResourceAccessChecker();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Nested
    @DisplayName("superAdmin() 테스트")
    class SuperAdminTest {

        @Test
        @DisplayName("SUPER_ADMIN 역할이 있으면 true를 반환한다")
        void superAdmin_withSuperAdminRole_shouldReturnTrue() {
            // given
            setSecurityContext(Role.SUPER_ADMIN);

            // when & then
            assertThat(checker.superAdmin()).isTrue();
        }

        @Test
        @DisplayName("SUPER_ADMIN 역할이 없으면 false를 반환한다")
        void superAdmin_withoutSuperAdminRole_shouldReturnFalse() {
            // given
            setSecurityContext(Role.TENANT_ADMIN);

            // when & then
            assertThat(checker.superAdmin()).isFalse();
        }
    }

    @Nested
    @DisplayName("myself() 테스트")
    class MyselfTest {

        @Test
        @DisplayName("현재 사용자와 동일한 userId면 true를 반환한다")
        void myself_withSameUserId_shouldReturnTrue() {
            // given
            setSecurityContext(Role.USER);

            // when & then
            assertThat(checker.myself(USER_ID)).isTrue();
        }

        @Test
        @DisplayName("현재 사용자와 다른 userId면 false를 반환한다")
        void myself_withDifferentUserId_shouldReturnFalse() {
            // given
            setSecurityContext(Role.USER);

            // when & then
            assertThat(checker.myself(OTHER_USER_ID)).isFalse();
        }

        @Test
        @DisplayName("익명 사용자면 false를 반환한다")
        void myself_whenAnonymous_shouldReturnFalse() {
            // given
            SecurityContextHolder.setContext(SecurityContext.anonymous());

            // when & then
            assertThat(checker.myself(USER_ID)).isFalse();
        }
    }

    @Nested
    @DisplayName("myselfOr() 테스트")
    class MyselfOrTest {

        @Test
        @DisplayName("본인이면 true를 반환한다")
        void myselfOr_whenMyself_shouldReturnTrue() {
            // given
            setSecurityContext(Role.USER);

            // when & then
            assertThat(checker.myselfOr(USER_ID, "user:read")).isTrue();
        }

        @Test
        @DisplayName("본인이 아니어도 권한이 있으면 true를 반환한다")
        void myselfOr_whenHasPermission_shouldReturnTrue() {
            // given
            setSecurityContextWithPermissions(Set.of("user:read"));

            // when & then
            assertThat(checker.myselfOr(OTHER_USER_ID, "user:read")).isTrue();
        }

        @Test
        @DisplayName("본인도 아니고 권한도 없으면 false를 반환한다")
        void myselfOr_whenNotMyselfAndNoPermission_shouldReturnFalse() {
            // given
            setSecurityContext(Role.USER);

            // when & then
            assertThat(checker.myselfOr(OTHER_USER_ID, "user:update")).isFalse();
        }
    }

    @Nested
    @DisplayName("hasPermission() 테스트")
    class HasPermissionTest {

        @Test
        @DisplayName("SUPER_ADMIN은 모든 권한을 가진다")
        void hasPermission_whenSuperAdmin_shouldReturnTrue() {
            // given
            setSecurityContext(Role.SUPER_ADMIN);

            // when & then
            assertThat(checker.hasPermission("any:permission")).isTrue();
        }

        @Test
        @DisplayName("해당 권한이 있으면 true를 반환한다")
        void hasPermission_whenHasPermission_shouldReturnTrue() {
            // given
            setSecurityContextWithPermissions(Set.of("user:read", "user:update"));

            // when & then
            assertThat(checker.hasPermission("user:read")).isTrue();
        }

        @Test
        @DisplayName("해당 권한이 없으면 false를 반환한다")
        void hasPermission_whenNoPermission_shouldReturnFalse() {
            // given
            setSecurityContextWithPermissions(Set.of("user:read"));

            // when & then
            assertThat(checker.hasPermission("user:delete")).isFalse();
        }
    }

    @Nested
    @DisplayName("sameTenant() 테스트")
    class SameTenantTest {

        @Test
        @DisplayName("SUPER_ADMIN은 모든 테넌트에 접근 가능하다")
        void sameTenant_whenSuperAdmin_shouldReturnTrue() {
            // given
            setSecurityContext(Role.SUPER_ADMIN);

            // when & then
            assertThat(checker.sameTenant(OTHER_TENANT_ID)).isTrue();
        }

        @Test
        @DisplayName("같은 테넌트면 true를 반환한다")
        void sameTenant_whenSameTenant_shouldReturnTrue() {
            // given
            setSecurityContext(Role.TENANT_ADMIN);

            // when & then
            assertThat(checker.sameTenant(TENANT_ID)).isTrue();
        }

        @Test
        @DisplayName("다른 테넌트면 false를 반환한다")
        void sameTenant_whenDifferentTenant_shouldReturnFalse() {
            // given
            setSecurityContext(Role.TENANT_ADMIN);

            // when & then
            assertThat(checker.sameTenant(OTHER_TENANT_ID)).isFalse();
        }
    }

    @Nested
    @DisplayName("sameOrganization() 테스트")
    class SameOrganizationTest {

        @Test
        @DisplayName("SUPER_ADMIN은 모든 조직에 접근 가능하다")
        void sameOrganization_whenSuperAdmin_shouldReturnTrue() {
            // given
            setSecurityContext(Role.SUPER_ADMIN);

            // when & then
            assertThat(checker.sameOrganization(OTHER_ORG_ID)).isTrue();
        }

        @Test
        @DisplayName("TENANT_ADMIN은 자기 테넌트 내 모든 조직에 접근 가능하다")
        void sameOrganization_whenTenantAdmin_shouldReturnTrue() {
            // given
            setSecurityContext(Role.TENANT_ADMIN);

            // when & then
            assertThat(checker.sameOrganization(OTHER_ORG_ID)).isTrue();
        }

        @Test
        @DisplayName("같은 조직이면 true를 반환한다")
        void sameOrganization_whenSameOrg_shouldReturnTrue() {
            // given
            setSecurityContext(Role.USER);

            // when & then
            assertThat(checker.sameOrganization(ORG_ID)).isTrue();
        }

        @Test
        @DisplayName("다른 조직이면 false를 반환한다")
        void sameOrganization_whenDifferentOrg_shouldReturnFalse() {
            // given
            setSecurityContext(Role.USER);

            // when & then
            assertThat(checker.sameOrganization(OTHER_ORG_ID)).isFalse();
        }
    }

    @Nested
    @DisplayName("user() 테스트")
    class UserAccessTest {

        @Test
        @DisplayName("SUPER_ADMIN은 모든 사용자에 접근 가능하다")
        void user_whenSuperAdmin_shouldReturnTrue() {
            // given
            setSecurityContext(Role.SUPER_ADMIN);

            // when & then
            assertThat(checker.user(OTHER_USER_ID, "delete")).isTrue();
        }

        @Test
        @DisplayName("본인은 read, update 가능하다")
        void user_whenMyselfAndReadOrUpdate_shouldReturnTrue() {
            // given
            setSecurityContext(Role.USER);

            // when & then
            assertThat(checker.user(USER_ID, "read")).isTrue();
            assertThat(checker.user(USER_ID, "update")).isTrue();
        }

        @Test
        @DisplayName("본인이라도 delete는 권한 필요하다")
        void user_whenMyselfButDelete_shouldCheckPermission() {
            // given
            setSecurityContext(Role.USER);

            // when & then
            assertThat(checker.user(USER_ID, "delete")).isFalse();
        }

        @Test
        @DisplayName("user:read 권한이 있으면 다른 사용자도 조회 가능하다")
        void user_whenHasPermission_shouldReturnTrue() {
            // given
            setSecurityContextWithPermissions(Set.of("user:read"));

            // when & then
            assertThat(checker.user(OTHER_USER_ID, "read")).isTrue();
        }
    }

    @Nested
    @DisplayName("permission() 테스트")
    class PermissionAccessTest {

        @Test
        @DisplayName("SUPER_ADMIN은 permission 관리 가능하다")
        void permission_whenSuperAdmin_shouldReturnTrue() {
            // given
            setSecurityContext(Role.SUPER_ADMIN);

            // when & then
            assertThat(checker.permission("perm-id", "create")).isTrue();
            assertThat(checker.permission("perm-id", "delete")).isTrue();
        }

        @Test
        @DisplayName("인증된 사용자는 permission 조회 가능하다")
        void permission_whenAuthenticatedAndRead_shouldReturnTrue() {
            // given
            setSecurityContext(Role.USER);

            // when & then
            assertThat(checker.permission("perm-id", "read")).isTrue();
        }

        @Test
        @DisplayName("일반 사용자는 permission 생성/수정/삭제 불가하다")
        void permission_whenNotSuperAdminAndWrite_shouldReturnFalse() {
            // given
            setSecurityContext(Role.TENANT_ADMIN);

            // when & then
            assertThat(checker.permission("perm-id", "create")).isFalse();
            assertThat(checker.permission("perm-id", "update")).isFalse();
            assertThat(checker.permission("perm-id", "delete")).isFalse();
        }
    }

    @Nested
    @DisplayName("authenticated() 테스트")
    class AuthenticatedTest {

        @Test
        @DisplayName("인증된 사용자면 true를 반환한다")
        void authenticated_whenAuthenticated_shouldReturnTrue() {
            // given
            setSecurityContext(Role.USER);

            // when & then
            assertThat(checker.authenticated()).isTrue();
        }

        @Test
        @DisplayName("익명 사용자면 false를 반환한다")
        void authenticated_whenAnonymous_shouldReturnFalse() {
            // given
            SecurityContextHolder.setContext(SecurityContext.anonymous());

            // when & then
            assertThat(checker.authenticated()).isFalse();
        }
    }

    private void setSecurityContext(String role) {
        SecurityContext context =
                SecurityContext.builder()
                        .userId(USER_ID)
                        .tenantId(TENANT_ID)
                        .organizationId(ORG_ID)
                        .roles(Set.of(role))
                        .permissions(Set.of())
                        .build();
        SecurityContextHolder.setContext(context);
    }

    private void setSecurityContextWithPermissions(Set<String> permissions) {
        SecurityContext context =
                SecurityContext.builder()
                        .userId(USER_ID)
                        .tenantId(TENANT_ID)
                        .organizationId(ORG_ID)
                        .roles(Set.of(Role.USER))
                        .permissions(permissions)
                        .build();
        SecurityContextHolder.setContext(context);
    }
}
