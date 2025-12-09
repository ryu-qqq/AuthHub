package com.ryuqq.authhub.domain.role.vo;

import java.util.Objects;

/**
 * RoleDescription - 역할 설명 Value Object
 *
 * <p>0-500자 길이의 선택적 설명 문자열입니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public final class RoleDescription {

    private static final int MAX_LENGTH = 500;

    private final String value;

    RoleDescription(String value) {
        this.value = validate(value);
    }

    private String validate(String value) {
        if (value == null) {
            return "";
        }
        String trimmed = value.trim();
        if (trimmed.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    String.format("RoleDescription은 %d자 이하여야 합니다", MAX_LENGTH));
        }
        return trimmed;
    }

    public static RoleDescription of(String value) {
        return new RoleDescription(value);
    }

    public static RoleDescription empty() {
        return new RoleDescription(null);
    }

    public String value() {
        return value;
    }

    public boolean isEmpty() {
        return value.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RoleDescription that = (RoleDescription) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "RoleDescription{value='" + value + "'}";
    }
}
