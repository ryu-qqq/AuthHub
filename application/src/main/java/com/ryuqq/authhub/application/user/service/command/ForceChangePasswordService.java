package com.ryuqq.authhub.application.user.service.command;

import com.ryuqq.authhub.application.common.time.TimeProvider;
import com.ryuqq.authhub.application.user.dto.command.ForceChangePasswordCommand;
import com.ryuqq.authhub.application.user.manager.UserCommandManager;
import com.ryuqq.authhub.application.user.manager.UserReadManager;
import com.ryuqq.authhub.application.user.port.in.command.ForceChangePasswordUseCase;
import com.ryuqq.authhub.application.user.port.out.client.PasswordEncoderClient;
import com.ryuqq.authhub.domain.user.aggregate.User;
import com.ryuqq.authhub.domain.user.id.UserId;
import com.ryuqq.authhub.domain.user.vo.HashedPassword;
import org.springframework.stereotype.Service;

/**
 * ForceChangePasswordService - 강제 비밀번호 변경 Service
 *
 * <p>현재 비밀번호 검증 없이 비밀번호를 변경합니다. Internal API (M2M) 전용입니다.
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
public class ForceChangePasswordService implements ForceChangePasswordUseCase {

    private final UserReadManager readManager;
    private final UserCommandManager commandManager;
    private final PasswordEncoderClient passwordEncoderClient;
    private final TimeProvider timeProvider;

    public ForceChangePasswordService(
            UserReadManager readManager,
            UserCommandManager commandManager,
            PasswordEncoderClient passwordEncoderClient,
            TimeProvider timeProvider) {
        this.readManager = readManager;
        this.commandManager = commandManager;
        this.passwordEncoderClient = passwordEncoderClient;
        this.timeProvider = timeProvider;
    }

    @Override
    public void execute(ForceChangePasswordCommand command) {
        UserId userId = UserId.of(command.userId());

        // 1. 사용자 조회 (존재 여부 검증)
        User user = readManager.findById(userId);

        // 2. 새 비밀번호 해싱 및 변경 (현재 비밀번호 검증 없음)
        HashedPassword newHashedPassword =
                HashedPassword.of(passwordEncoderClient.hash(command.newPassword()));
        user.changePassword(newHashedPassword, timeProvider.now());

        // 3. 영속화
        commandManager.persist(user);
    }
}
