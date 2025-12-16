package com.ryuqq.authhub.adapter.in.rest.common;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.ryuqq.authhub.adapter.in.rest.auth.component.JwtClaimsExtractor;
import com.ryuqq.authhub.adapter.in.rest.auth.config.ServiceTokenProperties;
import com.ryuqq.authhub.adapter.in.rest.auth.filter.ServiceTokenAuthenticationFilter;
import java.util.Optional;
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

    /**
     * JWT Claims 추출기 Mock
     *
     * <p>테스트에서는 JWT 검증을 수행하지 않으므로 항상 empty를 반환합니다.
     */
    @Bean
    public JwtClaimsExtractor jwtClaimsExtractor() {
        JwtClaimsExtractor mock = mock(JwtClaimsExtractor.class);
        when(mock.extractClaims(anyString())).thenReturn(Optional.empty());
        return mock;
    }

    /**
     * Service Token Properties Mock
     *
     * <p>테스트에서는 서비스 토큰 인증을 비활성화합니다.
     */
    @Bean
    public ServiceTokenProperties serviceTokenProperties() {
        ServiceTokenProperties mock = mock(ServiceTokenProperties.class);
        when(mock.isEnabled()).thenReturn(false);
        return mock;
    }

    /**
     * Service Token Authentication Filter Mock
     *
     * <p>테스트에서는 서비스 토큰 인증 필터를 mock으로 대체합니다.
     */
    @Bean
    public ServiceTokenAuthenticationFilter serviceTokenAuthenticationFilter() {
        return mock(ServiceTokenAuthenticationFilter.class);
    }
}
