package com.ryuqq.authhub.application.user.service.command;

import com.ryuqq.authhub.application.user.assembler.UserRoleAssembler;
import com.ryuqq.authhub.application.user.dto.command.AssignUserRoleCommand;
import com.ryuqq.authhub.application.user.dto.response.UserRoleResponse;
import com.ryuqq.authhub.application.user.factory.command.UserRoleCommandFactory;
import com.ryuqq.authhub.application.user.manager.command.UserRoleTransactionManager;
import com.ryuqq.authhub.application.user.manager.query.UserRoleReadManager;
import com.ryuqq.authhub.application.user.port.in.command.AssignUserRoleUseCase;
import com.ryuqq.authhub.domain.role.identifier.RoleId;
import com.ryuqq.authhub.domain.user.exception.DuplicateUserRoleException;
import com.ryuqq.authhub.domain.user.identifier.UserId;
import com.ryuqq.authhub.domain.user.vo.UserRole;
import org.springframework.stereotype.Service;

/**
 * AssignUserRoleService - 사용자 역할 할당 Service
 *
 * <p>AssignUserRoleUseCase를 구현합니다.
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
public class AssignUserRoleService implements AssignUserRoleUseCase {

    private final UserRoleCommandFactory commandFactory;
    private final UserRoleTransactionManager transactionManager;
    private final UserRoleReadManager readManager;
    private final UserRoleAssembler assembler;

    public AssignUserRoleService(
            UserRoleCommandFactory commandFactory,
            UserRoleTransactionManager transactionManager,
            UserRoleReadManager readManager,
            UserRoleAssembler assembler) {
        this.commandFactory = commandFactory;
        this.transactionManager = transactionManager;
        this.readManager = readManager;
        this.assembler = assembler;
    }

    @Override
    public UserRoleResponse execute(AssignUserRoleCommand command) {
        UserId userId = UserId.of(command.userId());
        RoleId roleId = RoleId.of(command.roleId());

        // 1. 중복 할당 검사
        if (readManager.existsByUserIdAndRoleId(userId, roleId)) {
            throw new DuplicateUserRoleException(command.userId(), command.roleId());
        }

        // 2. Factory: Command → Domain
        UserRole userRole = commandFactory.create(command);

        // 3. Manager: 영속화
        UserRole saved = transactionManager.persist(userRole);

        // 4. Assembler: Response 변환
        return assembler.toResponse(saved);
    }
}
