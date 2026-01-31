package com.ryuqq.authhub.application.user.service.command;

import com.ryuqq.authhub.application.common.dto.command.UpdateContext;
import com.ryuqq.authhub.application.user.dto.command.UpdateUserCommand;
import com.ryuqq.authhub.application.user.factory.UserCommandFactory;
import com.ryuqq.authhub.application.user.manager.UserCommandManager;
import com.ryuqq.authhub.application.user.port.in.command.UpdateUserUseCase;
import com.ryuqq.authhub.application.user.validator.UserValidator;
import com.ryuqq.authhub.domain.user.aggregate.User;
import com.ryuqq.authhub.domain.user.aggregate.UserUpdateData;
import com.ryuqq.authhub.domain.user.id.UserId;
import org.springframework.stereotype.Service;

/**
 * UpdateUserService - 사용자 정보 수정 Service
 *
 * <p>UpdateUserUseCase를 구현합니다.
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
public class UpdateUserService implements UpdateUserUseCase {

    private final UserValidator validator;
    private final UserCommandFactory commandFactory;
    private final UserCommandManager commandManager;

    public UpdateUserService(
            UserValidator validator,
            UserCommandFactory commandFactory,
            UserCommandManager commandManager) {
        this.validator = validator;
        this.commandFactory = commandFactory;
        this.commandManager = commandManager;
    }

    @Override
    public void execute(UpdateUserCommand command) {
        // 1. Factory: UpdateContext 생성
        UpdateContext<UserId, UserUpdateData> context = commandFactory.createUpdateContext(command);

        // 2. Validator: 사용자 존재 여부 검증 및 조회
        User user = validator.findExistingOrThrow(context.id());

        // 3. Domain: 정보 수정 (Domain에서 비즈니스 로직 처리)
        user.update(context.updateData(), context.changedAt());

        // 4. Manager: 영속화
        commandManager.persist(user);
    }
}
