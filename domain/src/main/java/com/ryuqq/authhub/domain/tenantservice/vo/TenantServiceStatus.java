package com.ryuqq.authhub.domain.tenantservice.vo;

/**
 * TenantServiceStatus - 테넌트-서비스 구독 상태 Value Object
 *
 * <p>테넌트의 서비스 구독 상태를 나타냅니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public enum TenantServiceStatus {
    ACTIVE("활성"),
    INACTIVE("비활성"),
    SUSPENDED("일시 중지");

    private final String description;

    TenantServiceStatus(String description) {
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

    public boolean isSuspended() {
        return this == SUSPENDED;
    }
}
