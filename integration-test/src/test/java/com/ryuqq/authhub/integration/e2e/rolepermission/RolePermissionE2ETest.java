package com.ryuqq.authhub.integration.e2e.rolepermission;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import com.ryuqq.authhub.adapter.out.persistence.permission.entity.PermissionJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.permission.repository.PermissionJpaRepository;
import com.ryuqq.authhub.adapter.out.persistence.role.entity.RoleJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.role.repository.RoleJpaRepository;
import com.ryuqq.authhub.adapter.out.persistence.rolepermission.entity.RolePermissionJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.rolepermission.repository.RolePermissionJpaRepository;
import com.ryuqq.authhub.domain.permission.vo.PermissionType;
import com.ryuqq.authhub.domain.role.vo.RoleScope;
import com.ryuqq.authhub.domain.role.vo.RoleType;
import com.ryuqq.authhub.integration.common.base.E2ETestBase;
import com.ryuqq.authhub.integration.common.tag.TestTags;
import io.restassured.response.Response;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

/**
 * RolePermission API E2E 테스트.
 *
 * <p>역할-권한 관계 API의 전체 흐름을 검증합니다. REST API → Application → Domain → Repository → DB
 */
@Tag(TestTags.E2E)
@Tag(TestTags.ROLE)
class RolePermissionE2ETest extends E2ETestBase {

    private static final Instant FIXED_TIME = Instant.parse("2025-01-01T00:00:00Z");

    @Autowired private RolePermissionJpaRepository rolePermissionJpaRepository;

    @Autowired private RoleJpaRepository roleJpaRepository;

    @Autowired private PermissionJpaRepository permissionJpaRepository;

    private Long savedRoleId;
    private Long savedPermissionId1;
    private Long savedPermissionId2;

    @BeforeEach
    void setUp() {
        rolePermissionJpaRepository.deleteAll();
        roleJpaRepository.deleteAll();
        permissionJpaRepository.deleteAll();

        RoleJpaEntity role =
                RoleJpaEntity.of(
                        null,
                        null,
                        null,
                        "TEST_ROLE",
                        "테스트 역할",
                        "테스트",
                        RoleType.CUSTOM,
                        RoleScope.GLOBAL,
                        FIXED_TIME,
                        FIXED_TIME,
                        null);
        savedRoleId = roleJpaRepository.save(role).getRoleId();

        PermissionJpaEntity perm1 =
                PermissionJpaEntity.of(
                        null,
                        null,
                        "user:read",
                        "user",
                        "read",
                        null,
                        PermissionType.CUSTOM,
                        FIXED_TIME,
                        FIXED_TIME,
                        null);
        savedPermissionId1 = permissionJpaRepository.save(perm1).getPermissionId();

        PermissionJpaEntity perm2 =
                PermissionJpaEntity.of(
                        null,
                        null,
                        "user:write",
                        "user",
                        "write",
                        null,
                        PermissionType.CUSTOM,
                        FIXED_TIME,
                        FIXED_TIME,
                        null);
        savedPermissionId2 = permissionJpaRepository.save(perm2).getPermissionId();
    }

    @Nested
    @DisplayName("POST /api/v1/auth/roles/{roleId}/permissions - 역할에 권한 부여")
    class GrantRolePermissionTest {

        @Test
        @DisplayName("역할에 권한을 부여할 수 있다")
        void shouldGrantPermissionToRole() {
            // given
            Map<String, Object> request = Map.of("permissionIds", List.of(savedPermissionId1));
            String path = "/api/v1/auth/roles/" + savedRoleId + "/permissions";

            // when
            Response response = givenAuthenticated().body(request).when().post(path);

            // then
            response.then().statusCode(HttpStatus.CREATED.value()).body("success", equalTo(true));

            List<RolePermissionJpaEntity> found =
                    rolePermissionJpaRepository.findAll().stream()
                            .filter(rp -> rp.getRoleId().equals(savedRoleId))
                            .toList();
            assertThat(found).hasSize(1);
            assertThat(found.get(0).getPermissionId()).isEqualTo(savedPermissionId1);
        }

        @Test
        @DisplayName("역할에 여러 권한을 한 번에 부여할 수 있다")
        void shouldGrantMultiplePermissionsToRole() {
            // given
            Map<String, Object> request =
                    Map.of("permissionIds", List.of(savedPermissionId1, savedPermissionId2));
            String path = "/api/v1/auth/roles/" + savedRoleId + "/permissions";

            // when
            givenAuthenticated()
                    .body(request)
                    .when()
                    .post(path)
                    .then()
                    .statusCode(HttpStatus.CREATED.value());

            // then
            List<RolePermissionJpaEntity> found =
                    rolePermissionJpaRepository.findAll().stream()
                            .filter(rp -> rp.getRoleId().equals(savedRoleId))
                            .toList();
            assertThat(found).hasSize(2);
        }

        @Test
        @DisplayName("permissionIds가 비어있으면 400 에러를 반환한다")
        void shouldReturn400WhenPermissionIdsEmpty() {
            // given
            Map<String, Object> request = Map.of("permissionIds", List.of());
            String path = "/api/v1/auth/roles/" + savedRoleId + "/permissions";

            // when & then
            givenAuthenticated()
                    .body(request)
                    .when()
                    .post(path)
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
        @DisplayName("존재하지 않는 roleId로 권한 부여 시 404를 반환한다")
        void shouldReturn404WhenRoleNotFound() {
            // given
            Long nonExistentRoleId = 999999L;
            Map<String, Object> request = Map.of("permissionIds", List.of(savedPermissionId1));
            String path = "/api/v1/auth/roles/" + nonExistentRoleId + "/permissions";

            // when
            Response response = givenAuthenticated().body(request).when().post(path);

            // then - RFC 7807 ProblemDetail 형식 검증
            response.then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body("status", equalTo(404))
                    .body("title", notNullValue());
        }

        @Test
        @DisplayName("존재하지 않는 permissionId로 부여 시 404를 반환한다")
        void shouldReturn404WhenPermissionNotFound() {
            // given
            Long nonExistentPermissionId = 999999L;
            Map<String, Object> request = Map.of("permissionIds", List.of(nonExistentPermissionId));
            String path = "/api/v1/auth/roles/" + savedRoleId + "/permissions";

            // when
            Response response = givenAuthenticated().body(request).when().post(path);

            // then
            response.then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body("status", equalTo(404))
                    .body("title", notNullValue());
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/auth/roles/{roleId}/permissions - 역할에서 권한 제거")
    class RevokeRolePermissionTest {

        @Test
        @DisplayName("역할에서 권한을 제거할 수 있다")
        void shouldRevokePermissionFromRole() {
            // given - 먼저 권한 부여
            Map<String, Object> grantRequest = Map.of("permissionIds", List.of(savedPermissionId1));
            givenAuthenticated()
                    .body(grantRequest)
                    .when()
                    .post("/api/v1/auth/roles/" + savedRoleId + "/permissions")
                    .then()
                    .statusCode(HttpStatus.CREATED.value());

            Map<String, Object> revokeRequest =
                    Map.of("permissionIds", List.of(savedPermissionId1));
            String path = "/api/v1/auth/roles/" + savedRoleId + "/permissions";

            // when
            givenAuthenticated()
                    .body(revokeRequest)
                    .when()
                    .delete(path)
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());

            // then
            List<RolePermissionJpaEntity> found =
                    rolePermissionJpaRepository.findAll().stream()
                            .filter(rp -> rp.getRoleId().equals(savedRoleId))
                            .toList();
            assertThat(found).isEmpty();
        }
    }
}
