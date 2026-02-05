package com.ryuqq.authhub.adapter.in.rest.internal.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.authhub.adapter.in.rest.internal.dto.command.OnboardingApiRequest;
import com.ryuqq.authhub.adapter.in.rest.internal.fixture.InternalApiFixture;
import com.ryuqq.authhub.application.tenant.dto.command.OnboardingCommand;
import com.ryuqq.authhub.application.tenant.dto.response.OnboardingResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * InternalOnboardingApiMapper 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("InternalOnboardingApiMapper 단위 테스트")
class InternalOnboardingApiMapperTest {

    private InternalOnboardingApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new InternalOnboardingApiMapper();
    }

    @Nested
    @DisplayName("toCommand 메서드는")
    class ToCommand {

        @Test
        @DisplayName("OnboardingApiRequest와 idempotencyKey를 OnboardingCommand로 정상 변환한다")
        void shouldConvertToOnboardingCommand() {
            // Given
            OnboardingApiRequest request = InternalApiFixture.onboardingRequest();
            String idempotencyKey = "test-idempotency-key-12345";

            // When
            OnboardingCommand command = mapper.toCommand(request, idempotencyKey);

            // Then
            assertThat(command).isNotNull();
            assertThat(command.tenantName()).isEqualTo(InternalApiFixture.defaultTenantName());
            assertThat(command.organizationName()).isEqualTo(InternalApiFixture.defaultOrgName());
            assertThat(command.idempotencyKey()).isEqualTo(idempotencyKey);
        }

        @Test
        @DisplayName("null request를 처리한다")
        void shouldHandleNullRequest() {
            // Given
            OnboardingApiRequest request = null;
            String idempotencyKey = "test-key";

            // When & Then
            org.junit.jupiter.api.Assertions.assertThrows(
                    NullPointerException.class, () -> mapper.toCommand(request, idempotencyKey));
        }

        @Test
        @DisplayName("null idempotencyKey는 NullPointerException을 발생시킨다")
        void shouldThrowExceptionWhenIdempotencyKeyIsNull() {
            // Given
            OnboardingApiRequest request = InternalApiFixture.onboardingRequest();
            String idempotencyKey = null;

            // When & Then
            org.junit.jupiter.api.Assertions.assertThrows(
                    NullPointerException.class, () -> mapper.toCommand(request, idempotencyKey));
        }
    }

    @Nested
    @DisplayName("toApiResponse 메서드는")
    class ToApiResponse {

        @Test
        @DisplayName("OnboardingResult를 OnboardingResultApiResponse로 정상 변환한다")
        void shouldConvertToOnboardingResultApiResponse() {
            // Given
            String tenantId = "01933abc-1234-7000-8000-000000000001";
            String organizationId = "01933abc-1234-7000-8000-000000000002";
            OnboardingResult result = new OnboardingResult(tenantId, organizationId);

            // When
            var response = mapper.toApiResponse(result);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.tenantId()).isEqualTo(tenantId);
            assertThat(response.organizationId()).isEqualTo(organizationId);
        }

        @Test
        @DisplayName("result가 null이면 NullPointerException을 발생시킨다")
        void shouldThrowExceptionWhenResultIsNull() {
            // When & Then
            assertThatThrownBy(() -> mapper.toApiResponse(null))
                    .isInstanceOf(NullPointerException.class);
        }
    }
}
