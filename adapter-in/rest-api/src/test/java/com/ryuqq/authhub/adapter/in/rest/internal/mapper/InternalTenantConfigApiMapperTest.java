package com.ryuqq.authhub.adapter.in.rest.internal.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.authhub.adapter.in.rest.internal.fixture.InternalApiFixture;
import com.ryuqq.authhub.application.tenant.dto.response.TenantConfigResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * InternalTenantConfigApiMapper 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("InternalTenantConfigApiMapper 단위 테스트")
class InternalTenantConfigApiMapperTest {

    private InternalTenantConfigApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new InternalTenantConfigApiMapper();
    }

    @Nested
    @DisplayName("toApiResponse 메서드는")
    class ToApiResponse {

        @Test
        @DisplayName("TenantConfigResult를 TenantConfigApiResponse로 정상 변환한다")
        void shouldConvertToTenantConfigApiResponse() {
            // Given
            String tenantId = InternalApiFixture.defaultTenantId();
            String name = InternalApiFixture.defaultTenantName();
            String status = InternalApiFixture.defaultStatus();
            boolean active = true;
            TenantConfigResult result = new TenantConfigResult(tenantId, name, status, active);

            // When
            var response = mapper.toApiResponse(result);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.tenantId()).isEqualTo(tenantId);
            assertThat(response.name()).isEqualTo(name);
            assertThat(response.status()).isEqualTo(status);
            assertThat(response.active()).isTrue();
        }

        @Test
        @DisplayName("active가 false인 TenantConfigResult를 변환한다")
        void shouldConvertInactiveTenant() {
            // Given
            String tenantId = InternalApiFixture.defaultTenantId();
            String name = InternalApiFixture.defaultTenantName();
            String status = "INACTIVE";
            boolean active = false;
            TenantConfigResult result = new TenantConfigResult(tenantId, name, status, active);

            // When
            var response = mapper.toApiResponse(result);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.tenantId()).isEqualTo(tenantId);
            assertThat(response.name()).isEqualTo(name);
            assertThat(response.status()).isEqualTo(status);
            assertThat(response.active()).isFalse();
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
