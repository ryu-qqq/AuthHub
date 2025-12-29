package com.ryuqq.authhub.sdk.model.tenant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Tenant Model Tests")
class TenantModelTest {

    @Nested
    @DisplayName("CreateTenantRequest")
    class CreateTenantRequestTest {

        @Test
        @DisplayName("유효한 이름으로 생성 성공")
        void shouldCreateWithValidName() {
            CreateTenantRequest request = new CreateTenantRequest("Test Tenant");

            assertThat(request.name()).isEqualTo("Test Tenant");
        }

        @Test
        @DisplayName("name이 null이면 예외 발생")
        void shouldThrowWhenNameIsNull() {
            assertThatThrownBy(() -> new CreateTenantRequest(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("name");
        }

        @Test
        @DisplayName("name이 빈 문자열이면 예외 발생")
        void shouldThrowWhenNameIsBlank() {
            assertThatThrownBy(() -> new CreateTenantRequest("   "))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("name must not be blank");
        }

        @Test
        @DisplayName("equals와 hashCode가 올바르게 동작")
        void shouldImplementEqualsAndHashCode() {
            CreateTenantRequest request1 = new CreateTenantRequest("Test Tenant");
            CreateTenantRequest request2 = new CreateTenantRequest("Test Tenant");

            assertThat(request1).isEqualTo(request2);
            assertThat(request1.hashCode()).isEqualTo(request2.hashCode());
        }
    }

    @Nested
    @DisplayName("CreateTenantResponse")
    class CreateTenantResponseTest {

        @Test
        @DisplayName("tenantId로 생성 성공")
        void shouldCreateWithTenantId() {
            CreateTenantResponse response = new CreateTenantResponse("tenant-123");

            assertThat(response.tenantId()).isEqualTo("tenant-123");
        }
    }

    @Nested
    @DisplayName("TenantResponse")
    class TenantResponseTest {

        @Test
        @DisplayName("모든 필드로 생성 성공")
        void shouldCreateWithAllFields() {
            Instant now = Instant.now();
            TenantResponse response =
                    new TenantResponse("tenant-1", "Test Tenant", "ACTIVE", now, now);

            assertThat(response.tenantId()).isEqualTo("tenant-1");
            assertThat(response.name()).isEqualTo("Test Tenant");
            assertThat(response.status()).isEqualTo("ACTIVE");
            assertThat(response.createdAt()).isEqualTo(now);
            assertThat(response.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("equals와 hashCode가 올바르게 동작")
        void shouldImplementEqualsAndHashCode() {
            Instant now = Instant.now();
            TenantResponse response1 =
                    new TenantResponse("tenant-1", "Test Tenant", "ACTIVE", now, now);
            TenantResponse response2 =
                    new TenantResponse("tenant-1", "Test Tenant", "ACTIVE", now, now);

            assertThat(response1).isEqualTo(response2);
            assertThat(response1.hashCode()).isEqualTo(response2.hashCode());
        }
    }

    @Nested
    @DisplayName("TenantSummaryResponse")
    class TenantSummaryResponseTest {

        @Test
        @DisplayName("모든 필드로 생성 성공")
        void shouldCreateWithAllFields() {
            Instant now = Instant.now();
            TenantSummaryResponse response =
                    new TenantSummaryResponse("tenant-1", "Test Tenant", "ACTIVE", 10, now, now);

            assertThat(response.tenantId()).isEqualTo("tenant-1");
            assertThat(response.name()).isEqualTo("Test Tenant");
            assertThat(response.status()).isEqualTo("ACTIVE");
            assertThat(response.organizationCount()).isEqualTo(10);
            assertThat(response.createdAt()).isEqualTo(now);
            assertThat(response.updatedAt()).isEqualTo(now);
        }
    }

    @Nested
    @DisplayName("TenantDetailResponse")
    class TenantDetailResponseTest {

        @Test
        @DisplayName("모든 필드로 생성 성공")
        void shouldCreateWithAllFields() {
            Instant now = Instant.now();
            List<TenantOrganizationSummaryResponse> organizations =
                    List.of(
                            new TenantOrganizationSummaryResponse(
                                    "org-1", "Test Org", "ACTIVE", now));

            TenantDetailResponse response =
                    new TenantDetailResponse(
                            "tenant-1", "Test Tenant", "ACTIVE", organizations, 50, now, now);

            assertThat(response.tenantId()).isEqualTo("tenant-1");
            assertThat(response.name()).isEqualTo("Test Tenant");
            assertThat(response.status()).isEqualTo("ACTIVE");
            assertThat(response.organizations()).hasSize(1);
            assertThat(response.organizationCount()).isEqualTo(50);
            assertThat(response.createdAt()).isEqualTo(now);
            assertThat(response.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("빈 조직 목록으로 생성 가능")
        void shouldCreateWithEmptyOrganizations() {
            Instant now = Instant.now();
            TenantDetailResponse response =
                    new TenantDetailResponse(
                            "tenant-1", "Test Tenant", "ACTIVE", List.of(), 0, now, now);

            assertThat(response.organizations()).isEmpty();
            assertThat(response.organizationCount()).isZero();
        }
    }

    @Nested
    @DisplayName("TenantOrganizationSummaryResponse")
    class TenantOrganizationSummaryResponseTest {

        @Test
        @DisplayName("모든 필드로 생성 성공")
        void shouldCreateWithAllFields() {
            Instant now = Instant.now();
            TenantOrganizationSummaryResponse response =
                    new TenantOrganizationSummaryResponse("org-1", "Test Org", "ACTIVE", now);

            assertThat(response.organizationId()).isEqualTo("org-1");
            assertThat(response.name()).isEqualTo("Test Org");
            assertThat(response.status()).isEqualTo("ACTIVE");
            assertThat(response.createdAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("equals와 hashCode가 올바르게 동작")
        void shouldImplementEqualsAndHashCode() {
            Instant now = Instant.now();
            TenantOrganizationSummaryResponse response1 =
                    new TenantOrganizationSummaryResponse("org-1", "Test Org", "ACTIVE", now);
            TenantOrganizationSummaryResponse response2 =
                    new TenantOrganizationSummaryResponse("org-1", "Test Org", "ACTIVE", now);

            assertThat(response1).isEqualTo(response2);
            assertThat(response1.hashCode()).isEqualTo(response2.hashCode());
        }
    }

    @Nested
    @DisplayName("UpdateTenantNameRequest")
    class UpdateTenantNameRequestTest {

        @Test
        @DisplayName("name으로 생성 성공")
        void shouldCreateWithName() {
            UpdateTenantNameRequest request = new UpdateTenantNameRequest("New Tenant Name");

            assertThat(request.name()).isEqualTo("New Tenant Name");
        }
    }

    @Nested
    @DisplayName("UpdateTenantStatusRequest")
    class UpdateTenantStatusRequestTest {

        @Test
        @DisplayName("status로 생성 성공")
        void shouldCreateWithStatus() {
            UpdateTenantStatusRequest request = new UpdateTenantStatusRequest("INACTIVE");

            assertThat(request.status()).isEqualTo("INACTIVE");
        }
    }
}
