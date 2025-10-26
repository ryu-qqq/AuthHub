package com.ryuqq.authhub.adapter.out.persistence.security.ratelimit.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.Objects;

/**
 * Rate Limit Redis Entity.
 *
 * <p>Redis에 저장되는 Rate Limit 카운트 데이터를 표현하는 Entity입니다.
 * {@code @RedisHash}를 사용하여 Redis에 Hash 구조로 저장되며, TTL(Time To Live)은 60초로 설정됩니다.</p>
 *
 * <p><strong>Key Naming Convention:</strong></p>
 * <ul>
 *   <li>Redis Key 패턴: {@code rate_limit:{type}:{identifier}:{endpoint}}</li>
 *   <li>IP 기반 예시: {@code rate_limit:ip:192.168.0.1:/api/auth/login}</li>
 *   <li>User 기반 예시: {@code rate_limit:user:uuid-1234:/api/users}</li>
 *   <li>Endpoint 기반 예시: {@code rate_limit:endpoint:global:/api/public}</li>
 * </ul>
 *
 * <p><strong>TTL 전략:</strong></p>
 * <ul>
 *   <li>60초 후 자동 만료 (시간 윈도우 기반 Rate Limiting)</li>
 *   <li>Redis의 EXPIRE 메커니즘을 활용한 자동 삭제</li>
 *   <li>메모리 효율적 관리 (만료된 카운트 자동 제거)</li>
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>✅ Lombok 금지 - Plain Java getter/setter 직접 구현</li>
 *   <li>✅ 불변성 보장 - final 필드 사용 (Redis Entity는 mutable for framework)</li>
 *   <li>✅ Javadoc 완비 - 모든 public 메서드에 @author, @since 포함</li>
 *   <li>✅ 원자성 보장 - INCR 명령어 사용 전제</li>
 * </ul>
 *
 * <p><strong>필드 설명:</strong></p>
 * <ul>
 *   <li>{@code key}: Rate Limit Key (type:identifier:endpoint 조합, Redis Key로 사용)</li>
 *   <li>{@code count}: 현재 요청 카운트 (INCR 명령어로 원자적 증가)</li>
 *   <li>{@code lastResetAt}: 마지막 리셋 시각 (Unix timestamp, milliseconds)</li>
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@RedisHash(value = "rate_limit", timeToLive = 60) // RateLimitRedisEntity.RATE_LIMIT_TTL_SECONDS
public class RateLimitRedisEntity {

    /**
     * Rate Limit의 TTL(Time To Live) 값.
     * 60초 = 1분 (시간 윈도우 기반 Rate Limiting)
     *
     * <p>주의: {@code @RedisHash} 어노테이션의 timeToLive 속성은 컴파일 타임 상수만 허용하므로,
     * 어노테이션에는 직접 숫자를 사용하고 테스트 코드에서는 이 상수를 참조합니다.</p>
     */
    public static final long RATE_LIMIT_TTL_SECONDS = 60L;

    @Id
    private String key;

    private Integer count;
    private Long lastResetAt;

    /**
     * 기본 생성자 (Redis 프레임워크용).
     * 외부에서 직접 사용하지 않으며, 팩토리 메서드를 통해 생성합니다.
     *
     * @author AuthHub Team
     * @since 1.0.0
     */
    protected RateLimitRedisEntity() {
    }

    /**
     * 모든 필드를 포함한 생성자 (private).
     * 외부에서는 팩토리 메서드를 통해서만 생성 가능합니다.
     *
     * @param key Rate Limit Key (type:identifier:endpoint) (null 불가)
     * @param count 현재 카운트 (null 불가, 0 이상)
     * @param lastResetAt 마지막 리셋 시각 (Unix timestamp, milliseconds) (null 불가)
     */
    private RateLimitRedisEntity(
            final String key,
            final Integer count,
            final Long lastResetAt
    ) {
        this.key = key;
        this.count = count;
        this.lastResetAt = lastResetAt;
    }

    /**
     * 새로운 RateLimitRedisEntity를 생성하는 팩토리 메서드.
     *
     * @param key Rate Limit Key (type:identifier:endpoint) (null 불가)
     * @param count 현재 카운트 (null 불가, 0 이상)
     * @param lastResetAt 마지막 리셋 시각 (Unix timestamp, milliseconds) (null 불가)
     * @return 새로 생성된 RateLimitRedisEntity 인스턴스
     * @throws IllegalArgumentException 인자가 null이거나 count가 음수인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    public static RateLimitRedisEntity create(
            final String key,
            final Integer count,
            final Long lastResetAt
    ) {
        validateNotNull(key, "key");
        validateNotNull(count, "count");
        validateNotNull(lastResetAt, "lastResetAt");

        if (count < 0) {
            throw new IllegalArgumentException("count cannot be negative: " + count);
        }

        return new RateLimitRedisEntity(key, count, lastResetAt);
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
     * Rate Limit Key를 반환합니다.
     *
     * @return Rate Limit Key (type:identifier:endpoint)
     * @author AuthHub Team
     * @since 1.0.0
     */
    public String getKey() {
        return this.key;
    }

    /**
     * 현재 카운트를 반환합니다.
     *
     * @return 현재 요청 카운트
     * @author AuthHub Team
     * @since 1.0.0
     */
    public Integer getCount() {
        return this.count;
    }

    /**
     * 마지막 리셋 시각을 반환합니다.
     *
     * @return 마지막 리셋 시각 (Unix timestamp, milliseconds)
     * @author AuthHub Team
     * @since 1.0.0
     */
    public Long getLastResetAt() {
        return this.lastResetAt;
    }

    /**
     * Rate Limit Key를 설정합니다.
     * Spring Data Redis 프레임워크에서 사용됩니다.
     *
     * @param key Rate Limit Key
     * @author AuthHub Team
     * @since 1.0.0
     */
    public void setKey(final String key) {
        this.key = key;
    }

    /**
     * 현재 카운트를 설정합니다.
     * Spring Data Redis 프레임워크에서 사용됩니다.
     *
     * @param count 현재 카운트
     * @author AuthHub Team
     * @since 1.0.0
     */
    public void setCount(final Integer count) {
        this.count = count;
    }

    /**
     * 마지막 리셋 시각을 설정합니다.
     * Spring Data Redis 프레임워크에서 사용됩니다.
     *
     * @param lastResetAt 마지막 리셋 시각 (Unix timestamp, milliseconds)
     * @author AuthHub Team
     * @since 1.0.0
     */
    public void setLastResetAt(final Long lastResetAt) {
        this.lastResetAt = lastResetAt;
    }

    /**
     * 두 RateLimitRedisEntity 객체의 동등성을 비교합니다.
     * key가 같으면 같은 Entity로 간주합니다.
     *
     * @param obj 비교 대상 객체
     * @return key가 같으면 true, 아니면 false
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
        RateLimitRedisEntity other = (RateLimitRedisEntity) obj;
        return Objects.equals(this.key, other.key);
    }

    /**
     * 해시 코드를 반환합니다.
     * key를 기준으로 계산됩니다.
     *
     * @return 해시 코드
     * @author AuthHub Team
     * @since 1.0.0
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.key);
    }

    /**
     * RateLimitRedisEntity의 문자열 표현을 반환합니다.
     *
     * @return "RateLimitRedisEntity{key=..., count=..., lastResetAt=...}" 형식의 문자열
     * @author AuthHub Team
     * @since 1.0.0
     */
    @Override
    public String toString() {
        return "RateLimitRedisEntity{" +
                "key='" + this.key + '\'' +
                ", count=" + this.count +
                ", lastResetAt=" + this.lastResetAt +
                '}';
    }
}
