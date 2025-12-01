package com.ryuqq.authhub.domain.user.vo;

import java.util.Objects;

/**
 * Password - 비밀번호 Value Object
 *
 * <p>BCrypt 해시된 비밀번호를 저장합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public final class Password {

    private final String hashedValue;

    private Password(String hashedValue) {
        if (hashedValue == null || hashedValue.isBlank()) {
            throw new IllegalArgumentException("Password cannot be null or blank");
        }
        this.hashedValue = hashedValue;
    }

    public static Password ofHashed(String hashedValue) {
        return new Password(hashedValue);
    }

    public String hashedValue() {
        return hashedValue;
    }

    public boolean matches(Password other) {
        if (other == null) {
            return false;
        }
        return Objects.equals(this.hashedValue, other.hashedValue);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Password password = (Password) o;
        return Objects.equals(hashedValue, password.hashedValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hashedValue);
    }

    @Override
    public String toString() {
        return "Password{***}";
    }
}
