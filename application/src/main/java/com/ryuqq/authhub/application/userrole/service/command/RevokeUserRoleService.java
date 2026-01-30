package com.ryuqq.authhub.application.userrole.service.command;

import com.ryuqq.authhub.application.user.validator.UserValidator;
import com.ryuqq.authhub.application.userrole.dto.command.RevokeUserRoleCommand;
import com.ryuqq.authhub.application.userrole.manager.UserRoleCommandManager;
import com.ryuqq.authhub.application.userrole.port.in.command.RevokeUserRoleUseCase;
import com.ryuqq.authhub.domain.role.id.RoleId;
import com.ryuqq.authhub.domain.user.id.UserId;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * RevokeUserRoleService - 사용자 역할 철회 Service
 *
 * <p>사용자로부터 역할을 철회하는 비즈니스 로직을 처리합니다.
 *
 * <p><strong>처리 흐름:</strong>
 *
 * <ol>
 *   <li>User 존재 검증 (UserValidator)
 *   <li>할당된 역할만 삭제 (할당되지 않은 역할은 무시)
 * </ol>
 *
 * @author development-team
 * @since 1.0.0
 */
@Service
public class RevokeUserRoleService implements RevokeUserRoleUseCase {

    private final UserValidator userValidator;
    private final UserRoleCommandManager commandManager;

    public RevokeUserRoleService(
            UserValidator userValidator, UserRoleCommandManager commandManager) {
        this.userValidator = userValidator;
        this.commandManager = commandManager;
    }

    @Override
    public void revoke(RevokeUserRoleCommand command) {

        UserId userId = UserId.of(command.userId());
        List<RoleId> roleIds = command.roleIds().stream().map(RoleId::of).toList();

        // User 존재 검증
        userValidator.findExistingOrThrow(userId);

        // 할당된 역할만 삭제 (할당되지 않은 역할은 무시됨)
        commandManager.deleteAll(userId, roleIds);
    }
}
