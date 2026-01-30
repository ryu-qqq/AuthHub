package com.ryuqq.authhub.application.token.manager;

import com.ryuqq.authhub.application.token.port.out.command.RefreshTokenCacheCommandPort;
import com.ryuqq.authhub.domain.user.id.UserId;
import org.springframework.stereotype.Component;

/**
 * RefreshTokenCacheCommandManager - Refresh Token 캐시 Command 관리자
 *
 * <p>Refresh Token 캐시(Redis) 저장/삭제를 담당하는 Manager
 *
 * <p>Redis 작업은 트랜잭션 없이 수행 (외부 시스템)
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class RefreshTokenCacheCommandManager {

    private final RefreshTokenCacheCommandPort cacheCommandPort;

    public RefreshTokenCacheCommandManager(RefreshTokenCacheCommandPort cacheCommandPort) {
        this.cacheCommandPort = cacheCommandPort;
    }

    /**
     * Refresh Token 캐시 저장
     *
     * @param userId 사용자 ID (Value Object)
     * @param refreshToken Refresh Token 값
     * @param expiresInSeconds 만료 시간 (초)
     */
    public void save(UserId userId, String refreshToken, long expiresInSeconds) {
        cacheCommandPort.save(userId, refreshToken, expiresInSeconds);
    }

    /**
     * Refresh Token 삭제 (사용자 ID 기준)
     *
     * @param userId 사용자 ID (Value Object)
     */
    public void deleteByUserId(UserId userId) {
        cacheCommandPort.deleteByUserId(userId);
    }

    /**
     * Refresh Token 삭제 (토큰 값 기준)
     *
     * @param refreshToken Refresh Token 값
     */
    public void deleteByToken(String refreshToken) {
        cacheCommandPort.deleteByToken(refreshToken);
    }
}
