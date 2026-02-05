package com.ryuqq.authhub.integration.e2e.internal;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

import com.ryuqq.authhub.adapter.out.persistence.permission.entity.PermissionJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.permission.repository.PermissionJpaRepository;
import com.ryuqq.authhub.adapter.out.persistence.permissionendpoint.entity.PermissionEndpointJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.permissionendpoint.repository.PermissionEndpointJpaRepository;
import com.ryuqq.authhub.domain.permission.vo.PermissionType;
import com.ryuqq.authhub.domain.permissionendpoint.vo.HttpMethod;
import com.ryuqq.authhub.integration.common.base.E2ETestBase;
import com.ryuqq.authhub.integration.common.tag.TestTags;
import io.restassured.response.Response;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

/**
 * Internal Permission Spec API E2E 테스트.
 *
 * <p>Gateway가 사용하는 엔드포인트-권한 스펙 조회 API를 검증합니다.
 *
 * <p>REST API → Application → Domain → Repository → DB 전체 흐름을 테스트합니다.
 */
@Tag(TestTags.E2E)
@Tag(TestTags.PERMISSION)
class InternalPermissionSpecE2ETest extends E2ETestBase {

    private static final String SPEC_PATH = "/api/v1/internal/endpoint-permissions/spec";
    private static final Instant FIXED_TIME = Instant.parse("2025-01-01T00:00:00Z");

    @Autowired private PermissionEndpointJpaRepository permissionEndpointRepository;

    @Autowired private PermissionJpaRepository permissionRepository;

    @BeforeEach
    void setUp() {
        permissionEndpointRepository.deleteAll();
        permissionRepository.deleteAll();
    }

    @Nested
    @DisplayName("GET /api/v1/internal/endpoint-permissions/spec - 권한 스펙 조회")
    class GetPermissionSpecTest {

        @Test
        @DisplayName("서비스 토큰으로 권한 스펙을 조회할 수 있다")
        void shouldGetPermissionSpecWithServiceToken() {
            // given - 테스트 데이터 생성
            PermissionJpaEntity permission = createPermission("user:read", "user", "read");
            createPermissionEndpoint(
                    permission.getPermissionId(),
                    "user-service",
                    "/api/v1/users",
                    HttpMethod.GET,
                    false);

            // when
            Response response = givenServiceToken().when().get(SPEC_PATH);

            // then
            response.then()
                    .statusCode(HttpStatus.OK.value())
                    .body("success", equalTo(true))
                    .body("data.endpoints", hasSize(1))
                    .body("data.endpoints[0].serviceName", equalTo("user-service"))
                    .body("data.endpoints[0].pathPattern", equalTo("/api/v1/users"))
                    .body("data.endpoints[0].httpMethod", equalTo("GET"))
                    .body("data.endpoints[0].requiredPermissions", hasItem("user:read"));
        }

        @Test
        @DisplayName("빈 스펙 목록을 정상적으로 반환한다")
        void shouldReturnEmptySpecList() {
            // given - 데이터 없음

            // when
            Response response = givenServiceToken().when().get(SPEC_PATH);

            // then
            response.then()
                    .statusCode(HttpStatus.OK.value())
                    .body("success", equalTo(true))
                    .body("data.endpoints", empty());
        }

        @Test
        @DisplayName("여러 엔드포인트 스펙을 조회할 수 있다")
        void shouldGetMultipleEndpointSpecs() {
            // given
            PermissionJpaEntity readPermission = createPermission("user:read", "user", "read");
            PermissionJpaEntity writePermission = createPermission("user:write", "user", "write");

            createPermissionEndpoint(
                    readPermission.getPermissionId(),
                    "user-service",
                    "/api/v1/users",
                    HttpMethod.GET,
                    false);
            createPermissionEndpoint(
                    writePermission.getPermissionId(),
                    "user-service",
                    "/api/v1/users",
                    HttpMethod.POST,
                    false);

            // when
            Response response = givenServiceToken().when().get(SPEC_PATH);

            // then
            response.then().statusCode(HttpStatus.OK.value()).body("data.endpoints", hasSize(2));
        }

        @Test
        @DisplayName("version과 updatedAt이 포함된다")
        void shouldIncludeVersionAndUpdatedAt() {
            // given
            PermissionJpaEntity permission = createPermission("user:read", "user", "read");
            createPermissionEndpoint(
                    permission.getPermissionId(),
                    "user-service",
                    "/api/v1/users",
                    HttpMethod.GET,
                    false);

            // when
            Response response = givenServiceToken().when().get(SPEC_PATH);

            // then
            response.then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.version", notNullValue())
                    .body("data.updatedAt", notNullValue());
        }

        @Test
        @DisplayName("공개 엔드포인트 정보가 포함된다")
        void shouldIncludePublicEndpointInfo() {
            // given
            PermissionJpaEntity permission = createPermission("health:read", "health", "read");
            createPermissionEndpoint(
                    permission.getPermissionId(),
                    "health-service",
                    "/api/v1/health",
                    HttpMethod.GET,
                    true);

            // when
            Response response = givenServiceToken().when().get(SPEC_PATH);

            // then
            response.then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.endpoints[0].isPublic", equalTo(true));
        }

        @Test
        @DisplayName("Path Variable이 포함된 URL 패턴을 처리할 수 있다")
        void shouldHandlePathVariablePattern() {
            // given
            PermissionJpaEntity permission = createPermission("user:read", "user", "read");
            createPermissionEndpoint(
                    permission.getPermissionId(),
                    "user-service",
                    "/api/v1/users/{id}",
                    HttpMethod.GET,
                    false);

            // when
            Response response = givenServiceToken().when().get(SPEC_PATH);

            // then
            response.then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.endpoints[0].pathPattern", equalTo("/api/v1/users/{id}"));
        }
    }

    @Nested
    @DisplayName("인증 테스트")
    class AuthenticationTest {

        @Test
        @DisplayName("일반 사용자 인증으로도 스펙을 조회할 수 있다")
        void shouldAllowAuthenticatedUserToGetSpec() {
            // given
            PermissionJpaEntity permission = createPermission("user:read", "user", "read");
            createPermissionEndpoint(
                    permission.getPermissionId(),
                    "user-service",
                    "/api/v1/users",
                    HttpMethod.GET,
                    false);

            // when - 일반 인증으로 요청 (테스트 환경에서는 permitAll)
            Response response = givenAuthenticated().when().get(SPEC_PATH);

            // then
            response.then().statusCode(HttpStatus.OK.value()).body("success", equalTo(true));
        }
    }

    // ========================================
    // Helper Methods
    // ========================================

    private PermissionJpaEntity createPermission(String key, String resource, String action) {
        PermissionJpaEntity entity =
                PermissionJpaEntity.of(
                        null,
                        null,
                        key,
                        resource,
                        action,
                        key + " 권한",
                        PermissionType.CUSTOM,
                        FIXED_TIME,
                        FIXED_TIME,
                        null);
        return permissionRepository.save(entity);
    }

    private void createPermissionEndpoint(
            Long permissionId,
            String serviceName,
            String urlPattern,
            HttpMethod httpMethod,
            boolean isPublic) {
        PermissionEndpointJpaEntity entity =
                PermissionEndpointJpaEntity.of(
                        null,
                        permissionId,
                        serviceName,
                        urlPattern,
                        httpMethod,
                        "테스트 엔드포인트",
                        isPublic,
                        FIXED_TIME,
                        FIXED_TIME,
                        null);
        permissionEndpointRepository.save(entity);
    }
}
