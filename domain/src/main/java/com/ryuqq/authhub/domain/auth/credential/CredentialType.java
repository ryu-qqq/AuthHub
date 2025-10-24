package com.ryuqq.authhub.domain.auth.credential;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 인증 정보의 타입을 나타내는 Enum.
 *
 * <p>사용자는 다양한 방식으로 인증할 수 있으며, 각 방식은 고유한 타입으로 구분됩니다.</p>
 *
 * <p><strong>지원하는 인증 타입:</strong></p>
 * <ul>
 *   <li>{@link #EMAIL} - 이메일 기반 인증</li>
 *   <li>{@link #PHONE} - 전화번호 기반 인증</li>
 *   <li>{@link #USERNAME} - 사용자명 기반 인증</li>
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>✅ Lombok 미사용 - Pure Java Enum</li>
 *   <li>✅ Framework 의존성 금지</li>
 *   <li>✅ 불변성 보장 - Enum의 본질적 특성</li>
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
public enum CredentialType {

    /**
     * 이메일 기반 인증.
     * 사용자는 이메일 주소와 비밀번호로 인증합니다.
     */
    EMAIL("email", "이메일"),

    /**
     * 전화번호 기반 인증.
     * 사용자는 전화번호와 비밀번호 또는 OTP로 인증합니다.
     */
    PHONE("phone", "전화번호"),

    /**
     * 사용자명 기반 인증.
     * 사용자는 임의의 사용자명과 비밀번호로 인증합니다.
     */
    USERNAME("username", "사용자명");

    private static final Map<String, CredentialType> CODE_MAP = Stream.of(values())
            .collect(Collectors.toUnmodifiableMap(CredentialType::getCode, type -> type));

    private final String code;
    private final String description;

    /**
     * CredentialType enum의 생성자.
     *
     * @param code 타입 코드 (데이터베이스 저장용, 소문자)
     * @param description 타입 설명 (한글)
     */
    CredentialType(final String code, final String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 타입 코드를 반환합니다.
     * 주로 데이터베이스 저장 시 사용됩니다.
     *
     * @return 타입 코드 (email, phone, username)
     * @author AuthHub Team
     * @since 1.0.0
     */
    public String getCode() {
        return this.code;
    }

    /**
     * 타입 설명을 반환합니다.
     *
     * @return 타입 설명 (한글)
     * @author AuthHub Team
     * @since 1.0.0
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * 타입 코드로부터 CredentialType을 조회합니다.
     *
     * @param code 타입 코드 (email, phone, username - 대소문자 무관)
     * @return 매칭되는 CredentialType
     * @throws IllegalArgumentException 매칭되는 타입이 없는 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    public static CredentialType fromCode(final String code) {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Credential type code cannot be null or empty");
        }

        final CredentialType result = CODE_MAP.get(code.trim().toLowerCase());
        if (result == null) {
            throw new IllegalArgumentException("Unknown credential type code: " + code);
        }
        return result;
    }

    /**
     * 문자열로부터 CredentialType을 생성합니다.
     *
     * <p>Enum 상수 이름(EMAIL, PHONE, USERNAME)으로부터 CredentialType을 생성합니다.
     * 대소문자를 구분하지 않으며, 앞뒤 공백은 제거됩니다.</p>
     *
     * <p><strong>사용 예시:</strong></p>
     * <pre>
     * CredentialType.fromString("EMAIL")     → CredentialType.EMAIL
     * CredentialType.fromString("email")     → CredentialType.EMAIL
     * CredentialType.fromString(" phone ")   → CredentialType.PHONE
     * CredentialType.fromString("INVALID")   → InvalidCredentialException
     * </pre>
     *
     * @param type Enum 상수 이름 (EMAIL, PHONE, USERNAME - 대소문자 무관)
     * @return 매칭되는 CredentialType
     * @throws com.ryuqq.authhub.domain.auth.credential.exception.InvalidCredentialException
     *         type이 null이거나 유효하지 않은 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    public static CredentialType fromString(final String type) {
        if (type == null) {
            throw new com.ryuqq.authhub.domain.auth.credential.exception.InvalidCredentialException(
                    "credentialType cannot be null"
            );
        }

        final String normalizedType = type.trim().toUpperCase();
        if (normalizedType.isEmpty()) {
            throw new com.ryuqq.authhub.domain.auth.credential.exception.InvalidCredentialException(
                    "credentialType cannot be empty"
            );
        }

        try {
            return CredentialType.valueOf(normalizedType);
        } catch (final IllegalArgumentException e) {
            throw new com.ryuqq.authhub.domain.auth.credential.exception.InvalidCredentialException(
                    "Invalid credentialType: " + type + " (expected: EMAIL, PHONE, USERNAME)"
            );
        }
    }

    /**
     * 이메일 기반 인증 타입인지 확인합니다.
     *
     * @return EMAIL 타입이면 true, 아니면 false
     * @author AuthHub Team
     * @since 1.0.0
     */
    public boolean isEmail() {
        return this == EMAIL;
    }

    /**
     * 전화번호 기반 인증 타입인지 확인합니다.
     *
     * @return PHONE 타입이면 true, 아니면 false
     * @author AuthHub Team
     * @since 1.0.0
     */
    public boolean isPhone() {
        return this == PHONE;
    }

    /**
     * 사용자명 기반 인증 타입인지 확인합니다.
     *
     * @return USERNAME 타입이면 true, 아니면 false
     * @author AuthHub Team
     * @since 1.0.0
     */
    public boolean isUsername() {
        return this == USERNAME;
    }
}
