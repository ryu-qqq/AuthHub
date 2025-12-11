package com.ryuqq.authhub.application.endpointpermission.dto.query;

/**
 * GetServiceEndpointPermissionSpecQuery - 서비스별 엔드포인트 권한 스펙 조회 Query DTO
 *
 * <p>Gateway에서 서비스별 엔드포인트 권한 스펙 목록을 조회할 때 사용합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>Record 필수
 *   <li>불변 객체
 *   <li>Lombok 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public record GetServiceEndpointPermissionSpecQuery(String serviceName) {

    public GetServiceEndpointPermissionSpecQuery {
        if (serviceName == null || serviceName.isBlank()) {
            throw new IllegalArgumentException("serviceName must not be null or blank");
        }
    }
}
