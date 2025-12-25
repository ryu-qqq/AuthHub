package com.ryuqq.authhub.integration.role.fixture;

import com.ryuqq.authhub.adapter.in.rest.role.dto.command.CreateRoleApiRequest;
import com.ryuqq.authhub.adapter.in.rest.role.dto.command.GrantRolePermissionApiRequest;
import com.ryuqq.authhub.adapter.in.rest.role.dto.command.UpdateRoleApiRequest;
import java.util.UUID;

/**
 * 역할 통합 테스트 Fixture
 *
 * <p>API Request/Response 객체 생성 유틸리티
 *
 * @author Development Team
 * @since 1.0.0
 */
public final class RoleIntegrationTestFixture {

    private RoleIntegrationTestFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    // ========================================
    // 역할 생성 요청 (테넌트 역할)
    // ========================================
    public static CreateRoleApiRequest createTenantRoleRequest(String tenantId) {
        return createTenantRoleRequest(tenantId, "TEST_ROLE", "Test role description");
    }

    public static CreateRoleApiRequest createTenantRoleRequest(
            String tenantId, String name, String description) {
        return new CreateRoleApiRequest(tenantId, name, description, "TENANT", false);
    }

    public static CreateRoleApiRequest createTenantRoleRequestWithUniqueName(String tenantId) {
        return new CreateRoleApiRequest(
                tenantId,
                "ROLE_" + System.currentTimeMillis(),
                "Test role description",
                "TENANT",
                false);
    }

    // ========================================
    // 역할 생성 요청 (시스템 역할)
    // ========================================
    public static CreateRoleApiRequest createSystemRoleRequest() {
        return createSystemRoleRequest("SYSTEM_ADMIN", "System administrator role");
    }

    public static CreateRoleApiRequest createSystemRoleRequest(String name, String description) {
        return new CreateRoleApiRequest(null, name, description, "SYSTEM", true);
    }

    // ========================================
    // 역할 생성 요청 (조직 역할)
    // ========================================
    public static CreateRoleApiRequest createOrganizationRoleRequest(String tenantId) {
        return new CreateRoleApiRequest(
                tenantId, "ORG_ROLE", "Organization level role", "ORGANIZATION", false);
    }

    // ========================================
    // 역할 수정 요청
    // ========================================
    public static UpdateRoleApiRequest updateRoleRequest() {
        return updateRoleRequest("UPDATED_ROLE_NAME", "Updated description");
    }

    public static UpdateRoleApiRequest updateRoleRequest(String name, String description) {
        return new UpdateRoleApiRequest(name, description);
    }

    // ========================================
    // 권한 부여 요청
    // ========================================
    public static GrantRolePermissionApiRequest grantPermissionRequest(UUID permissionId) {
        return new GrantRolePermissionApiRequest(permissionId);
    }

    public static GrantRolePermissionApiRequest grantPermissionRequest(String permissionId) {
        return new GrantRolePermissionApiRequest(UUID.fromString(permissionId));
    }

    // ========================================
    // 검증 실패용 Fixture
    // ========================================
    public static CreateRoleApiRequest createRoleRequestWithEmptyName(String tenantId) {
        return new CreateRoleApiRequest(tenantId, "", "Description", "TENANT", false);
    }
}
