package com.ryuqq.authhub.application.user.service.query;

import com.ryuqq.authhub.application.user.assembler.UserAssembler;
import com.ryuqq.authhub.application.user.dto.query.GetUserQuery;
import com.ryuqq.authhub.application.user.dto.response.UserResponse;
import com.ryuqq.authhub.application.user.manager.query.UserReadManager;
import com.ryuqq.authhub.application.user.port.in.query.GetUserUseCase;
import com.ryuqq.authhub.domain.user.aggregate.User;
import com.ryuqq.authhub.domain.user.identifier.UserId;
import org.springframework.stereotype.Service;

/**
 * GetUserService - 사용자 단건 조회 Service
 *
 * <p>GetUserUseCase를 구현합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code @Service} 어노테이션
 *   <li>{@code @Transactional} 직접 사용 금지 (Manager/Facade 책임)
 *   <li>Manager → Assembler 흐름
 *   <li>Port 직접 호출 금지
 *   <li>Lombok 금지
 * </ul>
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
    public UserResponse execute(GetUserQuery query) {
        // 1. Manager: 조회
        UserId userId = UserId.of(query.userId());
        User user = readManager.getById(userId);

        // 2. Assembler: Response 변환
        return assembler.toResponse(user);
    }
}
