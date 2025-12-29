package com.ryuqq.authhub.integration.auth;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.adapter.in.rest.auth.dto.response.LoginApiResponse;
import com.ryuqq.authhub.adapter.in.rest.auth.dto.response.TokenApiResponse;
import com.ryuqq.authhub.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.authhub.adapter.in.rest.organization.dto.response.CreateOrganizationApiResponse;
import com.ryuqq.authhub.adapter.in.rest.tenant.dto.response.CreateTenantApiResponse;
import com.ryuqq.authhub.adapter.in.rest.user.dto.response.CreateUserApiResponse;
import com.ryuqq.authhub.integration.auth.fixture.AuthIntegrationTestFixture;
import com.ryuqq.authhub.integration.config.BaseIntegrationTest;
import com.ryuqq.authhub.integration.organization.fixture.OrganizationIntegrationTestFixture;
import com.ryuqq.authhub.integration.tenant.fixture.TenantIntegrationTestFixture;
import com.ryuqq.authhub.integration.user.fixture.UserIntegrationTestFixture;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * 인증 플로우 통합 테스트
 *
 * <p>인증 관련 API의 전체 레이어 통합 테스트를 수행합니다.
 *
 * <p><strong>테스트 범위:</strong>
 *
 * <ul>
 *   <li>로그인 → 토큰 발급
 *   <li>토큰 갱신
 *   <li>로그아웃
 *   <li>인증 실패 시나리오
 * </ul>
 *
 * @author Development Team
 * @since 1.0.0
 */
@DisplayName("인증 플로우 통합 테스트")
class AuthFlowIntegrationTest extends BaseIntegrationTest {

    private String tenantId;
    private String organizationId;
    private String testIdentifier;
    private static final String TEST_PASSWORD = "Password123!";

    private String tenantsUrl() {
        return apiV1() + "/auth/tenants";
    }

    private String organizationsUrl() {
        return apiV1() + "/auth/organizations";
    }

    private String usersUrl() {
        return apiV1() + "/auth/users";
    }

    private String authUrl() {
        return apiV1() + "/auth";
    }

    @BeforeEach
    void setUp() {
        // 테스트용 테넌트 생성
        var tenantRequest = TenantIntegrationTestFixture.createTenantRequestWithUniqueName();
        ResponseEntity<ApiResponse<CreateTenantApiResponse>> tenantResponse =
                postForApiResponse(
                        tenantsUrl(), tenantRequest, new ParameterizedTypeReference<>() {});
        this.tenantId = tenantResponse.getBody().data().tenantId();

        // 테스트용 조직 생성
        var orgRequest =
                OrganizationIntegrationTestFixture.createOrganizationRequestWithUniqueName(
                        tenantId);
        ResponseEntity<ApiResponse<CreateOrganizationApiResponse>> orgResponse =
                postForApiResponse(
                        organizationsUrl(), orgRequest, new ParameterizedTypeReference<>() {});
        this.organizationId = orgResponse.getBody().data().organizationId();

        // 테스트용 사용자 생성
        this.testIdentifier = "authtest" + System.currentTimeMillis() + "@example.com";
        String testPhoneNumber = "010-" + System.currentTimeMillis() % 10000000;
        var userRequest =
                UserIntegrationTestFixture.createUserRequest(
                        tenantId, organizationId, testIdentifier, testPhoneNumber, TEST_PASSWORD);
        postForApiResponse(
                usersUrl(),
                userRequest,
                new ParameterizedTypeReference<ApiResponse<CreateUserApiResponse>>() {});
    }

    @Nested
    @DisplayName("로그인")
    class Login {

        @Test
        @DisplayName("TC-001: 로그인 - 성공")
        void login_success() {
            // given
            var request = AuthIntegrationTestFixture.loginRequest(testIdentifier, TEST_PASSWORD);

            // when
            ResponseEntity<ApiResponse<LoginApiResponse>> response =
                    postForApiResponse(
                            authUrl() + "/login", request, new ParameterizedTypeReference<>() {});

            // then - Controller가 201 CREATED를 반환 (토큰/세션 생성)
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().data().accessToken()).isNotBlank();
            assertThat(response.getBody().data().refreshToken()).isNotBlank();
            assertThat(response.getBody().data().tokenType()).isEqualTo("Bearer");
            assertThat(response.getBody().data().expiresIn()).isPositive();
        }

        @Test
        @DisplayName("TC-002: 로그인 - 잘못된 비밀번호로 실패")
        void login_wrongPassword_returns401() {
            // given
            var request = AuthIntegrationTestFixture.loginRequest(testIdentifier, "wrongpassword");

            // when
            ResponseEntity<Map<String, Object>> response =
                    restTemplate.exchange(
                            authUrl() + "/login",
                            HttpMethod.POST,
                            createHttpEntity(request),
                            new ParameterizedTypeReference<>() {});

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        }

        @Test
        @DisplayName("TC-003: 로그인 - 존재하지 않는 사용자로 실패")
        void login_nonExistentUser_returns401() {
            // given
            var request = AuthIntegrationTestFixture.loginRequestWithInvalidCredentials();

            // when
            ResponseEntity<Map<String, Object>> response =
                    restTemplate.exchange(
                            authUrl() + "/login",
                            HttpMethod.POST,
                            createHttpEntity(request),
                            new ParameterizedTypeReference<>() {});

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        }

        @Test
        @DisplayName("TC-004: 로그인 - 빈 식별자로 실패")
        void login_emptyIdentifier_returns400() {
            // given
            var request = AuthIntegrationTestFixture.loginRequestWithEmptyIdentifier();

            // when
            ResponseEntity<Map<String, Object>> response =
                    restTemplate.exchange(
                            authUrl() + "/login",
                            HttpMethod.POST,
                            createHttpEntity(request),
                            new ParameterizedTypeReference<>() {});

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        @Test
        @DisplayName("TC-005: 로그인 - 빈 비밀번호로 실패")
        void login_emptyPassword_returns400() {
            // given
            var request = AuthIntegrationTestFixture.loginRequestWithEmptyPassword();

            // when
            ResponseEntity<Map<String, Object>> response =
                    restTemplate.exchange(
                            authUrl() + "/login",
                            HttpMethod.POST,
                            createHttpEntity(request),
                            new ParameterizedTypeReference<>() {});

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
    }

    @Nested
    @DisplayName("토큰 갱신")
    class RefreshToken {

        @Test
        @DisplayName("TC-006: 토큰 갱신 - 성공")
        void refreshToken_success() {
            // given - 먼저 로그인
            var loginRequest =
                    AuthIntegrationTestFixture.loginRequest(testIdentifier, TEST_PASSWORD);
            ResponseEntity<ApiResponse<LoginApiResponse>> loginResponse =
                    postForApiResponse(
                            authUrl() + "/login",
                            loginRequest,
                            new ParameterizedTypeReference<>() {});
            String refreshToken = loginResponse.getBody().data().refreshToken();

            var refreshRequest = AuthIntegrationTestFixture.refreshTokenRequest(refreshToken);

            // when - 토큰 갱신
            ResponseEntity<ApiResponse<TokenApiResponse>> response =
                    postForApiResponse(
                            authUrl() + "/refresh",
                            refreshRequest,
                            new ParameterizedTypeReference<>() {});

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().data().accessToken()).isNotBlank();
            assertThat(response.getBody().data().refreshToken()).isNotBlank();
            assertThat(response.getBody().data().tokenType()).isEqualTo("Bearer");
        }

        @Test
        @DisplayName("TC-007: 토큰 갱신 - 잘못된 리프레시 토큰으로 실패")
        void refreshToken_invalidToken_returns401() {
            // given
            var request = AuthIntegrationTestFixture.refreshTokenRequest("invalid-refresh-token");

            // when
            ResponseEntity<Map<String, Object>> response =
                    restTemplate.exchange(
                            authUrl() + "/refresh",
                            HttpMethod.POST,
                            createHttpEntity(request),
                            new ParameterizedTypeReference<>() {});

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        }
    }

    @Nested
    @DisplayName("로그아웃")
    class Logout {

        @Test
        @DisplayName("TC-008: 로그아웃 - 성공")
        void logout_success() {
            // given - 먼저 로그인
            var loginRequest =
                    AuthIntegrationTestFixture.loginRequest(testIdentifier, TEST_PASSWORD);
            ResponseEntity<ApiResponse<LoginApiResponse>> loginResponse =
                    postForApiResponse(
                            authUrl() + "/login",
                            loginRequest,
                            new ParameterizedTypeReference<>() {});
            String accessToken = loginResponse.getBody().data().accessToken();

            var logoutRequest =
                    AuthIntegrationTestFixture.logoutRequest(
                            loginResponse.getBody().data().userId());

            // when - 로그아웃 (인증 헤더 포함)
            ResponseEntity<Void> response =
                    restTemplate.exchange(
                            authUrl() + "/logout",
                            HttpMethod.POST,
                            createHttpEntityWithAuth(logoutRequest, accessToken),
                            Void.class);

            // then - Controller가 200 OK 반환
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        }

        @Test
        @DisplayName("TC-009: 로그아웃 후 리프레시 토큰 사용 불가")
        void logout_thenRefreshTokenInvalid() {
            // given - 로그인 후 로그아웃
            var loginRequest =
                    AuthIntegrationTestFixture.loginRequest(testIdentifier, TEST_PASSWORD);
            ResponseEntity<ApiResponse<LoginApiResponse>> loginResponse =
                    postForApiResponse(
                            authUrl() + "/login",
                            loginRequest,
                            new ParameterizedTypeReference<>() {});
            String accessToken = loginResponse.getBody().data().accessToken();
            String refreshToken = loginResponse.getBody().data().refreshToken();

            var logoutRequest =
                    AuthIntegrationTestFixture.logoutRequest(
                            loginResponse.getBody().data().userId());
            restTemplate.exchange(
                    authUrl() + "/logout",
                    HttpMethod.POST,
                    createHttpEntityWithAuth(logoutRequest, accessToken),
                    Void.class);

            // when - 로그아웃된 리프레시 토큰으로 갱신 시도
            var refreshRequest = AuthIntegrationTestFixture.refreshTokenRequest(refreshToken);
            ResponseEntity<Map<String, Object>> response =
                    restTemplate.exchange(
                            authUrl() + "/refresh",
                            HttpMethod.POST,
                            createHttpEntity(refreshRequest),
                            new ParameterizedTypeReference<>() {});

            // then - 토큰 갱신 실패
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        }
    }

    @Nested
    @DisplayName("전체 인증 플로우")
    class FullAuthFlow {

        @Test
        @DisplayName("TC-010: 전체 인증 플로우 - 로그인 → 토큰 갱신 → 로그아웃")
        void fullAuthFlow_success() {
            // 1. 로그인 (201 CREATED - 토큰/세션 생성)
            var loginRequest =
                    AuthIntegrationTestFixture.loginRequest(testIdentifier, TEST_PASSWORD);
            ResponseEntity<ApiResponse<LoginApiResponse>> loginResponse =
                    postForApiResponse(
                            authUrl() + "/login",
                            loginRequest,
                            new ParameterizedTypeReference<>() {});
            assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            String accessToken = loginResponse.getBody().data().accessToken();
            String refreshToken = loginResponse.getBody().data().refreshToken();

            // 2. 토큰 갱신
            var refreshRequest = AuthIntegrationTestFixture.refreshTokenRequest(refreshToken);
            ResponseEntity<ApiResponse<TokenApiResponse>> refreshResponse =
                    postForApiResponse(
                            authUrl() + "/refresh",
                            refreshRequest,
                            new ParameterizedTypeReference<>() {});
            assertThat(refreshResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
            String newAccessToken = refreshResponse.getBody().data().accessToken();
            String newRefreshToken = refreshResponse.getBody().data().refreshToken();

            // 3. 로그아웃 (200 OK - Controller 반환값)
            var logoutRequest =
                    AuthIntegrationTestFixture.logoutRequest(
                            loginResponse.getBody().data().userId());
            ResponseEntity<Void> logoutResponse =
                    restTemplate.exchange(
                            authUrl() + "/logout",
                            HttpMethod.POST,
                            createHttpEntityWithAuth(logoutRequest, newAccessToken),
                            Void.class);
            assertThat(logoutResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

            // 4. 로그아웃 후 토큰 갱신 실패 확인
            var invalidRefreshRequest =
                    AuthIntegrationTestFixture.refreshTokenRequest(newRefreshToken);
            ResponseEntity<Map<String, Object>> invalidRefreshResponse =
                    restTemplate.exchange(
                            authUrl() + "/refresh",
                            HttpMethod.POST,
                            createHttpEntity(invalidRefreshRequest),
                            new ParameterizedTypeReference<>() {});
            assertThat(invalidRefreshResponse.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        }
    }
}
