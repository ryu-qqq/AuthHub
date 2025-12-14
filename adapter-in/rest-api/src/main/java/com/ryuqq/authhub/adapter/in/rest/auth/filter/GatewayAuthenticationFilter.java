package com.ryuqq.authhub.adapter.in.rest.auth.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryuqq.authhub.adapter.in.rest.auth.component.JwtClaimsExtractor;
import com.ryuqq.authhub.adapter.in.rest.auth.component.JwtClaimsExtractor.JwtClaims;
import com.ryuqq.authhub.adapter.in.rest.auth.component.SecurityContext;
import com.ryuqq.authhub.adapter.in.rest.auth.component.SecurityContextHolder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Gateway 헤더 기반 인증 필터 (JWT Fallback 지원)
 *
 * <p>Gateway에서 전달하는 X-* 헤더를 파싱하여 SecurityContext를 설정합니다.
 *
 * <p><strong>인증 우선순위:</strong>
 *
 * <ol>
 *   <li>X-User-Id 헤더가 있으면 Gateway 인증 사용 (프로덕션 권장)
 *   <li>X-User-Id 없고 Authorization: Bearer가 있으면 JWT 직접 검증 (로컬 개발용)
 *   <li>둘 다 없으면 Anonymous 처리
 * </ol>
 *
 * <p><strong>JWT Fallback 사용 시나리오:</strong>
 *
 * <ul>
 *   <li>로컬 개발 환경에서 Gateway 없이 직접 호출
 *   <li>프론트엔드 직접 연동 테스트
 *   <li>AuthHub API 직접 테스트
 * </ul>
 *
 * <p>하이브리드 권한 체계:
 *
 * <ul>
 *   <li>X-User-Roles: 역할 기반 권한 (콤마 구분, 예: ROLE_SUPER_ADMIN,ROLE_ADMIN)
 *   <li>X-Permissions: 리소스 기반 권한 (콤마 구분, 예: user:read,order:write)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class GatewayAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(GatewayAuthenticationFilter.class);

    private static final String HEADER_USER_ID = "X-User-Id";
    private static final String HEADER_TENANT_ID = "X-Tenant-Id";
    private static final String HEADER_ORGANIZATION_ID = "X-Organization-Id";
    private static final String HEADER_ROLES = "X-User-Roles";
    private static final String HEADER_PERMISSIONS = "X-Permissions";
    private static final String HEADER_TRACE_ID = "X-Trace-Id";
    private static final String HEADER_AUTHORIZATION = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final ObjectMapper objectMapper;
    private final JwtClaimsExtractor jwtClaimsExtractor;

    public GatewayAuthenticationFilter(
            ObjectMapper objectMapper, JwtClaimsExtractor jwtClaimsExtractor) {
        this.objectMapper = objectMapper;
        this.jwtClaimsExtractor = jwtClaimsExtractor;
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
        // 디버그: 인증 관련 헤더 로깅
        logAuthHeaders(request);

        // 1. Gateway 인증 (X-User-Id 헤더) - 우선순위 최상위
        String userId = request.getHeader(HEADER_USER_ID);
        if (StringUtils.hasText(userId)) {
            log.info("[AUTH] Gateway 인증 시도: userId={}", userId);
            return buildGatewaySecurityContext(request, userId);
        }

        // 2. JWT Fallback (Authorization: Bearer) - 로컬 개발/직접 호출용
        Optional<JwtClaims> jwtClaims = extractJwtClaimsFromAuthorizationHeader(request);
        if (jwtClaims.isPresent()) {
            log.info("[AUTH] JWT Fallback 인증 시도");
            return buildJwtSecurityContext(jwtClaims.get());
        }

        // 3. Anonymous
        log.info("[AUTH] Anonymous 처리 (인증 헤더 없음)");
        return SecurityContext.anonymous();
    }

    /**
     * 인증 관련 헤더를 로깅합니다 (디버깅용).
     *
     * @param request HTTP 요청
     */
    private void logAuthHeaders(HttpServletRequest request) {
        log.info("[AUTH-HEADERS] URI: {} {}", request.getMethod(), request.getRequestURI());
        log.info("[AUTH-HEADERS] X-User-Id: {}", request.getHeader(HEADER_USER_ID));
        log.info("[AUTH-HEADERS] X-Tenant-Id: {}", request.getHeader(HEADER_TENANT_ID));
        log.info("[AUTH-HEADERS] X-User-Roles: {}", request.getHeader(HEADER_ROLES));
        log.info("[AUTH-HEADERS] Authorization: {}",
                request.getHeader(HEADER_AUTHORIZATION) != null ? "[PRESENT]" : "[ABSENT]");
    }

    /**
     * Gateway 인증 컨텍스트 생성
     *
     * <p>X-* 헤더에서 인증 정보를 추출합니다.
     */
    private SecurityContext buildGatewaySecurityContext(HttpServletRequest request, String userId) {
        String tenantId = parseStringHeader(request.getHeader(HEADER_TENANT_ID));
        String organizationId = parseStringHeader(request.getHeader(HEADER_ORGANIZATION_ID));
        Set<String> roles = parseRoles(request.getHeader(HEADER_ROLES));
        Set<String> permissions = parsePermissions(request.getHeader(HEADER_PERMISSIONS));
        String traceId = request.getHeader(HEADER_TRACE_ID);

        log.info("[AUTH] Gateway SecurityContext 생성: userId={}, tenantId={}, roles={}, permissions={}",
                userId, tenantId, roles, permissions);

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
     * Authorization 헤더에서 JWT Claims 추출
     *
     * @param request HTTP 요청
     * @return JWT Claims (유효하지 않으면 empty)
     */
    private Optional<JwtClaims> extractJwtClaimsFromAuthorizationHeader(
            HttpServletRequest request) {
        String authHeader = request.getHeader(HEADER_AUTHORIZATION);
        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith(BEARER_PREFIX)) {
            return Optional.empty();
        }

        String token = authHeader.substring(BEARER_PREFIX.length());
        return jwtClaimsExtractor.extractClaims(token);
    }

    /**
     * JWT Claims로 SecurityContext 생성
     *
     * <p>JWT에서 추출한 정보로 인증 컨텍스트를 생성합니다.
     */
    private SecurityContext buildJwtSecurityContext(JwtClaims claims) {
        return SecurityContext.builder()
                .userId(claims.userId())
                .tenantId(claims.tenantId())
                .organizationId(claims.organizationId())
                .roles(claims.roles())
                .permissions(claims.permissions())
                .traceId(null)
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

    /**
     * X-User-Roles 헤더 파싱
     *
     * <p>콤마로 구분된 역할 문자열을 파싱합니다. 예: "SUPER_ADMIN,ADMIN"
     *
     * <p>Spring Security의 hasRole()은 자동으로 ROLE_ prefix를 추가하여 검사하므로,
     * Gateway에서 prefix 없이 보내도 여기서 자동으로 ROLE_ prefix를 추가합니다.
     *
     * @param rolesHeader 역할 헤더 값
     * @return ROLE_ prefix가 추가된 역할 Set
     */
    private Set<String> parseRoles(String rolesHeader) {
        if (!StringUtils.hasText(rolesHeader)) {
            return Set.of();
        }
        return Arrays.stream(rolesHeader.split(","))
                .map(String::trim)
                .filter(StringUtils::hasText)
                .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role)
                .collect(Collectors.toSet());
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
