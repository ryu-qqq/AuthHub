package com.ryuqq.authhub.domain.user.id;

/**
 * UserId - User 식별자 Value Object
 *
 * <p>String 기반 UUIDv7 식별자를 사용합니다.
 *
 * @param value 식별자 값
 * @author development-team
 * @since 1.0.0
 */
public record UserId(String value) {

    /** Compact Constructor - null/blank 방어 */
    public UserId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("UserId는 null이거나 빈 값일 수 없습니다");
        }
    }

    /**
     * 새로운 UserId 생성
     *
     * @param value 식별자 값
     * @return 새로운 UserId 인스턴스
     */
    public static UserId forNew(String value) {
        return new UserId(value);
    }

    /**
     * 기존 값으로 UserId 생성 (DB 조회 시 사용)
     *
     * @param value 식별자 값
     * @return UserId 인스턴스
     */
    public static UserId of(String value) {
        return new UserId(value);
    }
}
