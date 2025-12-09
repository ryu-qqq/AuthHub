package com.ryuqq.authhub.domain.role.vo;

import java.util.Objects;

/**
 * RoleName - 역할 이름 Value Object
 *
 * <p>1-50자 길이의 문자열입니다. 역할 이름은 영문 대문자와 언더스코어만 허용합니다.
 *
 * <p>예시: SUPER_ADMIN, TENANT_ADMIN, ORG_ADMIN, USER
 *
 * @author development-team
 * @since 1.0.0
 */
public final class RoleName {

    private static final int MIN_LENGTH = 1;
    private static final int MAX_LENGTH = 50;
    private static final String PATTERN = "^[A-Z][A-Z0-9_]*$";

    private final String value;

    RoleName(String value) {
        String normalizedValue = normalizeAndValidate(value);
        this.value = normalizedValue;
    }

    private String normalizeAndValidate(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("RoleName은 null이거나 빈 문자열일 수 없습니다");
        }
        String trimmed = value.trim();
        String uppercased = trimmed.toUpperCase();
        int length = uppercased.length();
        if (length < MIN_LENGTH || length > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    String.format("RoleName은 %d자 이상 %d자 이하여야 합니다", MIN_LENGTH, MAX_LENGTH));
        }
        if (!uppercased.matches(PATTERN)) {
            throw new IllegalArgumentException(
                    "RoleName은 영문 대문자로 시작하고, 영문 대문자, 숫자, 언더스코어만 사용할 수 있습니다");
        }
        return uppercased;
    }

    public static RoleName of(String value) {
        return new RoleName(value);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RoleName that = (RoleName) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "RoleName{value='" + value + "'}";
    }
}
