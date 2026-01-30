package com.ryuqq.authhub.integration.e2e.role;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import com.ryuqq.authhub.adapter.out.persistence.role.repository.RoleJpaRepository;
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
 * Role API E2E 테스트.
 *
 * <p>Role 도메인의 전체 API 흐름을 검증합니다. REST API → Application → Domain → Repository → DB
 */
@Tag(TestTags.E2E)
@Tag(TestTags.ROLE)
class RoleE2ETest extends E2ETestBase {

    private static final String BASE_PATH = "/api/v1/auth/roles";

    @Autowired private RoleJpaRepository roleJpaRepository;

    @BeforeEach
    void setUp() {
        roleJpaRepository.deleteAll();
    }

    @Nested
    @DisplayName("POST /api/v1/auth/roles - 역할 생성")
    class CreateRoleTest {

        @Test
        @DisplayName("유효한 요청으로 역할을 생성할 수 있다")
        void shouldCreateRoleWithValidRequest() {
            // given
            Map<String, Object> request =
                    Map.of(
                            "name", "USER_MANAGER",
                            "displayName", "사용자 관리자",
                            "description", "사용자 관리 권한을 가진 역할");

            // when
            Response response = givenAuthenticated().body(request).when().post(BASE_PATH);

            // then
            response.then()
                    .statusCode(HttpStatus.CREATED.value())
                    .body("success", equalTo(true))
                    .body("data.roleId", notNullValue());

            Long createdRoleId = response.jsonPath().getLong("data.roleId");
            assertThat(roleJpaRepository.findById(createdRoleId)).isPresent();
        }

        @Test
        @DisplayName("테넌트 ID 없이 Global 역할을 생성할 수 있다")
        void shouldCreateGlobalRoleWithoutTenantId() {
            // given
            Map<String, Object> request =
                    Map.of(
                            "name", "GLOBAL_ADMIN",
                            "displayName", "글로벌 관리자");

            // when
            Response response = givenAuthenticated().body(request).when().post(BASE_PATH);

            // then
            response.then()
                    .statusCode(HttpStatus.CREATED.value())
                    .body("success", equalTo(true))
                    .body("data.roleId", notNullValue());
        }

        @Test
        @DisplayName("name이 없으면 400 에러를 반환한다")
        void shouldReturn400WhenNameMissing() {
            // given
            Map<String, Object> request = Map.of("displayName", "표시 이름");

            // when & then
            givenAuthenticated()
                    .body(request)
                    .when()
                    .post(BASE_PATH)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @DisplayName("name 형식이 UPPER_SNAKE_CASE가 아니면 400 에러를 반환한다")
        void shouldReturn400WhenNameFormatInvalid() {
            // given
            Map<String, Object> request = Map.of("name", "invalid-format");

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
    @DisplayName("PUT /api/v1/auth/roles/{roleId} - 역할 수정")
    class UpdateRoleTest {

        @Test
        @DisplayName("역할 표시 이름과 설명을 수정할 수 있다")
        void shouldUpdateRole() {
            // given
            Map<String, Object> createRequest =
                    Map.of(
                            "name", "EDITABLE_ROLE",
                            "displayName", "원본 표시명",
                            "description", "원본 설명");
            Response createResponse =
                    givenAuthenticated().body(createRequest).when().post(BASE_PATH);
            Long roleId = createResponse.jsonPath().getLong("data.roleId");

            Map<String, Object> updateRequest =
                    Map.of(
                            "displayName", "수정된 표시명",
                            "description", "수정된 설명");

            // when
            Response response =
                    givenAuthenticated().body(updateRequest).when().put(BASE_PATH + "/" + roleId);

            // then
            response.then().statusCode(HttpStatus.OK.value()).body("success", equalTo(true));

            assertThat(roleJpaRepository.findById(roleId))
                    .isPresent()
                    .get()
                    .satisfies(
                            role -> {
                                assertThat(role.getDisplayName()).isEqualTo("수정된 표시명");
                                assertThat(role.getDescription()).isEqualTo("수정된 설명");
                            });
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/auth/roles/{roleId} - 역할 삭제")
    class DeleteRoleTest {

        @Test
        @DisplayName("역할을 삭제할 수 있다")
        void shouldDeleteRole() {
            // given
            Map<String, Object> createRequest =
                    Map.of(
                            "name", "DELETABLE_ROLE",
                            "displayName", "삭제용 역할");
            Response createResponse =
                    givenAuthenticated().body(createRequest).when().post(BASE_PATH);
            Long roleId = createResponse.jsonPath().getLong("data.roleId");

            // when
            givenAuthenticated()
                    .when()
                    .delete(BASE_PATH + "/" + roleId)
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());

            // then - Soft Delete 확인 (deletedAt이 설정됨)
            assertThat(roleJpaRepository.findById(roleId))
                    .isPresent()
                    .get()
                    .satisfies(r -> assertThat(r.isDeleted()).isTrue());
        }
    }

    // ========================================
    // 409 Conflict 테스트
    // ========================================

    @Nested
    @DisplayName("409 Conflict 테스트")
    class ConflictTest {

        @Test
        @DisplayName("동일한 name의 역할이 이미 존재하면 409를 반환한다")
        void shouldReturn409WhenRoleNameAlreadyExists() {
            // given - 동일 이름의 역할 먼저 생성
            Map<String, Object> firstRequest =
                    Map.of(
                            "name", "DUPLICATE_ROLE",
                            "displayName", "중복 역할");
            givenAuthenticated().body(firstRequest).when().post(BASE_PATH);

            // 동일한 이름으로 다시 생성 시도
            Map<String, Object> duplicateRequest =
                    Map.of(
                            "name", "DUPLICATE_ROLE",
                            "displayName", "또 다른 중복 역할");

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
        @DisplayName("존재하지 않는 역할 수정 시 404를 반환한다")
        void shouldReturn404WhenUpdatingNonExistentRole() {
            // given
            Long nonExistentRoleId = 999999L;
            Map<String, Object> request =
                    Map.of(
                            "displayName", "수정된 표시명",
                            "description", "수정된 설명");

            // when
            Response response =
                    givenAuthenticated()
                            .body(request)
                            .when()
                            .put(BASE_PATH + "/" + nonExistentRoleId);

            // then - RFC 7807 ProblemDetail 형식 검증
            response.then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body("status", equalTo(404))
                    .body("title", notNullValue())
                    .body("detail", notNullValue());
        }

        @Test
        @DisplayName("존재하지 않는 역할 삭제 시 404를 반환한다")
        void shouldReturn404WhenDeletingNonExistentRole() {
            // given
            Long nonExistentRoleId = 999999L;

            // when
            Response response =
                    givenAuthenticated().when().delete(BASE_PATH + "/" + nonExistentRoleId);

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
            Map<String, Object> request = Map.of("displayName", "표시 이름");

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
        @DisplayName("잘못된 name 형식 시 400 에러와 상세 정보를 반환한다")
        void shouldReturn400WithInvalidNameFormat() {
            // given - UPPER_SNAKE_CASE가 아닌 형식
            Map<String, Object> request = Map.of("name", "invalid-format", "displayName", "잘못된 형식");

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
