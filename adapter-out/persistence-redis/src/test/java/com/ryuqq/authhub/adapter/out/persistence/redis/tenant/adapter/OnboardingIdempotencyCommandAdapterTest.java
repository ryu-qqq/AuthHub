package com.ryuqq.authhub.adapter.out.persistence.redis.tenant.adapter;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryuqq.authhub.application.tenant.dto.response.OnboardingResult;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

/**
 * OnboardingIdempotencyCommandAdapter 단위 테스트
 *
 * <p>Mock 객체를 사용하여 외부 의존성 없이 Command Adapter의 동작을 검증합니다.
 *
 * <p><strong>테스트 전략:</strong>
 *
 * <ul>
 *   <li>MockitoExtension 사용 - {@code @SpringBootTest} 금지 (Zero-Tolerance)
 *   <li>RedisTemplate, ObjectMapper는 Mock으로 대체
 *   <li>Command 메서드(save)의 동작 검증
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("cache")
@Tag("persistence-layer")
@DisplayName("OnboardingIdempotencyCommandAdapter 단위 테스트")
class OnboardingIdempotencyCommandAdapterTest {

    private static final String TEST_IDEMPOTENCY_KEY = "test-idempotency-key-123";
    private static final OnboardingResult TEST_RESULT =
            new OnboardingResult("tenant-id-uuid", "org-id-uuid");
    private static final String TEST_JSON =
            "{\"tenantId\":\"tenant-id-uuid\",\"organizationId\":\"org-id-uuid\"}";
    private static final long TTL_SECONDS = 86400L;

    @Mock private RedisTemplate<String, String> redisTemplate;

    @Mock private ValueOperations<String, String> valueOperations;

    @Mock private ObjectMapper objectMapper;

    private OnboardingIdempotencyCommandAdapter commandAdapter;

    @BeforeEach
    void setUp() {
        commandAdapter = new OnboardingIdempotencyCommandAdapter(redisTemplate, objectMapper);
    }

    @Nested
    @DisplayName("save() 메서드는")
    class SaveMethod {

        @Test
        @DisplayName("멱등키로 OnboardingResult를 Redis에 저장한다")
        void shouldSaveOnboardingResultWithIdempotencyKey() throws JsonProcessingException {
            // Given
            String expectedKey = "idempotency::onboarding::" + TEST_IDEMPOTENCY_KEY;

            when(redisTemplate.opsForValue()).thenReturn(valueOperations);
            when(objectMapper.writeValueAsString(TEST_RESULT)).thenReturn(TEST_JSON);

            // When
            commandAdapter.save(TEST_IDEMPOTENCY_KEY, TEST_RESULT, TTL_SECONDS);

            // Then
            verify(valueOperations)
                    .set(eq(expectedKey), eq(TEST_JSON), eq(TTL_SECONDS), eq(TimeUnit.SECONDS));
        }

        @Test
        @DisplayName("ObjectMapper 직렬화 실패 시 IllegalStateException을 던진다")
        void shouldThrowIllegalStateExceptionWhenSerializationFails()
                throws JsonProcessingException {
            // Given
            when(redisTemplate.opsForValue()).thenReturn(valueOperations);
            when(objectMapper.writeValueAsString(TEST_RESULT))
                    .thenThrow(new JsonProcessingException("Serialization failed") {});

            // When & Then
            assertThrows(
                    IllegalStateException.class,
                    () -> commandAdapter.save(TEST_IDEMPOTENCY_KEY, TEST_RESULT, TTL_SECONDS));
        }
    }
}
