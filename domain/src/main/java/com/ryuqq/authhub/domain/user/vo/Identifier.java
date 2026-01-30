package com.ryuqq.authhub.domain.user.vo;

/**
 * Identifier - 사용자 로그인 식별자 Value Object
 *
 * <p>사용자가 로그인 시 사용하는 고유 식별자입니다. 이메일, 사용자명 등이 될 수 있습니다.
 *
 * <p><strong>제약 조건:</strong>
 *
 * <ul>
 *   <li>최소 4자, 최대 100자
 *   <li>null 또는 빈 값 불가
 * </ul>
 *
 * @param value 식별자 값
 * @author development-team
 * @since 1.0.0
 */
public record Identifier(String value) {

    private static final int MIN_LENGTH = 4;
    private static final int MAX_LENGTH = 100;

    /** Compact Constructor - 유효성 검증 */
    public Identifier {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Identifier는 null이거나 빈 값일 수 없습니다");
        }
        if (value.length() < MIN_LENGTH) {
            throw new IllegalArgumentException(
                    "Identifier는 최소 " + MIN_LENGTH + "자 이상이어야 합니다: " + value.length() + "자");
        }
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    "Identifier는 최대 " + MAX_LENGTH + "자까지 가능합니다: " + value.length() + "자");
        }
    }

    /**
     * Identifier 생성
     *
     * @param value 식별자 값
     * @return Identifier 인스턴스
     */
    public static Identifier of(String value) {
        return new Identifier(value);
    }

    /**
     * 문자열로부터 Identifier 파싱 (nullable)
     *
     * <p>null이거나 빈 문자열이면 null을 반환합니다.
     *
     * @param value 식별자 문자열 (nullable)
     * @return Identifier 또는 null
     */
    public static Identifier fromNullable(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return new Identifier(value);
    }
}
