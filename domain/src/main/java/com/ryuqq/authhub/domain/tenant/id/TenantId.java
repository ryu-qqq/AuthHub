package com.ryuqq.authhub.domain.tenant.id;

/**
 * TenantId - Tenant 식별자 Value Object
 *
 * <p>String 기반 식별자를 사용합니다.
 *
 * @param value 식별자 값
 * @author development-team
 * @since 1.0.0
 */
public record TenantId(String value) {

    /** Compact Constructor - null/blank 방어 */
    public TenantId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("TenantId는 null이거나 빈 값일 수 없습니다");
        }
    }

    /**
     * 새로운 TenantId 생성
     *
     * @param value 식별자 값
     * @return 새로운 TenantId 인스턴스
     */
    public static TenantId forNew(String value) {
        return new TenantId(value);
    }

    /**
     * 기존 값으로 TenantId 생성 (DB 조회 시 사용)
     *
     * @param value 식별자 값
     * @return TenantId 인스턴스
     */
    public static TenantId of(String value) {
        return new TenantId(value);
    }

    /**
     * nullable한 문자열로부터 TenantId 생성
     *
     * <p>null이거나 빈 문자열이면 null을 반환합니다.
     *
     * @param value 식별자 값 (nullable)
     * @return TenantId 인스턴스 또는 null
     */
    public static TenantId fromNullable(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return new TenantId(value);
    }
}
