package com.ryuqq.authhub.integration.e2e.token;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import com.ryuqq.authhub.adapter.out.persistence.organization.entity.OrganizationJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.organization.fixture.OrganizationJpaEntityFixture;
import com.ryuqq.authhub.adapter.out.persistence.organization.repository.OrganizationJpaRepository;
import com.ryuqq.authhub.adapter.out.persistence.tenant.entity.TenantJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.tenant.fixture.TenantJpaEntityFixture;
import com.ryuqq.authhub.adapter.out.persistence.tenant.repository.TenantJpaRepository;
import com.ryuqq.authhub.adapter.out.persistence.user.entity.UserJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.user.fixture.UserJpaEntityFixture;
import com.ryuqq.authhub.adapter.out.persistence.user.repository.UserJpaRepository;
import com.ryuqq.authhub.domain.user.vo.UserStatus;
import com.ryuqq.authhub.integration.common.base.E2ETestBase;
import com.ryuqq.authhub.integration.common.tag.TestTags;
import io.restassured.response.Response;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Token API E2E 테스트.
 *
 * <p>로그인, 토큰 갱신, 로그아웃 등 인증 API의 전체 흐름을 검증합니다.
 */
@Tag(TestTags.E2E)
@Tag(TestTags.AUTH)
class TokenE2ETest extends E2ETestBase {

    private static final String LOGIN_PATH = "/api/v1/auth/login";
    private static final String REFRESH_PATH = "/api/v1/auth/refresh";
    private static final String LOGOUT_PATH = "/api/v1/auth/logout";
    private static final String JWKS_PATH = "/api/v1/auth/jwks";

    @Autowired private UserJpaRepository userJpaRepository;

    @Autowired private OrganizationJpaRepository organizationJpaRepository;

    @Autowired private TenantJpaRepository tenantJpaRepository;

    @Autowired private PasswordEncoder passwordEncoder;

    private TenantJpaEntity savedTenant;
    private OrganizationJpaEntity savedOrganization;
    private String testIdentifier;
    private String testPassword;

    @BeforeEach
    void setUp() {
        userJpaRepository.deleteAll();
        organizationJpaRepository.deleteAll();
        tenantJpaRepository.deleteAll();

        savedTenant = tenantJpaRepository.save(TenantJpaEntityFixture.create());
        savedOrganization =
                organizationJpaRepository.save(
                        OrganizationJpaEntityFixture.createWithTenant(savedTenant.getTenantId()));

        testIdentifier = "logintest@example.com";
        testPassword = "password123!";

        String userId = UUID.randomUUID().toString();
        UserJpaEntity user =
                UserJpaEntity.of(
                        userId,
                        savedOrganization.getOrganizationId(),
                        testIdentifier,
                        "010-1234-5678",
                        passwordEncoder.encode(testPassword),
                        UserStatus.ACTIVE,
                        UserJpaEntityFixture.fixedTime(),
                        UserJpaEntityFixture.fixedTime(),
                        null);
        userJpaRepository.save(user);
    }

    @Nested
    @DisplayName("POST /api/v1/auth/login - 로그인")
    class LoginTest {

        @Test
        @DisplayName("유효한 식별자와 비밀번호로 로그인할 수 있다")
        void shouldLoginWithValidCredentials() {
            // given
            Map<String, Object> request =
                    Map.of(
                            "identifier", testIdentifier,
                            "password", testPassword);

            // when
            Response response = givenJson().body(request).when().post(LOGIN_PATH);

            // then
            response.then()
                    .statusCode(HttpStatus.CREATED.value())
                    .body("success", equalTo(true))
                    .body("data.userId", notNullValue())
                    .body("data.accessToken", notNullValue())
                    .body("data.refreshToken", notNullValue());
        }

        @Test
        @DisplayName("식별자가 없으면 400 에러를 반환한다")
        void shouldReturn400WhenIdentifierMissing() {
            // given
            Map<String, Object> request = Map.of("password", testPassword);

            // when & then
            givenJson()
                    .body(request)
                    .when()
                    .post(LOGIN_PATH)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @DisplayName("비밀번호가 틀리면 401 에러를 반환한다")
        void shouldReturn401WhenPasswordWrong() {
            // given
            Map<String, Object> request =
                    Map.of("identifier", testIdentifier, "password", "wrongpassword");

            // when & then
            givenJson()
                    .body(request)
                    .when()
                    .post(LOGIN_PATH)
                    .then()
                    .statusCode(HttpStatus.UNAUTHORIZED.value());
        }

        @Test
        @DisplayName("존재하지 않는 식별자로 로그인하면 401 에러를 반환한다")
        void shouldReturn401WhenUserNotFound() {
            // given
            Map<String, Object> request =
                    Map.of(
                            "identifier", "nonexistent@example.com",
                            "password", "password123!");

            // when & then
            givenJson()
                    .body(request)
                    .when()
                    .post(LOGIN_PATH)
                    .then()
                    .statusCode(HttpStatus.UNAUTHORIZED.value());
        }
    }

    @Nested
    @DisplayName("POST /api/v1/auth/refresh - 토큰 갱신")
    class RefreshTokenTest {

        @Test
        @DisplayName("유효한 Refresh Token으로 새 토큰을 발급받을 수 있다")
        void shouldRefreshTokenWithValidRefreshToken() {
            // given - 로그인하여 refresh token 획득
            Map<String, Object> loginRequest =
                    Map.of(
                            "identifier", testIdentifier,
                            "password", testPassword);
            Response loginResponse = givenJson().body(loginRequest).when().post(LOGIN_PATH);
            String refreshToken = loginResponse.jsonPath().getString("data.refreshToken");

            Map<String, Object> refreshRequest = Map.of("refreshToken", refreshToken);

            // when
            Response response = givenJson().body(refreshRequest).when().post(REFRESH_PATH);

            // then
            response.then()
                    .statusCode(HttpStatus.OK.value())
                    .body("success", equalTo(true))
                    .body("data.accessToken", notNullValue())
                    .body("data.refreshToken", notNullValue());
        }

        @Test
        @DisplayName("refreshToken이 없으면 400 에러를 반환한다")
        void shouldReturn400WhenRefreshTokenMissing() {
            // given
            Map<String, Object> request = Map.of();

            // when & then
            givenJson()
                    .body(request)
                    .when()
                    .post(REFRESH_PATH)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }
    }

    @Nested
    @DisplayName("POST /api/v1/auth/logout - 로그아웃")
    class LogoutTest {

        @Test
        @DisplayName("사용자 ID로 로그아웃할 수 있다")
        void shouldLogoutWithUserId() {
            // given - 로그인하여 userId 획득
            Map<String, Object> loginRequest =
                    Map.of(
                            "identifier", testIdentifier,
                            "password", testPassword);
            Response loginResponse = givenJson().body(loginRequest).when().post(LOGIN_PATH);
            String userId = loginResponse.jsonPath().getString("data.userId");

            Map<String, Object> logoutRequest = Map.of("userId", userId);

            // when
            Response response = givenJson().body(logoutRequest).when().post(LOGOUT_PATH);

            // then
            response.then().statusCode(HttpStatus.OK.value()).body("success", equalTo(true));
        }

        @Test
        @DisplayName("userId가 없으면 400 에러를 반환한다")
        void shouldReturn400WhenUserIdMissing() {
            // given
            Map<String, Object> request = Map.of();

            // when & then
            givenJson()
                    .body(request)
                    .when()
                    .post(LOGOUT_PATH)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/auth/jwks - 공개키 목록 조회")
    class GetPublicKeysTest {

        @Test
        @DisplayName("공개키 목록을 조회할 수 있다")
        void shouldGetPublicKeys() {
            // when
            Response response = givenJson().when().get(JWKS_PATH);

            // then
            response.then().statusCode(HttpStatus.OK.value());

            Object keys = response.jsonPath().get("keys");
            assertThat(keys).isNotNull();
        }
    }
}
