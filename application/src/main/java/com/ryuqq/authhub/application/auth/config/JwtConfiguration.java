package com.ryuqq.authhub.application.auth.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * JWT Configuration.
 *
 * <p>JWT 관련 설정을 Spring Bean으로 등록하는 Configuration 클래스입니다.</p>
 *
 * <p><strong>설정 외부화:</strong></p>
 * <ul>
 *   <li>{@code auth.jwt.access-token-validity}: Access Token 유효 기간 (기본값: 15m)</li>
 *   <li>{@code auth.jwt.refresh-token-validity}: Refresh Token 유효 기간 (기본값: 7d)</li>
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>✅ Lombok 금지 - Plain Java 사용</li>
 *   <li>✅ Javadoc 완비 - @author, @since 포함</li>
 *   <li>✅ @Value로 외부 설정 주입</li>
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@Configuration
public class JwtConfiguration {

    /**
     * JWT Properties Bean 생성.
     *
     * <p>외부 설정에서 JWT 유효 기간을 주입받습니다.
     * 설정이 없으면 기본값을 사용합니다.</p>
     *
     * @param accessTokenValidity Access Token 유효 기간 (기본값: 15m)
     * @param refreshTokenValidity Refresh Token 유효 기간 (기본값: 7d)
     * @return JwtProperties Bean
     * @author AuthHub Team
     * @since 1.0.0
     */
    @Bean
    public JwtProperties jwtProperties(
            @Value("${auth.jwt.access-token-validity:15m}") final Duration accessTokenValidity,
            @Value("${auth.jwt.refresh-token-validity:7d}") final Duration refreshTokenValidity
    ) {
        return new JwtProperties(accessTokenValidity, refreshTokenValidity);
    }
}
