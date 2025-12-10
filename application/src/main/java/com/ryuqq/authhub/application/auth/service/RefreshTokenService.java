package com.ryuqq.authhub.application.auth.service;

import com.ryuqq.authhub.application.auth.dto.command.RefreshTokenCommand;
import com.ryuqq.authhub.application.auth.dto.response.TokenResponse;
import com.ryuqq.authhub.application.auth.manager.TokenManager;
import com.ryuqq.authhub.application.auth.port.in.RefreshTokenUseCase;
import org.springframework.stereotype.Service;

/**
 * RefreshTokenService - 토큰 갱신 서비스 구현체
 *
 * <p>Refresh Token을 사용한 토큰 갱신 로직을 처리합니다.
 *
 * <p><strong>주의:</strong>
 *
 * <ul>
 *   <li>TokenManager가 모든 토큰 관련 로직을 처리
 *   <li>토큰 갱신은 트랜잭션 외부에서 수행
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Service
public class RefreshTokenService implements RefreshTokenUseCase {

    private final TokenManager tokenManager;

    public RefreshTokenService(TokenManager tokenManager) {
        this.tokenManager = tokenManager;
    }

    @Override
    public TokenResponse execute(RefreshTokenCommand command) {
        return tokenManager.refreshTokens(command.refreshToken());
    }
}
