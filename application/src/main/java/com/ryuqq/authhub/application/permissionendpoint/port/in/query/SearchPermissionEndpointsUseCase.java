package com.ryuqq.authhub.application.permissionendpoint.port.in.query;

import com.ryuqq.authhub.application.permissionendpoint.dto.query.PermissionEndpointSearchParams;
import com.ryuqq.authhub.application.permissionendpoint.dto.response.PermissionEndpointPageResult;

/**
 * SearchPermissionEndpointsUseCase - PermissionEndpoint 검색 UseCase
 *
 * <p>PermissionEndpoint 목록 조회 비즈니스 로직을 정의하는 포트입니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public interface SearchPermissionEndpointsUseCase {

    /**
     * PermissionEndpoint 목록 검색
     *
     * @param params 검색 파라미터
     * @return 페이지 결과
     */
    PermissionEndpointPageResult search(PermissionEndpointSearchParams params);
}
