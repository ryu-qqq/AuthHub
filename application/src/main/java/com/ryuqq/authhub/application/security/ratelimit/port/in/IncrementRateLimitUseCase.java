package com.ryuqq.authhub.application.security.ratelimit.port.in;

import com.ryuqq.authhub.domain.security.ratelimit.vo.RateLimitType;

/**
 * Rate Limit 카운터 증가 UseCase (Port In).
 *
 * <p>Redis 기반 분산 카운터를 증가시키는 인바운드 포트입니다.
 * Hexagonal Architecture의 Port-Adapter 패턴에 따라 Application Layer의 진입점 역할을 합니다.</p>
 *
 * <p><strong>주요 책임:</strong></p>
 * <ul>
 *   <li>Redis 카운터 증가 (원자적 연산)</li>
 *   <li>TTL 설정 (시간 윈도우 기반)</li>
 *   <li>증가 결과 반환</li>
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>✅ Port/Adapter 패턴 - Application Layer 진입점</li>
 *   <li>✅ Command/Query 분리 - 쓰기 전용 (Command)</li>
 *   <li>✅ Lombok 금지 - Plain Java 사용</li>
 *   <li>✅ Javadoc 완비 - @author, @since 포함</li>
 *   <li>✅ Transaction 경계 - Redis 연산은 트랜잭션 밖에서 처리</li>
 * </ul>
 *
 * <p><strong>Redis 연산 특성:</strong></p>
 * <ul>
 *   <li>원자적(Atomic) 증가 연산 사용 (INCR 명령)</li>
 *   <li>TTL 자동 설정 (시간 윈도우 기반)</li>
 *   <li>트랜잭션 불필요 (Redis 자체가 원자성 보장)</li>
 * </ul>
 *
 * <p><strong>사용 예시:</strong></p>
 * <pre>
 * // IP 기반 카운터 증가
 * IncrementRateLimitUseCase.Command command = new IncrementRateLimitUseCase.Command(
 *     "192.168.1.100",
 *     "/api/v1/login",
 *     RateLimitType.IP_BASED,
 *     60L // TTL: 60초
 * );
 *
 * incrementRateLimitUseCase.incrementRateLimit(command);
 * </pre>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
public interface IncrementRateLimitUseCase {

    /**
     * Rate Limit 카운터를 증가시킵니다.
     *
     * <p>Redis INCR 명령을 사용하여 원자적으로 카운터를 증가시키고,
     * TTL을 설정하여 시간 윈도우가 지나면 자동으로 삭제되도록 합니다.</p>
     *
     * @param command 증가 요청 Command
     * @throws IllegalArgumentException command가 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    void incrementRateLimit(Command command);

    /**
     * Rate Limit 카운터 증가 요청 Command.
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
        private final long ttlSeconds;

        /**
         * Command 생성자.
         *
         * @param identifier 제한 대상 식별자 (IP 주소, 사용자 ID 등)
         * @param endpoint API 엔드포인트
         * @param type Rate Limit 타입
         * @param ttlSeconds TTL (Time To Live, 초 단위)
         * @throws IllegalArgumentException identifier, endpoint가 null이거나 빈 문자열이거나,
         *                                  type이 null이거나, ttlSeconds가 0 이하인 경우
         */
        public Command(
                final String identifier,
                final String endpoint,
                final RateLimitType type,
                final long ttlSeconds
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
            if (ttlSeconds <= 0) {
                throw new IllegalArgumentException("TTL seconds must be positive: " + ttlSeconds);
            }

            this.identifier = identifier;
            this.endpoint = endpoint;
            this.type = type;
            this.ttlSeconds = ttlSeconds;
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

        /**
         * TTL (Time To Live)을 초 단위로 반환합니다.
         *
         * @return TTL (초)
         */
        public long getTtlSeconds() {
            return this.ttlSeconds;
        }
    }
}
