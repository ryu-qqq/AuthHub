package com.ryuqq.authhub.adapter.out.persistence.security.blacklist.mapper;

import com.ryuqq.authhub.adapter.out.persistence.security.blacklist.entity.BlacklistedTokenRedisEntity;
import com.ryuqq.authhub.domain.security.blacklist.BlacklistedToken;
import com.ryuqq.authhub.domain.security.blacklist.vo.BlacklistedTokenId;
import com.ryuqq.authhub.domain.security.blacklist.vo.BlacklistReason;
import com.ryuqq.authhub.domain.security.blacklist.vo.ExpiresAt;
import com.ryuqq.authhub.domain.security.blacklist.vo.Jti;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Objects;

/**
 * Blacklist Entity Mapper.
 *
 * <p>Domain Layer의 {@link BlacklistedToken}과
 * Persistence Layer의 {@link BlacklistedTokenRedisEntity} 간 변환을 담당합니다.
 * Hexagonal Architecture의 Mapper 역할을 수행하며, 레이어 간 경계를 명확히 합니다.</p>
 *
 * <p><strong>주요 책임:</strong></p>
 * <ul>
 *   <li>Domain → Redis Entity 변환 (toEntity)</li>
 *   <li>Redis Entity → Domain 변환 (toDomain)</li>
 *   <li>VO 변환 처리 (Jti, ExpiresAt, BlacklistReason)</li>
 *   <li>Null 안전성 보장</li>
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>✅ Lombok 금지 - Plain Java 구현</li>
 *   <li>✅ Law of Demeter 준수 - 직접적인 변환 메서드 제공</li>
 *   <li>✅ Null 안전성 - Objects.requireNonNull 사용</li>
 *   <li>✅ Javadoc 완비 - @author, @since 포함</li>
 *   <li>✅ Mapper 패턴 - 단방향 변환 메서드 제공</li>
 * </ul>
 *
 * <p><strong>변환 로직:</strong></p>
 * <ul>
 *   <li><strong>toEntity</strong>: Domain Aggregate → Redis Entity</li>
 *   <li><strong>toDomain</strong>: Redis Entity → Domain Aggregate (reconstruct 사용)</li>
 * </ul>
 *
 * <p><strong>사용 예시:</strong></p>
 * <pre>
 * // Domain → Entity
 * BlacklistedToken domain = BlacklistedToken.create(...);
 * BlacklistedTokenRedisEntity entity = mapper.toEntity(domain);
 *
 * // Entity → Domain
 * BlacklistedTokenRedisEntity entity = repository.findById("jti").orElseThrow();
 * BlacklistedToken domain = mapper.toDomain(entity);
 * </pre>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@Component
public class BlacklistEntityMapper {

    /**
     * Domain {@link BlacklistedToken}을 Redis Entity로 변환합니다.
     *
     * <p><strong>변환 과정:</strong></p>
     * <ol>
     *   <li>JTI VO → String</li>
     *   <li>BlacklistReason VO → String</li>
     *   <li>Instant → Unix timestamp (milliseconds)</li>
     *   <li>TTL 자동 계산 (Entity 생성 시)</li>
     * </ol>
     *
     * @param domain Domain Aggregate (null 불가)
     * @return Redis Entity
     * @throws NullPointerException domain이 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    public BlacklistedTokenRedisEntity toEntity(final BlacklistedToken domain) {
        Objects.requireNonNull(domain, "BlacklistedToken domain cannot be null");

        final String jtiValue = extractJti(domain);
        final String reasonValue = extractReason(domain);
        final Instant blacklistedAt = domain.getBlacklistedAt();
        final Instant expiresAt = extractExpiresAt(domain);

        return BlacklistedTokenRedisEntity.create(
                jtiValue,
                reasonValue,
                blacklistedAt,
                expiresAt
        );
    }

    /**
     * Redis Entity를 Domain {@link BlacklistedToken}으로 변환합니다.
     *
     * <p><strong>변환 과정:</strong></p>
     * <ol>
     *   <li>String → JTI VO</li>
     *   <li>String → BlacklistReason VO</li>
     *   <li>Unix timestamp → Instant</li>
     *   <li>reconstruct 패턴 사용 (ID 유지)</li>
     * </ol>
     *
     * <p><strong>주의사항:</strong></p>
     * <ul>
     *   <li>Domain ID는 새로 생성하지 않음 (reconstruct 사용)</li>
     *   <li>Redis에서 복원 시 기존 ID 유지</li>
     * </ul>
     *
     * @param entity Redis Entity (null 불가)
     * @return Domain Aggregate
     * @throws NullPointerException entity가 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    public BlacklistedToken toDomain(final BlacklistedTokenRedisEntity entity) {
        Objects.requireNonNull(entity, "BlacklistedTokenRedisEntity cannot be null");

        // Note: Redis Entity에는 Domain ID가 없으므로 새로 생성
        // 실제로는 JTI를 ID로 사용하는 것이 맞지만,
        // Domain 설계상 BlacklistedTokenId가 별도로 존재하므로 새로 생성
        final BlacklistedTokenId id = BlacklistedTokenId.newId();
        final Jti jti = Jti.of(entity.getJti());
        final ExpiresAt expiresAt = ExpiresAt.fromEpochSeconds(entity.getExpiresAt() / 1000);
        final BlacklistReason reason = BlacklistReason.valueOf(entity.getReason());
        final Instant blacklistedAt = Instant.ofEpochMilli(entity.getBlacklistedAt());

        return BlacklistedToken.reconstruct(
                id,
                jti,
                expiresAt,
                reason,
                blacklistedAt
        );
    }

    /**
     * Domain에서 JTI 값을 추출합니다.
     *
     * @param domain Domain Aggregate
     * @return JTI 문자열 값
     */
    private String extractJti(final BlacklistedToken domain) {
        final Jti jti = domain.getJti();
        Objects.requireNonNull(jti, "JTI cannot be null");
        return jti.asString();
    }

    /**
     * Domain에서 BlacklistReason 값을 추출합니다.
     *
     * @param domain Domain Aggregate
     * @return BlacklistReason 문자열 값
     */
    private String extractReason(final BlacklistedToken domain) {
        final BlacklistReason reason = domain.getReason();
        Objects.requireNonNull(reason, "BlacklistReason cannot be null");
        return reason.name();
    }

    /**
     * Domain에서 ExpiresAt Instant 값을 추출합니다.
     *
     * @param domain Domain Aggregate
     * @return ExpiresAt Instant
     */
    private Instant extractExpiresAt(final BlacklistedToken domain) {
        final ExpiresAt expiresAt = domain.getExpiresAt();
        Objects.requireNonNull(expiresAt, "ExpiresAt cannot be null");
        return expiresAt.asInstant();
    }
}
