package com.ryuqq.authhub.domain.organization.id;

/**
 * OrganizationId - Organization 식별자 Value Object
 *
 * <p>String 기반 식별자를 사용합니다.
 *
 * @param value 식별자 값
 * @author development-team
 * @since 1.0.0
 */
public record OrganizationId(String value) {

    /** Compact Constructor - null/blank 방어 */
    public OrganizationId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("OrganizationId는 null이거나 빈 값일 수 없습니다");
        }
    }

    /**
     * 새로운 OrganizationId 생성
     *
     * @param value 식별자 값
     * @return 새로운 OrganizationId 인스턴스
     */
    public static OrganizationId forNew(String value) {
        return new OrganizationId(value);
    }

    /**
     * 기존 값으로 OrganizationId 생성 (DB 조회 시 사용)
     *
     * @param value 식별자 값
     * @return OrganizationId 인스턴스
     */
    public static OrganizationId of(String value) {
        return new OrganizationId(value);
    }
}
