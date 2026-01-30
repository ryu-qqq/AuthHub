package com.ryuqq.authhub.application.permission.internal;

import com.ryuqq.authhub.application.common.dto.command.StatusChangeContext;
import com.ryuqq.authhub.application.permission.dto.command.DeletePermissionCommand;
import com.ryuqq.authhub.application.permission.factory.PermissionCommandFactory;
import com.ryuqq.authhub.application.permission.manager.PermissionCommandManager;
import com.ryuqq.authhub.application.permission.validator.PermissionValidator;
import com.ryuqq.authhub.domain.permission.aggregate.Permission;
import com.ryuqq.authhub.domain.permission.id.PermissionId;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * PermissionDeleteCoordinator - 권한 삭제 조정자
 *
 * <p>Permission 삭제 시 필요한 cross-domain 검증을 조정합니다.
 *
 * <p><strong>책임:</strong>
 *
 * <ul>
 *   <li>Permission 존재 여부 검증 (PermissionValidator)
 *   <li>Permission 사용 여부 검증 (PermissionUsageChecker - Role에서 구현)
 *   <li>Domain 삭제 로직 실행
 *   <li>영속화 조정 (PermissionCommandManager)
 * </ul>
 *
 * <p><strong>확장성:</strong>
 *
 * <p>PermissionUsageChecker는 Optional로 주입받아 Role 도메인 구현 전에도 동작합니다. Role 구현 시 RoleValidator가
 * PermissionUsageChecker를 구현하면 자동으로 주입됩니다.
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class PermissionDeleteCoordinator {

    private final PermissionValidator permissionValidator;
    private final Optional<PermissionUsageChecker> permissionUsageChecker;
    private final PermissionCommandFactory commandFactory;
    private final PermissionCommandManager commandManager;

    public PermissionDeleteCoordinator(
            PermissionValidator permissionValidator,
            Optional<PermissionUsageChecker> permissionUsageChecker,
            PermissionCommandFactory commandFactory,
            PermissionCommandManager commandManager) {
        this.permissionValidator = permissionValidator;
        this.permissionUsageChecker = permissionUsageChecker;
        this.commandFactory = commandFactory;
        this.commandManager = commandManager;
    }

    /**
     * Permission 삭제 실행
     *
     * <p>삭제 전 검증:
     *
     * <ul>
     *   <li>Permission 존재 여부 검증
     *   <li>Role에 할당된 권한인지 검증 (PermissionUsageChecker 구현체 존재 시)
     *   <li>SYSTEM 권한 삭제 시 Domain에서 예외 발생
     * </ul>
     *
     * @param command 삭제 Command
     */
    public void execute(DeletePermissionCommand command) {
        // 1. Factory: Command → StatusChangeContext (id, changedAt)
        StatusChangeContext<PermissionId> context = commandFactory.createDeleteContext(command);

        // 2. Validator: Permission 조회 및 존재 여부 검증
        Permission permission = permissionValidator.findExistingOrThrow(context.id());

        // 3. UsageChecker: 권한 사용 여부 검증 (Role 구현 시 활성화)
        permissionUsageChecker.ifPresent(checker -> checker.validateNotInUse(context.id()));

        // 4. Domain: 삭제 (SYSTEM 권한이면 예외 발생)
        permission.delete(context.changedAt());

        // 5. Manager: 영속화
        commandManager.persist(permission);
    }
}
