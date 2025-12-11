package com.ryuqq.authhub.adapter.in.rest.common;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Controller 단위 테스트용 Security 설정
 *
 * <p>@WebMvcTest에서 Security를 비활성화하고 모든 요청을 허용합니다. 인증/인가 테스트는 별도의 통합 테스트에서 수행합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
@TestConfiguration
public class ControllerTestSecurityConfig {

    @Bean
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .build();
    }
}
