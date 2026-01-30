package com.ryuqq.authhub.application.permissionendpoint.port.in.command;

import com.ryuqq.authhub.application.permissionendpoint.dto.command.UpdatePermissionEndpointCommand;

/**
 * UpdatePermissionEndpointUseCase - PermissionEndpoint 수정 UseCase
 *
 * <p>PermissionEndpoint 수정 비즈니스 로직을 정의하는 포트입니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public interface UpdatePermissionEndpointUseCase {

    /**
     * PermissionEndpoint 수정
     *
     * @param command 수정 Command
     */
    void update(UpdatePermissionEndpointCommand command);
}
