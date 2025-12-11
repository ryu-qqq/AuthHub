package com.ryuqq.authhub.application.role.service.command;

import com.ryuqq.authhub.application.role.dto.command.DeleteRoleCommand;
import com.ryuqq.authhub.application.role.manager.command.RoleTransactionManager;
import com.ryuqq.authhub.application.role.manager.query.RoleReadManager;
import com.ryuqq.authhub.application.role.port.in.command.DeleteRoleUseCase;
import com.ryuqq.authhub.domain.role.aggregate.Role;
import com.ryuqq.authhub.domain.role.identifier.RoleId;
import java.time.Clock;
import org.springframework.stereotype.Service;

/**
 * DeleteRoleService - 역할 삭제 Service
 *
 * <p>DeleteRoleUseCase를 구현합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code @Service} 어노테이션
 *   <li>{@code @Transactional} 직접 사용 금지 (Manager/Facade 책임)
 *   <li>Manager/Facade 흐름
 *   <li>Port 직접 호출 금지
 *   <li>Lombok 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Service
public class DeleteRoleService implements DeleteRoleUseCase {

    private final RoleTransactionManager transactionManager;
    private final RoleReadManager readManager;
    private final Clock clock;

    public DeleteRoleService(
            RoleTransactionManager transactionManager, RoleReadManager readManager, Clock clock) {
        this.transactionManager = transactionManager;
        this.readManager = readManager;
        this.clock = clock;
    }

    @Override
    public void execute(DeleteRoleCommand command) {
        // 1. 기존 Role 조회
        RoleId roleId = RoleId.of(command.roleId());
        Role existing = readManager.getById(roleId);

        // 2. 삭제 (SYSTEM 역할이면 예외 발생)
        Role deleted = existing.delete(clock);

        // 3. Manager: 영속화
        transactionManager.persist(deleted);
    }
}
