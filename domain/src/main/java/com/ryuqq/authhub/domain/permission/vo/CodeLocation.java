package com.ryuqq.authhub.domain.permission.vo;

import java.util.Objects;

/**
 * CodeLocation - 코드 위치 Value Object
 *
 * <p>권한이 사용되는 코드 위치를 나타냅니다.
 *
 * <p><strong>형식:</strong> "ClassName.java:lineNumber" (예: ProductController.java:45)
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>Lombok 금지 - Plain Java 사용
 *   <li>불변 객체 (Immutable)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public final class CodeLocation {

    private static final int MAX_LENGTH = 500;

    private final String value;

    private CodeLocation(String value) {
        validate(value);
        this.value = value;
    }

    /**
     * CodeLocation 생성
     *
     * @param value 코드 위치 (예: ProductController.java:45)
     * @return CodeLocation 인스턴스
     */
    public static CodeLocation of(String value) {
        return new CodeLocation(value);
    }

    private void validate(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("CodeLocation은 null이거나 빈 값일 수 없습니다");
        }
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    String.format("CodeLocation은 %d자를 초과할 수 없습니다", MAX_LENGTH));
        }
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
        CodeLocation that = (CodeLocation) o;
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
