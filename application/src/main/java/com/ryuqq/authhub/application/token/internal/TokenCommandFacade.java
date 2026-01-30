package com.ryuqq.authhub.application.token.internal;

import com.ryuqq.authhub.application.token.manager.RefreshTokenCacheCommandManager;
import com.ryuqq.authhub.application.token.manager.RefreshTokenCommandManager;
import com.ryuqq.authhub.domain.token.vo.RefreshToken;
import com.ryuqq.authhub.domain.user.id.UserId;
import org.springframework.stereotype.Component;

/**
 * TokenCommandFacade - Refresh Token 저장/무효화 Facade
 *
 * <p>Refresh Token의 저장 및 무효화를 담당하는 Facade입니다.
 *
 * <p><strong>책임:</strong>
 *
 * <ul>
 *   <li>Refresh Token 저장 (RDB + Cache)
 *   <li>Refresh Token 무효화 (RDB + Cache)
 * </ul>
 *
 * <p><strong>주의:</strong> 트랜잭션 외부에서 호출해야 합니다. Redis 저장은 외부 시스템 호출입니다.
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class TokenCommandFacade {

    private final RefreshTokenCommandManager refreshTokenCommandManager;
    private final RefreshTokenCacheCommandManager refreshTokenCacheCommandManager;

    public TokenCommandFacade(
            RefreshTokenCommandManager refreshTokenCommandManager,
            RefreshTokenCacheCommandManager refreshTokenCacheCommandManager) {
        this.refreshTokenCommandManager = refreshTokenCommandManager;
        this.refreshTokenCacheCommandManager = refreshTokenCacheCommandManager;
    }

    /**
     * Refresh Token 저장 (RDB + Cache)
     *
     * @param refreshToken RefreshToken VO
     */
    public void persistRefreshToken(RefreshToken refreshToken) {
        refreshTokenCommandManager.persist(refreshToken.userId(), refreshToken.token());
        refreshTokenCacheCommandManager.save(
                refreshToken.userId(), refreshToken.token(), refreshToken.expiresInSeconds());
    }

    /**
     * Refresh Token 무효화 (로그아웃 시)
     *
     * @param userId 사용자 ID (Value Object)
     */
    public void revokeTokensByUserId(UserId userId) {
        refreshTokenCacheCommandManager.deleteByUserId(userId);
        refreshTokenCommandManager.deleteByUserId(userId);
    }

    /**
     * Refresh Token 무효화 (토큰 기준)
     *
     * <p>Cache에서만 삭제합니다. RDB 삭제는 사용자 ID 기준으로만 수행합니다.
     *
     * @param refreshToken Refresh Token 값
     */
    public void revokeToken(String refreshToken) {
        refreshTokenCacheCommandManager.deleteByToken(refreshToken);
    }
}
