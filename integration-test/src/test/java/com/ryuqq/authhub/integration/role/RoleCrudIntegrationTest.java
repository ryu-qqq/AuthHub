package com.ryuqq.authhub.integration.role;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.authhub.adapter.in.rest.permission.dto.response.CreatePermissionApiResponse;
import com.ryuqq.authhub.adapter.in.rest.role.dto.response.CreateRoleApiResponse;
import com.ryuqq.authhub.adapter.in.rest.role.dto.response.RoleApiResponse;
import com.ryuqq.authhub.adapter.in.rest.tenant.dto.response.CreateTenantApiResponse;
import com.ryuqq.authhub.integration.config.BaseIntegrationTest;
import com.ryuqq.authhub.integration.permission.fixture.PermissionIntegrationTestFixture;
import com.ryuqq.authhub.integration.role.fixture.RoleIntegrationTestFixture;
import com.ryuqq.authhub.integration.tenant.fixture.TenantIntegrationTestFixture;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * 역할 CRUD 통합 테스트
 *
 * <p>역할 관련 API의 전체 레이어 통합 테스트를 수행합니다.
 *
 * @author Development Team
 * @since 1.0.0
 */
@DisplayName("역할 CRUD 통합 테스트")
class RoleCrudIntegrationTest extends BaseIntegrationTest {

    private String tenantId;

    private String tenantsUrl() {
        return apiV1() + "/auth/tenants";
    }

    private String rolesUrl() {
        return apiV1() + "/auth/roles";
    }

    private String permissionsUrl() {
        return apiV1() + "/auth/permissions";
    }

    @BeforeEach
    void setUp() {
        // 테스트용 테넌트 생성
        var tenantRequest = TenantIntegrationTestFixture.createTenantRequestWithUniqueName();
        ResponseEntity<ApiResponse<CreateTenantApiResponse>> tenantResponse =
                postForApiResponse(
                        tenantsUrl(), tenantRequest, new ParameterizedTypeReference<>() {});
        this.tenantId = tenantResponse.getBody().data().tenantId();
    }

    @Nested
    @DisplayName("역할 생성")
    class CreateRole {

        @Test
        @DisplayName("TC-001: 테넌트 역할 생성 - 성공")
        void createTenantRole_success() {
            // given
            var request =
                    RoleIntegrationTestFixture.createTenantRoleRequestWithUniqueName(tenantId);

            // when
            ResponseEntity<ApiResponse<CreateRoleApiResponse>> response =
                    postForApiResponse(rolesUrl(), request, new ParameterizedTypeReference<>() {});

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().data().roleId()).isNotBlank();
        }

        @Test
        @DisplayName("TC-002: 조직 역할 생성 - 성공")
        void createOrganizationRole_success() {
            // given
            var request = RoleIntegrationTestFixture.createOrganizationRoleRequest(tenantId);

            // when
            ResponseEntity<ApiResponse<CreateRoleApiResponse>> response =
                    postForApiResponse(rolesUrl(), request, new ParameterizedTypeReference<>() {});

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().data().roleId()).isNotBlank();
        }

        @Test
        @DisplayName("TC-003: 역할 생성 - 빈 이름으로 실패")
        void createRole_emptyName_returns400() {
            // given
            var request = RoleIntegrationTestFixture.createRoleRequestWithEmptyName(tenantId);

            // when
            ResponseEntity<Map<String, Object>> response =
                    restTemplate.exchange(
                            rolesUrl(),
                            HttpMethod.POST,
                            createHttpEntity(request),
                            new ParameterizedTypeReference<>() {});

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
    }

    @Nested
    @DisplayName("역할 조회")
    class GetRole {

        @Test
        @DisplayName("TC-004: 역할 조회 - 성공")
        void getRole_success() {
            // given - 먼저 역할 생성
            var createRequest =
                    RoleIntegrationTestFixture.createTenantRoleRequestWithUniqueName(tenantId);
            ResponseEntity<ApiResponse<CreateRoleApiResponse>> createResponse =
                    postForApiResponse(
                            rolesUrl(), createRequest, new ParameterizedTypeReference<>() {});
            String roleId = createResponse.getBody().data().roleId();

            // when - 역할 조회
            ResponseEntity<ApiResponse<RoleApiResponse>> response =
                    getForApiResponse(
                            rolesUrl() + "/" + roleId, new ParameterizedTypeReference<>() {});

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().data().roleId()).isEqualTo(roleId);
            assertThat(response.getBody().data().tenantId()).isEqualTo(tenantId);
        }

        @Test
        @DisplayName("TC-005: 존재하지 않는 역할 조회 - 404")
        void getRole_notFound_returns404() {
            // given - 존재하지 않는 랜덤 UUID
            String nonExistentUuid = java.util.UUID.randomUUID().toString();

            // when
            ResponseEntity<Map<String, Object>> response =
                    restTemplate.exchange(
                            rolesUrl() + "/" + nonExistentUuid,
                            HttpMethod.GET,
                            null,
                            new ParameterizedTypeReference<>() {});

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("역할 수정")
    class UpdateRole {

        @Test
        @DisplayName("TC-006: 역할 정보 수정 - 성공")
        void updateRole_success() {
            // given - 먼저 역할 생성
            var createRequest =
                    RoleIntegrationTestFixture.createTenantRoleRequestWithUniqueName(tenantId);
            ResponseEntity<ApiResponse<CreateRoleApiResponse>> createResponse =
                    postForApiResponse(
                            rolesUrl(), createRequest, new ParameterizedTypeReference<>() {});
            String roleId = createResponse.getBody().data().roleId();

            var updateRequest =
                    RoleIntegrationTestFixture.updateRoleRequest(
                            "UPDATED_ROLE", "Updated description");

            // when - 역할 수정
            ResponseEntity<Void> response =
                    restTemplate.exchange(
                            rolesUrl() + "/" + roleId,
                            HttpMethod.PUT,
                            createHttpEntity(updateRequest),
                            Void.class);

            // then - Controller가 200 OK 반환
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

            // verify - 수정된 정보 확인
            ResponseEntity<ApiResponse<RoleApiResponse>> getResponse =
                    getForApiResponse(
                            rolesUrl() + "/" + roleId, new ParameterizedTypeReference<>() {});
            assertThat(getResponse.getBody().data().name()).isEqualTo("UPDATED_ROLE");
            assertThat(getResponse.getBody().data().description()).isEqualTo("Updated description");
        }
    }

    @Nested
    @DisplayName("역할 권한 부여")
    class GrantRolePermission {

        @Test
        @DisplayName("TC-007: 역할에 권한 부여 - 성공")
        void grantPermissionToRole_success() {
            // given - 역할과 권한 생성
            var roleRequest =
                    RoleIntegrationTestFixture.createTenantRoleRequestWithUniqueName(tenantId);
            ResponseEntity<ApiResponse<CreateRoleApiResponse>> roleResponse =
                    postForApiResponse(
                            rolesUrl(), roleRequest, new ParameterizedTypeReference<>() {});
            String roleId = roleResponse.getBody().data().roleId();

            var permissionRequest =
                    PermissionIntegrationTestFixture.createPermissionRequestWithUniqueResource();
            ResponseEntity<ApiResponse<CreatePermissionApiResponse>> permissionResponse =
                    postForApiResponse(
                            permissionsUrl(),
                            permissionRequest,
                            new ParameterizedTypeReference<>() {});
            String permissionId = permissionResponse.getBody().data().permissionId();

            var grantRequest = RoleIntegrationTestFixture.grantPermissionRequest(permissionId);

            // when - 권한 부여
            ResponseEntity<Void> response =
                    restTemplate.exchange(
                            rolesUrl() + "/" + roleId + "/permissions",
                            HttpMethod.POST,
                            createHttpEntity(grantRequest),
                            Void.class);

            // then - Controller가 201 CREATED 반환 (권한 할당 생성)
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        }
    }

    @Nested
    @DisplayName("역할 삭제")
    class DeleteRole {

        @Test
        @DisplayName("TC-008: 역할 삭제 (Soft Delete) - 성공")
        void deleteRole_success() {
            // given - 먼저 역할 생성
            var createRequest =
                    RoleIntegrationTestFixture.createTenantRoleRequestWithUniqueName(tenantId);
            ResponseEntity<ApiResponse<CreateRoleApiResponse>> createResponse =
                    postForApiResponse(
                            rolesUrl(), createRequest, new ParameterizedTypeReference<>() {});
            String roleId = createResponse.getBody().data().roleId();

            // when - 역할 삭제 (Soft Delete)
            ResponseEntity<Void> response =
                    restTemplate.exchange(
                            rolesUrl() + "/" + roleId + "/delete",
                            HttpMethod.PATCH,
                            null,
                            Void.class);

            // then - Controller가 204 NO_CONTENT 반환
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        }

        @Test
        @DisplayName("TC-009: 삭제된 역할 조회 - 404")
        void getDeletedRole_returns404() {
            // given - 역할 생성 후 삭제
            var createRequest =
                    RoleIntegrationTestFixture.createTenantRoleRequestWithUniqueName(tenantId);
            ResponseEntity<ApiResponse<CreateRoleApiResponse>> createResponse =
                    postForApiResponse(
                            rolesUrl(), createRequest, new ParameterizedTypeReference<>() {});
            String roleId = createResponse.getBody().data().roleId();

            // 삭제 실행
            restTemplate.exchange(
                    rolesUrl() + "/" + roleId + "/delete", HttpMethod.PATCH, null, Void.class);

            // when - 삭제된 역할 조회
            ResponseEntity<Map<String, Object>> response =
                    restTemplate.exchange(
                            rolesUrl() + "/" + roleId,
                            HttpMethod.GET,
                            null,
                            new ParameterizedTypeReference<>() {});

            // then - 삭제된 엔티티는 조회 불가 (Role은 deleted 필터 적용됨)
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("역할 목록 조회")
    class ListRoles {

        @Test
        @DisplayName("TC-010: 역할 목록 조회 - 성공")
        void listRoles_success() {
            // given - 역할 2개 생성
            var request1 =
                    RoleIntegrationTestFixture.createTenantRoleRequestWithUniqueName(tenantId);
            var request2 =
                    RoleIntegrationTestFixture.createTenantRoleRequestWithUniqueName(tenantId);
            postForApiResponse(rolesUrl(), request1, new ParameterizedTypeReference<>() {});
            postForApiResponse(rolesUrl(), request2, new ParameterizedTypeReference<>() {});

            // when - 목록 조회
            ResponseEntity<ApiResponse<List<RoleApiResponse>>> response =
                    restTemplate.exchange(
                            rolesUrl() + "?tenantId=" + tenantId,
                            HttpMethod.GET,
                            null,
                            new ParameterizedTypeReference<>() {});

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().data()).isNotEmpty();
            // 목록 조회 API가 정상 동작하는지 확인
            assertThat(response.getBody().data().size()).isGreaterThanOrEqualTo(1);
        }
    }
}
