package com.ryuqq.authhub.domain.permission.vo;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * ServiceName - 서비스명 Value Object
 *
 * <p>권한을 사용하는 서비스의 이름을 나타냅니다.
 *
 * <p><strong>형식:</strong> 소문자, 숫자, 하이픈만 허용 (예: product-service, order-api)
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>Lombok 금지 - Plain Java 사용
 *   <li>불변 객체 (Immutable)
 *   <li>생성 시 유효성 검증
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public final class ServiceName {

    private static final Pattern SERVICE_NAME_PATTERN = Pattern.compile("^[a-z][a-z0-9-]*$");
    private static final int MAX_LENGTH = 100;

    private final String value;

    private ServiceName(String value) {
        validate(value);
        this.value = value;
    }

    /**
     * ServiceName 생성
     *
     * @param value 서비스명
     * @return ServiceName 인스턴스
     * @throws IllegalArgumentException 유효하지 않은 서비스명인 경우
     */
    public static ServiceName of(String value) {
        return new ServiceName(value);
    }

    private void validate(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("ServiceName은 null이거나 빈 값일 수 없습니다");
        }
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    String.format("ServiceName은 %d자를 초과할 수 없습니다: %s", MAX_LENGTH, value));
        }
        java.util.regex.Matcher matcher = SERVICE_NAME_PATTERN.matcher(value);
        if (!matcher.matches()) {
            throw new IllegalArgumentException(
                    String.format("ServiceName은 소문자로 시작하고 소문자, 숫자, 하이픈만 포함해야 합니다: %s", value));
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
        ServiceName that = (ServiceName) o;
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
