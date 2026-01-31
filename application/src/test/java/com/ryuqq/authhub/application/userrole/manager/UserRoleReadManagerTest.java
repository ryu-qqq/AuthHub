package com.ryuqq.authhub.application.userrole.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.authhub.application.userrole.port.out.query.UserRoleQueryPort;
import com.ryuqq.authhub.domain.role.id.RoleId;
import com.ryuqq.authhub.domain.user.id.UserId;
import com.ryuqq.authhub.domain.userrole.aggregate.UserRole;
import com.ryuqq.authhub.domain.userrole.fixture.UserRoleFixture;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * UserRoleReadManager 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("UserRoleReadManager 단위 테스트")
class UserRoleReadManagerTest {

    @Mock private UserRoleQueryPort queryPort;

    private UserRoleReadManager sut;

    @BeforeEach
    void setUp() {
        sut = new UserRoleReadManager(queryPort);
    }

    @Nested
    @DisplayName("exists 메서드")
    class Exists {

        @Test
        @DisplayName("존재하면 true 반환")
        void shouldReturnTrue_WhenExists() {
            // given
            UserId userId = UserRoleFixture.defaultUserId();
            RoleId roleId = UserRoleFixture.defaultRoleId();

            given(queryPort.exists(userId, roleId)).willReturn(true);

            // when
            boolean result = sut.exists(userId, roleId);

            // then
            assertThat(result).isTrue();
            then(queryPort).should().exists(userId, roleId);
        }

        @Test
        @DisplayName("존재하지 않으면 false 반환")
        void shouldReturnFalse_WhenNotExists() {
            // given
            UserId userId = UserRoleFixture.defaultUserId();
            RoleId roleId = UserRoleFixture.defaultRoleId();

            given(queryPort.exists(userId, roleId)).willReturn(false);

            // when
            boolean result = sut.exists(userId, roleId);

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("findAllByUserId 메서드")
    class FindAllByUserId {

        @Test
        @DisplayName("성공: 사용자 ID에 해당하는 UserRole 목록 반환")
        void shouldReturnUserRoles_ForUserId() {
            // given
            UserId userId = UserRoleFixture.defaultUserId();
            List<UserRole> expected = List.of(UserRoleFixture.create());

            given(queryPort.findAllByUserId(userId)).willReturn(expected);

            // when
            List<UserRole> result = sut.findAllByUserId(userId);

            // then
            assertThat(result).hasSize(1);
            then(queryPort).should().findAllByUserId(userId);
        }
    }
}
