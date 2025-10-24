package com.ryuqq.authhub.adapter.out.persistence.auth.token.mapper;

import com.ryuqq.authhub.adapter.out.persistence.auth.token.entity.RefreshTokenRedisEntity;
import com.ryuqq.authhub.domain.auth.token.ExpiresAt;
import com.ryuqq.authhub.domain.auth.token.IssuedAt;
import com.ryuqq.authhub.domain.auth.token.JwtToken;
import com.ryuqq.authhub.domain.auth.token.Token;
import com.ryuqq.authhub.domain.auth.token.TokenId;
import com.ryuqq.authhub.domain.auth.token.TokenType;
import com.ryuqq.authhub.domain.auth.user.UserId;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Objects;

/**
 * Domain의 Token과 Redis의 RefreshTokenRedisEntity 간 변환을 담당하는 Mapper.
 *
 * <p>Persistence Adapter에서 Domain Layer와 Persistence Layer 간의 데이터 변환을 수행합니다.
 * Anti-Corruption Layer 역할을 하며, Domain 모델의 순수성을 보호합니다.</p>
 *
 * <p><strong>주요 책임:</strong></p>
 * <ul>
 *   <li>Domain Token → Redis RefreshTokenRedisEntity 변환 (영속화 시)</li>
 *   <li>Redis RefreshTokenRedisEntity → Domain Token 변환 (조회 시)</li>
 *   <li>Domain Value Object ↔ Redis 기본 타입 매핑</li>
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>✅ Lombok 금지 - Plain Java 구현</li>
 *   <li>✅ Law of Demeter 준수 - 직접 getter 호출, chaining 금지</li>
 *   <li>✅ Null 안전성 - null 체크 철저히 수행</li>
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@Component
public class TokenEntityMapper {

    /**
     * Domain의 Token을 Redis의 RefreshTokenRedisEntity로 변환합니다.
     *
     * <p>Token Aggregate를 Redis에 영속화하기 위해 Redis Entity로 변환합니다.
     * Token은 REFRESH 타입이어야 하며, ACCESS 타입은 Redis에 저장하지 않습니다.</p>
     *
     * <p><strong>매핑 규칙:</strong></p>
     * <ul>
     *   <li>TokenId.value() → RefreshTokenRedisEntity.tokenId (UUID String)</li>
     *   <li>UserId.value() → RefreshTokenRedisEntity.userId (UUID String)</li>
     *   <li>JwtToken.value() → RefreshTokenRedisEntity.jwtToken (JWT String)</li>
     *   <li>IssuedAt.value() → RefreshTokenRedisEntity.issuedAt (Unix timestamp millis)</li>
     *   <li>ExpiresAt.value() → RefreshTokenRedisEntity.expiresAt (Unix timestamp millis)</li>
     * </ul>
     *
     * @param token Domain Token (null 불가, REFRESH 타입이어야 함)
     * @return RefreshTokenRedisEntity
     * @throws NullPointerException token이 null인 경우
     * @throws IllegalArgumentException token이 REFRESH 타입이 아닌 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    public RefreshTokenRedisEntity toEntity(final Token token) {
        Objects.requireNonNull(token, "Token cannot be null");

        if (!token.isRefreshToken()) {
            throw new IllegalArgumentException(
                    "Only REFRESH type tokens can be stored in Redis (actual: " + token.getType() + ")"
            );
        }

        return RefreshTokenRedisEntity.create(
                token.getId().asString(),
                token.getUserId().asString(),
                token.getJwtValue(),
                toEpochMillis(token.getIssuedAt()),
                toEpochMillis(token.getExpiresAt())
        );
    }

    /**
     * Redis의 RefreshTokenRedisEntity를 Domain의 Token으로 변환합니다.
     *
     * <p>Redis에서 조회한 Entity를 Domain Aggregate로 재구성합니다.
     * Token.reconstruct() 팩토리 메서드를 사용하여 불변 객체를 생성합니다.</p>
     *
     * <p><strong>매핑 규칙:</strong></p>
     * <ul>
     *   <li>RefreshTokenRedisEntity.tokenId → TokenId (UUID 파싱)</li>
     *   <li>RefreshTokenRedisEntity.userId → UserId (UUID 파싱)</li>
     *   <li>RefreshTokenRedisEntity.jwtToken → JwtToken</li>
     *   <li>RefreshTokenRedisEntity.issuedAt → IssuedAt (Unix timestamp millis → Instant)</li>
     *   <li>RefreshTokenRedisEntity.expiresAt → ExpiresAt (Unix timestamp millis → Instant)</li>
     * </ul>
     *
     * @param entity RefreshTokenRedisEntity (null 불가)
     * @return Domain Token (TokenType은 REFRESH)
     * @throws NullPointerException entity가 null인 경우
     * @throws IllegalArgumentException entity의 필드가 유효하지 않은 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    public Token toDomain(final RefreshTokenRedisEntity entity) {
        Objects.requireNonNull(entity, "RefreshTokenRedisEntity cannot be null");

        return Token.reconstruct(
                TokenId.fromString(entity.getTokenId()),
                UserId.fromString(entity.getUserId()),
                TokenType.REFRESH,
                JwtToken.from(entity.getJwtToken()),
                fromEpochMillisToIssuedAt(entity.getIssuedAt()),
                fromEpochMillisToExpiresAt(entity.getExpiresAt())
        );
    }

    /**
     * Domain의 IssuedAt을 Unix timestamp (milliseconds)로 변환합니다.
     *
     * @param issuedAt Domain IssuedAt (null 불가)
     * @return Unix timestamp (milliseconds)
     * @throws NullPointerException issuedAt이 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    private Long toEpochMillis(final IssuedAt issuedAt) {
        Objects.requireNonNull(issuedAt, "IssuedAt cannot be null");
        return issuedAt.value().toEpochMilli();
    }

    /**
     * Domain의 ExpiresAt을 Unix timestamp (milliseconds)로 변환합니다.
     *
     * @param expiresAt Domain ExpiresAt (null 불가)
     * @return Unix timestamp (milliseconds)
     * @throws NullPointerException expiresAt이 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    private Long toEpochMillis(final ExpiresAt expiresAt) {
        Objects.requireNonNull(expiresAt, "ExpiresAt cannot be null");
        return expiresAt.value().toEpochMilli();
    }

    /**
     * Unix timestamp (milliseconds)를 Domain의 IssuedAt으로 변환합니다.
     *
     * @param epochMillis Unix timestamp (milliseconds) (null 불가)
     * @return Domain IssuedAt
     * @throws NullPointerException epochMillis가 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    private IssuedAt fromEpochMillisToIssuedAt(final Long epochMillis) {
        Objects.requireNonNull(epochMillis, "IssuedAt epoch millis cannot be null");
        return IssuedAt.from(Instant.ofEpochMilli(epochMillis));
    }

    /**
     * Unix timestamp (milliseconds)를 Domain의 ExpiresAt으로 변환합니다.
     *
     * @param epochMillis Unix timestamp (milliseconds) (null 불가)
     * @return Domain ExpiresAt
     * @throws NullPointerException epochMillis가 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    private ExpiresAt fromEpochMillisToExpiresAt(final Long epochMillis) {
        Objects.requireNonNull(epochMillis, "ExpiresAt epoch millis cannot be null");
        return ExpiresAt.from(Instant.ofEpochMilli(epochMillis));
    }
}
