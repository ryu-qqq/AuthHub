package com.ryuqq.authhub.application.user.service.command;

import com.ryuqq.authhub.application.common.dto.command.UpdateContext;
import com.ryuqq.authhub.application.user.dto.command.ChangePasswordCommand;
import com.ryuqq.authhub.application.user.factory.UserCommandFactory;
import com.ryuqq.authhub.application.user.manager.UserCommandManager;
import com.ryuqq.authhub.application.user.port.in.command.ChangePasswordUseCase;
import com.ryuqq.authhub.application.user.validator.UserValidator;
import com.ryuqq.authhub.domain.user.aggregate.User;
import com.ryuqq.authhub.domain.user.id.UserId;
import com.ryuqq.authhub.domain.user.vo.HashedPassword;
import org.springframework.stereotype.Service;

/**
 * ChangePasswordService - 비밀번호 변경 Service
 *
 * <p>ChangePasswordUseCase를 구현합니다.
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
public class ChangePasswordService implements ChangePasswordUseCase {

    private final UserValidator validator;
    private final UserCommandFactory commandFactory;
    private final UserCommandManager commandManager;

    public ChangePasswordService(
            UserValidator validator,
            UserCommandFactory commandFactory,
            UserCommandManager commandManager) {
        this.validator = validator;
        this.commandFactory = commandFactory;
        this.commandManager = commandManager;
    }

    @Override
    public void execute(ChangePasswordCommand command) {
        // 1. Factory: PasswordChangeContext 생성 (ID, 새 비밀번호, 변경시간)
        UpdateContext<UserId, HashedPassword> context =
                commandFactory.createPasswordChangeContext(command);

        // 2. Validator: 사용자 조회 및 현재 비밀번호 검증 (한 번에 처리)
        User user = validator.validatePasswordAndFindUser(context.id(), command.currentPassword());

        // 3. Domain: 비밀번호 변경 (Domain에서 비즈니스 로직 처리)
        user.changePassword(context.updateData(), context.changedAt());

        // 4. Manager: 영속화
        commandManager.persist(user);
    }
}
