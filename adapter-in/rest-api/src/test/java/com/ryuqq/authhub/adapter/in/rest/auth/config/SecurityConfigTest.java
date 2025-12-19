package com.ryuqq.authhub.adapter.in.rest.auth.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import com.ryuqq.authhub.adapter.in.rest.auth.filter.GatewayAuthenticationFilter;
import com.ryuqq.authhub.adapter.in.rest.auth.handler.SecurityExceptionHandler;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * SecurityConfig 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("SecurityConfig 단위 테스트")
class SecurityConfigTest {

    private SecurityConfig securityConfig;
    private CorsProperties corsProperties;
    private GatewayAuthenticationFilter gatewayAuthenticationFilter;
    private SecurityExceptionHandler securityExceptionHandler;

    @BeforeEach
    void setUp() {
        corsProperties = new CorsProperties();
        gatewayAuthenticationFilter = mock(GatewayAuthenticationFilter.class);
        securityExceptionHandler = mock(SecurityExceptionHandler.class);
        securityConfig =
                new SecurityConfig(
                        corsProperties, gatewayAuthenticationFilter, securityExceptionHandler);
    }

    @Nested
    @DisplayName("corsConfigurationSource() 테스트")
    class CorsConfigurationSourceTest {

        private MockHttpServletRequest createMockRequest() {
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setRequestURI("/api/v1/test");
            return request;
        }

        @Test
        @DisplayName("빈 CORS 설정으로 CorsConfigurationSource를 생성한다")
        void corsConfigurationSource_withEmptyConfig_shouldCreateSource() {
            // given & when
            CorsConfigurationSource source = securityConfig.corsConfigurationSource();

            // then
            assertThat(source).isInstanceOf(UrlBasedCorsConfigurationSource.class);
        }

        @Test
        @DisplayName("allowedOrigins가 설정되면 적용된다")
        void corsConfigurationSource_withAllowedOrigins_shouldApply() {
            // given
            corsProperties.setAllowedOrigins(
                    List.of("http://localhost:3000", "https://example.com"));

            // when
            CorsConfigurationSource source = securityConfig.corsConfigurationSource();
            CorsConfiguration config = source.getCorsConfiguration(createMockRequest());

            // then
            assertThat(config).isNotNull();
            assertThat(config.getAllowedOrigins())
                    .containsExactly("http://localhost:3000", "https://example.com");
        }

        @Test
        @DisplayName("allowedMethods가 설정되면 적용된다")
        void corsConfigurationSource_withAllowedMethods_shouldApply() {
            // given
            corsProperties.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));

            // when
            CorsConfigurationSource source = securityConfig.corsConfigurationSource();
            CorsConfiguration config = source.getCorsConfiguration(createMockRequest());

            // then
            assertThat(config).isNotNull();
            assertThat(config.getAllowedMethods()).containsExactly("GET", "POST", "PUT", "DELETE");
        }

        @Test
        @DisplayName("allowedHeaders가 설정되면 적용된다")
        void corsConfigurationSource_withAllowedHeaders_shouldApply() {
            // given
            corsProperties.setAllowedHeaders(List.of("Authorization", "Content-Type"));

            // when
            CorsConfigurationSource source = securityConfig.corsConfigurationSource();
            CorsConfiguration config = source.getCorsConfiguration(createMockRequest());

            // then
            assertThat(config).isNotNull();
            assertThat(config.getAllowedHeaders()).containsExactly("Authorization", "Content-Type");
        }

        @Test
        @DisplayName("exposedHeaders가 설정되면 적용된다")
        void corsConfigurationSource_withExposedHeaders_shouldApply() {
            // given
            corsProperties.setExposedHeaders(List.of("X-Request-Id", "X-Trace-Id"));

            // when
            CorsConfigurationSource source = securityConfig.corsConfigurationSource();
            CorsConfiguration config = source.getCorsConfiguration(createMockRequest());

            // then
            assertThat(config).isNotNull();
            assertThat(config.getExposedHeaders()).containsExactly("X-Request-Id", "X-Trace-Id");
        }

        @Test
        @DisplayName("allowCredentials가 설정되면 적용된다")
        void corsConfigurationSource_withAllowCredentials_shouldApply() {
            // given
            corsProperties.setAllowCredentials(true);

            // when
            CorsConfigurationSource source = securityConfig.corsConfigurationSource();
            CorsConfiguration config = source.getCorsConfiguration(createMockRequest());

            // then
            assertThat(config).isNotNull();
            assertThat(config.getAllowCredentials()).isTrue();
        }

        @Test
        @DisplayName("모든 CORS 설정이 동시에 적용된다")
        void corsConfigurationSource_withFullConfig_shouldApplyAll() {
            // given
            corsProperties.setAllowedOrigins(List.of("http://localhost:3000"));
            corsProperties.setAllowedMethods(List.of("GET", "POST"));
            corsProperties.setAllowedHeaders(List.of("*"));
            corsProperties.setExposedHeaders(List.of("Authorization"));
            corsProperties.setAllowCredentials(true);

            // when
            CorsConfigurationSource source = securityConfig.corsConfigurationSource();
            CorsConfiguration config = source.getCorsConfiguration(createMockRequest());

            // then
            assertThat(config).isNotNull();
            assertThat(config.getAllowedOrigins()).containsExactly("http://localhost:3000");
            assertThat(config.getAllowedMethods()).containsExactly("GET", "POST");
            assertThat(config.getAllowedHeaders()).containsExactly("*");
            assertThat(config.getExposedHeaders()).containsExactly("Authorization");
            assertThat(config.getAllowCredentials()).isTrue();
        }
    }

    @Nested
    @DisplayName("생성자 테스트")
    class ConstructorTest {

        @Test
        @DisplayName("모든 의존성이 주입된다")
        void constructor_shouldInjectAllDependencies() {
            // given & when
            SecurityConfig config =
                    new SecurityConfig(
                            corsProperties, gatewayAuthenticationFilter, securityExceptionHandler);

            // then
            assertThat(config).isNotNull();
        }
    }
}
