package com.ryuqq.authhub.adapter.in.rest.internal.fixture;

import com.ryuqq.authhub.adapter.in.rest.internal.dto.command.EndpointSyncApiRequest;
import com.ryuqq.authhub.adapter.in.rest.internal.dto.command.EndpointSyncApiRequest.EndpointInfoApiRequest;
import com.ryuqq.authhub.adapter.in.rest.internal.dto.command.OnboardingApiRequest;
import com.ryuqq.authhub.adapter.in.rest.internal.dto.response.EndpointPermissionSpecApiResponse;
import com.ryuqq.authhub.adapter.in.rest.internal.dto.response.EndpointPermissionSpecListApiResponse;
import com.ryuqq.authhub.adapter.in.rest.internal.dto.response.EndpointSyncResultApiResponse;
import com.ryuqq.authhub.adapter.in.rest.internal.dto.response.OnboardingResultApiResponse;
import com.ryuqq.authhub.adapter.in.rest.internal.dto.response.TenantConfigApiResponse;
import java.time.Instant;
import java.util.List;

/**
 * Internal API 테스트 픽스처
 *
 * <p>Internal API 테스트에 사용되는 DTO 픽스처를 제공합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public final class InternalApiFixture {

    private static final String DEFAULT_SERVICE_NAME = "marketplace";
    private static final String DEFAULT_HTTP_METHOD = "GET";
    private static final String DEFAULT_PATH_PATTERN = "/api/v1/products/{id}";
    private static final String DEFAULT_PERMISSION_KEY = "product:read";
    private static final String DEFAULT_DESCRIPTION = "상품 조회";
    private static final Long DEFAULT_PERMISSION_ENDPOINT_ID = 1L;
    private static final Long DEFAULT_PERMISSION_ID = 10L;
    private static final String DEFAULT_TENANT_ID = "550e8400-e29b-41d4-a716-446655440000";
    private static final String DEFAULT_TENANT_NAME = "테스트 테넌트";
    private static final String DEFAULT_STATUS = "ACTIVE";
    private static final String DEFAULT_ORG_NAME = "기본 조직";

    private InternalApiFixture() {}

    // ========== EndpointSyncApiRequest ==========

    /** 기본 동기화 요청 */
    public static EndpointSyncApiRequest endpointSyncRequest() {
        return new EndpointSyncApiRequest(DEFAULT_SERVICE_NAME, List.of(endpointInfoRequest()));
    }

    /** 커스텀 서비스 이름으로 동기화 요청 */
    public static EndpointSyncApiRequest endpointSyncRequest(String serviceName) {
        return new EndpointSyncApiRequest(serviceName, List.of(endpointInfoRequest()));
    }

    /** 빈 엔드포인트 목록으로 동기화 요청 */
    public static EndpointSyncApiRequest endpointSyncRequestWithEmptyEndpoints(String serviceName) {
        return new EndpointSyncApiRequest(serviceName, List.of());
    }

    /** 여러 엔드포인트로 동기화 요청 */
    public static EndpointSyncApiRequest endpointSyncRequestWithMultipleEndpoints() {
        return new EndpointSyncApiRequest(
                DEFAULT_SERVICE_NAME,
                List.of(
                        endpointInfoRequest("GET", "/api/v1/products", "product:read", "상품 목록 조회"),
                        endpointInfoRequest("POST", "/api/v1/products", "product:create", "상품 생성"),
                        endpointInfoRequest(
                                "PUT", "/api/v1/products/{id}", "product:update", "상품 수정"),
                        endpointInfoRequest(
                                "DELETE", "/api/v1/products/{id}", "product:delete", "상품 삭제")));
    }

    /** 기본 엔드포인트 정보 요청 */
    public static EndpointInfoApiRequest endpointInfoRequest() {
        return new EndpointInfoApiRequest(
                DEFAULT_HTTP_METHOD,
                DEFAULT_PATH_PATTERN,
                DEFAULT_PERMISSION_KEY,
                DEFAULT_DESCRIPTION);
    }

    /** 커스텀 엔드포인트 정보 요청 */
    public static EndpointInfoApiRequest endpointInfoRequest(
            String httpMethod, String pathPattern, String permissionKey, String description) {
        return new EndpointInfoApiRequest(httpMethod, pathPattern, permissionKey, description);
    }

    // ========== EndpointSyncResultApiResponse ==========

    /** 기본 동기화 결과 응답 */
    public static EndpointSyncResultApiResponse endpointSyncResultResponse() {
        return new EndpointSyncResultApiResponse(DEFAULT_SERVICE_NAME, 4, 2, 3, 1);
    }

    // ========== EndpointPermissionSpecApiResponse ==========

    /** 기본 스펙 응답 */
    public static EndpointPermissionSpecApiResponse endpointPermissionSpecResponse() {
        return new EndpointPermissionSpecApiResponse(
                DEFAULT_SERVICE_NAME,
                DEFAULT_PATH_PATTERN,
                DEFAULT_HTTP_METHOD,
                List.of(DEFAULT_PERMISSION_KEY),
                List.of(),
                false,
                DEFAULT_DESCRIPTION);
    }

    /** 커스텀 스펙 응답 */
    public static EndpointPermissionSpecApiResponse endpointPermissionSpecResponse(
            String serviceName,
            String pathPattern,
            String httpMethod,
            List<String> requiredPermissions,
            List<String> requiredRoles,
            boolean isPublic,
            String description) {
        return new EndpointPermissionSpecApiResponse(
                serviceName,
                pathPattern,
                httpMethod,
                requiredPermissions,
                requiredRoles,
                isPublic,
                description);
    }

    // ========== EndpointPermissionSpecListApiResponse ==========

    private static final Instant FIXED_INSTANT = Instant.parse("2025-01-01T00:00:00Z");
    private static final String FIXED_VERSION = String.valueOf(FIXED_INSTANT.toEpochMilli());

    /** 기본 스펙 목록 응답 */
    public static EndpointPermissionSpecListApiResponse endpointPermissionSpecListResponse() {
        return new EndpointPermissionSpecListApiResponse(
                FIXED_VERSION, FIXED_INSTANT, List.of(endpointPermissionSpecResponse()));
    }

    /** 빈 스펙 목록 응답 */
    public static EndpointPermissionSpecListApiResponse emptyEndpointPermissionSpecListResponse() {
        return new EndpointPermissionSpecListApiResponse("0", null, List.of());
    }

    // ========== OnboardingApiRequest ==========

    /** 기본 온보딩 요청 */
    public static OnboardingApiRequest onboardingRequest() {
        return new OnboardingApiRequest(DEFAULT_TENANT_NAME, DEFAULT_ORG_NAME);
    }

    /** 커스텀 온보딩 요청 */
    public static OnboardingApiRequest onboardingRequest(
            String tenantName, String organizationName) {
        return new OnboardingApiRequest(tenantName, organizationName);
    }

    // ========== OnboardingResultApiResponse ==========

    /** 기본 온보딩 결과 응답 */
    public static OnboardingResultApiResponse onboardingResultResponse() {
        return new OnboardingResultApiResponse(
                "01933abc-1234-7000-8000-000000000001", "01933abc-1234-7000-8000-000000000002");
    }

    /** 커스텀 온보딩 결과 응답 */
    public static OnboardingResultApiResponse onboardingResultResponse(
            String tenantId, String organizationId) {
        return new OnboardingResultApiResponse(tenantId, organizationId);
    }

    // ========== TenantConfigApiResponse ==========

    /** 기본 테넌트 설정 응답 */
    public static TenantConfigApiResponse tenantConfigResponse() {
        return new TenantConfigApiResponse(
                DEFAULT_TENANT_ID, DEFAULT_TENANT_NAME, DEFAULT_STATUS, true);
    }

    /** 비활성 테넌트 설정 응답 */
    public static TenantConfigApiResponse inactiveTenantConfigResponse() {
        return new TenantConfigApiResponse(
                DEFAULT_TENANT_ID, DEFAULT_TENANT_NAME, "INACTIVE", false);
    }

    /** 커스텀 테넌트 설정 응답 */
    public static TenantConfigApiResponse tenantConfigResponse(
            String tenantId, String name, String status, boolean active) {
        return new TenantConfigApiResponse(tenantId, name, status, active);
    }

    // ========== Default Values ==========

    public static String defaultServiceName() {
        return DEFAULT_SERVICE_NAME;
    }

    public static String defaultHttpMethod() {
        return DEFAULT_HTTP_METHOD;
    }

    public static String defaultPathPattern() {
        return DEFAULT_PATH_PATTERN;
    }

    public static String defaultPermissionKey() {
        return DEFAULT_PERMISSION_KEY;
    }

    public static String defaultDescription() {
        return DEFAULT_DESCRIPTION;
    }

    public static Long defaultPermissionEndpointId() {
        return DEFAULT_PERMISSION_ENDPOINT_ID;
    }

    public static Long defaultPermissionId() {
        return DEFAULT_PERMISSION_ID;
    }

    public static String defaultTenantId() {
        return DEFAULT_TENANT_ID;
    }

    public static String defaultTenantName() {
        return DEFAULT_TENANT_NAME;
    }

    public static String defaultStatus() {
        return DEFAULT_STATUS;
    }

    public static String defaultOrgName() {
        return DEFAULT_ORG_NAME;
    }
}
