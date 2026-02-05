package com.ryuqq.authhub.adapter.in.rest.tenantservice.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.authhub.adapter.in.rest.tenantservice.dto.request.SubscribeTenantServiceApiRequest;
import com.ryuqq.authhub.adapter.in.rest.tenantservice.dto.request.UpdateTenantServiceStatusApiRequest;
import com.ryuqq.authhub.adapter.in.rest.tenantservice.fixture.TenantServiceApiFixture;
import com.ryuqq.authhub.application.tenantservice.dto.command.SubscribeTenantServiceCommand;
import com.ryuqq.authhub.application.tenantservice.dto.command.UpdateTenantServiceStatusCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * TenantServiceCommandApiMapper 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("TenantServiceCommandApiMapper 단위 테스트")
class TenantServiceCommandApiMapperTest {

    private TenantServiceCommandApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new TenantServiceCommandApiMapper();
    }

    @Nested
    @DisplayName("toCommand(SubscribeTenantServiceApiRequest) 메서드는")
    class ToCommandSubscribeTenantService {

        @Test
        @DisplayName("SubscribeTenantServiceApiRequest를 SubscribeTenantServiceCommand로 변환한다")
        void shouldConvertToSubscribeTenantServiceCommand() {
            // Given
            SubscribeTenantServiceApiRequest request = TenantServiceApiFixture.subscribeRequest();

            // When
            SubscribeTenantServiceCommand result = mapper.toCommand(request);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.tenantId()).isEqualTo(TenantServiceApiFixture.defaultTenantId());
            assertThat(result.serviceId()).isEqualTo(TenantServiceApiFixture.defaultServiceId());
        }

        @Test
        @DisplayName("request가 null이면 NullPointerException을 발생시킨다")
        void shouldThrowExceptionWhenRequestIsNull() {
            // When & Then
            assertThatThrownBy(() -> mapper.toCommand((SubscribeTenantServiceApiRequest) null))
                    .isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @DisplayName("toCommand(Long, UpdateTenantServiceStatusApiRequest) 메서드는")
    class ToCommandUpdateTenantServiceStatus {

        @Test
        @DisplayName("UpdateTenantServiceStatusApiRequest를 UpdateTenantServiceStatusCommand로 변환한다")
        void shouldConvertToUpdateTenantServiceStatusCommand() {
            // Given
            Long tenantServiceId = TenantServiceApiFixture.defaultTenantServiceId();
            UpdateTenantServiceStatusApiRequest request =
                    TenantServiceApiFixture.updateStatusRequest();

            // When
            UpdateTenantServiceStatusCommand result = mapper.toCommand(tenantServiceId, request);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.tenantServiceId()).isEqualTo(tenantServiceId);
            assertThat(result.status()).isEqualTo("INACTIVE");
        }

        @Test
        @DisplayName("request가 null이면 NullPointerException을 발생시킨다")
        void shouldThrowExceptionWhenRequestIsNull() {
            // Given
            Long tenantServiceId = TenantServiceApiFixture.defaultTenantServiceId();

            // When & Then
            assertThatThrownBy(() -> mapper.toCommand(tenantServiceId, null))
                    .isInstanceOf(NullPointerException.class);
        }
    }
}
