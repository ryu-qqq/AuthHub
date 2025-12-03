package com.ryuqq.authhub.domain.organization.vo;

import java.util.Objects;

/**
 * OrganizationName - Organization 이름 Value Object
 *
 * <p>Organization의 이름을 나타내는 불변 객체입니다. 2자 이상 100자 이하의 문자열만 허용합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public final class OrganizationName {

    private static final int MIN_LENGTH = 2;
    private static final int MAX_LENGTH = 100;

    private final String value;

    private OrganizationName(String value) {
        if (value == null) {
            throw new IllegalArgumentException("OrganizationName은 null일 수 없습니다");
        }
        if (value.length() < MIN_LENGTH || value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("OrganizationName은 2자 이상 100자 이하여야 합니다");
        }
        this.value = value;
    }

    public static OrganizationName of(String value) {
        return new OrganizationName(value);
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
        OrganizationName that = (OrganizationName) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "OrganizationName{value='" + value + "'}";
    }
}
