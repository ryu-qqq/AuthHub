package com.ryuqq.authhub.application.user.usecase;

import com.ryuqq.authhub.application.user.assembler.UserAssembler;
import com.ryuqq.authhub.application.user.dto.command.UpdateUserCommand;
import com.ryuqq.authhub.application.user.dto.response.UserResponse;
import com.ryuqq.authhub.application.user.port.out.command.UserPersistencePort;
import com.ryuqq.authhub.application.user.port.out.query.UserQueryPort;
import com.ryuqq.authhub.domain.common.Clock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * UpdateUserUseCaseImpl - 사용자 정보 수정 UseCase 구현체
 *
 * <p>사용자 프로필 정보 수정 비즈니스 로직을 구현합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 * <ul>
 *   <li>Transaction 내 외부 API 호출 금지</li>
 *   <li>Domain 검증 후 영속화</li>
 *   <li>Response DTO로 변환 후 반환 (Assembler 사용)</li>
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Service
@Transactional
public class UpdateUserUseCaseImpl implements UpdateUserUseCase {

    private final UserQueryPort userQueryPort;
    private final UserPersistencePort userPersistencePort;
    private final UserAssembler userAssembler;
    private final Clock clock;

    public UpdateUserUseCaseImpl(
            UserQueryPort userQueryPort,
            UserPersistencePort userPersistencePort,
            UserAssembler userAssembler,
            Clock clock) {
        this.userQueryPort = userQueryPort;
        this.userPersistencePort = userPersistencePort;
        this.userAssembler = userAssembler;
        this.clock = clock;
    }

    @Override
    public UserResponse execute(UpdateUserCommand command) {
        // TODO: Red Phase - 테스트 실패 확인용 최소 구현
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
