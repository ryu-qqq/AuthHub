package com.ryuqq.authhub.adapter.out.persistence.auth.cache.adapter;

import com.ryuqq.authhub.application.auth.port.out.cache.RefreshTokenCachePort;
import com.ryuqq.authhub.domain.user.identifier.UserId;
import java.time.Duration;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * RefreshTokenCacheAdapter - RefreshToken 캐시 어댑터
 *
 * <p>RefreshTokenCachePort 구현체입니다. Redis를 사용하여 RefreshToken을 캐싱합니다.
 *
 * <p><strong>Key 패턴:</strong>
 *
 * <ul>
 *   <li>UserId → Token: {@code refresh_token::user::{userId}}
 *   <li>Token → UserId: {@code refresh_token::token::{token}}
 * </ul>
 *
 * <p><strong>양방향 매핑:</strong>
 *
 * <ul>
 *   <li>findByUserId: userId로 token 조회
 *   <li>findUserIdByToken: token으로 userId 역조회
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>@Transactional 사용 금지 (Redis)
 *   <li>비즈니스 로직 금지 (단순 저장/조회만)
 *   <li>TTL 필수 설정
 *   <li>KEYS 명령어 절대 금지
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@Component
public class RefreshTokenCacheAdapter implements RefreshTokenCachePort {

    private static final String USER_KEY_PREFIX = "refresh_token::user::";
    private static final String TOKEN_KEY_PREFIX = "refresh_token::token::";

    private final RedisTemplate<String, String> redisTemplate;

    public RefreshTokenCacheAdapter(RedisTemplate<String, String> redisTemplate) {
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
        String userIdString = userId.value().toString();

        // userId → token 매핑 저장
        String userKey = generateUserKey(userIdString);
        redisTemplate.opsForValue().set(userKey, refreshToken, ttl);

        // token → userId 역방향 매핑 저장
        String tokenKey = generateTokenKey(refreshToken);
        redisTemplate.opsForValue().set(tokenKey, userIdString, ttl);
    }

    /**
     * UserId로 RefreshToken 조회
     *
     * @param userId 사용자 ID (Value Object)
     * @return RefreshToken 문자열 (Optional)
     */
    @Override
    public Optional<String> findByUserId(UserId userId) {
        String userKey = generateUserKey(userId.value().toString());
        String token = redisTemplate.opsForValue().get(userKey);
        return Optional.ofNullable(token);
    }

    /**
     * RefreshToken으로 UserId 조회
     *
     * @param refreshToken RefreshToken 값
     * @return UserId (Optional)
     */
    @Override
    public Optional<UserId> findUserIdByToken(String refreshToken) {
        String tokenKey = generateTokenKey(refreshToken);
        String userIdString = redisTemplate.opsForValue().get(tokenKey);

        if (userIdString == null) {
            return Optional.empty();
        }

        return Optional.of(UserId.of(UUID.fromString(userIdString)));
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
        String userKey = generateUserKey(userId.value().toString());

        // 먼저 token 값을 조회하여 역방향 키도 삭제
        String token = redisTemplate.opsForValue().get(userKey);
        if (token != null) {
            String tokenKey = generateTokenKey(token);
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
        String tokenKey = generateTokenKey(refreshToken);

        // 먼저 userId 값을 조회하여 역방향 키도 삭제
        String userIdString = redisTemplate.opsForValue().get(tokenKey);
        if (userIdString != null) {
            String userKey = generateUserKey(userIdString);
            redisTemplate.delete(userKey);
        }

        redisTemplate.delete(tokenKey);
    }

    private String generateUserKey(String userId) {
        return USER_KEY_PREFIX + userId;
    }

    private String generateTokenKey(String token) {
        return TOKEN_KEY_PREFIX + token;
    }
}
