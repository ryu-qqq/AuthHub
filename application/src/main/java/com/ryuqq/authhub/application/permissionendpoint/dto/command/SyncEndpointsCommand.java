package com.ryuqq.authhub.application.permissionendpoint.dto.command;

import java.util.List;

/**
 * SyncEndpointsCommand - 엔드포인트 동기화 Command DTO
 *
 * <p>다른 서비스에서 엔드포인트를 동기화할 때 사용합니다.
 *
 * @param serviceName 서비스 이름 (예: "authhub", "marketplace")
 * @param serviceCode 서비스 코드 (Role-Permission 자동 매핑용, nullable)
 * @param endpoints 엔드포인트 정보 목록
 * @author development-team
 * @since 1.0.0
 */
public record SyncEndpointsCommand(
        String serviceName, String serviceCode, List<EndpointSyncItem> endpoints) {

    /** serviceCode 없는 하위 호환 생성자 */
    public SyncEndpointsCommand(String serviceName, List<EndpointSyncItem> endpoints) {
        this(serviceName, null, endpoints);
    }

    /**
     * EndpointSyncItem - 개별 엔드포인트 동기화 정보
     *
     * @param httpMethod HTTP 메서드
     * @param pathPattern URL 패턴
     * @param permissionKey 권한 키 (예: "product:create")
     * @param description 설명
     * @param isPublic 공개 엔드포인트 여부 (인증 없이 접근 가능)
     */
    public record EndpointSyncItem(
            String httpMethod,
            String pathPattern,
            String permissionKey,
            String description,
            boolean isPublic) {

        /** 기본 isPublic=false 편의 생성자 */
        public EndpointSyncItem(
                String httpMethod, String pathPattern, String permissionKey, String description) {
            this(httpMethod, pathPattern, permissionKey, description, false);
        }
    }
}
