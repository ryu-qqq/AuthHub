package com.ryuqq.authhub.domain.user.vo;

import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Email - 이메일 Value Object
 *
 * <p>RFC 5322 규격을 따르는 이메일 주소입니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public final class Email {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");

    private final String value;

    private Email(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Email cannot be null or blank");
        }
        String trimmed = value.trim().toLowerCase(Locale.ROOT);
        if (!EMAIL_PATTERN.matcher(trimmed).matches()) {
            throw new IllegalArgumentException("Invalid email format: " + value);
        }
        this.value = trimmed;
    }

    public static Email of(String value) {
        return new Email(value);
    }

    public String value() {
        return value;
    }

    public String domain() {
        return value.substring(value.indexOf('@') + 1);
    }

    public String localPart() {
        return value.substring(0, value.indexOf('@'));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Email email = (Email) o;
        return Objects.equals(value, email.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
