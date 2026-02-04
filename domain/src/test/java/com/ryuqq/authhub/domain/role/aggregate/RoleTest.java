package com.ryuqq.authhub.domain.role.aggregate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.authhub.domain.role.exception.SystemRoleNotDeletableException;
import com.ryuqq.authhub.domain.role.exception.SystemRoleNotModifiableException;
import com.ryuqq.authhub.domain.role.fixture.RoleFixture;
import com.ryuqq.authhub.domain.role.vo.RoleName;
import com.ryuqq.authhub.domain.tenant.id.TenantId;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Role Aggregate 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("Role Aggregate 테스트")
class RoleTest {

    private static final Instant NOW = Instant.parse("2025-01-15T10:00:00Z");

    @Nested
    @DisplayName("Role 생성 테스트")
    class CreateTests {

        @Test
        @DisplayName("시스템 역할을 성공적으로 생성한다")
        void shouldCreateSystemRoleSuccessfully() {
            // when
            Role role =
                    Role.createSystem(RoleName.of("SUPER_ADMIN"), "슈퍼 관리자", "시스템 전체 관리 권한", NOW);

            // then
            assertThat(role.nameValue()).isEqualTo("SUPER_ADMIN");
            assertThat(role.displayNameValue()).isEqualTo("슈퍼 관리자");
            assertThat(role.descriptionValue()).isEqualTo("시스템 전체 관리 권한");
            assertThat(role.isSystem()).isTrue();
            assertThat(role.isCustom()).isFalse();
            assertThat(role.isGlobal()).isTrue();
            assertThat(role.isTenantSpecific()).isFalse();
            assertThat(role.isNew()).isTrue();
            assertThat(role.isDeleted()).isFalse();
        }

        @Test
        @DisplayName("커스텀 역할을 성공적으로 생성한다")
        void shouldCreateCustomRoleSuccessfully() {
            // when
            Role role = Role.createCustom(RoleName.of("CUSTOM_ROLE"), "커스텀 역할", "사용자 정의 역할", NOW);

            // then
            assertThat(role.nameValue()).isEqualTo("CUSTOM_ROLE");
            assertThat(role.isSystem()).isFalse();
            assertThat(role.isCustom()).isTrue();
            assertThat(role.isGlobal()).isTrue();
            assertThat(role.isNew()).isTrue();
        }

        @Test
        @DisplayName("테넌트 전용 커스텀 역할을 성공적으로 생성한다")
        void shouldCreateTenantCustomRoleSuccessfully() {
            // given
            TenantId tenantId = TenantId.of("01941234-5678-7000-8000-123456789abc");

            // when
            Role role =
                    Role.createTenantCustom(
                            tenantId, RoleName.of("TENANT_ROLE"), "테넌트 역할", "테넌트 전용 역할", NOW);

            // then
            assertThat(role.nameValue()).isEqualTo("TENANT_ROLE");
            assertThat(role.tenantIdValue()).isEqualTo(tenantId.value());
            assertThat(role.isCustom()).isTrue();
            assertThat(role.isGlobal()).isFalse();
            assertThat(role.isTenantSpecific()).isTrue();
        }

        @Test
        @DisplayName("통합 create 메서드로 시스템 역할을 생성한다")
        void shouldCreateSystemRoleViaUnifiedMethod() {
            // when
            Role role =
                    Role.create(
                            null,
                            null,
                            RoleName.of("UNIFIED_SYSTEM"),
                            "통합 시스템 역할",
                            "통합 메서드로 생성된 시스템 역할",
                            true,
                            NOW);

            // then
            assertThat(role.isSystem()).isTrue();
            assertThat(role.isGlobal()).isTrue();
        }

        @Test
        @DisplayName("통합 create 메서드로 테넌트 커스텀 역할을 생성한다")
        void shouldCreateTenantCustomRoleViaUnifiedMethod() {
            // given
            TenantId tenantId = TenantId.of("01941234-5678-7000-8000-123456789abc");

            // when
            Role role =
                    Role.create(
                            tenantId,
                            null,
                            RoleName.of("UNIFIED_TENANT"),
                            "통합 테넌트 역할",
                            "통합 메서드로 생성된 테넌트 역할",
                            false,
                            NOW);

            // then
            assertThat(role.isCustom()).isTrue();
            assertThat(role.isTenantSpecific()).isTrue();
            assertThat(role.tenantIdValue()).isEqualTo(tenantId.value());
        }
    }

    @Nested
    @DisplayName("Role 수정 테스트")
    class UpdateTests {

        @Test
        @DisplayName("커스텀 역할의 정보를 수정한다")
        void shouldUpdateCustomRole() {
            // given
            Role role = RoleFixture.createCustomRole();
            RoleUpdateData updateData = RoleUpdateData.of("새로운 표시명", "새로운 설명");

            // when
            role.update(updateData, NOW);

            // then
            assertThat(role.displayNameValue()).isEqualTo("새로운 표시명");
            assertThat(role.descriptionValue()).isEqualTo("새로운 설명");
            assertThat(role.updatedAt()).isEqualTo(NOW);
        }

        @Test
        @DisplayName("표시명만 수정한다")
        void shouldUpdateOnlyDisplayName() {
            // given
            Role role = RoleFixture.createCustomRole();
            String originalDescription = role.descriptionValue();
            RoleUpdateData updateData = RoleUpdateData.of("새로운 표시명", null);

            // when
            role.update(updateData, NOW);

            // then
            assertThat(role.displayNameValue()).isEqualTo("새로운 표시명");
            assertThat(role.descriptionValue()).isEqualTo(originalDescription);
        }

        @Test
        @DisplayName("설명만 수정한다")
        void shouldUpdateOnlyDescription() {
            // given
            Role role = RoleFixture.createCustomRole();
            String originalDisplayName = role.displayNameValue();
            RoleUpdateData updateData = RoleUpdateData.of(null, "새로운 설명");

            // when
            role.update(updateData, NOW);

            // then
            assertThat(role.displayNameValue()).isEqualTo(originalDisplayName);
            assertThat(role.descriptionValue()).isEqualTo("새로운 설명");
        }

        @Test
        @DisplayName("시스템 역할은 수정할 수 없다")
        void shouldThrowExceptionWhenUpdatingSystemRole() {
            // given
            Role systemRole = RoleFixture.createSystemRole();
            RoleUpdateData updateData = RoleUpdateData.of("새로운 표시명", "새로운 설명");

            // when & then
            assertThatThrownBy(() -> systemRole.update(updateData, NOW))
                    .isInstanceOf(SystemRoleNotModifiableException.class);
        }
    }

    @Nested
    @DisplayName("Role 삭제/복원 테스트")
    class DeleteRestoreTests {

        @Test
        @DisplayName("커스텀 역할을 삭제(소프트 삭제)한다")
        void shouldDeleteCustomRole() {
            // given
            Role role = RoleFixture.createCustomRole();

            // when
            role.delete(NOW);

            // then
            assertThat(role.isDeleted()).isTrue();
            assertThat(role.isActive()).isFalse();
            assertThat(role.updatedAt()).isEqualTo(NOW);
        }

        @Test
        @DisplayName("시스템 역할은 삭제할 수 없다")
        void shouldThrowExceptionWhenDeletingSystemRole() {
            // given
            Role systemRole = RoleFixture.createSystemRole();

            // when & then
            assertThatThrownBy(() -> systemRole.delete(NOW))
                    .isInstanceOf(SystemRoleNotDeletableException.class);
        }

        @Test
        @DisplayName("삭제된 역할을 복원한다")
        void shouldRestoreRole() {
            // given
            Role role = RoleFixture.createDeleted();
            assertThat(role.isDeleted()).isTrue();

            // when
            role.restore(NOW);

            // then
            assertThat(role.isDeleted()).isFalse();
            assertThat(role.isActive()).isTrue();
            assertThat(role.updatedAt()).isEqualTo(NOW);
        }
    }

    @Nested
    @DisplayName("Role Query 메서드 테스트")
    class QueryMethodTests {

        @Test
        @DisplayName("Global 역할은 tenantId가 null이다")
        void globalRoleShouldHaveNullTenantId() {
            // given
            Role globalRole = RoleFixture.create();

            // then
            assertThat(globalRole.isGlobal()).isTrue();
            assertThat(globalRole.isTenantSpecific()).isFalse();
            assertThat(globalRole.tenantIdValue()).isNull();
        }

        @Test
        @DisplayName("테넌트 전용 역할은 tenantId가 있다")
        void tenantRoleShouldHaveTenantId() {
            // given
            Role tenantRole = RoleFixture.createTenantRole();

            // then
            assertThat(tenantRole.isGlobal()).isFalse();
            assertThat(tenantRole.isTenantSpecific()).isTrue();
            assertThat(tenantRole.tenantIdValue()).isNotNull();
        }

        @Test
        @DisplayName("isNew는 ID가 없을 때 true를 반환한다")
        void isNewShouldReturnTrueWhenIdIsNull() {
            // given
            Role newRole = RoleFixture.createNewCustomRole();
            Role existingRole = RoleFixture.create();

            // then
            assertThat(newRole.isNew()).isTrue();
            assertThat(existingRole.isNew()).isFalse();
        }
    }

    @Nested
    @DisplayName("Role equals/hashCode 테스트")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("같은 roleId를 가진 Role은 동등하다")
        void shouldBeEqualWhenSameRoleId() {
            // given
            Role role1 = RoleFixture.create();
            Role role2 = RoleFixture.create();

            // then
            assertThat(role1).isEqualTo(role2);
            assertThat(role1.hashCode()).isEqualTo(role2.hashCode());
        }

        @Test
        @DisplayName("ID가 없는 경우 name과 tenantId로 동등성을 판단한다")
        void shouldUseNameAndTenantIdWhenIdIsNull() {
            // given
            Role newRole1 = Role.createCustom(RoleName.of("SAME_NAME"), "표시명1", "설명1", NOW);
            Role newRole2 = Role.createCustom(RoleName.of("SAME_NAME"), "표시명2", "설명2", NOW);

            // then - 같은 name, null tenantId이므로 동등함
            assertThat(newRole1).isEqualTo(newRole2);
        }

        @Test
        @DisplayName("name이 다르면 동등하지 않다 (ID가 없는 경우)")
        void shouldNotBeEqualWhenDifferentName() {
            // given
            Role role1 = Role.createCustom(RoleName.of("ROLE_A"), "표시명1", "설명1", NOW);
            Role role2 = Role.createCustom(RoleName.of("ROLE_B"), "표시명2", "설명2", NOW);

            // then
            assertThat(role1).isNotEqualTo(role2);
        }
    }
}
