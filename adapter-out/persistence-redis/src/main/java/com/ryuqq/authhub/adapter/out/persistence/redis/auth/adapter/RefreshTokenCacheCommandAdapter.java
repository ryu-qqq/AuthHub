package com.ryuqq.authhub.adapter.out.persistence.redis.auth.adapter;

import com.ryuqq.authhub.adapter.out.persistence.redis.common.RedisKeyGenerator;
import com.ryuqq.authhub.application.token.port.out.command.RefreshTokenCacheCommandPort;
import com.ryuqq.authhub.domain.user.id.UserId;
import java.time.Duration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * RefreshTokenCacheCommandAdapter - RefreshToken 캐시 Command 어댑터
 *
 * <p>RefreshTokenCacheCommandPort 구현체입니다. Redis를 사용하여 RefreshToken을 저장/삭제합니다.
 *
 * <p><strong>양방향 매핑:</strong>
 *
 * <ul>
 *   <li>UserId → Token: {@code refresh_token::user::{userId}}
 *   <li>Token → UserId: {@code refresh_token::token::{token}}
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>@Transactional 사용 금지 (Redis)
 *   <li>비즈니스 로직 금지 (단순 저장/삭제만)
 *   <li>TTL 필수 설정
 *   <li>KEYS 명령어 절대 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class RefreshTokenCacheCommandAdapter implements RefreshTokenCacheCommandPort {

    private final RedisTemplate<String, String> redisTemplate;

    public RefreshTokenCacheCommandAdapter(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * RefreshToken 캐시 저장
     *
     * <p>양방향 조회를 위해 두 개의 키를 저장합니다:
     *
     * <ul>
     *   <li>userId → token (userId로 token 조회용)
     *   <li>token → userId (token으로 userId 조회용)
     * </ul>
     *
     * @param userId 사용자 ID (Value Object)
     * @param refreshToken RefreshToken 문자열
     * @param expiresInSeconds 만료 시간 (초)
     */
    @Override
    public void save(UserId userId, String refreshToken, long expiresInSeconds) {
        Duration ttl = Duration.ofSeconds(expiresInSeconds);
        String userIdString = userId.value();

        // userId → token 매핑 저장
        String userKey = RedisKeyGenerator.refreshTokenByUser(userIdString);
        redisTemplate.opsForValue().set(userKey, refreshToken, ttl);

        // token → userId 역방향 매핑 저장
        String tokenKey = RedisKeyGenerator.refreshTokenByToken(refreshToken);
        redisTemplate.opsForValue().set(tokenKey, userIdString, ttl);
    }

    /**
     * UserId로 RefreshToken 삭제
     *
     * <p>양방향 매핑을 모두 삭제합니다.
     *
     * @param userId 사용자 ID (Value Object)
     */
    @Override
    public void deleteByUserId(UserId userId) {
        String userKey = RedisKeyGenerator.refreshTokenByUser(userId.value());

        // 먼저 token 값을 조회하여 역방향 키도 삭제
        String token = redisTemplate.opsForValue().get(userKey);
        if (token != null) {
            String tokenKey = RedisKeyGenerator.refreshTokenByToken(token);
            redisTemplate.delete(tokenKey);
        }

        redisTemplate.delete(userKey);
    }

    /**
     * RefreshToken 값으로 삭제
     *
     * <p>양방향 매핑을 모두 삭제합니다.
     *
     * @param refreshToken RefreshToken 값
     */
    @Override
    public void deleteByToken(String refreshToken) {
        String tokenKey = RedisKeyGenerator.refreshTokenByToken(refreshToken);

        // 먼저 userId 값을 조회하여 역방향 키도 삭제
        String userIdString = redisTemplate.opsForValue().get(tokenKey);
        if (userIdString != null) {
            String userKey = RedisKeyGenerator.refreshTokenByUser(userIdString);
            redisTemplate.delete(userKey);
        }

        redisTemplate.delete(tokenKey);
    }
}
