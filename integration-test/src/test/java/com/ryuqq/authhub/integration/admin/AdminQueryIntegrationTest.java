package com.ryuqq.authhub.integration.admin;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.authhub.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.authhub.adapter.in.rest.organization.dto.response.CreateOrganizationApiResponse;
import com.ryuqq.authhub.adapter.in.rest.organization.dto.response.OrganizationDetailApiResponse;
import com.ryuqq.authhub.adapter.in.rest.organization.dto.response.OrganizationSummaryApiResponse;
import com.ryuqq.authhub.adapter.in.rest.role.dto.response.CreateRoleApiResponse;
import com.ryuqq.authhub.adapter.in.rest.role.dto.response.RoleDetailApiResponse;
import com.ryuqq.authhub.adapter.in.rest.role.dto.response.RoleSummaryApiResponse;
import com.ryuqq.authhub.adapter.in.rest.tenant.dto.response.CreateTenantApiResponse;
import com.ryuqq.authhub.adapter.in.rest.tenant.dto.response.TenantDetailApiResponse;
import com.ryuqq.authhub.adapter.in.rest.tenant.dto.response.TenantSummaryApiResponse;
import com.ryuqq.authhub.adapter.in.rest.user.dto.response.CreateUserApiResponse;
import com.ryuqq.authhub.adapter.in.rest.user.dto.response.UserDetailApiResponse;
import com.ryuqq.authhub.adapter.in.rest.user.dto.response.UserSummaryApiResponse;
import com.ryuqq.authhub.integration.config.BaseIntegrationTest;
import com.ryuqq.authhub.integration.organization.fixture.OrganizationIntegrationTestFixture;
import com.ryuqq.authhub.integration.role.fixture.RoleIntegrationTestFixture;
import com.ryuqq.authhub.integration.tenant.fixture.TenantIntegrationTestFixture;
import com.ryuqq.authhub.integration.user.fixture.UserIntegrationTestFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Admin Query API 통합 테스트
 *
 * <p>Admin 전용 조회 API의 전체 레이어 통합 테스트를 수행합니다.
 *
 * <p><strong>테스트 대상 엔드포인트:</strong>
 *
 * <ul>
 *   <li>User: /admin/search, /{id}/admin/detail
 *   <li>Role: /admin/search, /{id}/admin/detail
 *   <li>Tenant: /admin/search, /{id}/admin/detail
 *   <li>Organization: /admin/search, /{id}/admin/detail
 * </ul>
 *
 * @author Development Team
 * @since 1.0.0
 */
@DisplayName("Admin Query API 통합 테스트")
class AdminQueryIntegrationTest extends BaseIntegrationTest {

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

        // 테스트용 역할 생성
        var roleRequest =
                RoleIntegrationTestFixture.createTenantRoleRequestWithUniqueName(tenantId);
        ResponseEntity<ApiResponse<CreateRoleApiResponse>> roleResponse =
                postForApiResponse(rolesUrl(), roleRequest, new ParameterizedTypeReference<>() {});
        this.roleId = roleResponse.getBody().data().roleId();

        // 테스트용 사용자 생성
        var userRequest =
                UserIntegrationTestFixture.createUserRequestWithUniqueIdentifier(
                        tenantId, organizationId);
        ResponseEntity<ApiResponse<CreateUserApiResponse>> userResponse =
                postForApiResponse(usersUrl(), userRequest, new ParameterizedTypeReference<>() {});
        this.userId = userResponse.getBody().data().userId();
    }

    @Nested
    @DisplayName("User Admin Query API")
    class UserAdminQueryTest {

        @Test
        @DisplayName("TC-AQ-001: 사용자 Admin 검색 - 성공")
        void searchUsersAdmin_success() {
            // when
            ResponseEntity<PageApiResponse<UserSummaryApiResponse>> response =
                    restTemplate.exchange(
                            usersUrl() + "/admin/search?page=0&size=20",
                            HttpMethod.GET,
                            null,
                            new ParameterizedTypeReference<>() {});

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().content()).isNotNull();
        }

        @Test
        @DisplayName("TC-AQ-002: 사용자 Admin 검색 - 테넌트 필터 적용")
        void searchUsersAdmin_withTenantFilter() {
            // when
            ResponseEntity<PageApiResponse<UserSummaryApiResponse>> response =
                    restTemplate.exchange(
                            usersUrl() + "/admin/search?tenantId=" + tenantId + "&page=0&size=20",
                            HttpMethod.GET,
                            null,
                            new ParameterizedTypeReference<>() {});

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().content()).isNotEmpty();
        }

        @Test
        @DisplayName("TC-AQ-003: 사용자 Admin 상세 조회 - 성공")
        void getUserDetail_success() {
            // when
            ResponseEntity<ApiResponse<UserDetailApiResponse>> response =
                    restTemplate.exchange(
                            usersUrl() + "/" + userId + "/admin/detail",
                            HttpMethod.GET,
                            null,
                            new ParameterizedTypeReference<>() {});

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().data()).isNotNull();
            assertThat(response.getBody().data().userId().toString()).isEqualTo(userId);
            assertThat(response.getBody().data().tenantName()).isNotNull();
            assertThat(response.getBody().data().organizationName()).isNotNull();
        }

        @Test
        @DisplayName("TC-AQ-004: 존재하지 않는 사용자 Admin 상세 조회 - 404")
        void getUserDetail_notFound() {
            // given
            String nonExistentId = java.util.UUID.randomUUID().toString();

            // when
            ResponseEntity<Object> response =
                    restTemplate.exchange(
                            usersUrl() + "/" + nonExistentId + "/admin/detail",
                            HttpMethod.GET,
                            null,
                            new ParameterizedTypeReference<>() {});

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("Role Admin Query API")
    class RoleAdminQueryTest {

        @Test
        @DisplayName("TC-AQ-005: 역할 Admin 검색 - 성공")
        void searchRolesAdmin_success() {
            // when
            ResponseEntity<PageApiResponse<RoleSummaryApiResponse>> response =
                    restTemplate.exchange(
                            rolesUrl() + "/admin/search?page=0&size=20",
                            HttpMethod.GET,
                            null,
                            new ParameterizedTypeReference<>() {});

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().content()).isNotNull();
        }

        @Test
        @DisplayName("TC-AQ-006: 역할 Admin 검색 - 테넌트 필터 적용")
        void searchRolesAdmin_withTenantFilter() {
            // when
            ResponseEntity<PageApiResponse<RoleSummaryApiResponse>> response =
                    restTemplate.exchange(
                            rolesUrl() + "/admin/search?tenantId=" + tenantId + "&page=0&size=20",
                            HttpMethod.GET,
                            null,
                            new ParameterizedTypeReference<>() {});

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().content()).isNotEmpty();
        }

        @Test
        @DisplayName("TC-AQ-007: 역할 Admin 상세 조회 - 성공")
        void getRoleDetail_success() {
            // when
            ResponseEntity<ApiResponse<RoleDetailApiResponse>> response =
                    restTemplate.exchange(
                            rolesUrl() + "/" + roleId + "/admin/detail",
                            HttpMethod.GET,
                            null,
                            new ParameterizedTypeReference<>() {});

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().data()).isNotNull();
            assertThat(response.getBody().data().roleId().toString()).isEqualTo(roleId);
            assertThat(response.getBody().data().tenantName()).isNotNull();
        }

        @Test
        @DisplayName("TC-AQ-008: 존재하지 않는 역할 Admin 상세 조회 - 404")
        void getRoleDetail_notFound() {
            // given
            String nonExistentId = java.util.UUID.randomUUID().toString();

            // when
            ResponseEntity<Object> response =
                    restTemplate.exchange(
                            rolesUrl() + "/" + nonExistentId + "/admin/detail",
                            HttpMethod.GET,
                            null,
                            new ParameterizedTypeReference<>() {});

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("Tenant Admin Query API")
    class TenantAdminQueryTest {

        @Test
        @DisplayName("TC-AQ-009: 테넌트 Admin 검색 - 성공")
        void searchTenantsAdmin_success() {
            // when
            ResponseEntity<ApiResponse<PageApiResponse<TenantSummaryApiResponse>>> response =
                    restTemplate.exchange(
                            tenantsUrl() + "/admin/search?page=0&size=20",
                            HttpMethod.GET,
                            null,
                            new ParameterizedTypeReference<>() {});

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().data()).isNotNull();
            assertThat(response.getBody().data().content()).isNotEmpty();
        }

        @Test
        @DisplayName("TC-AQ-010: 테넌트 Admin 검색 - 상태 필터 적용")
        void searchTenantsAdmin_withStatusFilter() {
            // when
            ResponseEntity<ApiResponse<PageApiResponse<TenantSummaryApiResponse>>> response =
                    restTemplate.exchange(
                            tenantsUrl() + "/admin/search?status=ACTIVE&page=0&size=20",
                            HttpMethod.GET,
                            null,
                            new ParameterizedTypeReference<>() {});

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().data()).isNotNull();
        }

        @Test
        @DisplayName("TC-AQ-011: 테넌트 Admin 상세 조회 - 성공")
        void getTenantDetail_success() {
            // when
            ResponseEntity<ApiResponse<TenantDetailApiResponse>> response =
                    restTemplate.exchange(
                            tenantsUrl() + "/" + tenantId + "/admin/detail",
                            HttpMethod.GET,
                            null,
                            new ParameterizedTypeReference<>() {});

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().data()).isNotNull();
            assertThat(response.getBody().data().tenantId().toString()).isEqualTo(tenantId);
            assertThat(response.getBody().data().organizationCount()).isGreaterThanOrEqualTo(0);
        }

        @Test
        @DisplayName("TC-AQ-012: 존재하지 않는 테넌트 Admin 상세 조회 - 404")
        void getTenantDetail_notFound() {
            // given
            String nonExistentId = java.util.UUID.randomUUID().toString();

            // when
            ResponseEntity<Object> response =
                    restTemplate.exchange(
                            tenantsUrl() + "/" + nonExistentId + "/admin/detail",
                            HttpMethod.GET,
                            null,
                            new ParameterizedTypeReference<>() {});

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("Organization Admin Query API")
    class OrganizationAdminQueryTest {

        @Test
        @DisplayName("TC-AQ-013: 조직 Admin 검색 - 성공")
        void searchOrganizationsAdmin_success() {
            // when
            ResponseEntity<ApiResponse<PageApiResponse<OrganizationSummaryApiResponse>>> response =
                    restTemplate.exchange(
                            organizationsUrl() + "/admin/search?page=0&size=20",
                            HttpMethod.GET,
                            null,
                            new ParameterizedTypeReference<>() {});

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().data()).isNotNull();
            assertThat(response.getBody().data().content()).isNotEmpty();
        }

        @Test
        @DisplayName("TC-AQ-014: 조직 Admin 검색 - 테넌트 필터 적용")
        void searchOrganizationsAdmin_withTenantFilter() {
            // when
            ResponseEntity<ApiResponse<PageApiResponse<OrganizationSummaryApiResponse>>> response =
                    restTemplate.exchange(
                            organizationsUrl()
                                    + "/admin/search?tenantId="
                                    + tenantId
                                    + "&page=0&size=20",
                            HttpMethod.GET,
                            null,
                            new ParameterizedTypeReference<>() {});

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().data()).isNotNull();
            assertThat(response.getBody().data().content()).isNotEmpty();
        }

        @Test
        @DisplayName("TC-AQ-015: 조직 Admin 상세 조회 - 성공")
        void getOrganizationDetail_success() {
            // when
            ResponseEntity<ApiResponse<OrganizationDetailApiResponse>> response =
                    restTemplate.exchange(
                            organizationsUrl() + "/" + organizationId + "/admin/detail",
                            HttpMethod.GET,
                            null,
                            new ParameterizedTypeReference<>() {});

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().data()).isNotNull();
            assertThat(response.getBody().data().organizationId().toString())
                    .isEqualTo(organizationId);
            assertThat(response.getBody().data().tenantName()).isNotNull();
            assertThat(response.getBody().data().userCount()).isGreaterThanOrEqualTo(0);
        }

        @Test
        @DisplayName("TC-AQ-016: 존재하지 않는 조직 Admin 상세 조회 - 404")
        void getOrganizationDetail_notFound() {
            // given
            String nonExistentId = java.util.UUID.randomUUID().toString();

            // when
            ResponseEntity<Object> response =
                    restTemplate.exchange(
                            organizationsUrl() + "/" + nonExistentId + "/admin/detail",
                            HttpMethod.GET,
                            null,
                            new ParameterizedTypeReference<>() {});

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }
    }
}
