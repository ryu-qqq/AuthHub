package com.ryuqq.authhub.application.permission.service.command;

import com.ryuqq.authhub.application.permission.dto.command.DeletePermissionCommand;
import com.ryuqq.authhub.application.permission.manager.command.PermissionTransactionManager;
import com.ryuqq.authhub.application.permission.manager.query.PermissionReadManager;
import com.ryuqq.authhub.application.permission.port.in.command.DeletePermissionUseCase;
import com.ryuqq.authhub.domain.permission.aggregate.Permission;
import com.ryuqq.authhub.domain.permission.identifier.PermissionId;
import java.time.Clock;
import org.springframework.stereotype.Service;

/**
 * DeletePermissionService - 권한 삭제 Service
 *
 * <p>DeletePermissionUseCase를 구현합니다.
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
public class DeletePermissionService implements DeletePermissionUseCase {

    private final PermissionTransactionManager transactionManager;
    private final PermissionReadManager readManager;
    private final Clock clock;

    public DeletePermissionService(
            PermissionTransactionManager transactionManager,
            PermissionReadManager readManager,
            Clock clock) {
        this.transactionManager = transactionManager;
        this.readManager = readManager;
        this.clock = clock;
    }

    @Override
    public void execute(DeletePermissionCommand command) {
        // 1. 기존 Permission 조회
        PermissionId permissionId = PermissionId.of(command.permissionId());
        Permission existing = readManager.getById(permissionId);

        // 2. 삭제 (SYSTEM 권한이면 예외 발생)
        Permission deleted = existing.delete(clock);

        // 3. Manager: 영속화 (soft delete)
        transactionManager.persist(deleted);
    }
}
