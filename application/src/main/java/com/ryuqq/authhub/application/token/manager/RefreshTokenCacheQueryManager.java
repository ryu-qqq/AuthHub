package com.ryuqq.authhub.application.token.manager;

import com.ryuqq.authhub.application.token.port.out.query.RefreshTokenCacheQueryPort;
import com.ryuqq.authhub.domain.user.id.UserId;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * RefreshTokenCacheQueryManager - Refresh Token 캐시 Query 관리자
 *
 * <p>Refresh Token 캐시(Redis) 조회를 담당하는 Manager
 *
 * <p>Redis 작업은 트랜잭션 없이 수행 (외부 시스템)
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class RefreshTokenCacheQueryManager {

    private final RefreshTokenCacheQueryPort cacheQueryPort;

    public RefreshTokenCacheQueryManager(RefreshTokenCacheQueryPort cacheQueryPort) {
        this.cacheQueryPort = cacheQueryPort;
    }

    /**
     * 사용자 ID로 Refresh Token 조회
     *
     * @param userId 사용자 ID (Value Object)
     * @return Refresh Token (Optional)
     */
    public Optional<String> findByUserId(UserId userId) {
        return cacheQueryPort.findByUserId(userId);
    }

    /**
     * Refresh Token으로 사용자 ID 조회
     *
     * @param refreshToken Refresh Token 값
     * @return 사용자 ID (Optional)
     */
    public Optional<UserId> findUserIdByToken(String refreshToken) {
        return cacheQueryPort.findUserIdByToken(refreshToken);
    }
}
