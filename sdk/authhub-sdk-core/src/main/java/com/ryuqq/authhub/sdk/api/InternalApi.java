package com.ryuqq.authhub.sdk.api;

import com.ryuqq.authhub.sdk.model.common.ApiResponse;
import com.ryuqq.authhub.sdk.model.internal.EndpointPermissionSpecList;

/**
 * Internal API 인터페이스.
 *
 * <p>Gateway 및 내부 서비스 간 통신을 위한 API를 제공합니다. 서비스 토큰 인증을 사용합니다.
 */
public interface InternalApi {

    /**
     * 엔드포인트-권한 스펙 전체 조회.
     *
     * <p>Gateway가 시작 시 또는 갱신 시 호출하여 전체 스펙을 캐싱합니다.
     *
     * @return 엔드포인트-권한 스펙 목록
     */
    ApiResponse<EndpointPermissionSpecList> getPermissionSpec();
}
