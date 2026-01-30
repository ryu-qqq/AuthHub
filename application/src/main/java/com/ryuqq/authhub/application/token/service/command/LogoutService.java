package com.ryuqq.authhub.application.token.service.command;

import com.ryuqq.authhub.application.token.dto.command.LogoutCommand;
import com.ryuqq.authhub.application.token.internal.TokenCommandFacade;
import com.ryuqq.authhub.application.token.port.in.command.LogoutUseCase;
import com.ryuqq.authhub.domain.user.id.UserId;
import org.springframework.stereotype.Service;

/**
 * LogoutService - 로그아웃 서비스 구현체
 *
 * <p>로그아웃 비즈니스 로직을 처리합니다.
 *
 * <p><strong>주의:</strong>
 *
 * <ul>
 *   <li>TokenCommandFacade가 Cache/RDB 양쪽 토큰 삭제 처리
 *   <li>토큰 삭제는 트랜잭션 외부에서 수행
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Service
public class LogoutService implements LogoutUseCase {

    private final TokenCommandFacade tokenCommandFacade;

    public LogoutService(TokenCommandFacade tokenCommandFacade) {
        this.tokenCommandFacade = tokenCommandFacade;
    }

    @Override
    public void execute(LogoutCommand command) {
        UserId userId = UserId.of(command.userId());
        tokenCommandFacade.revokeTokensByUserId(userId);
    }
}
