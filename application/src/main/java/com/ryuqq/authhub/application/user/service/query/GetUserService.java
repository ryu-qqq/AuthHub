package com.ryuqq.authhub.application.user.service.query;

import com.ryuqq.authhub.application.user.assembler.UserAssembler;
import com.ryuqq.authhub.application.user.dto.response.UserResult;
import com.ryuqq.authhub.application.user.manager.UserReadManager;
import com.ryuqq.authhub.application.user.port.in.query.GetUserUseCase;
import com.ryuqq.authhub.domain.user.aggregate.User;
import com.ryuqq.authhub.domain.user.id.UserId;
import org.springframework.stereotype.Service;

/**
 * GetUserService - 사용자 단건 조회 Service
 *
 * <p>GetUserUseCase를 구현합니다.
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
public class GetUserService implements GetUserUseCase {

    private final UserReadManager readManager;
    private final UserAssembler assembler;

    public GetUserService(UserReadManager readManager, UserAssembler assembler) {
        this.readManager = readManager;
        this.assembler = assembler;
    }

    @Override
    public UserResult execute(String userId) {
        // 1. Manager: ID로 사용자 조회
        UserId id = UserId.of(userId);
        User user = readManager.findById(id);

        // 2. Assembler: Domain → Result
        return assembler.toResult(user);
    }
}
