package com.ryuqq.authhub.domain.identity.organization.vo;

/**
 * 조직명을 나타내는 Value Object.
 * 조직명은 중복 불가하며, 길이와 형식 제약을 가집니다.
 *
 * <p>이 Record는 불변(immutable) 객체이며, 동등성 비교는 내부 value 값으로 수행됩니다.</p>
 *
 * <p><strong>Validation 규칙:</strong></p>
 * <ul>
 *   <li>길이: 2자 이상, 100자 이하</li>
 *   <li>허용 문자: 한글, 영문, 숫자, 공백, 하이픈(-), 언더스코어(_)</li>
 *   <li>시작/끝 공백 불가</li>
 *   <li>연속 공백 불가</li>
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>Lombok 미사용 - Java 21 Record 사용</li>
 *   <li>불변성 보장 - Record의 본질적 불변성</li>
 *   <li>Law of Demeter 준수</li>
 * </ul>
 *
 * @param value 조직명 문자열 (null 불가, 유효성 검증 통과 필수)
 * @author AuthHub Team
 * @since 1.0.0
 */
public record OrganizationName(String value) {

    private static final int MIN_LENGTH = 2;
    private static final int MAX_LENGTH = 100;
    private static final String NAME_PATTERN = "^[가-힣a-zA-Z0-9\\s_-]+$";

    /**
     * Compact constructor - 조직명 값의 유효성 검증을 수행합니다.
     *
     * @throws IllegalArgumentException value가 null이거나 유효성 검증에 실패한 경우
     */
    public OrganizationName {
        validateNotNull(value);
        validateNoLeadingTrailingSpaces(value);
        validateNoConsecutiveSpaces(value);
        validateLength(value);
        validatePattern(value);
    }

    /**
     * Null 여부를 검증합니다.
     *
     * @param value 검증 대상 조직명
     * @throws IllegalArgumentException value가 null이거나 비어있는 경우
     */
    private void validateNotNull(final String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Organization name cannot be null or empty");
        }
    }

    /**
     * 시작/끝 공백을 검증합니다.
     *
     * @param value 검증 대상 조직명
     * @throws IllegalArgumentException 시작이나 끝에 공백이 있는 경우
     */
    private void validateNoLeadingTrailingSpaces(final String value) {
        if (!value.equals(value.trim())) {
            throw new IllegalArgumentException("Organization name cannot have leading or trailing spaces");
        }
    }

    /**
     * 연속 공백을 검증합니다.
     *
     * @param value 검증 대상 조직명
     * @throws IllegalArgumentException 연속 공백이 포함된 경우
     */
    private void validateNoConsecutiveSpaces(final String value) {
        if (value.contains("  ")) {
            throw new IllegalArgumentException("Organization name cannot contain consecutive spaces");
        }
    }

    /**
     * 조직명 길이를 검증합니다.
     *
     * @param value 검증 대상 조직명
     * @throws IllegalArgumentException 길이가 허용 범위를 벗어난 경우
     */
    private void validateLength(final String value) {
        final int length = value.length();
        if (length < MIN_LENGTH || length > MAX_LENGTH) {
            throw new IllegalArgumentException(
                String.format("Organization name length must be between %d and %d characters, but was %d",
                    MIN_LENGTH, MAX_LENGTH, length)
            );
        }
    }

    /**
     * 조직명 형식을 검증합니다.
     * 한글, 영문, 숫자, 공백, 하이픈, 언더스코어만 허용합니다.
     *
     * @param value 검증 대상 조직명
     * @throws IllegalArgumentException 허용되지 않은 문자가 포함된 경우
     */
    private void validatePattern(final String value) {
        if (!value.matches(NAME_PATTERN)) {
            throw new IllegalArgumentException(
                "Organization name can only contain Korean, English, numbers, spaces, hyphens, and underscores"
            );
        }
    }

    /**
     * 조직명 문자열을 반환합니다 (Law of Demeter 준수).
     *
     * @return 조직명 문자열
     * @author AuthHub Team
     * @since 1.0.0
     */
    public String getValue() {
        return value;
    }

    /**
     * 조직명 길이를 반환합니다.
     *
     * @return 조직명 문자열 길이
     * @author AuthHub Team
     * @since 1.0.0
     */
    public int getLength() {
        return value.length();
    }

    /**
     * 주어진 문자열이 유효한 조직명 형식인지 검증합니다 (정적 헬퍼 메서드).
     *
     * @param value 검증 대상 문자열
     * @return 유효하면 true, 그렇지 않으면 false
     * @author AuthHub Team
     * @since 1.0.0
     */
    public static boolean isValid(final String value) {
        if (value == null || value.trim().isEmpty()) {
            return false;
        }
        if (!value.equals(value.trim())) {
            return false;
        }
        if (value.contains("  ")) {
            return false;
        }
        final int length = value.length();
        if (length < MIN_LENGTH || length > MAX_LENGTH) {
            return false;
        }
        return value.matches(NAME_PATTERN);
    }
}
