package com.ryuqq.authhub.application.user.service;

import com.ryuqq.authhub.application.user.assembler.UserQueryAssembler;
import com.ryuqq.authhub.application.user.dto.response.UserResponse;
import com.ryuqq.authhub.application.user.port.in.GetUserUseCase;
import com.ryuqq.authhub.application.user.port.out.query.UserQueryPort;
import com.ryuqq.authhub.domain.user.aggregate.User;
import com.ryuqq.authhub.domain.user.exception.UserNotFoundException;
import com.ryuqq.authhub.domain.user.identifier.UserId;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * GetUserService - 사용자 조회 UseCase 구현체
 *
 * <p>사용자 정보 조회를 수행합니다.
 *
 * <p><strong>Query UseCase:</strong>
 *
 * <ul>
 *   <li>읽기 전용 트랜잭션 (@Transactional(readOnly = true))
 *   <li>데이터 변경 없음 - Manager 사용 안함
 *   <li>QueryAssembler로 Response DTO 변환
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>도메인 객체 직접 반환 금지 - Response DTO로 변환
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Service
@Transactional(readOnly = true)
public class GetUserService implements GetUserUseCase {

    private final UserQueryPort userQueryPort;
    private final UserQueryAssembler userQueryAssembler;

    public GetUserService(UserQueryPort userQueryPort, UserQueryAssembler userQueryAssembler) {
        this.userQueryPort = userQueryPort;
        this.userQueryAssembler = userQueryAssembler;
    }

    @Override
    public UserResponse execute(UUID userId) {
        // 1. User 조회
        User user =
                userQueryPort
                        .findById(UserId.of(userId))
                        .orElseThrow(() -> new UserNotFoundException(userId));

        // 2. Response 변환 및 반환
        return userQueryAssembler.toResponse(user);
    }
}
