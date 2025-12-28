package com.ryuqq.authhub.integration.config;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.ryuqq.authhub.adapter.in.rest.auth.component.ResourceAccessChecker;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * 통합 테스트용 Security 설정
 *
 * <p>통합 테스트에서는 인증/인가를 비활성화하여 API 기능 테스트에 집중합니다.
 *
 * <p>{@code @Order(1)}을 사용하여 메인 {@code SecurityConfig}보다 높은 우선순위로 설정합니다. {@code
 * ResourceAccessChecker} Mock을 제공하여 모든 권한 검사를 통과시킵니다.
 *
 * @author Development Team
 * @since 1.0.0
 */
@TestConfiguration
public class TestSecurityConfig {

    /**
     * 테스트용 Security Filter Chain - 모든 요청 허용
     *
     * <p>{@code @Order(1)}로 메인 설정보다 먼저 적용되고, 모든 요청을 매칭하여 처리합니다.
     *
     * @param http HttpSecurity 설정 객체
     * @return SecurityFilterChain 인스턴스
     */
    @Bean
    @Order(1)
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public SecurityFilterChain integrationTestSecurityFilterChain(HttpSecurity http)
            throws Exception {
        return http.securityMatcher("/**")
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .build();
    }

    /**
     * 테스트용 ResourceAccessChecker Mock
     *
     * <p>모든 권한 검사를 통과시켜 API 기능 테스트에 집중할 수 있도록 합니다.
     *
     * <p>{@code @PreAuthorize("@access.superAdmin()")} SpEL 표현식에서 "access" 이름으로 빈을 찾으므로 빈 이름을
     * "access"로 지정합니다.
     *
     * @return Mock ResourceAccessChecker
     */
    @Bean("access")
    @Primary
    public ResourceAccessChecker resourceAccessChecker() {
        ResourceAccessChecker mock = mock(ResourceAccessChecker.class);

        // 모든 권한 검사 통과
        when(mock.superAdmin()).thenReturn(true);
        when(mock.tenantAdmin()).thenReturn(true);
        when(mock.orgAdmin()).thenReturn(true);
        when(mock.authenticated()).thenReturn(true);
        when(mock.myself(anyString())).thenReturn(true);
        when(mock.myselfOr(anyString(), anyString())).thenReturn(true);
        when(mock.hasPermission(anyString())).thenReturn(true);
        when(mock.hasAnyPermission(any(String[].class))).thenReturn(true);
        when(mock.hasAllPermissions(any(String[].class))).thenReturn(true);
        when(mock.hasRole(anyString())).thenReturn(true);
        when(mock.hasAnyRole(any(String[].class))).thenReturn(true);
        when(mock.sameTenant(anyString())).thenReturn(true);
        when(mock.sameOrganization(anyString())).thenReturn(true);
        when(mock.tenant(anyString(), anyString())).thenReturn(true);
        when(mock.organization(anyString(), anyString())).thenReturn(true);
        when(mock.user(anyString(), anyString())).thenReturn(true);
        when(mock.role(anyString(), anyString())).thenReturn(true);
        when(mock.permission(anyString(), anyString())).thenReturn(true);

        return mock;
    }
}
