package com.ryuqq.authhub.adapter.in.rest.permissionendpoint.mapper;

import com.ryuqq.authhub.adapter.in.rest.permissionendpoint.dto.request.CreatePermissionEndpointApiRequest;
import com.ryuqq.authhub.adapter.in.rest.permissionendpoint.dto.request.UpdatePermissionEndpointApiRequest;
import com.ryuqq.authhub.application.permissionendpoint.dto.command.CreatePermissionEndpointCommand;
import com.ryuqq.authhub.application.permissionendpoint.dto.command.DeletePermissionEndpointCommand;
import com.ryuqq.authhub.application.permissionendpoint.dto.command.UpdatePermissionEndpointCommand;
import org.springframework.stereotype.Component;

/**
 * PermissionEndpointCommandApiMapper - PermissionEndpoint Command API 변환 매퍼
 *
 * <p>API Request와 Application Command 간 변환을 담당합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class PermissionEndpointCommandApiMapper {

    /**
     * CreatePermissionEndpointApiRequest -> CreatePermissionEndpointCommand 변환
     *
     * @param request API 요청 DTO
     * @return Application Command DTO
     */
    public CreatePermissionEndpointCommand toCommand(CreatePermissionEndpointApiRequest request) {
        return new CreatePermissionEndpointCommand(
                request.permissionId(),
                request.serviceName(),
                request.urlPattern(),
                request.httpMethod(),
                request.description(),
                request.isPublic());
    }

    /**
     * UpdatePermissionEndpointApiRequest + PathVariable ID -> UpdatePermissionEndpointCommand 변환
     *
     * @param permissionEndpointId PermissionEndpoint ID (PathVariable)
     * @param request API 요청 DTO
     * @return Application Command DTO
     */
    public UpdatePermissionEndpointCommand toCommand(
            Long permissionEndpointId, UpdatePermissionEndpointApiRequest request) {
        return new UpdatePermissionEndpointCommand(
                permissionEndpointId,
                request.serviceName(),
                request.urlPattern(),
                request.httpMethod(),
                request.description(),
                request.isPublic());
    }

    /**
     * PathVariable ID -> DeletePermissionEndpointCommand 변환
     *
     * @param permissionEndpointId PermissionEndpoint ID (PathVariable)
     * @return Application Command DTO
     */
    public DeletePermissionEndpointCommand toDeleteCommand(Long permissionEndpointId) {
        return new DeletePermissionEndpointCommand(permissionEndpointId);
    }
}
