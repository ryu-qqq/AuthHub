package com.ryuqq.authhub.domain.user.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.authhub.domain.role.identifier.RoleId;
import com.ryuqq.authhub.domain.user.identifier.UserId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

/**
 * UserRole Value Object 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("UserRole VO 테스트")
class UserRoleTest {

    private static final Instant FIXED_TIME = Instant.parse("2025-01-01T00:00:00Z");

    @Nested
    @DisplayName("UserRole 생성")
    class CreateTest {

        @Test
        @DisplayName("UserRole을 생성한다")
        void shouldCreateUserRole() {
            // given
            UserId userId = UserId.of(UUID.randomUUID());
            RoleId roleId = RoleId.of(UUID.randomUUID());

            // when
            UserRole userRole = UserRole.of(userId, roleId, FIXED_TIME);

            // then
            assertThat(userRole).isNotNull();
            assertThat(userRole.getUserId()).isEqualTo(userId);
            assertThat(userRole.getRoleId()).isEqualTo(roleId);
            assertThat(userRole.getAssignedAt()).isEqualTo(FIXED_TIME);
        }

        @Test
        @DisplayName("UserId가 null이면 예외 발생")
        void shouldThrowExceptionWhenUserIdNull() {
            // given
            RoleId roleId = RoleId.of(UUID.randomUUID());

            // when & then
            assertThatThrownBy(() -> UserRole.of(null, roleId, FIXED_TIME))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("UserId");
        }

        @Test
        @DisplayName("RoleId가 null이면 예외 발생")
        void shouldThrowExceptionWhenRoleIdNull() {
            // given
            UserId userId = UserId.of(UUID.randomUUID());

            // when & then
            assertThatThrownBy(() -> UserRole.of(userId, null, FIXED_TIME))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("RoleId");
        }

        @Test
        @DisplayName("assignedAt이 null이면 예외 발생")
        void shouldThrowExceptionWhenAssignedAtNull() {
            // given
            UserId userId = UserId.of(UUID.randomUUID());
            RoleId roleId = RoleId.of(UUID.randomUUID());

            // when & then
            assertThatThrownBy(() -> UserRole.of(userId, roleId, null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("assignedAt");
        }
    }

    @Nested
    @DisplayName("헬퍼 메서드")
    class HelperMethodsTest {

        @Test
        @DisplayName("userIdValue는 UUID를 반환한다")
        void shouldReturnUserIdValue() {
            // given
            UUID userUuid = UUID.randomUUID();
            UserId userId = UserId.of(userUuid);
            RoleId roleId = RoleId.of(UUID.randomUUID());
            UserRole userRole = UserRole.of(userId, roleId, FIXED_TIME);

            // then
            assertThat(userRole.userIdValue()).isEqualTo(userUuid);
        }

        @Test
        @DisplayName("roleIdValue는 UUID를 반환한다")
        void shouldReturnRoleIdValue() {
            // given
            UUID roleUuid = UUID.randomUUID();
            UserId userId = UserId.of(UUID.randomUUID());
            RoleId roleId = RoleId.of(roleUuid);
            UserRole userRole = UserRole.of(userId, roleId, FIXED_TIME);

            // then
            assertThat(userRole.roleIdValue()).isEqualTo(roleUuid);
        }
    }

    @Nested
    @DisplayName("equals 및 hashCode")
    class EqualsHashCodeTest {

        @Test
        @DisplayName("같은 userId와 roleId를 가진 UserRole은 동일하다")
        void shouldBeEqualWhenSameUserIdAndRoleId() {
            // given
            UUID userUuid = UUID.randomUUID();
            UUID roleUuid = UUID.randomUUID();
            UserRole userRole1 = UserRole.of(
                    UserId.of(userUuid), RoleId.of(roleUuid), FIXED_TIME);
            UserRole userRole2 = UserRole.of(
                    UserId.of(userUuid), RoleId.of(roleUuid), Instant.now());

            // then - assignedAt이 달라도 동일
            assertThat(userRole1).isEqualTo(userRole2);
            assertThat(userRole1.hashCode()).isEqualTo(userRole2.hashCode());
        }

        @Test
        @DisplayName("다른 userId를 가진 UserRole은 다르다")
        void shouldNotBeEqualWhenDifferentUserId() {
            // given
            UUID roleUuid = UUID.randomUUID();
            UserRole userRole1 = UserRole.of(
                    UserId.of(UUID.randomUUID()), RoleId.of(roleUuid), FIXED_TIME);
            UserRole userRole2 = UserRole.of(
                    UserId.of(UUID.randomUUID()), RoleId.of(roleUuid), FIXED_TIME);

            // then
            assertThat(userRole1).isNotEqualTo(userRole2);
        }

        @Test
        @DisplayName("다른 roleId를 가진 UserRole은 다르다")
        void shouldNotBeEqualWhenDifferentRoleId() {
            // given
            UUID userUuid = UUID.randomUUID();
            UserRole userRole1 = UserRole.of(
                    UserId.of(userUuid), RoleId.of(UUID.randomUUID()), FIXED_TIME);
            UserRole userRole2 = UserRole.of(
                    UserId.of(userUuid), RoleId.of(UUID.randomUUID()), FIXED_TIME);

            // then
            assertThat(userRole1).isNotEqualTo(userRole2);
        }
    }

    @Nested
    @DisplayName("reconstitute")
    class ReconstituteTest {

        @Test
        @DisplayName("reconstitute로 UserRole을 복원한다")
        void shouldReconstituteUserRole() {
            // given
            UserId userId = UserId.of(UUID.randomUUID());
            RoleId roleId = RoleId.of(UUID.randomUUID());

            // when
            UserRole userRole = UserRole.reconstitute(userId, roleId, FIXED_TIME);

            // then
            assertThat(userRole.getUserId()).isEqualTo(userId);
            assertThat(userRole.getRoleId()).isEqualTo(roleId);
            assertThat(userRole.getAssignedAt()).isEqualTo(FIXED_TIME);
        }
    }
}
