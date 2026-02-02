package com.ryuqq.authhub.sdk.client.internal;

import com.fasterxml.jackson.core.type.TypeReference;
import com.ryuqq.authhub.sdk.api.InternalApi;
import com.ryuqq.authhub.sdk.model.common.ApiResponse;
import com.ryuqq.authhub.sdk.model.internal.EndpointPermissionSpecList;

/**
 * InternalApi의 기본 구현체.
 *
 * <p>서비스 토큰 인증을 사용하여 Internal API를 호출합니다.
 */
final class DefaultInternalApi implements InternalApi {

    private static final String PERMISSION_SPEC_PATH = "/api/v1/internal/endpoint-permissions/spec";

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
}
