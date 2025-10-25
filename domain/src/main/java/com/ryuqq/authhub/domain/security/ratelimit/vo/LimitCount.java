package com.ryuqq.authhub.domain.security.ratelimit.vo;

/**
 * Rate Limit 규칙의 제한 횟수를 나타내는 Value Object.
 *
 * <p>시간 윈도우 내에서 허용되는 최대 요청 횟수를 정의합니다.</p>
 *
 * <p><strong>도메인 규칙:</strong></p>
 * <ul>
 *   <li>제한 횟수는 반드시 1 이상이어야 합니다</li>
 *   <li>0 이하의 값은 허용되지 않습니다 (Rate Limiting 무의미)</li>
 *   <li>매우 큰 값(Integer.MAX_VALUE)도 허용되지만, 실질적인 제한 효과가 없을 수 있습니다</li>
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>✅ Lombok 미사용 - Java 21 Record 사용</li>
 *   <li>✅ 불변성 보장 - Record의 본질적 불변성</li>
 *   <li>✅ Law of Demeter 준수</li>
 * </ul>
 *
 * @param value 제한 횟수 (1 이상)
 * @author AuthHub Team
 * @since 1.0.0
 */
public record LimitCount(int value) {

    /**
     * Compact constructor - 제한 횟수의 유효성을 검증합니다.
     *
     * @throws IllegalArgumentException value가 1 미만인 경우
     */
    public LimitCount {
        if (value < 1) {
            throw new IllegalArgumentException(
                    "Limit count must be at least 1, but was: " + value
            );
        }
    }

    /**
     * 정수 값으로부터 LimitCount를 생성합니다.
     *
     * @param count 제한 횟수 (1 이상)
     * @return LimitCount 인스턴스
     * @throws IllegalArgumentException count가 1 미만인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    public static LimitCount of(final int count) {
        return new LimitCount(count);
    }

    /**
     * 현재 요청 횟수가 제한을 초과했는지 확인합니다.
     *
     * @param currentCount 현재 요청 횟수
     * @return 제한을 초과했으면 true, 아니면 false
     * @author AuthHub Team
     * @since 1.0.0
     */
    public boolean isExceeded(final int currentCount) {
        return currentCount >= this.value;
    }

    /**
     * 현재 요청 횟수가 제한 이내인지 확인합니다.
     *
     * @param currentCount 현재 요청 횟수
     * @return 제한 이내이면 true, 아니면 false
     * @author AuthHub Team
     * @since 1.0.0
     */
    public boolean isWithinLimit(final int currentCount) {
        return currentCount < this.value;
    }

    /**
     * 제한까지 남은 요청 횟수를 계산합니다.
     *
     * @param currentCount 현재 요청 횟수
     * @return 남은 요청 횟수 (0 이상)
     * @author AuthHub Team
     * @since 1.0.0
     */
    public int remainingCount(final int currentCount) {
        final int remaining = this.value - currentCount;
        return Math.max(0, remaining);
    }
}
