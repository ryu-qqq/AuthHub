package com.ryuqq.authhub.domain.permission.vo;

import java.util.Objects;

/**
 * Resource - 권한 대상 리소스 Value Object
 *
 * <p>권한이 적용되는 대상 리소스를 나타냅니다. 1-50자 길이의 소문자 영문과 언더스코어, 하이픈만 허용합니다.
 *
 * <p>예시: user, organization, tenant, role, permission
 *
 * @author development-team
 * @since 1.0.0
 */
public final class Resource {

    private static final int MIN_LENGTH = 1;
    private static final int MAX_LENGTH = 50;
    private static final String PATTERN = "^[a-z][a-z0-9_-]*$";

    private final String value;

    Resource(String value) {
        String normalizedValue = normalizeAndValidate(value);
        this.value = normalizedValue;
    }

    private String normalizeAndValidate(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Resource는 null이거나 빈 문자열일 수 없습니다");
        }
        String trimmed = value.trim();
        String lowercased = trimmed.toLowerCase();
        int length = lowercased.length();
        if (length < MIN_LENGTH || length > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    String.format("Resource는 %d자 이상 %d자 이하여야 합니다", MIN_LENGTH, MAX_LENGTH));
        }
        if (!lowercased.matches(PATTERN)) {
            throw new IllegalArgumentException(
                    "Resource는 소문자로 시작하고, 소문자, 숫자, 언더스코어, 하이픈만 사용할 수 있습니다");
        }
        return lowercased;
    }

    public static Resource of(String value) {
        return new Resource(value);
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
        Resource that = (Resource) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "Resource{value='" + value + "'}";
    }
}
