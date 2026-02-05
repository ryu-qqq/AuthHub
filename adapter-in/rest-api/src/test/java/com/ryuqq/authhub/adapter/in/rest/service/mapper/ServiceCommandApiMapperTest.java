package com.ryuqq.authhub.adapter.in.rest.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.authhub.adapter.in.rest.service.dto.request.CreateServiceApiRequest;
import com.ryuqq.authhub.adapter.in.rest.service.fixture.ServiceApiFixture;
import com.ryuqq.authhub.application.service.dto.command.CreateServiceCommand;
import com.ryuqq.authhub.application.service.dto.command.UpdateServiceCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * ServiceCommandApiMapper 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("ServiceCommandApiMapper 단위 테스트")
class ServiceCommandApiMapperTest {

    private ServiceCommandApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ServiceCommandApiMapper();
    }

    @Nested
    @DisplayName("toCommand(CreateServiceApiRequest) 메서드는")
    class ToCommandCreateService {

        @Test
        @DisplayName("CreateServiceApiRequest를 CreateServiceCommand로 변환한다")
        void shouldConvertToCreateServiceCommand() {
            // Given
            var request = ServiceApiFixture.createServiceRequest();

            // When
            CreateServiceCommand result = mapper.toCommand(request);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.serviceCode()).isEqualTo(ServiceApiFixture.defaultServiceCode());
            assertThat(result.name()).isEqualTo(ServiceApiFixture.defaultName());
            assertThat(result.description()).isEqualTo(ServiceApiFixture.defaultDescription());
        }

        @Test
        @DisplayName("request가 null이면 NullPointerException을 발생시킨다")
        void shouldThrowExceptionWhenRequestIsNull() {
            // When & Then
            assertThatThrownBy(() -> mapper.toCommand((CreateServiceApiRequest) null))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("request의 필드가 null이어도 변환한다")
        void shouldConvertWhenFieldsAreNull() {
            // Given
            var request =
                    new CreateServiceApiRequest(
                            ServiceApiFixture.defaultServiceCode(),
                            ServiceApiFixture.defaultName(),
                            null);

            // When
            CreateServiceCommand result = mapper.toCommand(request);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.serviceCode()).isEqualTo(ServiceApiFixture.defaultServiceCode());
            assertThat(result.name()).isEqualTo(ServiceApiFixture.defaultName());
            assertThat(result.description()).isNull();
        }
    }

    @Nested
    @DisplayName("toCommand(Long, UpdateServiceApiRequest) 메서드는")
    class ToCommandUpdateService {

        @Test
        @DisplayName("UpdateServiceApiRequest를 UpdateServiceCommand로 변환한다")
        void shouldConvertToUpdateServiceCommand() {
            // Given
            Long serviceId = ServiceApiFixture.defaultServiceId();
            var request = ServiceApiFixture.updateServiceRequest();

            // When
            UpdateServiceCommand result = mapper.toCommand(serviceId, request);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.serviceId()).isEqualTo(serviceId);
            assertThat(result.name()).isEqualTo(ServiceApiFixture.defaultName());
            assertThat(result.description()).isEqualTo(ServiceApiFixture.defaultDescription());
            assertThat(result.status()).isEqualTo(ServiceApiFixture.defaultStatus());
        }

        @Test
        @DisplayName("serviceId가 null이면 NullPointerException을 발생시킨다")
        void shouldThrowExceptionWhenServiceIdIsNull() {
            // Given
            var request = ServiceApiFixture.updateServiceRequest();

            // When & Then
            assertThatThrownBy(() -> mapper.toCommand(null, request))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("request가 null이면 NullPointerException을 발생시킨다")
        void shouldThrowExceptionWhenRequestIsNull() {
            // Given
            Long serviceId = ServiceApiFixture.defaultServiceId();

            // When & Then
            assertThatThrownBy(() -> mapper.toCommand(serviceId, null))
                    .isInstanceOf(NullPointerException.class);
        }
    }
}
