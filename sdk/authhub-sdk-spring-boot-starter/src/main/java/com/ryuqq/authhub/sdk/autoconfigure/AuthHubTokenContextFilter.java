package com.ryuqq.authhub.sdk.autoconfigure;

import com.ryuqq.authhub.sdk.auth.ThreadLocalTokenResolver;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * HTTP 요청에서 Authorization 헤더의 토큰을 추출하여 ThreadLocalTokenResolver에 설정하는 필터.
 *
 * <p>이 필터를 사용하면 각 HTTP 요청에서 받은 토큰을 AuthHub API 호출 시 자동으로 전달할 수 있습니다.
 *
 * <p>사용 예시:
 *
 * <pre>
 * &#64;Configuration
 * public class FilterConfig {
 *
 *     &#64;Bean
 *     public FilterRegistrationBean&lt;AuthHubTokenContextFilter&gt; authHubTokenFilter() {
 *         FilterRegistrationBean&lt;AuthHubTokenContextFilter&gt; bean = new FilterRegistrationBean&lt;&gt;();
 *         bean.setFilter(new AuthHubTokenContextFilter());
 *         bean.addUrlPatterns("/api/*");
 *         bean.setOrder(Ordered.HIGHEST_PRECEDENCE + 10);
 *         return bean;
 *     }
 * }
 * </pre>
 */
public class AuthHubTokenContextFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader(AUTHORIZATION_HEADER);

        if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
            String token = authHeader.substring(BEARER_PREFIX.length());
            ThreadLocalTokenResolver.set(token);
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            ThreadLocalTokenResolver.clear();
        }
    }
}
