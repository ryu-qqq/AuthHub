package com.ryuqq.authhub.application.organization.assembler;

import com.ryuqq.authhub.application.organization.dto.response.OrganizationResponse;
import com.ryuqq.authhub.domain.organization.aggregate.Organization;
import com.ryuqq.authhub.domain.organization.fixture.OrganizationFixture;
import com.ryuqq.authhub.domain.organization.vo.OrganizationStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * OrganizationAssembler 단위 테스트
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
@DisplayName("OrganizationAssembler 테스트")
class OrganizationAssemblerTest {

    private final OrganizationAssembler organizationAssembler = new OrganizationAssembler();

    @Nested
    @DisplayName("toResponse() - Domain -> Response 변환")
    class ToResponse {

        @Test
        @DisplayName("Organization Domain을 OrganizationResponse로 변환해야 한다")
        void shouldConvertOrganizationToResponse() {
            // Given
            Organization organization = OrganizationFixture.anOrganization();

            // When
            OrganizationResponse response = organizationAssembler.toResponse(organization);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.organizationId()).isEqualTo(organization.organizationIdValue());
            assertThat(response.tenantId()).isEqualTo(organization.tenantIdValue());
            assertThat(response.name()).isEqualTo(organization.organizationNameValue());
            assertThat(response.status()).isEqualTo(organization.statusValue());
            assertThat(response.createdAt()).isEqualTo(organization.createdAt());
            assertThat(response.updatedAt()).isEqualTo(organization.updatedAt());
        }

        @Test
        @DisplayName("새로운 Organization (ID 없음)도 변환해야 한다")
        void shouldHandleNewOrganization() {
            // Given
            Organization organization = OrganizationFixture.aNewOrganization();

            // When
            OrganizationResponse response = organizationAssembler.toResponse(organization);

            // Then
            assertThat(response.organizationId()).isNull();
            assertThat(response.name()).isNotNull();
            assertThat(response.status()).isEqualTo("ACTIVE");
        }

        @Test
        @DisplayName("null Organization 입력 시 NullPointerException이 발생해야 한다")
        void shouldThrowExceptionWhenOrganizationIsNull() {
            // When & Then
            assertThatThrownBy(() -> organizationAssembler.toResponse(null))
                    .isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @DisplayName("다양한 OrganizationStatus 변환")
    class StatusConversion {

        @Test
        @DisplayName("ACTIVE 상태가 정확히 변환되어야 한다")
        void shouldConvertActiveStatus() {
            // Given
            Organization organization = OrganizationFixture.anOrganization();

            // When
            OrganizationResponse response = organizationAssembler.toResponse(organization);

            // Then
            assertThat(response.status()).isEqualTo("ACTIVE");
        }

        @Test
        @DisplayName("INACTIVE 상태가 정확히 변환되어야 한다")
        void shouldConvertInactiveStatus() {
            // Given
            Organization organization = OrganizationFixture.anInactiveOrganization();

            // When
            OrganizationResponse response = organizationAssembler.toResponse(organization);

            // Then
            assertThat(response.status()).isEqualTo("INACTIVE");
        }

        @Test
        @DisplayName("DELETED 상태가 정확히 변환되어야 한다")
        void shouldConvertDeletedStatus() {
            // Given
            Organization organization = OrganizationFixture.aDeletedOrganization();

            // When
            OrganizationResponse response = organizationAssembler.toResponse(organization);

            // Then
            assertThat(response.status()).isEqualTo("DELETED");
        }
    }

    @Nested
    @DisplayName("필드 변환 정확성")
    class FieldConversion {

        @Test
        @DisplayName("커스텀 이름으로 생성된 Organization이 정확히 변환되어야 한다")
        void shouldConvertCustomName() {
            // Given
            Organization organization = OrganizationFixture.builder()
                    .asExisting()
                    .organizationName("커스텀 조직명")
                    .build();

            // When
            OrganizationResponse response = organizationAssembler.toResponse(organization);

            // Then
            assertThat(response.name()).isEqualTo("커스텀 조직명");
        }

        @Test
        @DisplayName("다른 tenantId를 가진 Organization이 정확히 변환되어야 한다")
        void shouldConvertDifferentTenantId() {
            // Given
            Organization organization = OrganizationFixture.builder()
                    .asExisting()
                    .tenantId(999L)
                    .build();

            // When
            OrganizationResponse response = organizationAssembler.toResponse(organization);

            // Then
            assertThat(response.tenantId()).isEqualTo(999L);
        }
    }
}
