package com.ryuqq.authhub.application.role.service.command;

import com.ryuqq.authhub.application.role.assembler.RoleAssembler;
import com.ryuqq.authhub.application.role.dto.command.UpdateRoleCommand;
import com.ryuqq.authhub.application.role.dto.response.RoleResponse;
import com.ryuqq.authhub.application.role.factory.command.RoleCommandFactory;
import com.ryuqq.authhub.application.role.manager.command.RoleTransactionManager;
import com.ryuqq.authhub.application.role.manager.query.RoleReadManager;
import com.ryuqq.authhub.application.role.port.in.command.UpdateRoleUseCase;
import com.ryuqq.authhub.domain.role.aggregate.Role;
import com.ryuqq.authhub.domain.role.identifier.RoleId;
import org.springframework.stereotype.Service;

/**
 * UpdateRoleService - 역할 수정 Service
 *
 * <p>UpdateRoleUseCase를 구현합니다.
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
public class UpdateRoleService implements UpdateRoleUseCase {

    private final RoleReadManager readManager;
    private final RoleCommandFactory commandFactory;
    private final RoleTransactionManager transactionManager;
    private final RoleAssembler assembler;

    public UpdateRoleService(
            RoleReadManager readManager,
            RoleCommandFactory commandFactory,
            RoleTransactionManager transactionManager,
            RoleAssembler assembler) {
        this.readManager = readManager;
        this.commandFactory = commandFactory;
        this.transactionManager = transactionManager;
        this.assembler = assembler;
    }

    @Override
    public RoleResponse execute(UpdateRoleCommand command) {
        // 1. 기존 Role 조회
        RoleId roleId = RoleId.of(command.roleId());
        Role role = readManager.getById(roleId);

        // 2. Factory: Command 적용 (이름/설명 변경)
        Role updated = commandFactory.applyUpdate(role, command);

        // 3. Manager: 영속화
        Role saved = transactionManager.persist(updated);

        // 4. Assembler: Response 변환
        return assembler.toResponse(saved);
    }
}
