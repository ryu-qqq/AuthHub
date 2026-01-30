package com.ryuqq.authhub.application.role.internal;

import com.ryuqq.authhub.application.common.dto.command.StatusChangeContext;
import com.ryuqq.authhub.application.role.dto.command.DeleteRoleCommand;
import com.ryuqq.authhub.application.role.factory.RoleCommandFactory;
import com.ryuqq.authhub.application.role.manager.RoleCommandManager;
import com.ryuqq.authhub.application.role.validator.RoleValidator;
import com.ryuqq.authhub.domain.role.aggregate.Role;
import com.ryuqq.authhub.domain.role.id.RoleId;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * RoleDeleteCoordinator - 역할 삭제 조정자
 *
 * <p>Role 삭제 시 필요한 cross-domain 검증을 조정합니다.
 *
 * <p><strong>책임:</strong>
 *
 * <ul>
 *   <li>Role 존재 여부 검증 (RoleValidator)
 *   <li>Role 사용 여부 검증 (RoleUsageChecker - UserRole에서 구현)
 *   <li>Domain 삭제 로직 실행
 *   <li>영속화 조정 (RoleCommandManager)
 * </ul>
 *
 * <p><strong>확장성:</strong>
 *
 * <p>RoleUsageChecker는 Optional로 주입받아 UserRole 도메인 구현 전에도 동작합니다. UserRole 구현 시 UserRoleValidator가
 * RoleUsageChecker를 구현하면 자동으로 주입됩니다.
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class RoleDeleteCoordinator {

    private final RoleValidator roleValidator;
    private final Optional<RoleUsageChecker> roleUsageChecker;
    private final RoleCommandFactory commandFactory;
    private final RoleCommandManager commandManager;

    public RoleDeleteCoordinator(
            RoleValidator roleValidator,
            Optional<RoleUsageChecker> roleUsageChecker,
            RoleCommandFactory commandFactory,
            RoleCommandManager commandManager) {
        this.roleValidator = roleValidator;
        this.roleUsageChecker = roleUsageChecker;
        this.commandFactory = commandFactory;
        this.commandManager = commandManager;
    }

    /**
     * Role 삭제 실행
     *
     * <p>삭제 전 검증:
     *
     * <ul>
     *   <li>Role 존재 여부 검증
     *   <li>User에 할당된 역할인지 검증 (RoleUsageChecker 구현체 존재 시)
     *   <li>SYSTEM 역할 삭제 시 Domain에서 예외 발생
     * </ul>
     *
     * @param command 삭제 Command
     */
    public void execute(DeleteRoleCommand command) {
        // 1. Factory: Command → StatusChangeContext (id, changedAt)
        StatusChangeContext<RoleId> context = commandFactory.createDeleteContext(command);

        // 2. Validator: Role 조회 및 존재 여부 검증
        Role role = roleValidator.findExistingOrThrow(context.id());

        // 3. UsageChecker: 역할 사용 여부 검증 (UserRole 구현 시 활성화)
        roleUsageChecker.ifPresent(checker -> checker.validateNotInUse(context.id()));

        // 4. Domain: 삭제 (SYSTEM 역할이면 예외 발생)
        role.delete(context.changedAt());

        // 5. Manager: 영속화
        commandManager.persist(role);
    }
}
