package com.ryuqq.authhub.integration.e2e.organization;

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
import com.ryuqq.authhub.domain.organization.vo.OrganizationStatus;
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

/**
 * Organization API E2E 테스트.
 *
 * <p>Organization 도메인의 전체 API 흐름을 검증합니다. REST API → Application → Domain → Repository → DB
 */
@Tag(TestTags.E2E)
@Tag(TestTags.ORGANIZATION)
class OrganizationE2ETest extends E2ETestBase {

    private static final String BASE_PATH = "/api/v1/auth/organizations";

    @Autowired private OrganizationJpaRepository organizationJpaRepository;

    @Autowired private TenantJpaRepository tenantJpaRepository;

    private TenantJpaEntity savedTenant;

    @BeforeEach
    void setUp() {
        organizationJpaRepository.deleteAll();
        tenantJpaRepository.deleteAll();

        savedTenant = tenantJpaRepository.save(TenantJpaEntityFixture.create());
    }

    @Nested
    @DisplayName("POST /api/v1/auth/organizations - 조직 생성")
    class CreateOrganizationTest {

        @Test
        @DisplayName("유효한 요청으로 조직을 생성할 수 있다")
        void shouldCreateOrganizationWithValidRequest() {
            // given
            Map<String, Object> request =
                    Map.of("tenantId", savedTenant.getTenantId(), "name", "테스트 조직");

            // when
            Response response = givenSystemAdmin().body(request).when().post(BASE_PATH);

            // then
            response.then()
                    .statusCode(HttpStatus.CREATED.value())
                    .body("success", equalTo(true))
                    .body("data.organizationId", notNullValue());

            String createdOrgId = response.jsonPath().getString("data.organizationId");
            assertThat(organizationJpaRepository.findById(createdOrgId)).isPresent();
        }

        @Test
        @DisplayName("테넌트 ID가 없으면 400 에러를 반환한다")
        void shouldReturn400WhenTenantIdMissing() {
            // given
            Map<String, Object> request = Map.of("name", "테스트 조직");

            // when & then
            givenSystemAdmin()
                    .body(request)
                    .when()
                    .post(BASE_PATH)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @DisplayName("이름이 없으면 400 에러를 반환한다")
        void shouldReturn400WhenNameMissing() {
            // given
            Map<String, Object> request = Map.of("tenantId", savedTenant.getTenantId());

            // when & then
            givenSystemAdmin()
                    .body(request)
                    .when()
                    .post(BASE_PATH)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/auth/organizations/{organizationId}/name - 조직 이름 수정")
    class UpdateOrganizationNameTest {

        @Test
        @DisplayName("조직 이름을 수정할 수 있다")
        void shouldUpdateOrganizationName() {
            // given
            OrganizationJpaEntity org =
                    organizationJpaRepository.save(
                            OrganizationJpaEntityFixture.createWithTenant(
                                    savedTenant.getTenantId()));
            Map<String, Object> request = Map.of("name", "수정된 조직명");

            // when
            Response response =
                    givenAuthenticated(DEFAULT_USER_ID, savedTenant.getTenantId())
                            .header(HEADER_ORGANIZATION_ID, org.getOrganizationId())
                            .body(request)
                            .when()
                            .put(BASE_PATH + "/" + org.getOrganizationId() + "/name");

            // then
            response.then().statusCode(HttpStatus.OK.value()).body("success", equalTo(true));

            assertThat(organizationJpaRepository.findById(org.getOrganizationId()))
                    .isPresent()
                    .get()
                    .extracting(OrganizationJpaEntity::getName)
                    .isEqualTo("수정된 조직명");
        }
    }

    @Nested
    @DisplayName("PATCH /api/v1/auth/organizations/{organizationId}/status - 조직 상태 수정")
    class UpdateOrganizationStatusTest {

        @Test
        @DisplayName("조직 상태를 수정할 수 있다")
        void shouldUpdateOrganizationStatus() {
            // given
            OrganizationJpaEntity org =
                    organizationJpaRepository.save(
                            OrganizationJpaEntityFixture.createWithTenant(
                                    savedTenant.getTenantId()));
            Map<String, Object> request = Map.of("status", "INACTIVE");

            // when
            Response response =
                    givenAuthenticated(DEFAULT_USER_ID, savedTenant.getTenantId())
                            .header(HEADER_ORGANIZATION_ID, org.getOrganizationId())
                            .body(request)
                            .when()
                            .patch(BASE_PATH + "/" + org.getOrganizationId() + "/status");

            // then
            response.then().statusCode(HttpStatus.OK.value()).body("success", equalTo(true));

            assertThat(organizationJpaRepository.findById(org.getOrganizationId()))
                    .isPresent()
                    .get()
                    .extracting(OrganizationJpaEntity::getStatus)
                    .isEqualTo(OrganizationStatus.INACTIVE);
        }
    }

    // ========================================
    // Query 테스트 (목록 조회)
    // ========================================

    @Nested
    @DisplayName("GET /api/v1/auth/organizations - 조직 목록 조회")
    class GetOrganizationsTest {

        @Test
        @DisplayName("조직 목록을 조회할 수 있다")
        void shouldGetOrganizationList() {
            // given - 테스트 조직 2개 생성
            organizationJpaRepository.save(
                    OrganizationJpaEntityFixture.createWithTenant(savedTenant.getTenantId()));
            organizationJpaRepository.save(
                    OrganizationJpaEntityFixture.createWithTenantAndName(
                            savedTenant.getTenantId(), "두번째 조직"));

            // when
            Response response = givenSystemAdmin().when().get(BASE_PATH);

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
            // given - 테스트 조직 3개 생성
            organizationJpaRepository.save(
                    OrganizationJpaEntityFixture.createWithTenant(savedTenant.getTenantId()));
            organizationJpaRepository.save(
                    OrganizationJpaEntityFixture.createWithTenantAndName(
                            savedTenant.getTenantId(), "조직2"));
            organizationJpaRepository.save(
                    OrganizationJpaEntityFixture.createWithTenantAndName(
                            savedTenant.getTenantId(), "조직3"));

            // when - 페이지 크기 2로 조회
            Response response =
                    givenSystemAdmin()
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
                    .body("data.first", equalTo(true));
        }

        @Test
        @DisplayName("테넌트 ID로 필터링할 수 있다")
        void shouldFilterByTenantId() {
            // given
            organizationJpaRepository.save(
                    OrganizationJpaEntityFixture.createWithTenant(savedTenant.getTenantId()));

            // when
            Response response =
                    givenSystemAdmin()
                            .queryParam("tenantIds", savedTenant.getTenantId())
                            .when()
                            .get(BASE_PATH);

            // then
            response.then()
                    .statusCode(HttpStatus.OK.value())
                    .body("success", equalTo(true))
                    .body("data.content", hasSize(greaterThanOrEqualTo(1)));
        }
    }

    // ========================================
    // 404 Not Found 테스트
    // ========================================

    @Nested
    @DisplayName("404 Not Found 테스트")
    class NotFoundTest {

        @Test
        @DisplayName("존재하지 않는 조직 이름 수정 시 404를 반환한다")
        void shouldReturn404WhenUpdatingNonExistentOrganization() {
            // given
            String nonExistentOrgId = UUID.randomUUID().toString();
            Map<String, Object> request = Map.of("name", "새 이름");

            // when
            Response response =
                    givenAuthenticated(DEFAULT_USER_ID, savedTenant.getTenantId())
                            .header(HEADER_ORGANIZATION_ID, nonExistentOrgId)
                            .body(request)
                            .when()
                            .put(BASE_PATH + "/" + nonExistentOrgId + "/name");

            // then - RFC 7807 ProblemDetail 형식 검증
            response.then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body("status", equalTo(404))
                    .body("title", notNullValue())
                    .body("detail", notNullValue());
        }

        @Test
        @DisplayName("존재하지 않는 조직 상태 수정 시 404를 반환한다")
        void shouldReturn404WhenUpdatingStatusOfNonExistentOrganization() {
            // given
            String nonExistentOrgId = UUID.randomUUID().toString();
            Map<String, Object> request = Map.of("status", "INACTIVE");

            // when
            Response response =
                    givenAuthenticated(DEFAULT_USER_ID, savedTenant.getTenantId())
                            .header(HEADER_ORGANIZATION_ID, nonExistentOrgId)
                            .body(request)
                            .when()
                            .patch(BASE_PATH + "/" + nonExistentOrgId + "/status");

            // then
            response.then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body("status", equalTo(404))
                    .body("title", notNullValue());
        }
    }

    // ========================================
    // 409 Conflict 테스트
    // ========================================

    @Nested
    @DisplayName("409 Conflict 테스트")
    class ConflictTest {

        @Test
        @DisplayName("동일 테넌트 내에 동일한 이름의 조직이 이미 존재하면 409를 반환한다")
        void shouldReturn409WhenOrganizationNameAlreadyExistsInTenant() {
            // given - 동일 테넌트 내에 동일 이름의 조직이 이미 존재
            String duplicateName = "중복조직";
            organizationJpaRepository.save(
                    OrganizationJpaEntityFixture.createWithTenantAndName(
                            savedTenant.getTenantId(), duplicateName));

            // 동일 이름으로 다시 생성 시도
            Map<String, Object> request =
                    Map.of("tenantId", savedTenant.getTenantId(), "name", duplicateName);

            // when
            Response response = givenSystemAdmin().body(request).when().post(BASE_PATH);

            // then - RFC 7807 ProblemDetail 형식 검증
            response.then()
                    .statusCode(HttpStatus.CONFLICT.value())
                    .body("status", equalTo(409))
                    .body("title", notNullValue());
        }

        @Test
        @DisplayName("조직 이름 수정 시 동일 테넌트 내 다른 조직과 중복되면 409를 반환한다")
        void shouldReturn409WhenUpdatingToExistingNameInSameTenant() {
            // given - 동일 테넌트 내에 두 개의 조직 생성
            OrganizationJpaEntity org1 =
                    organizationJpaRepository.save(
                            OrganizationJpaEntityFixture.createWithTenantAndName(
                                    savedTenant.getTenantId(), "조직A"));
            OrganizationJpaEntity org2 =
                    organizationJpaRepository.save(
                            OrganizationJpaEntityFixture.createWithTenantAndName(
                                    savedTenant.getTenantId(), "조직B"));

            // org2의 이름을 org1과 동일하게 변경 시도
            Map<String, Object> request = Map.of("name", "조직A");

            // when
            Response response =
                    givenAuthenticated(DEFAULT_USER_ID, savedTenant.getTenantId())
                            .header(HEADER_ORGANIZATION_ID, org2.getOrganizationId())
                            .body(request)
                            .when()
                            .put(BASE_PATH + "/" + org2.getOrganizationId() + "/name");

            // then
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
            // given - name 누락
            Map<String, Object> request = Map.of("tenantId", savedTenant.getTenantId());

            // when
            Response response = givenSystemAdmin().body(request).when().post(BASE_PATH);

            // then - RFC 7807 ProblemDetail 형식 검증
            response.then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("status", equalTo(400))
                    .body("title", notNullValue())
                    .body("detail", notNullValue());
        }
    }
}
