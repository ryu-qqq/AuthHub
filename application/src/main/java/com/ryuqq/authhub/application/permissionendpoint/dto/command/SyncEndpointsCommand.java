package com.ryuqq.authhub.application.permissionendpoint.dto.command;

import java.util.List;

/**
 * SyncEndpointsCommand - 엔드포인트 동기화 Command DTO
 *
 * <p>다른 서비스에서 엔드포인트를 동기화할 때 사용합니다.
 *
 * @param serviceName 서비스 이름 (예: "authhub", "marketplace")
 * @param endpoints 엔드포인트 정보 목록
 * @author development-team
 * @since 1.0.0
 */
public record SyncEndpointsCommand(String serviceName, List<EndpointSyncItem> endpoints) {

    /**
     * EndpointSyncItem - 개별 엔드포인트 동기화 정보
     *
     * @param httpMethod HTTP 메서드
     * @param pathPattern URL 패턴
     * @param permissionKey 권한 키 (예: "product:create")
     * @param description 설명
     */
    public record EndpointSyncItem(
            String httpMethod, String pathPattern, String permissionKey, String description) {}
}
