package com.ryuqq.authhub.adapter.out.persistence.redis.tenant.adapter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryuqq.authhub.adapter.out.persistence.redis.common.RedisKeyGenerator;
import com.ryuqq.authhub.application.tenant.dto.response.OnboardingResult;
import com.ryuqq.authhub.application.tenant.port.out.query.OnboardingIdempotencyQueryPort;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * OnboardingIdempotencyQueryAdapter - 온보딩 멱등키 Query 어댑터
 *
 * <p>OnboardingIdempotencyQueryPort 구현체입니다. Redis에서 멱등키 기반 온보딩 결과를 조회합니다.
 *
 * <p><strong>키 패턴:</strong>
 *
 * <ul>
 *   <li>{@code idempotency::onboarding::{key}}
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>@Transactional 사용 금지 (Redis)
 *   <li>비즈니스 로직 금지 (단순 조회만)
 *   <li>KEYS 명령어 절대 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class OnboardingIdempotencyQueryAdapter implements OnboardingIdempotencyQueryPort {

    private static final Logger log =
            LoggerFactory.getLogger(OnboardingIdempotencyQueryAdapter.class);
    private static final String OPERATION_NAME = "onboarding";

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    public OnboardingIdempotencyQueryAdapter(
            RedisTemplate<String, String> redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * 멱등키로 캐시된 온보딩 결과 조회
     *
     * @param idempotencyKey 멱등키 (X-Idempotency-Key 헤더)
     * @return 캐시된 OnboardingResult (Optional)
     */
    @Override
    public Optional<OnboardingResult> findByIdempotencyKey(String idempotencyKey) {
        String key = RedisKeyGenerator.idempotency(OPERATION_NAME, idempotencyKey);
        String json = redisTemplate.opsForValue().get(key);

        if (json == null || json.isBlank()) {
            return Optional.empty();
        }

        try {
            OnboardingResult result = objectMapper.readValue(json, OnboardingResult.class);
            return Optional.of(result);
        } catch (JsonProcessingException e) {
            log.warn(
                    "Failed to deserialize OnboardingResult for idempotencyKey: {}",
                    idempotencyKey,
                    e);
            return Optional.empty();
        }
    }
}
