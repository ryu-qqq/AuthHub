package com.ryuqq.authhub.application.user.service.command;

import com.ryuqq.authhub.application.user.assembler.UserAssembler;
import com.ryuqq.authhub.application.user.dto.command.UpdateUserCommand;
import com.ryuqq.authhub.application.user.dto.response.UserResponse;
import com.ryuqq.authhub.application.user.manager.command.UserTransactionManager;
import com.ryuqq.authhub.application.user.manager.query.UserReadManager;
import com.ryuqq.authhub.application.user.port.in.command.UpdateUserUseCase;
import com.ryuqq.authhub.domain.user.aggregate.User;
import com.ryuqq.authhub.domain.user.exception.DuplicateUserIdentifierException;
import com.ryuqq.authhub.domain.user.identifier.UserId;
import java.time.Clock;
import org.springframework.stereotype.Service;

/**
 * UpdateUserService - 사용자 수정 Service
 *
 * <p>UpdateUserUseCase를 구현합니다.
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
public class UpdateUserService implements UpdateUserUseCase {

    private final UserTransactionManager transactionManager;
    private final UserReadManager readManager;
    private final UserAssembler assembler;
    private final Clock clock;

    public UpdateUserService(
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
    public UserResponse execute(UpdateUserCommand command) {
        // 1. 기존 사용자 조회
        UserId userId = UserId.of(command.userId());
        User user = readManager.getById(userId);

        // 2. 식별자 변경 시 중복 체크
        if (command.identifier() != null && !command.identifier().equals(user.getIdentifier())) {
            if (readManager.existsByTenantIdAndOrganizationIdAndIdentifier(
                    user.getTenantId(), user.getOrganizationId(), command.identifier())) {
                throw new DuplicateUserIdentifierException(
                        user.tenantIdValue(), user.organizationIdValue(), command.identifier());
            }
            user = user.changeIdentifier(command.identifier(), clock);
        }

        // 3. Manager: 영속화
        User saved = transactionManager.persist(user);

        // 4. Assembler: Response 변환
        return assembler.toResponse(saved);
    }
}
