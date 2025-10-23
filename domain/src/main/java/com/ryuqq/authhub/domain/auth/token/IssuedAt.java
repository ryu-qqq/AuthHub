package com.ryuqq.authhub.domain.auth.token;

import java.time.Duration;
import java.time.Instant;

/**
 * 토큰의 발급 시각을 나타내는 Value Object.
 *
 * <p>JWT 토큰의 iat(issued at) 클레임에 해당하는 값을 래핑하며,
 * 토큰의 발급 시점 관련 비즈니스 로직을 캡슐화합니다.</p>
 *
 * <p><strong>비즈니스 규칙:</strong></p>
 * <ul>
 *   <li>발급 시각은 미래일 수 없음</li>
 *   <li>토큰의 age(현재로부터 발급 후 경과 시간) 계산 가능</li>
 *   <li>발급 시각 기반으로 만료 시각 계산 가능</li>
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>Lombok 미사용 - Java 21 Record 사용</li>
 *   <li>불변성 보장 - Record의 본질적 불변성</li>
 *   <li>Law of Demeter 준수 - 직접적인 행위 메서드 제공</li>
 * </ul>
 *
 * @param value 발급 시각 Instant (null 불가)
 * @author AuthHub Team
 * @since 1.0.0
 */
public record IssuedAt(Instant value) {

    /**
     * Compact constructor - 발급 시각의 null 검증 및 미래 시각 검증을 수행합니다.
     *
     * @throws IllegalArgumentException value가 null이거나 미래 시각인 경우
     */
    public IssuedAt {
        if (value == null) {
            throw new IllegalArgumentException("IssuedAt value cannot be null");
        }
        if (value.isAfter(Instant.now())) {
            throw new IllegalArgumentException("IssuedAt cannot be in the future");
        }
    }

    /**
     * 현재 시각으로 IssuedAt을 생성합니다.
     *
     * @return IssuedAt 인스턴스
     * @author AuthHub Team
     * @since 1.0.0
     */
    public static IssuedAt now() {
        return new IssuedAt(Instant.now());
    }

    /**
     * 특정 Instant 값으로부터 IssuedAt을 생성합니다.
     *
     * @param instant 발급 시각 Instant (null 불가, 미래 불가)
     * @return IssuedAt 인스턴스
     * @throws IllegalArgumentException instant가 null이거나 미래 시각인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    public static IssuedAt from(final Instant instant) {
        return new IssuedAt(instant);
    }

    /**
     * Unix timestamp (epoch seconds)로부터 IssuedAt을 생성합니다.
     * JWT의 iat 클레임 값으로부터 복원할 때 사용됩니다.
     *
     * @param epochSeconds Unix timestamp (초 단위)
     * @return IssuedAt 인스턴스
     * @throws IllegalArgumentException epochSeconds가 미래 시각인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    public static IssuedAt fromEpochSeconds(final long epochSeconds) {
        return new IssuedAt(Instant.ofEpochSecond(epochSeconds));
    }

    /**
     * 발급 시각으로부터 현재까지 경과한 시간을 계산합니다.
     * Law of Demeter 준수 - 외부에서 Duration.between() 직접 호출하지 않고 여기서 제공
     *
     * @return 경과 시간 Duration (항상 양수)
     * @author AuthHub Team
     * @since 1.0.0
     */
    public Duration age() {
        return Duration.between(this.value, Instant.now());
    }

    /**
     * 발급 시각으로부터 특정 시각까지 경과한 시간을 계산합니다.
     *
     * @param referenceTime 기준 시각 (null 불가)
     * @return 경과 시간 Duration
     * @throws IllegalArgumentException referenceTime이 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    public Duration ageAt(final Instant referenceTime) {
        if (referenceTime == null) {
            throw new IllegalArgumentException("Reference time cannot be null");
        }
        return Duration.between(this.value, referenceTime);
    }

    /**
     * 발급 시각이 특정 시각보다 이전인지 확인합니다.
     *
     * @param other 비교 대상 시각 (null 불가)
     * @return 발급 시각이 이전이면 true, 아니면 false
     * @throws IllegalArgumentException other가 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    public boolean isBefore(final Instant other) {
        if (other == null) {
            throw new IllegalArgumentException("Comparison time cannot be null");
        }
        return this.value.isBefore(other);
    }

    /**
     * 발급 시각이 특정 시각보다 이후인지 확인합니다.
     *
     * @param other 비교 대상 시각 (null 불가)
     * @return 발급 시각이 이후면 true, 아니면 false
     * @throws IllegalArgumentException other가 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    public boolean isAfter(final Instant other) {
        if (other == null) {
            throw new IllegalArgumentException("Comparison time cannot be null");
        }
        return this.value.isAfter(other);
    }

    /**
     * 발급 시각으로부터 지정된 유효 기간 후의 만료 시각을 계산합니다.
     *
     * @param validity 유효 기간 (null 불가, 양수)
     * @return ExpiresAt 인스턴스
     * @throws IllegalArgumentException validity가 null이거나 음수인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    public ExpiresAt calculateExpiresAt(final Duration validity) {
        return ExpiresAt.fromIssuedAt(this.value, validity);
    }

    /**
     * IssuedAt을 Unix timestamp (epoch seconds)로 반환합니다.
     * JWT의 iat 클레임 값으로 사용됩니다.
     *
     * @return Unix timestamp (초 단위)
     * @author AuthHub Team
     * @since 1.0.0
     */
    public long asEpochSeconds() {
        return this.value.getEpochSecond();
    }
}
