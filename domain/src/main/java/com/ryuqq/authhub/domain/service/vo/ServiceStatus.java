package com.ryuqq.authhub.domain.service.vo;

/**
 * ServiceStatus - 서비스 상태 Value Object
 *
 * <p>서비스의 활성/비활성 상태를 나타냅니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public enum ServiceStatus {
    ACTIVE("활성"),
    INACTIVE("비활성");

    private final String description;

    ServiceStatus(String description) {
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
