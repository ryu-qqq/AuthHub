package com.ryuqq.authhub.application.permissionendpoint.port.in.command;

import com.ryuqq.authhub.application.permissionendpoint.dto.command.DeletePermissionEndpointCommand;

/**
 * DeletePermissionEndpointUseCase - PermissionEndpoint 삭제 UseCase
 *
 * <p>PermissionEndpoint 삭제 비즈니스 로직을 정의하는 포트입니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public interface DeletePermissionEndpointUseCase {

    /**
     * PermissionEndpoint 삭제 (소프트 삭제)
     *
     * @param command 삭제 Command
     */
    void delete(DeletePermissionEndpointCommand command);
}
