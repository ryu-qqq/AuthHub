package com.ryuqq.authhub.sdk.filter;

import com.ryuqq.authhub.sdk.context.UserContext;
import com.ryuqq.authhub.sdk.context.UserContextHolder;
import com.ryuqq.authhub.sdk.header.SecurityHeaders;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;
import java.util.function.BiPredicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * ServiceTokenAuthenticationFilter - 내부 서비스 간 통신 인증 필터
 *
 * <p>다른 마이크로서비스에서 AuthHub 또는 다른 서비스로 직접 호출할 때 사용되는 필터입니다. Gateway를 거치지 않는 내부 서비스 간 통신에서 인증을 처리합니다.
 *
 * <p><strong>처리 흐름:</strong>
 *
 * <ol>
 *   <li>X-Service-Name, X-Service-Token 헤더 확인
 *   <li>토큰 검증 (설정된 검증 로직 사용)
 *   <li>서비스 계정 UserContext 생성
 *   <li>UserContextHolder에 저장
 * </ol>
 *
 * <p><strong>사용 방법:</strong>
 *
 * <pre>{@code
 * @Bean
 * public ServiceTokenAuthenticationFilter serviceTokenFilter() {
 *     return new ServiceTokenAuthenticationFilter((serviceName, token) -> {
 *         // 토큰 검증 로직 구현
 *         // 예: 환경변수에서 읽은 시크릿과 비교
 *         return expectedToken.equals(token);
 *     });
 * }
 * }</pre>
 *
 * <p><strong>Internal API 경로 예시:</strong>
 *
 * <ul>
 *   <li>/api/v1/internal/** - 내부 서비스 전용 API
 *   <li>/internal/** - 내부 서비스 전용 API
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public class ServiceTokenAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log =
            LoggerFactory.getLogger(ServiceTokenAuthenticationFilter.class);

    private final BiPredicate<String, String> tokenValidator;
    private final Set<String> internalPathPrefixes;

    /**
     * 기본 생성자 - 내부 경로 기본값 사용
     *
     * @param tokenValidator 서비스 토큰 검증 함수 (serviceName, token) -> boolean
     */
    public ServiceTokenAuthenticationFilter(BiPredicate<String, String> tokenValidator) {
        this(tokenValidator, Set.of("/api/v1/internal", "/internal"));
    }

    /**
     * 커스텀 내부 경로 지정 생성자
     *
     * @param tokenValidator 서비스 토큰 검증 함수
     * @param internalPathPrefixes 내부 API 경로 prefix 목록
     */
    public ServiceTokenAuthenticationFilter(
            BiPredicate<String, String> tokenValidator, Set<String> internalPathPrefixes) {
        this.tokenValidator = tokenValidator;
        this.internalPathPrefixes = internalPathPrefixes;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String serviceName = request.getHeader(SecurityHeaders.SERVICE_NAME);
        String serviceToken = request.getHeader(SecurityHeaders.SERVICE_TOKEN);

        // 서비스 토큰이 없으면 다음 필터로 (일반 요청)
        if (serviceName == null || serviceToken == null) {
            filterChain.doFilter(request, response);
            return;
        }

        // 토큰 검증
        if (!tokenValidator.test(serviceName, serviceToken)) {
            log.warn(
                    "Invalid service token: serviceName={}, uri={}",
                    serviceName,
                    request.getRequestURI());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Invalid service token\"}");
            return;
        }

        try {
            // 서비스 계정 컨텍스트 생성
            UserContext serviceContext = createServiceContext(request, serviceName);
            UserContextHolder.setContext(serviceContext);

            log.debug(
                    "Service account authenticated: serviceName={}, uri={}",
                    serviceName,
                    request.getRequestURI());

            filterChain.doFilter(request, response);

        } finally {
            UserContextHolder.clearContext();
        }
    }

    /**
     * 서비스 계정 UserContext 생성
     *
     * <p>원본 사용자 정보가 있으면 함께 보존합니다.
     *
     * @param request HTTP 요청
     * @param serviceName 서비스 이름
     * @return 서비스 계정 UserContext
     */
    private UserContext createServiceContext(HttpServletRequest request, String serviceName) {
        // 원본 사용자 정보 (서비스가 사용자 대신 호출하는 경우)
        String originalUserId = request.getHeader(SecurityHeaders.ORIGINAL_USER_ID);
        String originalTenantId = request.getHeader(SecurityHeaders.ORIGINAL_TENANT_ID);
        String originalOrgId = request.getHeader(SecurityHeaders.ORIGINAL_ORGANIZATION_ID);
        String correlationId = request.getHeader(SecurityHeaders.CORRELATION_ID);

        return UserContext.builder()
                .userId(originalUserId)
                .tenantId(originalTenantId)
                .organizationId(originalOrgId)
                .correlationId(correlationId)
                .requestSource(serviceName)
                .serviceAccount(true)
                .build();
    }

    /**
     * Internal API 경로에만 필터 적용
     *
     * @param request HTTP 요청
     * @return 필터 제외 여부 (Internal API가 아니면 제외)
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();

        // 서비스 토큰 헤더가 없으면 필터 스킵
        if (request.getHeader(SecurityHeaders.SERVICE_TOKEN) == null) {
            return true;
        }

        // Internal 경로가 아니면 스킵 (선택적)
        // 주석 처리하면 모든 경로에서 서비스 토큰 허용
        // return internalPathPrefixes.stream().noneMatch(uri::startsWith);

        return false;
    }
}
