package com.ryuqq.authhub.domain.tenant.vo;

/** TenantStatus Enum Tenant의 상태 */
public enum TenantStatus {
    /** 활성 상태 */
    ACTIVE,

    /** 비활성 상태 */
    INACTIVE,

    /** 삭제된 상태 (Soft Delete) */
    DELETED
}
