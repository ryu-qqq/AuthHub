package com.ryuqq.authhub.domain.user.vo;

/**
 * UserStatus - 사용자 상태 열거형
 *
 * @author development-team
 * @since 1.0.0
 */
public enum UserStatus {

    ACTIVE("활성"),
    INACTIVE("비활성"),
    LOCKED("잠금"),
    DELETED("삭제됨");

    private final String description;

    UserStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isActive() {
        return this == ACTIVE;
    }

    public boolean canLogin() {
        return this == ACTIVE;
    }
}
