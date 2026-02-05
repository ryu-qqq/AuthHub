package com.ryuqq.authhub.domain.tenantservice.id;

/**
 * TenantServiceId - TenantService 식별자 Value Object
 *
 * <p>Long 기반 Auto Increment 식별자를 사용합니다.
 *
 * @param value 식별자 값
 * @author development-team
 * @since 1.0.0
 */
public record TenantServiceId(Long value) {

    /** Compact Constructor - null 방어 */
    public TenantServiceId {
        if (value == null) {
            throw new IllegalArgumentException("TenantServiceId는 null일 수 없습니다");
        }
        if (value <= 0) {
            throw new IllegalArgumentException("TenantServiceId는 0보다 커야 합니다");
        }
    }

    /**
     * 기존 값으로 TenantServiceId 생성 (DB 조회 시 사용)
     *
     * @param value 식별자 값
     * @return TenantServiceId 인스턴스
     */
    public static TenantServiceId of(Long value) {
        return new TenantServiceId(value);
    }

    /**
     * nullable한 값으로부터 TenantServiceId 생성
     *
     * <p>null이면 null을 반환합니다.
     *
     * @param value 식별자 값 (nullable)
     * @return TenantServiceId 인스턴스 또는 null
     */
    public static TenantServiceId fromNullable(Long value) {
        if (value == null) {
            return null;
        }
        return new TenantServiceId(value);
    }
}
