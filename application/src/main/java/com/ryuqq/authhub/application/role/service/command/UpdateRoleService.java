package com.ryuqq.authhub.application.role.service.command;

import com.ryuqq.authhub.application.role.assembler.RoleAssembler;
import com.ryuqq.authhub.application.role.dto.command.UpdateRoleCommand;
import com.ryuqq.authhub.application.role.dto.response.RoleResponse;
import com.ryuqq.authhub.application.role.manager.command.RoleTransactionManager;
import com.ryuqq.authhub.application.role.manager.query.RoleReadManager;
import com.ryuqq.authhub.application.role.port.in.command.UpdateRoleUseCase;
import com.ryuqq.authhub.domain.role.aggregate.Role;
import com.ryuqq.authhub.domain.role.identifier.RoleId;
import com.ryuqq.authhub.domain.role.vo.RoleDescription;
import com.ryuqq.authhub.domain.role.vo.RoleName;
import java.time.Clock;
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

    private final RoleTransactionManager transactionManager;
    private final RoleReadManager readManager;
    private final RoleAssembler assembler;
    private final Clock clock;

    public UpdateRoleService(
            RoleTransactionManager transactionManager,
            RoleReadManager readManager,
            RoleAssembler assembler,
            Clock clock) {
        this.transactionManager = transactionManager;
        this.readManager = readManager;
        this.assembler = assembler;
        this.clock = clock;
    }

    @Override
    public RoleResponse execute(UpdateRoleCommand command) {
        // 1. 기존 Role 조회
        RoleId roleId = RoleId.of(command.roleId());
        Role existing = readManager.getById(roleId);

        // 2. 이름 변경 (SYSTEM 역할이면 예외 발생)
        Role updated = existing;
        if (command.name() != null && !command.name().isBlank()) {
            RoleName newName = RoleName.of(command.name());
            updated = updated.changeName(newName, clock);
        }

        // 3. 설명 변경 (SYSTEM 역할이면 예외 발생)
        if (command.description() != null) {
            RoleDescription newDescription = RoleDescription.of(command.description());
            updated = updated.changeDescription(newDescription, clock);
        }

        // 4. Manager: 영속화
        Role saved = transactionManager.persist(updated);

        // 5. Assembler: Response 변환
        return assembler.toResponse(saved);
    }
}
