package com.ryuqq.authhub.integration.e2e.tenant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import com.ryuqq.authhub.adapter.out.persistence.tenant.entity.TenantJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.tenant.fixture.TenantJpaEntityFixture;
import com.ryuqq.authhub.adapter.out.persistence.tenant.repository.TenantJpaRepository;
import com.ryuqq.authhub.domain.tenant.vo.TenantStatus;
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
 * Tenant API E2E 테스트.
 *
 * <p>Tenant 도메인의 전체 API 흐름을 검증합니다. REST API → Application → Domain → Repository → DB
 */
@Tag(TestTags.E2E)
@Tag(TestTags.TENANT)
class TenantE2ETest extends E2ETestBase {

    private static final String BASE_PATH = "/api/v1/auth/tenants";

    @Autowired private TenantJpaRepository tenantJpaRepository;

    @BeforeEach
    void setUp() {
        tenantJpaRepository.deleteAll();
    }

    @Nested
    @DisplayName("POST /api/v1/auth/tenants - 테넌트 생성")
    class CreateTenantTest {

        @Test
        @DisplayName("유효한 요청으로 테넌트를 생성할 수 있다")
        void shouldCreateTenantWithValidRequest() {
            // given
            Map<String, Object> request = Map.of("name", "테넌트A");

            // when
            Response response = givenSystemAdmin().body(request).when().post(BASE_PATH);

            // then
            response.then()
                    .statusCode(HttpStatus.CREATED.value())
                    .body("success", equalTo(true))
                    .body("data.tenantId", notNullValue());

            // DB 검증
            String createdTenantId = response.jsonPath().getString("data.tenantId");
            assertThat(tenantJpaRepository.findById(createdTenantId)).isPresent();
        }

        @Test
        @DisplayName("이름이 없으면 400 에러를 반환한다")
        void shouldReturn400WhenNameMissing() {
            // given
            Map<String, Object> request = Map.of();

            // when & then
            givenSystemAdmin()
                    .body(request)
                    .when()
                    .post(BASE_PATH)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @DisplayName("이름이 2자 미만이면 400 에러를 반환한다")
        void shouldReturn400WhenNameTooShort() {
            // given
            Map<String, Object> request = Map.of("name", "A");

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
    @DisplayName("PUT /api/v1/auth/tenants/{tenantId}/name - 테넌트 이름 수정")
    class UpdateTenantNameTest {

        @Test
        @DisplayName("테넌트 이름을 수정할 수 있다")
        void shouldUpdateTenantName() {
            // given
            TenantJpaEntity tenant = tenantJpaRepository.save(TenantJpaEntityFixture.create());
            Map<String, Object> request = Map.of("name", "수정된 테넌트명");

            // when
            Response response =
                    givenSystemAdmin()
                            .body(request)
                            .when()
                            .put(BASE_PATH + "/" + tenant.getTenantId() + "/name");

            // then
            response.then()
                    .statusCode(HttpStatus.OK.value())
                    .body("success", equalTo(true))
                    .body("data.tenantId", equalTo(tenant.getTenantId()));

            assertThat(tenantJpaRepository.findById(tenant.getTenantId()))
                    .isPresent()
                    .get()
                    .extracting(TenantJpaEntity::getName)
                    .isEqualTo("수정된 테넌트명");
        }
    }

    @Nested
    @DisplayName("PATCH /api/v1/auth/tenants/{tenantId}/status - 테넌트 상태 수정")
    class UpdateTenantStatusTest {

        @Test
        @DisplayName("테넌트 상태를 수정할 수 있다")
        void shouldUpdateTenantStatus() {
            // given
            TenantJpaEntity tenant = tenantJpaRepository.save(TenantJpaEntityFixture.create());
            Map<String, Object> request = Map.of("status", "INACTIVE");

            // when
            Response response =
                    givenSystemAdmin()
                            .body(request)
                            .when()
                            .patch(BASE_PATH + "/" + tenant.getTenantId() + "/status");

            // then
            response.then().statusCode(HttpStatus.OK.value()).body("success", equalTo(true));

            assertThat(tenantJpaRepository.findById(tenant.getTenantId()))
                    .isPresent()
                    .get()
                    .extracting(TenantJpaEntity::getStatus)
                    .isEqualTo(TenantStatus.INACTIVE);
        }
    }

    // ========================================
    // 409 Conflict 테스트
    // ========================================

    @Nested
    @DisplayName("409 Conflict 테스트")
    class ConflictTest {

        @Test
        @DisplayName("동일한 이름의 테넌트가 이미 존재하면 409를 반환한다")
        void shouldReturn409WhenTenantNameAlreadyExists() {
            // given - 동일 이름의 테넌트 먼저 생성
            String duplicateName = "중복테넌트";
            TenantJpaEntity existingTenant =
                    tenantJpaRepository.save(TenantJpaEntityFixture.createWithName(duplicateName));

            Map<String, Object> request = Map.of("name", duplicateName);

            // when
            Response response = givenSystemAdmin().body(request).when().post(BASE_PATH);

            // then - RFC 7807 ProblemDetail 형식 검증
            response.then()
                    .statusCode(HttpStatus.CONFLICT.value())
                    .body("status", equalTo(409))
                    .body("title", notNullValue());
        }
    }

    // ========================================
    // 404 Not Found 테스트
    // ========================================

    @Nested
    @DisplayName("404 Not Found 테스트")
    class NotFoundTest {

        @Test
        @DisplayName("존재하지 않는 테넌트 이름 수정 시 404를 반환한다")
        void shouldReturn404WhenUpdatingNonExistentTenant() {
            // given
            String nonExistentTenantId = UUID.randomUUID().toString();
            Map<String, Object> request = Map.of("name", "새 이름");

            // when
            Response response =
                    givenSystemAdmin()
                            .body(request)
                            .when()
                            .put(BASE_PATH + "/" + nonExistentTenantId + "/name");

            // then - RFC 7807 ProblemDetail 형식 검증
            response.then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body("status", equalTo(404))
                    .body("title", notNullValue())
                    .body("detail", notNullValue());
        }

        @Test
        @DisplayName("존재하지 않는 테넌트 상태 수정 시 404를 반환한다")
        void shouldReturn404WhenUpdatingStatusOfNonExistentTenant() {
            // given
            String nonExistentTenantId = UUID.randomUUID().toString();
            Map<String, Object> request = Map.of("status", "INACTIVE");

            // when
            Response response =
                    givenSystemAdmin()
                            .body(request)
                            .when()
                            .patch(BASE_PATH + "/" + nonExistentTenantId + "/status");

            // then
            response.then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body("status", equalTo(404))
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
            Map<String, Object> request = Map.of();

            // when
            Response response = givenSystemAdmin().body(request).when().post(BASE_PATH);

            // then - RFC 7807 ProblemDetail 형식 검증
            response.then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("status", equalTo(400))
                    .body("title", notNullValue())
                    .body("detail", notNullValue());
        }

        @Test
        @DisplayName("이름이 너무 짧으면 400 에러와 상세 정보를 반환한다")
        void shouldReturn400WhenNameTooShort() {
            // given - 2자 미만
            Map<String, Object> request = Map.of("name", "A");

            // when
            Response response = givenSystemAdmin().body(request).when().post(BASE_PATH);

            // then
            response.then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("status", equalTo(400))
                    .body("title", notNullValue());
        }
    }
}
