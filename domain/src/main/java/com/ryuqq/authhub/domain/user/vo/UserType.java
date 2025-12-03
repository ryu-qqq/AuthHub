package com.ryuqq.authhub.domain.user.vo;

/**
 * UserType - 사용자 유형 Enum
 *
 * @author development-team
 * @since 1.0.0
 */
public enum UserType {
    PUBLIC("일반 사용자"),
    INTERNAL("내부 사용자");

    private final String description;

    UserType(String description) {
        this.description = description;
    }

    public String description() {
        return description;
    }
}
