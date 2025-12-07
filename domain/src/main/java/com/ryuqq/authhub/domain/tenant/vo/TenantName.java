package com.ryuqq.authhub.domain.tenant.vo;

import java.util.Objects;

/**
 * TenantName - 테넌트 이름 Value Object
 *
 * <p>1-100자 길이의 문자열입니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public final class TenantName {

    private static final int MIN_LENGTH = 1;
    private static final int MAX_LENGTH = 100;

    private final String value;

    TenantName(String value) {
        validate(value);
        this.value = value.trim();
    }

    private void validate(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("TenantName은 null이거나 빈 문자열일 수 없습니다");
        }
        String trimmed = value.trim();
        if (trimmed.isEmpty() || trimmed.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    String.format("TenantName은 %d자 이상 %d자 이하여야 합니다", MIN_LENGTH, MAX_LENGTH));
        }
    }

    public static TenantName of(String value) {
        return new TenantName(value);
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
        TenantName that = (TenantName) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "TenantName{value='" + value + "'}";
    }
}
