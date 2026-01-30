package com.ryuqq.authhub.adapter.out.persistence.redis.tenant.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryuqq.authhub.application.tenant.dto.response.OnboardingResult;
import java.util.Optional;
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
 * OnboardingIdempotencyQueryAdapter 단위 테스트
 *
 * <p>Mock 객체를 사용하여 외부 의존성 없이 Query Adapter의 동작을 검증합니다.
 *
 * <p><strong>테스트 전략:</strong>
 *
 * <ul>
 *   <li>MockitoExtension 사용 - {@code @SpringBootTest} 금지 (Zero-Tolerance)
 *   <li>RedisTemplate, ObjectMapper는 Mock으로 대체
 *   <li>Query 메서드(findByIdempotencyKey)의 동작 검증
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("cache")
@Tag("persistence-layer")
@DisplayName("OnboardingIdempotencyQueryAdapter 단위 테스트")
class OnboardingIdempotencyQueryAdapterTest {

    private static final String TEST_IDEMPOTENCY_KEY = "test-idempotency-key-456";
    private static final String TEST_JSON =
            "{\"tenantId\":\"tenant-id-uuid\",\"organizationId\":\"org-id-uuid\"}";
    private static final OnboardingResult TEST_RESULT =
            new OnboardingResult("tenant-id-uuid", "org-id-uuid");

    @Mock private RedisTemplate<String, String> redisTemplate;

    @Mock private ValueOperations<String, String> valueOperations;

    @Mock private ObjectMapper objectMapper;

    private OnboardingIdempotencyQueryAdapter queryAdapter;

    @BeforeEach
    void setUp() {
        queryAdapter = new OnboardingIdempotencyQueryAdapter(redisTemplate, objectMapper);
    }

    @Nested
    @DisplayName("findByIdempotencyKey() 메서드는")
    class FindByIdempotencyKeyMethod {

        @Test
        @DisplayName("캐시에서 OnboardingResult를 조회한다")
        void shouldReturnOnboardingResultWhenExists() throws JsonProcessingException {
            // Given
            String expectedKey = "idempotency::onboarding::" + TEST_IDEMPOTENCY_KEY;

            when(redisTemplate.opsForValue()).thenReturn(valueOperations);
            when(valueOperations.get(expectedKey)).thenReturn(TEST_JSON);
            when(objectMapper.readValue(eq(TEST_JSON), eq(OnboardingResult.class)))
                    .thenReturn(TEST_RESULT);

            // When
            Optional<OnboardingResult> result =
                    queryAdapter.findByIdempotencyKey(TEST_IDEMPOTENCY_KEY);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(TEST_RESULT);
            assertThat(result.get().tenantId()).isEqualTo("tenant-id-uuid");
            assertThat(result.get().organizationId()).isEqualTo("org-id-uuid");
        }

        @Test
        @DisplayName("캐시에 없으면 빈 Optional을 반환한다")
        void shouldReturnEmptyWhenNotExists() {
            // Given
            String expectedKey = "idempotency::onboarding::" + TEST_IDEMPOTENCY_KEY;

            when(redisTemplate.opsForValue()).thenReturn(valueOperations);
            when(valueOperations.get(expectedKey)).thenReturn(null);

            // When
            Optional<OnboardingResult> result =
                    queryAdapter.findByIdempotencyKey(TEST_IDEMPOTENCY_KEY);

            // Then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("캐시 값이 빈 문자열이면 빈 Optional을 반환한다")
        void shouldReturnEmptyWhenValueIsBlank() {
            // Given
            String expectedKey = "idempotency::onboarding::" + TEST_IDEMPOTENCY_KEY;

            when(redisTemplate.opsForValue()).thenReturn(valueOperations);
            when(valueOperations.get(expectedKey)).thenReturn("   ");

            // When
            Optional<OnboardingResult> result =
                    queryAdapter.findByIdempotencyKey(TEST_IDEMPOTENCY_KEY);

            // Then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("역직렬화 실패 시 빈 Optional을 반환한다")
        void shouldReturnEmptyWhenDeserializationFails() throws JsonProcessingException {
            // Given
            String expectedKey = "idempotency::onboarding::" + TEST_IDEMPOTENCY_KEY;

            when(redisTemplate.opsForValue()).thenReturn(valueOperations);
            when(valueOperations.get(expectedKey)).thenReturn("invalid-json");
            when(objectMapper.readValue(eq("invalid-json"), eq(OnboardingResult.class)))
                    .thenThrow(new JsonProcessingException("Invalid JSON") {});

            // When
            Optional<OnboardingResult> result =
                    queryAdapter.findByIdempotencyKey(TEST_IDEMPOTENCY_KEY);

            // Then
            assertThat(result).isEmpty();
        }
    }
}
