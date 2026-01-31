package com.ryuqq.authhub.application.tenant.manager;

import com.ryuqq.authhub.application.tenant.dto.response.OnboardingResult;
import com.ryuqq.authhub.application.tenant.port.out.query.OnboardingIdempotencyQueryPort;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * OnboardingIdempotencyQueryManager - 온보딩 멱등키 Query 관리자
 *
 * <p>온보딩 멱등키 캐시(Redis) 조회를 담당하는 Manager
 *
 * <p>Redis 작업은 트랜잭션 없이 수행 (외부 시스템)
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class OnboardingIdempotencyQueryManager {

    private final OnboardingIdempotencyQueryPort queryPort;

    public OnboardingIdempotencyQueryManager(OnboardingIdempotencyQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    /**
     * 멱등키로 캐시된 온보딩 결과 조회
     *
     * @param idempotencyKey 멱등키 (X-Idempotency-Key 헤더)
     * @return 캐시된 OnboardingResult (Optional)
     */
    public Optional<OnboardingResult> findByIdempotencyKey(String idempotencyKey) {
        return queryPort.findByIdempotencyKey(idempotencyKey);
    }
}
