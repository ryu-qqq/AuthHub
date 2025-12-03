package com.ryuqq.authhub.domain.user.vo;

import java.util.Objects;

/**
 * Credential - 인증 정보 Value Object
 *
 * <p>사용자의 인증 정보를 담고 있는 불변 객체입니다. 로그인 식별자(이메일 또는 아이디)와 비밀번호를 포함합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public final class Credential {

    private final String identifier;
    private final Password password;

    private Credential(String identifier, Password password) {
        if (identifier == null || identifier.isBlank()) {
            throw new IllegalArgumentException("Credential identifier cannot be null or blank");
        }
        this.identifier = identifier.trim();
        this.password = password;
    }

    public static Credential of(String identifier, Password password) {
        return new Credential(identifier, password);
    }

    public static Credential ofEmail(Email email, Password password) {
        if (email == null) {
            throw new IllegalArgumentException("Email cannot be null");
        }
        return new Credential(email.value(), password);
    }

    public String identifier() {
        return identifier;
    }

    public Password password() {
        return password;
    }

    public String getHashedPasswordValue() {
        return password.hashedValue();
    }

    public boolean hasPassword() {
        return password != null;
    }

    public Credential withPassword(Password newPassword) {
        return new Credential(this.identifier, newPassword);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Credential that = (Credential) o;
        return Objects.equals(identifier, that.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier);
    }

    @Override
    public String toString() {
        return "Credential{identifier='" + identifier + "'}";
    }
}
