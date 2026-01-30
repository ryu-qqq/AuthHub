package com.ryuqq.authhub.domain.userrole.aggregate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.authhub.domain.role.id.RoleId;
import com.ryuqq.authhub.domain.user.id.UserId;
import com.ryuqq.authhub.domain.userrole.fixture.UserRoleFixture;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * UserRole Aggregate 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("UserRole Aggregate 테스트")
class UserRoleTest {

    private static final Instant NOW = Instant.parse("2025-01-15T10:00:00Z");

    @Nested
    @DisplayName("UserRole 생성 테스트")
    class CreateTests {

        @Test
        @DisplayName("새로운 UserRole을 성공적으로 생성한다")
        void shouldCreateUserRoleSuccessfully() {
            // given
            UserId userId = UserId.of("01941234-5678-7000-8000-123456789999");
            RoleId roleId = RoleId.of(1L);

            // when
            UserRole userRole = UserRole.create(userId, roleId, NOW);

            // then
            assertThat(userRole.userIdValue()).isEqualTo(userId.value());
            assertThat(userRole.roleIdValue()).isEqualTo(roleId.value());
            assertThat(userRole.isNew()).isTrue();
            assertThat(userRole.createdAt()).isEqualTo(NOW);
        }

        @Test
        @DisplayName("userId가 null이면 예외가 발생한다")
        void shouldThrowExceptionWhenUserIdIsNull() {
            // given
            RoleId roleId = RoleId.of(1L);

            // when & then
            assertThatThrownBy(() -> UserRole.create(null, roleId, NOW))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("userId");
        }

        @Test
        @DisplayName("roleId가 null이면 예외가 발생한다")
        void shouldThrowExceptionWhenRoleIdIsNull() {
            // given
            UserId userId = UserId.of("01941234-5678-7000-8000-123456789999");

            // when & then
            assertThatThrownBy(() -> UserRole.create(userId, null, NOW))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("roleId");
        }
    }

    @Nested
    @DisplayName("UserRole Query 메서드 테스트")
    class QueryMethodTests {

        @Test
        @DisplayName("isNew는 ID가 없을 때 true를 반환한다")
        void isNewShouldReturnTrueWhenIdIsNull() {
            // given
            UserRole newUserRole = UserRoleFixture.createNew();
            UserRole existingUserRole = UserRoleFixture.create();

            // then
            assertThat(newUserRole.isNew()).isTrue();
            assertThat(existingUserRole.isNew()).isFalse();
        }

        @Test
        @DisplayName("userIdValue는 사용자 ID 문자열을 반환한다")
        void userIdValueShouldReturnUserIdString() {
            // given
            UserRole userRole = UserRoleFixture.create();

            // then
            assertThat(userRole.userIdValue()).isEqualTo(UserRoleFixture.defaultUserIdString());
        }

        @Test
        @DisplayName("roleIdValue는 역할 ID Long 값을 반환한다")
        void roleIdValueShouldReturnRoleIdLong() {
            // given
            UserRole userRole = UserRoleFixture.create();

            // then
            assertThat(userRole.roleIdValue()).isEqualTo(UserRoleFixture.defaultRoleIdValue());
        }
    }

    @Nested
    @DisplayName("UserRole equals/hashCode 테스트")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("같은 userRoleId를 가진 UserRole은 동등하다")
        void shouldBeEqualWhenSameUserRoleId() {
            // given
            UserRole userRole1 = UserRoleFixture.create();
            UserRole userRole2 = UserRoleFixture.create();

            // then
            assertThat(userRole1).isEqualTo(userRole2);
            assertThat(userRole1.hashCode()).isEqualTo(userRole2.hashCode());
        }

        @Test
        @DisplayName("ID가 없는 경우 userId와 roleId로 동등성을 판단한다")
        void shouldUseUserIdAndRoleIdWhenIdIsNull() {
            // given
            UserRole newUserRole1 =
                    UserRoleFixture.createNewWithUserAndRole(
                            "01941234-5678-7000-8000-123456789abc", 1L);
            UserRole newUserRole2 =
                    UserRoleFixture.createNewWithUserAndRole(
                            "01941234-5678-7000-8000-123456789abc", 1L);

            // then - 같은 userId + roleId이므로 동등함
            assertThat(newUserRole1).isEqualTo(newUserRole2);
        }

        @Test
        @DisplayName("userId가 다르면 동등하지 않다 (ID가 없는 경우)")
        void shouldNotBeEqualWhenDifferentUserId() {
            // given
            UserRole userRole1 =
                    UserRoleFixture.createNewWithUserAndRole(
                            "01941234-5678-7000-8000-123456789abc", 1L);
            UserRole userRole2 =
                    UserRoleFixture.createNewWithUserAndRole(
                            "01941234-5678-7000-8000-123456789def", 1L);

            // then
            assertThat(userRole1).isNotEqualTo(userRole2);
        }

        @Test
        @DisplayName("roleId가 다르면 동등하지 않다 (ID가 없는 경우)")
        void shouldNotBeEqualWhenDifferentRoleId() {
            // given
            UserRole userRole1 =
                    UserRoleFixture.createNewWithUserAndRole(
                            "01941234-5678-7000-8000-123456789abc", 1L);
            UserRole userRole2 =
                    UserRoleFixture.createNewWithUserAndRole(
                            "01941234-5678-7000-8000-123456789abc", 2L);

            // then
            assertThat(userRole1).isNotEqualTo(userRole2);
        }
    }
}
