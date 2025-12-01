package com.ryuqq.authhub.domain.tenant.vo;

/**
 * TenantStatus - Tenant 상태 Enum
 *
 * <p>Tenant의 라이프사이클 상태를 정의합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public enum TenantStatus {

    ACTIVE("활성"),
    INACTIVE("비활성"),
    DELETED("삭제됨");

    private final String description;

    TenantStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean canActivate() {
        return this == INACTIVE;
    }

    public boolean canDeactivate() {
        return this == ACTIVE;
    }

    public boolean canDelete() {
        return this != DELETED;
    }
}
