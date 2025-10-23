package com.ryuqq.authhub.domain.auth.token;

import java.util.UUID;

/**
 * Token의 고유 식별자를 나타내는 Value Object.
 * UUID 기반으로 전역적으로 유일한 토큰 식별자를 제공합니다.
 *
 * <p>이 Record는 불변(immutable) 객체이며, 동등성 비교는 내부 UUID 값으로 수행됩니다.</p>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>Lombok 미사용 - Java 21 Record 사용</li>
 *   <li>불변성 보장 - Record의 본질적 불변성</li>
 *   <li>Law of Demeter 준수</li>
 * </ul>
 *
 * @param value UUID 값 (null 불가)
 * @author AuthHub Team
 * @since 1.0.0
 */
public record TokenId(UUID value) {

    /**
     * Compact constructor - UUID 값의 null 검증을 수행합니다.
     *
     * @throws IllegalArgumentException value가 null인 경우
     */
    public TokenId {
        if (value == null) {
            throw new IllegalArgumentException("TokenId value cannot be null");
        }
    }

    /**
     * 새로운 UUID를 생성하여 TokenId를 만듭니다.
     *
     * @return 새로 생성된 TokenId 인스턴스
     * @author AuthHub Team
     * @since 1.0.0
     */
    public static TokenId newId() {
        return new TokenId(UUID.randomUUID());
    }

    /**
     * 기존 UUID 문자열로부터 TokenId를 생성합니다.
     *
     * @param uuidString UUID 문자열 (null 불가)
     * @return TokenId 인스턴스
     * @throws IllegalArgumentException uuidString이 null이거나 유효하지 않은 UUID 형식인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    public static TokenId fromString(final String uuidString) {
        if (uuidString == null || uuidString.trim().isEmpty()) {
            throw new IllegalArgumentException("UUID string cannot be null or empty");
        }
        try {
            return new TokenId(UUID.fromString(uuidString));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid UUID format: " + uuidString, e);
        }
    }

    /**
     * 기존 UUID 객체로부터 TokenId를 생성합니다.
     *
     * @param uuid UUID 객체 (null 불가)
     * @return TokenId 인스턴스
     * @throws IllegalArgumentException uuid가 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    public static TokenId from(final UUID uuid) {
        return new TokenId(uuid);
    }

    /**
     * TokenId를 문자열로 변환합니다.
     *
     * @return UUID 문자열 표현
     * @author AuthHub Team
     * @since 1.0.0
     */
    public String asString() {
        return value.toString();
    }
}
