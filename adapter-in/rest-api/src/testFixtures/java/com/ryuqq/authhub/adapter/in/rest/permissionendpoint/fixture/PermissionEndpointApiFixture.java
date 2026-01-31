package com.ryuqq.authhub.adapter.in.rest.permissionendpoint.fixture;

import com.ryuqq.authhub.adapter.in.rest.permissionendpoint.dto.request.CreatePermissionEndpointApiRequest;
import com.ryuqq.authhub.adapter.in.rest.permissionendpoint.dto.request.UpdatePermissionEndpointApiRequest;
import com.ryuqq.authhub.adapter.in.rest.permissionendpoint.dto.response.PermissionEndpointApiResponse;
import java.time.Instant;

/**
 * PermissionEndpoint API 테스트 픽스처
 *
 * <p>PermissionEndpoint 관련 API 테스트에 사용되는 DTO 픽스처를 제공합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public final class PermissionEndpointApiFixture {

    private static final Long DEFAULT_PERMISSION_ENDPOINT_ID = 1L;
    private static final Long DEFAULT_PERMISSION_ID = 1L;
    private static final String DEFAULT_URL_PATTERN = "/api/v1/users/{id}";
    private static final String DEFAULT_HTTP_METHOD = "GET";
    private static final String DEFAULT_DESCRIPTION = "사용자 상세 조회 API";
    private static final String FIXED_TIME = "2025-01-01T00:00:00Z";

    private PermissionEndpointApiFixture() {}

    // ========== CreatePermissionEndpointApiRequest ==========

    /** 기본 생성 요청 */
    public static CreatePermissionEndpointApiRequest createPermissionEndpointRequest() {
        return new CreatePermissionEndpointApiRequest(
                DEFAULT_PERMISSION_ID,
                DEFAULT_URL_PATTERN,
                DEFAULT_HTTP_METHOD,
                DEFAULT_DESCRIPTION);
    }

    /** 커스텀 URL 패턴으로 생성 요청 */
    public static CreatePermissionEndpointApiRequest createPermissionEndpointRequest(
            String urlPattern, String httpMethod) {
        return new CreatePermissionEndpointApiRequest(
                DEFAULT_PERMISSION_ID, urlPattern, httpMethod, DEFAULT_DESCRIPTION);
    }

    /** 커스텀 권한 ID로 생성 요청 */
    public static CreatePermissionEndpointApiRequest createPermissionEndpointRequestWithPermission(
            Long permissionId) {
        return new CreatePermissionEndpointApiRequest(
                permissionId, DEFAULT_URL_PATTERN, DEFAULT_HTTP_METHOD, DEFAULT_DESCRIPTION);
    }

    // ========== UpdatePermissionEndpointApiRequest ==========

    /** 기본 수정 요청 */
    public static UpdatePermissionEndpointApiRequest updatePermissionEndpointRequest() {
        return new UpdatePermissionEndpointApiRequest(
                "/api/v1/users/{id}/profile", "GET", "사용자 프로필 조회 API");
    }

    /** URL 패턴만 수정 요청 */
    public static UpdatePermissionEndpointApiRequest updateUrlPatternRequest(String urlPattern) {
        return new UpdatePermissionEndpointApiRequest(urlPattern, null, null);
    }

    /** HTTP 메서드만 수정 요청 */
    public static UpdatePermissionEndpointApiRequest updateHttpMethodRequest(String httpMethod) {
        return new UpdatePermissionEndpointApiRequest(null, httpMethod, null);
    }

    // ========== PermissionEndpointApiResponse ==========

    /** 기본 응답 */
    public static PermissionEndpointApiResponse permissionEndpointResponse() {
        return new PermissionEndpointApiResponse(
                DEFAULT_PERMISSION_ENDPOINT_ID,
                DEFAULT_PERMISSION_ID,
                DEFAULT_URL_PATTERN,
                DEFAULT_HTTP_METHOD,
                DEFAULT_DESCRIPTION,
                FIXED_TIME,
                FIXED_TIME);
    }

    /** 커스텀 응답 */
    public static PermissionEndpointApiResponse permissionEndpointResponse(
            Long id, String urlPattern, String httpMethod) {
        return new PermissionEndpointApiResponse(
                id,
                DEFAULT_PERMISSION_ID,
                urlPattern,
                httpMethod,
                DEFAULT_DESCRIPTION,
                FIXED_TIME,
                FIXED_TIME);
    }

    // ========== Default Values ==========

    public static Long defaultPermissionEndpointId() {
        return DEFAULT_PERMISSION_ENDPOINT_ID;
    }

    public static Long defaultPermissionId() {
        return DEFAULT_PERMISSION_ID;
    }

    public static String defaultUrlPattern() {
        return DEFAULT_URL_PATTERN;
    }

    public static String defaultHttpMethod() {
        return DEFAULT_HTTP_METHOD;
    }

    public static String defaultDescription() {
        return DEFAULT_DESCRIPTION;
    }

    public static Instant fixedTime() {
        return Instant.parse(FIXED_TIME);
    }
}
