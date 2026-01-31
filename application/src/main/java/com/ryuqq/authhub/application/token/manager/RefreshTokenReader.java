package com.ryuqq.authhub.application.token.manager;

import com.ryuqq.authhub.application.token.port.out.query.RefreshTokenQueryPort;
import com.ryuqq.authhub.domain.user.id.UserId;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * RefreshTokenReader - Refresh Token 조회 Reader
 *
 * <p>Refresh Token 조회를 담당하는 Reader (Cache-Aside 패턴)
 *
 * <p><strong>조회 전략:</strong>
 *
 * <ol>
 *   <li>Cache(Redis)에서 먼저 조회
 *   <li>Cache Miss 시 RDB에서 조회 (fallback)
 *   <li>RDB에서 조회 성공 시 Cache에 저장 (Cache Warming)
 * </ol>
 *
 * <p><strong>주의:</strong> Cache Warming 시 TTL은 기본값 사용 (토큰 만료 시간을 알 수 없으므로)
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class RefreshTokenReader {

    private static final long DEFAULT_CACHE_TTL_SECONDS = 60 * 60 * 24 * 7; // 7일 (기본 TTL)

    private final RefreshTokenCacheQueryManager refreshTokenCacheQueryManager;
    private final RefreshTokenCacheCommandManager refreshTokenCacheCommandManager;
    private final RefreshTokenQueryPort refreshTokenQueryPort;

    public RefreshTokenReader(
            RefreshTokenCacheQueryManager refreshTokenCacheQueryManager,
            RefreshTokenCacheCommandManager refreshTokenCacheCommandManager,
            RefreshTokenQueryPort refreshTokenQueryPort) {
        this.refreshTokenCacheQueryManager = refreshTokenCacheQueryManager;
        this.refreshTokenCacheCommandManager = refreshTokenCacheCommandManager;
        this.refreshTokenQueryPort = refreshTokenQueryPort;
    }

    /**
     * RefreshToken으로 UserId 조회 (Cache → RDB fallback)
     *
     * <p>조회 순서:
     *
     * <ol>
     *   <li>Cache에서 토큰으로 UserId 조회
     *   <li>Cache Miss 시 RDB에서 조회
     *   <li>RDB 조회 성공 시 Cache에 저장 (Cache Warming)
     * </ol>
     *
     * @param refreshToken RefreshToken 값
     * @return UserId (Optional)
     */
    public Optional<UserId> findUserIdByToken(String refreshToken) {
        Optional<UserId> cachedUserId =
                refreshTokenCacheQueryManager.findUserIdByToken(refreshToken);
        if (cachedUserId.isPresent()) {
            return cachedUserId;
        }

        Optional<UserId> dbUserId = refreshTokenQueryPort.findUserIdByToken(refreshToken);

        dbUserId.ifPresent(userId -> warmCache(userId, refreshToken));

        return dbUserId;
    }

    /**
     * UserId로 RefreshToken 조회 (Cache → RDB fallback)
     *
     * @param userId 사용자 ID (Value Object)
     * @return RefreshToken (Optional)
     */
    public Optional<String> findByUserId(UserId userId) {
        Optional<String> cachedToken = refreshTokenCacheQueryManager.findByUserId(userId);
        if (cachedToken.isPresent()) {
            return cachedToken;
        }

        Optional<String> dbToken = refreshTokenQueryPort.findByUserId(userId);

        dbToken.ifPresent(token -> warmCache(userId, token));

        return dbToken;
    }

    private void warmCache(UserId userId, String refreshToken) {
        refreshTokenCacheCommandManager.save(userId, refreshToken, DEFAULT_CACHE_TTL_SECONDS);
    }
}
