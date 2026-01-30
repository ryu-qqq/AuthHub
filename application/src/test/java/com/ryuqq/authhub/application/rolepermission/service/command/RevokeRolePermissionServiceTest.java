package com.ryuqq.authhub.application.rolepermission.service.command;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.authhub.application.role.validator.RoleValidator;
import com.ryuqq.authhub.application.rolepermission.dto.command.RevokeRolePermissionCommand;
import com.ryuqq.authhub.application.rolepermission.fixture.RolePermissionCommandFixtures;
import com.ryuqq.authhub.application.rolepermission.manager.RolePermissionCommandManager;
import com.ryuqq.authhub.domain.permission.id.PermissionId;
import com.ryuqq.authhub.domain.role.aggregate.Role;
import com.ryuqq.authhub.domain.role.fixture.RoleFixture;
import com.ryuqq.authhub.domain.role.id.RoleId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * RevokeRolePermissionService 단위 테스트
 *
 * <p><strong>테스트 설계 원칙:</strong>
 *
 * <ul>
 *   <li>Service는 오케스트레이션만 담당 → Validator, Manager 호출 검증
 *   <li>BDDMockito 스타일로 Given-When-Then 구조 명확화
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("RevokeRolePermissionService 단위 테스트")
class RevokeRolePermissionServiceTest {

    @Mock private RoleValidator roleValidator;

    @Mock private RolePermissionCommandManager commandManager;

    private RevokeRolePermissionService sut;

    @BeforeEach
    void setUp() {
        sut = new RevokeRolePermissionService(roleValidator, commandManager);
    }

    @Nested
    @DisplayName("revoke 메서드")
    class Revoke {

        @Test
        @DisplayName("성공: RoleValidator → CommandManager 순서로 호출")
        void shouldOrchestrate_ValidatorThenCommandManager() {
            // given
            RevokeRolePermissionCommand command = RolePermissionCommandFixtures.revokeCommand();
            Role role = RoleFixture.create();

            given(roleValidator.findExistingOrThrow(any(RoleId.class))).willReturn(role);

            // when
            sut.revoke(command);

            // then
            then(roleValidator).should().findExistingOrThrow(any(RoleId.class));
            then(commandManager)
                    .should()
                    .deleteAll(
                            RoleId.of(command.roleId()),
                            command.permissionIds().stream().map(PermissionId::of).toList());
        }
    }
}
