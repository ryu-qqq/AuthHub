package com.ryuqq.authhub.domain.user.vo;

import java.util.Objects;

/**
 * Credential - 인증 정보 Value Object
 *
 * <p>사용자의 인증 정보를 담고 있는 불변 객체입니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public final class Credential {

    private final CredentialType type;
    private final String identifier;
    private final Password password;

    private Credential(CredentialType type, String identifier, Password password) {
        if (type == null) {
            throw new IllegalArgumentException("CredentialType cannot be null");
        }
        if (identifier == null || identifier.isBlank()) {
            throw new IllegalArgumentException("Credential identifier cannot be null or blank");
        }
        this.type = type;
        this.identifier = identifier.trim();
        this.password = password;
    }

    public static Credential ofEmail(Email email, Password password) {
        if (email == null) {
            throw new IllegalArgumentException("Email cannot be null");
        }
        return new Credential(CredentialType.EMAIL, email.value(), password);
    }

    public static Credential ofPhone(PhoneNumber phoneNumber, Password password) {
        if (phoneNumber == null) {
            throw new IllegalArgumentException("PhoneNumber cannot be null");
        }
        return new Credential(CredentialType.PHONE, phoneNumber.value(), password);
    }

    public static Credential of(CredentialType type, String identifier, Password password) {
        return new Credential(type, identifier, password);
    }

    public CredentialType type() {
        return type;
    }

    public String identifier() {
        return identifier;
    }

    public Password password() {
        return password;
    }

    public boolean hasPassword() {
        return password != null;
    }

    public boolean isEmailCredential() {
        return type == CredentialType.EMAIL;
    }

    public boolean isPhoneCredential() {
        return type == CredentialType.PHONE;
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
        return type == that.type && Objects.equals(identifier, that.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, identifier);
    }

    @Override
    public String toString() {
        return "Credential{type=" + type + ", identifier='" + identifier + "'}";
    }
}
