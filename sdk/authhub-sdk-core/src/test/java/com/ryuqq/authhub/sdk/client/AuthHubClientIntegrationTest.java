package com.ryuqq.authhub.sdk.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.authhub.sdk.exception.AuthHubNotFoundException;
import com.ryuqq.authhub.sdk.model.common.ApiResponse;
import com.ryuqq.authhub.sdk.model.onboarding.TenantOnboardingRequest;
import com.ryuqq.authhub.sdk.model.organization.CreateOrganizationRequest;
import com.ryuqq.authhub.sdk.model.permission.CreatePermissionRequest;
import com.ryuqq.authhub.sdk.model.role.CreateRoleRequest;
import com.ryuqq.authhub.sdk.model.tenant.CreateTenantRequest;
import com.ryuqq.authhub.sdk.model.tenant.CreateTenantResponse;
import com.ryuqq.authhub.sdk.model.tenant.TenantResponse;
import com.ryuqq.authhub.sdk.model.user.CreateUserRequest;
import java.io.IOException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("AuthHubClient Integration Test")
class AuthHubClientIntegrationTest {

    private MockWebServer mockServer;
    private AuthHubClient client;

    @BeforeEach
    void setUp() throws IOException {
        mockServer = new MockWebServer();
        mockServer.start();

        String baseUrl = mockServer.url("/").toString();
        baseUrl = baseUrl.substring(0, baseUrl.length() - 1);

        client =
                AuthHubClient.builder().baseUrl(baseUrl).serviceToken("test-service-token").build();
    }

    @AfterEach
    void tearDown() throws IOException {
        mockServer.shutdown();
    }

    @Nested
    @DisplayName("TenantApi 테스트")
    class TenantApiTest {

        @Test
        @DisplayName("테넌트를 생성한다")
        void shouldCreateTenant() throws InterruptedException {
            // given
            String responseBody =
                    """
                    {
                        "success": true,
                        "data": {
                            "tenantId": "550e8400-e29b-41d4-a716-446655440000"
                        },
                        "timestamp": "2025-01-01T00:00:00",
                        "requestId": "req-123"
                    }
                    """;
            mockServer.enqueue(
                    new MockResponse()
                            .setResponseCode(201)
                            .setHeader("Content-Type", "application/json")
                            .setBody(responseBody));

            CreateTenantRequest request = new CreateTenantRequest("Test Tenant");

            // when
            ApiResponse<CreateTenantResponse> response = client.tenants().create(request);

            // then
            assertThat(response.success()).isTrue();
            assertThat(response.data().tenantId())
                    .isEqualTo("550e8400-e29b-41d4-a716-446655440000");

            RecordedRequest recordedRequest = mockServer.takeRequest();
            assertThat(recordedRequest.getMethod()).isEqualTo("POST");
            assertThat(recordedRequest.getPath()).isEqualTo("/api/v1/tenants");
            assertThat(recordedRequest.getHeader("Authorization"))
                    .isEqualTo("Bearer test-service-token");
        }

        @Test
        @DisplayName("테넌트 ID로 테넌트를 조회한다")
        void shouldGetTenantById() throws InterruptedException {
            // given
            String responseBody =
                    """
                    {
                        "success": true,
                        "data": {
                            "tenantId": "tenant-123",
                            "name": "Test Tenant",
                            "status": "ACTIVE"
                        },
                        "timestamp": "2025-01-01T00:00:00",
                        "requestId": "req-123"
                    }
                    """;
            mockServer.enqueue(
                    new MockResponse()
                            .setResponseCode(200)
                            .setHeader("Content-Type", "application/json")
                            .setBody(responseBody));

            // when
            ApiResponse<TenantResponse> response = client.tenants().getById("tenant-123");

            // then
            assertThat(response.success()).isTrue();
            assertThat(response.data().tenantId()).isEqualTo("tenant-123");
            assertThat(response.data().name()).isEqualTo("Test Tenant");

            RecordedRequest recordedRequest = mockServer.takeRequest();
            assertThat(recordedRequest.getPath()).isEqualTo("/api/v1/tenants/tenant-123");
        }

        @Test
        @DisplayName("존재하지 않는 테넌트 조회 시 예외를 발생시킨다")
        void shouldThrowExceptionWhenTenantNotFound() {
            // given
            mockServer.enqueue(
                    new MockResponse()
                            .setResponseCode(404)
                            .setHeader("Content-Type", "application/json")
                            .setBody(
                                    "{\"errorCode\": \"TENANT_NOT_FOUND\", \"message\": \"Tenant"
                                            + " not found\"}"));

            // when & then
            assertThatThrownBy(() -> client.tenants().getById("non-existent"))
                    .isInstanceOf(AuthHubNotFoundException.class)
                    .hasMessageContaining("Tenant not found");
        }

        @Test
        @DisplayName("테넌트를 삭제한다")
        void shouldDeleteTenant() throws InterruptedException {
            // given
            mockServer.enqueue(new MockResponse().setResponseCode(204));

            // when
            client.tenants().delete("tenant-123");

            // then
            RecordedRequest recordedRequest = mockServer.takeRequest();
            assertThat(recordedRequest.getMethod()).isEqualTo("DELETE");
            assertThat(recordedRequest.getPath()).isEqualTo("/api/v1/tenants/tenant-123/delete");
        }
    }

    @Nested
    @DisplayName("OrganizationApi 테스트")
    class OrganizationApiTest {

        @Test
        @DisplayName("조직을 생성한다")
        void shouldCreateOrganization() throws InterruptedException {
            // given
            String responseBody =
                    """
                    {
                        "success": true,
                        "data": {
                            "organizationId": "org-12345"
                        },
                        "timestamp": "2025-01-01T00:00:00",
                        "requestId": "req-123"
                    }
                    """;
            mockServer.enqueue(
                    new MockResponse()
                            .setResponseCode(201)
                            .setHeader("Content-Type", "application/json")
                            .setBody(responseBody));

            // when
            CreateOrganizationRequest request =
                    new CreateOrganizationRequest("tenant-1", "Test Org");
            var response = client.organizations().create(request);

            // then
            assertThat(response.success()).isTrue();
            assertThat(response.data().organizationId()).isEqualTo("org-12345");

            RecordedRequest recordedRequest = mockServer.takeRequest();
            assertThat(recordedRequest.getMethod()).isEqualTo("POST");
            assertThat(recordedRequest.getPath()).isEqualTo("/api/v1/organizations");
        }
    }

    @Nested
    @DisplayName("UserApi 테스트")
    class UserApiTest {

        @Test
        @DisplayName("사용자를 생성한다")
        void shouldCreateUser() throws InterruptedException {
            // given
            String responseBody =
                    """
                    {
                        "success": true,
                        "data": {
                            "userId": 12345
                        },
                        "timestamp": "2025-01-01T00:00:00",
                        "requestId": "req-123"
                    }
                    """;
            mockServer.enqueue(
                    new MockResponse()
                            .setResponseCode(201)
                            .setHeader("Content-Type", "application/json")
                            .setBody(responseBody));

            // when
            CreateUserRequest request =
                    new CreateUserRequest(
                            "tenant-1", "org-1", "testuser", "010-1234-5678", "password123");
            var response = client.users().create(request);

            // then
            assertThat(response.success()).isTrue();
            assertThat(response.data().userId()).isEqualTo(12345L);

            RecordedRequest recordedRequest = mockServer.takeRequest();
            assertThat(recordedRequest.getMethod()).isEqualTo("POST");
            assertThat(recordedRequest.getPath()).isEqualTo("/api/v1/users");
        }
    }

    @Nested
    @DisplayName("OnboardingApi 테스트")
    class OnboardingApiTest {

        @Test
        @DisplayName("테넌트 온보딩을 수행한다")
        void shouldOnboardTenant() throws InterruptedException {
            // given
            String responseBody =
                    """
                    {
                        "success": true,
                        "data": {
                            "tenantId": "tenant-123",
                            "organizationId": "org-123",
                            "userId": "user-100",
                            "temporaryPassword": "temp-pass-123"
                        },
                        "timestamp": "2025-01-01T00:00:00",
                        "requestId": "req-123"
                    }
                    """;
            mockServer.enqueue(
                    new MockResponse()
                            .setResponseCode(201)
                            .setHeader("Content-Type", "application/json")
                            .setBody(responseBody));

            // when
            TenantOnboardingRequest request =
                    new TenantOnboardingRequest(
                            "New Tenant", "New Org", "admin@example.com", "010-0000-0000");
            var response = client.onboarding().onboard(request);

            // then
            assertThat(response.success()).isTrue();
            assertThat(response.data().tenantId()).isEqualTo("tenant-123");
            assertThat(response.data().organizationId()).isEqualTo("org-123");
            assertThat(response.data().userId()).isEqualTo("user-100");
            assertThat(response.data().temporaryPassword()).isEqualTo("temp-pass-123");

            RecordedRequest recordedRequest = mockServer.takeRequest();
            assertThat(recordedRequest.getMethod()).isEqualTo("POST");
            assertThat(recordedRequest.getPath()).isEqualTo("/api/v1/system/onboarding");
        }
    }

    @Nested
    @DisplayName("RoleApi 테스트")
    class RoleApiTest {

        @Test
        @DisplayName("역할을 생성한다")
        void shouldCreateRole() throws InterruptedException {
            // given
            String responseBody =
                    """
                    {
                        "success": true,
                        "data": {
                            "roleId": 1
                        },
                        "timestamp": "2025-01-01T00:00:00",
                        "requestId": "req-123"
                    }
                    """;
            mockServer.enqueue(
                    new MockResponse()
                            .setResponseCode(201)
                            .setHeader("Content-Type", "application/json")
                            .setBody(responseBody));

            // when
            CreateRoleRequest request =
                    new CreateRoleRequest("org-1", "Admin", "Administrator role");
            var response = client.roles().create(request);

            // then
            assertThat(response.success()).isTrue();
            assertThat(response.data().roleId()).isEqualTo(1);

            RecordedRequest recordedRequest = mockServer.takeRequest();
            assertThat(recordedRequest.getMethod()).isEqualTo("POST");
            assertThat(recordedRequest.getPath()).isEqualTo("/api/v1/roles");
        }
    }

    @Nested
    @DisplayName("PermissionApi 테스트")
    class PermissionApiTest {

        @Test
        @DisplayName("권한을 생성한다")
        void shouldCreatePermission() throws InterruptedException {
            // given
            String responseBody =
                    """
                    {
                        "success": true,
                        "data": {
                            "permissionId": 1
                        },
                        "timestamp": "2025-01-01T00:00:00",
                        "requestId": "req-123"
                    }
                    """;
            mockServer.enqueue(
                    new MockResponse()
                            .setResponseCode(201)
                            .setHeader("Content-Type", "application/json")
                            .setBody(responseBody));

            // when
            CreatePermissionRequest request =
                    new CreatePermissionRequest("USER", "READ", "Read user data", false);
            var response = client.permissions().create(request);

            // then
            assertThat(response.success()).isTrue();
            assertThat(response.data().permissionId()).isEqualTo(1);

            RecordedRequest recordedRequest = mockServer.takeRequest();
            assertThat(recordedRequest.getMethod()).isEqualTo("POST");
            assertThat(recordedRequest.getPath()).isEqualTo("/api/v1/permissions");
        }
    }
}
