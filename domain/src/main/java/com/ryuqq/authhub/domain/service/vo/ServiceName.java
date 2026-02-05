package com.ryuqq.authhub.domain.service.vo;

/**
 * ServiceName - 서비스 이름 Value Object
 *
 * <p>1-100자 길이의 문자열입니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public record ServiceName(String value) {

    private static final int MIN_LENGTH = 1;
    private static final int MAX_LENGTH = 100;

    /** Compact Constructor - 유효성 검증 */
    public ServiceName {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("ServiceName은 null이거나 빈 문자열일 수 없습니다");
        }
        value = value.trim();
        if (value.isEmpty() || value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    String.format("ServiceName은 %d자 이상 %d자 이하여야 합니다", MIN_LENGTH, MAX_LENGTH));
        }
    }

    public static ServiceName of(String value) {
        return new ServiceName(value);
    }
}
