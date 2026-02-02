package com.ryuqq.authhub.domain.permissionendpoint.vo;

/**
 * ServiceName - 서비스 이름 Value Object
 *
 * <p>마이크로서비스 식별자를 나타내는 불변 값 객체입니다.
 *
 * <p><strong>검증 규칙:</strong>
 *
 * <ul>
 *   <li>null이거나 빈 값 불가
 *   <li>최대 100자
 *   <li>영문 소문자, 숫자, 하이픈만 허용 (예: product-service, order-api)
 * </ul>
 *
 * @param value 서비스 이름 문자열
 * @author development-team
 * @since 1.0.0
 */
public record ServiceName(String value) {

    private static final int MAX_LENGTH = 100;
    private static final String PATTERN = "^[a-z0-9][a-z0-9-]*[a-z0-9]$|^[a-z0-9]$";

    public ServiceName {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("serviceName은 null이거나 빈 값일 수 없습니다");
        }
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("serviceName은 " + MAX_LENGTH + "자를 초과할 수 없습니다");
        }
        if (!value.matches(PATTERN)) {
            throw new IllegalArgumentException("serviceName은 영문 소문자, 숫자, 하이픈만 허용됩니다: " + value);
        }
    }

    /**
     * 팩토리 메서드
     *
     * @param value 서비스 이름 문자열
     * @return ServiceName 인스턴스
     */
    public static ServiceName of(String value) {
        return new ServiceName(value);
    }
}
