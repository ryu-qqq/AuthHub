package com.ryuqq.authhub.application.tenant.port.out.command;

import com.ryuqq.authhub.application.tenant.dto.response.OnboardingResult;

/**
 * OnboardingIdempotencyCommandPort - 온보딩 멱등키 Command 포트
 *
 * <p>온보딩 멱등키 기반 캐시 저장 Port입니다.
 *
 * <p><strong>특징:</strong>
 *
 * <ul>
 *   <li>Cache Command 전용 Port (저장)
 *   <li>Redis 캐시 저장소 대상
 *   <li>TTL 설정 필수 (24시간 권장)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface OnboardingIdempotencyCommandPort {

    /**
     * 온보딩 결과 캐시 저장
     *
     * @param idempotencyKey 멱등키 (X-Idempotency-Key 헤더)
     * @param result 온보딩 결과
     * @param ttlSeconds TTL (초)
     */
    void save(String idempotencyKey, OnboardingResult result, long ttlSeconds);
}
