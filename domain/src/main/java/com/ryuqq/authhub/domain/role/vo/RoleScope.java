package com.ryuqq.authhub.domain.role.vo;

/**
 * RoleScope - 역할 적용 범위
 *
 * <p>역할이 적용되는 범위를 정의합니다.
 *
 * <ul>
 *   <li>GLOBAL: 전체 시스템에 적용 (예: SUPER_ADMIN)
 *   <li>TENANT: 특정 테넌트 내에서 적용 (예: TENANT_ADMIN)
 *   <li>ORGANIZATION: 특정 조직 내에서 적용 (예: ORG_ADMIN, USER)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public enum RoleScope {

    /** 전체 시스템 범위 - 모든 테넌트와 조직에 적용 */
    GLOBAL,

    /** 테넌트 범위 - 특정 테넌트 내의 모든 조직에 적용 */
    TENANT,

    /** 조직 범위 - 특정 조직 내에서만 적용 */
    ORGANIZATION
}
