package com.ryuqq.authhub.domain.permission.vo;

import java.util.Objects;

/**
 * Action - 권한 행위 Value Object
 *
 * <p>권한이 허용하는 행위를 나타냅니다.
 * 1-30자 길이의 소문자 영문과 언더스코어, 하이픈만 허용합니다.
 *
 * <p>예시: read, write, create, update, delete, manage, admin
 *
 * @author development-team
 * @since 1.0.0
 */
public final class Action {

    private static final int MIN_LENGTH = 1;
    private static final int MAX_LENGTH = 30;
    private static final String PATTERN = "^[a-z][a-z0-9_-]*$";

    private final String value;

    Action(String value) {
        String normalizedValue = normalizeAndValidate(value);
        this.value = normalizedValue;
    }

    private String normalizeAndValidate(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Action은 null이거나 빈 문자열일 수 없습니다");
        }
        String trimmed = value.trim();
        String lowercased = trimmed.toLowerCase();
        int length = lowercased.length();
        if (length < MIN_LENGTH || length > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    String.format("Action은 %d자 이상 %d자 이하여야 합니다", MIN_LENGTH, MAX_LENGTH));
        }
        if (!lowercased.matches(PATTERN)) {
            throw new IllegalArgumentException(
                    "Action은 소문자로 시작하고, 소문자, 숫자, 언더스코어, 하이픈만 사용할 수 있습니다");
        }
        return lowercased;
    }

    public static Action of(String value) {
        return new Action(value);
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
        Action that = (Action) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "Action{value='" + value + "'}";
    }
}
