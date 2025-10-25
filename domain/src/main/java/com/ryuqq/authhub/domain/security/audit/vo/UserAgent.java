package com.ryuqq.authhub.domain.security.audit.vo;

/**
 * HTTP User-Agent 정보를 나타내는 Value Object.
 * 클라이언트의 브라우저, OS, 디바이스 정보를 안전하게 저장합니다.
 *
 * <p>이 Record는 불변(immutable) 객체이며, User-Agent 문자열을 검증하여 도메인 무결성을 보장합니다.</p>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>✅ Lombok 미사용 - Java 21 Record 사용</li>
 *   <li>✅ 불변성 보장 - Record의 본질적 불변성</li>
 *   <li>✅ Law of Demeter 준수</li>
 *   <li>✅ 도메인 검증 로직 포함 - null 및 길이 검증</li>
 * </ul>
 *
 * @param value User-Agent 문자열 (null 불가, 최대 1000자)
 * @author AuthHub Team
 * @since 1.0.0
 */
public record UserAgent(String value) {

    /**
     * User-Agent 최대 길이 (1000자).
     */
    private static final int MAX_LENGTH = 1000;

    /**
     * 알 수 없는 User-Agent를 나타내는 상수.
     */
    public static final String UNKNOWN = "UNKNOWN";

    /**
     * Compact constructor - User-Agent 값의 검증을 수행합니다.
     *
     * @throws IllegalArgumentException value가 null이거나 최대 길이를 초과하는 경우
     */
    public UserAgent {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("User-Agent cannot be null or empty");
        }

        final String trimmedValue = value.trim();

        if (trimmedValue.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    "User-Agent length cannot exceed " + MAX_LENGTH + " characters: " + trimmedValue.length()
            );
        }

        value = trimmedValue;
    }

    /**
     * 새로운 UserAgent를 생성합니다.
     *
     * @param userAgent User-Agent 문자열 (null 불가)
     * @return UserAgent 인스턴스
     * @throws IllegalArgumentException userAgent가 null이거나 최대 길이를 초과하는 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    public static UserAgent of(final String userAgent) {
        return new UserAgent(userAgent);
    }

    /**
     * 알 수 없는 User-Agent를 나타내는 UserAgent를 생성합니다.
     *
     * @return UNKNOWN 값을 가진 UserAgent 인스턴스
     * @author AuthHub Team
     * @since 1.0.0
     */
    public static UserAgent unknown() {
        return new UserAgent(UNKNOWN);
    }

    /**
     * 알 수 없는 User-Agent인지 확인합니다.
     *
     * @return UNKNOWN이면 true, 아니면 false
     * @author AuthHub Team
     * @since 1.0.0
     */
    public boolean isUnknown() {
        return UNKNOWN.equals(this.value);
    }

    /**
     * User-Agent 문자열을 반환합니다.
     *
     * @return User-Agent 문자열
     * @author AuthHub Team
     * @since 1.0.0
     */
    public String asString() {
        return this.value;
    }

    /**
     * User-Agent 문자열의 길이를 반환합니다.
     *
     * @return User-Agent 문자열 길이
     * @author AuthHub Team
     * @since 1.0.0
     */
    public int length() {
        return this.value.length();
    }

    /**
     * 모바일 User-Agent인지 간단히 추정합니다.
     * "Mobile", "Android", "iPhone", "iPad" 키워드 포함 여부로 판단합니다.
     *
     * @return 모바일로 추정되면 true, 아니면 false
     * @author AuthHub Team
     * @since 1.0.0
     */
    public boolean isMobile() {
        final String lowerCaseValue = this.value.toLowerCase();
        return lowerCaseValue.contains("mobile")
                || lowerCaseValue.contains("android")
                || lowerCaseValue.contains("iphone")
                || lowerCaseValue.contains("ipad");
    }

    /**
     * 데스크톱 User-Agent인지 간단히 추정합니다.
     * 모바일이 아니고 "Windows", "Mac", "Linux" 키워드 포함 여부로 판단합니다.
     *
     * @return 데스크톱으로 추정되면 true, 아니면 false
     * @author AuthHub Team
     * @since 1.0.0
     */
    public boolean isDesktop() {
        if (isMobile()) {
            return false;
        }
        final String lowerCaseValue = this.value.toLowerCase();
        return lowerCaseValue.contains("windows")
                || lowerCaseValue.contains("mac")
                || lowerCaseValue.contains("linux");
    }
}
