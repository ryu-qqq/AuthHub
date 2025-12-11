package com.ryuqq.authhub.application.user.service.command;

import com.ryuqq.authhub.application.user.assembler.UserAssembler;
import com.ryuqq.authhub.application.user.dto.command.UpdateUserStatusCommand;
import com.ryuqq.authhub.application.user.dto.response.UserResponse;
import com.ryuqq.authhub.application.user.manager.command.UserTransactionManager;
import com.ryuqq.authhub.application.user.manager.query.UserReadManager;
import com.ryuqq.authhub.application.user.port.in.command.UpdateUserStatusUseCase;
import com.ryuqq.authhub.domain.user.aggregate.User;
import com.ryuqq.authhub.domain.user.identifier.UserId;
import com.ryuqq.authhub.domain.user.vo.UserStatus;
import java.time.Clock;
import java.util.Locale;
import org.springframework.stereotype.Service;

/**
 * UpdateUserStatusService - 사용자 상태 변경 Service
 *
 * <p>UpdateUserStatusUseCase를 구현합니다.
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
public class UpdateUserStatusService implements UpdateUserStatusUseCase {

    private final UserTransactionManager transactionManager;
    private final UserReadManager readManager;
    private final UserAssembler assembler;
    private final Clock clock;

    public UpdateUserStatusService(
            UserTransactionManager transactionManager,
            UserReadManager readManager,
            UserAssembler assembler,
            Clock clock) {
        this.transactionManager = transactionManager;
        this.readManager = readManager;
        this.assembler = assembler;
        this.clock = clock;
    }

    @Override
    public UserResponse execute(UpdateUserStatusCommand command) {
        // 1. 기존 사용자 조회
        UserId userId = UserId.of(command.userId());
        User user = readManager.getById(userId);

        // 2. 상태 변경
        UserStatus newStatus = UserStatus.valueOf(command.status().toUpperCase(Locale.ENGLISH));
        User updatedUser = user.changeStatus(newStatus, clock);

        // 3. Manager: 영속화
        User saved = transactionManager.persist(updatedUser);

        // 4. Assembler: Response 변환
        return assembler.toResponse(saved);
    }
}
