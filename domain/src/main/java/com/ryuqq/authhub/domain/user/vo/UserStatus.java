package com.ryuqq.authhub.domain.user.vo;

/**
 * UserStatus - 사용자 상태 Enum
 *
 * @author development-team
 * @since 1.0.0
 */
public enum UserStatus {
    ACTIVE("활성"),
    INACTIVE("비활성"),
    SUSPENDED("정지"),
    DELETED("삭제");

    private final String description;

    UserStatus(String description) {
        this.description = description;
    }

    public String description() {
        return description;
    }

    public boolean canTransitionTo(UserStatus target) {
        return this != DELETED;
    }
}
