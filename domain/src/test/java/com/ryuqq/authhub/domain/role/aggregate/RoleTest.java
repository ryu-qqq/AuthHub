package com.ryuqq.authhub.domain.role.aggregate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.authhub.domain.common.util.ClockHolder;
import com.ryuqq.authhub.domain.role.exception.SystemRoleNotDeletableException;
import com.ryuqq.authhub.domain.role.exception.SystemRoleNotModifiableException;
import com.ryuqq.authhub.domain.role.fixture.RoleFixture;
import com.ryuqq.authhub.domain.role.identifier.RoleId;
import com.ryuqq.authhub.domain.role.vo.RoleDescription;
import com.ryuqq.authhub.domain.role.vo.RoleName;
import com.ryuqq.authhub.domain.role.vo.RoleScope;
import com.ryuqq.authhub.domain.role.vo.RoleType;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Role Aggregate Root 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("Role Aggregate 테스트")
class RoleTest {

    private static final Instant FIXED_TIME = Instant.parse("2025-01-01T00:00:00Z");
    private static final ClockHolder FIXED_CLOCK_HOLDER =
            () -> Clock.fixed(FIXED_TIME, ZoneOffset.UTC);
    private static final Clock FIXED_CLOCK = FIXED_CLOCK_HOLDER.clock();

    @Nested
    @DisplayName("createSystemGlobal 팩토리 메서드")
    class CreateSystemGlobalTest {

        @Test
        @DisplayName("새로운 시스템 GLOBAL 역할을 생성한다")
        void shouldCreateSystemGlobalRole() {
            // given
            RoleName name = RoleName.of("SUPER_ADMIN");
            RoleDescription description = RoleDescription.of("Super admin role");

            // when
            Role role = Role.createSystemGlobal(name, description, FIXED_CLOCK);

            // then
            assertThat(role).isNotNull();
            assertThat(role.isNew()).isTrue();
            assertThat(role.nameValue()).isEqualTo("SUPER_ADMIN");
            assertThat(role.getScope()).isEqualTo(RoleScope.GLOBAL);
            assertThat(role.getType()).isEqualTo(RoleType.SYSTEM);
            assertThat(role.isSystem()).isTrue();
            assertThat(role.isGlobal()).isTrue();
            assertThat(role.getTenantId()).isNull();
            assertThat(role.createdAt()).isEqualTo(FIXED_TIME);
            assertThat(role.updatedAt()).isEqualTo(FIXED_TIME);
        }

        @Test
        @DisplayName("설명이 null이면 빈 설명으로 생성된다")
        void shouldCreateWithEmptyDescriptionWhenNull() {
            // given
            RoleName name = RoleName.of("ADMIN");

            // when
            Role role = Role.createSystemGlobal(name, null, FIXED_CLOCK);

            // then
            assertThat(role.descriptionValue()).isEmpty();
        }
    }

    @Nested
    @DisplayName("createCustomTenant 팩토리 메서드")
    class CreateCustomTenantTest {

        @Test
        @DisplayName("새로운 커스텀 TENANT 역할을 생성한다")
        void shouldCreateCustomTenantRole() {
            // given
            TenantId tenantId = TenantId.of(UUID.randomUUID());
            RoleName name = RoleName.of("CUSTOM_ROLE");
            RoleDescription description = RoleDescription.of("Custom tenant role");

            // when
            Role role = Role.createCustomTenant(tenantId, name, description, FIXED_CLOCK);

            // then
            assertThat(role).isNotNull();
            assertThat(role.isNew()).isTrue();
            assertThat(role.nameValue()).isEqualTo("CUSTOM_ROLE");
            assertThat(role.getScope()).isEqualTo(RoleScope.TENANT);
            assertThat(role.getType()).isEqualTo(RoleType.CUSTOM);
            assertThat(role.isCustom()).isTrue();
            assertThat(role.isTenantScoped()).isTrue();
            assertThat(role.getTenantId()).isEqualTo(tenantId);
        }

        @Test
        @DisplayName("tenantId가 null이면 예외 발생")
        void shouldThrowExceptionWhenTenantIdIsNull() {
            // given
            RoleName name = RoleName.of("CUSTOM_ROLE");
            RoleDescription description = RoleDescription.of("Description");

            // when/then
            assertThatThrownBy(() -> Role.createCustomTenant(null, name, description, FIXED_CLOCK))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("TENANT 범위 역할은 tenantId가 필수입니다");
        }
    }

    @Nested
    @DisplayName("createCustomOrganization 팩토리 메서드")
    class CreateCustomOrganizationTest {

        @Test
        @DisplayName("새로운 커스텀 ORGANIZATION 역할을 생성한다")
        void shouldCreateCustomOrganizationRole() {
            // given
            TenantId tenantId = TenantId.of(UUID.randomUUID());
            RoleName name = RoleName.of("ORG_ROLE");
            RoleDescription description = RoleDescription.of("Organization role");

            // when
            Role role = Role.createCustomOrganization(tenantId, name, description, FIXED_CLOCK);

            // then
            assertThat(role).isNotNull();
            assertThat(role.isNew()).isTrue();
            assertThat(role.nameValue()).isEqualTo("ORG_ROLE");
            assertThat(role.getScope()).isEqualTo(RoleScope.ORGANIZATION);
            assertThat(role.getType()).isEqualTo(RoleType.CUSTOM);
            assertThat(role.isOrganizationScoped()).isTrue();
        }

        @Test
        @DisplayName("tenantId가 null이면 예외 발생")
        void shouldThrowExceptionWhenTenantIdIsNull() {
            // given
            RoleName name = RoleName.of("ORG_ROLE");
            RoleDescription description = RoleDescription.of("Description");

            // when/then
            assertThatThrownBy(
                            () ->
                                    Role.createCustomOrganization(
                                            null, name, description, FIXED_CLOCK))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("ORGANIZATION 범위 역할은 tenantId가 필수입니다");
        }
    }

    @Nested
    @DisplayName("reconstitute 팩토리 메서드")
    class ReconstituteTest {

        @Test
        @DisplayName("DB에서 Role을 재구성한다")
        void shouldReconstituteRoleFromDb() {
            // given
            UUID uuid = UUID.randomUUID();
            UUID tenantUuid = UUID.randomUUID();
            RoleId roleId = RoleId.of(uuid);
            TenantId tenantId = TenantId.of(tenantUuid);
            RoleName name = RoleName.of("TEST_ROLE");
            RoleDescription description = RoleDescription.of("Test role");
            Instant createdAt = Instant.parse("2024-01-01T00:00:00Z");
            Instant updatedAt = Instant.parse("2024-06-01T00:00:00Z");

            // when
            Role role =
                    Role.reconstitute(
                            roleId,
                            tenantId,
                            name,
                            description,
                            RoleScope.ORGANIZATION,
                            RoleType.CUSTOM,
                            false,
                            createdAt,
                            updatedAt);

            // then
            assertThat(role).isNotNull();
            assertThat(role.isNew()).isFalse();
            assertThat(role.roleIdValue()).isEqualTo(uuid);
            assertThat(role.tenantIdValue()).isEqualTo(tenantUuid);
            assertThat(role.nameValue()).isEqualTo("TEST_ROLE");
            assertThat(role.createdAt()).isEqualTo(createdAt);
            assertThat(role.updatedAt()).isEqualTo(updatedAt);
        }

        @Test
        @DisplayName("roleId가 null이면 예외 발생")
        void shouldThrowExceptionWhenRoleIdIsNull() {
            assertThatThrownBy(
                            () ->
                                    Role.reconstitute(
                                            null,
                                            TenantId.of(UUID.randomUUID()),
                                            RoleName.of("TEST"),
                                            RoleDescription.of("Test"),
                                            RoleScope.ORGANIZATION,
                                            RoleType.CUSTOM,
                                            false,
                                            FIXED_TIME,
                                            FIXED_TIME))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("reconstitute requires non-null roleId");
        }

        @Test
        @DisplayName("필수 필드가 null이면 예외 발생")
        void shouldThrowExceptionWhenRequiredFieldIsNull() {
            RoleId id = RoleId.forNew(UUID.randomUUID());
            TenantId tenantId = TenantId.of(UUID.randomUUID());

            assertThatThrownBy(
                            () ->
                                    Role.reconstitute(
                                            id,
                                            tenantId,
                                            null,
                                            RoleDescription.of("Test"),
                                            RoleScope.ORGANIZATION,
                                            RoleType.CUSTOM,
                                            false,
                                            FIXED_TIME,
                                            FIXED_TIME))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("RoleName은 null일 수 없습니다");

            assertThatThrownBy(
                            () ->
                                    Role.reconstitute(
                                            id,
                                            tenantId,
                                            RoleName.of("TEST"),
                                            RoleDescription.of("Test"),
                                            null,
                                            RoleType.CUSTOM,
                                            false,
                                            FIXED_TIME,
                                            FIXED_TIME))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("RoleScope는 null일 수 없습니다");

            assertThatThrownBy(
                            () ->
                                    Role.reconstitute(
                                            id,
                                            tenantId,
                                            RoleName.of("TEST"),
                                            RoleDescription.of("Test"),
                                            RoleScope.ORGANIZATION,
                                            null,
                                            false,
                                            FIXED_TIME,
                                            FIXED_TIME))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("RoleType은 null일 수 없습니다");
        }
    }

    @Nested
    @DisplayName("changeName 비즈니스 메서드")
    class ChangeNameTest {

        @Test
        @DisplayName("커스텀 역할의 이름을 변경한다")
        void shouldChangeNameForCustomRole() {
            // given
            Role role = RoleFixture.create();
            RoleName newName = RoleName.of("CHANGED_ROLE");

            // when
            Role changedRole = role.changeName(newName, FIXED_CLOCK);

            // then
            assertThat(changedRole.nameValue()).isEqualTo("CHANGED_ROLE");
            assertThat(changedRole.updatedAt()).isEqualTo(FIXED_TIME);
        }

        @Test
        @DisplayName("시스템 역할 이름 변경 시 예외 발생")
        void shouldThrowExceptionWhenChangingSystemRoleName() {
            // given
            Role systemRole = RoleFixture.createSystemGlobal();
            RoleName newName = RoleName.of("NEW_NAME");

            // when/then
            assertThatThrownBy(() -> systemRole.changeName(newName, FIXED_CLOCK))
                    .isInstanceOf(SystemRoleNotModifiableException.class);
        }
    }

    @Nested
    @DisplayName("changeDescription 비즈니스 메서드")
    class ChangeDescriptionTest {

        @Test
        @DisplayName("커스텀 역할의 설명을 변경한다")
        void shouldChangeDescriptionForCustomRole() {
            // given
            Role role = RoleFixture.create();
            RoleDescription newDescription = RoleDescription.of("Updated description");

            // when
            Role changedRole = role.changeDescription(newDescription, FIXED_CLOCK);

            // then
            assertThat(changedRole.descriptionValue()).isEqualTo("Updated description");
            assertThat(changedRole.updatedAt()).isEqualTo(FIXED_TIME);
        }

        @Test
        @DisplayName("시스템 역할 설명 변경 시 예외 발생")
        void shouldThrowExceptionWhenChangingSystemRoleDescription() {
            // given
            Role systemRole = RoleFixture.createSystemGlobal();
            RoleDescription newDescription = RoleDescription.of("New description");

            // when/then
            assertThatThrownBy(() -> systemRole.changeDescription(newDescription, FIXED_CLOCK))
                    .isInstanceOf(SystemRoleNotModifiableException.class);
        }
    }

    @Nested
    @DisplayName("delete 비즈니스 메서드")
    class DeleteTest {

        @Test
        @DisplayName("커스텀 역할을 삭제한다")
        void shouldDeleteCustomRole() {
            // given
            Role role = RoleFixture.create();

            // when
            Role deletedRole = role.delete(FIXED_CLOCK);

            // then
            assertThat(deletedRole.isDeleted()).isTrue();
            assertThat(deletedRole.updatedAt()).isEqualTo(FIXED_TIME);
        }

        @Test
        @DisplayName("시스템 역할 삭제 시 예외 발생")
        void shouldThrowExceptionWhenDeletingSystemRole() {
            // given
            Role systemRole = RoleFixture.createSystemGlobal();

            // when/then
            assertThatThrownBy(() -> systemRole.delete(FIXED_CLOCK))
                    .isInstanceOf(SystemRoleNotDeletableException.class);
        }
    }

    @Nested
    @DisplayName("Helper 메서드")
    class HelperMethodsTest {

        @Test
        @DisplayName("isSystem은 시스템 역할일 때 true 반환")
        void shouldReturnTrueWhenSystemRole() {
            // given
            Role systemRole = RoleFixture.createSystemGlobal();
            Role customRole = RoleFixture.create();

            // then
            assertThat(systemRole.isSystem()).isTrue();
            assertThat(customRole.isSystem()).isFalse();
        }

        @Test
        @DisplayName("isCustom은 커스텀 역할일 때 true 반환")
        void shouldReturnTrueWhenCustomRole() {
            // given
            Role customRole = RoleFixture.create();
            Role systemRole = RoleFixture.createSystemGlobal();

            // then
            assertThat(customRole.isCustom()).isTrue();
            assertThat(systemRole.isCustom()).isFalse();
        }

        @Test
        @DisplayName("isGlobal은 GLOBAL 범위일 때 true 반환")
        void shouldReturnTrueWhenGlobalScope() {
            // given
            Role globalRole = RoleFixture.createSystemGlobal();
            Role orgRole = RoleFixture.create();

            // then
            assertThat(globalRole.isGlobal()).isTrue();
            assertThat(orgRole.isGlobal()).isFalse();
        }

        @Test
        @DisplayName("isTenantScoped는 TENANT 범위일 때 true 반환")
        void shouldReturnTrueWhenTenantScope() {
            // given
            Role tenantRole = RoleFixture.createCustomTenant();
            Role orgRole = RoleFixture.create();

            // then
            assertThat(tenantRole.isTenantScoped()).isTrue();
            assertThat(orgRole.isTenantScoped()).isFalse();
        }

        @Test
        @DisplayName("isOrganizationScoped는 ORGANIZATION 범위일 때 true 반환")
        void shouldReturnTrueWhenOrganizationScope() {
            // given
            Role orgRole = RoleFixture.create();
            Role tenantRole = RoleFixture.createCustomTenant();

            // then
            assertThat(orgRole.isOrganizationScoped()).isTrue();
            assertThat(tenantRole.isOrganizationScoped()).isFalse();
        }
    }

    @Nested
    @DisplayName("equals 및 hashCode")
    class EqualsHashCodeTest {

        @Test
        @DisplayName("같은 ID를 가진 Role은 동일하다")
        void shouldBeEqualWhenSameRoleId() {
            // given
            UUID uuid = UUID.randomUUID();
            Role role1 =
                    Role.reconstitute(
                            RoleId.of(uuid),
                            TenantId.of(UUID.randomUUID()),
                            RoleName.of("ROLE1"),
                            RoleDescription.of("Role 1"),
                            RoleScope.ORGANIZATION,
                            RoleType.CUSTOM,
                            false,
                            FIXED_TIME,
                            FIXED_TIME);
            Role role2 =
                    Role.reconstitute(
                            RoleId.of(uuid),
                            TenantId.of(UUID.randomUUID()),
                            RoleName.of("ROLE2"),
                            RoleDescription.of("Role 2"),
                            RoleScope.TENANT,
                            RoleType.SYSTEM,
                            false,
                            FIXED_TIME,
                            FIXED_TIME);

            // then
            assertThat(role1).isEqualTo(role2);
            assertThat(role1.hashCode()).isEqualTo(role2.hashCode());
        }

        @Test
        @DisplayName("다른 ID를 가진 Role은 다르다")
        void shouldNotBeEqualWhenDifferentRoleId() {
            // given
            Role role1 =
                    Role.reconstitute(
                            RoleId.of(UUID.randomUUID()),
                            TenantId.of(UUID.randomUUID()),
                            RoleName.of("ROLE"),
                            RoleDescription.of("Role"),
                            RoleScope.ORGANIZATION,
                            RoleType.CUSTOM,
                            false,
                            FIXED_TIME,
                            FIXED_TIME);
            Role role2 =
                    Role.reconstitute(
                            RoleId.of(UUID.randomUUID()),
                            TenantId.of(UUID.randomUUID()),
                            RoleName.of("ROLE"),
                            RoleDescription.of("Role"),
                            RoleScope.ORGANIZATION,
                            RoleType.CUSTOM,
                            false,
                            FIXED_TIME,
                            FIXED_TIME);

            // then
            assertThat(role1).isNotEqualTo(role2);
        }

        @Test
        @DisplayName("ID가 null인 Role은 서로 다르다")
        void shouldNotBeEqualWhenIdIsNull() {
            // given
            Role role1 = RoleFixture.createNew();
            Role role2 = RoleFixture.createNew();

            // then
            assertThat(role1).isNotEqualTo(role2);
        }

        @Test
        @DisplayName("자기 자신과 같다")
        void shouldBeEqualToItself() {
            // given
            Role role = RoleFixture.create();

            // then
            assertThat(role).isEqualTo(role);
        }

        @Test
        @DisplayName("null과 같지 않다")
        void shouldNotBeEqualToNull() {
            // given
            Role role = RoleFixture.create();

            // then
            assertThat(role).isNotEqualTo(null);
        }
    }

    @Nested
    @DisplayName("toString")
    class ToStringTest {

        @Test
        @DisplayName("Role의 문자열 표현을 반환한다")
        void shouldReturnStringRepresentation() {
            // given
            Role role = RoleFixture.create();

            // when
            String toString = role.toString();

            // then
            assertThat(toString).contains("Role");
            assertThat(toString).contains("roleId");
            assertThat(toString).contains("name");
            assertThat(toString).contains("scope");
            assertThat(toString).contains("type");
        }
    }
}
