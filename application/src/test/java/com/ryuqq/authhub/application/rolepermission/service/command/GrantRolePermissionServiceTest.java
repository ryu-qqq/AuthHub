package com.ryuqq.authhub.application.rolepermission.service.command;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import com.ryuqq.authhub.application.rolepermission.dto.command.GrantRolePermissionCommand;
import com.ryuqq.authhub.application.rolepermission.fixture.RolePermissionCommandFixtures;
import com.ryuqq.authhub.application.rolepermission.internal.GrantRolePermissionCoordinator;
import com.ryuqq.authhub.application.rolepermission.manager.RolePermissionCommandManager;
import com.ryuqq.authhub.domain.rolepermission.aggregate.RolePermission;
import com.ryuqq.authhub.domain.rolepermission.fixture.RolePermissionFixture;
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
 * GrantRolePermissionService 단위 테스트
 *
 * <p><strong>테스트 설계 원칙:</strong>
 *
 * <ul>
 *   <li>Service는 오케스트레이션만 담당 → Coordinator, Manager 호출 검증
 *   <li>BDDMockito 스타일로 Given-When-Then 구조 명확화
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("GrantRolePermissionService 단위 테스트")
class GrantRolePermissionServiceTest {

    @Mock private GrantRolePermissionCoordinator coordinator;

    @Mock private RolePermissionCommandManager commandManager;

    private GrantRolePermissionService sut;

    @BeforeEach
    void setUp() {
        sut = new GrantRolePermissionService(coordinator, commandManager);
    }

    @Nested
    @DisplayName("grant 메서드")
    class Grant {

        @Test
        @DisplayName("성공: Coordinator → CommandManager 순서로 호출")
        void shouldOrchestrate_CoordinatorThenCommandManager() {
            // given
            GrantRolePermissionCommand command = RolePermissionCommandFixtures.grantCommand();
            List<RolePermission> rolePermissions = List.of(RolePermissionFixture.createNew());

            given(coordinator.coordinate(command.roleId(), command.permissionIds()))
                    .willReturn(rolePermissions);

            // when
            sut.grant(command);

            // then
            then(coordinator).should().coordinate(command.roleId(), command.permissionIds());
            then(commandManager).should().persistAll(rolePermissions);
        }

        @Test
        @DisplayName("Coordinator가 빈 목록 반환 시 CommandManager 호출하지 않음")
        void shouldNotCallCommandManager_WhenCoordinatorReturnsEmpty() {
            // given
            GrantRolePermissionCommand command = RolePermissionCommandFixtures.grantCommand();

            given(coordinator.coordinate(command.roleId(), command.permissionIds()))
                    .willReturn(List.of());

            // when
            sut.grant(command);

            // then
            then(coordinator).should().coordinate(command.roleId(), command.permissionIds());
            then(commandManager).should(never()).persistAll(anyList());
        }
    }
}
