package com.ryuqq.authhub.application.permission.service.command;

import com.ryuqq.authhub.application.permission.assembler.PermissionAssembler;
import com.ryuqq.authhub.application.permission.dto.command.CreatePermissionCommand;
import com.ryuqq.authhub.application.permission.dto.response.PermissionResponse;
import com.ryuqq.authhub.application.permission.factory.command.PermissionCommandFactory;
import com.ryuqq.authhub.application.permission.manager.command.PermissionTransactionManager;
import com.ryuqq.authhub.application.permission.port.in.command.CreatePermissionUseCase;
import com.ryuqq.authhub.application.permission.validator.PermissionValidator;
import com.ryuqq.authhub.domain.permission.aggregate.Permission;
import com.ryuqq.authhub.domain.permission.vo.Action;
import com.ryuqq.authhub.domain.permission.vo.PermissionKey;
import com.ryuqq.authhub.domain.permission.vo.Resource;
import org.springframework.stereotype.Service;

/**
 * CreatePermissionService - 권한 생성 Service
 *
 * <p>CreatePermissionUseCase를 구현합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code @Service} 어노테이션
 *   <li>{@code @Transactional} 직접 사용 금지 (Manager/Facade 책임)
 *   <li>Validator → Factory → Manager/Facade → Assembler 흐름
 *   <li>Port 직접 호출 금지
 *   <li>Lombok 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Service
public class CreatePermissionService implements CreatePermissionUseCase {

    private final PermissionValidator validator;
    private final PermissionCommandFactory commandFactory;
    private final PermissionTransactionManager transactionManager;
    private final PermissionAssembler assembler;

    public CreatePermissionService(
            PermissionValidator validator,
            PermissionCommandFactory commandFactory,
            PermissionTransactionManager transactionManager,
            PermissionAssembler assembler) {
        this.validator = validator;
        this.commandFactory = commandFactory;
        this.transactionManager = transactionManager;
        this.assembler = assembler;
    }

    @Override
    public PermissionResponse execute(CreatePermissionCommand command) {
        // 1. Validator: 중복 키 검사
        PermissionKey key =
                PermissionKey.of(Resource.of(command.resource()), Action.of(command.action()));
        validator.validateKeyNotDuplicated(key);

        // 2. Factory: Command → Domain
        Permission permission = commandFactory.create(command);

        // 3. Manager: 영속화
        Permission saved = transactionManager.persist(permission);

        // 4. Assembler: Response 변환
        return assembler.toResponse(saved);
    }
}
