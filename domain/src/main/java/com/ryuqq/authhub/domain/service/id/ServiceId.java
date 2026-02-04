package com.ryuqq.authhub.domain.service.id;

/**
 * ServiceId - Service 식별자 Value Object
 *
 * <p>Long 기반 Auto Increment 식별자를 사용합니다.
 *
 * @param value 식별자 값
 * @author development-team
 * @since 1.0.0
 */
public record ServiceId(Long value) {

    /** Compact Constructor - null 방어 */
    public ServiceId {
        if (value == null) {
            throw new IllegalArgumentException("ServiceId는 null일 수 없습니다");
        }
        if (value <= 0) {
            throw new IllegalArgumentException("ServiceId는 0보다 커야 합니다");
        }
    }

    /**
     * 기존 값으로 ServiceId 생성 (DB 조회 시 사용)
     *
     * @param value 식별자 값
     * @return ServiceId 인스턴스
     */
    public static ServiceId of(Long value) {
        return new ServiceId(value);
    }

    /**
     * nullable한 값으로부터 ServiceId 생성
     *
     * <p>null이면 null을 반환합니다.
     *
     * @param value 식별자 값 (nullable)
     * @return ServiceId 인스턴스 또는 null
     */
    public static ServiceId fromNullable(Long value) {
        if (value == null) {
            return null;
        }
        return new ServiceId(value);
    }
}
