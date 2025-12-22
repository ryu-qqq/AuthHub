package com.ryuqq.authhub.application.auth.service;

/**
 * Scope 기반 접근 제어 인터페이스
 *
 * <p>역할 범위(Scope)에 따른 데이터 접근 제어를 담당합니다.
 *
 * <p><strong>Scope 계층:</strong>
 *
 * <ul>
 *   <li>GLOBAL: 전체 시스템 접근 (SUPER_ADMIN)
 *   <li>TENANT: 본인 테넌트 내 접근 (TENANT_ADMIN)
 *   <li>ORGANIZATION: 본인 조직 내 접근 (ORG_ADMIN, USER)
 * </ul>
 *
 * <p><strong>접근 제어 규칙:</strong>
 *
 * <ul>
 *   <li>GLOBAL scope: 모든 테넌트/조직 접근 가능
 *   <li>TENANT scope: 본인 테넌트 내 모든 조직 접근 가능
 *   <li>ORGANIZATION scope: 본인 조직만 접근 가능
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface ScopeBasedAccessControl {

    /**
     * 특정 테넌트 데이터에 접근 가능한지 확인
     *
     * <p>접근 허용 조건:
     *
     * <ul>
     *   <li>GLOBAL scope: 모든 테넌트 접근 가능
     *   <li>TENANT scope: 본인 테넌트만 접근 가능
     *   <li>ORGANIZATION scope: 본인 테넌트만 접근 가능
     * </ul>
     *
     * @param targetTenantId 접근 대상 테넌트 ID
     * @return 접근 가능하면 true
     */
    boolean canAccessTenant(String targetTenantId);

    /**
     * 특정 조직 데이터에 접근 가능한지 확인
     *
     * <p>접근 허용 조건:
     *
     * <ul>
     *   <li>GLOBAL scope: 모든 조직 접근 가능
     *   <li>TENANT scope: 본인 테넌트 내 모든 조직 접근 가능
     *   <li>ORGANIZATION scope: 본인 조직만 접근 가능
     * </ul>
     *
     * @param targetOrganizationId 접근 대상 조직 ID
     * @param targetTenantId 대상 조직이 속한 테넌트 ID (TENANT scope 검증용)
     * @return 접근 가능하면 true
     */
    boolean canAccessOrganization(String targetOrganizationId, String targetTenantId);

    /**
     * 전역 데이터에 접근 가능한지 확인
     *
     * <p>GLOBAL scope 역할(SUPER_ADMIN)만 접근 가능합니다.
     *
     * @return GLOBAL scope이면 true
     */
    boolean canAccessGlobal();

    /**
     * 현재 사용자의 Scope 문자열 반환
     *
     * <p>사용자가 보유한 역할 중 가장 높은 scope를 반환합니다.
     *
     * @return 현재 Scope (GLOBAL, TENANT, ORGANIZATION 중 하나)
     * @see com.ryuqq.auth.common.constant.Scopes
     */
    String getCurrentScope();
}
