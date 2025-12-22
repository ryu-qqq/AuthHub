package com.ryuqq.authhub.adapter.in.rest.auth.component;

import com.ryuqq.authhub.application.auth.service.ScopeBasedAccessControl;
import com.ryuqq.authhub.domain.auth.vo.Role;
import com.ryuqq.authhub.domain.role.vo.RoleScope;
import java.util.Objects;
import java.util.Set;
import org.springframework.stereotype.Component;

/**
 * RoleScope 기반 접근 제어 검증기
 *
 * <p>SecurityContext의 역할 정보를 기반으로 RoleScope를 결정하고, 대상 리소스에 대한 접근 권한을 검증합니다.
 *
 * <p><strong>역할-Scope 매핑:</strong>
 *
 * <ul>
 *   <li>SUPER_ADMIN → GLOBAL (전체 시스템)
 *   <li>TENANT_ADMIN → TENANT (본인 테넌트)
 *   <li>ORG_ADMIN, USER → ORGANIZATION (본인 조직)
 * </ul>
 *
 * <p><strong>사용 예시:</strong>
 *
 * <pre>{@code
 * // Controller에서 @PreAuthorize와 함께 사용
 * @PreAuthorize("@scope.canAccessTenant(#tenantId)")
 * public TenantResponse getTenant(@PathVariable String tenantId) { ... }
 *
 * @PreAuthorize("@scope.canAccessOrganization(#orgId, #tenantId)")
 * public List<UserResponse> getOrgUsers(@PathVariable String orgId, @PathVariable String tenantId) { ... }
 * }</pre>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component("scope")
public class ScopeValidator implements ScopeBasedAccessControl {

    @Override
    public boolean canAccessTenant(String targetTenantId) {
        if (targetTenantId == null) {
            return false;
        }

        RoleScope scope = getCurrentScope();
        SecurityContext context = SecurityContextHolder.getContext();

        switch (scope) {
            case GLOBAL:
                return true;

            case TENANT:
            case ORGANIZATION:
                String currentTenantId = context.getTenantId();
                return currentTenantId != null && Objects.equals(currentTenantId, targetTenantId);

            default:
                return false;
        }
    }

    @Override
    public boolean canAccessOrganization(String targetOrganizationId, String targetTenantId) {
        if (targetOrganizationId == null) {
            return false;
        }

        RoleScope scope = getCurrentScope();
        SecurityContext context = SecurityContextHolder.getContext();

        switch (scope) {
            case GLOBAL:
                return true;

            case TENANT:
                String currentTenantId = context.getTenantId();
                return currentTenantId != null && Objects.equals(currentTenantId, targetTenantId);

            case ORGANIZATION:
                String currentOrgId = context.getOrganizationId();
                return currentOrgId != null && Objects.equals(currentOrgId, targetOrganizationId);

            default:
                return false;
        }
    }

    @Override
    public boolean canAccessGlobal() {
        return getCurrentScope() == RoleScope.GLOBAL;
    }

    @Override
    public RoleScope getCurrentScope() {
        SecurityContext context = SecurityContextHolder.getContext();
        Set<String> roles = context.getRoles();
        return determineScope(roles);
    }

    /**
     * 사용자의 역할 집합에서 가장 높은 RoleScope를 결정
     *
     * <p>역할 계층 (높은 순):
     *
     * <ol>
     *   <li>SUPER_ADMIN → GLOBAL
     *   <li>TENANT_ADMIN → TENANT
     *   <li>ORG_ADMIN, USER → ORGANIZATION
     * </ol>
     *
     * @param roles 사용자 역할 집합
     * @return 가장 높은 RoleScope (기본값: ORGANIZATION)
     */
    private RoleScope determineScope(Set<String> roles) {
        if (roles == null || roles.isEmpty()) {
            return RoleScope.ORGANIZATION;
        }

        if (roles.contains(Role.SUPER_ADMIN)) {
            return RoleScope.GLOBAL;
        }

        if (roles.contains(Role.TENANT_ADMIN)) {
            return RoleScope.TENANT;
        }

        return RoleScope.ORGANIZATION;
    }

    /**
     * 특정 테넌트에 대한 접근 권한 확인 (조직 정보 없이)
     *
     * <p>@PreAuthorize에서 간편하게 사용할 수 있는 오버로드 메서드입니다.
     *
     * @param targetTenantId 대상 테넌트 ID
     * @return 접근 가능하면 true
     */
    public boolean tenant(String targetTenantId) {
        return canAccessTenant(targetTenantId);
    }

    /**
     * 특정 조직에 대한 접근 권한 확인
     *
     * <p>조직이 속한 테넌트 정보가 필요합니다.
     *
     * @param targetOrganizationId 대상 조직 ID
     * @param targetTenantId 대상 조직이 속한 테넌트 ID
     * @return 접근 가능하면 true
     */
    public boolean organization(String targetOrganizationId, String targetTenantId) {
        return canAccessOrganization(targetOrganizationId, targetTenantId);
    }

    /**
     * GLOBAL scope 접근 권한 확인 (별칭)
     *
     * @return GLOBAL scope이면 true
     */
    public boolean global() {
        return canAccessGlobal();
    }

    /**
     * 본인 테넌트에 대한 접근 권한 확인
     *
     * <p>현재 사용자의 테넌트와 동일한지 확인합니다.
     *
     * @param targetTenantId 대상 테넌트 ID
     * @return 본인 테넌트이거나 GLOBAL scope이면 true
     */
    public boolean ownTenant(String targetTenantId) {
        return canAccessTenant(targetTenantId);
    }

    /**
     * 본인 조직에 대한 접근 권한 확인
     *
     * <p>현재 사용자의 조직과 동일한지 확인합니다.
     *
     * @param targetOrganizationId 대상 조직 ID
     * @return 본인 조직이거나 상위 scope이면 true
     */
    public boolean ownOrganization(String targetOrganizationId) {
        SecurityContext context = SecurityContextHolder.getContext();
        return canAccessOrganization(targetOrganizationId, context.getTenantId());
    }
}
