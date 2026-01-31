package com.ryuqq.authhub.adapter.in.rest.tenant.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.adapter.in.rest.tenant.fixture.TenantApiFixture;
import com.ryuqq.authhub.application.tenant.dto.command.CreateTenantCommand;
import com.ryuqq.authhub.application.tenant.dto.command.UpdateTenantNameCommand;
import com.ryuqq.authhub.application.tenant.dto.command.UpdateTenantStatusCommand;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * TenantCommandApiMapper 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("TenantCommandApiMapper 단위 테스트")
class TenantCommandApiMapperTest {

    private TenantCommandApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new TenantCommandApiMapper();
    }

    @Nested
    @DisplayName("toCommand(CreateTenantApiRequest) 메서드는")
    class ToCommandCreateTenant {

        @Test
        @DisplayName("CreateTenantApiRequest를 CreateTenantCommand로 변환한다")
        void shouldConvertToCreateTenantCommand() {
            // Given
            var request = TenantApiFixture.createTenantRequest();

            // When
            CreateTenantCommand result = mapper.toCommand(request);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.name()).isEqualTo(TenantApiFixture.defaultTenantName());
        }
    }

    @Nested
    @DisplayName("toCommand(UUID, UpdateTenantNameApiRequest) 메서드는")
    class ToCommandUpdateTenantName {

        @Test
        @DisplayName("UpdateTenantNameApiRequest를 UpdateTenantNameCommand로 변환한다")
        void shouldConvertToUpdateTenantNameCommand() {
            // Given
            UUID tenantId = TenantApiFixture.defaultTenantId();
            var request = TenantApiFixture.updateTenantNameRequest();

            // When
            UpdateTenantNameCommand result = mapper.toCommand(tenantId, request);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.tenantId()).isEqualTo(tenantId.toString());
            assertThat(result.name()).isEqualTo("수정된테넌트");
        }
    }

    @Nested
    @DisplayName("toCommand(UUID, UpdateTenantStatusApiRequest) 메서드는")
    class ToCommandUpdateTenantStatus {

        @Test
        @DisplayName("UpdateTenantStatusApiRequest를 UpdateTenantStatusCommand로 변환한다")
        void shouldConvertToUpdateTenantStatusCommand() {
            // Given
            UUID tenantId = TenantApiFixture.defaultTenantId();
            var request = TenantApiFixture.updateTenantStatusRequest();

            // When
            UpdateTenantStatusCommand result = mapper.toCommand(tenantId, request);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.tenantId()).isEqualTo(tenantId.toString());
            assertThat(result.status()).isEqualTo("INACTIVE");
        }
    }

    @Nested
    @DisplayName("toResponse(TenantResult) 메서드는")
    class ToResponse {

        @Test
        @DisplayName("TenantResult를 TenantApiResponse로 변환한다")
        void shouldConvertToTenantApiResponse() {
            // Given
            var result = TenantApiFixture.tenantResult();

            // When
            var response = mapper.toResponse(result);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.tenantId()).isEqualTo(TenantApiFixture.defaultTenantIdString());
            assertThat(response.name()).isEqualTo(TenantApiFixture.defaultTenantName());
            assertThat(response.status()).isEqualTo(TenantApiFixture.defaultStatus());
            assertThat(response.createdAt()).isNotNull();
            assertThat(response.updatedAt()).isNotNull();
        }
    }
}
