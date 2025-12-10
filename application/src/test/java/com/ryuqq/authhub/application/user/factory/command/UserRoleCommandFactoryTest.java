package com.ryuqq.authhub.application.user.factory.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.authhub.application.user.dto.command.AssignUserRoleCommand;
import com.ryuqq.authhub.domain.common.util.ClockHolder;
import com.ryuqq.authhub.domain.role.fixture.RoleFixture;
import com.ryuqq.authhub.domain.user.fixture.UserFixture;
import com.ryuqq.authhub.domain.user.vo.UserRole;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * UserRoleCommandFactory 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("UserRoleCommandFactory 단위 테스트")
class UserRoleCommandFactoryTest {

    private UserRoleCommandFactory factory;
    private ClockHolder clockHolder;

    @BeforeEach
    void setUp() {
        clockHolder = UserFixture.fixedClockHolder();
        factory = new UserRoleCommandFactory(clockHolder);
    }

    @Nested
    @DisplayName("create 메서드")
    class CreateTest {

        @Test
        @DisplayName("AssignUserRoleCommand로 UserRole을 생성한다")
        void shouldCreateUserRoleFromCommand() {
            // given
            UUID userId = UserFixture.defaultUUID();
            UUID roleId = RoleFixture.defaultUUID();
            AssignUserRoleCommand command = new AssignUserRoleCommand(userId, roleId);

            // when
            UserRole result = factory.create(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.userIdValue()).isEqualTo(userId);
            assertThat(result.roleIdValue()).isEqualTo(roleId);
            assertThat(result.getAssignedAt()).isNotNull();
        }

        @Test
        @DisplayName("할당 시간이 현재 시간으로 설정된다")
        void shouldSetAssignedAtToCurrentTime() {
            // given
            AssignUserRoleCommand command =
                    new AssignUserRoleCommand(
                            UserFixture.defaultUUID(), RoleFixture.defaultUUID());

            // when
            UserRole result = factory.create(command);

            // then
            assertThat(result.getAssignedAt()).isNotNull();
        }

        @Test
        @DisplayName("userId가 null이면 예외를 발생시킨다")
        void shouldThrowWhenUserIdIsNull() {
            // when & then
            assertThatThrownBy(() -> new AssignUserRoleCommand(null, RoleFixture.defaultUUID()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("userId");
        }

        @Test
        @DisplayName("roleId가 null이면 예외를 발생시킨다")
        void shouldThrowWhenRoleIdIsNull() {
            // when & then
            assertThatThrownBy(() -> new AssignUserRoleCommand(UserFixture.defaultUUID(), null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("roleId");
        }
    }
}
