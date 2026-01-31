package com.ryuqq.authhub.application.userrole.service.command;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import com.ryuqq.authhub.application.userrole.dto.command.AssignUserRoleCommand;
import com.ryuqq.authhub.application.userrole.fixture.UserRoleCommandFixtures;
import com.ryuqq.authhub.application.userrole.internal.AssignUserRoleCoordinator;
import com.ryuqq.authhub.application.userrole.manager.UserRoleCommandManager;
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
 * AssignUserRoleService 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("AssignUserRoleService 단위 테스트")
class AssignUserRoleServiceTest {

    @Mock private AssignUserRoleCoordinator coordinator;

    @Mock private UserRoleCommandManager commandManager;

    private AssignUserRoleService sut;

    @BeforeEach
    void setUp() {
        sut = new AssignUserRoleService(coordinator, commandManager);
    }

    @Nested
    @DisplayName("assign 메서드")
    class Assign {

        @Test
        @DisplayName("성공: Coordinator → CommandManager 순서로 호출")
        void shouldOrchestrate_CoordinatorThenCommandManager() {
            // given
            AssignUserRoleCommand command = UserRoleCommandFixtures.assignCommand();
            List<UserRole> userRoles = List.of(UserRoleFixture.createNew());

            given(coordinator.coordinate(command.userId(), command.roleIds()))
                    .willReturn(userRoles);

            // when
            sut.assign(command);

            // then
            then(coordinator).should().coordinate(command.userId(), command.roleIds());
            then(commandManager).should().persistAll(userRoles);
        }

        @Test
        @DisplayName("Coordinator가 빈 목록 반환 시 CommandManager 호출하지 않음")
        void shouldNotCallCommandManager_WhenCoordinatorReturnsEmpty() {
            // given
            AssignUserRoleCommand command = UserRoleCommandFixtures.assignCommand();

            given(coordinator.coordinate(command.userId(), command.roleIds()))
                    .willReturn(List.of());

            // when
            sut.assign(command);

            // then
            then(coordinator).should().coordinate(command.userId(), command.roleIds());
            then(commandManager).should(never()).persistAll(anyList());
        }
    }
}
