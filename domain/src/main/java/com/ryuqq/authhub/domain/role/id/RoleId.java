package com.ryuqq.authhub.domain.role.id;

/**
 * RoleId - Role 식별자 Value Object
 *
 * <p>Long 기반 Auto Increment 식별자를 사용합니다.
 *
 * @param value 식별자 값
 * @author development-team
 * @since 1.0.0
 */
public record RoleId(Long value) {

    /** Compact Constructor - null 방어 */
    public RoleId {
        if (value == null) {
            throw new IllegalArgumentException("RoleId는 null일 수 없습니다");
        }
        if (value <= 0) {
            throw new IllegalArgumentException("RoleId는 0보다 커야 합니다");
        }
    }

    /**
     * 기존 값으로 RoleId 생성 (DB 조회 시 사용)
     *
     * @param value 식별자 값
     * @return RoleId 인스턴스
     */
    public static RoleId of(Long value) {
        return new RoleId(value);
    }
}
