package com.ryuqq.authhub.adapter.in.rest.tenant.dto.response;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * TenantApiResponse 단위 테스트
 *
 * <p>검증 범위:
 *
 * <ul>
 *   <li>Record 생성 검증
 *   <li>필드 접근 검증
 *   <li>equals/hashCode 검증
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@DisplayName("TenantApiResponse 단위 테스트")
@Tag("unit")
@Tag("adapter-rest")
class TenantApiResponseTest {

    @Nested
    @DisplayName("Record 생성 테스트")
    class RecordCreationTest {

        @Test
        @DisplayName("[생성] 모든 필드로 생성 시 정상 생성")
        void create_withAllFields_shouldCreateSuccessfully() {
            // Given
            String tenantId = UUID.randomUUID().toString();
            String name = "TestTenant";
            String status = "ACTIVE";
            Instant createdAt = Instant.now();
            Instant updatedAt = Instant.now();

            // When
            TenantApiResponse response =
                    new TenantApiResponse(tenantId, name, status, createdAt, updatedAt);

            // Then
            assertThat(response.tenantId()).isEqualTo(tenantId);
            assertThat(response.name()).isEqualTo(name);
            assertThat(response.status()).isEqualTo(status);
            assertThat(response.createdAt()).isEqualTo(createdAt);
            assertThat(response.updatedAt()).isEqualTo(updatedAt);
        }

        @Test
        @DisplayName("[생성] null 필드로 생성 시 정상 생성")
        void create_withNullFields_shouldCreateSuccessfully() {
            // When
            TenantApiResponse response = new TenantApiResponse(null, null, null, null, null);

            // Then
            assertThat(response.tenantId()).isNull();
            assertThat(response.name()).isNull();
            assertThat(response.status()).isNull();
            assertThat(response.createdAt()).isNull();
            assertThat(response.updatedAt()).isNull();
        }
    }

    @Nested
    @DisplayName("equals/hashCode 테스트")
    class EqualsHashCodeTest {

        @Test
        @DisplayName("[equals] 같은 필드 값이면 같음")
        void equals_withSameFields_shouldBeEqual() {
            // Given
            String tenantId = UUID.randomUUID().toString();
            String name = "TestTenant";
            String status = "ACTIVE";
            Instant createdAt = Instant.parse("2024-01-01T00:00:00Z");
            Instant updatedAt = Instant.parse("2024-01-02T00:00:00Z");

            TenantApiResponse response1 =
                    new TenantApiResponse(tenantId, name, status, createdAt, updatedAt);
            TenantApiResponse response2 =
                    new TenantApiResponse(tenantId, name, status, createdAt, updatedAt);

            // When & Then
            assertThat(response1).isEqualTo(response2);
            assertThat(response1.hashCode()).isEqualTo(response2.hashCode());
        }

        @Test
        @DisplayName("[equals] 다른 tenantId면 다름")
        void equals_withDifferentTenantId_shouldNotBeEqual() {
            // Given
            Instant now = Instant.now();
            TenantApiResponse response1 =
                    new TenantApiResponse(UUID.randomUUID().toString(), "Test", "ACTIVE", now, now);
            TenantApiResponse response2 =
                    new TenantApiResponse(UUID.randomUUID().toString(), "Test", "ACTIVE", now, now);

            // When & Then
            assertThat(response1).isNotEqualTo(response2);
        }

        @Test
        @DisplayName("[equals] 다른 name이면 다름")
        void equals_withDifferentName_shouldNotBeEqual() {
            // Given
            String tenantId = UUID.randomUUID().toString();
            Instant now = Instant.now();
            TenantApiResponse response1 = new TenantApiResponse(tenantId, "Tenant1", "ACTIVE", now, now);
            TenantApiResponse response2 = new TenantApiResponse(tenantId, "Tenant2", "ACTIVE", now, now);

            // When & Then
            assertThat(response1).isNotEqualTo(response2);
        }

        @Test
        @DisplayName("[equals] 다른 status면 다름")
        void equals_withDifferentStatus_shouldNotBeEqual() {
            // Given
            String tenantId = UUID.randomUUID().toString();
            Instant now = Instant.now();
            TenantApiResponse response1 = new TenantApiResponse(tenantId, "Test", "ACTIVE", now, now);
            TenantApiResponse response2 = new TenantApiResponse(tenantId, "Test", "INACTIVE", now, now);

            // When & Then
            assertThat(response1).isNotEqualTo(response2);
        }
    }

    @Nested
    @DisplayName("toString 테스트")
    class ToStringTest {

        @Test
        @DisplayName("[toString] Record의 기본 toString 형식 확인")
        void toString_shouldContainAllFields() {
            // Given
            String tenantId = "test-uuid";
            TenantApiResponse response =
                    new TenantApiResponse(tenantId, "TestTenant", "ACTIVE", null, null);

            // When
            String result = response.toString();

            // Then
            assertThat(result).contains("tenantId=test-uuid");
            assertThat(result).contains("name=TestTenant");
            assertThat(result).contains("status=ACTIVE");
        }
    }
}
