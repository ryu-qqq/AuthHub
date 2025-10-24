package com.ryuqq.authhub.application.auth.config;

import java.time.Duration;

/**
 * JWT 토큰 설정 Properties.
 *
 * <p>JWT Access Token 및 Refresh Token의 유효 기간을 담는 설정 객체입니다.
 * 외부 설정 (예: application.yml, 환경 변수)에서 주입받거나,
 * 기본값을 사용할 수 있습니다.</p>
 *
 * <p><strong>기본 설정:</strong></p>
 * <ul>
 *   <li>Access Token: 15분</li>
 *   <li>Refresh Token: 7일</li>
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>✅ Lombok 금지 - Plain Java Record 사용</li>
 *   <li>✅ Javadoc 완비 - @author, @since 포함</li>
 *   <li>✅ 불변 객체 - Record로 구현</li>
 *   <li>✅ Spring Boot 의존성 없음 - 순수 Application Layer</li>
 * </ul>
 *
 * @param accessTokenValidity Access Token 유효 기간 (null 불가)
 * @param refreshTokenValidity Refresh Token 유효 기간 (null 불가)
 * @author AuthHub Team
 * @since 1.0.0
 */
public record JwtProperties(
        Duration accessTokenValidity,
        Duration refreshTokenValidity
) {
    /**
     * 기본 설정으로 JwtProperties를 생성합니다.
     *
     * <p>Access Token: 15분, Refresh Token: 7일</p>
     *
     * @return 기본 설정이 적용된 JwtProperties
     * @author AuthHub Team
     * @since 1.0.0
     */
    public static JwtProperties defaults() {
        return new JwtProperties(
                Duration.ofMinutes(15),
                Duration.ofDays(7)
        );
    }

    /**
     * Compact Constructor - 입력값 검증.
     *
     * @throws IllegalArgumentException accessTokenValidity 또는 refreshTokenValidity가 null인 경우
     */
    public JwtProperties {
        if (accessTokenValidity == null) {
            throw new IllegalArgumentException("accessTokenValidity cannot be null");
        }

        if (refreshTokenValidity == null) {
            throw new IllegalArgumentException("refreshTokenValidity cannot be null");
        }
    }
}
