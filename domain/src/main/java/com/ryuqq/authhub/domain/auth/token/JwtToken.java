package com.ryuqq.authhub.domain.auth.token;

/**
 * JWT 토큰 값을 나타내는 Value Object.
 *
 * <p>JWT(JSON Web Token) 형식의 토큰 문자열을 래핑하여 도메인 의미를 부여합니다.
 * 토큰의 생성과 검증 로직은 Application Layer의 Port로 분리되며,
 * 이 객체는 순수하게 토큰 값의 보관과 유효성 검증만 담당합니다.</p>
 *
 * <p><strong>JWT 토큰 형식:</strong></p>
 * <ul>
 *   <li>Header.Payload.Signature 구조 (점으로 구분된 3개 부분)</li>
 *   <li>Base64URL로 인코딩된 문자열</li>
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>Lombok 미사용 - Java 21 Record 사용</li>
 *   <li>불변성 보장 - Record의 본질적 불변성</li>
 *   <li>JWT 생성/검증 로직은 Application Layer로 분리</li>
 *   <li>Domain은 순수 비즈니스 로직만 포함</li>
 * </ul>
 *
 * @param value JWT 토큰 문자열 (null 불가, 공백 불가)
 * @author AuthHub Team
 * @since 1.0.0
 */
public record JwtToken(String value) {

    private static final int MIN_JWT_LENGTH = 10; // 최소한의 JWT 길이 검증

    /**
     * Compact constructor - JWT 토큰 값의 기본 유효성 검증을 수행합니다.
     *
     * <p>JWT 형식의 완전한 검증은 Application Layer의 ValidateTokenPort에서 수행되며,
     * 여기서는 null, 공백, 최소 길이만 검증합니다.</p>
     *
     * @throws IllegalArgumentException value가 null, 공백, 또는 너무 짧은 경우
     */
    public JwtToken {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("JWT token value cannot be null or empty");
        }
        if (value.length() < MIN_JWT_LENGTH) {
            throw new IllegalArgumentException(
                    "JWT token value is too short (minimum " + MIN_JWT_LENGTH + " characters)"
            );
        }
    }

    /**
     * JWT 토큰 문자열로부터 JwtToken을 생성합니다.
     *
     * @param tokenString JWT 토큰 문자열 (null 불가)
     * @return JwtToken 인스턴스
     * @throws IllegalArgumentException tokenString이 유효하지 않은 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    public static JwtToken from(final String tokenString) {
        return new JwtToken(tokenString);
    }

    /**
     * JWT 토큰 값을 문자열로 반환합니다.
     *
     * @return JWT 토큰 문자열
     * @author AuthHub Team
     * @since 1.0.0
     */
    public String asString() {
        return this.value;
    }

    /**
     * JWT 토큰 값의 길이를 반환합니다.
     *
     * @return 토큰 문자열 길이
     * @author AuthHub Team
     * @since 1.0.0
     */
    public int length() {
        return this.value.length();
    }

    /**
     * JWT 토큰 값이 특정 접두사로 시작하는지 확인합니다.
     * Bearer 토큰 형식 검증 등에 사용될 수 있습니다.
     *
     * @param prefix 확인할 접두사
     * @return 접두사로 시작하면 true, 아니면 false
     * @author AuthHub Team
     * @since 1.0.0
     */
    public boolean startsWith(final String prefix) {
        if (prefix == null) {
            return false;
        }
        return this.value.startsWith(prefix);
    }
}
