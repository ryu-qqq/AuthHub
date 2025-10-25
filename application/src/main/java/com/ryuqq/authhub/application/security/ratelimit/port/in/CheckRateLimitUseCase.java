package com.ryuqq.authhub.application.security.ratelimit.port.in;

import com.ryuqq.authhub.domain.security.ratelimit.vo.RateLimitType;

/**
 * Rate Limit 확인 UseCase (Port In).
 *
 * <p>현재 요청이 Rate Limit을 초과했는지 확인하는 인바운드 포트입니다.
 * Hexagonal Architecture의 Port-Adapter 패턴에 따라 Application Layer의 진입점 역할을 합니다.</p>
 *
 * <p><strong>주요 책임:</strong></p>
 * <ul>
 *   <li>Redis에서 현재 카운트 조회</li>
 *   <li>RateLimitRule Aggregate 적용</li>
 *   <li>제한 초과 여부 반환</li>
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>✅ Port/Adapter 패턴 - Application Layer 진입점</li>
 *   <li>✅ Command/Query 분리 - 조회 전용 (Query)</li>
 *   <li>✅ Lombok 금지 - Plain Java 사용</li>
 *   <li>✅ Javadoc 완비 - @author, @since 포함</li>
 * </ul>
 *
 * <p><strong>사용 예시:</strong></p>
 * <pre>
 * // IP 기반 Rate Limit 확인
 * CheckRateLimitUseCase.Command command = new CheckRateLimitUseCase.Command(
 *     "192.168.1.100",
 *     "/api/v1/login",
 *     RateLimitType.IP_BASED
 * );
 *
 * CheckRateLimitUseCase.Result result = checkRateLimitUseCase.checkRateLimit(command);
 * if (result.isExceeded()) {
 *     // HTTP 429 Too Many Requests 반환
 * }
 * </pre>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
public interface CheckRateLimitUseCase {

    /**
     * Rate Limit 확인을 수행합니다.
     *
     * @param command 확인 요청 Command
     * @return 확인 결과 Result
     * @throws IllegalArgumentException command가 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    Result checkRateLimit(Command command);

    /**
     * Rate Limit 확인 요청 Command.
     *
     * <p>불변 객체로 설계되어 스레드 안전성을 보장합니다.</p>
     *
     * @author AuthHub Team
     * @since 1.0.0
     */
    final class Command {

        private final String identifier;
        private final String endpoint;
        private final RateLimitType type;

        /**
         * Command 생성자.
         *
         * @param identifier 제한 대상 식별자 (IP 주소, 사용자 ID 등)
         * @param endpoint API 엔드포인트
         * @param type Rate Limit 타입
         * @throws IllegalArgumentException identifier, endpoint 또는 type이 null이거나 빈 문자열인 경우
         */
        public Command(
                final String identifier,
                final String endpoint,
                final RateLimitType type
        ) {
            if (identifier == null || identifier.isBlank()) {
                throw new IllegalArgumentException("Identifier cannot be null or blank");
            }
            if (endpoint == null || endpoint.isBlank()) {
                throw new IllegalArgumentException("Endpoint cannot be null or blank");
            }
            if (type == null) {
                throw new IllegalArgumentException("RateLimitType cannot be null");
            }

            this.identifier = identifier;
            this.endpoint = endpoint;
            this.type = type;
        }

        /**
         * 제한 대상 식별자를 반환합니다.
         *
         * @return 식별자 (IP 주소, 사용자 ID 등)
         */
        public String getIdentifier() {
            return this.identifier;
        }

        /**
         * API 엔드포인트를 반환합니다.
         *
         * @return 엔드포인트
         */
        public String getEndpoint() {
            return this.endpoint;
        }

        /**
         * Rate Limit 타입을 반환합니다.
         *
         * @return RateLimitType
         */
        public RateLimitType getType() {
            return this.type;
        }
    }

    /**
     * Rate Limit 확인 결과 Result.
     *
     * <p>불변 객체로 설계되어 스레드 안전성을 보장합니다.</p>
     *
     * @author AuthHub Team
     * @since 1.0.0
     */
    final class Result {

        private final boolean exceeded;
        private final int currentCount;
        private final int limitCount;
        private final long remainingCount;
        private final long timeWindowSeconds;

        /**
         * Result 생성자.
         *
         * @param exceeded 제한 초과 여부
         * @param currentCount 현재 요청 횟수
         * @param limitCount 제한 횟수
         * @param remainingCount 남은 요청 횟수
         * @param timeWindowSeconds 시간 윈도우 (초)
         * @throws IllegalArgumentException currentCount, limitCount, timeWindowSeconds가 음수인 경우
         */
        public Result(
                final boolean exceeded,
                final int currentCount,
                final int limitCount,
                final long remainingCount,
                final long timeWindowSeconds
        ) {
            if (currentCount < 0) {
                throw new IllegalArgumentException("Current count cannot be negative");
            }
            if (limitCount < 0) {
                throw new IllegalArgumentException("Limit count cannot be negative");
            }
            if (timeWindowSeconds < 0) {
                throw new IllegalArgumentException("Time window seconds cannot be negative");
            }

            this.exceeded = exceeded;
            this.currentCount = currentCount;
            this.limitCount = limitCount;
            this.remainingCount = remainingCount;
            this.timeWindowSeconds = timeWindowSeconds;
        }

        /**
         * 제한 초과 여부를 반환합니다.
         *
         * @return 제한을 초과했으면 true, 아니면 false
         */
        public boolean isExceeded() {
            return this.exceeded;
        }

        /**
         * 현재 요청 횟수를 반환합니다.
         *
         * @return 현재 요청 횟수
         */
        public int getCurrentCount() {
            return this.currentCount;
        }

        /**
         * 제한 횟수를 반환합니다.
         *
         * @return 제한 횟수
         */
        public int getLimitCount() {
            return this.limitCount;
        }

        /**
         * 남은 요청 횟수를 반환합니다.
         *
         * @return 남은 요청 횟수
         */
        public long getRemainingCount() {
            return this.remainingCount;
        }

        /**
         * 시간 윈도우를 초 단위로 반환합니다.
         *
         * @return 시간 윈도우 (초)
         */
        public long getTimeWindowSeconds() {
            return this.timeWindowSeconds;
        }
    }
}
