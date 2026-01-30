package com.ryuqq.authhub.adapter.out.persistence.redis.auth.adapter;

import com.ryuqq.authhub.adapter.out.persistence.redis.common.RedisKeyGenerator;
import com.ryuqq.authhub.application.token.port.out.query.RefreshTokenCacheQueryPort;
import com.ryuqq.authhub.domain.user.id.UserId;
import java.util.Optional;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * RefreshTokenCacheQueryAdapter - RefreshToken 캐시 Query 어댑터
 *
 * <p>RefreshTokenCacheQueryPort 구현체입니다. Redis에서 RefreshToken을 조회합니다.
 *
 * <p><strong>양방향 조회 지원:</strong>
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
 *   <li>비즈니스 로직 금지 (단순 조회만)
 *   <li>저장/수정/삭제 메서드 금지 (CommandAdapter로 분리)
 *   <li>KEYS 명령어 절대 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class RefreshTokenCacheQueryAdapter implements RefreshTokenCacheQueryPort {

    private final RedisTemplate<String, String> redisTemplate;

    public RefreshTokenCacheQueryAdapter(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * UserId로 RefreshToken 조회
     *
     * @param userId 사용자 ID (Value Object)
     * @return RefreshToken 문자열 (Optional)
     */
    @Override
    public Optional<String> findByUserId(UserId userId) {
        String userKey = RedisKeyGenerator.refreshTokenByUser(userId.value());
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
        String tokenKey = RedisKeyGenerator.refreshTokenByToken(refreshToken);
        String userIdString = redisTemplate.opsForValue().get(tokenKey);

        if (userIdString == null) {
            return Optional.empty();
        }

        return Optional.of(UserId.of(userIdString));
    }
}
