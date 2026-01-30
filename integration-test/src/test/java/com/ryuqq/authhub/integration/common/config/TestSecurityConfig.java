package com.ryuqq.authhub.integration.common.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * 테스트용 Security 설정.
 *
 * <p>테스트 환경에서는 보안 제약을 완화하여 테스트 용이성을 높임. 실제 인증/인가는 Gateway 헤더를 통해 시뮬레이션.
 */
@TestConfiguration
@EnableMethodSecurity
public class TestSecurityConfig {

    /**
     * 테스트용 SecurityFilterChain. 모든 요청을 허용하고 CSRF를 비활성화.
     *
     * @param http HttpSecurity
     * @return SecurityFilterChain
     * @throws Exception 설정 오류 시
     */
    @Bean
    @Order(1)
    public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
        return http.securityMatcher("/**")
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .build();
    }

    /**
     * @PreAuthorize 권한 체크용 Mock Access Checker.
     *
     * <p>테스트에서는 모든 권한을 허용합니다.
     */
    @Bean(name = "access")
    @Primary
    public TestAccessChecker accessChecker() {
        return new TestAccessChecker();
    }

    /** 테스트용 Access Checker - 모든 권한 허용 */
    public static class TestAccessChecker {

        public boolean hasPermission(String resource, String action) {
            return true;
        }

        public boolean self(String userId) {
            return true;
        }

        public boolean superAdmin() {
            return true;
        }

        public boolean organization(String organizationId, String action) {
            return true;
        }

        public boolean tenant(String tenantId, String action) {
            return true;
        }

        public boolean role(String roleId, String action) {
            return true;
        }
    }
}
