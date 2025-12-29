package com.ryuqq.authhub.application.user.service.command;

import com.ryuqq.authhub.application.user.assembler.UserAssembler;
import com.ryuqq.authhub.application.user.dto.command.CreateUserCommand;
import com.ryuqq.authhub.application.user.dto.response.UserResponse;
import com.ryuqq.authhub.application.user.factory.command.UserCommandFactory;
import com.ryuqq.authhub.application.user.manager.command.UserTransactionManager;
import com.ryuqq.authhub.application.user.manager.query.UserReadManager;
import com.ryuqq.authhub.application.user.port.in.command.CreateUserUseCase;
import com.ryuqq.authhub.domain.organization.identifier.OrganizationId;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import com.ryuqq.authhub.domain.user.aggregate.User;
import com.ryuqq.authhub.domain.user.exception.DuplicateUserIdentifierException;
import com.ryuqq.authhub.domain.user.exception.DuplicateUserPhoneNumberException;
import org.springframework.stereotype.Service;

/**
 * CreateUserService - 사용자 생성 Service
 *
 * <p>CreateUserUseCase를 구현합니다.
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
public class CreateUserService implements CreateUserUseCase {

    private final UserCommandFactory commandFactory;
    private final UserTransactionManager transactionManager;
    private final UserReadManager readManager;
    private final UserAssembler assembler;

    public CreateUserService(
            UserCommandFactory commandFactory,
            UserTransactionManager transactionManager,
            UserReadManager readManager,
            UserAssembler assembler) {
        this.commandFactory = commandFactory;
        this.transactionManager = transactionManager;
        this.readManager = readManager;
        this.assembler = assembler;
    }

    @Override
    public UserResponse execute(CreateUserCommand command) {
        // 1. 중복 식별자 검사
        TenantId tenantId = TenantId.of(command.tenantId());
        OrganizationId organizationId = OrganizationId.of(command.organizationId());

        if (readManager.existsByTenantIdAndOrganizationIdAndIdentifier(
                tenantId, organizationId, command.identifier())) {
            throw new DuplicateUserIdentifierException(
                    command.tenantId(), command.organizationId(), command.identifier());
        }

        // 2. 중복 핸드폰 번호 검사
        if (readManager.existsByTenantIdAndPhoneNumber(tenantId, command.phoneNumber())) {
            throw new DuplicateUserPhoneNumberException(command.tenantId(), command.phoneNumber());
        }

        // 3. Factory: Command → Domain
        User user = commandFactory.create(command);

        // 4. Manager: 영속화
        User saved = transactionManager.persist(user);

        // 5. Assembler: Response 변환
        return assembler.toResponse(saved);
    }
}
