package com.ryuqq.authhub.adapter.out.persistence.security.blacklist.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.time.Instant;
import java.util.Objects;

/**
 * Blacklisted Token Redis Entity.
 *
 * <p>Redis에 저장되는 블랙리스트 토큰 데이터를 표현하는 Entity입니다.
 * {@code @RedisHash}를 사용하여 Redis Hash 구조로 저장되며,
 * {@code @TimeToLive}를 통해 토큰 만료 시간까지 자동 TTL이 설정됩니다.</p>
 *
 * <p><strong>Key Naming Convention:</strong></p>
 * <ul>
 *   <li>Redis Key 패턴: {@code blacklist_token:{jti}}</li>
 *   <li>예시: {@code blacklist_token:{jti}}</li>
 * </ul>
 *
 * <p><strong>Redis 구조 전략:</strong></p>
 * <ul>
 *   <li><strong>Hash 저장</strong>: Spring Data Redis {@code @RedisHash} 활용</li>
 *   <li><strong>SET 저장</strong>: {@code blacklist:tokens} SET에 JTI 추가 (Adapter에서 처리)</li>
 *   <li><strong>ZSET 저장</strong>: {@code blacklist:expiry} ZSET에 만료시간 저장 (Adapter에서 처리)</li>
 * </ul>
 *
 * <p><strong>TTL 전략:</strong></p>
 * <ul>
 *   <li>토큰 만료 시간(expiresAt)까지 자동 TTL 설정</li>
 *   <li>Redis의 {@code @TimeToLive} 메커니즘을 활용한 자동 삭제</li>
 *   <li>메모리 효율적 관리 (만료된 토큰 자동 제거)</li>
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>✅ Lombok 금지 - Plain Java getter/setter 직접 구현</li>
 *   <li>✅ 불변성 고려 - 생성 후 변경 최소화 (Redis Entity는 mutable for framework)</li>
 *   <li>✅ Javadoc 완비 - 모든 public 메서드에 @author, @since 포함</li>
 *   <li>✅ TTL 자동 관리 - {@code @TimeToLive} 활용</li>
 * </ul>
 *
 * <p><strong>필드 설명:</strong></p>
 * <ul>
 *   <li>{@code jti}: JWT ID (고유 식별자, Redis Key로 사용)</li>
 *   <li>{@code ttl}: Time To Live (토큰 만료 시간까지 남은 초)</li>
 *   <li>{@code reason}: 블랙리스트 등록 사유</li>
 *   <li>{@code blacklistedAt}: 블랙리스트 등록 시각 (Unix timestamp, milliseconds)</li>
 *   <li>{@code expiresAt}: 토큰 만료 시각 (Unix timestamp, milliseconds)</li>
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@RedisHash(value = "blacklist_token")
public class BlacklistedTokenRedisEntity {

    @Id
    private String jti;

    @TimeToLive
    private Long ttl;

    private String reason;
    private Long blacklistedAt;
    private Long expiresAt;

    /**
     * 기본 생성자 (Redis 프레임워크용).
     * 외부에서 직접 사용하지 않으며, 팩토리 메서드를 통해 생성합니다.
     *
     * @author AuthHub Team
     * @since 1.0.0
     */
    protected BlacklistedTokenRedisEntity() {
    }

    /**
     * 모든 필드를 포함한 생성자 (private).
     * 외부에서는 팩토리 메서드를 통해서만 생성 가능합니다.
     *
     * @param jti JWT ID (null 불가)
     * @param ttl Time To Live (초) (null 불가, 0 이상)
     * @param reason 블랙리스트 등록 사유 (null 불가)
     * @param blacklistedAt 블랙리스트 등록 시각 (Unix timestamp, milliseconds) (null 불가)
     * @param expiresAt 토큰 만료 시각 (Unix timestamp, milliseconds) (null 불가)
     */
    private BlacklistedTokenRedisEntity(
            final String jti,
            final Long ttl,
            final String reason,
            final Long blacklistedAt,
            final Long expiresAt
    ) {
        this.jti = jti;
        this.ttl = ttl;
        this.reason = reason;
        this.blacklistedAt = blacklistedAt;
        this.expiresAt = expiresAt;
    }

    /**
     * 새로운 BlacklistedTokenRedisEntity를 생성하는 팩토리 메서드.
     *
     * <p><strong>TTL 계산:</strong></p>
     * <ul>
     *   <li>TTL = (expiresAt - 현재시간) / 1000 (밀리초 → 초)</li>
     *   <li>이미 만료된 토큰(TTL &lt;= 0)도 등록 가능 (최소 TTL 1초 보장)</li>
     * </ul>
     *
     * @param jti JWT ID (null 불가)
     * @param reason 블랙리스트 등록 사유 (null 불가)
     * @param blacklistedAt 블랙리스트 등록 시각 (Instant) (null 불가)
     * @param expiresAt 토큰 만료 시각 (Instant) (null 불가)
     * @return 새로 생성된 BlacklistedTokenRedisEntity 인스턴스
     * @throws IllegalArgumentException 인자가 null이거나 유효하지 않은 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    public static BlacklistedTokenRedisEntity create(
            final String jti,
            final String reason,
            final Instant blacklistedAt,
            final Instant expiresAt
    ) {
        validateNotNull(jti, "jti");
        validateNotNull(reason, "reason");
        validateNotNull(blacklistedAt, "blacklistedAt");
        validateNotNull(expiresAt, "expiresAt");

        if (jti.trim().isEmpty()) {
            throw new IllegalArgumentException("jti cannot be empty");
        }
        if (reason.trim().isEmpty()) {
            throw new IllegalArgumentException("reason cannot be empty");
        }

        final long ttl = calculateTtl(expiresAt);
        final long blacklistedAtEpochMilli = blacklistedAt.toEpochMilli();
        final long expiresAtEpochMilli = expiresAt.toEpochMilli();

        return new BlacklistedTokenRedisEntity(
                jti,
                ttl,
                reason,
                blacklistedAtEpochMilli,
                expiresAtEpochMilli
        );
    }

    /**
     * TTL을 계산합니다 (토큰 만료 시간까지 남은 초).
     *
     * <p><strong>계산 로직:</strong></p>
     * <ul>
     *   <li>TTL = (expiresAt - 현재시간) / 1000</li>
     *   <li>이미 만료된 경우(TTL &lt;= 0): 최소 TTL 1초 반환</li>
     *   <li>Redis는 TTL이 0 이하면 즉시 삭제되므로, 최소 1초 보장</li>
     * </ul>
     *
     * @param expiresAt 토큰 만료 시각
     * @return TTL (초 단위, 최소 1초)
     */
    private static long calculateTtl(final Instant expiresAt) {
        final long nowEpochMilli = Instant.now().toEpochMilli();
        final long expiresAtEpochMilli = expiresAt.toEpochMilli();
        final long ttlMillis = expiresAtEpochMilli - nowEpochMilli;
        final long ttlSeconds = ttlMillis / 1000;

        // 이미 만료된 토큰도 최소 1초는 보장 (즉시 삭제 방지)
        return Math.max(ttlSeconds, 1L);
    }

    /**
     * null 검증 헬퍼 메서드.
     *
     * @param value 검증할 값
     * @param fieldName 필드명 (에러 메시지용)
     * @throws IllegalArgumentException value가 null인 경우
     */
    private static void validateNotNull(final Object value, final String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + " cannot be null");
        }
    }

    /**
     * JWT ID를 반환합니다.
     *
     * @return JWT ID
     * @author AuthHub Team
     * @since 1.0.0
     */
    public String getJti() {
        return this.jti;
    }

    /**
     * Time To Live를 반환합니다.
     *
     * @return TTL (초 단위)
     * @author AuthHub Team
     * @since 1.0.0
     */
    public Long getTtl() {
        return this.ttl;
    }

    /**
     * 블랙리스트 등록 사유를 반환합니다.
     *
     * @return 블랙리스트 등록 사유
     * @author AuthHub Team
     * @since 1.0.0
     */
    public String getReason() {
        return this.reason;
    }

    /**
     * 블랙리스트 등록 시각을 반환합니다.
     *
     * @return 블랙리스트 등록 시각 (Unix timestamp, milliseconds)
     * @author AuthHub Team
     * @since 1.0.0
     */
    public Long getBlacklistedAt() {
        return this.blacklistedAt;
    }

    /**
     * 토큰 만료 시각을 반환합니다.
     *
     * @return 토큰 만료 시각 (Unix timestamp, milliseconds)
     * @author AuthHub Team
     * @since 1.0.0
     */
    public Long getExpiresAt() {
        return this.expiresAt;
    }

    /**
     * JWT ID를 설정합니다.
     * Spring Data Redis 프레임워크에서 사용됩니다.
     *
     * @param jti JWT ID
     * @author AuthHub Team
     * @since 1.0.0
     */
    public void setJti(final String jti) {
        this.jti = jti;
    }

    /**
     * Time To Live를 설정합니다.
     * Spring Data Redis 프레임워크에서 사용됩니다.
     *
     * @param ttl TTL (초 단위)
     * @author AuthHub Team
     * @since 1.0.0
     */
    public void setTtl(final Long ttl) {
        this.ttl = ttl;
    }

    /**
     * 블랙리스트 등록 사유를 설정합니다.
     * Spring Data Redis 프레임워크에서 사용됩니다.
     *
     * @param reason 블랙리스트 등록 사유
     * @author AuthHub Team
     * @since 1.0.0
     */
    public void setReason(final String reason) {
        this.reason = reason;
    }

    /**
     * 블랙리스트 등록 시각을 설정합니다.
     * Spring Data Redis 프레임워크에서 사용됩니다.
     *
     * @param blacklistedAt 블랙리스트 등록 시각 (Unix timestamp, milliseconds)
     * @author AuthHub Team
     * @since 1.0.0
     */
    public void setBlacklistedAt(final Long blacklistedAt) {
        this.blacklistedAt = blacklistedAt;
    }

    /**
     * 토큰 만료 시각을 설정합니다.
     * Spring Data Redis 프레임워크에서 사용됩니다.
     *
     * @param expiresAt 토큰 만료 시각 (Unix timestamp, milliseconds)
     * @author AuthHub Team
     * @since 1.0.0
     */
    public void setExpiresAt(final Long expiresAt) {
        this.expiresAt = expiresAt;
    }

    /**
     * 두 BlacklistedTokenRedisEntity 객체의 동등성을 비교합니다.
     * jti가 같으면 같은 Entity로 간주합니다.
     *
     * @param obj 비교 대상 객체
     * @return jti가 같으면 true, 아니면 false
     * @author AuthHub Team
     * @since 1.0.0
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        BlacklistedTokenRedisEntity other = (BlacklistedTokenRedisEntity) obj;
        return Objects.equals(this.jti, other.jti);
    }

    /**
     * 해시 코드를 반환합니다.
     * jti를 기준으로 계산됩니다.
     *
     * @return 해시 코드
     * @author AuthHub Team
     * @since 1.0.0
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.jti);
    }

    /**
     * BlacklistedTokenRedisEntity의 문자열 표현을 반환합니다.
     *
     * @return "BlacklistedTokenRedisEntity{jti=..., ttl=..., reason=..., blacklistedAt=..., expiresAt=...}" 형식의 문자열
     * @author AuthHub Team
     * @since 1.0.0
     */
    @Override
    public String toString() {
        return "BlacklistedTokenRedisEntity{" +
                "jti='" + this.jti + '\'' +
                ", ttl=" + this.ttl +
                ", reason='" + this.reason + '\'' +
                ", blacklistedAt=" + this.blacklistedAt +
                ", expiresAt=" + this.expiresAt +
                '}';
    }
}
