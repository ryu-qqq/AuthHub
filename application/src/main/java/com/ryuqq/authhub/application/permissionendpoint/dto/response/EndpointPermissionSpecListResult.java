package com.ryuqq.authhub.application.permissionendpoint.dto.response;

import java.time.Instant;
import java.util.List;

/**
 * EndpointPermissionSpecListResult - Gateway용 엔드포인트-권한 매핑 스펙 목록 DTO
 *
 * <p>Gateway가 전체 엔드포인트-권한 매핑 정보를 캐싱하기 위해 사용합니다.
 *
 * <p>RDTO-001: Response DTO는 Record로 정의.
 *
 * @param version 스펙 버전 (ETag용)
 * @param updatedAt 마지막 수정 시간 (ISO 8601)
 * @param endpoints 엔드포인트-권한 매핑 목록
 * @author development-team
 * @since 1.0.0
 */
public record EndpointPermissionSpecListResult(
        String version, Instant updatedAt, List<EndpointPermissionSpecResult> endpoints) {

    /**
     * 빈 결과 생성
     *
     * @return 빈 EndpointPermissionSpecListResult
     */
    public static EndpointPermissionSpecListResult empty() {
        return new EndpointPermissionSpecListResult("0", Instant.now(), List.of());
    }

    /**
     * 목록으로부터 결과 생성
     *
     * @param endpoints 엔드포인트 목록
     * @param latestUpdatedAt 가장 최근 수정 시간
     * @return EndpointPermissionSpecListResult
     */
    public static EndpointPermissionSpecListResult of(
            List<EndpointPermissionSpecResult> endpoints, Instant latestUpdatedAt) {
        String version =
                String.valueOf(latestUpdatedAt != null ? latestUpdatedAt.toEpochMilli() : 0);
        return new EndpointPermissionSpecListResult(version, latestUpdatedAt, endpoints);
    }
}
