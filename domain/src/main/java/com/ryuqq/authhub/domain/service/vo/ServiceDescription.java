package com.ryuqq.authhub.domain.service.vo;

/**
 * ServiceDescription - 서비스 설명 Value Object
 *
 * <p>0-500자 길이의 문자열입니다. null을 허용합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public record ServiceDescription(String value) {

    private static final int MAX_LENGTH = 500;

    /** Compact Constructor - 유효성 검증 */
    public ServiceDescription {
        if (value != null) {
            value = value.trim();
            if (value.length() > MAX_LENGTH) {
                throw new IllegalArgumentException(
                        String.format("ServiceDescription은 %d자 이하여야 합니다", MAX_LENGTH));
            }
            if (value.isEmpty()) {
                value = null;
            }
        }
    }

    /**
     * 빈 설명 생성
     *
     * @return null 값을 가진 ServiceDescription
     */
    public static ServiceDescription empty() {
        return new ServiceDescription(null);
    }

    /**
     * 설명 생성
     *
     * @param value 설명 값 (nullable)
     * @return ServiceDescription 인스턴스
     */
    public static ServiceDescription of(String value) {
        return new ServiceDescription(value);
    }

    /**
     * 값이 존재하는지 확인
     *
     * @return 값이 존재하면 true
     */
    public boolean hasValue() {
        return value != null && !value.isEmpty();
    }
}
