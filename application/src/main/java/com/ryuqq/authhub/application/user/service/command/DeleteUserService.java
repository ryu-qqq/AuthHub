package com.ryuqq.authhub.application.user.service.command;

import com.ryuqq.authhub.application.user.dto.command.DeleteUserCommand;
import com.ryuqq.authhub.application.user.manager.command.UserTransactionManager;
import com.ryuqq.authhub.application.user.manager.query.UserReadManager;
import com.ryuqq.authhub.application.user.port.in.command.DeleteUserUseCase;
import com.ryuqq.authhub.domain.user.aggregate.User;
import com.ryuqq.authhub.domain.user.identifier.UserId;
import java.time.Clock;
import org.springframework.stereotype.Service;

/**
 * DeleteUserService - 사용자 삭제 Service
 *
 * <p>DeleteUserUseCase를 구현합니다.
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
public class DeleteUserService implements DeleteUserUseCase {

    private final UserTransactionManager transactionManager;
    private final UserReadManager readManager;
    private final Clock clock;

    public DeleteUserService(
            UserTransactionManager transactionManager, UserReadManager readManager, Clock clock) {
        this.transactionManager = transactionManager;
        this.readManager = readManager;
        this.clock = clock;
    }

    @Override
    public void execute(DeleteUserCommand command) {
        // 1. 기존 사용자 조회
        UserId userId = UserId.of(command.userId());
        User user = readManager.getById(userId);

        // 2. 삭제 처리 (소프트 삭제)
        User deletedUser = user.markAsDeleted(clock);

        // 3. Manager: 영속화
        transactionManager.persist(deletedUser);
    }
}
