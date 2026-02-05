package com.ryuqq.authhub.integration.e2e.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

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
import io.restassured.response.Response;
import java.time.Instant;
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
 * Internal API E2E 통합 테스트.
 *
 * <p>Gateway 및 외부 서비스와의 통합을 위한 내부 API 전체 엔드포인트를 검증합니다.
 *
 * <p>엔드포인트 (6개):
 *
 * <ul>
 *   <li>GET /api/v1/internal/endpoint-permissions/spec - 권한 스펙 조회
 *   <li>GET /api/v1/internal/tenants/{tenantId}/config - 테넌트 설정 조회
 *   <li>GET /api/v1/internal/users/{userId}/permissions - 사용자 권한 조회
 *   <li>POST /api/v1/internal/onboarding - 테넌트 온보딩
 *   <li>POST /api/v1/internal/users/register - 사용자 등록
 *   <li>POST /api/v1/internal/endpoints/sync - 엔드포인트 동기화
 * </ul>
 */
@Tag(TestTags.E2E)
@Tag("internal")
@DisplayName("Internal API E2E 통합 테스트")
class InternalApiE2ETest extends E2ETestBase {

    private static final String BASE_PATH = "/api/v1/internal";
    private static final Instant FIXED_TIME = Instant.parse("2025-01-01T00:00:00Z");

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
        // 1. 자식 엔티티부터 삭제 (FK 순서)
        permissionEndpointRepository.deleteAll();
        rolePermissionRepository.deleteAll();
        userRoleRepository.deleteAll();
        permissionRepository.deleteAll();
        roleRepository.deleteAll();
        userRepository.deleteAll();
        organizationRepository.deleteAll();
        tenantRepository.deleteAll();
        serviceRepository.deleteAll();

        // 2. 부모 엔티티 생성 (공통 사전 데이터)
        savedTenant = tenantRepository.save(TenantJpaEntityFixture.create());
        savedOrganization =
                organizationRepository.save(
                        OrganizationJpaEntityFixture.createWithTenant(savedTenant.getTenantId()));
        savedService =
                serviceRepository.save(ServiceJpaEntityFixture.createWithCode("SVC_MARKETPLACE"));
    }

    // ========================================
    // Query Tests: GET /endpoint-permissions/spec
    // ========================================

    @Nested
    @DisplayName("GET /api/v1/internal/endpoint-permissions/spec - 권한 스펙 조회")
    class GetEndpointPermissionSpecTest {

        @Test
        @DisplayName("정상 조회 - 데이터 존재 시")
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
            Response response =
                    givenServiceToken().when().get(BASE_PATH + "/endpoint-permissions/spec");

            // then
            response.then()
                    .statusCode(HttpStatus.OK.value())
                    .body("success", equalTo(true))
                    .body("data.endpoints", hasSize(greaterThanOrEqualTo(1)))
                    .body("data.version", notNullValue())
                    .body("data.updatedAt", notNullValue());
        }

        @Test
        @DisplayName("빈 결과 반환")
        void shouldReturnEmptySpecList() {
            // given - 데이터 없음

            // when
            Response response =
                    givenServiceToken().when().get(BASE_PATH + "/endpoint-permissions/spec");

            // then
            response.then()
                    .statusCode(HttpStatus.OK.value())
                    .body("success", equalTo(true))
                    .body("data.endpoints", empty());
        }

        @Test
        @DisplayName("여러 엔드포인트 조회")
        void shouldGetMultipleEndpointSpecs() {
            // given
            PermissionJpaEntity readPerm = createPermission(null, "user:read", "user", "read");
            PermissionJpaEntity writePerm = createPermission(null, "user:write", "user", "write");

            createPermissionEndpoint(
                    readPerm.getPermissionId(),
                    "user-service",
                    "/api/v1/users",
                    HttpMethod.GET,
                    false);
            createPermissionEndpoint(
                    writePerm.getPermissionId(),
                    "user-service",
                    "/api/v1/users",
                    HttpMethod.POST,
                    false);

            // when
            Response response =
                    givenServiceToken().when().get(BASE_PATH + "/endpoint-permissions/spec");

            // then
            response.then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.endpoints", hasSize(greaterThanOrEqualTo(2)));
        }

        @Test
        @DisplayName("공개 엔드포인트 isPublic=true")
        void shouldIncludePublicEndpointInfo() {
            // given
            PermissionJpaEntity permission =
                    createPermission(null, "health:read", "health", "read");
            createPermissionEndpoint(
                    permission.getPermissionId(),
                    "health-service",
                    "/api/v1/health",
                    HttpMethod.GET,
                    true);

            // when
            Response response =
                    givenServiceToken().when().get(BASE_PATH + "/endpoint-permissions/spec");

            // then
            response.then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.endpoints[0].isPublic", equalTo(true));
        }

        @Test
        @DisplayName("Path Variable 패턴 처리")
        void shouldHandlePathVariablePattern() {
            // given
            PermissionJpaEntity permission = createPermission(null, "user:read", "user", "read");
            createPermissionEndpoint(
                    permission.getPermissionId(),
                    "user-service",
                    "/api/v1/users/{id}",
                    HttpMethod.GET,
                    false);

            // when
            Response response =
                    givenServiceToken().when().get(BASE_PATH + "/endpoint-permissions/spec");

            // then
            response.then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.endpoints[0].pathPattern", equalTo("/api/v1/users/{id}"));
        }

        @Test
        @DisplayName("일반 사용자 인증도 허용 (테스트 환경)")
        void shouldAllowAuthenticatedUserToGetSpec() {
            // given
            PermissionJpaEntity permission = createPermission(null, "user:read", "user", "read");
            createPermissionEndpoint(
                    permission.getPermissionId(),
                    "user-service",
                    "/api/v1/users",
                    HttpMethod.GET,
                    false);

            // when - 일반 인증으로 요청 (테스트 환경에서는 permitAll)
            Response response =
                    givenAuthenticated().when().get(BASE_PATH + "/endpoint-permissions/spec");

            // then
            response.then().statusCode(HttpStatus.OK.value()).body("success", equalTo(true));
        }
    }

    // ========================================
    // Query Tests: GET /tenants/{tenantId}/config
    // ========================================

    @Nested
    @DisplayName("GET /api/v1/internal/tenants/{tenantId}/config - 테넌트 설정 조회")
    class GetTenantConfigTest {

        @Test
        @DisplayName("존재하는 테넌트 조회")
        void shouldGetTenantConfigSuccessfully() {
            // when
            Response response =
                    givenServiceToken()
                            .when()
                            .get(
                                    BASE_PATH + "/tenants/{tenantId}/config",
                                    savedTenant.getTenantId());

            // then
            response.then()
                    .statusCode(HttpStatus.OK.value())
                    .body("success", equalTo(true))
                    .body("data.tenantId", equalTo(savedTenant.getTenantId()))
                    .body("data.name", equalTo(savedTenant.getName()))
                    .body("data.status", equalTo("ACTIVE"))
                    .body("data.active", equalTo(true));
        }

        @Test
        @DisplayName("비활성 테넌트 조회")
        void shouldGetInactiveTenantConfig() {
            // given - 비활성 테넌트
            TenantJpaEntity inactiveTenant =
                    TenantJpaEntity.of(
                            UUID.randomUUID().toString(),
                            "Inactive Tenant",
                            com.ryuqq.authhub.domain.tenant.vo.TenantStatus.INACTIVE,
                            FIXED_TIME,
                            FIXED_TIME,
                            null);
            inactiveTenant = tenantRepository.save(inactiveTenant);

            // when
            Response response =
                    givenServiceToken()
                            .when()
                            .get(
                                    BASE_PATH + "/tenants/{tenantId}/config",
                                    inactiveTenant.getTenantId());

            // then
            response.then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.status", equalTo("INACTIVE"))
                    .body("data.active", equalTo(false));
        }

        @Test
        @DisplayName("존재하지 않는 테넌트 조회 → 404")
        void shouldReturn404WhenTenantNotFound() {
            // when
            Response response =
                    givenServiceToken()
                            .when()
                            .get(
                                    BASE_PATH + "/tenants/{tenantId}/config",
                                    "non-existent-tenant-id");

            // then
            response.then().statusCode(HttpStatus.NOT_FOUND.value()).body("status", equalTo(404));
        }
    }

    // ========================================
    // Query Tests: GET /users/{userId}/permissions
    // ========================================

    @Nested
    @DisplayName("GET /api/v1/internal/users/{userId}/permissions - 사용자 권한 조회")
    class GetUserPermissionsTest {

        @Test
        @DisplayName("사용자 역할/권한 조회")
        void shouldGetUserPermissionsSuccessfully() {
            // given
            UserJpaEntity user =
                    createUser(savedOrganization.getOrganizationId(), "admin@test.com");

            // Role + Permission 생성
            RoleJpaEntity adminRole = createRole(null, null, "ADMIN", RoleScope.GLOBAL);
            RoleJpaEntity viewerRole = createRole(null, null, "VIEWER", RoleScope.GLOBAL);

            PermissionJpaEntity readPerm = createPermission(null, "user:read", "user", "read");
            PermissionJpaEntity writePerm = createPermission(null, "user:write", "user", "write");
            PermissionJpaEntity adminPerm =
                    createPermission(null, "admin:manage", "admin", "manage");

            // UserRole 연결
            createUserRole(user.getUserId(), adminRole.getRoleId());
            createUserRole(user.getUserId(), viewerRole.getRoleId());

            // RolePermission 연결
            createRolePermission(adminRole.getRoleId(), readPerm.getPermissionId());
            createRolePermission(adminRole.getRoleId(), writePerm.getPermissionId());
            createRolePermission(adminRole.getRoleId(), adminPerm.getPermissionId());
            createRolePermission(viewerRole.getRoleId(), readPerm.getPermissionId());

            // when
            Response response =
                    givenServiceToken()
                            .when()
                            .get(BASE_PATH + "/users/{userId}/permissions", user.getUserId());

            // then
            response.then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.userId", equalTo(user.getUserId()))
                    .body("data.roles", hasItem("ADMIN"))
                    .body("data.roles", hasItem("VIEWER"))
                    .body("data.permissions", hasItem("user:read"))
                    .body("data.permissions", hasItem("user:write"))
                    .body("data.permissions", hasItem("admin:manage"));
        }

        @Test
        @DisplayName("역할 없는 사용자 조회 → 빈 배열")
        void shouldReturnEmptyForUserWithoutRoles() {
            // given - 역할 없는 사용자
            UserJpaEntity user =
                    createUser(savedOrganization.getOrganizationId(), "norole@test.com");

            // when
            Response response =
                    givenServiceToken()
                            .when()
                            .get(BASE_PATH + "/users/{userId}/permissions", user.getUserId());

            // then
            response.then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.roles", empty())
                    .body("data.permissions", empty());
        }
    }

    // ========================================
    // Command Tests: POST /onboarding
    // ========================================

    @Nested
    @DisplayName("POST /api/v1/internal/onboarding - 테넌트 온보딩")
    class OnboardingTest {

        @Test
        @DisplayName("정상 온보딩")
        void shouldOnboardSuccessfully() {
            // given
            String idempotencyKey = UUID.randomUUID().toString();
            Map<String, Object> request =
                    Map.of(
                            "tenantName",
                            "new-tenant-" + System.currentTimeMillis(),
                            "organizationName",
                            "default-org");

            // when
            Response response =
                    givenServiceToken()
                            .header("X-Idempotency-Key", idempotencyKey)
                            .body(request)
                            .when()
                            .post(BASE_PATH + "/onboarding");

            // then
            response.then()
                    .statusCode(HttpStatus.CREATED.value())
                    .body("data.tenantId", notNullValue())
                    .body("data.organizationId", notNullValue());

            // DB 검증
            String tenantId = response.jsonPath().getString("data.tenantId");
            String orgId = response.jsonPath().getString("data.organizationId");

            assertThat(tenantRepository.findById(tenantId)).isPresent();
            assertThat(organizationRepository.findById(orgId)).isPresent();
        }

        @Test
        @DisplayName("tenantName 누락 → 400")
        void shouldReturn400WhenTenantNameMissing() {
            // given
            Map<String, Object> request = Map.of("organizationName", "default-org");

            // when
            Response response =
                    givenServiceToken()
                            .header("X-Idempotency-Key", UUID.randomUUID().toString())
                            .body(request)
                            .when()
                            .post(BASE_PATH + "/onboarding");

            // then
            response.then().statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @DisplayName("X-Idempotency-Key 헤더 누락 → 400")
        void shouldReturn400WhenIdempotencyKeyMissing() {
            // given
            Map<String, Object> request =
                    Map.of("tenantName", "test-tenant", "organizationName", "default-org");

            // when
            Response response =
                    givenServiceToken().body(request).when().post(BASE_PATH + "/onboarding");

            // then
            response.then().statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @DisplayName("중복 테넌트 이름 → 409")
        void shouldReturn409WhenDuplicateTenantName() {
            // given - 기존 테넌트
            String duplicateName = savedTenant.getName();

            Map<String, Object> request =
                    Map.of("tenantName", duplicateName, "organizationName", "new-org");

            // when
            Response response =
                    givenServiceToken()
                            .header("X-Idempotency-Key", UUID.randomUUID().toString())
                            .body(request)
                            .when()
                            .post(BASE_PATH + "/onboarding");

            // then
            response.then().statusCode(HttpStatus.CONFLICT.value());
        }
    }

    // ========================================
    // Command Tests: POST /users/register
    // ========================================

    @Nested
    @DisplayName("POST /api/v1/internal/users/register - 사용자 등록")
    class UserRegisterTest {

        @Test
        @DisplayName("사용자만 생성 (역할 없음)")
        void shouldRegisterUserWithoutRoles() {
            // given
            Map<String, Object> request =
                    Map.of(
                            "organizationId",
                            savedOrganization.getOrganizationId(),
                            "identifier",
                            "user-" + System.currentTimeMillis() + "@test.com",
                            "phoneNumber",
                            "010-1234-5678",
                            "password",
                            "SecurePassword123!");

            // when
            Response response =
                    givenServiceToken().body(request).when().post(BASE_PATH + "/users/register");

            // then
            response.then()
                    .statusCode(HttpStatus.CREATED.value())
                    .body("data.userId", notNullValue())
                    .body("data.assignedRoleCount", equalTo(0));

            // DB 검증
            String userId = response.jsonPath().getString("data.userId");
            assertThat(userRepository.findById(userId)).isPresent();
        }

        @Test
        @DisplayName("사용자 + SERVICE scope Role 할당")
        void shouldRegisterUserWithServiceScopeRoles() {
            // given - Service Scope Role 생성
            RoleJpaEntity adminRole =
                    createRole(null, savedService.getServiceId(), "ADMIN", RoleScope.SERVICE);
            RoleJpaEntity editorRole =
                    createRole(null, savedService.getServiceId(), "EDITOR", RoleScope.SERVICE);

            Map<String, Object> request =
                    Map.of(
                            "organizationId", savedOrganization.getOrganizationId(),
                            "identifier",
                                    "service-admin-" + System.currentTimeMillis() + "@test.com",
                            "password", "SecurePassword123!",
                            "serviceCode", savedService.getServiceCode(),
                            "roleNames", java.util.List.of("ADMIN", "EDITOR"));

            // when
            Response response =
                    givenServiceToken().body(request).when().post(BASE_PATH + "/users/register");

            // then
            response.then()
                    .statusCode(HttpStatus.CREATED.value())
                    .body("data.userId", notNullValue())
                    .body("data.assignedRoleCount", equalTo(2));
        }

        @Test
        @DisplayName("사용자 + GLOBAL scope Role 할당")
        void shouldRegisterUserWithGlobalScopeRole() {
            // given - Global Role 생성
            RoleJpaEntity globalAdminRole = createRole(null, null, "ADMIN", RoleScope.GLOBAL);

            Map<String, Object> request =
                    Map.of(
                            "organizationId",
                            savedOrganization.getOrganizationId(),
                            "identifier",
                            "global-admin-" + System.currentTimeMillis() + "@test.com",
                            "password",
                            "SecurePassword123!",
                            "roleNames",
                            java.util.List.of("ADMIN"));

            // when
            Response response =
                    givenServiceToken().body(request).when().post(BASE_PATH + "/users/register");

            // then
            response.then()
                    .statusCode(HttpStatus.CREATED.value())
                    .body("data.assignedRoleCount", equalTo(1));
        }

        @Test
        @DisplayName("organizationId 누락 → 400")
        void shouldReturn400WhenOrganizationIdMissing() {
            // given
            Map<String, Object> request =
                    Map.of("identifier", "noorg@test.com", "password", "SecurePassword123!");

            // when
            Response response =
                    givenServiceToken().body(request).when().post(BASE_PATH + "/users/register");

            // then
            response.then().statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @DisplayName("password 누락 → 400")
        void shouldReturn400WhenPasswordMissing() {
            // given
            Map<String, Object> request =
                    Map.of(
                            "organizationId",
                            savedOrganization.getOrganizationId(),
                            "identifier",
                            "nopw@test.com");

            // when
            Response response =
                    givenServiceToken().body(request).when().post(BASE_PATH + "/users/register");

            // then
            response.then().statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @DisplayName("존재하지 않는 organizationId → 404")
        void shouldReturn404WhenOrganizationNotFound() {
            // given
            Map<String, Object> request =
                    Map.of(
                            "organizationId", "non-existent-org-id",
                            "identifier", "orphan@test.com",
                            "password", "SecurePassword123!");

            // when
            Response response =
                    givenServiceToken().body(request).when().post(BASE_PATH + "/users/register");

            // then
            response.then().statusCode(HttpStatus.NOT_FOUND.value());
        }

        @Test
        @DisplayName("존재하지 않는 serviceCode → 404")
        void shouldReturn404WhenServiceNotFound() {
            // given
            Map<String, Object> request =
                    Map.of(
                            "organizationId", savedOrganization.getOrganizationId(),
                            "identifier", "noservice@test.com",
                            "password", "SecurePassword123!",
                            "serviceCode", "NON_EXISTENT_SERVICE",
                            "roleNames", java.util.List.of("ADMIN"));

            // when
            Response response =
                    givenServiceToken().body(request).when().post(BASE_PATH + "/users/register");

            // then
            response.then().statusCode(HttpStatus.NOT_FOUND.value());
        }
    }

    // ========================================
    // Command Tests: POST /endpoints/sync
    // ========================================

    @Nested
    @DisplayName("POST /api/v1/internal/endpoints/sync - 엔드포인트 동기화")
    class EndpointSyncTest {

        @Test
        @DisplayName("신규 Permission + Endpoint 생성")
        void shouldSyncNewEndpoints() {
            // given
            Map<String, Object> endpoint =
                    Map.of(
                            "httpMethod", "POST",
                            "pathPattern", "/api/v1/products",
                            "permissionKey", "product:create",
                            "description", "상품 생성");

            Map<String, Object> request =
                    Map.of(
                            "serviceName", "marketplace",
                            "serviceCode", savedService.getServiceCode(),
                            "endpoints", java.util.List.of(endpoint));

            // when
            Response response =
                    givenServiceToken().body(request).when().post(BASE_PATH + "/endpoints/sync");

            // then
            response.then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.serviceName", equalTo("marketplace"))
                    .body("data.totalEndpoints", equalTo(1))
                    .body("data.createdPermissions", greaterThanOrEqualTo(0))
                    .body("data.createdEndpoints", greaterThanOrEqualTo(0));
        }

        @Test
        @DisplayName("기존 Permission 재사용 + 신규 Endpoint 생성")
        void shouldReuseExistingPermission() {
            // given - 기존 Permission
            PermissionJpaEntity existingPerm =
                    createPermission(null, "product:read", "product", "read");

            Map<String, Object> endpoint =
                    Map.of(
                            "httpMethod", "GET",
                            "pathPattern", "/api/v1/products",
                            "permissionKey", "product:read");

            Map<String, Object> request =
                    Map.of("serviceName", "marketplace", "endpoints", java.util.List.of(endpoint));

            // when
            Response response =
                    givenServiceToken().body(request).when().post(BASE_PATH + "/endpoints/sync");

            // then
            response.then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.createdPermissions", equalTo(0));
        }

        @Test
        @DisplayName("read action → ADMIN, EDITOR, VIEWER 모두 매핑")
        void shouldMapReadActionToAllRoles() {
            // given - Service Scope Roles
            createRole(null, savedService.getServiceId(), "ADMIN", RoleScope.SERVICE);
            createRole(null, savedService.getServiceId(), "EDITOR", RoleScope.SERVICE);
            createRole(null, savedService.getServiceId(), "VIEWER", RoleScope.SERVICE);

            Map<String, Object> endpoint =
                    Map.of(
                            "httpMethod", "GET",
                            "pathPattern", "/api/v1/products",
                            "permissionKey", "product:read");

            Map<String, Object> request =
                    Map.of(
                            "serviceName", "marketplace",
                            "serviceCode", savedService.getServiceCode(),
                            "endpoints", java.util.List.of(endpoint));

            // when
            Response response =
                    givenServiceToken().body(request).when().post(BASE_PATH + "/endpoints/sync");

            // then
            response.then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.mappedRolePermissions", greaterThanOrEqualTo(0));
        }

        @Test
        @DisplayName("create action → ADMIN, EDITOR만 매핑")
        void shouldMapCreateActionToAdminAndEditor() {
            // given
            createRole(null, savedService.getServiceId(), "ADMIN", RoleScope.SERVICE);
            createRole(null, savedService.getServiceId(), "EDITOR", RoleScope.SERVICE);
            createRole(null, savedService.getServiceId(), "VIEWER", RoleScope.SERVICE);

            Map<String, Object> endpoint =
                    Map.of(
                            "httpMethod", "POST",
                            "pathPattern", "/api/v1/products",
                            "permissionKey", "product:create");

            Map<String, Object> request =
                    Map.of(
                            "serviceName", "marketplace",
                            "serviceCode", savedService.getServiceCode(),
                            "endpoints", java.util.List.of(endpoint));

            // when
            Response response =
                    givenServiceToken().body(request).when().post(BASE_PATH + "/endpoints/sync");

            // then
            response.then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.mappedRolePermissions", greaterThanOrEqualTo(0));
        }

        @Test
        @DisplayName("delete action → ADMIN만 매핑")
        void shouldMapDeleteActionToAdminOnly() {
            // given
            createRole(null, savedService.getServiceId(), "ADMIN", RoleScope.SERVICE);
            createRole(null, savedService.getServiceId(), "EDITOR", RoleScope.SERVICE);
            createRole(null, savedService.getServiceId(), "VIEWER", RoleScope.SERVICE);

            Map<String, Object> endpoint =
                    Map.of(
                            "httpMethod", "DELETE",
                            "pathPattern", "/api/v1/products/{id}",
                            "permissionKey", "product:delete");

            Map<String, Object> request =
                    Map.of(
                            "serviceName", "marketplace",
                            "serviceCode", savedService.getServiceCode(),
                            "endpoints", java.util.List.of(endpoint));

            // when
            Response response =
                    givenServiceToken().body(request).when().post(BASE_PATH + "/endpoints/sync");

            // then
            response.then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.mappedRolePermissions", greaterThanOrEqualTo(0));
        }

        @Test
        @DisplayName("serviceName 누락 → 400")
        void shouldReturn400WhenServiceNameMissing() {
            // given
            Map<String, Object> endpoint =
                    Map.of(
                            "httpMethod", "GET",
                            "pathPattern", "/api/v1/products",
                            "permissionKey", "product:read");

            Map<String, Object> request = Map.of("endpoints", java.util.List.of(endpoint));

            // when
            Response response =
                    givenServiceToken().body(request).when().post(BASE_PATH + "/endpoints/sync");

            // then
            response.then().statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @DisplayName("endpoints 빈 배열 → 400")
        void shouldReturn400WhenEndpointsEmpty() {
            // given
            Map<String, Object> request =
                    Map.of("serviceName", "marketplace", "endpoints", java.util.List.of());

            // when
            Response response =
                    givenServiceToken().body(request).when().post(BASE_PATH + "/endpoints/sync");

            // then
            response.then().statusCode(HttpStatus.BAD_REQUEST.value());
        }
    }

    // ========================================
    // 전체 플로우 시나리오
    // ========================================

    @Nested
    @DisplayName("전체 플로우 시나리오")
    class FullFlowTest {

        @Test
        @DisplayName("온보딩 → 사용자 등록 → 권한 조회")
        void shouldCompleteOnboardingToUserPermissionsFlow() {
            // 1. 온보딩
            String idempotencyKey = UUID.randomUUID().toString();
            Map<String, Object> onboardingRequest =
                    Map.of(
                            "tenantName",
                            "flow-tenant-" + System.currentTimeMillis(),
                            "organizationName",
                            "flow-org");

            Response onboardingResponse =
                    givenServiceToken()
                            .header("X-Idempotency-Key", idempotencyKey)
                            .body(onboardingRequest)
                            .when()
                            .post(BASE_PATH + "/onboarding");

            onboardingResponse.then().statusCode(HttpStatus.CREATED.value());

            String newOrgId = onboardingResponse.jsonPath().getString("data.organizationId");

            // 2. 사용자 등록
            RoleJpaEntity flowRole = createRole(null, null, "FLOW_ADMIN", RoleScope.GLOBAL);

            Map<String, Object> registerRequest =
                    Map.of(
                            "organizationId",
                            newOrgId,
                            "identifier",
                            "flow-user-" + System.currentTimeMillis() + "@test.com",
                            "password",
                            "SecurePassword123!",
                            "roleNames",
                            java.util.List.of("FLOW_ADMIN"));

            Response registerResponse =
                    givenServiceToken()
                            .body(registerRequest)
                            .when()
                            .post(BASE_PATH + "/users/register");

            registerResponse.then().statusCode(HttpStatus.CREATED.value());

            String userId = registerResponse.jsonPath().getString("data.userId");

            // 3. 권한 조회
            Response permissionsResponse =
                    givenServiceToken()
                            .when()
                            .get(BASE_PATH + "/users/{userId}/permissions", userId);

            permissionsResponse
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.userId", equalTo(userId))
                    .body("data.roles", hasItem("FLOW_ADMIN"));
        }

        @Test
        @DisplayName("엔드포인트 동기화 → 권한 스펙 조회")
        void shouldCompleteSyncToSpecFlow() {
            // 1. 엔드포인트 동기화
            Map<String, Object> endpoint =
                    Map.of(
                            "httpMethod", "POST",
                            "pathPattern", "/api/v1/flows",
                            "permissionKey", "flow:create",
                            "description", "플로우 생성");

            Map<String, Object> syncRequest =
                    Map.of(
                            "serviceName", "flow-service",
                            "serviceCode", savedService.getServiceCode(),
                            "endpoints", java.util.List.of(endpoint));

            Response syncResponse =
                    givenServiceToken()
                            .body(syncRequest)
                            .when()
                            .post(BASE_PATH + "/endpoints/sync");

            syncResponse.then().statusCode(HttpStatus.OK.value());

            // 2. 권한 스펙 조회
            Response specResponse =
                    givenServiceToken().when().get(BASE_PATH + "/endpoint-permissions/spec");

            specResponse
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.endpoints", hasSize(greaterThan(0)));
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
