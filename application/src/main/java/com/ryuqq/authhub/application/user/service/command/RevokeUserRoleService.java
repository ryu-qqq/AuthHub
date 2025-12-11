package com.ryuqq.authhub.application.user.service.command;

import com.ryuqq.authhub.application.user.dto.command.RevokeUserRoleCommand;
import com.ryuqq.authhub.application.user.manager.command.UserRoleTransactionManager;
import com.ryuqq.authhub.application.user.manager.query.UserRoleReadManager;
import com.ryuqq.authhub.application.user.port.in.command.RevokeUserRoleUseCase;
import com.ryuqq.authhub.domain.role.identifier.RoleId;
import com.ryuqq.authhub.domain.user.exception.UserRoleNotFoundException;
import com.ryuqq.authhub.domain.user.identifier.UserId;
import org.springframework.stereotype.Service;

/**
 * RevokeUserRoleService - 사용자 역할 해제 Service
 *
 * <p>RevokeUserRoleUseCase를 구현합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code @Service} 어노테이션
 *   <li>{@code @Transactional} 직접 사용 금지 (Manager/Facade 책임)
 *   <li>Factory → Manager/Facade → Assembler 흐름
 *   <li>Port 직접 호출 금지
 *   <li>Lombok 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Service
public class RevokeUserRoleService implements RevokeUserRoleUseCase {

    private final UserRoleTransactionManager transactionManager;
    private final UserRoleReadManager readManager;

    public RevokeUserRoleService(
            UserRoleTransactionManager transactionManager, UserRoleReadManager readManager) {
        this.transactionManager = transactionManager;
        this.readManager = readManager;
    }

    @Override
    public void execute(RevokeUserRoleCommand command) {
        UserId userId = UserId.of(command.userId());
        RoleId roleId = RoleId.of(command.roleId());

        // 1. 존재 확인
        if (!readManager.existsByUserIdAndRoleId(userId, roleId)) {
            throw new UserRoleNotFoundException(command.userId(), command.roleId());
        }

        // 2. Manager: 삭제
        transactionManager.delete(userId, roleId);
    }
}
