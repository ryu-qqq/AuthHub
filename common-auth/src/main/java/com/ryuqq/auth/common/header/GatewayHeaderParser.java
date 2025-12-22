package com.ryuqq.auth.common.header;

import com.ryuqq.auth.common.context.UserContext;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * GatewayHeaderParser - Gateway 헤더 파싱 유틸리티
 *
 * <p>Gateway에서 전달된 HTTP 헤더를 UserContext로 변환합니다.
 *
 * <p><strong>지원 헤더:</strong>
 *
 * <ul>
 *   <li>X-User-Id: 사용자 ID
 *   <li>X-Tenant-Id: 테넌트 ID
 *   <li>X-Organization-Id: 조직 ID
 *   <li>X-User-Email: 사용자 이메일
 *   <li>X-User-Roles: 역할 목록 (쉼표 구분)
 *   <li>X-User-Permissions: 권한 목록 (쉼표 구분)
 *   <li>X-Correlation-Id: 분산 추적 ID
 *   <li>X-Request-Source: 요청 출처
 * </ul>
 *
 * <p><strong>사용 예시:</strong>
 *
 * <pre>{@code
 * // HttpServletRequest에서 파싱
 * UserContext context = GatewayHeaderParser.parse(request::getHeader);
 *
 * // Spring WebFlux ServerRequest에서 파싱
 * UserContext context = GatewayHeaderParser.parse(
 *     name -> request.headers().firstHeader(name)
 * );
 * }</pre>
 *
 * @author development-team
 * @since 1.0.0
 */
public final class GatewayHeaderParser {

    private static final String DELIMITER = ",";
    private static final String ROLE_PREFIX = "ROLE_";

    private GatewayHeaderParser() {
        throw new AssertionError("Utility class - cannot instantiate");
    }

    /**
     * 헤더 함수를 사용하여 UserContext 생성
     *
     * @param headerGetter 헤더 이름으로 값을 반환하는 함수
     * @return 파싱된 UserContext
     */
    public static UserContext parse(Function<String, String> headerGetter) {
        if (headerGetter == null) {
            return UserContext.builder().build();
        }

        String userId = headerGetter.apply(SecurityHeaders.USER_ID);
        String tenantId = headerGetter.apply(SecurityHeaders.TENANT_ID);
        String organizationId = headerGetter.apply(SecurityHeaders.ORGANIZATION_ID);
        String email = headerGetter.apply(SecurityHeaders.USER_EMAIL);
        String rolesHeader = headerGetter.apply(SecurityHeaders.ROLES);
        String permissionsHeader = headerGetter.apply(SecurityHeaders.PERMISSIONS);
        String correlationId = headerGetter.apply(SecurityHeaders.CORRELATION_ID);
        String requestSource = headerGetter.apply(SecurityHeaders.REQUEST_SOURCE);
        String serviceToken = headerGetter.apply(SecurityHeaders.SERVICE_TOKEN);

        Set<String> roles = parseCommaSeparated(rolesHeader);
        Set<String> permissions = parseCommaSeparated(permissionsHeader);

        // 역할에 ROLE_ 접두사 정규화
        Set<String> normalizedRoles = normalizeRoles(roles);

        // 서비스 토큰이 있으면 서비스 계정
        boolean isServiceAccount = isNotBlank(serviceToken);

        return UserContext.builder()
                .userId(nullIfBlank(userId))
                .tenantId(nullIfBlank(tenantId))
                .organizationId(nullIfBlank(organizationId))
                .email(nullIfBlank(email))
                .roles(normalizedRoles)
                .permissions(permissions)
                .correlationId(nullIfBlank(correlationId))
                .requestSource(nullIfBlank(requestSource))
                .serviceAccount(isServiceAccount)
                .build();
    }

    /**
     * 헤더 함수와 Scope를 사용하여 UserContext 생성
     *
     * @param headerGetter 헤더 이름으로 값을 반환하는 함수
     * @param scope 사용자 범위 (GLOBAL, TENANT, ORGANIZATION)
     * @return 파싱된 UserContext
     */
    public static UserContext parseWithScope(Function<String, String> headerGetter, String scope) {
        UserContext base = parse(headerGetter);
        return UserContext.builder()
                .userId(base.getUserId())
                .tenantId(base.getTenantId())
                .organizationId(base.getOrganizationId())
                .email(base.getEmail())
                .roles(base.getRoles())
                .permissions(base.getPermissions())
                .scope(scope)
                .serviceAccount(base.isServiceAccount())
                .correlationId(base.getCorrelationId())
                .requestSource(base.getRequestSource())
                .build();
    }

    /**
     * 쉼표로 구분된 문자열을 Set으로 파싱
     *
     * @param value 쉼표 구분 문자열
     * @return 파싱된 Set (빈 문자열이면 빈 Set)
     */
    public static Set<String> parseCommaSeparated(String value) {
        if (value == null || value.isBlank()) {
            return Collections.emptySet();
        }
        return Arrays.stream(value.split(DELIMITER))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());
    }

    /**
     * 역할 Set을 ROLE_ 접두사 형식으로 정규화
     *
     * <p>"ADMIN" → "ROLE_ADMIN", "ROLE_USER" → "ROLE_USER"
     *
     * @param roles 역할 Set
     * @return 정규화된 역할 Set
     */
    public static Set<String> normalizeRoles(Set<String> roles) {
        if (roles == null || roles.isEmpty()) {
            return Collections.emptySet();
        }
        return roles.stream().map(GatewayHeaderParser::normalizeRole).collect(Collectors.toSet());
    }

    /**
     * 단일 역할을 ROLE_ 접두사 형식으로 정규화
     *
     * @param role 역할
     * @return 정규화된 역할
     */
    public static String normalizeRole(String role) {
        if (role == null) {
            return null;
        }
        String trimmed = role.trim();
        if (trimmed.startsWith(ROLE_PREFIX)) {
            return trimmed;
        }
        return ROLE_PREFIX + trimmed;
    }

    /**
     * ROLE_ 접두사 제거
     *
     * @param role 역할
     * @return 접두사 없는 역할
     */
    public static String stripRolePrefix(String role) {
        if (role == null) {
            return null;
        }
        String trimmed = role.trim();
        if (trimmed.startsWith(ROLE_PREFIX)) {
            return trimmed.substring(ROLE_PREFIX.length());
        }
        return trimmed;
    }

    private static String nullIfBlank(String value) {
        return (value == null || value.isBlank()) ? null : value.trim();
    }

    private static boolean isNotBlank(String value) {
        return value != null && !value.isBlank();
    }
}
