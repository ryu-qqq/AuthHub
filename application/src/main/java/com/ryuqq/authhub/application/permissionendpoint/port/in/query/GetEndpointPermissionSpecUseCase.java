package com.ryuqq.authhub.application.permissionendpoint.port.in.query;

import com.ryuqq.authhub.application.permissionendpoint.dto.response.EndpointPermissionSpecListResult;

/**
 * GetEndpointPermissionSpecUseCase - Gateway용 엔드포인트-권한 스펙 조회 UseCase
 *
 * <p>Gateway가 URL 기반 권한 검사를 위해 전체 엔드포인트-권한 매핑 정보를 조회합니다.
 *
 * <p><strong>사용 시나리오:</strong>
 *
 * <ul>
 *   <li>Gateway 시작 시 전체 스펙 캐싱
 *   <li>스펙 변경 시 Gateway 갱신
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface GetEndpointPermissionSpecUseCase {

    /**
     * 모든 활성 엔드포인트-권한 스펙 조회
     *
     * <p>삭제되지 않은 모든 PermissionEndpoint와 연결된 Permission 정보를 반환합니다.
     *
     * @return 엔드포인트-권한 스펙 목록
     */
    EndpointPermissionSpecListResult getAll();
}
