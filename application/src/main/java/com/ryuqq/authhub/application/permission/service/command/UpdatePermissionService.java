package com.ryuqq.authhub.application.permission.service.command;

import com.ryuqq.authhub.application.permission.assembler.PermissionAssembler;
import com.ryuqq.authhub.application.permission.dto.command.UpdatePermissionCommand;
import com.ryuqq.authhub.application.permission.dto.response.PermissionResponse;
import com.ryuqq.authhub.application.permission.factory.command.PermissionCommandFactory;
import com.ryuqq.authhub.application.permission.manager.command.PermissionTransactionManager;
import com.ryuqq.authhub.application.permission.manager.query.PermissionReadManager;
import com.ryuqq.authhub.application.permission.port.in.command.UpdatePermissionUseCase;
import com.ryuqq.authhub.domain.permission.aggregate.Permission;
import com.ryuqq.authhub.domain.permission.identifier.PermissionId;
import org.springframework.stereotype.Service;

/**
 * UpdatePermissionService - 권한 수정 Service
 *
 * <p>UpdatePermissionUseCase를 구현합니다.
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
public class UpdatePermissionService implements UpdatePermissionUseCase {

    private final PermissionReadManager readManager;
    private final PermissionCommandFactory commandFactory;
    private final PermissionTransactionManager transactionManager;
    private final PermissionAssembler assembler;

    public UpdatePermissionService(
            PermissionReadManager readManager,
            PermissionCommandFactory commandFactory,
            PermissionTransactionManager transactionManager,
            PermissionAssembler assembler) {
        this.readManager = readManager;
        this.commandFactory = commandFactory;
        this.transactionManager = transactionManager;
        this.assembler = assembler;
    }

    @Override
    public PermissionResponse execute(UpdatePermissionCommand command) {
        // 1. 기존 Permission 조회
        PermissionId permissionId = PermissionId.of(command.permissionId());
        Permission existing = readManager.getById(permissionId);

        // 2. Factory: Command 적용 (설명 변경, SYSTEM 권한이면 예외 발생)
        Permission updated = commandFactory.applyUpdate(existing, command);

        // 3. Manager: 영속화
        Permission saved = transactionManager.persist(updated);

        // 4. Assembler: Response 변환
        return assembler.toResponse(saved);
    }
}
