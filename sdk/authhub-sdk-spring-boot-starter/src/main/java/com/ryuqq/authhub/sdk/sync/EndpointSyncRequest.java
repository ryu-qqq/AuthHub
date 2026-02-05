package com.ryuqq.authhub.sdk.sync;

import java.util.List;

/**
 * EndpointSyncRequest - 엔드포인트 동기화 요청 DTO
 *
 * <p>AuthHub에 엔드포인트 동기화를 요청할 때 사용하는 DTO입니다.
 *
 * @param serviceName 서비스 이름 (예: "authhub", "marketplace")
 * @param serviceCode 서비스 코드 (Role-Permission 자동 매핑용, nullable)
 * @param endpoints 엔드포인트 정보 목록
 * @author development-team
 * @since 1.0.0
 */
public record EndpointSyncRequest(
        String serviceName, String serviceCode, List<EndpointInfo> endpoints) {

    /**
     * EndpointSyncRequest 생성 (serviceCode 포함)
     *
     * @param serviceName 서비스 이름
     * @param serviceCode 서비스 코드 (nullable)
     * @param endpoints 엔드포인트 목록
     * @return EndpointSyncRequest 인스턴스
     */
    public static EndpointSyncRequest of(
            String serviceName, String serviceCode, List<EndpointInfo> endpoints) {
        return new EndpointSyncRequest(serviceName, serviceCode, endpoints);
    }

    /**
     * EndpointSyncRequest 생성 (하위 호환 - serviceCode 없이)
     *
     * @param serviceName 서비스 이름
     * @param endpoints 엔드포인트 목록
     * @return EndpointSyncRequest 인스턴스
     */
    public static EndpointSyncRequest of(String serviceName, List<EndpointInfo> endpoints) {
        return new EndpointSyncRequest(serviceName, null, endpoints);
    }
}
