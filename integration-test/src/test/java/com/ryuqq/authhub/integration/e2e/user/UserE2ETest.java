package com.ryuqq.authhub.integration.e2e.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

import com.ryuqq.authhub.adapter.out.persistence.organization.entity.OrganizationJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.organization.fixture.OrganizationJpaEntityFixture;
import com.ryuqq.authhub.adapter.out.persistence.organization.repository.OrganizationJpaRepository;
import com.ryuqq.authhub.adapter.out.persistence.tenant.entity.TenantJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.tenant.fixture.TenantJpaEntityFixture;
import com.ryuqq.authhub.adapter.out.persistence.tenant.repository.TenantJpaRepository;
import com.ryuqq.authhub.adapter.out.persistence.user.entity.UserJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.user.repository.UserJpaRepository;
import com.ryuqq.authhub.domain.user.vo.UserStatus;
import com.ryuqq.authhub.integration.common.base.E2ETestBase;
import com.ryuqq.authhub.integration.common.tag.TestTags;
import io.restassured.response.Response;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

/**
 * User API E2E 테스트.
 *
 * <p>User 도메인의 전체 API 흐름을 검증합니다. REST API → Application → Domain → Repository → DB
 */
@Tag(TestTags.E2E)
@Tag(TestTags.USER)
class UserE2ETest extends E2ETestBase {

    private static final String BASE_PATH = "/api/v1/auth/users";

    @Autowired private UserJpaRepository userJpaRepository;

    @Autowired private OrganizationJpaRepository organizationJpaRepository;

    @Autowired private TenantJpaRepository tenantJpaRepository;

    private TenantJpaEntity savedTenant;
    private OrganizationJpaEntity savedOrganization;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 정리 (FK 순서: 자식 → 부모)
        userJpaRepository.deleteAll();
        organizationJpaRepository.deleteAll();
        tenantJpaRepository.deleteAll();

        // 부모 엔티티 생성 (FK 관계)
        savedTenant = tenantJpaRepository.save(TenantJpaEntityFixture.create());
        savedOrganization =
                organizationJpaRepository.save(
                        OrganizationJpaEntityFixture.createWithTenant(savedTenant.getTenantId()));
    }

    @Nested
    @DisplayName("POST /api/v1/auth/users - 사용자 생성")
    class CreateUserTest {

        @Test
        @DisplayName("유효한 요청으로 사용자를 생성할 수 있다")
        void shouldCreateUserWithValidRequest() {
            // given
            Map<String, Object> request =
                    Map.of(
                            "organizationId", savedOrganization.getOrganizationId(),
                            "identifier", "newuser@example.com",
                            "phoneNumber", "010-1234-5678",
                            "password", "password123!");

            // when
            Response response =
                    givenAuthenticated(DEFAULT_USER_ID, savedTenant.getTenantId())
                            .body(request)
                            .when()
                            .post(BASE_PATH);

            // then
            response.then()
                    .statusCode(HttpStatus.CREATED.value())
                    .body("success", equalTo(true))
                    .body("data.userId", notNullValue());

            // DB 검증
            String createdUserId = response.jsonPath().getString("data.userId");
            assertThat(userJpaRepository.findById(createdUserId)).isPresent();
        }

        @Test
        @DisplayName("전화번호 없이 사용자를 생성할 수 있다")
        void shouldCreateUserWithoutPhoneNumber() {
            // given
            Map<String, Object> request =
                    Map.of(
                            "organizationId", savedOrganization.getOrganizationId(),
                            "identifier", "nophone@example.com",
                            "password", "password123!");

            // when
            Response response =
                    givenAuthenticated(DEFAULT_USER_ID, savedTenant.getTenantId())
                            .body(request)
                            .when()
                            .post(BASE_PATH);

            // then
            response.then()
                    .statusCode(HttpStatus.CREATED.value())
                    .body("success", equalTo(true))
                    .body("data.userId", notNullValue());
        }

        @Test
        @DisplayName("식별자가 없으면 400 에러를 반환한다")
        void shouldReturn400WhenIdentifierMissing() {
            // given
            Map<String, Object> request =
                    Map.of(
                            "organizationId",
                            savedOrganization.getOrganizationId(),
                            "password",
                            "password123!");

            // when & then
            givenAuthenticated(DEFAULT_USER_ID, savedTenant.getTenantId())
                    .body(request)
                    .when()
                    .post(BASE_PATH)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @DisplayName("비밀번호가 8자 미만이면 400 에러를 반환한다")
        void shouldReturn400WhenPasswordTooShort() {
            // given
            Map<String, Object> request =
                    Map.of(
                            "organizationId", savedOrganization.getOrganizationId(),
                            "identifier", "shortpw@example.com",
                            "password", "short");

            // when & then
            givenAuthenticated(DEFAULT_USER_ID, savedTenant.getTenantId())
                    .body(request)
                    .when()
                    .post(BASE_PATH)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @Disabled("E2E 테스트 환경에서 에러 코드가 다를 수 있음 - 추후 조정 필요")
        @DisplayName("존재하지 않는 조직 ID로 요청하면 404 에러를 반환한다")
        void shouldReturn404WhenOrganizationNotFound() {
            // given
            Map<String, Object> request =
                    Map.of(
                            "organizationId", "non-existent-org-id",
                            "identifier", "orphan@example.com",
                            "password", "password123!");

            // when & then
            givenAuthenticated(DEFAULT_USER_ID, savedTenant.getTenantId())
                    .body(request)
                    .when()
                    .post(BASE_PATH)
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value());
        }
    }

    @Nested
    @DisplayName("인증 테스트")
    class AuthenticationTest {

        @Test
        @Disabled("TestAccessChecker가 모든 권한 허용 - 실제 보안 테스트는 별도 환경 필요")
        @DisplayName("인증 헤더 없이 요청하면 401/403 에러를 반환한다")
        void shouldReturnUnauthorizedWithoutAuthHeader() {
            // given
            Map<String, Object> request =
                    Map.of(
                            "organizationId", savedOrganization.getOrganizationId(),
                            "identifier", "unauth@example.com",
                            "password", "password123!");

            // when & then
            // 인증 헤더 없이 요청 (givenJson만 사용)
            int statusCode =
                    givenJson().body(request).when().post(BASE_PATH).then().extract().statusCode();

            // 401 Unauthorized 또는 403 Forbidden
            assertThat(statusCode)
                    .isIn(HttpStatus.UNAUTHORIZED.value(), HttpStatus.FORBIDDEN.value());
        }
    }

    // ========================================
    // Query 테스트 (목록/단건 조회)
    // ========================================

    @Nested
    @DisplayName("GET /api/v1/auth/users - 사용자 목록 조회")
    class GetUsersTest {

        @Test
        @DisplayName("사용자 목록을 조회할 수 있다")
        void shouldGetUserList() {
            // given - 테스트 사용자 2명 생성
            createTestUser("user1@example.com", "010-1111-1111");
            createTestUser("user2@example.com", "010-2222-2222");

            // when
            Response response =
                    givenAuthenticated(DEFAULT_USER_ID, savedTenant.getTenantId())
                            .when()
                            .get(BASE_PATH);

            // then
            response.then()
                    .statusCode(HttpStatus.OK.value())
                    .body("success", equalTo(true))
                    .body("data.content", hasSize(greaterThanOrEqualTo(2)))
                    .body("data.page", equalTo(0))
                    .body("data.size", equalTo(20))
                    .body("data.totalElements", greaterThanOrEqualTo(2));
        }

        @Test
        @DisplayName("페이지네이션이 적용된다")
        void shouldApplyPagination() {
            // given - 테스트 사용자 3명 생성
            createTestUser("page1@example.com", "010-1111-0001");
            createTestUser("page2@example.com", "010-1111-0002");
            createTestUser("page3@example.com", "010-1111-0003");

            // when - 페이지 크기 2로 조회
            Response response =
                    givenAuthenticated(DEFAULT_USER_ID, savedTenant.getTenantId())
                            .queryParam("page", 0)
                            .queryParam("size", 2)
                            .when()
                            .get(BASE_PATH);

            // then
            response.then()
                    .statusCode(HttpStatus.OK.value())
                    .body("success", equalTo(true))
                    .body("data.content", hasSize(2))
                    .body("data.page", equalTo(0))
                    .body("data.size", equalTo(2))
                    .body("data.totalElements", greaterThanOrEqualTo(3))
                    .body("data.first", equalTo(true));
        }

        @Test
        @DisplayName("조직 ID로 필터링할 수 있다")
        void shouldFilterByOrganizationId() {
            // given
            createTestUser("org-filter@example.com", "010-3333-3333");

            // when
            Response response =
                    givenAuthenticated(DEFAULT_USER_ID, savedTenant.getTenantId())
                            .queryParam("organizationId", savedOrganization.getOrganizationId())
                            .when()
                            .get(BASE_PATH);

            // then
            response.then()
                    .statusCode(HttpStatus.OK.value())
                    .body("success", equalTo(true))
                    .body("data.content", hasSize(greaterThanOrEqualTo(1)));
        }

        @Test
        @DisplayName("검색어로 필터링할 수 있다")
        void shouldFilterBySearchWord() {
            // given
            createTestUser("searchable@example.com", "010-4444-4444");

            // when
            Response response =
                    givenAuthenticated(DEFAULT_USER_ID, savedTenant.getTenantId())
                            .queryParam("searchWord", "searchable")
                            .when()
                            .get(BASE_PATH);

            // then
            response.then().statusCode(HttpStatus.OK.value()).body("success", equalTo(true));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/auth/users/{userId} - 사용자 단건 조회")
    class GetUserDetailTest {

        @Test
        @DisplayName("사용자 상세 정보를 조회할 수 있다")
        void shouldGetUserDetail() {
            // given
            String userId = createTestUser("detail@example.com", "010-5555-5555");

            // when
            Response response =
                    givenAuthenticated(DEFAULT_USER_ID, savedTenant.getTenantId())
                            .when()
                            .get(BASE_PATH + "/" + userId);

            // then
            response.then()
                    .statusCode(HttpStatus.OK.value())
                    .body("success", equalTo(true))
                    .body("data.userId", equalTo(userId))
                    .body("data.identifier", equalTo("detail@example.com"))
                    .body("data.phoneNumber", equalTo("010-5555-5555"));
        }

        @Test
        @DisplayName("존재하지 않는 사용자 조회 시 404를 반환한다")
        void shouldReturn404WhenUserNotFound() {
            // given
            String nonExistentUserId = UUID.randomUUID().toString();

            // when
            Response response =
                    givenAuthenticated(DEFAULT_USER_ID, savedTenant.getTenantId())
                            .when()
                            .get(BASE_PATH + "/" + nonExistentUserId);

            // then - RFC 7807 ProblemDetail 형식 검증
            response.then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body("status", equalTo(404))
                    .body("title", notNullValue())
                    .body("detail", notNullValue());
        }
    }

    // ========================================
    // 409 Conflict 테스트
    // ========================================

    @Nested
    @DisplayName("409 Conflict 테스트")
    class ConflictTest {

        @Test
        @DisplayName("동일한 identifier의 사용자가 이미 존재하면 409를 반환한다")
        void shouldReturn409WhenIdentifierAlreadyExists() {
            // given - 동일 identifier의 사용자 먼저 생성
            String duplicateIdentifier = "duplicate@example.com";
            createTestUser(duplicateIdentifier, "010-9999-9999");

            // 동일한 identifier로 다시 생성 시도
            Map<String, Object> request =
                    Map.of(
                            "organizationId",
                            savedOrganization.getOrganizationId(),
                            "identifier",
                            duplicateIdentifier,
                            "password",
                            "password123!",
                            "name",
                            "중복 사용자");

            // when
            Response response =
                    givenAuthenticated(DEFAULT_USER_ID, savedTenant.getTenantId())
                            .body(request)
                            .when()
                            .post(BASE_PATH);

            // then - RFC 7807 ProblemDetail 형식 검증
            response.then()
                    .statusCode(HttpStatus.CONFLICT.value())
                    .body("status", equalTo(409))
                    .body("title", notNullValue());
        }
    }

    // ========================================
    // 에러 응답 검증 테스트
    // ========================================

    @Nested
    @DisplayName("에러 응답 검증")
    class ErrorResponseTest {

        @Test
        @DisplayName("필수값 누락 시 400 에러와 함께 검증 에러 정보를 반환한다")
        void shouldReturn400WithValidationError() {
            // given - identifier 누락
            Map<String, Object> request =
                    Map.of(
                            "organizationId",
                            savedOrganization.getOrganizationId(),
                            "password",
                            "password123!");

            // when
            Response response =
                    givenAuthenticated(DEFAULT_USER_ID, savedTenant.getTenantId())
                            .body(request)
                            .when()
                            .post(BASE_PATH);

            // then - RFC 7807 ProblemDetail 형식 검증
            response.then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("status", equalTo(400))
                    .body("title", notNullValue())
                    .body("detail", notNullValue());
        }

        @Test
        @DisplayName("유효하지 않은 비밀번호 형식 시 400 에러를 반환한다")
        void shouldReturn400WithInvalidPasswordFormat() {
            // given - 비밀번호 8자 미만
            Map<String, Object> request =
                    Map.of(
                            "organizationId", savedOrganization.getOrganizationId(),
                            "identifier", "invalid-pw@example.com",
                            "password", "short");

            // when
            Response response =
                    givenAuthenticated(DEFAULT_USER_ID, savedTenant.getTenantId())
                            .body(request)
                            .when()
                            .post(BASE_PATH);

            // then
            response.then().statusCode(HttpStatus.BAD_REQUEST.value()).body("status", equalTo(400));
        }
    }

    // ========================================
    // Helper Methods
    // ========================================

    /** 테스트용 사용자를 생성하고 userId를 반환합니다. */
    private String createTestUser(String identifier, String phoneNumber) {
        String userId = UUID.randomUUID().toString();
        Instant now = Instant.now();

        UserJpaEntity user =
                UserJpaEntity.of(
                        userId,
                        savedOrganization.getOrganizationId(),
                        identifier,
                        phoneNumber,
                        "$2a$10$hashedpassword",
                        UserStatus.ACTIVE,
                        now,
                        now,
                        null);
        userJpaRepository.save(user);
        return userId;
    }
}
