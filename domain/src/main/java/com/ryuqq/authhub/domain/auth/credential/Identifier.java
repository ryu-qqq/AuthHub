package com.ryuqq.authhub.domain.auth.credential;

import java.util.regex.Pattern;

/**
 * 인증 정보의 실제 식별자 값을 나타내는 Value Object.
 * 이메일, 전화번호, 사용자명 등의 실제 값을 캡슐화합니다.
 *
 * <p>이 Record는 불변(immutable) 객체이며, 식별자 값의 유효성을 보장합니다.</p>
 *
 * <p><strong>검증 규칙:</strong></p>
 * <ul>
 *   <li>EMAIL: RFC 5322 기본 형식 (example@domain.com)</li>
 *   <li>PHONE: 숫자만 허용, 10-11자리 (01012345678)</li>
 *   <li>USERNAME: 영문, 숫자, 언더스코어, 4-20자</li>
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>✅ Lombok 미사용 - Java 21 Record 사용</li>
 *   <li>✅ 불변성 보장 - Record의 본질적 불변성</li>
 *   <li>✅ Framework 의존성 금지 - Pure Java만 사용</li>
 *   <li>✅ 도메인 검증 로직 포함 - 생성 시점 유효성 검증</li>
 * </ul>
 *
 * @param value 식별자 값 (null 불가, 공백 불가)
 * @author AuthHub Team
 * @since 1.0.0
 */
public record Identifier(String value) {

    /** 이메일 형식 정규표현식 (RFC 5322 간소화 버전) */
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    );

    /** 전화번호 형식 정규표현식 (숫자만, 10-11자리) */
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9]{10,11}$");

    /** 사용자명 형식 정규표현식 (영문, 숫자, 언더스코어, 4-20자) */
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{4,20}$");

    /**
     * Compact constructor - 식별자 값의 기본 검증을 수행합니다.
     *
     * @throws IllegalArgumentException value가 null이거나 공백인 경우
     */
    public Identifier {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Identifier value cannot be null or empty");
        }
    }

    /**
     * 타입별로 검증된 Identifier를 생성합니다.
     * 각 타입에 맞는 형식 검증을 수행합니다.
     *
     * @param type 인증 타입 (null 불가)
     * @param value 식별자 값 (null 불가)
     * @return 검증된 Identifier 인스턴스
     * @throws IllegalArgumentException 타입이 null이거나 value가 형식에 맞지 않는 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    public static Identifier of(final CredentialType type, final String value) {
        if (type == null) {
            throw new IllegalArgumentException("CredentialType cannot be null");
        }
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Identifier value cannot be null or empty");
        }

        String trimmedValue = value.trim();

        switch (type) {
            case EMAIL:
                if (!EMAIL_PATTERN.matcher(trimmedValue).matches()) {
                    throw new IllegalArgumentException("Invalid email format: " + value);
                }
                return new Identifier(trimmedValue.toLowerCase()); // 이메일은 소문자로 정규화
            case PHONE:
                if (!PHONE_PATTERN.matcher(trimmedValue).matches()) {
                    throw new IllegalArgumentException("Invalid phone format (10-11 digits required): " + value);
                }
                return new Identifier(trimmedValue);
            case USERNAME:
                if (!USERNAME_PATTERN.matcher(trimmedValue).matches()) {
                    throw new IllegalArgumentException("Invalid username format (4-20 alphanumeric or underscore): " + value);
                }
                return new Identifier(trimmedValue.toLowerCase()); // 사용자명은 소문자로 정규화
            default:
                throw new IllegalArgumentException("Unsupported credential type: " + type);
        }
    }

    /**
     * 이메일 형식의 Identifier를 생성합니다.
     *
     * @param email 이메일 주소 (null 불가)
     * @return 이메일 Identifier
     * @throws IllegalArgumentException 이메일 형식이 유효하지 않은 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    public static Identifier ofEmail(final String email) {
        return of(CredentialType.EMAIL, email);
    }

    /**
     * 전화번호 형식의 Identifier를 생성합니다.
     *
     * @param phone 전화번호 (null 불가, 10-11자리 숫자)
     * @return 전화번호 Identifier
     * @throws IllegalArgumentException 전화번호 형식이 유효하지 않은 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    public static Identifier ofPhone(final String phone) {
        return of(CredentialType.PHONE, phone);
    }

    /**
     * 사용자명 형식의 Identifier를 생성합니다.
     *
     * @param username 사용자명 (null 불가, 4-20자 영문/숫자/언더스코어)
     * @return 사용자명 Identifier
     * @throws IllegalArgumentException 사용자명 형식이 유효하지 않은 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    public static Identifier ofUsername(final String username) {
        return of(CredentialType.USERNAME, username);
    }

    /**
     * 특정 타입에 대한 유효성을 검증합니다.
     * 생성 후 타입 변경 등을 검증할 때 사용합니다.
     *
     * @param type 검증할 타입
     * @return 유효하면 true, 아니면 false
     * @author AuthHub Team
     * @since 1.0.0
     */
    public boolean isValidFor(final CredentialType type) {
        if (type == null) {
            return false;
        }

        try {
            switch (type) {
                case EMAIL:
                    return EMAIL_PATTERN.matcher(this.value).matches();
                case PHONE:
                    return PHONE_PATTERN.matcher(this.value).matches();
                case USERNAME:
                    return USERNAME_PATTERN.matcher(this.value).matches();
                default:
                    return false;
            }
        } catch (Exception e) {
            return false;
        }
    }
}
