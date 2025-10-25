package com.ryuqq.authhub.application.security.ratelimit.port.out;

import com.ryuqq.authhub.domain.security.ratelimit.vo.RateLimitType;

/**
 * Rate Limit 증가 Port (Port Out).
 *
 * <p>Redis에서 Rate Limit 카운트를 원자적으로 증가시키는 아웃바운드 포트입니다.
 * Hexagonal Architecture의 Port-Adapter 패턴에 따라 Infrastructure Layer와의 경계 역할을 합니다.</p>
 *
 * <p><strong>주요 책임:</strong></p>
 * <ul>
 *   <li>Redis 카운트 원자적 증가 (INCR 명령)</li>
 *   <li>TTL 자동 설정 (첫 증가 시)</li>
 *   <li>증가 후 현재 카운트 반환</li>
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>✅ Port/Adapter 패턴 - Infrastructure Layer 경계</li>
 *   <li>✅ Command/Query 분리 - 쓰기 전용 (Command)</li>
 *   <li>✅ Lombok 금지 - Plain Java 사용</li>
 *   <li>✅ Javadoc 완비 - @author, @since 포함</li>
 *   <li>✅ Transaction 경계 - Redis 연산은 트랜잭션 밖에서 처리</li>
 * </ul>
 *
 * <p><strong>Redis 연산 특성:</strong></p>
 * <ul>
 *   <li>INCR 명령 사용 - 원자적(Atomic) 연산 보장</li>
 *   <li>키가 없으면 0으로 초기화 후 증가 (1 반환)</li>
 *   <li>TTL 자동 설정 - 시간 윈도우 기반</li>
 *   <li>동시성 문제 없음 - Redis 단일 스레드 모델</li>
 * </ul>
 *
 * <p><strong>구현 계층 (Adapter-Out-Persistence):</strong></p>
 * <ul>
 *   <li>RateLimitPersistenceAdapter (Port 구현체)</li>
 *   <li>RedisTemplate.opsForValue().increment() 사용</li>
 *   <li>TTL 설정: RedisTemplate.expire()</li>
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
public interface IncrementRateLimitPort {

    /**
     * Redis 카운트를 원자적으로 증가시키고, 증가 후 값을 반환합니다.
     *
     * <p>Redis INCR 명령을 사용하여 카운트를 1 증가시키며,
     * 키가 처음 생성되는 경우 TTL을 자동으로 설정합니다.</p>
     *
     * <p><strong>동작 방식:</strong></p>
     * <ol>
     *   <li>Redis INCR 명령 실행 (원자적)</li>
     *   <li>키가 없었다면 → 0에서 1로 증가</li>
     *   <li>키가 있었다면 → 기존 값에서 1 증가</li>
     *   <li>첫 증가 시 TTL 설정 (EXPIRE 명령)</li>
     *   <li>증가 후 현재 값 반환</li>
     * </ol>
     *
     * @param identifier 제한 대상 식별자 (IP 주소, 사용자 ID 등)
     * @param endpoint API 엔드포인트
     * @param type Rate Limit 타입
     * @param ttlSeconds TTL (Time To Live, 초 단위)
     * @return 증가 후 현재 카운트
     * @throws IllegalArgumentException identifier, endpoint가 null이거나 빈 문자열이거나,
     *                                  type이 null이거나, ttlSeconds가 0 이하인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    long incrementAndGet(String identifier, String endpoint, RateLimitType type, long ttlSeconds);

    /**
     * Redis 카운트를 지정된 값만큼 증가시킵니다 (배치 증가).
     *
     * <p>여러 요청을 한 번에 처리할 때 사용합니다.</p>
     *
     * @param identifier 제한 대상 식별자
     * @param endpoint API 엔드포인트
     * @param type Rate Limit 타입
     * @param incrementBy 증가할 값
     * @param ttlSeconds TTL (초 단위)
     * @return 증가 후 현재 카운트
     * @throws IllegalArgumentException incrementBy가 0 이하인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    long incrementByAndGet(
            String identifier,
            String endpoint,
            RateLimitType type,
            long incrementBy,
            long ttlSeconds
    );
}
