package com.ryuqq.authhub.integration.user;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.authhub.adapter.in.rest.organization.dto.response.CreateOrganizationApiResponse;
import com.ryuqq.authhub.adapter.in.rest.role.dto.response.CreateRoleApiResponse;
import com.ryuqq.authhub.adapter.in.rest.tenant.dto.response.CreateTenantApiResponse;
import com.ryuqq.authhub.adapter.in.rest.user.dto.response.CreateUserApiResponse;
import com.ryuqq.authhub.adapter.in.rest.user.dto.response.UserRoleApiResponse;
import com.ryuqq.authhub.integration.config.BaseIntegrationTest;
import com.ryuqq.authhub.integration.organization.fixture.OrganizationIntegrationTestFixture;
import com.ryuqq.authhub.integration.role.fixture.RoleIntegrationTestFixture;
import com.ryuqq.authhub.integration.tenant.fixture.TenantIntegrationTestFixture;
import com.ryuqq.authhub.integration.user.fixture.UserIntegrationTestFixture;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * 사용자-역할 관계 통합 테스트
 *
 * <p>사용자에게 역할을 할당/해제하는 API의 전체 레이어 통합 테스트를 수행합니다.
 *
 * @author Development Team
 * @since 1.0.0
 */
@DisplayName("사용자-역할 관계 통합 테스트")
class UserRoleIntegrationTest extends BaseIntegrationTest {

    private String tenantId;
    private String organizationId;
    private String userId;
    private String roleId;

    private String tenantsUrl() {
        return apiV1() + "/auth/tenants";
    }

    private String organizationsUrl() {
        return apiV1() + "/auth/organizations";
    }

    private String usersUrl() {
        return apiV1() + "/auth/users";
    }

    private String rolesUrl() {
        return apiV1() + "/auth/roles";
    }

    private String userRolesUrl(String userId) {
        return usersUrl() + "/" + userId + "/roles";
    }

    @BeforeEach
    void setUp() {
        // 테스트용 테넌트 생성
        var tenantRequest = TenantIntegrationTestFixture.createTenantRequestWithUniqueName();
        ResponseEntity<ApiResponse<CreateTenantApiResponse>> tenantResponse =
                postForApiResponse(
                        tenantsUrl(), tenantRequest, new ParameterizedTypeReference<>() {});
        this.tenantId = tenantResponse.getBody().data().tenantId();

        // 테스트용 조직 생성
        var orgRequest =
                OrganizationIntegrationTestFixture.createOrganizationRequestWithUniqueName(
                        tenantId);
        ResponseEntity<ApiResponse<CreateOrganizationApiResponse>> orgResponse =
                postForApiResponse(
                        organizationsUrl(), orgRequest, new ParameterizedTypeReference<>() {});
        this.organizationId = orgResponse.getBody().data().organizationId();

        // 테스트용 사용자 생성
        var userRequest =
                UserIntegrationTestFixture.createUserRequestWithUniqueIdentifier(
                        tenantId, organizationId);
        ResponseEntity<ApiResponse<CreateUserApiResponse>> userResponse =
                postForApiResponse(usersUrl(), userRequest, new ParameterizedTypeReference<>() {});
        this.userId = userResponse.getBody().data().userId();

        // 테스트용 역할 생성
        var roleRequest =
                RoleIntegrationTestFixture.createTenantRoleRequestWithUniqueName(tenantId);
        ResponseEntity<ApiResponse<CreateRoleApiResponse>> roleResponse =
                postForApiResponse(rolesUrl(), roleRequest, new ParameterizedTypeReference<>() {});
        this.roleId = roleResponse.getBody().data().roleId();
    }

    @Nested
    @DisplayName("역할 할당")
    class AssignRole {

        @Test
        @DisplayName("TC-001: 사용자에게 역할 할당 - 성공")
        void assignRole_success() {
            // given
            var request = UserIntegrationTestFixture.assignRoleRequest(roleId);

            // when
            ResponseEntity<ApiResponse<UserRoleApiResponse>> response =
                    postForApiResponse(
                            userRolesUrl(userId), request, new ParameterizedTypeReference<>() {});

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().data()).isNotNull();
        }

        /**
         * TC-002: 존재하지 않는 사용자에게 역할 할당
         *
         * <p>현재 아키텍처에서는 Long FK 전략을 사용하여 JPA 관계 어노테이션과 FK 제약조건이 없습니다. 따라서 존재하지 않는 사용자에게도 역할 할당이
         * 성공합니다 (orphan UUID 허용).
         *
         * <p>향후 비즈니스 요구사항에 따라 사용자 존재 검증 로직 추가 시 이 테스트를 404 기대로 변경해야 합니다.
         */
        @Test
        @DisplayName("TC-002: 사용자에게 역할 할당 - 존재하지 않는 사용자 (현재: 성공, Long FK 전략)")
        void assignRole_nonExistentUser_succeeds_dueToLongFkStrategy() {
            // given
            String nonExistentUserId = UUID.randomUUID().toString();
            var request = UserIntegrationTestFixture.assignRoleRequest(roleId);

            // when
            ResponseEntity<ApiResponse<UserRoleApiResponse>> response =
                    postForApiResponse(
                            userRolesUrl(nonExistentUserId),
                            request,
                            new ParameterizedTypeReference<>() {});

            // then - Long FK 전략으로 인해 FK 제약조건 없이 Insert 성공
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        }

        /**
         * TC-003: 존재하지 않는 역할을 사용자에게 할당
         *
         * <p>현재 아키텍처에서는 Long FK 전략을 사용하여 JPA 관계 어노테이션과 FK 제약조건이 없습니다. 따라서 존재하지 않는 역할도 할당이 성공합니다
         * (orphan UUID 허용).
         *
         * <p>향후 비즈니스 요구사항에 따라 역할 존재 검증 로직 추가 시 이 테스트를 404 기대로 변경해야 합니다.
         */
        @Test
        @DisplayName("TC-003: 사용자에게 역할 할당 - 존재하지 않는 역할 (현재: 성공, Long FK 전략)")
        void assignRole_nonExistentRole_succeeds_dueToLongFkStrategy() {
            // given
            String nonExistentRoleId = UUID.randomUUID().toString();
            var request = UserIntegrationTestFixture.assignRoleRequest(nonExistentRoleId);

            // when
            ResponseEntity<ApiResponse<UserRoleApiResponse>> response =
                    postForApiResponse(
                            userRolesUrl(userId), request, new ParameterizedTypeReference<>() {});

            // then - Long FK 전략으로 인해 FK 제약조건 없이 Insert 성공
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        }
    }

    @Nested
    @DisplayName("역할 해제")
    class RevokeRole {

        @Test
        @DisplayName("TC-004: 사용자 역할 해제 - 성공")
        void revokeRole_success() {
            // given - 먼저 역할 할당
            var assignRequest = UserIntegrationTestFixture.assignRoleRequest(roleId);
            postForApiResponse(
                    userRolesUrl(userId), assignRequest, new ParameterizedTypeReference<>() {});

            // when - 역할 해제
            ResponseEntity<Void> response =
                    restTemplate.exchange(
                            userRolesUrl(userId) + "/" + roleId + "/revoke",
                            HttpMethod.PATCH,
                            null,
                            Void.class);

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        }

        @Test
        @DisplayName("TC-005: 사용자 역할 해제 - 미할당된 역할")
        void revokeRole_notAssigned_returns404() {
            // given - 역할 할당 없이 바로 해제 시도
            String unassignedRoleId = roleId;

            // when
            ResponseEntity<Map<String, Object>> response =
                    restTemplate.exchange(
                            userRolesUrl(userId) + "/" + unassignedRoleId + "/revoke",
                            HttpMethod.PATCH,
                            null,
                            new ParameterizedTypeReference<>() {});

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("고급 시나리오")
    class AdvancedScenarios {

        @Test
        @DisplayName("TC-006: 사용자에게 여러 역할 할당 - 성공")
        void assignMultipleRoles_success() {
            // given - 두 번째 역할 생성
            var roleRequest2 =
                    RoleIntegrationTestFixture.createTenantRoleRequestWithUniqueName(tenantId);
            ResponseEntity<ApiResponse<CreateRoleApiResponse>> roleResponse2 =
                    postForApiResponse(
                            rolesUrl(), roleRequest2, new ParameterizedTypeReference<>() {});
            String roleId2 = roleResponse2.getBody().data().roleId();

            // when - 두 개의 역할을 차례로 할당
            var assignRequest1 = UserIntegrationTestFixture.assignRoleRequest(roleId);
            var assignRequest2 = UserIntegrationTestFixture.assignRoleRequest(roleId2);

            ResponseEntity<ApiResponse<UserRoleApiResponse>> response1 =
                    postForApiResponse(
                            userRolesUrl(userId),
                            assignRequest1,
                            new ParameterizedTypeReference<>() {});
            ResponseEntity<ApiResponse<UserRoleApiResponse>> response2 =
                    postForApiResponse(
                            userRolesUrl(userId),
                            assignRequest2,
                            new ParameterizedTypeReference<>() {});

            // then - 두 역할 모두 할당 성공
            assertThat(response1.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(response1.getBody().data()).isNotNull();
            assertThat(response2.getBody().data()).isNotNull();
        }

        @Test
        @DisplayName("TC-007: 여러 역할 할당 후 부분 해제 - 성공")
        void assignMultipleRolesThenRevokeOne_success() {
            // given - 두 번째 역할 생성 및 두 역할 모두 할당
            var roleRequest2 =
                    RoleIntegrationTestFixture.createTenantRoleRequestWithUniqueName(tenantId);
            ResponseEntity<ApiResponse<CreateRoleApiResponse>> roleResponse2 =
                    postForApiResponse(
                            rolesUrl(), roleRequest2, new ParameterizedTypeReference<>() {});
            String roleId2 = roleResponse2.getBody().data().roleId();

            var assignRequest1 = UserIntegrationTestFixture.assignRoleRequest(roleId);
            var assignRequest2 = UserIntegrationTestFixture.assignRoleRequest(roleId2);

            postForApiResponse(
                    userRolesUrl(userId), assignRequest1, new ParameterizedTypeReference<>() {});
            postForApiResponse(
                    userRolesUrl(userId), assignRequest2, new ParameterizedTypeReference<>() {});

            // when - 첫 번째 역할만 해제
            ResponseEntity<Void> revokeResponse =
                    restTemplate.exchange(
                            userRolesUrl(userId) + "/" + roleId + "/revoke",
                            HttpMethod.PATCH,
                            null,
                            Void.class);

            // then - 해제 성공
            assertThat(revokeResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

            // verify - 두 번째 역할은 여전히 해제 가능 (할당되어 있음)
            ResponseEntity<Void> secondRevokeResponse =
                    restTemplate.exchange(
                            userRolesUrl(userId) + "/" + roleId2 + "/revoke",
                            HttpMethod.PATCH,
                            null,
                            Void.class);
            assertThat(secondRevokeResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        }
    }
}
