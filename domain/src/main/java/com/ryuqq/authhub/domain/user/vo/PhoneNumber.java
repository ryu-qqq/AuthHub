package com.ryuqq.authhub.domain.user.vo;

/**
 * PhoneNumber - 전화번호 Value Object
 *
 * <p>사용자의 전화번호를 나타내는 VO입니다.
 *
 * <p><strong>제약 조건:</strong>
 *
 * <ul>
 *   <li>숫자와 하이픈만 허용
 *   <li>최소 10자, 최대 20자
 *   <li>예: 010-1234-5678, 01012345678
 * </ul>
 *
 * @param value 전화번호 값
 * @author development-team
 * @since 1.0.0
 */
public record PhoneNumber(String value) {

    private static final String PHONE_PATTERN = "^[0-9-]+$";
    private static final int MIN_LENGTH = 10;
    private static final int MAX_LENGTH = 20;

    /** Compact Constructor - 유효성 검증 */
    public PhoneNumber {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("PhoneNumber는 null이거나 빈 값일 수 없습니다");
        }
        if (!value.matches(PHONE_PATTERN)) {
            throw new IllegalArgumentException("PhoneNumber는 숫자와 하이픈만 포함해야 합니다: " + value);
        }
        String digitsOnly = value.replace("-", "");
        if (digitsOnly.length() < MIN_LENGTH) {
            throw new IllegalArgumentException("PhoneNumber는 최소 " + MIN_LENGTH + "자리 숫자가 필요합니다");
        }
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("PhoneNumber는 최대 " + MAX_LENGTH + "자까지 가능합니다");
        }
    }

    /**
     * PhoneNumber 생성
     *
     * @param value 전화번호 값
     * @return PhoneNumber 인스턴스
     */
    public static PhoneNumber of(String value) {
        return new PhoneNumber(value);
    }

    /**
     * 문자열로부터 PhoneNumber 파싱 (nullable)
     *
     * <p>null이거나 빈 문자열이면 null을 반환합니다.
     *
     * @param value 전화번호 문자열 (nullable)
     * @return PhoneNumber 또는 null
     */
    public static PhoneNumber fromNullable(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return new PhoneNumber(value);
    }

    /**
     * 숫자만 추출하여 반환
     *
     * @return 숫자만 포함된 전화번호
     */
    public String digitsOnly() {
        return value.replace("-", "");
    }
}
