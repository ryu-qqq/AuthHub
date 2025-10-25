package com.ryuqq.authhub.application.security.ratelimit.port.out;

import com.ryuqq.authhub.domain.security.ratelimit.vo.RateLimitType;

/**
 * Rate Limit 리셋 Port (Port Out).
 *
 * <p>Redis에서 Rate Limit 카운트를 삭제(리셋)하는 아웃바운드 포트입니다.
 * Hexagonal Architecture의 Port-Adapter 패턴에 따라 Infrastructure Layer와의 경계 역할을 합니다.</p>
 *
 * <p><strong>주요 책임:</strong></p>
 * <ul>
 *   <li>Redis 카운트 키 삭제</li>
 *   <li>관리자 수동 리셋 지원</li>
 *   <li>테스트 환경 초기화</li>
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
 * <p><strong>사용 시나리오:</strong></p>
 * <ul>
 *   <li>관리자가 특정 IP/사용자의 제한 해제</li>
 *   <li>오탐지(False Positive) 해제</li>
 *   <li>테스트 환경 카운터 초기화</li>
 *   <li>긴급 상황 대응 (DDoS 공격 후 정상 IP 복구)</li>
 * </ul>
 *
 * <p><strong>구현 계층 (Adapter-Out-Persistence):</strong></p>
 * <ul>
 *   <li>RateLimitPersistenceAdapter (Port 구현체)</li>
 *   <li>RedisTemplate.delete() 사용</li>
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
public interface ResetRateLimitPort {

    /**
     * Redis에서 Rate Limit 카운트를 삭제(리셋)합니다.
     *
     * <p>Redis DEL 명령을 사용하여 카운트 키를 완전히 삭제합니다.</p>
     *
     * @param identifier 제한 대상 식별자 (IP 주소, 사용자 ID 등)
     * @param endpoint API 엔드포인트
     * @param type Rate Limit 타입
     * @return 삭제 성공 여부 (true: 삭제됨, false: 키가 없었음)
     * @throws IllegalArgumentException identifier, endpoint가 null이거나 빈 문자열이거나, type이 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    boolean reset(String identifier, String endpoint, RateLimitType type);

    /**
     * 특정 식별자의 모든 엔드포인트 Rate Limit을 리셋합니다.
     *
     * <p>특정 IP 또는 사용자의 모든 Rate Limit을 일괄 삭제할 때 사용합니다.</p>
     *
     * @param identifier 제한 대상 식별자
     * @param type Rate Limit 타입
     * @return 삭제된 키의 개수
     * @author AuthHub Team
     * @since 1.0.0
     */
    int resetAll(String identifier, RateLimitType type);

    /**
     * 특정 엔드포인트의 모든 Rate Limit을 리셋합니다.
     *
     * <p>특정 API 엔드포인트의 모든 IP/사용자 제한을 일괄 삭제할 때 사용합니다.</p>
     *
     * @param endpoint API 엔드포인트
     * @param type Rate Limit 타입
     * @return 삭제된 키의 개수
     * @author AuthHub Team
     * @since 1.0.0
     */
    int resetByEndpoint(String endpoint, RateLimitType type);
}
