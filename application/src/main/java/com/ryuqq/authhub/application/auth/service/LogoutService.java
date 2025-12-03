package com.ryuqq.authhub.application.auth.service;

import com.ryuqq.authhub.application.auth.dto.command.LogoutCommand;
import com.ryuqq.authhub.application.auth.manager.TokenManager;
import com.ryuqq.authhub.application.auth.port.in.LogoutUseCase;
import com.ryuqq.authhub.domain.user.identifier.UserId;
import org.springframework.stereotype.Service;

/**
 * LogoutService - 로그아웃 서비스 구현체
 *
 * <p>로그아웃 비즈니스 로직을 처리합니다.
 *
 * <p><strong>주의:</strong>
 *
 * <ul>
 *   <li>TokenManager가 Cache/RDB 양쪽 토큰 삭제 처리
 *   <li>토큰 삭제는 트랜잭션 외부에서 수행
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Service
public class LogoutService implements LogoutUseCase {

    private final TokenManager tokenManager;

    public LogoutService(TokenManager tokenManager) {
        this.tokenManager = tokenManager;
    }

    @Override
    public void execute(LogoutCommand command) {
        UserId userId = UserId.of(command.userId());
        tokenManager.revokeTokensByUserId(userId);
    }
}
