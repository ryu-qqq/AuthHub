package com.ryuqq.authhub.domain.organization.vo;

/**
 * OrganizationStatus - Organization 상태 열거형
 *
 * <p>Organization의 상태와 상태 전이 규칙을 정의합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public enum OrganizationStatus {
    ACTIVE("활성"),
    INACTIVE("비활성"),
    DELETED("삭제됨");

    private final String description;

    OrganizationStatus(String description) {
        this.description = description;
    }

    public String description() {
        return description;
    }

    /** 활성화 가능 여부를 반환합니다. INACTIVE 상태에서만 활성화 가능합니다. */
    public boolean canActivate() {
        return this == INACTIVE;
    }

    /** 비활성화 가능 여부를 반환합니다. ACTIVE 상태에서만 비활성화 가능합니다. */
    public boolean canDeactivate() {
        return this == ACTIVE;
    }

    /** 삭제 가능 여부를 반환합니다. DELETED 상태가 아닌 경우에만 삭제 가능합니다. */
    public boolean canDelete() {
        return this != DELETED;
    }
}
