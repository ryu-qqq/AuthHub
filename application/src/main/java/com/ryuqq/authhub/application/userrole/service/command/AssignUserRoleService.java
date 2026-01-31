package com.ryuqq.authhub.application.userrole.service.command;

import com.ryuqq.authhub.application.userrole.dto.command.AssignUserRoleCommand;
import com.ryuqq.authhub.application.userrole.internal.AssignUserRoleCoordinator;
import com.ryuqq.authhub.application.userrole.manager.UserRoleCommandManager;
import com.ryuqq.authhub.application.userrole.port.in.command.AssignUserRoleUseCase;
import com.ryuqq.authhub.domain.userrole.aggregate.UserRole;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * AssignUserRoleService - 사용자 역할 할당 Service
 *
 * <p>사용자에게 역할을 할당하는 비즈니스 로직을 처리합니다.
 *
 * <p><strong>처리 흐름:</strong>
 *
 * <ol>
 *   <li>Coordinator에서 검증 + 필터링 + UserRole 생성
 *   <li>생성된 UserRole 목록이 비어있으면 조기 반환
 *   <li>CommandManager를 통해 영속화
 * </ol>
 *
 * @author development-team
 * @since 1.0.0
 */
@Service
public class AssignUserRoleService implements AssignUserRoleUseCase {

    private final AssignUserRoleCoordinator coordinator;
    private final UserRoleCommandManager commandManager;

    public AssignUserRoleService(
            AssignUserRoleCoordinator coordinator, UserRoleCommandManager commandManager) {
        this.coordinator = coordinator;
        this.commandManager = commandManager;
    }

    @Override
    public void assign(AssignUserRoleCommand command) {
        List<UserRole> userRoles = coordinator.coordinate(command.userId(), command.roleIds());

        if (userRoles.isEmpty()) {
            return;
        }

        commandManager.persistAll(userRoles);
    }
}
