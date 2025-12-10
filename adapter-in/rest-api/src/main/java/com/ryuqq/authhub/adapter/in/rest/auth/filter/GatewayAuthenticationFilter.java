package com.ryuqq.authhub.adapter.in.rest.auth.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryuqq.authhub.adapter.in.rest.auth.component.SecurityContext;
import com.ryuqq.authhub.adapter.in.rest.auth.component.SecurityContextHolder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
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
 * <p>처리 흐름:
 *
 * <ol>
 *   <li>X-User-Id 헤더 확인 (있으면 인증된 요청, UUID 형식)
 *   <li>X-Tenant-Id, X-Roles, X-Permissions 헤더 추출
 *   <li>SecurityContext 설정
 *   <li>Spring SecurityContextHolder에 동기화
 * </ol>
 *
 * <p>X-User-Id 헤더가 없는 경우:
 *
 * <ul>
 *   <li>Public API 요청으로 판단
 *   <li>Anonymous SecurityContext 설정
 *   <li>인증이 필요한 API는 Spring Security에서 401 반환
 * </ul>
 *
 * <p>하이브리드 권한 체계:
 *
 * <ul>
 *   <li>X-Roles: 역할 기반 권한 (JSON 배열, 예: ["ROLE_TENANT_ADMIN"])
 *   <li>X-Permissions: 리소스 기반 권한 (콤마 구분, 예: user:read,order:write)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class GatewayAuthenticationFilter extends OncePerRequestFilter {

    private static final String HEADER_USER_ID = "X-User-Id";
    private static final String HEADER_TENANT_ID = "X-Tenant-Id";
    private static final String HEADER_ORGANIZATION_ID = "X-Organization-Id";
    private static final String HEADER_ROLES = "X-Roles";
    private static final String HEADER_PERMISSIONS = "X-Permissions";
    private static final String HEADER_TRACE_ID = "X-Trace-Id";

    private final ObjectMapper objectMapper;

    public GatewayAuthenticationFilter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            SecurityContext context = buildSecurityContext(request);
            SecurityContextHolder.setContext(context);

            if (context.isAuthenticated()) {
                synchronizeWithSpringSecurityContext(context);
            }

            filterChain.doFilter(request, response);

        } finally {
            SecurityContextHolder.clearContext();
            org.springframework.security.core.context.SecurityContextHolder.clearContext();
        }
    }

    private SecurityContext buildSecurityContext(HttpServletRequest request) {
        String userId = request.getHeader(HEADER_USER_ID);

        if (!StringUtils.hasText(userId)) {
            return SecurityContext.anonymous();
        }

        String tenantId = parseStringHeader(request.getHeader(HEADER_TENANT_ID));
        String organizationId = parseStringHeader(request.getHeader(HEADER_ORGANIZATION_ID));
        Set<String> roles = parseRoles(request.getHeader(HEADER_ROLES));
        Set<String> permissions = parsePermissions(request.getHeader(HEADER_PERMISSIONS));
        String traceId = request.getHeader(HEADER_TRACE_ID);

        return SecurityContext.builder()
                .userId(userId)
                .tenantId(tenantId)
                .organizationId(organizationId)
                .roles(roles)
                .permissions(permissions)
                .traceId(traceId)
                .build();
    }

    /**
     * 문자열 헤더 파싱 (UUIDv7 형식)
     *
     * <p>빈 값이면 null을 반환합니다.
     *
     * @param header 헤더 값
     * @return 유효한 문자열이면 그대로, 빈 값이면 null
     */
    private String parseStringHeader(String header) {
        return StringUtils.hasText(header) ? header : null;
    }

    private Set<String> parseRoles(String rolesHeader) {
        if (!StringUtils.hasText(rolesHeader)) {
            return Set.of();
        }
        try {
            String[] roles = objectMapper.readValue(rolesHeader, String[].class);
            return Set.of(roles);
        } catch (JsonProcessingException e) {
            return Set.of();
        }
    }

    /**
     * X-Permissions 헤더 파싱
     *
     * <p>콤마로 구분된 권한 문자열을 파싱합니다. 예: "user:read,order:write"
     *
     * @param permissionsHeader 권한 헤더 값
     * @return 권한 Set
     */
    private Set<String> parsePermissions(String permissionsHeader) {
        if (!StringUtils.hasText(permissionsHeader)) {
            return Set.of();
        }
        return Arrays.stream(permissionsHeader.split(","))
                .map(String::trim)
                .filter(StringUtils::hasText)
                .collect(Collectors.toSet());
    }

    private void synchronizeWithSpringSecurityContext(SecurityContext context) {
        // Roles를 GrantedAuthority로 변환
        Set<GrantedAuthority> authorities =
                context.getRoles().stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toSet());

        // Permissions도 GrantedAuthority로 추가 (Spring Security에서 hasAuthority() 사용 가능)
        context.getPermissions().stream()
                .map(SimpleGrantedAuthority::new)
                .forEach(authorities::add);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(context.getUserId(), null, authorities);

        org.springframework.security.core.context.SecurityContextHolder.getContext()
                .setAuthentication(authentication);
    }
}
