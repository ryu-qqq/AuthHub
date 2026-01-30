package com.ryuqq.authhub.application.tenant.manager;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;

import com.ryuqq.authhub.application.tenant.dto.response.OnboardingResult;
import com.ryuqq.authhub.application.tenant.port.out.command.OnboardingIdempotencyCommandPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * OnboardingIdempotencyCommandManager 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("OnboardingIdempotencyCommandManager 단위 테스트")
class OnboardingIdempotencyCommandManagerTest {

    @Mock private OnboardingIdempotencyCommandPort commandPort;

    private OnboardingIdempotencyCommandManager sut;

    @BeforeEach
    void setUp() {
        sut = new OnboardingIdempotencyCommandManager(commandPort);
    }

    @Nested
    @DisplayName("save 메서드")
    class Save {

        @Test
        @DisplayName("성공: Port를 통해 캐시 저장")
        void shouldSaveCache_ThroughPort() {
            // given
            String idempotencyKey = "test-key";
            OnboardingResult result = new OnboardingResult("tenant-id", "org-id");

            // when
            sut.save(idempotencyKey, result);

            // then
            then(commandPort).should().save(eq(idempotencyKey), eq(result), eq(86400L));
        }

        @Test
        @DisplayName("성공: TTL 지정하여 캐시 저장")
        void shouldSaveCache_WithCustomTtl() {
            // given
            String idempotencyKey = "test-key";
            OnboardingResult result = new OnboardingResult("tenant-id", "org-id");
            long customTtl = 3600L;

            // when
            sut.save(idempotencyKey, result, customTtl);

            // then
            then(commandPort).should().save(eq(idempotencyKey), eq(result), eq(customTtl));
        }

        @Test
        @DisplayName("성공: 캐시 저장 실패해도 예외 던지지 않음 (best effort)")
        void shouldNotThrowException_WhenCacheSaveFails() {
            // given
            String idempotencyKey = "test-key";
            OnboardingResult result = new OnboardingResult("tenant-id", "org-id");

            willThrow(new RuntimeException("Redis connection failed"))
                    .given(commandPort)
                    .save(any(), any(), anyLong());

            // when & then - 예외가 발생하지 않아야 함
            assertThatCode(() -> sut.save(idempotencyKey, result)).doesNotThrowAnyException();
        }
    }
}
