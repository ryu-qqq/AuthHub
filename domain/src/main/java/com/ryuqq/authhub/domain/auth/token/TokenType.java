package com.ryuqq.authhub.domain.auth.token;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * JWT 토큰의 타입을 나타내는 Enum.
 *
 * <p>JWT 인증 시스템에서 사용되는 토큰은 용도에 따라 두 가지 타입으로 구분됩니다.</p>
 *
 * <p><strong>지원하는 토큰 타입:</strong></p>
 * <ul>
 *   <li>{@link #ACCESS} - 액세스 토큰 (짧은 만료 시간, API 접근 권한 부여)</li>
 *   <li>{@link #REFRESH} - 리프레시 토큰 (긴 만료 시간, 액세스 토큰 재발급용)</li>
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
public enum TokenType {

    /**
     * 액세스 토큰.
     * API 엔드포인트 접근 권한을 부여하는 단기 유효 토큰입니다.
     * 일반적으로 15분~1시간의 짧은 만료 시간을 가집니다.
     */
    ACCESS("access", "액세스 토큰"),

    /**
     * 리프레시 토큰.
     * 액세스 토큰이 만료되었을 때 새로운 액세스 토큰을 발급받기 위한 장기 유효 토큰입니다.
     * 일반적으로 7일~30일의 긴 만료 시간을 가집니다.
     */
    REFRESH("refresh", "리프레시 토큰");

    private static final Map<String, TokenType> CODE_MAP = Stream.of(values())
            .collect(Collectors.toUnmodifiableMap(TokenType::getCode, type -> type));

    private final String code;
    private final String description;

    /**
     * TokenType enum의 생성자.
     *
     * @param code 타입 코드 (데이터베이스 저장용, 소문자)
     * @param description 타입 설명 (한글)
     */
    TokenType(final String code, final String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 타입 코드를 반환합니다.
     * 주로 데이터베이스 저장 시 사용됩니다.
     *
     * @return 타입 코드 (access, refresh)
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
     * 타입 코드로부터 TokenType을 조회합니다.
     *
     * @param code 타입 코드 (access, refresh - 대소문자 무관)
     * @return 매칭되는 TokenType
     * @throws IllegalArgumentException 매칭되는 타입이 없는 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    public static TokenType fromCode(final String code) {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Token type code cannot be null or empty");
        }

        final TokenType result = CODE_MAP.get(code.trim().toLowerCase());
        if (result == null) {
            throw new IllegalArgumentException("Unknown token type code: " + code);
        }
        return result;
    }

    /**
     * 액세스 토큰 타입인지 확인합니다.
     *
     * @return ACCESS 타입이면 true, 아니면 false
     * @author AuthHub Team
     * @since 1.0.0
     */
    public boolean isAccessToken() {
        return this == ACCESS;
    }

    /**
     * 리프레시 토큰 타입인지 확인합니다.
     *
     * @return REFRESH 타입이면 true, 아니면 false
     * @author AuthHub Team
     * @since 1.0.0
     */
    public boolean isRefreshToken() {
        return this == REFRESH;
    }
}
