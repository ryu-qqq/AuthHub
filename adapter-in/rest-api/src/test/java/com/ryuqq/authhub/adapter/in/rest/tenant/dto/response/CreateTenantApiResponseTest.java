package com.ryuqq.authhub.adapter.in.rest.tenant.dto.response;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * CreateTenantApiResponse 단위 테스트
 *
 * <p>검증 범위:
 *
 * <ul>
 *   <li>Record 생성 검증
 *   <li>필드 접근 검증
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@DisplayName("CreateTenantApiResponse 단위 테스트")
@Tag("unit")
@Tag("adapter-rest")
class CreateTenantApiResponseTest {

    @Nested
    @DisplayName("Record 생성 테스트")
    class RecordCreationTest {

        @Test
        @DisplayName("[생성] tenantId로 생성 시 정상 생성")
        void create_withTenantId_shouldCreateSuccessfully() {
            // Given
            String tenantId = UUID.randomUUID().toString();

            // When
            CreateTenantApiResponse response = new CreateTenantApiResponse(tenantId);

            // Then
            assertThat(response.tenantId()).isEqualTo(tenantId);
        }

        @Test
        @DisplayName("[생성] null tenantId로 생성 시 정상 생성")
        void create_withNullTenantId_shouldCreateSuccessfully() {
            // When
            CreateTenantApiResponse response = new CreateTenantApiResponse(null);

            // Then
            assertThat(response.tenantId()).isNull();
        }
    }

    @Nested
    @DisplayName("equals/hashCode 테스트")
    class EqualsHashCodeTest {

        @Test
        @DisplayName("[equals] 같은 tenantId면 같음")
        void equals_withSameTenantId_shouldBeEqual() {
            // Given
            String tenantId = UUID.randomUUID().toString();
            CreateTenantApiResponse response1 = new CreateTenantApiResponse(tenantId);
            CreateTenantApiResponse response2 = new CreateTenantApiResponse(tenantId);

            // When & Then
            assertThat(response1).isEqualTo(response2);
            assertThat(response1.hashCode()).isEqualTo(response2.hashCode());
        }

        @Test
        @DisplayName("[equals] 다른 tenantId면 다름")
        void equals_withDifferentTenantId_shouldNotBeEqual() {
            // Given
            CreateTenantApiResponse response1 =
                    new CreateTenantApiResponse(UUID.randomUUID().toString());
            CreateTenantApiResponse response2 =
                    new CreateTenantApiResponse(UUID.randomUUID().toString());

            // When & Then
            assertThat(response1).isNotEqualTo(response2);
        }
    }
}
