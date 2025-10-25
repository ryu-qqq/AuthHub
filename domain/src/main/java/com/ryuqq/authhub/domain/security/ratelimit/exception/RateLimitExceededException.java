package com.ryuqq.authhub.domain.security.ratelimit.exception;

/**
 * Rate Limit 제한이 초과되었을 때 발생하는 예외.
 *
 * <p>시간 윈도우 내에서 허용된 요청 횟수를 초과한 경우 발생합니다.</p>
 *
 * <p><strong>사용 시나리오:</strong></p>
 * <ul>
 *   <li>IP 기반 Rate Limit 초과 - 동일 IP에서 과도한 요청</li>
 *   <li>사용자 기반 Rate Limit 초과 - 특정 사용자의 과도한 API 호출</li>
 *   <li>엔드포인트 기반 Rate Limit 초과 - 특정 API의 부하 제한 도달</li>
 * </ul>
 *
 * <p><strong>처리 방안:</strong></p>
 * <ul>
 *   <li>HTTP 429 Too Many Requests 응답 반환</li>
 *   <li>Retry-After 헤더 제공 (시간 윈도우 기반)</li>
 *   <li>클라이언트에게 제한 정보 제공 (X-RateLimit-* 헤더)</li>
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>✅ Framework 의존성 금지 - Pure Java Exception만 사용</li>
 *   <li>✅ 명확한 예외 메시지 제공</li>
 *   <li>✅ 도메인 비즈니스 규칙 표현</li>
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
public class RateLimitExceededException extends RateLimitDomainException {

    private final int currentCount;
    private final int limitCount;
    private final long timeWindowSeconds;

    /**
     * Rate Limit 초과 예외를 생성합니다.
     *
     * @param currentCount 현재 요청 횟수
     * @param limitCount 제한 횟수
     * @param timeWindowSeconds 시간 윈도우 (초)
     * @author AuthHub Team
     * @since 1.0.0
     */
    public RateLimitExceededException(
            final int currentCount,
            final int limitCount,
            final long timeWindowSeconds
    ) {
        super(String.format(
                "Rate limit exceeded: %d requests in %d seconds (limit: %d)",
                currentCount,
                timeWindowSeconds,
                limitCount
        ));
        this.currentCount = currentCount;
        this.limitCount = limitCount;
        this.timeWindowSeconds = timeWindowSeconds;
    }

    /**
     * 사용자 정의 메시지와 함께 Rate Limit 초과 예외를 생성합니다.
     *
     * @param message 예외 메시지
     * @param currentCount 현재 요청 횟수
     * @param limitCount 제한 횟수
     * @param timeWindowSeconds 시간 윈도우 (초)
     * @author AuthHub Team
     * @since 1.0.0
     */
    public RateLimitExceededException(
            final String message,
            final int currentCount,
            final int limitCount,
            final long timeWindowSeconds
    ) {
        super(message);
        this.currentCount = currentCount;
        this.limitCount = limitCount;
        this.timeWindowSeconds = timeWindowSeconds;
    }

    /**
     * 현재 요청 횟수를 반환합니다.
     *
     * @return 현재 요청 횟수
     * @author AuthHub Team
     * @since 1.0.0
     */
    public int getCurrentCount() {
        return this.currentCount;
    }

    /**
     * 제한 횟수를 반환합니다.
     *
     * @return 제한 횟수
     * @author AuthHub Team
     * @since 1.0.0
     */
    public int getLimitCount() {
        return this.limitCount;
    }

    /**
     * 시간 윈도우를 초 단위로 반환합니다.
     *
     * @return 시간 윈도우 (초)
     * @author AuthHub Team
     * @since 1.0.0
     */
    public long getTimeWindowSeconds() {
        return this.timeWindowSeconds;
    }

    /**
     * 제한을 초과한 요청 횟수를 반환합니다.
     *
     * @return 초과 횟수
     * @author AuthHub Team
     * @since 1.0.0
     */
    public int getExceededCount() {
        return this.currentCount - this.limitCount;
    }
}
