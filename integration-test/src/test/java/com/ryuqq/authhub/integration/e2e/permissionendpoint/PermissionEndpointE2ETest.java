package com.ryuqq.authhub.integration.e2e.permissionendpoint;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import com.ryuqq.authhub.adapter.out.persistence.permission.entity.PermissionJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.permission.repository.PermissionJpaRepository;
import com.ryuqq.authhub.adapter.out.persistence.permissionendpoint.repository.PermissionEndpointJpaRepository;
import com.ryuqq.authhub.domain.permission.vo.PermissionType;
import com.ryuqq.authhub.integration.common.base.E2ETestBase;
import com.ryuqq.authhub.integration.common.tag.TestTags;
import io.restassured.response.Response;
import java.time.Instant;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

/**
 * PermissionEndpoint API E2E 테스트.
 *
 * <p>PermissionEndpoint 도메인의 전체 API 흐름을 검증합니다. REST API → Application → Domain → Repository → DB
 */
@Tag(TestTags.E2E)
@Tag(TestTags.PERMISSION)
class PermissionEndpointE2ETest extends E2ETestBase {

    private static final String BASE_PATH = "/api/v1/permission-endpoints";

    @Autowired private PermissionEndpointJpaRepository permissionEndpointJpaRepository;

    @Autowired private PermissionJpaRepository permissionJpaRepository;

    private static final Instant FIXED_TIME = Instant.parse("2025-01-01T00:00:00Z");

    private Long savedPermissionId;

    @BeforeEach
    void setUp() {
        permissionEndpointJpaRepository.deleteAll();
        permissionJpaRepository.deleteAll();

        PermissionJpaEntity permission =
                PermissionJpaEntity.of(
                        null,
                        null,
                        "user:read",
                        "user",
                        "read",
                        "사용자 조회",
                        PermissionType.CUSTOM,
                        FIXED_TIME,
                        FIXED_TIME,
                        null);
        savedPermissionId = permissionJpaRepository.save(permission).getPermissionId();
    }

    @Nested
    @DisplayName("POST /api/v1/permission-endpoints - 권한 엔드포인트 생성")
    class CreatePermissionEndpointTest {

        @Test
        @DisplayName("유효한 요청으로 권한 엔드포인트를 생성할 수 있다")
        void shouldCreatePermissionEndpointWithValidRequest() {
            // given
            Map<String, Object> request =
                    Map.of(
                            "permissionId", savedPermissionId,
                            "serviceName", "user-service",
                            "urlPattern", "/api/v1/users/{id}",
                            "httpMethod", "GET",
                            "description", "사용자 상세 조회 API");

            // when
            Response response = givenAuthenticated().body(request).when().post(BASE_PATH);

            // then
            response.then()
                    .statusCode(HttpStatus.CREATED.value())
                    .body("success", equalTo(true))
                    .body("data", notNullValue());

            Long createdId = response.jsonPath().getLong("data");
            assertThat(permissionEndpointJpaRepository.findById(createdId)).isPresent();
        }

        @Test
        @DisplayName("permissionId가 없으면 400 에러를 반환한다")
        void shouldReturn400WhenPermissionIdMissing() {
            // given
            Map<String, Object> request =
                    Map.of(
                            "serviceName", "user-service",
                            "urlPattern", "/api/v1/users",
                            "httpMethod", "GET");

            // when & then
            givenAuthenticated()
                    .body(request)
                    .when()
                    .post(BASE_PATH)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @DisplayName("urlPattern이 /로 시작하지 않으면 400 에러를 반환한다")
        void shouldReturn400WhenUrlPatternInvalid() {
            // given
            Map<String, Object> request =
                    Map.of(
                            "permissionId", savedPermissionId,
                            "serviceName", "user-service",
                            "urlPattern", "api/v1/users",
                            "httpMethod", "GET");

            // when & then
            givenAuthenticated()
                    .body(request)
                    .when()
                    .post(BASE_PATH)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }
    }

    // ========================================
    // FK 제약 조건 위반 테스트
    // ========================================

    @Nested
    @DisplayName("FK 제약 조건 위반 테스트")
    class ForeignKeyConstraintTest {

        @Test
        @DisplayName("존재하지 않는 permissionId로 생성 시 404를 반환한다")
        void shouldReturn404WhenPermissionNotFound() {
            // given
            Long nonExistentPermissionId = 999999L;
            Map<String, Object> request =
                    Map.of(
                            "permissionId", nonExistentPermissionId,
                            "serviceName", "user-service",
                            "urlPattern", "/api/v1/test",
                            "httpMethod", "GET");

            // when
            Response response = givenAuthenticated().body(request).when().post(BASE_PATH);

            // then - RFC 7807 ProblemDetail 형식 검증
            response.then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body("status", equalTo(404))
                    .body("title", notNullValue());
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/permission-endpoints/{permissionEndpointId} - 권한 엔드포인트 삭제")
    class DeletePermissionEndpointTest {

        @Test
        @DisplayName("권한 엔드포인트를 삭제할 수 있다")
        void shouldDeletePermissionEndpoint() {
            // given
            Map<String, Object> createRequest =
                    Map.of(
                            "permissionId", savedPermissionId,
                            "serviceName", "user-service",
                            "urlPattern", "/api/v1/delete-me",
                            "httpMethod", "DELETE");
            Response createResponse =
                    givenAuthenticated().body(createRequest).when().post(BASE_PATH);
            Long permissionEndpointId = createResponse.jsonPath().getLong("data");

            // when
            givenAuthenticated()
                    .when()
                    .delete(BASE_PATH + "/" + permissionEndpointId)
                    .then()
                    .statusCode(HttpStatus.OK.value());

            // then - Soft Delete 확인 (deletedAt이 설정됨)
            assertThat(permissionEndpointJpaRepository.findById(permissionEndpointId))
                    .isPresent()
                    .get()
                    .satisfies(pe -> assertThat(pe.isDeleted()).isTrue());
        }
    }
}
