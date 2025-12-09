package com.ryuqq.authhub.domain.permission.vo;

import java.util.Objects;

/**
 * PermissionDescription - 권한 설명 Value Object
 *
 * <p>0-500자 길이의 선택적 권한 설명입니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public final class PermissionDescription {

    private static final int MAX_LENGTH = 500;
    private static final PermissionDescription EMPTY = new PermissionDescription("");

    private final String value;

    PermissionDescription(String value) {
        String normalizedValue = normalize(value);
        this.value = normalizedValue;
    }

    private String normalize(String value) {
        if (value == null) {
            return "";
        }
        String trimmed = value.trim();
        if (trimmed.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    String.format("PermissionDescription은 %d자 이하여야 합니다", MAX_LENGTH));
        }
        return trimmed;
    }

    public static PermissionDescription of(String value) {
        if (value == null || value.isBlank()) {
            return EMPTY;
        }
        return new PermissionDescription(value);
    }

    public static PermissionDescription empty() {
        return EMPTY;
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
        PermissionDescription that = (PermissionDescription) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "PermissionDescription{value='" + value + "'}";
    }
}
