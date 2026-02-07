package com.ryuqq.authhub.adapter.in.rest.auth.config;

import com.ryuqq.authhub.adapter.in.rest.auth.filter.GatewayAuthenticationFilter;
import com.ryuqq.authhub.adapter.in.rest.auth.handler.SecurityExceptionHandler;
import com.ryuqq.authhub.adapter.in.rest.auth.paths.SecurityPaths;
import com.ryuqq.authhub.sdk.filter.ServiceTokenAuthenticationFilter;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Spring Security 설정
 *
 * <p>Gateway 연동 기반의 Stateless 인증을 구성합니다. Gateway에서 JWT 검증 후 X-* 헤더로 인증 정보를 전달합니다.
 *
 * <p>인증/인가 흐름:
 *
 * <pre>
 * Gateway (JWT 검증) → X-* 헤더 → GatewayAuthenticationFilter → SecurityContext
 *                                                              ↓
 *                                              @PreAuthorize (권한 기반 접근 제어)
 * </pre>
 *
 * <p>엔드포인트 권한 분류 (SecurityPaths 참조):
 *
 * <ul>
 *   <li>PUBLIC: 인증 불필요 (로그인, 헬스체크, OAuth2)
 *   <li>DOCS: 인증 불필요 (API 문서 - Swagger, REST Docs)
 *   <li>INTERNAL: 서비스 토큰 인증 (ServiceTokenAuthenticationFilter가 검증)
 *   <li>AUTHENTICATED: 인증된 사용자 + @PreAuthorize 권한 검사 (관리 API)
 * </ul>
 *
 * <p>권한 처리:
 *
 * <ul>
 *   <li>URL 기반 역할 검사 제거 → @PreAuthorize 어노테이션으로 대체
 *   <li>ResourceAccessChecker SpEL 함수로 리소스 접근 제어
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@EnableConfigurationProperties(ServiceTokenProperties.class)
public class SecurityConfig {

    private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);

    private final CorsProperties corsProperties;
    private final ServiceTokenProperties serviceTokenProperties;
    private final GatewayAuthenticationFilter gatewayAuthenticationFilter;
    private final SecurityExceptionHandler securityExceptionHandler;

    @Autowired
    public SecurityConfig(
            CorsProperties corsProperties,
            ServiceTokenProperties serviceTokenProperties,
            GatewayAuthenticationFilter gatewayAuthenticationFilter,
            SecurityExceptionHandler securityExceptionHandler) {
        this.corsProperties = corsProperties;
        this.serviceTokenProperties = serviceTokenProperties;
        this.gatewayAuthenticationFilter = gatewayAuthenticationFilter;
        this.securityExceptionHandler = securityExceptionHandler;
    }

    @Bean
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // CSRF 비활성화 (Stateless JWT 사용)
        http.csrf(AbstractHttpConfigurer::disable)
                // CORS 설정
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // 세션 비활성화 (Stateless)
                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 인증 실패 / 접근 거부 핸들러 설정
                .exceptionHandling(
                        exception ->
                                exception
                                        .authenticationEntryPoint(securityExceptionHandler)
                                        .accessDeniedHandler(securityExceptionHandler))
                // 엔드포인트 권한 설정
                .authorizeHttpRequests(this::configureAuthorization);

        // Gateway 인증 필터 추가 (X-* 헤더 기반) - 먼저 등록하여 order 확보
        http.addFilterBefore(
                gatewayAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        // 서비스 토큰 필터 등록 (활성화된 경우 Gateway 필터 앞에 배치)
        if (serviceTokenProperties.isEnabled()) {
            log.info("[SECURITY] ServiceTokenAuthenticationFilter enabled");
            http.addFilterBefore(
                    serviceTokenAuthenticationFilter(), GatewayAuthenticationFilter.class);
        } else {
            log.info(
                    "[SECURITY] ServiceTokenAuthenticationFilter disabled"
                            + " — internal endpoints require standard auth");
        }

        return http.build();
    }

    /**
     * 서비스 토큰 인증 필터
     *
     * <p>Internal API 경로에 대해 X-Service-Name, X-Service-Token 헤더를 검증합니다. 설정된 시크릿과 토큰이 일치하면 인증을
     * 허용합니다.
     */
    private ServiceTokenAuthenticationFilter serviceTokenAuthenticationFilter() {
        String secret = serviceTokenProperties.getSecret();
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException(
                    "security.service-token.secret is required when enabled=true");
        }
        return new ServiceTokenAuthenticationFilter(
                (serviceName, token) ->
                        MessageDigest.isEqual(
                                secret.getBytes(StandardCharsets.UTF_8),
                                token != null
                                        ? token.getBytes(StandardCharsets.UTF_8)
                                        : new byte[0]));
    }

    /**
     * 엔드포인트 권한 설정
     *
     * <p>SecurityPaths에서 정의된 경로별 권한을 설정합니다. 관리 API의 세부 권한은 @PreAuthorize 어노테이션으로 처리됩니다.
     */
    private void configureAuthorization(
            AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry
                    auth) {

        // PUBLIC 엔드포인트 설정 (인증 불필요)
        auth.requestMatchers(SecurityPaths.Public.PATTERNS.toArray(String[]::new)).permitAll();

        // DOCS 엔드포인트 설정 (공개 - API 문서 접근)
        auth.requestMatchers(SecurityPaths.Docs.PATTERNS.toArray(String[]::new)).permitAll();

        // INTERNAL 엔드포인트 설정 (서비스 토큰 필터 활성화 시에만 permitAll — 비활성화 시 authenticated)
        if (serviceTokenProperties.isEnabled()) {
            auth.requestMatchers(SecurityPaths.Internal.PATTERNS.toArray(String[]::new))
                    .permitAll();
        }

        // 그 외 모든 요청은 인증 필요 + @PreAuthorize로 세부 권한 검사
        auth.anyRequest().authenticated();
    }

    /** CORS 설정 */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        if (!corsProperties.getAllowedOrigins().isEmpty()) {
            configuration.setAllowedOrigins(corsProperties.getAllowedOrigins());
        }
        if (!corsProperties.getAllowedMethods().isEmpty()) {
            configuration.setAllowedMethods(corsProperties.getAllowedMethods());
        }
        if (!corsProperties.getAllowedHeaders().isEmpty()) {
            configuration.setAllowedHeaders(corsProperties.getAllowedHeaders());
        }
        if (!corsProperties.getExposedHeaders().isEmpty()) {
            configuration.setExposedHeaders(corsProperties.getExposedHeaders());
        }
        configuration.setAllowCredentials(corsProperties.isAllowCredentials());

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
