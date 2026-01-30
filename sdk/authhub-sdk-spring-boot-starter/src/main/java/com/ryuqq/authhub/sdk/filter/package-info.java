/**
 * Gateway 및 서비스 간 통신 인증 필터
 *
 * <p>이 패키지는 HTTP 요청에서 보안 컨텍스트를 설정하는 Servlet 필터를 제공합니다.
 *
 * <p><strong>제공 필터:</strong>
 *
 * <ul>
 *   <li>{@link com.ryuqq.authhub.sdk.filter.GatewayAuthenticationFilter} - Gateway 헤더 기반 인증
 *   <li>{@link com.ryuqq.authhub.sdk.filter.ServiceTokenAuthenticationFilter} - 내부 서비스 간 통신 인증
 * </ul>
 *
 * <p><strong>의존성:</strong>
 *
 * <p>이 패키지의 필터들은 Spring Web과 Jakarta Servlet API에 의존합니다. common-auth 모듈에서는 compileOnly로 선언되어 있으므로,
 * 사용하는 서비스에서 해당 의존성을 제공해야 합니다.
 *
 * <p><strong>Spring Boot 사용 시:</strong>
 *
 * <pre>{@code
 * // 자동 빈 등록
 * @Configuration
 * public class SecurityFilterConfig {
 *
 *     @Bean
 *     public GatewayAuthenticationFilter gatewayAuthenticationFilter() {
 *         return new GatewayAuthenticationFilter();
 *     }
 *
 *     @Bean
 *     public ServiceTokenAuthenticationFilter serviceTokenFilter(
 *             @Value("${service.token.secret}") String secret) {
 *         return new ServiceTokenAuthenticationFilter(
 *             (serviceName, token) -> secret.equals(token)
 *         );
 *     }
 * }
 * }</pre>
 *
 * @author development-team
 * @since 1.0.0
 */
package com.ryuqq.authhub.sdk.filter;
