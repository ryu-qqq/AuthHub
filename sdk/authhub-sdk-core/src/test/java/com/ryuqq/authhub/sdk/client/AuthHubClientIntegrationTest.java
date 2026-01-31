package com.ryuqq.authhub.sdk.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.authhub.sdk.exception.AuthHubUnauthorizedException;
import com.ryuqq.authhub.sdk.model.auth.LoginRequest;
import com.ryuqq.authhub.sdk.model.auth.LoginResponse;
import com.ryuqq.authhub.sdk.model.auth.LogoutRequest;
import com.ryuqq.authhub.sdk.model.auth.MyContextResponse;
import com.ryuqq.authhub.sdk.model.auth.RefreshTokenRequest;
import com.ryuqq.authhub.sdk.model.auth.TokenResponse;
import com.ryuqq.authhub.sdk.model.common.ApiResponse;
import com.ryuqq.authhub.sdk.model.onboarding.TenantOnboardingRequest;
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
    @DisplayName("AuthApi 테스트")
    class AuthApiTest {

        @Test
        @DisplayName("로그인을 수행한다")
        void shouldLogin() throws InterruptedException {
            // given
            String responseBody =
                    """
                    {
                        "success": true,
                        "data": {
                            "userId": "user-123",
                            "accessToken": "access-token-abc",
                            "refreshToken": "refresh-token-xyz",
                            "expiresIn": 3600,
                            "tokenType": "Bearer"
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

            LoginRequest request = new LoginRequest("user@example.com", "password123");

            // when
            ApiResponse<LoginResponse> response = client.auth().login(request);

            // then
            assertThat(response.success()).isTrue();
            assertThat(response.data().userId()).isEqualTo("user-123");
            assertThat(response.data().accessToken()).isEqualTo("access-token-abc");
            assertThat(response.data().refreshToken()).isEqualTo("refresh-token-xyz");
            assertThat(response.data().expiresIn()).isEqualTo(3600L);
            assertThat(response.data().tokenType()).isEqualTo("Bearer");

            RecordedRequest recordedRequest = mockServer.takeRequest();
            assertThat(recordedRequest.getMethod()).isEqualTo("POST");
            assertThat(recordedRequest.getPath()).isEqualTo("/api/v1/auth/login");
            // 로그인은 인증 없이 호출 (Public API)
            assertThat(recordedRequest.getHeader("Authorization")).isNull();
        }

        @Test
        @DisplayName("토큰을 갱신한다")
        void shouldRefreshToken() throws InterruptedException {
            // given
            String responseBody =
                    """
                    {
                        "success": true,
                        "data": {
                            "accessToken": "new-access-token",
                            "refreshToken": "new-refresh-token",
                            "accessTokenExpiresIn": 3600,
                            "refreshTokenExpiresIn": 86400,
                            "tokenType": "Bearer"
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

            RefreshTokenRequest request = new RefreshTokenRequest("old-refresh-token");

            // when
            ApiResponse<TokenResponse> response = client.auth().refresh(request);

            // then
            assertThat(response.success()).isTrue();
            assertThat(response.data().accessToken()).isEqualTo("new-access-token");
            assertThat(response.data().refreshToken()).isEqualTo("new-refresh-token");

            RecordedRequest recordedRequest = mockServer.takeRequest();
            assertThat(recordedRequest.getMethod()).isEqualTo("POST");
            assertThat(recordedRequest.getPath()).isEqualTo("/api/v1/auth/refresh");
            // 토큰 갱신도 인증 없이 호출 (Public API)
            assertThat(recordedRequest.getHeader("Authorization")).isNull();
        }

        @Test
        @DisplayName("로그아웃을 수행한다")
        void shouldLogout() throws InterruptedException {
            // given
            String responseBody =
                    """
                    {
                        "success": true,
                        "data": null,
                        "timestamp": "2025-01-01T00:00:00",
                        "requestId": "req-123"
                    }
                    """;
            mockServer.enqueue(
                    new MockResponse()
                            .setResponseCode(200)
                            .setHeader("Content-Type", "application/json")
                            .setBody(responseBody));

            LogoutRequest request = new LogoutRequest("user-123");

            // when
            client.auth().logout(request);

            // then
            RecordedRequest recordedRequest = mockServer.takeRequest();
            assertThat(recordedRequest.getMethod()).isEqualTo("POST");
            assertThat(recordedRequest.getPath()).isEqualTo("/api/v1/auth/logout");
            // 로그아웃은 인증 필요
            assertThat(recordedRequest.getHeader("Authorization"))
                    .isEqualTo("Bearer test-service-token");
        }

        @Test
        @DisplayName("내 정보를 조회한다")
        void shouldGetMyContext() throws InterruptedException {
            // given
            String responseBody =
                    """
                    {
                        "success": true,
                        "data": {
                            "userId": "user-123",
                            "email": "user@example.com",
                            "name": "Test User",
                            "tenant": {
                                "id": "tenant-1",
                                "name": "Test Tenant"
                            },
                            "organization": {
                                "id": "org-1",
                                "name": "Test Org"
                            },
                            "roles": [
                                {"id": "1", "name": "Admin"}
                            ],
                            "permissions": ["user:read", "user:write"]
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
            ApiResponse<MyContextResponse> response = client.auth().getMe();

            // then
            assertThat(response.success()).isTrue();
            assertThat(response.data().userId()).isEqualTo("user-123");
            assertThat(response.data().email()).isEqualTo("user@example.com");
            assertThat(response.data().tenant().id()).isEqualTo("tenant-1");
            assertThat(response.data().organization().id()).isEqualTo("org-1");
            assertThat(response.data().roles()).hasSize(1);
            assertThat(response.data().permissions()).containsExactly("user:read", "user:write");

            RecordedRequest recordedRequest = mockServer.takeRequest();
            assertThat(recordedRequest.getMethod()).isEqualTo("GET");
            assertThat(recordedRequest.getPath()).isEqualTo("/api/v1/auth/me");
            // 내 정보 조회는 인증 필요
            assertThat(recordedRequest.getHeader("Authorization"))
                    .isEqualTo("Bearer test-service-token");
        }

        @Test
        @DisplayName("잘못된 자격 증명으로 로그인 시 예외를 발생시킨다")
        void shouldThrowExceptionWhenLoginFails() {
            // given
            mockServer.enqueue(
                    new MockResponse()
                            .setResponseCode(401)
                            .setHeader("Content-Type", "application/json")
                            .setBody(
                                    "{\"errorCode\": \"INVALID_CREDENTIALS\", \"message\":"
                                            + " \"Invalid email or password\"}"));

            LoginRequest request = new LoginRequest("user@example.com", "wrong-password");

            // when & then
            assertThatThrownBy(() -> client.auth().login(request))
                    .isInstanceOf(AuthHubUnauthorizedException.class)
                    .hasMessageContaining("Invalid email or password");
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
                            "organizationId": "org-123"
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
            TenantOnboardingRequest request = new TenantOnboardingRequest("New Tenant", "New Org");
            String idempotencyKey = "test-idempotency-key-12345";
            var response = client.onboarding().onboard(request, idempotencyKey);

            // then
            assertThat(response.success()).isTrue();
            assertThat(response.data().tenantId()).isEqualTo("tenant-123");
            assertThat(response.data().organizationId()).isEqualTo("org-123");

            RecordedRequest recordedRequest = mockServer.takeRequest();
            assertThat(recordedRequest.getMethod()).isEqualTo("POST");
            assertThat(recordedRequest.getPath()).isEqualTo("/api/v1/internal/onboarding");
            assertThat(recordedRequest.getHeader("X-Idempotency-Key")).isEqualTo(idempotencyKey);
        }
    }
}
