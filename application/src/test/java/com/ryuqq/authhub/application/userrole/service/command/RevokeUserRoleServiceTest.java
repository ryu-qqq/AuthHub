package com.ryuqq.authhub.application.userrole.service.command;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.authhub.application.user.validator.UserValidator;
import com.ryuqq.authhub.application.userrole.dto.command.RevokeUserRoleCommand;
import com.ryuqq.authhub.application.userrole.fixture.UserRoleCommandFixtures;
import com.ryuqq.authhub.application.userrole.manager.UserRoleCommandManager;
import com.ryuqq.authhub.domain.role.id.RoleId;
import com.ryuqq.authhub.domain.user.aggregate.User;
import com.ryuqq.authhub.domain.user.fixture.UserFixture;
import com.ryuqq.authhub.domain.user.id.UserId;
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
 * RevokeUserRoleService 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("RevokeUserRoleService 단위 테스트")
class RevokeUserRoleServiceTest {

    @Mock private UserValidator userValidator;

    @Mock private UserRoleCommandManager commandManager;

    private RevokeUserRoleService sut;

    @BeforeEach
    void setUp() {
        sut = new RevokeUserRoleService(userValidator, commandManager);
    }

    @Nested
    @DisplayName("revoke 메서드")
    class Revoke {

        @Test
        @DisplayName("성공: UserValidator → CommandManager 순서로 호출")
        void shouldOrchestrate_ValidatorThenCommandManager() {
            // given
            RevokeUserRoleCommand command = UserRoleCommandFixtures.revokeCommand();
            User user = UserFixture.create();
            UserId userId = UserId.of(command.userId());
            List<RoleId> roleIds = command.roleIds().stream().map(RoleId::of).toList();

            given(userValidator.findExistingOrThrow(any(UserId.class))).willReturn(user);

            // when
            sut.revoke(command);

            // then
            then(userValidator).should().findExistingOrThrow(any(UserId.class));
            then(commandManager).should().deleteAll(UserId.of(command.userId()), roleIds);
        }
    }
}
