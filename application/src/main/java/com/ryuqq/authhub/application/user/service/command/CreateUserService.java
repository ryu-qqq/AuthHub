package com.ryuqq.authhub.application.user.service.command;

import com.ryuqq.authhub.application.user.dto.command.CreateUserCommand;
import com.ryuqq.authhub.application.user.factory.UserCommandFactory;
import com.ryuqq.authhub.application.user.manager.UserCommandManager;
import com.ryuqq.authhub.application.user.port.in.command.CreateUserUseCase;
import com.ryuqq.authhub.application.user.validator.UserValidator;
import com.ryuqq.authhub.domain.organization.id.OrganizationId;
import com.ryuqq.authhub.domain.user.aggregate.User;
import com.ryuqq.authhub.domain.user.vo.Identifier;
import com.ryuqq.authhub.domain.user.vo.PhoneNumber;
import org.springframework.stereotype.Service;

/**
 * CreateUserService - 사용자 생성 Service
 *
 * <p>CreateUserUseCase를 구현합니다.
 *
 * <p>SVC-001: @Service 어노테이션 필수.
 *
 * <p>SVC-002: UseCase(Port-In) 인터페이스 구현 필수.
 *
 * <p>SVC-006: @Transactional 금지 → Manager에서 처리.
 *
 * <p>SVC-007: Service에 비즈니스 로직 금지 → 오케스트레이션만.
 *
 * @author development-team
 * @since 1.0.0
 */
@Service
public class CreateUserService implements CreateUserUseCase {

    private final UserValidator validator;
    private final UserCommandFactory commandFactory;
    private final UserCommandManager commandManager;

    public CreateUserService(
            UserValidator validator,
            UserCommandFactory commandFactory,
            UserCommandManager commandManager) {
        this.validator = validator;
        this.commandFactory = commandFactory;
        this.commandManager = commandManager;
    }

    @Override
    public String execute(CreateUserCommand command) {
        // 1. Validator: 식별자/전화번호 중복 검증
        OrganizationId organizationId = OrganizationId.of(command.organizationId());
        Identifier identifier = Identifier.of(command.identifier());
        validator.validateIdentifierNotDuplicated(organizationId, identifier);

        PhoneNumber phoneNumber = PhoneNumber.fromNullable(command.phoneNumber());
        validator.validatePhoneNumberNotDuplicated(organizationId, phoneNumber);

        // 2. Factory: Command → Domain 생성
        User user = commandFactory.create(command);

        // 3. Manager: 영속화 및 ID 반환
        return commandManager.persist(user);
    }
}
