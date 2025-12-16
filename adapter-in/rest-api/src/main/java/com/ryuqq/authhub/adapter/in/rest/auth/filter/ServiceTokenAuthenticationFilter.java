package com.ryuqq.authhub.adapter.in.rest.auth.filter;

import com.ryuqq.authhub.adapter.in.rest.auth.config.ServiceTokenProperties;
import com.ryuqq.authhub.adapter.in.rest.auth.paths.ApiPaths;
import com.ryuqq.authhub.adapter.in.rest.auth.paths.SecurityPaths;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * 서비스 토큰 인증 필터 (Internal API용)
 *
 * <p>X-Service-Token 헤더를 검증하여 서비스 간 통신을 인증합니다.
 *
 * <p><strong>대상 경로:</strong> /api/v1/auth/system/** (System API)
 *
 * <p><strong>인증 흐름:</strong>
 *
 * <pre>
 * 1. System API 경로 요청인지 확인
 * 2. X-Service-Token 헤더 추출
 * 3. 설정된 시크릿과 비교
 * 4. 일치하면 ROLE_SERVICE 권한 부여
 * </pre>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class ServiceTokenAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log =
            LoggerFactory.getLogger(ServiceTokenAuthenticationFilter.class);

    private static final String SERVICE_PRINCIPAL = "SERVICE";
    private static final String ROLE_SERVICE = "ROLE_SERVICE";

    private final ServiceTokenProperties serviceTokenProperties;
    private final AntPathMatcher pathMatcher;

    public ServiceTokenAuthenticationFilter(ServiceTokenProperties serviceTokenProperties) {
        this.serviceTokenProperties = serviceTokenProperties;
        this.pathMatcher = new AntPathMatcher();
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestPath = request.getRequestURI();

        // System API 경로가 아니면 스킵
        if (!isSystemApiPath(requestPath)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 서비스 토큰 인증이 비활성화되어 있으면 스킵 (개발 환경)
        if (!serviceTokenProperties.isEnabled()) {
            log.debug(
                    "[SERVICE-TOKEN] Service token authentication disabled, skipping for: {}",
                    requestPath);
            setServiceAuthentication();
            filterChain.doFilter(request, response);
            return;
        }

        // X-Service-Token 헤더 검증
        String serviceToken = request.getHeader(SecurityPaths.Headers.SERVICE_TOKEN);
        if (!isValidServiceToken(serviceToken)) {
            log.warn("[SERVICE-TOKEN] Invalid or missing service token for: {}", requestPath);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter()
                    .write(
                            "{\"error\":\"Unauthorized\",\"message\":\"Invalid or missing"
                                    + " X-Service-Token\"}");
            return;
        }

        log.info("[SERVICE-TOKEN] Service token validated for: {}", requestPath);
        setServiceAuthentication();
        filterChain.doFilter(request, response);
    }

    /**
     * System API 경로인지 확인
     *
     * @param requestPath 요청 경로
     * @return System API 경로이면 true
     */
    private boolean isSystemApiPath(String requestPath) {
        String systemPattern = ApiPaths.System.BASE + "/**";
        return pathMatcher.match(systemPattern, requestPath);
    }

    /**
     * 서비스 토큰 유효성 검증
     *
     * @param token X-Service-Token 헤더 값
     * @return 유효하면 true
     */
    private boolean isValidServiceToken(String token) {
        if (!StringUtils.hasText(token)) {
            return false;
        }
        String expectedSecret = serviceTokenProperties.getSecret();
        return StringUtils.hasText(expectedSecret) && expectedSecret.equals(token);
    }

    /**
     * 서비스 인증 설정
     *
     * <p>ROLE_SERVICE 권한으로 SecurityContext를 설정합니다.
     */
    private void setServiceAuthentication() {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        SERVICE_PRINCIPAL, null, Set.of(new SimpleGrantedAuthority(ROLE_SERVICE)));

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
