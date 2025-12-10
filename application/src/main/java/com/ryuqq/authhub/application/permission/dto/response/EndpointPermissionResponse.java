package com.ryuqq.authhub.application.permission.dto.response;

import java.util.List;

/**
 * EndpointPermissionResponse - 엔드포인트별 권한 응답 DTO
 *
 * <p>개별 엔드포인트의 권한 정보를 표현합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public record EndpointPermissionResponse(
        String serviceName,
        String path,
        String method,
        List<String> requiredPermissions,
        List<String> requiredRoles,
        boolean isPublic) {

    public static EndpointPermissionResponse of(
            String serviceName,
            String path,
            String method,
            List<String> requiredPermissions,
            List<String> requiredRoles,
            boolean isPublic) {
        return new EndpointPermissionResponse(
                serviceName, path, method, requiredPermissions, requiredRoles, isPublic);
    }
}
