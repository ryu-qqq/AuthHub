package com.ryuqq.authhub.application.tenant.manager;

import com.ryuqq.authhub.application.tenant.dto.response.OnboardingResult;
import com.ryuqq.authhub.application.tenant.port.out.command.OnboardingIdempotencyCommandPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * OnboardingIdempotencyCommandManager - 온보딩 멱등키 Command 관리자
 *
 * <p>온보딩 멱등키 캐시(Redis) 저장을 담당하는 Manager
 *
 * <p>Redis 작업은 트랜잭션 없이 수행 (외부 시스템)
 *
 * <p><strong>Best Effort 정책:</strong>
 *
 * <ul>
 *   <li>캐시 저장 실패는 비즈니스 실패가 아님
 *   <li>실패 시 로그만 남기고 정상 진행
 *   <li>호출자는 캐시 저장 실패를 인지할 필요 없음
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class OnboardingIdempotencyCommandManager {

    private static final Logger log =
            LoggerFactory.getLogger(OnboardingIdempotencyCommandManager.class);
    private static final long DEFAULT_TTL_SECONDS = 86400L; // 24시간

    private final OnboardingIdempotencyCommandPort commandPort;

    public OnboardingIdempotencyCommandManager(OnboardingIdempotencyCommandPort commandPort) {
        this.commandPort = commandPort;
    }

    /**
     * 온보딩 결과 캐시 저장 (기본 TTL: 24시간)
     *
     * <p>캐시 저장 실패 시 로그만 남기고 정상 진행 (best effort)
     *
     * @param idempotencyKey 멱등키 (X-Idempotency-Key 헤더)
     * @param result 온보딩 결과
     */
    public void save(String idempotencyKey, OnboardingResult result) {
        save(idempotencyKey, result, DEFAULT_TTL_SECONDS);
    }

    /**
     * 온보딩 결과 캐시 저장 (TTL 지정)
     *
     * <p>캐시 저장 실패 시 로그만 남기고 정상 진행 (best effort)
     *
     * @param idempotencyKey 멱등키 (X-Idempotency-Key 헤더)
     * @param result 온보딩 결과
     * @param ttlSeconds TTL (초)
     */
    public void save(String idempotencyKey, OnboardingResult result, long ttlSeconds) {
        try {
            commandPort.save(idempotencyKey, result, ttlSeconds);
            log.info("Idempotency cache saved for key: {}", idempotencyKey);
        } catch (Exception e) {
            log.error(
                    "Failed to save idempotency cache for key: {}. Client retry may cause duplicate"
                            + " error.",
                    idempotencyKey,
                    e);
        }
    }
}
