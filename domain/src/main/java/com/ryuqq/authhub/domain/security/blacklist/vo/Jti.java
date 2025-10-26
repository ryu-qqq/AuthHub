package com.ryuqq.authhub.domain.security.blacklist.vo;

/**
 * JWT ID (JTI)를 나타내는 Value Object.
 * RFC 7519 표준에 정의된 JWT의 고유 식별자입니다.
 *
 * <p>JTI(JWT ID)는 JWT의 고유 식별자로, 토큰의 재사용을 방지하고 블랙리스트 관리에 사용됩니다.
 * RFC 7519 Section 4.1.7에 정의된 표준 Claim입니다.</p>
 *
 * <p><strong>주요 특징:</strong></p>
 * <ul>
 *   <li>JWT 토큰마다 고유한 값을 가짐</li>
 *   <li>토큰 재사용 공격(Replay Attack) 방지</li>
 *   <li>블랙리스트 등록 시 토큰 식별자로 사용</li>
 *   <li>대소문자를 구분하는 문자열 값</li>
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>✅ Lombok 미사용 - Java 21 Record 사용</li>
 *   <li>✅ 불변성 보장 - Record의 본질적 불변성</li>
 *   <li>✅ Law of Demeter 준수 - 직접적인 행위 메서드 제공</li>
 * </ul>
 *
 * <p><strong>검증 규칙:</strong></p>
 * <ul>
 *   <li>null 또는 빈 문자열 불가</li>
 *   <li>공백만으로 구성된 문자열 불가</li>
 *   <li>최대 길이: 255자 (일반적인 UUID 또는 고유 문자열 길이)</li>
 * </ul>
 *
 * @param value JTI 문자열 값 (null 불가, 빈 문자열 불가)
 * @author AuthHub Team
 * @since 1.0.0
 */
public record Jti(String value) {

    private static final int MAX_LENGTH = 255;

    /**
     * Compact constructor - JTI 값의 유효성 검증을 수행합니다.
     *
     * @throws IllegalArgumentException value가 null, 빈 문자열, 또는 최대 길이를 초과하는 경우
     */
    public Jti {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("JTI value cannot be null or empty");
        }
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    "JTI value exceeds maximum length of " + MAX_LENGTH + " characters"
            );
        }
    }

    /**
     * 문자열 값으로부터 Jti를 생성합니다.
     *
     * @param value JTI 문자열 값 (null 불가, 빈 문자열 불가)
     * @return Jti 인스턴스
     * @throws IllegalArgumentException value가 null이거나 빈 문자열인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    public static Jti of(final String value) {
        return new Jti(value);
    }

    /**
     * JTI 값을 문자열로 반환합니다.
     *
     * @return JTI 문자열 값
     * @author AuthHub Team
     * @since 1.0.0
     */
    public String asString() {
        return value;
    }

    /**
     * 다른 Jti와 동일한지 비교합니다.
     * 대소문자를 구분하여 비교합니다.
     *
     * @param other 비교할 Jti 객체
     * @return 동일하면 true, 아니면 false
     * @author AuthHub Team
     * @since 1.0.0
     */
    public boolean isSameAs(final Jti other) {
        if (other == null) {
            return false;
        }
        return this.value.equals(other.value);
    }
}
