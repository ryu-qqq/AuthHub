package com.ryuqq.authhub.application.permissionendpoint.dto.response;

import java.util.List;

/**
 * EndpointPermissionSpecListResult - Gateway용 엔드포인트-권한 매핑 스펙 목록 DTO
 *
 * <p>Gateway가 전체 엔드포인트-권한 매핑 정보를 캐싱하기 위해 사용합니다.
 *
 * <p>RDTO-001: Response DTO는 Record로 정의.
 *
 * @param endpoints 엔드포인트-권한 매핑 목록
 * @param totalCount 전체 개수
 * @author development-team
 * @since 1.0.0
 */
public record EndpointPermissionSpecListResult(
        List<EndpointPermissionSpecResult> endpoints, int totalCount) {

    /**
     * 빈 결과 생성
     *
     * @return 빈 EndpointPermissionSpecListResult
     */
    public static EndpointPermissionSpecListResult empty() {
        return new EndpointPermissionSpecListResult(List.of(), 0);
    }

    /**
     * 목록으로부터 결과 생성
     *
     * @param endpoints 엔드포인트 목록
     * @return EndpointPermissionSpecListResult
     */
    public static EndpointPermissionSpecListResult of(
            List<EndpointPermissionSpecResult> endpoints) {
        return new EndpointPermissionSpecListResult(endpoints, endpoints.size());
    }
}
