package com.ryuqq.authhub.domain.auth.credential;

/**
 * BCrypt로 해시된 비밀번호를 나타내는 Value Object.
 *
 * <p>이 Record는 불변(immutable) 객체이며, 해시된 비밀번호 문자열을 캡슐화합니다.
 * 실제 해싱 작업은 Infrastructure 계층에서 수행되며, Domain 계층에서는 해시된 값만 다룹니다.</p>
 *
 * <p><strong>설계 원칙:</strong></p>
 * <ul>
 *   <li>Domain Layer에서는 해싱 알고리즘에 의존하지 않음</li>
 *   <li>해시된 문자열의 형식만 검증 (BCrypt: $2a$, $2b$, $2y$ 접두사)</li>
 *   <li>실제 해싱/검증은 Infrastructure Layer의 PasswordEncoder에 위임</li>
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>✅ Lombok 미사용 - Java 21 Record 사용</li>
 *   <li>✅ 불변성 보장 - Record의 본질적 불변성</li>
 *   <li>✅ Framework 의존성 금지 - BCrypt 라이브러리 사용 안 함 (Infrastructure에 위임)</li>
 * </ul>
 *
 * <p><strong>BCrypt 해시 형식:</strong></p>
 * <pre>
 * $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy
 * └─┘ └┘ └─────────────────────────────────────────────┘
 *  │   │                     │
 *  │   │          Salt + Hash (53자)
 *  │  Cost (10 = 2^10 rounds)
 * Version ($2a, $2b, $2y)
 * </pre>
 *
 * @param value BCrypt 해시 문자열 (null 불가, 60자)
 * @author AuthHub Team
 * @since 1.0.0
 */
public record PasswordHash(String value) {

    /** BCrypt 해시의 표준 길이 */
    private static final int BCRYPT_HASH_LENGTH = 60;

    /**
     * Compact constructor - BCrypt 해시 형식을 검증합니다.
     *
     * @throws IllegalArgumentException value가 null이거나 BCrypt 형식이 아닌 경우
     */
    public PasswordHash {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("PasswordHash value cannot be null or empty");
        }
        if (!isBCryptFormat(value)) {
            throw new IllegalArgumentException("Invalid BCrypt hash format");
        }
    }

    /**
     * BCrypt 해시 문자열로부터 PasswordHash를 생성합니다.
     * Infrastructure Layer에서 이미 해싱된 값을 받을 때 사용합니다.
     *
     * @param hashedValue BCrypt 해시 문자열 (null 불가)
     * @return PasswordHash 인스턴스
     * @throws IllegalArgumentException hashedValue가 null이거나 BCrypt 형식이 아닌 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    public static PasswordHash from(final String hashedValue) {
        return new PasswordHash(hashedValue);
    }

    /**
     * BCrypt 형식인지 검증합니다.
     * BCrypt 해시는 $2a$, $2b$, $2y$로 시작하며 60자 길이를 가집니다.
     *
     * @param value 검증할 문자열
     * @return BCrypt 형식이면 true, 아니면 false
     */
    private static boolean isBCryptFormat(final String value) {
        if (value == null || value.length() != BCRYPT_HASH_LENGTH) {
            return false;
        }
        // BCrypt 해시는 $2a$, $2b$, $2y$로 시작
        return value.startsWith("$2a$") || value.startsWith("$2b$") || value.startsWith("$2y$");
    }

    /**
     * 해시 문자열을 반환합니다.
     * Infrastructure Layer의 PasswordEncoder에서 검증 시 사용됩니다.
     *
     * @return BCrypt 해시 문자열
     * @author AuthHub Team
     * @since 1.0.0
     */
    public String asString() {
        return this.value;
    }

    /**
     * 특정 평문 비밀번호와 매칭되는지 확인합니다.
     * 실제 검증은 Infrastructure Layer의 PasswordEncoder에 위임해야 합니다.
     *
     * <p><strong>주의:</strong> 이 메서드는 도메인 설계상 시그니처만 제공합니다.
     * 실제 구현은 Infrastructure Layer에서 PasswordEncoder를 통해 수행되어야 합니다.</p>
     *
     * @param rawPassword 평문 비밀번호 (Infrastructure Layer에서 전달)
     * @param passwordEncoder Infrastructure Layer의 PasswordEncoder (BCrypt 검증 수행)
     * @return 매칭되면 true, 아니면 false
     * @author AuthHub Team
     * @since 1.0.0
     */
    public boolean matches(final String rawPassword, final PasswordEncoder passwordEncoder) {
        if (rawPassword == null || rawPassword.trim().isEmpty()) {
            return false;
        }
        if (passwordEncoder == null) {
            throw new IllegalArgumentException("PasswordEncoder cannot be null");
        }
        return passwordEncoder.matches(rawPassword, this.value);
    }

    /**
     * PasswordEncoder 인터페이스.
     * Domain Layer는 이 인터페이스만 의존하며, 실제 구현은 Infrastructure Layer에 있습니다.
     *
     * <p>이는 의존성 역전 원칙(DIP)을 따릅니다:
     * Domain → Interface (Domain에 위치) ← Infrastructure Implementation</p>
     *
     * @author AuthHub Team
     * @since 1.0.0
     */
    public interface PasswordEncoder {
        /**
         * 평문 비밀번호를 BCrypt 해시로 인코딩합니다.
         *
         * @param rawPassword 평문 비밀번호
         * @return BCrypt 해시 문자열
         */
        String encode(String rawPassword);

        /**
         * 평문 비밀번호와 해시값이 매칭되는지 검증합니다.
         *
         * @param rawPassword 평문 비밀번호
         * @param encodedPassword BCrypt 해시 문자열
         * @return 매칭되면 true, 아니면 false
         */
        boolean matches(String rawPassword, String encodedPassword);
    }
}
