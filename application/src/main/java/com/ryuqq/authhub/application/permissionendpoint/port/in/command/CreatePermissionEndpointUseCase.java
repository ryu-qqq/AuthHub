package com.ryuqq.authhub.application.permissionendpoint.port.in.command;

import com.ryuqq.authhub.application.permissionendpoint.dto.command.CreatePermissionEndpointCommand;

/**
 * CreatePermissionEndpointUseCase - PermissionEndpoint 생성 UseCase
 *
 * <p>PermissionEndpoint 생성 비즈니스 로직을 정의하는 포트입니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public interface CreatePermissionEndpointUseCase {

    /**
     * PermissionEndpoint 생성
     *
     * @param command 생성 Command
     * @return 생성된 PermissionEndpoint ID
     */
    Long create(CreatePermissionEndpointCommand command);
}
