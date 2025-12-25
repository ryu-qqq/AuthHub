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

    /**
     * 기본 테넌트 역할 생성 요청 생성
     *
     * @param tenantId 테넌트 ID (UUID 문자열)
     * @return scope="TENANT"인 역할 생성 요청
     */
    public static CreateRoleApiRequest createTenantRoleRequest(String tenantId) {
        return createTenantRoleRequest(tenantId, "TEST_ROLE", "Test role description");
    }

    /**
     * 커스텀 테넌트 역할 생성 요청 생성
     *
     * @param tenantId 테넌트 ID (UUID 문자열)
     * @param name 역할명
     * @param description 역할 설명
     * @return scope="TENANT"인 역할 생성 요청
     */
    public static CreateRoleApiRequest createTenantRoleRequest(
            String tenantId, String name, String description) {
        return new CreateRoleApiRequest(tenantId, name, description, "TENANT", false);
    }

    /**
     * 유니크 이름을 가진 테넌트 역할 생성 요청 생성
     *
     * <p>테스트 간 충돌 방지를 위해 타임스탬프 기반 유니크 이름을 사용합니다.
     *
     * @param tenantId 테넌트 ID (UUID 문자열)
     * @return 유니크 이름을 가진 테넌트 역할 생성 요청
     */
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

    /**
     * 기본 시스템 역할 생성 요청 생성
     *
     * @return scope="SYSTEM", isDefault=true인 역할 생성 요청
     */
    public static CreateRoleApiRequest createSystemRoleRequest() {
        return createSystemRoleRequest("SYSTEM_ADMIN", "System administrator role");
    }

    /**
     * 커스텀 시스템 역할 생성 요청 생성
     *
     * @param name 역할명
     * @param description 역할 설명
     * @return scope="SYSTEM"인 역할 생성 요청
     */
    public static CreateRoleApiRequest createSystemRoleRequest(String name, String description) {
        return new CreateRoleApiRequest(null, name, description, "SYSTEM", true);
    }

    // ========================================
    // 역할 생성 요청 (조직 역할)
    // ========================================

    /**
     * 조직 역할 생성 요청 생성
     *
     * @param tenantId 테넌트 ID (UUID 문자열)
     * @return scope="ORGANIZATION"인 역할 생성 요청
     */
    public static CreateRoleApiRequest createOrganizationRoleRequest(String tenantId) {
        return new CreateRoleApiRequest(
                tenantId, "ORG_ROLE", "Organization level role", "ORGANIZATION", false);
    }

    // ========================================
    // 역할 수정 요청
    // ========================================

    /**
     * 기본 역할 수정 요청 생성
     *
     * @return 기본값으로 설정된 역할 수정 요청
     */
    public static UpdateRoleApiRequest updateRoleRequest() {
        return updateRoleRequest("UPDATED_ROLE_NAME", "Updated description");
    }

    /**
     * 커스텀 역할 수정 요청 생성
     *
     * @param name 새로운 역할명
     * @param description 새로운 역할 설명
     * @return 역할 수정 요청
     */
    public static UpdateRoleApiRequest updateRoleRequest(String name, String description) {
        return new UpdateRoleApiRequest(name, description);
    }

    // ========================================
    // 권한 부여 요청
    // ========================================

    /**
     * 권한 부여 요청 생성 (UUID)
     *
     * @param permissionId 부여할 권한 ID
     * @return 권한 부여 요청
     */
    public static GrantRolePermissionApiRequest grantPermissionRequest(UUID permissionId) {
        return new GrantRolePermissionApiRequest(permissionId);
    }

    /**
     * 권한 부여 요청 생성 (문자열)
     *
     * @param permissionId 부여할 권한 ID (UUID 문자열)
     * @return 권한 부여 요청
     */
    public static GrantRolePermissionApiRequest grantPermissionRequest(String permissionId) {
        return new GrantRolePermissionApiRequest(UUID.fromString(permissionId));
    }

    // ========================================
    // 검증 실패용 Fixture
    // ========================================

    /**
     * 빈 이름을 가진 역할 생성 요청 (검증 실패용)
     *
     * <p>@NotBlank 검증 실패를 테스트하기 위한 Fixture입니다.
     *
     * @param tenantId 테넌트 ID
     * @return name이 빈 문자열인 역할 생성 요청
     */
    public static CreateRoleApiRequest createRoleRequestWithEmptyName(String tenantId) {
        return new CreateRoleApiRequest(tenantId, "", "Description", "TENANT", false);
    }
}
