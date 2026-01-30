package com.ryuqq.authhub.domain.permission.id;

/**
 * PermissionId - Permission 식별자 Value Object
 *
 * <p>Long 기반 Auto Increment 식별자를 사용합니다.
 *
 * @param value 식별자 값
 * @author development-team
 * @since 1.0.0
 */
public record PermissionId(Long value) {

    /** Compact Constructor - null 방어 */
    public PermissionId {
        if (value == null) {
            throw new IllegalArgumentException("PermissionId는 null일 수 없습니다");
        }
        if (value <= 0) {
            throw new IllegalArgumentException("PermissionId는 0보다 커야 합니다");
        }
    }

    /**
     * 기존 값으로 PermissionId 생성 (DB 조회 시 사용)
     *
     * @param value 식별자 값
     * @return PermissionId 인스턴스
     */
    public static PermissionId of(Long value) {
        return new PermissionId(value);
    }
}
