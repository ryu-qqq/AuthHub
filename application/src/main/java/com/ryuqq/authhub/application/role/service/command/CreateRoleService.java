package com.ryuqq.authhub.application.role.service.command;

import com.ryuqq.authhub.application.role.assembler.RoleAssembler;
import com.ryuqq.authhub.application.role.dto.command.CreateRoleCommand;
import com.ryuqq.authhub.application.role.dto.response.RoleResponse;
import com.ryuqq.authhub.application.role.factory.command.RoleCommandFactory;
import com.ryuqq.authhub.application.role.manager.command.RoleTransactionManager;
import com.ryuqq.authhub.application.role.manager.query.RoleReadManager;
import com.ryuqq.authhub.application.role.port.in.command.CreateRoleUseCase;
import com.ryuqq.authhub.domain.role.aggregate.Role;
import com.ryuqq.authhub.domain.role.exception.DuplicateRoleNameException;
import com.ryuqq.authhub.domain.role.vo.RoleName;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import org.springframework.stereotype.Service;

/**
 * CreateRoleService - 역할 생성 Service
 *
 * <p>CreateRoleUseCase를 구현합니다.
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
public class CreateRoleService implements CreateRoleUseCase {

    private final RoleCommandFactory commandFactory;
    private final RoleTransactionManager transactionManager;
    private final RoleReadManager readManager;
    private final RoleAssembler assembler;

    public CreateRoleService(
            RoleCommandFactory commandFactory,
            RoleTransactionManager transactionManager,
            RoleReadManager readManager,
            RoleAssembler assembler) {
        this.commandFactory = commandFactory;
        this.transactionManager = transactionManager;
        this.readManager = readManager;
        this.assembler = assembler;
    }

    @Override
    public RoleResponse execute(CreateRoleCommand command) {
        // 1. 중복 이름 검사 (테넌트 범위 내)
        TenantId tenantId = command.tenantId() != null ? TenantId.of(command.tenantId()) : null;
        RoleName roleName = RoleName.of(command.name());
        if (readManager.existsByTenantIdAndName(tenantId, roleName)) {
            if (tenantId != null) {
                throw new DuplicateRoleNameException(tenantId.value(), command.name());
            } else {
                throw new DuplicateRoleNameException(command.name());
            }
        }

        // 2. Factory: Command → Domain
        Role role = commandFactory.create(command);

        // 3. Manager: 영속화
        Role saved = transactionManager.persist(role);

        // 4. Assembler: Response 변환
        return assembler.toResponse(saved);
    }
}
