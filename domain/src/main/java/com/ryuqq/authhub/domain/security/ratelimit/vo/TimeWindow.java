package com.ryuqq.authhub.domain.security.ratelimit.vo;

import java.time.Duration;

/**
 * Rate Limit 규칙의 시간 윈도우를 나타내는 Value Object.
 *
 * <p>제한 횟수가 적용되는 시간 범위를 초 단위로 정의합니다.</p>
 *
 * <p><strong>도메인 규칙:</strong></p>
 * <ul>
 *   <li>시간 윈도우는 반드시 1초 이상이어야 합니다</li>
 *   <li>0 이하의 값은 허용되지 않습니다 (Rate Limiting 무의미)</li>
 *   <li>일반적인 시간 윈도우: 60초(1분), 3600초(1시간), 86400초(1일)</li>
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>✅ Lombok 미사용 - Java 21 Record 사용</li>
 *   <li>✅ 불변성 보장 - Record의 본질적 불변성</li>
 *   <li>✅ Law of Demeter 준수</li>
 * </ul>
 *
 * @param seconds 시간 윈도우 (초 단위, 1 이상)
 * @author AuthHub Team
 * @since 1.0.0
 */
public record TimeWindow(long seconds) {

    /**
     * 1분(60초)을 나타내는 상수.
     */
    public static final long ONE_MINUTE_SECONDS = 60L;

    /**
     * 1시간(3600초)을 나타내는 상수.
     */
    public static final long ONE_HOUR_SECONDS = 3600L;

    /**
     * 1일(86400초)을 나타내는 상수.
     */
    public static final long ONE_DAY_SECONDS = 86400L;

    /**
     * Compact constructor - 시간 윈도우의 유효성을 검증합니다.
     *
     * @throws IllegalArgumentException seconds가 1 미만인 경우
     */
    public TimeWindow {
        if (seconds < 1) {
            throw new IllegalArgumentException(
                    "Time window must be at least 1 second, but was: " + seconds
            );
        }
    }

    /**
     * 초 단위 값으로부터 TimeWindow를 생성합니다.
     *
     * @param seconds 시간 윈도우 (초 단위, 1 이상)
     * @return TimeWindow 인스턴스
     * @throws IllegalArgumentException seconds가 1 미만인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    public static TimeWindow ofSeconds(final long seconds) {
        return new TimeWindow(seconds);
    }

    /**
     * 분 단위 값으로부터 TimeWindow를 생성합니다.
     *
     * @param minutes 시간 윈도우 (분 단위, 1 이상)
     * @return TimeWindow 인스턴스
     * @throws IllegalArgumentException minutes가 1 미만인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    public static TimeWindow ofMinutes(final long minutes) {
        if (minutes < 1) {
            throw new IllegalArgumentException("Minutes must be at least 1, but was: " + minutes);
        }
        return new TimeWindow(minutes * ONE_MINUTE_SECONDS);
    }

    /**
     * 시간 단위 값으로부터 TimeWindow를 생성합니다.
     *
     * @param hours 시간 윈도우 (시간 단위, 1 이상)
     * @return TimeWindow 인스턴스
     * @throws IllegalArgumentException hours가 1 미만인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    public static TimeWindow ofHours(final long hours) {
        if (hours < 1) {
            throw new IllegalArgumentException("Hours must be at least 1, but was: " + hours);
        }
        return new TimeWindow(hours * ONE_HOUR_SECONDS);
    }

    /**
     * 일 단위 값으로부터 TimeWindow를 생성합니다.
     *
     * @param days 시간 윈도우 (일 단위, 1 이상)
     * @return TimeWindow 인스턴스
     * @throws IllegalArgumentException days가 1 미만인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    public static TimeWindow ofDays(final long days) {
        if (days < 1) {
            throw new IllegalArgumentException("Days must be at least 1, but was: " + days);
        }
        return new TimeWindow(days * ONE_DAY_SECONDS);
    }

    /**
     * Duration 객체로부터 TimeWindow를 생성합니다.
     *
     * @param duration Duration 객체 (1초 이상)
     * @return TimeWindow 인스턴스
     * @throws IllegalArgumentException duration이 null이거나 1초 미만인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    public static TimeWindow from(final Duration duration) {
        if (duration == null) {
            throw new IllegalArgumentException("Duration cannot be null");
        }
        return new TimeWindow(duration.getSeconds());
    }

    /**
     * TimeWindow를 Duration 객체로 변환합니다.
     *
     * @return Duration 객체
     * @author AuthHub Team
     * @since 1.0.0
     */
    public Duration toDuration() {
        return Duration.ofSeconds(this.seconds);
    }

    /**
     * 시간 윈도우를 분 단위로 변환합니다.
     *
     * @return 분 단위 값
     * @author AuthHub Team
     * @since 1.0.0
     */
    public long toMinutes() {
        return this.seconds / ONE_MINUTE_SECONDS;
    }

    /**
     * 시간 윈도우를 시간 단위로 변환합니다.
     *
     * @return 시간 단위 값
     * @author AuthHub Team
     * @since 1.0.0
     */
    public long toHours() {
        return this.seconds / ONE_HOUR_SECONDS;
    }

    /**
     * 시간 윈도우를 일 단위로 변환합니다.
     *
     * @return 일 단위 값
     * @author AuthHub Team
     * @since 1.0.0
     */
    public long toDays() {
        return this.seconds / ONE_DAY_SECONDS;
    }
}
