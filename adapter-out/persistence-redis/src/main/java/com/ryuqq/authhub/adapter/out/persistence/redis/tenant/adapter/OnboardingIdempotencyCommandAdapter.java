package com.ryuqq.authhub.adapter.out.persistence.redis.tenant.adapter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryuqq.authhub.adapter.out.persistence.redis.common.RedisKeyGenerator;
import com.ryuqq.authhub.application.tenant.dto.response.OnboardingResult;
import com.ryuqq.authhub.application.tenant.port.out.command.OnboardingIdempotencyCommandPort;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * OnboardingIdempotencyCommandAdapter - 온보딩 멱등키 Command 어댑터
 *
 * <p>OnboardingIdempotencyCommandPort 구현체입니다. Redis에 멱등키 기반 온보딩 결과를 저장합니다.
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
 *   <li>비즈니스 로직 금지 (단순 저장만)
 *   <li>KEYS 명령어 절대 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class OnboardingIdempotencyCommandAdapter implements OnboardingIdempotencyCommandPort {

    private static final Logger log =
            LoggerFactory.getLogger(OnboardingIdempotencyCommandAdapter.class);
    private static final String OPERATION_NAME = "onboarding";

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    public OnboardingIdempotencyCommandAdapter(
            RedisTemplate<String, String> redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * 온보딩 결과 캐시 저장
     *
     * @param idempotencyKey 멱등키 (X-Idempotency-Key 헤더)
     * @param result 온보딩 결과
     * @param ttlSeconds TTL (초)
     */
    @Override
    public void save(String idempotencyKey, OnboardingResult result, long ttlSeconds) {
        String key = RedisKeyGenerator.idempotency(OPERATION_NAME, idempotencyKey);

        try {
            String json = objectMapper.writeValueAsString(result);
            redisTemplate.opsForValue().set(key, json, ttlSeconds, TimeUnit.SECONDS);
            log.debug("Saved OnboardingResult to Redis for idempotencyKey: {}", idempotencyKey);
        } catch (JsonProcessingException e) {
            log.error(
                    "Failed to serialize OnboardingResult for idempotencyKey: {}",
                    idempotencyKey,
                    e);
            throw new IllegalStateException("Failed to serialize OnboardingResult", e);
        }
    }
}
