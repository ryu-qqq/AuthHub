package com.ryuqq.authhub.adapter.in.rest.auth.component;

import java.util.Objects;
import org.springframework.stereotype.Component;

/**
 * 리소스 접근 권한 검사기
 *
 * <p>@PreAuthorize 어노테이션에서 SpEL 함수로 사용합니다.
 *
 * <p>사용 예시:
 *
 * <pre>{@code
 * // Controller 메서드에서 사용
 * @PreAuthorize("@access.superAdmin()")
 * public void deleteAllTenants() { ... }
 *
 * @PreAuthorize("@access.tenantAdmin() or @access.hasPermission('user:create')")
 * public void createUser() { ... }
 *
 * @PreAuthorize("@access.myself(#userId) or @access.hasPermission('user:read')")
 * public UserResponse getUser(@PathVariable String userId) { ... }
 *
 * @PreAuthorize("@access.sameTenant(#tenantId)")
 * public TenantResponse getTenant(@PathVariable String tenantId) { ... }
 * }</pre>
 *
 * <p>리소스 격리 규칙:
 *
 * <ul>
 *   <li>SUPER_ADMIN: 모든 리소스 접근 가능
 *   <li>TENANT_ADMIN: 자기 테넌트 내 리소스만
 *   <li>ORG_ADMIN: 자기 조직 내 사용자만
 *   <li>USER: 자기 자신만
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component("access")
@SuppressWarnings("PMD.TooManyMethods")
public class ResourceAccessChecker {

    /**
     * 현재 사용자가 SUPER_ADMIN인지 확인
     *
     * @return SUPER_ADMIN이면 true
     */
    public boolean superAdmin() {
        return SecurityContextHolder.isSuperAdmin();
    }

    /**
     * 현재 사용자가 TENANT_ADMIN인지 확인
     *
     * @return TENANT_ADMIN이면 true
     */
    public boolean tenantAdmin() {
        return SecurityContextHolder.getContext().isTenantAdmin();
    }

    /**
     * 현재 사용자가 ORG_ADMIN인지 확인
     *
     * @return ORG_ADMIN이면 true
     */
    public boolean orgAdmin() {
        return SecurityContextHolder.getContext().isOrgAdmin();
    }

    /**
     * 현재 사용자가 요청한 userId 본인인지 확인
     *
     * @param userId 확인할 사용자 ID
     * @return 본인이면 true
     */
    public boolean myself(String userId) {
        String currentUserId = SecurityContextHolder.getCurrentUserId();
        return currentUserId != null && Objects.equals(currentUserId, userId);
    }

    /**
     * 본인이거나 특정 권한이 있는지 확인
     *
     * @param userId 확인할 사용자 ID
     * @param permission 필요한 권한
     * @return 본인이거나 권한이 있으면 true
     */
    public boolean myselfOr(String userId, String permission) {
        return myself(userId) || hasPermission(permission);
    }

    /**
     * 특정 권한 보유 여부 확인
     *
     * <p>SUPER_ADMIN은 모든 권한을 가집니다.
     *
     * @param permission 확인할 권한 (예: user:read, tenant:create)
     * @return 권한이 있으면 true
     */
    public boolean hasPermission(String permission) {
        if (superAdmin()) {
            return true;
        }
        return SecurityContextHolder.hasPermission(permission);
    }

    /**
     * 여러 권한 중 하나라도 보유 여부 확인
     *
     * @param permissions 확인할 권한들
     * @return 하나라도 있으면 true
     */
    public boolean hasAnyPermission(String... permissions) {
        if (superAdmin()) {
            return true;
        }
        return SecurityContextHolder.hasAnyPermission(permissions);
    }

    /**
     * 모든 권한 보유 여부 확인
     *
     * @param permissions 확인할 권한들
     * @return 모두 있으면 true
     */
    public boolean hasAllPermissions(String... permissions) {
        if (superAdmin()) {
            return true;
        }
        return SecurityContextHolder.hasAllPermissions(permissions);
    }

    /**
     * 특정 역할 보유 여부 확인
     *
     * @param role 확인할 역할
     * @return 역할이 있으면 true
     */
    public boolean hasRole(String role) {
        return SecurityContextHolder.hasRole(role);
    }

    /**
     * 여러 역할 중 하나라도 보유 여부 확인
     *
     * @param roles 확인할 역할들
     * @return 하나라도 있으면 true
     */
    public boolean hasAnyRole(String... roles) {
        return SecurityContextHolder.hasAnyRole(roles);
    }

    /**
     * 현재 사용자가 해당 테넌트 소속인지 확인
     *
     * <p>SUPER_ADMIN은 모든 테넌트에 접근 가능합니다.
     *
     * @param tenantId 확인할 테넌트 ID
     * @return 해당 테넌트 소속이거나 SUPER_ADMIN이면 true
     */
    public boolean sameTenant(String tenantId) {
        if (superAdmin()) {
            return true;
        }
        String currentTenantId = SecurityContextHolder.getCurrentTenantId();
        return currentTenantId != null && Objects.equals(currentTenantId, tenantId);
    }

    /**
     * 현재 사용자가 해당 조직 소속인지 확인
     *
     * <p>SUPER_ADMIN과 TENANT_ADMIN은 자기 테넌트 내 모든 조직에 접근 가능합니다.
     *
     * @param organizationId 확인할 조직 ID
     * @return 해당 조직 소속이거나 상위 권한이면 true
     */
    public boolean sameOrganization(String organizationId) {
        if (superAdmin() || tenantAdmin()) {
            return true;
        }
        String currentOrgId = SecurityContextHolder.getCurrentOrganizationId();
        return currentOrgId != null && Objects.equals(currentOrgId, organizationId);
    }

    /**
     * 테넌트 리소스 접근 권한 확인
     *
     * <p>리소스가 자기 테넌트 소속이고 해당 action 권한이 있는지 확인합니다.
     *
     * @param tenantId 리소스의 테넌트 ID
     * @param action 필요한 액션 (read, create, update, delete)
     * @return 접근 가능하면 true
     */
    public boolean tenant(String tenantId, String action) {
        if (superAdmin()) {
            return true;
        }
        if (!sameTenant(tenantId)) {
            return false;
        }
        return hasPermission("tenant:" + action);
    }

    /**
     * 조직 리소스 접근 권한 확인
     *
     * <p>SUPER_ADMIN은 모든 조직에 접근 가능합니다. TENANT_ADMIN은 자기 테넌트 내 조직에 접근 가능합니다. 그 외에는 자기 조직에만 접근 가능합니다.
     *
     * @param organizationId 리소스의 조직 ID
     * @param action 필요한 액션 (read, create, update, delete)
     * @return 접근 가능하면 true
     */
    public boolean organization(String organizationId, String action) {
        if (superAdmin()) {
            return true;
        }
        // TENANT_ADMIN은 organization:* 권한으로 처리
        if (tenantAdmin() && hasPermission("organization:" + action)) {
            return true;
        }
        if (!sameOrganization(organizationId)) {
            return false;
        }
        return hasPermission("organization:" + action);
    }

    /**
     * 사용자 리소스 접근 권한 확인
     *
     * <p>자기 자신이거나 상위 권한을 가진 경우 접근 가능합니다.
     *
     * @param userId 대상 사용자 ID
     * @param action 필요한 액션 (read, create, update, delete)
     * @return 접근 가능하면 true
     */
    public boolean user(String userId, String action) {
        if (superAdmin()) {
            return true;
        }
        // 본인은 read, update 가능
        if (myself(userId) && ("read".equals(action) || "update".equals(action))) {
            return true;
        }
        return hasPermission("user:" + action);
    }

    /**
     * 역할 리소스 접근 권한 확인
     *
     * @param roleId 대상 역할 ID (현재는 사용하지 않음)
     * @param action 필요한 액션 (read, create, update, delete)
     * @return 접근 가능하면 true
     */
    public boolean role(String roleId, String action) {
        if (superAdmin()) {
            return true;
        }
        return hasPermission("role:" + action);
    }

    /**
     * 권한 리소스 접근 권한 확인
     *
     * <p>Permission 관리는 SUPER_ADMIN만 가능합니다.
     *
     * @param permissionId 대상 권한 ID (현재는 사용하지 않음)
     * @param action 필요한 액션 (read, create, update, delete)
     * @return 접근 가능하면 true
     */
    public boolean permission(String permissionId, String action) {
        if (superAdmin()) {
            return true;
        }
        // read는 인증된 사용자면 가능
        if ("read".equals(action)) {
            return SecurityContextHolder.isAuthenticated();
        }
        // create, update, delete는 SUPER_ADMIN만 가능
        return false;
    }

    /**
     * 인증된 사용자인지 확인
     *
     * @return 인증되었으면 true
     */
    public boolean authenticated() {
        return SecurityContextHolder.isAuthenticated();
    }
}
