package com.ryuqq.authhub.sdk.filter;

import com.ryuqq.authhub.sdk.context.UserContext;
import com.ryuqq.authhub.sdk.context.UserContextHolder;
import com.ryuqq.authhub.sdk.header.GatewayHeaderParser;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * GatewayAuthenticationFilter - Gateway 헤더 기반 인증 필터
 *
 * <p>Gateway에서 전달된 HTTP 헤더를 파싱하여 UserContext를 생성하고 ThreadLocal에 저장합니다.
 *
 * <p><strong>처리 흐름:</strong>
 *
 * <ol>
 *   <li>Gateway에서 JWT 검증 후 X-User-Id, X-Tenant-Id 등 헤더 주입
 *   <li>이 필터에서 헤더 파싱 → UserContext 생성
 *   <li>UserContextHolder에 저장 (ThreadLocal)
 *   <li>Controller/Service에서 UserContextHolder.getContext()로 접근
 *   <li>요청 완료 후 자동 정리 (finally 블록)
 * </ol>
 *
 * <p><strong>사용 방법:</strong>
 *
 * <pre>{@code
 * // Spring Boot 자동 구성 시
 * @Bean
 * public GatewayAuthenticationFilter gatewayAuthenticationFilter() {
 *     return new GatewayAuthenticationFilter();
 * }
 *
 * // 또는 FilterRegistrationBean으로 순서 지정
 * @Bean
 * public FilterRegistrationBean<GatewayAuthenticationFilter> gatewayAuthFilterRegistration() {
 *     FilterRegistrationBean<GatewayAuthenticationFilter> registration = new FilterRegistrationBean<>();
 *     registration.setFilter(new GatewayAuthenticationFilter());
 *     registration.setOrder(Ordered.HIGHEST_PRECEDENCE + 10);
 *     return registration;
 * }
 * }</pre>
 *
 * <p><strong>헤더 → UserContext 매핑:</strong>
 *
 * <ul>
 *   <li>X-User-Id → userId
 *   <li>X-Tenant-Id → tenantId
 *   <li>X-Organization-Id → organizationId
 *   <li>X-User-Email → email
 *   <li>X-User-Roles → roles (쉼표 구분)
 *   <li>X-User-Permissions → permissions (쉼표 구분)
 *   <li>X-Correlation-Id → correlationId
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 * @see GatewayHeaderParser
 * @see UserContextHolder
 */
public class GatewayAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(GatewayAuthenticationFilter.class);

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            // 1. Gateway 헤더 파싱 → UserContext 생성
            UserContext context = GatewayHeaderParser.parse(request::getHeader);

            // 2. ThreadLocal에 저장
            UserContextHolder.setContext(context);

            // 3. 디버그 로깅
            if (log.isDebugEnabled()) {
                logUserContext(request, context);
            }

            // 4. 다음 필터로 진행
            filterChain.doFilter(request, response);

        } finally {
            // 5. 요청 완료 후 반드시 정리 (메모리 누수 방지)
            UserContextHolder.clearContext();
        }
    }

    /**
     * 사용자 컨텍스트 정보 로깅
     *
     * @param request HTTP 요청
     * @param context 파싱된 UserContext
     */
    private void logUserContext(HttpServletRequest request, UserContext context) {
        if (context.isAuthenticated()) {
            log.debug(
                    "Gateway auth context set: userId={}, tenantId={}, orgId={}, roles={},"
                            + " permissions={}, uri={}",
                    context.getUserId(),
                    context.getTenantId(),
                    context.getOrganizationId(),
                    context.getRoles().size(),
                    context.getPermissions().size(),
                    request.getRequestURI());
        } else if (context.isServiceAccount()) {
            log.debug(
                    "Service account request: correlationId={}, uri={}",
                    context.getCorrelationId(),
                    request.getRequestURI());
        } else {
            log.debug("Anonymous request: uri={}", request.getRequestURI());
        }
    }

    /**
     * 특정 경로 제외 여부 확인 (선택적 오버라이드)
     *
     * <p>기본적으로 모든 요청에 대해 필터 적용. 필요 시 오버라이드하여 특정 경로 제외.
     *
     * @param request HTTP 요청
     * @return 필터 제외 여부
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // 기본: 모든 요청 필터링
        // 필요 시 health check, actuator 등 제외 가능
        String uri = request.getRequestURI();
        return uri.startsWith("/actuator/") || uri.equals("/health");
    }
}
