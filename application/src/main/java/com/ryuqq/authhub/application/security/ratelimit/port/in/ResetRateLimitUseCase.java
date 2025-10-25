package com.ryuqq.authhub.application.security.ratelimit.port.in;

import com.ryuqq.authhub.domain.security.ratelimit.vo.RateLimitType;

/**
 * Rate Limit 카운터 리셋 UseCase (Port In).
 *
 * <p>Redis 기반 분산 카운터를 리셋(삭제)하는 인바운드 포트입니다.
 * Hexagonal Architecture의 Port-Adapter 패턴에 따라 Application Layer의 진입점 역할을 합니다.</p>
 *
 * <p><strong>주요 책임:</strong></p>
 * <ul>
 *   <li>Redis 카운터 삭제</li>
 *   <li>관리자 수동 리셋 지원</li>
 *   <li>테스트 환경 초기화</li>
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
 * <p><strong>사용 시나리오:</strong></p>
 * <ul>
 *   <li>관리자가 특정 사용자/IP의 Rate Limit을 수동으로 해제</li>
 *   <li>오탐지(False Positive)로 인한 제한 해제</li>
 *   <li>테스트 환경에서 카운터 초기화</li>
 * </ul>
 *
 * <p><strong>사용 예시:</strong></p>
 * <pre>
 * // IP 기반 카운터 리셋
 * ResetRateLimitUseCase.Command command = new ResetRateLimitUseCase.Command(
 *     "192.168.1.100",
 *     "/api/v1/login",
 *     RateLimitType.IP_BASED
 * );
 *
 * resetRateLimitUseCase.resetRateLimit(command);
 * </pre>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
public interface ResetRateLimitUseCase {

    /**
     * Rate Limit 카운터를 리셋(삭제)합니다.
     *
     * <p>Redis DEL 명령을 사용하여 카운터를 완전히 삭제합니다.</p>
     *
     * @param command 리셋 요청 Command
     * @throws IllegalArgumentException command가 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    void resetRateLimit(Command command);

    /**
     * Rate Limit 카운터 리셋 요청 Command.
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
         * @throws IllegalArgumentException identifier, endpoint가 null이거나 빈 문자열이거나, type이 null인 경우
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
}
