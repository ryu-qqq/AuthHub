package com.ryuqq.authhub.integration.e2e.sdk;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.authhub.adapter.out.persistence.organization.entity.OrganizationJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.organization.fixture.OrganizationJpaEntityFixture;
import com.ryuqq.authhub.adapter.out.persistence.organization.repository.OrganizationJpaRepository;
import com.ryuqq.authhub.adapter.out.persistence.permission.entity.PermissionJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.permission.repository.PermissionJpaRepository;
import com.ryuqq.authhub.adapter.out.persistence.permissionendpoint.entity.PermissionEndpointJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.permissionendpoint.repository.PermissionEndpointJpaRepository;
import com.ryuqq.authhub.adapter.out.persistence.role.entity.RoleJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.role.repository.RoleJpaRepository;
import com.ryuqq.authhub.adapter.out.persistence.rolepermission.repository.RolePermissionJpaRepository;
import com.ryuqq.authhub.adapter.out.persistence.service.entity.ServiceJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.service.fixture.ServiceJpaEntityFixture;
import com.ryuqq.authhub.adapter.out.persistence.service.repository.ServiceJpaRepository;
import com.ryuqq.authhub.adapter.out.persistence.tenant.entity.TenantJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.tenant.fixture.TenantJpaEntityFixture;
import com.ryuqq.authhub.adapter.out.persistence.tenant.repository.TenantJpaRepository;
import com.ryuqq.authhub.adapter.out.persistence.user.entity.UserJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.user.repository.UserJpaRepository;
import com.ryuqq.authhub.adapter.out.persistence.userrole.repository.UserRoleJpaRepository;
import com.ryuqq.authhub.domain.permission.vo.PermissionType;
import com.ryuqq.authhub.domain.permissionendpoint.vo.HttpMethod;
import com.ryuqq.authhub.domain.role.vo.RoleScope;
import com.ryuqq.authhub.domain.role.vo.RoleType;
import com.ryuqq.authhub.integration.common.base.E2ETestBase;
import com.ryuqq.authhub.integration.common.tag.TestTags;
import com.ryuqq.authhub.sdk.client.AuthHubClient;
import com.ryuqq.authhub.sdk.client.GatewayClient;
import com.ryuqq.authhub.sdk.exception.AuthHubException;
import com.ryuqq.authhub.sdk.exception.AuthHubNotFoundException;
import com.ryuqq.authhub.sdk.model.common.ApiResponse;
import com.ryuqq.authhub.sdk.model.internal.EndpointPermissionSpecList;
import com.ryuqq.authhub.sdk.model.internal.TenantConfig;
import com.ryuqq.authhub.sdk.model.internal.UserPermissions;
import com.ryuqq.authhub.sdk.model.onboarding.TenantOnboardingRequest;
import com.ryuqq.authhub.sdk.model.onboarding.TenantOnboardingResponse;
import com.ryuqq.authhub.sdk.model.user.CreateUserWithRolesRequest;
import com.ryuqq.authhub.sdk.model.user.CreateUserWithRolesResponse;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * SDK Internal API E2E 통합 테스트.
 *
 * <p>SDK 클라이언트(AuthHubClient, GatewayClient)가 실제 서버(Internal API)를 정상 호출하는지 검증합니다.
 *
 * <p>검증 대상 SDK API (6개):
 *
 * <ul>
 *   <li>GatewayClient.internal().getPermissionSpec() - 권한 스펙 조회
 *   <li>GatewayClient.internal().getTenantConfig(tenantId) - 테넌트 설정 조회
 *   <li>GatewayClient.internal().getUserPermissions(userId) - 사용자 권한 조회
 *   <li>AuthHubClient.onboarding().onboard(request, key) - 테넌트 온보딩
 *   <li>AuthHubClient.user().createUserWithRoles(request) - 사용자 등록
 *   <li>EndpointSync (직접 HTTP 호출) - 엔드포인트 동기화
 * </ul>
 */
@Tag(TestTags.E2E)
@Tag("sdk")
@DisplayName("SDK Internal API E2E 통합 테스트")
class SdkInternalApiE2ETest extends E2ETestBase {

    private static final Instant FIXED_TIME = Instant.parse("2025-01-01T00:00:00Z");

    // SDK Clients
    private AuthHubClient authHubClient;
    private GatewayClient gatewayClient;

    // Repository Dependencies
    @Autowired private TenantJpaRepository tenantRepository;
    @Autowired private OrganizationJpaRepository organizationRepository;
    @Autowired private UserJpaRepository userRepository;
    @Autowired private UserRoleJpaRepository userRoleRepository;
    @Autowired private RoleJpaRepository roleRepository;
    @Autowired private RolePermissionJpaRepository rolePermissionRepository;
    @Autowired private PermissionJpaRepository permissionRepository;
    @Autowired private PermissionEndpointJpaRepository permissionEndpointRepository;
    @Autowired private ServiceJpaRepository serviceRepository;

    // Test Data
    private TenantJpaEntity savedTenant;
    private OrganizationJpaEntity savedOrganization;
    private ServiceJpaEntity savedService;

    @BeforeEach
    void setUp() {
        // 1. SDK 클라이언트 생성 (포트는 E2ETestBase의 @LocalServerPort로 주입됨)
        String baseUrl = "http://localhost:" + port;

        authHubClient =
                AuthHubClient.builder()
                        .baseUrl(baseUrl)
                        .serviceToken(DEFAULT_SERVICE_TOKEN)
                        .build();

        gatewayClient =
                GatewayClient.builder()
                        .baseUrl(baseUrl)
                        .serviceName(DEFAULT_SERVICE_NAME)
                        .serviceToken(DEFAULT_SERVICE_TOKEN)
                        .build();

        // 2. 자식 엔티티부터 삭제 (FK 순서)
        permissionEndpointRepository.deleteAll();
        rolePermissionRepository.deleteAll();
        userRoleRepository.deleteAll();
        permissionRepository.deleteAll();
        roleRepository.deleteAll();
        userRepository.deleteAll();
        organizationRepository.deleteAll();
        tenantRepository.deleteAll();
        serviceRepository.deleteAll();

        // 3. 부모 엔티티 생성 (공통 사전 데이터)
        savedTenant = tenantRepository.save(TenantJpaEntityFixture.create());
        savedOrganization =
                organizationRepository.save(
                        OrganizationJpaEntityFixture.createWithTenant(savedTenant.getTenantId()));
        savedService =
                serviceRepository.save(ServiceJpaEntityFixture.createWithCode("SVC_MARKETPLACE"));
    }

    // ========================================
    // 1. GatewayClient - InternalApi.getPermissionSpec()
    // ========================================

    @Nested
    @DisplayName("GatewayClient.internal().getPermissionSpec() - 권한 스펙 조회")
    class GetPermissionSpecViaSdkTest {

        @Test
        @DisplayName("정상 조회 - 데이터 존재 시 endpoints 반환")
        void shouldGetPermissionSpecSuccessfully() {
            // given - 사전 데이터 생성
            PermissionJpaEntity permission = createPermission(null, "user:read", "user", "read");
            createPermissionEndpoint(
                    permission.getPermissionId(),
                    "user-service",
                    "/api/v1/users",
                    HttpMethod.GET,
                    false);

            // when
            ApiResponse<EndpointPermissionSpecList> response =
                    gatewayClient.internal().getPermissionSpec();

            // then
            assertThat(response).isNotNull();
            assertThat(response.success()).isTrue();
            assertThat(response.data()).isNotNull();
            assertThat(response.data().endpoints()).isNotEmpty();
            assertThat(response.data().version()).isNotNull();
            assertThat(response.data().updatedAt()).isNotNull();
        }

        @Test
        @DisplayName("빈 결과 반환 - 데이터 없을 때")
        void shouldReturnEmptySpecList() {
            // given - 데이터 없음

            // when
            ApiResponse<EndpointPermissionSpecList> response =
                    gatewayClient.internal().getPermissionSpec();

            // then
            assertThat(response).isNotNull();
            assertThat(response.success()).isTrue();
            assertThat(response.data().endpoints()).isEmpty();
        }
    }

    // ========================================
    // 2. GatewayClient - InternalApi.getTenantConfig()
    // ========================================

    @Nested
    @DisplayName("GatewayClient.internal().getTenantConfig() - 테넌트 설정 조회")
    class GetTenantConfigViaSdkTest {

        @Test
        @DisplayName("존재하는 테넌트 조회 - 정상 반환")
        void shouldGetTenantConfigSuccessfully() {
            // when
            ApiResponse<TenantConfig> response =
                    gatewayClient.internal().getTenantConfig(savedTenant.getTenantId());

            // then
            assertThat(response).isNotNull();
            assertThat(response.success()).isTrue();

            TenantConfig config = response.data();
            assertThat(config.tenantId()).isEqualTo(savedTenant.getTenantId());
            assertThat(config.name()).isEqualTo(savedTenant.getName());
            assertThat(config.status()).isEqualTo("ACTIVE");
            assertThat(config.active()).isTrue();
        }

        @Test
        @DisplayName("존재하지 않는 테넌트 조회 - AuthHubNotFoundException 발생")
        void shouldThrowNotFoundForNonExistentTenant() {
            // when & then
            assertThatThrownBy(
                            () ->
                                    gatewayClient
                                            .internal()
                                            .getTenantConfig("non-existent-tenant-id"))
                    .isInstanceOf(AuthHubNotFoundException.class);
        }
    }

    // ========================================
    // 3. GatewayClient - InternalApi.getUserPermissions()
    // ========================================

    @Nested
    @DisplayName("GatewayClient.internal().getUserPermissions() - 사용자 권한 조회")
    class GetUserPermissionsViaSdkTest {

        @Test
        @DisplayName("역할 있는 사용자 - roles/permissions 반환")
        void shouldGetUserPermissionsSuccessfully() {
            // given
            UserJpaEntity user =
                    createUser(savedOrganization.getOrganizationId(), "admin@test.com");

            RoleJpaEntity adminRole = createRole(null, null, "ADMIN", RoleScope.GLOBAL);
            PermissionJpaEntity readPerm = createPermission(null, "user:read", "user", "read");
            PermissionJpaEntity writePerm = createPermission(null, "user:write", "user", "write");

            createUserRole(user.getUserId(), adminRole.getRoleId());
            createRolePermission(adminRole.getRoleId(), readPerm.getPermissionId());
            createRolePermission(adminRole.getRoleId(), writePerm.getPermissionId());

            // when
            ApiResponse<UserPermissions> response =
                    gatewayClient.internal().getUserPermissions(user.getUserId());

            // then
            assertThat(response).isNotNull();
            assertThat(response.success()).isTrue();

            UserPermissions permissions = response.data();
            assertThat(permissions.userId()).isEqualTo(user.getUserId());
            assertThat(permissions.roles()).contains("ADMIN");
            assertThat(permissions.permissions()).contains("user:read", "user:write");
        }

        @Test
        @DisplayName("역할 없는 사용자 - 빈 배열 반환")
        void shouldReturnEmptyForUserWithoutRoles() {
            // given - 역할 없는 사용자
            UserJpaEntity user =
                    createUser(savedOrganization.getOrganizationId(), "norole@test.com");

            // when
            ApiResponse<UserPermissions> response =
                    gatewayClient.internal().getUserPermissions(user.getUserId());

            // then
            assertThat(response).isNotNull();
            assertThat(response.success()).isTrue();
            assertThat(response.data().roles()).isEmpty();
            assertThat(response.data().permissions()).isEmpty();
        }
    }

    // ========================================
    // 4. AuthHubClient - OnboardingApi.onboard()
    // ========================================

    @Nested
    @DisplayName("AuthHubClient.onboarding().onboard() - 테넌트 온보딩")
    class OnboardingViaSdkTest {

        @Test
        @DisplayName("정상 온보딩 - tenantId, organizationId 반환 (201)")
        void shouldOnboardSuccessfully() {
            // given
            String idempotencyKey = UUID.randomUUID().toString();
            TenantOnboardingRequest request =
                    new TenantOnboardingRequest(
                            "sdk-tenant-" + System.currentTimeMillis(), "sdk-default-org");

            // when
            ApiResponse<TenantOnboardingResponse> response =
                    authHubClient.onboarding().onboard(request, idempotencyKey);

            // then
            assertThat(response).isNotNull();
            assertThat(response.success()).isTrue();
            assertThat(response.data().tenantId()).isNotNull();
            assertThat(response.data().organizationId()).isNotNull();

            // DB 검증
            assertThat(tenantRepository.findById(response.data().tenantId())).isPresent();
            assertThat(organizationRepository.findById(response.data().organizationId()))
                    .isPresent();
        }

        @Test
        @DisplayName("중복 테넌트 이름 - AuthHubException(409) 발생")
        void shouldThrowConflictForDuplicateTenantName() {
            // given - 기존 테넌트와 같은 이름
            String duplicateName = savedTenant.getName();
            TenantOnboardingRequest request = new TenantOnboardingRequest(duplicateName, "new-org");
            String idempotencyKey = UUID.randomUUID().toString();

            // when & then
            assertThatThrownBy(() -> authHubClient.onboarding().onboard(request, idempotencyKey))
                    .isInstanceOf(AuthHubException.class)
                    .satisfies(
                            exception -> {
                                AuthHubException ex = (AuthHubException) exception;
                                assertThat(ex.getStatusCode()).isEqualTo(409);
                            });
        }
    }

    // ========================================
    // 5. AuthHubClient - UserApi.createUserWithRoles()
    // ========================================

    @Nested
    @DisplayName("AuthHubClient.user().createUserWithRoles() - 사용자 등록")
    class CreateUserWithRolesViaSdkTest {

        @Test
        @DisplayName("사용자만 생성 (역할 없음) - userId, assignedRoleCount=0 (201)")
        void shouldRegisterUserWithoutRoles() {
            // given
            CreateUserWithRolesRequest request =
                    new CreateUserWithRolesRequest(
                            savedOrganization.getOrganizationId(),
                            "sdk-user-" + System.currentTimeMillis() + "@test.com",
                            "010-1234-5678",
                            "SecurePassword123!",
                            null,
                            null);

            // when
            ApiResponse<CreateUserWithRolesResponse> response =
                    authHubClient.user().createUserWithRoles(request);

            // then
            assertThat(response).isNotNull();
            assertThat(response.success()).isTrue();
            assertThat(response.data().userId()).isNotNull();
            assertThat(response.data().assignedRoleCount()).isEqualTo(0);

            // DB 검증
            assertThat(userRepository.findById(response.data().userId())).isPresent();
        }

        @Test
        @DisplayName("사용자 + SERVICE scope Role 할당 - assignedRoleCount > 0 (201)")
        void shouldRegisterUserWithServiceScopeRoles() {
            // given - Service Scope Role 생성
            createRole(null, savedService.getServiceId(), "ADMIN", RoleScope.SERVICE);
            createRole(null, savedService.getServiceId(), "EDITOR", RoleScope.SERVICE);

            CreateUserWithRolesRequest request =
                    new CreateUserWithRolesRequest(
                            savedOrganization.getOrganizationId(),
                            "sdk-admin-" + System.currentTimeMillis() + "@test.com",
                            null,
                            "SecurePassword123!",
                            savedService.getServiceCode(),
                            List.of("ADMIN", "EDITOR"));

            // when
            ApiResponse<CreateUserWithRolesResponse> response =
                    authHubClient.user().createUserWithRoles(request);

            // then
            assertThat(response).isNotNull();
            assertThat(response.success()).isTrue();
            assertThat(response.data().userId()).isNotNull();
            assertThat(response.data().assignedRoleCount()).isEqualTo(2);
        }

        @Test
        @DisplayName("존재하지 않는 organizationId - AuthHubNotFoundException 발생")
        void shouldThrowNotFoundForNonExistentOrganization() {
            // given
            CreateUserWithRolesRequest request =
                    new CreateUserWithRolesRequest(
                            "non-existent-org-id",
                            "orphan@test.com",
                            null,
                            "SecurePassword123!",
                            null,
                            null);

            // when & then
            assertThatThrownBy(() -> authHubClient.user().createUserWithRoles(request))
                    .isInstanceOf(AuthHubNotFoundException.class);
        }
    }

    // ========================================
    // 6. EndpointSync (직접 HTTP 호출)
    // ========================================

    @Nested
    @DisplayName("EndpointSync - 엔드포인트 동기화")
    class EndpointSyncViaSdkTest {

        @Test
        @DisplayName("신규 Permission + Endpoint 생성 - 200")
        void shouldSyncNewEndpoints() {
            // given
            io.restassured.response.Response response =
                    givenServiceToken()
                            .body(
                                    java.util.Map.of(
                                            "serviceName",
                                            "sdk-marketplace",
                                            "serviceCode",
                                            savedService.getServiceCode(),
                                            "endpoints",
                                            List.of(
                                                    java.util.Map.of(
                                                            "httpMethod", "POST",
                                                            "pathPattern", "/api/v1/products",
                                                            "permissionKey", "product:create",
                                                            "description", "SDK 상품 생성"))))
                            .when()
                            .post("/api/v1/internal/endpoints/sync");

            // then
            response.then()
                    .statusCode(200)
                    .body("success", org.hamcrest.Matchers.equalTo(true))
                    .body("data.serviceName", org.hamcrest.Matchers.equalTo("sdk-marketplace"))
                    .body("data.totalEndpoints", org.hamcrest.Matchers.equalTo(1))
                    .body("data.createdPermissions", org.hamcrest.Matchers.greaterThanOrEqualTo(0))
                    .body("data.createdEndpoints", org.hamcrest.Matchers.greaterThanOrEqualTo(0));
        }
    }

    // ========================================
    // 전체 플로우 시나리오 (SDK 기반)
    // ========================================

    @Nested
    @DisplayName("전체 플로우 시나리오 (SDK 기반)")
    class SdkFullFlowTest {

        @Test
        @DisplayName("SDK 온보딩 -> 사용자 등록 -> 권한 조회 전체 흐름")
        void shouldCompleteOnboardingToUserPermissionsFlowViaSdk() {
            // 1. 온보딩 (AuthHubClient)
            String idempotencyKey = UUID.randomUUID().toString();
            TenantOnboardingRequest onboardingRequest =
                    new TenantOnboardingRequest(
                            "sdk-flow-tenant-" + System.currentTimeMillis(), "sdk-flow-org");

            ApiResponse<TenantOnboardingResponse> onboardingResponse =
                    authHubClient.onboarding().onboard(onboardingRequest, idempotencyKey);

            assertThat(onboardingResponse.success()).isTrue();
            String newOrgId = onboardingResponse.data().organizationId();

            // 2. 사용자 등록 (AuthHubClient) - GLOBAL Role 생성 후 할당
            RoleJpaEntity flowRole = createRole(null, null, "FLOW_ADMIN", RoleScope.GLOBAL);

            CreateUserWithRolesRequest registerRequest =
                    new CreateUserWithRolesRequest(
                            newOrgId,
                            "sdk-flow-user-" + System.currentTimeMillis() + "@test.com",
                            null,
                            "SecurePassword123!",
                            null,
                            List.of("FLOW_ADMIN"));

            ApiResponse<CreateUserWithRolesResponse> registerResponse =
                    authHubClient.user().createUserWithRoles(registerRequest);

            assertThat(registerResponse.success()).isTrue();
            assertThat(registerResponse.data().assignedRoleCount()).isEqualTo(1);
            String userId = registerResponse.data().userId();

            // 3. 권한 조회 (GatewayClient)
            ApiResponse<UserPermissions> permissionsResponse =
                    gatewayClient.internal().getUserPermissions(userId);

            assertThat(permissionsResponse.success()).isTrue();
            assertThat(permissionsResponse.data().userId()).isEqualTo(userId);
            assertThat(permissionsResponse.data().roles()).contains("FLOW_ADMIN");
        }

        @Test
        @DisplayName("SDK 엔드포인트 동기화 -> 권한 스펙 조회 전체 흐름")
        void shouldCompleteSyncToSpecFlowViaSdk() {
            // 1. 엔드포인트 동기화 (RestAssured)
            io.restassured.response.Response syncResponse =
                    givenServiceToken()
                            .body(
                                    java.util.Map.of(
                                            "serviceName",
                                            "sdk-flow-service",
                                            "serviceCode",
                                            savedService.getServiceCode(),
                                            "endpoints",
                                            List.of(
                                                    java.util.Map.of(
                                                            "httpMethod", "GET",
                                                            "pathPattern", "/api/v1/sdk/items",
                                                            "permissionKey", "item:read",
                                                            "description", "SDK 아이템 조회"))))
                            .when()
                            .post("/api/v1/internal/endpoints/sync");

            syncResponse.then().statusCode(200);

            // 2. 권한 스펙 조회 (GatewayClient)
            ApiResponse<EndpointPermissionSpecList> specResponse =
                    gatewayClient.internal().getPermissionSpec();

            assertThat(specResponse.success()).isTrue();
            assertThat(specResponse.data().endpoints()).isNotEmpty();
        }
    }

    // ========================================
    // Helper Methods
    // ========================================

    private PermissionJpaEntity createPermission(
            Long serviceId, String key, String resource, String action) {
        PermissionJpaEntity entity =
                PermissionJpaEntity.of(
                        null,
                        serviceId,
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

    private UserJpaEntity createUser(String organizationId, String identifier) {
        UserJpaEntity user =
                UserJpaEntity.of(
                        UUID.randomUUID().toString(),
                        organizationId,
                        identifier,
                        "010-0000-0000",
                        "$2a$10$hashedpassword",
                        com.ryuqq.authhub.domain.user.vo.UserStatus.ACTIVE,
                        FIXED_TIME,
                        FIXED_TIME,
                        null);
        return userRepository.save(user);
    }

    private RoleJpaEntity createRole(
            String tenantId, Long serviceId, String name, RoleScope scope) {
        RoleJpaEntity role =
                RoleJpaEntity.of(
                        null,
                        tenantId,
                        serviceId,
                        name,
                        name + " 표시명",
                        name + " 역할",
                        RoleType.CUSTOM,
                        scope,
                        FIXED_TIME,
                        FIXED_TIME,
                        null);
        return roleRepository.save(role);
    }

    private void createUserRole(String userId, Long roleId) {
        userRoleRepository.save(
                com.ryuqq.authhub.adapter.out.persistence.userrole.entity.UserRoleJpaEntity.of(
                        null, userId, roleId, FIXED_TIME, FIXED_TIME));
    }

    private void createRolePermission(Long roleId, Long permissionId) {
        rolePermissionRepository.save(
                com.ryuqq.authhub.adapter.out.persistence.rolepermission.entity
                        .RolePermissionJpaEntity.of(null, roleId, permissionId, FIXED_TIME));
    }
}
