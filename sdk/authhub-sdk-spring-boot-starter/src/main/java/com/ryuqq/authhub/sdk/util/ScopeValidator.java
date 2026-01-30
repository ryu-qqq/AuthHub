package com.ryuqq.authhub.sdk.util;

import com.ryuqq.authhub.sdk.constant.Scopes;
import com.ryuqq.authhub.sdk.context.UserContext;

/**
 * ScopeValidator - 범위 기반 접근 검증 유틸리티
 *
 * <p>사용자의 역할 범위(Scope)에 따라 특정 리소스에 대한 접근 권한을 검증합니다.
 *
 * <p><strong>범위 계층:</strong>
 *
 * <ul>
 *   <li>GLOBAL: 모든 테넌트/조직 데이터 접근 가능
 *   <li>TENANT: 해당 테넌트 내 모든 조직 데이터 접근 가능
 *   <li>ORGANIZATION: 해당 조직 데이터만 접근 가능
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public final class ScopeValidator {

    private ScopeValidator() {
        throw new AssertionError("Utility class - cannot instantiate");
    }

    /**
     * 사용자가 특정 테넌트 데이터에 접근할 수 있는지 검증
     *
     * @param userContext 사용자 컨텍스트
     * @param targetTenantId 접근하려는 테넌트 ID
     * @return 접근 가능 여부
     */
    public static boolean canAccessTenant(UserContext userContext, String targetTenantId) {
        if (userContext == null || targetTenantId == null) {
            return false;
        }

        String userScope = userContext.getScope();

        if (Scopes.GLOBAL.equals(userScope)) {
            return true;
        }

        if (Scopes.TENANT.equals(userScope) || Scopes.ORGANIZATION.equals(userScope)) {
            return targetTenantId.equals(userContext.getTenantId());
        }

        return false;
    }

    /**
     * 사용자가 특정 조직 데이터에 접근할 수 있는지 검증
     *
     * @param userContext 사용자 컨텍스트
     * @param targetTenantId 접근하려는 테넌트 ID
     * @param targetOrganizationId 접근하려는 조직 ID
     * @return 접근 가능 여부
     */
    public static boolean canAccessOrganization(
            UserContext userContext, String targetTenantId, String targetOrganizationId) {
        if (userContext == null || targetTenantId == null || targetOrganizationId == null) {
            return false;
        }

        String userScope = userContext.getScope();

        if (Scopes.GLOBAL.equals(userScope)) {
            return true;
        }

        if (!targetTenantId.equals(userContext.getTenantId())) {
            return false;
        }

        if (Scopes.TENANT.equals(userScope)) {
            return true;
        }

        if (Scopes.ORGANIZATION.equals(userScope)) {
            return targetOrganizationId.equals(userContext.getOrganizationId());
        }

        return false;
    }

    /**
     * 사용자가 특정 사용자 데이터에 접근할 수 있는지 검증
     *
     * @param userContext 사용자 컨텍스트
     * @param targetUserId 접근하려는 사용자 ID
     * @param targetTenantId 대상 사용자의 테넌트 ID
     * @param targetOrganizationId 대상 사용자의 조직 ID
     * @return 접근 가능 여부
     */
    public static boolean canAccessUser(
            UserContext userContext,
            String targetUserId,
            String targetTenantId,
            String targetOrganizationId) {
        if (userContext == null || targetUserId == null) {
            return false;
        }

        if (targetUserId.equals(userContext.getUserId())) {
            return true;
        }

        return canAccessOrganization(userContext, targetTenantId, targetOrganizationId);
    }

    /**
     * 사용자 범위가 대상 범위 이상인지 확인
     *
     * @param userScope 사용자 범위
     * @param requiredScope 필요한 범위
     * @return 사용자 범위가 필요 범위 이상이면 true
     */
    public static boolean hasSufficientScope(String userScope, String requiredScope) {
        return Scopes.includes(userScope, requiredScope);
    }

    /**
     * 사용자가 GLOBAL 범위인지 확인
     *
     * @param userContext 사용자 컨텍스트
     * @return GLOBAL 범위이면 true
     */
    public static boolean isGlobalScope(UserContext userContext) {
        return userContext != null && Scopes.GLOBAL.equals(userContext.getScope());
    }

    /**
     * 사용자가 TENANT 범위 이상인지 확인
     *
     * @param userContext 사용자 컨텍스트
     * @return TENANT 범위 이상이면 true
     */
    public static boolean isTenantScopeOrHigher(UserContext userContext) {
        if (userContext == null) {
            return false;
        }
        return Scopes.getLevel(userContext.getScope()) >= Scopes.getLevel(Scopes.TENANT);
    }
}
