package com.ryuqq.authhub.application.security.ratelimit.port.out;

import com.ryuqq.authhub.domain.security.ratelimit.vo.RateLimitType;

/**
 * Rate Limit 조회 Port (Port Out).
 *
 * <p>Redis에서 현재 Rate Limit 카운트를 조회하는 아웃바운드 포트입니다.
 * Hexagonal Architecture의 Port-Adapter 패턴에 따라 Infrastructure Layer와의 경계 역할을 합니다.</p>
 *
 * <p><strong>주요 책임:</strong></p>
 * <ul>
 *   <li>Redis에서 현재 카운트 조회</li>
 *   <li>카운트가 없으면 0 반환</li>
 *   <li>Redis 연결 실패 시 예외 전파</li>
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>✅ Port/Adapter 패턴 - Infrastructure Layer 경계</li>
 *   <li>✅ Command/Query 분리 - 조회 전용 (Query)</li>
 *   <li>✅ Lombok 금지 - Plain Java 사용</li>
 *   <li>✅ Javadoc 완비 - @author, @since 포함</li>
 *   <li>✅ Transaction 경계 - Redis 연산은 트랜잭션 밖에서 처리</li>
 * </ul>
 *
 * <p><strong>구현 계층 (Adapter-Out-Persistence):</strong></p>
 * <ul>
 *   <li>RateLimitPersistenceAdapter (Port 구현체)</li>
 *   <li>RateLimitRedisRepository (Spring Data Redis)</li>
 *   <li>RedisTemplate 사용</li>
 * </ul>
 *
 * <p><strong>Redis Key 구조:</strong></p>
 * <pre>
 * rate_limit:ip:{ip}:{endpoint}        (IP 기반)
 * rate_limit:user:{user_id}:{endpoint} (사용자 기반)
 * rate_limit:endpoint:{endpoint}       (엔드포인트 기반)
 * </pre>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
public interface LoadRateLimitPort {

    /**
     * Redis에서 현재 Rate Limit 카운트를 조회합니다.
     *
     * <p>Redis GET 명령을 사용하여 카운트를 조회하며,
     * 키가 존재하지 않으면 0을 반환합니다.</p>
     *
     * @param identifier 제한 대상 식별자 (IP 주소, 사용자 ID 등)
     * @param endpoint API 엔드포인트
     * @param type Rate Limit 타입
     * @return 현재 카운트 (키가 없으면 0)
     * @throws IllegalArgumentException identifier, endpoint가 null이거나 빈 문자열이거나, type이 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    int loadCurrentCount(String identifier, String endpoint, RateLimitType type);

    /**
     * Redis에서 현재 카운트와 TTL을 함께 조회합니다.
     *
     * <p>성능 최적화를 위해 카운트와 TTL을 한 번에 조회합니다.</p>
     *
     * @param identifier 제한 대상 식별자
     * @param endpoint API 엔드포인트
     * @param type Rate Limit 타입
     * @return CountWithTtl (카운트와 TTL)
     * @author AuthHub Team
     * @since 1.0.0
     */
    CountWithTtl loadCountWithTtl(String identifier, String endpoint, RateLimitType type);

    /**
     * 현재 카운트와 TTL을 담는 불변 객체.
     *
     * @author AuthHub Team
     * @since 1.0.0
     */
    final class CountWithTtl {

        private final int count;
        private final long ttlSeconds;

        /**
         * CountWithTtl 생성자.
         *
         * @param count 현재 카운트
         * @param ttlSeconds TTL (초 단위, -1이면 TTL 없음)
         */
        public CountWithTtl(final int count, final long ttlSeconds) {
            if (count < 0) {
                throw new IllegalArgumentException("Count cannot be negative");
            }
            this.count = count;
            this.ttlSeconds = ttlSeconds;
        }

        /**
         * 현재 카운트를 반환합니다.
         *
         * @return 현재 카운트
         */
        public int getCount() {
            return this.count;
        }

        /**
         * TTL을 초 단위로 반환합니다.
         *
         * @return TTL (초 단위, -1이면 TTL 없음)
         */
        public long getTtlSeconds() {
            return this.ttlSeconds;
        }

        /**
         * TTL이 설정되어 있는지 확인합니다.
         *
         * @return TTL이 있으면 true, 없으면 false
         */
        public boolean hasTtl() {
            return this.ttlSeconds > 0;
        }
    }
}
