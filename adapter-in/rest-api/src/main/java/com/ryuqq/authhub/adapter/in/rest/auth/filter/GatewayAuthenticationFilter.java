package com.ryuqq.authhub.adapter.in.rest.auth.filter;

import com.ryuqq.authhub.adapter.in.rest.auth.component.SecurityContext;
import com.ryuqq.authhub.adapter.in.rest.auth.component.SecurityContextHolder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Gateway 헤더 기반 인증 필터
 *
 * <p>Gateway에서 전달하는 X-* 헤더를 파싱하여 SecurityContext를 설정합니다.
 *
 * <p><strong>인증 흐름:</strong>
 *
 * <ol>
 *   <li>X-User-Id 헤더가 있으면 Gateway 인증 사용
 *   <li>X-User-Id 없으면 Anonymous 처리
 * </ol>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class GatewayAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(GatewayAuthenticationFilter.class);
    private static final String MDC_TRACE_ID_KEY = "traceId";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            SecurityContext context = buildSecurityContext(request);
            SecurityContextHolder.setContext(context);

            // MDC에 traceId 저장 (로깅 및 ApiResponse에서 사용)
            String traceId = context.getTraceId();
            if (traceId != null) {
                MDC.put(MDC_TRACE_ID_KEY, traceId);
            }

            if (context.isAuthenticated()) {
                synchronizeWithSpringSecurityContext(context);
            }

            filterChain.doFilter(request, response);

        } finally {
            MDC.remove(MDC_TRACE_ID_KEY);
            SecurityContextHolder.clearContext();
            org.springframework.security.core.context.SecurityContextHolder.clearContext();
        }
    }

    private SecurityContext buildSecurityContext(HttpServletRequest request) {
        logAuthHeaders(request);

        String userId = GatewayHeaderExtractor.getUserId(request);
        if (StringUtils.hasText(userId)) {
            log.debug("[AUTH] Gateway 인증: userId={}", userId);
            return buildGatewaySecurityContext(request, userId);
        }

        log.debug("[AUTH] Anonymous 처리 (X-User-Id 헤더 없음)");
        return SecurityContext.anonymous();
    }

    private void logAuthHeaders(HttpServletRequest request) {
        log.debug(
                "[AUTH] URI: {} {}, X-User-Id: {}",
                request.getMethod(),
                request.getRequestURI(),
                request.getHeader(GatewayHeaderExtractor.HEADER_USER_ID));
    }

    private SecurityContext buildGatewaySecurityContext(HttpServletRequest request, String userId) {
        String tenantId = GatewayHeaderExtractor.getTenantId(request);
        String organizationId = GatewayHeaderExtractor.getOrganizationId(request);
        Set<String> roles = GatewayHeaderExtractor.getRoles(request);
        Set<String> permissions = GatewayHeaderExtractor.getPermissions(request);
        String traceId = GatewayHeaderExtractor.getTraceId(request);

        log.debug(
                "[AUTH] SecurityContext: userId={}, tenantId={}, roles={}",
                userId,
                tenantId,
                roles);

        return SecurityContext.builder()
                .userId(userId)
                .tenantId(tenantId)
                .organizationId(organizationId)
                .roles(roles)
                .permissions(permissions)
                .traceId(traceId)
                .build();
    }

    private void synchronizeWithSpringSecurityContext(SecurityContext context) {
        Set<GrantedAuthority> authorities =
                context.getRoles().stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toSet());

        context.getPermissions().stream()
                .map(SimpleGrantedAuthority::new)
                .forEach(authorities::add);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(context.getUserId(), null, authorities);

        org.springframework.security.core.context.SecurityContextHolder.getContext()
                .setAuthentication(authentication);
    }
}
