package com.ryuqq.authhub.application.userrole.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import com.ryuqq.authhub.application.role.validator.RoleValidator;
import com.ryuqq.authhub.application.user.validator.UserValidator;
import com.ryuqq.authhub.application.userrole.factory.UserRoleCommandFactory;
import com.ryuqq.authhub.application.userrole.manager.UserRoleReadManager;
import com.ryuqq.authhub.domain.role.exception.RoleNotFoundException;
import com.ryuqq.authhub.domain.role.id.RoleId;
import com.ryuqq.authhub.domain.user.exception.UserNotFoundException;
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
 * AssignUserRoleCoordinator 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("AssignUserRoleCoordinator 단위 테스트")
class AssignUserRoleCoordinatorTest {

    @Mock private UserValidator userValidator;
    @Mock private RoleValidator roleValidator;
    @Mock private UserRoleReadManager userRoleReadManager;
    @Mock private UserRoleCommandFactory commandFactory;

    private AssignUserRoleCoordinator sut;

    @BeforeEach
    void setUp() {
        sut =
                new AssignUserRoleCoordinator(
                        userValidator, roleValidator, userRoleReadManager, commandFactory);
    }

    @Nested
    @DisplayName("coordinate 메서드")
    class Coordinate {

        @Test
        @DisplayName("성공: 검증 후 미할당 역할만 Factory로 생성하여 반환")
        void shouldReturnUserRoles_WhenValidationPassesAndNewRolesExist() {
            // given
            String userId = UserRoleFixture.defaultUserIdString();
            List<Long> roleIds = List.of(1L, 2L);
            UserId userIdVo = UserId.of(userId);
            List<RoleId> roleIdVos = List.of(RoleId.of(1L), RoleId.of(2L));
            List<RoleId> assignedIds = List.of(RoleId.of(1L)); // 1만 이미 할당
            List<RoleId> newRoleIds = List.of(RoleId.of(2L));
            List<UserRole> expected = List.of(UserRoleFixture.createNewWithUserAndRole(userId, 2L));

            given(userRoleReadManager.findAssignedRoleIds(userIdVo, roleIdVos))
                    .willReturn(assignedIds);
            given(commandFactory.createAll(userIdVo, newRoleIds)).willReturn(expected);

            // when (userValidator, roleValidator 통과 가정 - void이므로 아무것도 안 하면 통과)
            List<UserRole> result = sut.coordinate(userId, roleIds);

            // then
            assertThat(result).hasSize(1);
            then(userValidator).should().findExistingOrThrow(userIdVo);
            then(roleValidator).should().validateAllExist(roleIdVos);
            then(userRoleReadManager).should().findAssignedRoleIds(userIdVo, roleIdVos);
            then(commandFactory).should().createAll(userIdVo, newRoleIds);
        }

        @Test
        @DisplayName("roleIds가 null이면 빈 목록 반환, Validator/Factory 미호출")
        void shouldReturnEmpty_WhenRoleIdsIsNull() {
            // when
            List<UserRole> result = sut.coordinate(UserRoleFixture.defaultUserIdString(), null);

            // then
            assertThat(result).isEmpty();
            then(userValidator).shouldHaveNoInteractions();
            then(roleValidator).shouldHaveNoInteractions();
            then(commandFactory).should(never()).createAll(any(), anyList());
        }

        @Test
        @DisplayName("roleIds가 빈 목록이면 빈 목록 반환")
        void shouldReturnEmpty_WhenRoleIdsIsEmpty() {
            // when
            List<UserRole> result =
                    sut.coordinate(UserRoleFixture.defaultUserIdString(), List.of());

            // then
            assertThat(result).isEmpty();
            then(userValidator).shouldHaveNoInteractions();
            then(roleValidator).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("실패: 사용자 미존재 시 UserNotFoundException 발생")
        void shouldThrowUserNotFoundException_WhenUserNotExists() {
            // given
            String userId = UserRoleFixture.defaultUserIdString();
            List<Long> roleIds = List.of(1L);
            UserId userIdVo = UserId.of(userId);

            given(userValidator.findExistingOrThrow(userIdVo))
                    .willThrow(new UserNotFoundException(userIdVo));

            // when & then
            assertThatThrownBy(() -> sut.coordinate(userId, roleIds))
                    .isInstanceOf(UserNotFoundException.class);

            then(roleValidator).shouldHaveNoInteractions();
            then(commandFactory).should(never()).createAll(any(), anyList());
        }

        @Test
        @DisplayName("실패: 역할 미존재 시 RoleNotFoundException 발생")
        void shouldThrowRoleNotFoundException_WhenRoleNotExists() {
            // given
            String userId = UserRoleFixture.defaultUserIdString();
            List<Long> roleIds = List.of(999L);
            List<RoleId> roleIdVos = List.of(RoleId.of(999L));

            given(roleValidator.validateAllExist(roleIdVos))
                    .willThrow(new RoleNotFoundException(RoleId.of(999L)));

            // when & then
            assertThatThrownBy(() -> sut.coordinate(userId, roleIds))
                    .isInstanceOf(RoleNotFoundException.class);

            then(userValidator).should().findExistingOrThrow(UserId.of(userId));
            then(commandFactory).should(never()).createAll(any(), anyList());
        }

        @Test
        @DisplayName("이미 전부 할당된 역할이면 빈 목록 반환, Factory 미호출")
        void shouldReturnEmpty_WhenAllRolesAlreadyAssigned() {
            // given
            String userId = UserRoleFixture.defaultUserIdString();
            List<Long> roleIds = List.of(1L, 2L);
            UserId userIdVo = UserId.of(userId);
            List<RoleId> roleIdVos = List.of(RoleId.of(1L), RoleId.of(2L));
            List<RoleId> assignedIds = List.of(RoleId.of(1L), RoleId.of(2L)); // 전부 이미 할당

            given(userRoleReadManager.findAssignedRoleIds(userIdVo, roleIdVos))
                    .willReturn(assignedIds);

            // when
            List<UserRole> result = sut.coordinate(userId, roleIds);

            // then
            assertThat(result).isEmpty();
            then(userValidator).should().findExistingOrThrow(userIdVo);
            then(roleValidator).should().validateAllExist(roleIdVos);
            then(userRoleReadManager).should().findAssignedRoleIds(userIdVo, roleIdVos);
            then(commandFactory).should(never()).createAll(any(), anyList());
        }
    }
}
