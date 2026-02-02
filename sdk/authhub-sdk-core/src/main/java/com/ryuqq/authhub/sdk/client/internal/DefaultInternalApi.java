package com.ryuqq.authhub.sdk.client.internal;

import com.fasterxml.jackson.core.type.TypeReference;
import com.ryuqq.authhub.sdk.api.InternalApi;
import com.ryuqq.authhub.sdk.model.common.ApiResponse;
import com.ryuqq.authhub.sdk.model.internal.EndpointPermissionSpecList;
import com.ryuqq.authhub.sdk.model.internal.PublicKeys;
import com.ryuqq.authhub.sdk.model.internal.TenantConfig;
import com.ryuqq.authhub.sdk.model.internal.UserPermissions;

/**
 * InternalApi의 기본 구현체.
 *
 * <p>서비스 토큰 인증을 사용하여 Internal API를 호출합니다.
 */
final class DefaultInternalApi implements InternalApi {

    private static final String PERMISSION_SPEC_PATH = "/api/v1/internal/endpoint-permissions/spec";
    private static final String JWKS_PATH = "/api/v1/auth/jwks";
    private static final String TENANT_CONFIG_PATH = "/api/v1/internal/tenants/%s/config";
    private static final String USER_PERMISSIONS_PATH = "/api/v1/internal/users/%s/permissions";

    private final ServiceTokenHttpClientSupport httpClient;

    DefaultInternalApi(ServiceTokenHttpClientSupport httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public ApiResponse<EndpointPermissionSpecList> getPermissionSpec() {
        return httpClient.get(
                PERMISSION_SPEC_PATH,
                new TypeReference<ApiResponse<EndpointPermissionSpecList>>() {});
    }

    @Override
    public PublicKeys getJwks() {
        return httpClient.get(JWKS_PATH, new TypeReference<PublicKeys>() {});
    }

    @Override
    public ApiResponse<TenantConfig> getTenantConfig(String tenantId) {
        return httpClient.get(
                String.format(TENANT_CONFIG_PATH, tenantId),
                new TypeReference<ApiResponse<TenantConfig>>() {});
    }

    @Override
    public ApiResponse<UserPermissions> getUserPermissions(String userId) {
        return httpClient.get(
                String.format(USER_PERMISSIONS_PATH, userId),
                new TypeReference<ApiResponse<UserPermissions>>() {});
    }
}
