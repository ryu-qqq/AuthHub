package com.ryuqq.authhub.application.tenant.assembler;

import com.ryuqq.authhub.application.tenant.dto.response.TenantResponse;
import com.ryuqq.authhub.domain.tenant.aggregate.Tenant;
import com.ryuqq.authhub.domain.tenant.fixture.TenantFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * TenantAssembler 단위 테스트
 *
 * <p>Kent Beck TDD - Red Phase: 실패하는 테스트 먼저 작성
 *
 * <p>Assembler 규칙:
 * <ul>
 *   <li>Domain → Response 변환만 담당</li>
 *   <li>비즈니스 로직 없음 (순수 변환)</li>
 *   <li>null-safe 변환</li>
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@DisplayName("TenantAssembler 테스트")
class TenantAssemblerTest {

    private final TenantAssembler tenantAssembler = new TenantAssembler();

    @Nested
    @DisplayName("toResponse() - Domain -> Response 변환")
    class ToResponse {

        @Test
        @DisplayName("Tenant Domain을 TenantResponse로 변환해야 한다")
        void shouldConvertTenantToResponse() {
            // Given
            Tenant tenant = TenantFixture.aTenant();

            // When
            TenantResponse response = tenantAssembler.toResponse(tenant);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.tenantId()).isEqualTo(tenant.tenantIdValue());
            assertThat(response.name()).isEqualTo(tenant.tenantNameValue());
            assertThat(response.status()).isEqualTo(tenant.statusValue());
            assertThat(response.createdAt()).isEqualTo(tenant.createdAt());
            assertThat(response.updatedAt()).isEqualTo(tenant.updatedAt());
        }

        @Test
        @DisplayName("새로운 Tenant (ID 없음)도 변환해야 한다")
        void shouldHandleNewTenant() {
            // Given
            Tenant tenant = TenantFixture.aNewTenant();

            // When
            TenantResponse response = tenantAssembler.toResponse(tenant);

            // Then
            assertThat(response.tenantId()).isNull();
            assertThat(response.name()).isNotNull();
            assertThat(response.status()).isEqualTo("ACTIVE");
        }

        @Test
        @DisplayName("null Tenant 입력 시 NullPointerException이 발생해야 한다")
        void shouldThrowExceptionWhenTenantIsNull() {
            // When & Then
            assertThatThrownBy(() -> tenantAssembler.toResponse(null))
                    .isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @DisplayName("다양한 TenantStatus 변환")
    class StatusConversion {

        @Test
        @DisplayName("ACTIVE 상태가 정확히 변환되어야 한다")
        void shouldConvertActiveStatus() {
            // Given
            Tenant tenant = TenantFixture.aTenant();

            // When
            TenantResponse response = tenantAssembler.toResponse(tenant);

            // Then
            assertThat(response.status()).isEqualTo("ACTIVE");
        }

        @Test
        @DisplayName("INACTIVE 상태가 정확히 변환되어야 한다")
        void shouldConvertInactiveStatus() {
            // Given
            Tenant tenant = TenantFixture.anInactiveTenant();

            // When
            TenantResponse response = tenantAssembler.toResponse(tenant);

            // Then
            assertThat(response.status()).isEqualTo("INACTIVE");
        }

        @Test
        @DisplayName("DELETED 상태가 정확히 변환되어야 한다")
        void shouldConvertDeletedStatus() {
            // Given
            Tenant tenant = TenantFixture.aDeletedTenant();

            // When
            TenantResponse response = tenantAssembler.toResponse(tenant);

            // Then
            assertThat(response.status()).isEqualTo("DELETED");
        }
    }

    @Nested
    @DisplayName("필드 변환 정확성")
    class FieldConversion {

        @Test
        @DisplayName("커스텀 이름으로 생성된 Tenant가 정확히 변환되어야 한다")
        void shouldConvertCustomName() {
            // Given
            Tenant tenant = TenantFixture.builder()
                    .asExisting()
                    .tenantName("커스텀 테넌트명")
                    .build();

            // When
            TenantResponse response = tenantAssembler.toResponse(tenant);

            // Then
            assertThat(response.name()).isEqualTo("커스텀 테넌트명");
        }
    }
}
