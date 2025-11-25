package com.ryuqq.authhub.domain.organization.vo;

/** OrganizationStatus Enum Organization의 상태 */
public enum OrganizationStatus {
    /** 활성 상태 */
    ACTIVE,

    /** 비활성 상태 */
    INACTIVE,

    /** 삭제된 상태 (Soft Delete) */
    DELETED
}
