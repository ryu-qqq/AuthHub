package com.ryuqq.authhub.domain.tenant.vo;

import java.util.Objects;

/**
 * TenantName - Tenant 이름 Value Object
 *
 * <p>Tenant 이름은 2자 이상 100자 이하여야 합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public final class TenantName {

    private static final int MIN_LENGTH = 2;
    private static final int MAX_LENGTH = 100;

    private final String value;

    private TenantName(String value) {
        if (value == null) {
            throw new IllegalArgumentException("TenantName은 null일 수 없습니다");
        }
        if (value.length() < MIN_LENGTH || value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    "TenantName은 " + MIN_LENGTH + "자 이상 " + MAX_LENGTH + "자 이하여야 합니다");
        }
        this.value = value;
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
