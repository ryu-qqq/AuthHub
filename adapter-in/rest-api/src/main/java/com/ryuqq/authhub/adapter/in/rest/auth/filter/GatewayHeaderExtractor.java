package com.ryuqq.authhub.adapter.in.rest.auth.filter;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.util.StringUtils;

/**
 * Gateway 헤더 추출 및 파싱 유틸리티
 *
 * <p>GatewayAuthenticationFilter의 헤더 파싱 로직을 분리한 헬퍼 클래스입니다.
 *
 * @author development-team
 * @since 1.0.0
 */
final class GatewayHeaderExtractor {

    static final String HEADER_USER_ID = "X-User-Id";
    static final String HEADER_TENANT_ID = "X-Tenant-Id";
    static final String HEADER_ORGANIZATION_ID = "X-Organization-Id";
    static final String HEADER_ROLES = "X-User-Roles";
    static final String HEADER_PERMISSIONS = "X-Permissions";
    static final String HEADER_TRACE_ID = "X-Trace-Id";
    static final String HEADER_AUTHORIZATION = "Authorization";
    static final String BEARER_PREFIX = "Bearer ";

    private GatewayHeaderExtractor() {
        throw new AssertionError("Utility class");
    }

    /**
     * 문자열 헤더 파싱
     *
     * @param header 헤더 값
     * @return 유효한 문자열이면 그대로, 빈 값이면 null
     */
    static String parseStringHeader(String header) {
        return StringUtils.hasText(header) ? header : null;
    }

    /**
     * X-User-Roles 헤더 파싱
     *
     * <p>콤마로 구분된 역할 문자열을 파싱합니다. ROLE_ prefix가 없으면 자동 추가합니다.
     *
     * @param rolesHeader 역할 헤더 값
     * @return ROLE_ prefix가 추가된 역할 Set
     */
    static Set<String> parseRoles(String rolesHeader) {
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
     * <p>콤마로 구분된 권한 문자열을 파싱합니다.
     *
     * @param permissionsHeader 권한 헤더 값
     * @return 권한 Set
     */
    static Set<String> parsePermissions(String permissionsHeader) {
        if (!StringUtils.hasText(permissionsHeader)) {
            return Set.of();
        }
        return Arrays.stream(permissionsHeader.split(","))
                .map(String::trim)
                .filter(StringUtils::hasText)
                .collect(Collectors.toSet());
    }

    /**
     * 요청에서 사용자 ID 추출
     *
     * @param request HTTP 요청
     * @return 사용자 ID (없으면 null)
     */
    static String getUserId(HttpServletRequest request) {
        return request.getHeader(HEADER_USER_ID);
    }

    /**
     * 요청에서 테넌트 ID 추출
     *
     * @param request HTTP 요청
     * @return 테넌트 ID (없으면 null)
     */
    static String getTenantId(HttpServletRequest request) {
        return parseStringHeader(request.getHeader(HEADER_TENANT_ID));
    }

    /**
     * 요청에서 조직 ID 추출
     *
     * @param request HTTP 요청
     * @return 조직 ID (없으면 null)
     */
    static String getOrganizationId(HttpServletRequest request) {
        return parseStringHeader(request.getHeader(HEADER_ORGANIZATION_ID));
    }

    /**
     * 요청에서 역할 추출
     *
     * @param request HTTP 요청
     * @return 역할 Set
     */
    static Set<String> getRoles(HttpServletRequest request) {
        return parseRoles(request.getHeader(HEADER_ROLES));
    }

    /**
     * 요청에서 권한 추출
     *
     * @param request HTTP 요청
     * @return 권한 Set
     */
    static Set<String> getPermissions(HttpServletRequest request) {
        return parsePermissions(request.getHeader(HEADER_PERMISSIONS));
    }

    /**
     * 요청에서 트레이스 ID 추출
     *
     * @param request HTTP 요청
     * @return 트레이스 ID (없으면 null)
     */
    static String getTraceId(HttpServletRequest request) {
        return request.getHeader(HEADER_TRACE_ID);
    }

    /**
     * Authorization 헤더에서 Bearer 토큰 추출
     *
     * @param request HTTP 요청
     * @return Bearer 토큰 (없거나 형식이 틀리면 null)
     */
    static String extractBearerToken(HttpServletRequest request) {
        String authHeader = request.getHeader(HEADER_AUTHORIZATION);
        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith(BEARER_PREFIX)) {
            return null;
        }
        return authHeader.substring(BEARER_PREFIX.length());
    }
}
