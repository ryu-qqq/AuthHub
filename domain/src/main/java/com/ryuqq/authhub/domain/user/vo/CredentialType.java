package com.ryuqq.authhub.domain.user.vo;

/**
 * CredentialType - 인증 정보 유형 Enum
 *
 * @author development-team
 * @since 1.0.0
 */
public enum CredentialType {

    EMAIL("이메일 인증"),
    PHONE("전화번호 인증"),
    OAUTH("OAuth 인증");

    private final String description;

    CredentialType(String description) {
        this.description = description;
    }

    public String description() {
        return description;
    }
}
