package com.ryuqq.authhub.integration.e2e.permission;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import com.ryuqq.authhub.adapter.out.persistence.permission.repository.PermissionJpaRepository;
import com.ryuqq.authhub.integration.common.base.E2ETestBase;
import com.ryuqq.authhub.integration.common.tag.TestTags;
import io.restassured.response.Response;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

/**
 * Permission API E2E 테스트.
 *
 * <p>Permission 도메인의 전체 API 흐름을 검증합니다. REST API → Application → Domain → Repository → DB
 */
@Tag(TestTags.E2E)
@Tag(TestTags.PERMISSION)
class PermissionE2ETest extends E2ETestBase {

    private static final String BASE_PATH = "/api/v1/auth/permissions";

    @Autowired private PermissionJpaRepository permissionJpaRepository;

    @BeforeEach
    void setUp() {
        permissionJpaRepository.deleteAll();
    }

    @Nested
    @DisplayName("POST /api/v1/auth/permissions - 권한 생성")
    class CreatePermissionTest {

        @Test
        @DisplayName("유효한 요청으로 권한을 생성할 수 있다")
        void shouldCreatePermissionWithValidRequest() {
            // given
            Map<String, Object> request =
                    Map.of(
                            "resource", "user",
                            "action", "read",
                            "description", "사용자 조회 권한");

            // when
            Response response = givenAuthenticated().body(request).when().post(BASE_PATH);

            // then
            response.then()
                    .statusCode(HttpStatus.CREATED.value())
                    .body("success", equalTo(true))
                    .body("data.permissionId", notNullValue());

            Long createdPermissionId = response.jsonPath().getLong("data.permissionId");
            assertThat(permissionJpaRepository.findById(createdPermissionId)).isPresent();
        }

        @Test
        @DisplayName("resource가 없으면 400 에러를 반환한다")
        void shouldReturn400WhenResourceMissing() {
            // given
            Map<String, Object> request = Map.of("action", "read");

            // when & then
            givenAuthenticated()
                    .body(request)
                    .when()
                    .post(BASE_PATH)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @DisplayName("resource 형식이 잘못되면 400 에러를 반환한다")
        void shouldReturn400WhenResourceFormatInvalid() {
            // given
            Map<String, Object> request = Map.of("resource", "User", "action", "read"); // 대문자 불가

            // when & then
            givenAuthenticated()
                    .body(request)
                    .when()
                    .post(BASE_PATH)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/auth/permissions/{permissionId} - 권한 수정")
    class UpdatePermissionTest {

        @Test
        @DisplayName("권한 설명을 수정할 수 있다")
        void shouldUpdatePermissionDescription() {
            // given
            Map<String, Object> createRequest =
                    Map.of(
                            "resource", "user",
                            "action", "update",
                            "description", "원본 설명");
            Response createResponse =
                    givenAuthenticated().body(createRequest).when().post(BASE_PATH);
            Long permissionId = createResponse.jsonPath().getLong("data.permissionId");

            Map<String, Object> updateRequest = Map.of("description", "수정된 설명");

            // when
            Response response =
                    givenAuthenticated()
                            .body(updateRequest)
                            .when()
                            .put(BASE_PATH + "/" + permissionId);

            // then
            response.then().statusCode(HttpStatus.OK.value()).body("success", equalTo(true));

            assertThat(permissionJpaRepository.findById(permissionId))
                    .isPresent()
                    .get()
                    .extracting(p -> p.getDescription())
                    .isEqualTo("수정된 설명");
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/auth/permissions/{permissionId} - 권한 삭제")
    class DeletePermissionTest {

        @Test
        @DisplayName("권한을 삭제할 수 있다")
        void shouldDeletePermission() {
            // given
            Map<String, Object> createRequest =
                    Map.of(
                            "resource", "user",
                            "action", "delete",
                            "description", "삭제용 권한");
            Response createResponse =
                    givenAuthenticated().body(createRequest).when().post(BASE_PATH);
            Long permissionId = createResponse.jsonPath().getLong("data.permissionId");

            // when
            givenAuthenticated()
                    .when()
                    .delete(BASE_PATH + "/" + permissionId)
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());

            // then - Soft Delete 확인 (deletedAt이 설정됨)
            assertThat(permissionJpaRepository.findById(permissionId))
                    .isPresent()
                    .get()
                    .satisfies(p -> assertThat(p.isDeleted()).isTrue());
        }
    }

    // ========================================
    // 409 Conflict 테스트
    // ========================================

    @Nested
    @DisplayName("409 Conflict 테스트")
    class ConflictTest {

        @Test
        @DisplayName("동일한 resource:action 조합의 권한이 이미 존재하면 409를 반환한다")
        void shouldReturn409WhenPermissionKeyAlreadyExists() {
            // given - 동일 resource:action 조합의 권한 먼저 생성
            Map<String, Object> firstRequest =
                    Map.of(
                            "resource", "order",
                            "action", "create",
                            "description", "주문 생성 권한");
            givenAuthenticated().body(firstRequest).when().post(BASE_PATH);

            // 동일한 resource:action 으로 다시 생성 시도
            Map<String, Object> duplicateRequest =
                    Map.of(
                            "resource", "order",
                            "action", "create",
                            "description", "중복 권한");

            // when
            Response response = givenAuthenticated().body(duplicateRequest).when().post(BASE_PATH);

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
        @DisplayName("존재하지 않는 권한 수정 시 404를 반환한다")
        void shouldReturn404WhenUpdatingNonExistentPermission() {
            // given
            Long nonExistentPermissionId = 999999L;
            Map<String, Object> request = Map.of("description", "수정된 설명");

            // when
            Response response =
                    givenAuthenticated()
                            .body(request)
                            .when()
                            .put(BASE_PATH + "/" + nonExistentPermissionId);

            // then - RFC 7807 ProblemDetail 형식 검증
            response.then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body("status", equalTo(404))
                    .body("title", notNullValue())
                    .body("detail", notNullValue());
        }

        @Test
        @DisplayName("존재하지 않는 권한 삭제 시 404를 반환한다")
        void shouldReturn404WhenDeletingNonExistentPermission() {
            // given
            Long nonExistentPermissionId = 999999L;

            // when
            Response response =
                    givenAuthenticated().when().delete(BASE_PATH + "/" + nonExistentPermissionId);

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
            // given - resource 누락
            Map<String, Object> request = Map.of("action", "read");

            // when
            Response response = givenAuthenticated().body(request).when().post(BASE_PATH);

            // then - RFC 7807 ProblemDetail 형식 검증
            response.then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("status", equalTo(400))
                    .body("title", notNullValue())
                    .body("detail", notNullValue());
        }

        @Test
        @DisplayName("잘못된 resource 형식 시 400 에러와 상세 정보를 반환한다")
        void shouldReturn400WithInvalidResourceFormat() {
            // given - 대문자가 포함된 resource
            Map<String, Object> request = Map.of("resource", "User", "action", "read");

            // when
            Response response = givenAuthenticated().body(request).when().post(BASE_PATH);

            // then
            response.then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("status", equalTo(400))
                    .body("title", notNullValue());
        }
    }
}
