package com.ryuqq.authhub.application.tenant.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.authhub.application.tenant.dto.response.OnboardingResult;
import com.ryuqq.authhub.application.tenant.port.out.query.OnboardingIdempotencyQueryPort;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * OnboardingIdempotencyQueryManager 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("OnboardingIdempotencyQueryManager 단위 테스트")
class OnboardingIdempotencyQueryManagerTest {

    @Mock private OnboardingIdempotencyQueryPort queryPort;

    private OnboardingIdempotencyQueryManager sut;

    @BeforeEach
    void setUp() {
        sut = new OnboardingIdempotencyQueryManager(queryPort);
    }

    @Nested
    @DisplayName("findByIdempotencyKey 메서드")
    class FindByIdempotencyKey {

        @Test
        @DisplayName("성공: 캐시에 있으면 OnboardingResult Optional 반환")
        void shouldReturnResult_WhenCached() {
            // given
            String idempotencyKey = "test-idempotency-key";
            OnboardingResult expected = new OnboardingResult("tenant-uuid", "org-uuid");

            given(queryPort.findByIdempotencyKey(idempotencyKey)).willReturn(Optional.of(expected));

            // when
            Optional<OnboardingResult> result = sut.findByIdempotencyKey(idempotencyKey);

            // then
            assertThat(result).isPresent();
            assertThat(result.get().tenantId()).isEqualTo("tenant-uuid");
            assertThat(result.get().organizationId()).isEqualTo("org-uuid");
            then(queryPort).should().findByIdempotencyKey(idempotencyKey);
        }

        @Test
        @DisplayName("캐시에 없으면 빈 Optional 반환")
        void shouldReturnEmpty_WhenNotCached() {
            // given
            String idempotencyKey = "unknown-key";
            given(queryPort.findByIdempotencyKey(idempotencyKey)).willReturn(Optional.empty());

            // when
            Optional<OnboardingResult> result = sut.findByIdempotencyKey(idempotencyKey);

            // then
            assertThat(result).isEmpty();
            then(queryPort).should().findByIdempotencyKey(idempotencyKey);
        }
    }
}
