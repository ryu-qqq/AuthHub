package com.ryuqq.authhub.application.permissionendpoint.dto.response;

/**
 * SyncEndpointsResult - 엔드포인트 동기화 결과 DTO
 *
 * <p>RDTO-001: Response DTO는 Record로 정의.
 *
 * <p>RDTO-002: Record는 불변이므로 Builder 패턴 대신 static factory method 사용.
 *
 * @param serviceName 서비스 이름
 * @param totalEndpoints 전체 엔드포인트 수
 * @param createdPermissions 생성된 권한 수
 * @param createdEndpoints 생성된 엔드포인트 수
 * @param skippedEndpoints 스킵된 엔드포인트 수 (이미 존재)
 * @author development-team
 * @since 1.0.0
 */
public record SyncEndpointsResult(
        String serviceName,
        int totalEndpoints,
        int createdPermissions,
        int createdEndpoints,
        int skippedEndpoints) {

    /**
     * 동기화 결과 생성
     *
     * @param serviceName 서비스 이름
     * @param totalEndpoints 전체 엔드포인트 수
     * @param createdPermissions 생성된 권한 수
     * @param createdEndpoints 생성된 엔드포인트 수
     * @param skippedEndpoints 스킵된 엔드포인트 수
     * @return SyncEndpointsResult
     */
    public static SyncEndpointsResult of(
            String serviceName,
            int totalEndpoints,
            int createdPermissions,
            int createdEndpoints,
            int skippedEndpoints) {
        return new SyncEndpointsResult(
                serviceName,
                totalEndpoints,
                createdPermissions,
                createdEndpoints,
                skippedEndpoints);
    }
}
