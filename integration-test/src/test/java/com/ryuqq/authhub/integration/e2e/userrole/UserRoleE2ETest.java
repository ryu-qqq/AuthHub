package com.ryuqq.authhub.integration.e2e.userrole;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import com.ryuqq.authhub.adapter.out.persistence.organization.entity.OrganizationJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.organization.fixture.OrganizationJpaEntityFixture;
import com.ryuqq.authhub.adapter.out.persistence.organization.repository.OrganizationJpaRepository;
import com.ryuqq.authhub.adapter.out.persistence.role.entity.RoleJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.role.repository.RoleJpaRepository;
import com.ryuqq.authhub.adapter.out.persistence.tenant.entity.TenantJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.tenant.fixture.TenantJpaEntityFixture;
import com.ryuqq.authhub.adapter.out.persistence.tenant.repository.TenantJpaRepository;
import com.ryuqq.authhub.adapter.out.persistence.user.entity.UserJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.user.fixture.UserJpaEntityFixture;
import com.ryuqq.authhub.adapter.out.persistence.user.repository.UserJpaRepository;
import com.ryuqq.authhub.adapter.out.persistence.userrole.entity.UserRoleJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.userrole.repository.UserRoleJpaRepository;
import com.ryuqq.authhub.domain.role.vo.RoleType;
import com.ryuqq.authhub.domain.user.vo.UserStatus;
import com.ryuqq.authhub.integration.common.base.E2ETestBase;
import com.ryuqq.authhub.integration.common.tag.TestTags;
import io.restassured.response.Response;
import java.util.List;
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
 * UserRole API E2E 테스트.
 *
 * <p>사용자-역할 관계 API의 전체 흐름을 검증합니다. REST API → Application → Domain → Repository → DB
 */
@Tag(TestTags.E2E)
@Tag(TestTags.USER)
class UserRoleE2ETest extends E2ETestBase {

    @Autowired private UserRoleJpaRepository userRoleJpaRepository;

    @Autowired private UserJpaRepository userJpaRepository;

    @Autowired private RoleJpaRepository roleJpaRepository;

    @Autowired private OrganizationJpaRepository organizationJpaRepository;

    @Autowired private TenantJpaRepository tenantJpaRepository;

    private String savedUserId;
    private Long savedRoleId;
    private Long savedRoleId2;

    @BeforeEach
    void setUp() {
        userRoleJpaRepository.deleteAll();
        userJpaRepository.deleteAll();
        roleJpaRepository.deleteAll();
        organizationJpaRepository.deleteAll();
        tenantJpaRepository.deleteAll();

        TenantJpaEntity tenant = tenantJpaRepository.save(TenantJpaEntityFixture.create());
        OrganizationJpaEntity org =
                organizationJpaRepository.save(
                        OrganizationJpaEntityFixture.createWithTenant(tenant.getTenantId()));

        String userId = UUID.randomUUID().toString();
        UserJpaEntity user =
                UserJpaEntity.of(
                        userId,
                        org.getOrganizationId(),
                        "userrole@example.com",
                        "010-1234-5678",
                        "$2a$10$hashedpassword",
                        UserStatus.ACTIVE,
                        UserJpaEntityFixture.fixedTime(),
                        UserJpaEntityFixture.fixedTime(),
                        null);
        savedUserId = userJpaRepository.save(user).getUserId();

        RoleJpaEntity role =
                RoleJpaEntity.of(
                        null,
                        null,
                        "ASSIGN_ROLE",
                        "할당 역할",
                        null,
                        RoleType.CUSTOM,
                        UserJpaEntityFixture.fixedTime(),
                        UserJpaEntityFixture.fixedTime(),
                        null);
        savedRoleId = roleJpaRepository.save(role).getRoleId();

        RoleJpaEntity role2 =
                RoleJpaEntity.of(
                        null,
                        null,
                        "REVOKE_ROLE",
                        "철회 역할",
                        null,
                        RoleType.CUSTOM,
                        UserJpaEntityFixture.fixedTime(),
                        UserJpaEntityFixture.fixedTime(),
                        null);
        savedRoleId2 = roleJpaRepository.save(role2).getRoleId();
    }

    @Nested
    @DisplayName("POST /api/v1/auth/users/{userId}/roles - 사용자에게 역할 할당")
    class AssignUserRoleTest {

        @Test
        @DisplayName("사용자에게 역할을 할당할 수 있다")
        void shouldAssignRoleToUser() {
            // given
            Map<String, Object> request = Map.of("roleIds", List.of(savedRoleId));
            String path = "/api/v1/auth/users/" + savedUserId + "/roles";

            // when
            Response response = givenAuthenticated().body(request).when().post(path);

            // then
            response.then().statusCode(HttpStatus.CREATED.value()).body("success", equalTo(true));

            List<UserRoleJpaEntity> found =
                    userRoleJpaRepository.findAll().stream()
                            .filter(ur -> ur.getUserId().equals(savedUserId))
                            .toList();
            assertThat(found).hasSize(1);
            assertThat(found.get(0).getRoleId()).isEqualTo(savedRoleId);
        }

        @Test
        @DisplayName("사용자에게 여러 역할을 한 번에 할당할 수 있다")
        void shouldAssignMultipleRolesToUser() {
            // given
            Map<String, Object> request = Map.of("roleIds", List.of(savedRoleId, savedRoleId2));
            String path = "/api/v1/auth/users/" + savedUserId + "/roles";

            // when
            givenAuthenticated()
                    .body(request)
                    .when()
                    .post(path)
                    .then()
                    .statusCode(HttpStatus.CREATED.value());

            // then
            List<UserRoleJpaEntity> found =
                    userRoleJpaRepository.findAll().stream()
                            .filter(ur -> ur.getUserId().equals(savedUserId))
                            .toList();
            assertThat(found).hasSize(2);
        }

        @Test
        @DisplayName("roleIds가 비어있으면 400 에러를 반환한다")
        void shouldReturn400WhenRoleIdsEmpty() {
            // given
            Map<String, Object> request = Map.of("roleIds", List.of());
            String path = "/api/v1/auth/users/" + savedUserId + "/roles";

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
        @DisplayName("존재하지 않는 userId로 역할 할당 시 404를 반환한다")
        void shouldReturn404WhenUserNotFound() {
            // given
            String nonExistentUserId = UUID.randomUUID().toString();
            Map<String, Object> request = Map.of("roleIds", List.of(savedRoleId));
            String path = "/api/v1/auth/users/" + nonExistentUserId + "/roles";

            // when
            Response response = givenAuthenticated().body(request).when().post(path);

            // then - RFC 7807 ProblemDetail 형식 검증
            response.then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body("status", equalTo(404))
                    .body("title", notNullValue());
        }

        @Test
        @DisplayName("존재하지 않는 roleId로 할당 시 404를 반환한다")
        void shouldReturn404WhenRoleNotFound() {
            // given
            Long nonExistentRoleId = 999999L;
            Map<String, Object> request = Map.of("roleIds", List.of(nonExistentRoleId));
            String path = "/api/v1/auth/users/" + savedUserId + "/roles";

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
    @DisplayName("DELETE /api/v1/auth/users/{userId}/roles - 사용자로부터 역할 철회")
    class RevokeUserRoleTest {

        @Test
        @DisplayName("사용자로부터 역할을 철회할 수 있다")
        void shouldRevokeRoleFromUser() {
            // given - 먼저 역할 할당
            Map<String, Object> assignRequest = Map.of("roleIds", List.of(savedRoleId));
            givenAuthenticated()
                    .body(assignRequest)
                    .when()
                    .post("/api/v1/auth/users/" + savedUserId + "/roles")
                    .then()
                    .statusCode(HttpStatus.CREATED.value());

            Map<String, Object> revokeRequest = Map.of("roleIds", List.of(savedRoleId));
            String path = "/api/v1/auth/users/" + savedUserId + "/roles";

            // when
            givenAuthenticated()
                    .body(revokeRequest)
                    .when()
                    .delete(path)
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());

            // then
            List<UserRoleJpaEntity> found =
                    userRoleJpaRepository.findAll().stream()
                            .filter(ur -> ur.getUserId().equals(savedUserId))
                            .toList();
            assertThat(found).isEmpty();
        }
    }
}
