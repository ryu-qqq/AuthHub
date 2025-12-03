package com.ryuqq.authhub.domain.role.aggregate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.authhub.domain.role.identifier.RoleId;
import com.ryuqq.authhub.domain.role.vo.PermissionCode;
import com.ryuqq.authhub.domain.role.vo.RoleName;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Role Aggregate 테스트")
class RoleTest {

    @Nested
    @DisplayName("forNew() 메서드는")
    class ForNewMethod {

        @Test
        @DisplayName("새로운 Role을 생성할 수 있다")
        void shouldCreateNewRole() {
            TenantId tenantId = TenantId.of(1L);
            RoleName roleName = RoleName.of("ROLE_ADMIN");

            Role role = Role.forNew(tenantId, roleName, "관리자 역할", false);

            assertThat(role.isNew()).isTrue();
            assertThat(role.getTenantId()).isEqualTo(tenantId);
            assertThat(role.getName()).isEqualTo(roleName);
            assertThat(role.getDescription()).isEqualTo("관리자 역할");
            assertThat(role.isSystem()).isFalse();
            assertThat(role.getPermissions()).isEmpty();
        }

        @Test
        @DisplayName("시스템 전역 역할을 생성할 수 있다 (tenantId null)")
        void shouldCreateGlobalRole() {
            RoleName roleName = RoleName.of("ROLE_SUPER_ADMIN");

            Role role = Role.forNew(null, roleName, "수퍼 관리자", true);

            assertThat(role.isGlobalRole()).isTrue();
            assertThat(role.isSystem()).isTrue();
        }

        @Test
        @DisplayName("RoleName이 null이면 예외를 던진다")
        void shouldThrowExceptionForNullRoleName() {
            assertThatThrownBy(() -> Role.forNew(TenantId.of(1L), null, "설명", false))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("RoleName은 null");
        }
    }

    @Nested
    @DisplayName("reconstitute() 메서드는")
    class ReconstituteMethod {

        @Test
        @DisplayName("DB에서 Role을 재구성할 수 있다")
        void shouldReconstituteRole() {
            RoleId roleId = RoleId.of(1L);
            TenantId tenantId = TenantId.of(1L);
            RoleName roleName = RoleName.of("ROLE_USER");
            Set<PermissionCode> permissions =
                    Set.of(PermissionCode.of("user:read"), PermissionCode.of("user:write"));

            Role role = Role.reconstitute(roleId, tenantId, roleName, "사용자 역할", false, permissions);

            assertThat(role.isNew()).isFalse();
            assertThat(role.getRoleId()).isEqualTo(roleId);
            assertThat(role.getPermissions()).hasSize(2);
        }

        @Test
        @DisplayName("roleId가 null이면 예외를 던진다")
        void shouldThrowExceptionForNullRoleId() {
            assertThatThrownBy(
                            () ->
                                    Role.reconstitute(
                                            null,
                                            TenantId.of(1L),
                                            RoleName.of("ROLE_USER"),
                                            "설명",
                                            false,
                                            Set.of()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("non-null roleId");
        }
    }

    @Nested
    @DisplayName("addPermission() 메서드는")
    class AddPermissionMethod {

        @Test
        @DisplayName("Permission을 추가할 수 있다")
        void shouldAddPermission() {
            Role role =
                    Role.of(
                            RoleId.of(1L),
                            TenantId.of(1L),
                            RoleName.of("ROLE_USER"),
                            "사용자",
                            false,
                            Set.of());

            Role updatedRole = role.addPermission(PermissionCode.of("user:read"));

            assertThat(updatedRole.getPermissions()).hasSize(1);
            assertThat(role.getPermissions()).isEmpty(); // 불변성 확인
        }

        @Test
        @DisplayName("시스템 역할에는 Permission을 추가할 수 없다")
        void shouldThrowExceptionForSystemRole() {
            Role systemRole =
                    Role.of(
                            RoleId.of(1L),
                            null,
                            RoleName.of("ROLE_SUPER_ADMIN"),
                            "수퍼 관리자",
                            true,
                            Set.of());

            assertThatThrownBy(() -> systemRole.addPermission(PermissionCode.of("user:read")))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("시스템 역할은 수정");
        }
    }

    @Nested
    @DisplayName("removePermission() 메서드는")
    class RemovePermissionMethod {

        @Test
        @DisplayName("Permission을 제거할 수 있다")
        void shouldRemovePermission() {
            Role role =
                    Role.of(
                            RoleId.of(1L),
                            TenantId.of(1L),
                            RoleName.of("ROLE_USER"),
                            "사용자",
                            false,
                            Set.of(
                                    PermissionCode.of("user:read"),
                                    PermissionCode.of("user:write")));

            Role updatedRole = role.removePermission(PermissionCode.of("user:read"));

            assertThat(updatedRole.getPermissions()).hasSize(1);
            assertThat(role.getPermissions()).hasSize(2); // 불변성 확인
        }

        @Test
        @DisplayName("시스템 역할에서는 Permission을 제거할 수 없다")
        void shouldThrowExceptionForSystemRole() {
            Role systemRole =
                    Role.of(
                            RoleId.of(1L),
                            null,
                            RoleName.of("ROLE_SUPER_ADMIN"),
                            "수퍼 관리자",
                            true,
                            Set.of(PermissionCode.of("user:read")));

            assertThatThrownBy(() -> systemRole.removePermission(PermissionCode.of("user:read")))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("시스템 역할은 수정");
        }
    }

    @Nested
    @DisplayName("hasPermission() 메서드는")
    class HasPermissionMethod {

        @Test
        @DisplayName("정확히 일치하는 Permission을 확인할 수 있다")
        void shouldCheckExactPermission() {
            Role role =
                    Role.of(
                            RoleId.of(1L),
                            TenantId.of(1L),
                            RoleName.of("ROLE_USER"),
                            "사용자",
                            false,
                            Set.of(PermissionCode.of("user:read")));

            assertThat(role.hasPermission(PermissionCode.of("user:read"))).isTrue();
            assertThat(role.hasPermission(PermissionCode.of("user:write"))).isFalse();
        }

        @Test
        @DisplayName("와일드카드 Permission을 확인할 수 있다")
        void shouldCheckWildcardPermission() {
            Role role =
                    Role.of(
                            RoleId.of(1L),
                            TenantId.of(1L),
                            RoleName.of("ROLE_ADMIN"),
                            "관리자",
                            false,
                            Set.of(PermissionCode.of("user:*")));

            assertThat(role.hasPermission(PermissionCode.of("user:read"))).isTrue();
            assertThat(role.hasPermission(PermissionCode.of("user:write"))).isTrue();
            assertThat(role.hasPermission(PermissionCode.of("organization:read"))).isFalse();
        }
    }

    @Nested
    @DisplayName("equals() 메서드는")
    class EqualsMethod {

        @Test
        @DisplayName("같은 roleId를 가진 Role은 동등하다")
        void shouldBeEqualForSameRoleId() {
            Role role1 =
                    Role.of(
                            RoleId.of(1L),
                            TenantId.of(1L),
                            RoleName.of("ROLE_ADMIN"),
                            "관리자",
                            false,
                            Set.of());
            Role role2 =
                    Role.of(
                            RoleId.of(1L),
                            TenantId.of(2L),
                            RoleName.of("ROLE_USER"),
                            "사용자",
                            false,
                            Set.of());

            assertThat(role1).isEqualTo(role2);
            assertThat(role1.hashCode()).isEqualTo(role2.hashCode());
        }

        @Test
        @DisplayName("roleId가 null인 경우 name과 tenantId로 비교한다")
        void shouldCompareByNameAndTenantIdWhenRoleIdIsNull() {
            Role role1 = Role.forNew(TenantId.of(1L), RoleName.of("ROLE_ADMIN"), "관리자1", false);
            Role role2 = Role.forNew(TenantId.of(1L), RoleName.of("ROLE_ADMIN"), "관리자2", false);

            assertThat(role1).isEqualTo(role2);
        }

        @Test
        @DisplayName("다른 roleId를 가진 Role은 동등하지 않다")
        void shouldNotBeEqualForDifferentRoleId() {
            Role role1 =
                    Role.of(
                            RoleId.of(1L),
                            TenantId.of(1L),
                            RoleName.of("ROLE_ADMIN"),
                            "관리자",
                            false,
                            Set.of());
            Role role2 =
                    Role.of(
                            RoleId.of(2L),
                            TenantId.of(1L),
                            RoleName.of("ROLE_ADMIN"),
                            "관리자",
                            false,
                            Set.of());

            assertThat(role1).isNotEqualTo(role2);
        }
    }

    @Nested
    @DisplayName("Helper 메서드는")
    class HelperMethods {

        @Test
        @DisplayName("roleIdValue()는 roleId의 값을 반환한다")
        void shouldReturnRoleIdValue() {
            Role role =
                    Role.of(
                            RoleId.of(1L),
                            TenantId.of(1L),
                            RoleName.of("ROLE_ADMIN"),
                            "관리자",
                            false,
                            Set.of());

            assertThat(role.roleIdValue()).isEqualTo(1L);
        }

        @Test
        @DisplayName("nameValue()는 RoleName의 값을 반환한다")
        void shouldReturnNameValue() {
            Role role =
                    Role.of(
                            RoleId.of(1L),
                            TenantId.of(1L),
                            RoleName.of("ROLE_ADMIN"),
                            "관리자",
                            false,
                            Set.of());

            assertThat(role.nameValue()).isEqualTo("ROLE_ADMIN");
        }

        @Test
        @DisplayName("permissionValues()는 Permission 코드 Set을 반환한다")
        void shouldReturnPermissionValues() {
            Role role =
                    Role.of(
                            RoleId.of(1L),
                            TenantId.of(1L),
                            RoleName.of("ROLE_ADMIN"),
                            "관리자",
                            false,
                            Set.of(
                                    PermissionCode.of("user:read"),
                                    PermissionCode.of("user:write")));

            assertThat(role.permissionValues())
                    .containsExactlyInAnyOrder("user:read", "user:write");
        }
    }
}
