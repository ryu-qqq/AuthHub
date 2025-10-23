package com.ryuqq.authhub.domain.auth.token;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

/**
 * 토큰의 만료 시각을 나타내는 Value Object.
 *
 * <p>JWT 토큰의 exp(expiration time) 클레임에 해당하는 값을 래핑하며,
 * 토큰의 유효성을 판단하는 비즈니스 로직을 캡슐화합니다.</p>
 *
 * <p><strong>비즈니스 규칙:</strong></p>
 * <ul>
 *   <li>만료 시각은 과거일 수 없음 (생성 시점 검증)</li>
 *   <li>현재 시각이 만료 시각을 지나면 토큰은 만료된 것으로 간주</li>
 *   <li>만료 시각과 현재 시각의 비교는 도메인 로직</li>
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>Lombok 미사용 - Java 21 Record 사용</li>
 *   <li>불변성 보장 - Record의 본질적 불변성</li>
 *   <li>Law of Demeter 준수 - 직접적인 행위 메서드 제공</li>
 * </ul>
 *
 * @param value 만료 시각 Instant (null 불가)
 * @author AuthHub Team
 * @since 1.0.0
 */
public record ExpiresAt(Instant value) {

    /**
     * Compact constructor - 만료 시각의 null 검증을 수행합니다.
     *
     * @throws IllegalArgumentException value가 null인 경우
     */
    public ExpiresAt {
        if (value == null) {
            throw new IllegalArgumentException("ExpiresAt value cannot be null");
        }
    }

    /**
     * 특정 시각으로부터 지정된 기간 후의 만료 시각을 생성합니다.
     *
     * @param issuedAt 발급 시각 (null 불가)
     * @param validity 유효 기간 (null 불가, 양수)
     * @return ExpiresAt 인스턴스
     * @throws IllegalArgumentException issuedAt 또는 validity가 null이거나 validity가 음수인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    public static ExpiresAt fromIssuedAt(final Instant issuedAt, final Duration validity) {
        if (issuedAt == null) {
            throw new IllegalArgumentException("IssuedAt cannot be null");
        }
        if (validity == null || validity.isNegative()) {
            throw new IllegalArgumentException("Validity duration must be positive");
        }
        return new ExpiresAt(issuedAt.plus(validity));
    }

    /**
     * 현재 시각으로부터 지정된 기간 후의 만료 시각을 생성합니다.
     * 시스템 기본 Clock(UTC)을 사용합니다.
     *
     * @param validity 유효 기간 (null 불가, 양수)
     * @return ExpiresAt 인스턴스
     * @throws IllegalArgumentException validity가 null이거나 음수인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    public static ExpiresAt fromNow(final Duration validity) {
        return fromNow(validity, Clock.systemUTC());
    }

    /**
     * 지정된 Clock의 현재 시각으로부터 지정된 기간 후의 만료 시각을 생성합니다.
     * 테스트 시 Clock을 고정하여 시간 의존성을 제어할 수 있습니다.
     *
     * @param validity 유효 기간 (null 불가, 양수)
     * @param clock 시각 제공자 (null 불가)
     * @return ExpiresAt 인스턴스
     * @throws IllegalArgumentException validity 또는 clock이 null이거나 validity가 음수인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    public static ExpiresAt fromNow(final Duration validity, final Clock clock) {
        if (clock == null) {
            throw new IllegalArgumentException("Clock cannot be null");
        }
        return fromIssuedAt(Instant.now(clock), validity);
    }

    /**
     * 특정 Instant 값으로부터 ExpiresAt을 생성합니다.
     *
     * @param instant 만료 시각 Instant (null 불가)
     * @return ExpiresAt 인스턴스
     * @throws IllegalArgumentException instant가 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    public static ExpiresAt from(final Instant instant) {
        return new ExpiresAt(instant);
    }

    /**
     * 토큰이 현재 시각 기준으로 만료되었는지 확인합니다.
     * 시스템 기본 Clock(UTC)을 사용합니다.
     * Law of Demeter 준수 - 외부에서 value.isBefore(Instant.now()) 호출하지 않고 여기서 제공
     *
     * @return 만료되었으면 true, 아니면 false
     * @author AuthHub Team
     * @since 1.0.0
     */
    public boolean isExpired() {
        return isExpired(Clock.systemUTC());
    }

    /**
     * 토큰이 지정된 Clock의 현재 시각 기준으로 만료되었는지 확인합니다.
     * 테스트 시 Clock을 고정하여 시간 의존성을 제어할 수 있습니다.
     *
     * @param clock 현재 시각을 제공하는 Clock (null 불가)
     * @return 만료되었으면 true, 아니면 false
     * @throws IllegalArgumentException clock이 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    public boolean isExpired(final Clock clock) {
        if (clock == null) {
            throw new IllegalArgumentException("Clock cannot be null");
        }
        return this.value.isBefore(Instant.now(clock));
    }

    /**
     * 토큰이 특정 시각 기준으로 만료되었는지 확인합니다.
     *
     * @param referenceTime 기준 시각 (null 불가)
     * @return 만료되었으면 true, 아니면 false
     * @throws IllegalArgumentException referenceTime이 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    public boolean isExpiredAt(final Instant referenceTime) {
        if (referenceTime == null) {
            throw new IllegalArgumentException("Reference time cannot be null");
        }
        return this.value.isBefore(referenceTime);
    }

    /**
     * 토큰이 유효한지 확인합니다 (만료되지 않았는지).
     *
     * @return 유효하면 true, 만료되었으면 false
     * @author AuthHub Team
     * @since 1.0.0
     */
    public boolean isValid() {
        return !isExpired();
    }

    /**
     * 현재 시각으로부터 만료까지 남은 시간을 계산합니다.
     * 시스템 기본 Clock(UTC)을 사용합니다.
     *
     * @return 남은 시간 Duration (이미 만료된 경우 음수 Duration)
     * @author AuthHub Team
     * @since 1.0.0
     */
    public Duration remainingTime() {
        return remainingTime(Clock.systemUTC());
    }

    /**
     * 지정된 Clock의 현재 시각으로부터 만료까지 남은 시간을 계산합니다.
     * 테스트 시 Clock을 고정하여 시간 의존성을 제어할 수 있습니다.
     *
     * @param clock 현재 시각을 제공하는 Clock (null 불가)
     * @return 남은 시간 Duration (이미 만료된 경우 음수 Duration)
     * @throws IllegalArgumentException clock이 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    public Duration remainingTime(final Clock clock) {
        if (clock == null) {
            throw new IllegalArgumentException("Clock cannot be null");
        }
        return Duration.between(Instant.now(clock), this.value);
    }

    /**
     * ExpiresAt을 Unix timestamp (epoch seconds)로 반환합니다.
     * JWT의 exp 클레임 값으로 사용됩니다.
     *
     * @return Unix timestamp (초 단위)
     * @author AuthHub Team
     * @since 1.0.0
     */
    public long asEpochSeconds() {
        return this.value.getEpochSecond();
    }
}
