package com.ryuqq.authhub.adapter.in.rest.auth.config;

import com.ryuqq.authhub.adapter.in.rest.auth.filter.GatewayAuthenticationFilter;
import com.ryuqq.authhub.adapter.in.rest.auth.handler.SecurityExceptionHandler;
import java.util.Locale;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
 *                                              @PreAuthorize (역할 기반 접근 제어)
 * </pre>
 *
 * <p>엔드포인트 권한 분류:
 *
 * <ul>
 *   <li>PUBLIC: 인증 불필요 (api.public-endpoints)
 *   <li>ADMIN: 관리자 권한 필요 (api.admin-endpoints)
 *   <li>AUTHENTICATED: 인증된 사용자만 접근 가능 (나머지)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final SecurityProperties securityProperties;
    private final GatewayAuthenticationFilter gatewayAuthenticationFilter;
    private final SecurityExceptionHandler securityExceptionHandler;

    public SecurityConfig(
            SecurityProperties securityProperties,
            GatewayAuthenticationFilter gatewayAuthenticationFilter,
            SecurityExceptionHandler securityExceptionHandler) {
        this.securityProperties = securityProperties;
        this.gatewayAuthenticationFilter = gatewayAuthenticationFilter;
        this.securityExceptionHandler = securityExceptionHandler;
    }

    @Bean
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                // CSRF 비활성화 (Stateless JWT 사용)
                .csrf(AbstractHttpConfigurer::disable)

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
                .authorizeHttpRequests(this::configureAuthorization)

                // Gateway 인증 필터 추가 (X-* 헤더 기반)
                .addFilterBefore(
                        gatewayAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    /**
     * 엔드포인트 권한 설정
     *
     * <p>SecurityProperties에서 정의된 public/admin 엔드포인트를 등록합니다.
     */
    private void configureAuthorization(
            AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry
                    auth) {

        // PUBLIC 엔드포인트 설정
        configurePublicEndpoints(auth);

        // ADMIN 엔드포인트 설정
        configureAdminEndpoints(auth);

        // 그 외 모든 요청은 인증 필요
        auth.anyRequest().authenticated();
    }

    /** Public 엔드포인트 설정 */
    private void configurePublicEndpoints(
            AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry
                    auth) {

        for (SecurityProperties.PublicEndpoint endpoint : securityProperties.getPublicEndpoints()) {
            if (endpoint.hasMethod()) {
                HttpMethod httpMethod =
                        HttpMethod.valueOf(endpoint.getMethod().toUpperCase(Locale.ROOT));
                auth.requestMatchers(httpMethod, endpoint.getPattern()).permitAll();
            } else {
                auth.requestMatchers(endpoint.getPattern()).permitAll();
            }
        }
    }

    /** Admin 엔드포인트 설정 */
    private void configureAdminEndpoints(
            AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry
                    auth) {

        for (SecurityProperties.AdminEndpoint endpoint : securityProperties.getAdminEndpoints()) {
            String role = endpoint.getRole();
            if (endpoint.hasMethod()) {
                HttpMethod httpMethod =
                        HttpMethod.valueOf(endpoint.getMethod().toUpperCase(Locale.ROOT));
                auth.requestMatchers(httpMethod, endpoint.getPattern()).hasRole(role);
            } else {
                auth.requestMatchers(endpoint.getPattern()).hasRole(role);
            }
        }
    }

    /** CORS 설정 */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        SecurityProperties.CorsProperties corsProps = securityProperties.getCors();

        CorsConfiguration configuration = new CorsConfiguration();

        if (!corsProps.getAllowedOrigins().isEmpty()) {
            configuration.setAllowedOrigins(corsProps.getAllowedOrigins());
        }
        if (!corsProps.getAllowedMethods().isEmpty()) {
            configuration.setAllowedMethods(corsProps.getAllowedMethods());
        }
        if (!corsProps.getAllowedHeaders().isEmpty()) {
            configuration.setAllowedHeaders(corsProps.getAllowedHeaders());
        }
        if (!corsProps.getExposedHeaders().isEmpty()) {
            configuration.setExposedHeaders(corsProps.getExposedHeaders());
        }
        configuration.setAllowCredentials(corsProps.isAllowCredentials());

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
