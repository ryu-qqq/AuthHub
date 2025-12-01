package com.ryuqq.authhub.domain.user.vo;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * PhoneNumber - 전화번호 Value Object
 *
 * <p>E.164 포맷을 따르는 전화번호입니다. (예: +821012345678)
 *
 * @author development-team
 * @since 1.0.0
 */
public final class PhoneNumber {

    private static final Pattern E164_PATTERN = Pattern.compile("^\\+[1-9]\\d{1,14}$");

    private final String value;

    private PhoneNumber(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("PhoneNumber cannot be null or blank");
        }
        String normalized = normalize(value);
        if (!E164_PATTERN.matcher(normalized).matches()) {
            throw new IllegalArgumentException("Invalid phone number format: " + value);
        }
        this.value = normalized;
    }

    public static PhoneNumber of(String value) {
        return new PhoneNumber(value);
    }

    private String normalize(String raw) {
        String digitsOnly = raw.replaceAll("[^+\\d]", "");
        if (!digitsOnly.startsWith("+")) {
            if (digitsOnly.startsWith("82")) {
                return "+" + digitsOnly;
            }
            if (digitsOnly.startsWith("0")) {
                return "+82" + digitsOnly.substring(1);
            }
        }
        return digitsOnly;
    }

    public String value() {
        return value;
    }

    public String countryCode() {
        if (value.startsWith("+82")) {
            return "+82";
        }
        int endIndex = Math.min(value.length(), 4);
        return value.substring(0, endIndex);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PhoneNumber that = (PhoneNumber) o;
        return Objects.equals(value, that.value);
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
