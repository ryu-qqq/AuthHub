package com.ryuqq.authhub.domain.organization.vo;

/**
 * OrganizationStatus - 조직 상태 Value Object
 *
 * <p>조직의 활성/비활성 상태를 나타냅니다. 삭제는 DeletionStatus로 별도 관리합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public enum OrganizationStatus {
    ACTIVE("활성"),
    INACTIVE("비활성");

    private final String description;

    OrganizationStatus(String description) {
        this.description = description;
    }

    public String description() {
        return description;
    }

    public boolean isActive() {
        return this == ACTIVE;
    }

    public boolean isInactive() {
        return this == INACTIVE;
    }
}
