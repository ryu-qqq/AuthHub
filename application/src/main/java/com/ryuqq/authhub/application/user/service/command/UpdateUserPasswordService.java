package com.ryuqq.authhub.application.user.service.command;

import com.ryuqq.authhub.application.user.dto.command.UpdateUserPasswordCommand;
import com.ryuqq.authhub.application.user.manager.command.UserTransactionManager;
import com.ryuqq.authhub.application.user.manager.query.UserReadManager;
import com.ryuqq.authhub.application.user.port.in.command.UpdateUserPasswordUseCase;
import com.ryuqq.authhub.application.user.port.out.client.PasswordEncoderPort;
import com.ryuqq.authhub.domain.user.aggregate.User;
import com.ryuqq.authhub.domain.user.exception.InvalidPasswordException;
import com.ryuqq.authhub.domain.user.identifier.UserId;
import java.time.Clock;
import org.springframework.stereotype.Service;

/**
 * UpdateUserPasswordService - 비밀번호 변경 Service
 *
 * <p>UpdateUserPasswordUseCase를 구현합니다.
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
public class UpdateUserPasswordService implements UpdateUserPasswordUseCase {

    private final UserTransactionManager transactionManager;
    private final UserReadManager readManager;
    private final PasswordEncoderPort passwordEncoderPort;
    private final Clock clock;

    public UpdateUserPasswordService(
            UserTransactionManager transactionManager,
            UserReadManager readManager,
            PasswordEncoderPort passwordEncoderPort,
            Clock clock) {
        this.transactionManager = transactionManager;
        this.readManager = readManager;
        this.passwordEncoderPort = passwordEncoderPort;
        this.clock = clock;
    }

    @Override
    public void execute(UpdateUserPasswordCommand command) {
        // 1. 기존 사용자 조회
        UserId userId = UserId.of(command.userId());
        User user = readManager.getById(userId);

        // 2. 현재 비밀번호 검증
        if (!passwordEncoderPort.matches(command.currentPassword(), user.getHashedPassword())) {
            throw new InvalidPasswordException();
        }

        // 3. 새 비밀번호 해싱 및 변경
        String newHashedPassword = passwordEncoderPort.hash(command.newPassword());
        User updatedUser = user.changePassword(newHashedPassword, clock);

        // 4. Manager: 영속화
        transactionManager.persist(updatedUser);
    }
}
