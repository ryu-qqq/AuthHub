package com.ryuqq.authhub.application.tenant.port.out.query;

import com.ryuqq.authhub.application.tenant.dto.response.OnboardingResult;
import java.util.Optional;

/**
 * OnboardingIdempotencyQueryPort - 온보딩 멱등키 Query 포트
 *
 * <p>온보딩 멱등키 기반 캐시 조회 Port입니다.
 *
 * <p><strong>특징:</strong>
 *
 * <ul>
 *   <li>Cache Query 전용 Port (조회)
 *   <li>Redis 캐시 저장소 대상
 *   <li>X-Idempotency-Key 헤더 기반 멱등성 보장
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface OnboardingIdempotencyQueryPort {

    /**
     * 멱등키로 캐시된 온보딩 결과 조회
     *
     * @param idempotencyKey 멱등키 (X-Idempotency-Key 헤더)
     * @return 캐시된 OnboardingResult (Optional)
     */
    Optional<OnboardingResult> findByIdempotencyKey(String idempotencyKey);
}
