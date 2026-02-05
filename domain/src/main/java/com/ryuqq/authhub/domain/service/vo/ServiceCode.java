package com.ryuqq.authhub.domain.service.vo;

/**
 * ServiceCode - 서비스 비즈니스 식별자 Value Object
 *
 * <p>서비스를 구분하는 고유 코드입니다. (예: SVC_STORE, SVC_B2B, SVC_AUTHHUB)
 *
 * <p>DB에서 UNIQUE 제약이 걸려있으며, Permission/Role에서 FK로 참조하지 않습니다. FK 참조는 Long PK(ServiceId)를 사용합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public record ServiceCode(String value) {

    private static final int MAX_LENGTH = 50;

    /** Compact Constructor - null/blank 방어 및 길이 검증 */
    public ServiceCode {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("ServiceCode는 null이거나 빈 값일 수 없습니다");
        }
        value = value.trim().toUpperCase();
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    String.format("ServiceCode는 %d자 이하여야 합니다", MAX_LENGTH));
        }
    }

    /**
     * 새로운 ServiceCode 생성
     *
     * @param value 코드 값
     * @return 새로운 ServiceCode 인스턴스
     */
    public static ServiceCode of(String value) {
        return new ServiceCode(value);
    }

    /**
     * nullable한 문자열로부터 ServiceCode 생성
     *
     * <p>null이거나 빈 문자열이면 null을 반환합니다.
     *
     * @param value 코드 값 (nullable)
     * @return ServiceCode 인스턴스 또는 null
     */
    public static ServiceCode fromNullable(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return new ServiceCode(value);
    }
}
