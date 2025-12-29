package com.ryuqq.authhub.sdk.model.organization;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Organization Model Tests")
class OrganizationModelTest {

    @Nested
    @DisplayName("CreateOrganizationRequest")
    class CreateOrganizationRequestTest {

        @Test
        @DisplayName("유효한 입력으로 생성 성공")
        void shouldCreateWithValidInput() {
            CreateOrganizationRequest request =
                    new CreateOrganizationRequest("tenant-1", "Test Org");

            assertThat(request.tenantId()).isEqualTo("tenant-1");
            assertThat(request.name()).isEqualTo("Test Org");
        }

        @Test
        @DisplayName("tenantId가 null이면 예외 발생")
        void shouldThrowWhenTenantIdIsNull() {
            assertThatThrownBy(() -> new CreateOrganizationRequest(null, "Test Org"))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("tenantId");
        }

        @Test
        @DisplayName("name이 null이면 예외 발생")
        void shouldThrowWhenNameIsNull() {
            assertThatThrownBy(() -> new CreateOrganizationRequest("tenant-1", null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("name");
        }

        @Test
        @DisplayName("name이 빈 문자열이면 예외 발생")
        void shouldThrowWhenNameIsBlank() {
            assertThatThrownBy(() -> new CreateOrganizationRequest("tenant-1", "   "))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("name must not be blank");
        }

        @Test
        @DisplayName("equals와 hashCode가 올바르게 동작")
        void shouldImplementEqualsAndHashCode() {
            CreateOrganizationRequest request1 =
                    new CreateOrganizationRequest("tenant-1", "Test Org");
            CreateOrganizationRequest request2 =
                    new CreateOrganizationRequest("tenant-1", "Test Org");

            assertThat(request1).isEqualTo(request2);
            assertThat(request1.hashCode()).isEqualTo(request2.hashCode());
        }
    }

    @Nested
    @DisplayName("CreateOrganizationResponse")
    class CreateOrganizationResponseTest {

        @Test
        @DisplayName("organizationId로 생성 성공")
        void shouldCreateWithOrganizationId() {
            CreateOrganizationResponse response = new CreateOrganizationResponse("org-123");

            assertThat(response.organizationId()).isEqualTo("org-123");
        }
    }

    @Nested
    @DisplayName("OrganizationResponse")
    class OrganizationResponseTest {

        @Test
        @DisplayName("모든 필드로 생성 성공")
        void shouldCreateWithAllFields() {
            Instant now = Instant.now();
            OrganizationResponse response =
                    new OrganizationResponse("org-1", "tenant-1", "Test Org", "ACTIVE", now, now);

            assertThat(response.organizationId()).isEqualTo("org-1");
            assertThat(response.tenantId()).isEqualTo("tenant-1");
            assertThat(response.name()).isEqualTo("Test Org");
            assertThat(response.status()).isEqualTo("ACTIVE");
            assertThat(response.createdAt()).isEqualTo(now);
            assertThat(response.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("equals와 hashCode가 올바르게 동작")
        void shouldImplementEqualsAndHashCode() {
            Instant now = Instant.now();
            OrganizationResponse response1 =
                    new OrganizationResponse("org-1", "tenant-1", "Test Org", "ACTIVE", now, now);
            OrganizationResponse response2 =
                    new OrganizationResponse("org-1", "tenant-1", "Test Org", "ACTIVE", now, now);

            assertThat(response1).isEqualTo(response2);
            assertThat(response1.hashCode()).isEqualTo(response2.hashCode());
        }
    }

    @Nested
    @DisplayName("OrganizationSummaryResponse")
    class OrganizationSummaryResponseTest {

        @Test
        @DisplayName("모든 필드로 생성 성공")
        void shouldCreateWithAllFields() {
            Instant now = Instant.now();
            OrganizationSummaryResponse response =
                    new OrganizationSummaryResponse(
                            "org-1", "tenant-1", "Test Tenant", "Test Org", "ACTIVE", 50, now, now);

            assertThat(response.organizationId()).isEqualTo("org-1");
            assertThat(response.tenantId()).isEqualTo("tenant-1");
            assertThat(response.tenantName()).isEqualTo("Test Tenant");
            assertThat(response.name()).isEqualTo("Test Org");
            assertThat(response.status()).isEqualTo("ACTIVE");
            assertThat(response.userCount()).isEqualTo(50);
            assertThat(response.createdAt()).isEqualTo(now);
            assertThat(response.updatedAt()).isEqualTo(now);
        }
    }

    @Nested
    @DisplayName("OrganizationDetailResponse")
    class OrganizationDetailResponseTest {

        @Test
        @DisplayName("모든 필드로 생성 성공")
        void shouldCreateWithAllFields() {
            Instant now = Instant.now();
            List<OrganizationUserSummaryResponse> users =
                    List.of(new OrganizationUserSummaryResponse("user-1", "user@test.com", now));

            OrganizationDetailResponse response =
                    new OrganizationDetailResponse(
                            "org-1",
                            "tenant-1",
                            "Test Tenant",
                            "Test Org",
                            "ACTIVE",
                            users,
                            100,
                            now,
                            now);

            assertThat(response.organizationId()).isEqualTo("org-1");
            assertThat(response.tenantId()).isEqualTo("tenant-1");
            assertThat(response.tenantName()).isEqualTo("Test Tenant");
            assertThat(response.name()).isEqualTo("Test Org");
            assertThat(response.status()).isEqualTo("ACTIVE");
            assertThat(response.users()).hasSize(1);
            assertThat(response.userCount()).isEqualTo(100);
            assertThat(response.createdAt()).isEqualTo(now);
            assertThat(response.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("빈 사용자 목록으로 생성 가능")
        void shouldCreateWithEmptyUsers() {
            Instant now = Instant.now();
            OrganizationDetailResponse response =
                    new OrganizationDetailResponse(
                            "org-1",
                            "tenant-1",
                            "Test Tenant",
                            "Test Org",
                            "ACTIVE",
                            List.of(),
                            0,
                            now,
                            now);

            assertThat(response.users()).isEmpty();
            assertThat(response.userCount()).isZero();
        }
    }

    @Nested
    @DisplayName("OrganizationUserSummaryResponse")
    class OrganizationUserSummaryResponseTest {

        @Test
        @DisplayName("모든 필드로 생성 성공")
        void shouldCreateWithAllFields() {
            Instant now = Instant.now();
            OrganizationUserSummaryResponse response =
                    new OrganizationUserSummaryResponse("user-1", "user@test.com", now);

            assertThat(response.userId()).isEqualTo("user-1");
            assertThat(response.email()).isEqualTo("user@test.com");
            assertThat(response.createdAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("equals와 hashCode가 올바르게 동작")
        void shouldImplementEqualsAndHashCode() {
            Instant now = Instant.now();
            OrganizationUserSummaryResponse response1 =
                    new OrganizationUserSummaryResponse("user-1", "user@test.com", now);
            OrganizationUserSummaryResponse response2 =
                    new OrganizationUserSummaryResponse("user-1", "user@test.com", now);

            assertThat(response1).isEqualTo(response2);
            assertThat(response1.hashCode()).isEqualTo(response2.hashCode());
        }
    }

    @Nested
    @DisplayName("UpdateOrganizationRequest")
    class UpdateOrganizationRequestTest {

        @Test
        @DisplayName("name으로 생성 성공")
        void shouldCreateWithName() {
            UpdateOrganizationRequest request = new UpdateOrganizationRequest("New Org Name");

            assertThat(request.name()).isEqualTo("New Org Name");
        }
    }

    @Nested
    @DisplayName("UpdateOrganizationStatusRequest")
    class UpdateOrganizationStatusRequestTest {

        @Test
        @DisplayName("status로 생성 성공")
        void shouldCreateWithStatus() {
            UpdateOrganizationStatusRequest request =
                    new UpdateOrganizationStatusRequest("INACTIVE");

            assertThat(request.status()).isEqualTo("INACTIVE");
        }
    }
}
