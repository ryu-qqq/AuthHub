package com.ryuqq.authhub.application.auth.manager;

import com.ryuqq.authhub.application.auth.port.out.cache.RefreshTokenCachePort;
import com.ryuqq.authhub.domain.user.identifier.UserId;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * Refresh Token Cache Manager
 *
 * <p>Refresh Token 캐시(Redis) 저장/조회/삭제를 담당하는 Manager
 *
 * <p>Redis 작업은 트랜잭션 없이 수행 (외부 시스템)
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class RefreshTokenCacheManager {

    private final RefreshTokenCachePort refreshTokenCachePort;

    public RefreshTokenCacheManager(RefreshTokenCachePort refreshTokenCachePort) {
        this.refreshTokenCachePort = refreshTokenCachePort;
    }

    /**
     * Refresh Token 캐시 저장
     *
     * @param userId 사용자 ID (Value Object)
     * @param refreshToken Refresh Token 값
     * @param expiresInSeconds 만료 시간 (초)
     */
    public void save(UserId userId, String refreshToken, long expiresInSeconds) {
        refreshTokenCachePort.save(userId, refreshToken, expiresInSeconds);
    }

    /**
     * 사용자 ID로 Refresh Token 조회
     *
     * @param userId 사용자 ID (Value Object)
     * @return Refresh Token (Optional)
     */
    public Optional<String> findByUserId(UserId userId) {
        return refreshTokenCachePort.findByUserId(userId);
    }

    /**
     * Refresh Token으로 사용자 ID 조회
     *
     * @param refreshToken Refresh Token 값
     * @return 사용자 ID (Optional)
     */
    public Optional<UserId> findUserIdByToken(String refreshToken) {
        return refreshTokenCachePort.findUserIdByToken(refreshToken);
    }

    /**
     * Refresh Token 삭제 (사용자 ID 기준)
     *
     * @param userId 사용자 ID (Value Object)
     */
    public void deleteByUserId(UserId userId) {
        refreshTokenCachePort.deleteByUserId(userId);
    }

    /**
     * Refresh Token 삭제 (토큰 값 기준)
     *
     * @param refreshToken Refresh Token 값
     */
    public void deleteByToken(String refreshToken) {
        refreshTokenCachePort.deleteByToken(refreshToken);
    }
}
