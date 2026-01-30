package com.ryuqq.authhub.application.permissionendpoint.factory;

import com.ryuqq.authhub.application.common.dto.command.StatusChangeContext;
import com.ryuqq.authhub.application.common.dto.command.UpdateContext;
import com.ryuqq.authhub.application.common.time.TimeProvider;
import com.ryuqq.authhub.application.permissionendpoint.dto.command.CreatePermissionEndpointCommand;
import com.ryuqq.authhub.application.permissionendpoint.dto.command.DeletePermissionEndpointCommand;
import com.ryuqq.authhub.application.permissionendpoint.dto.command.UpdatePermissionEndpointCommand;
import com.ryuqq.authhub.domain.permission.id.PermissionId;
import com.ryuqq.authhub.domain.permissionendpoint.aggregate.PermissionEndpoint;
import com.ryuqq.authhub.domain.permissionendpoint.id.PermissionEndpointId;
import com.ryuqq.authhub.domain.permissionendpoint.vo.HttpMethod;
import com.ryuqq.authhub.domain.permissionendpoint.vo.PermissionEndpointUpdateData;
import org.springframework.stereotype.Component;

/**
 * PermissionEndpointCommandFactory - PermissionEndpoint Command → Domain 변환 Factory
 *
 * <p>Command DTO를 Domain 객체로 변환합니다.
 *
 * <p>C-006: 시간/ID 생성은 Factory에서만 허용됩니다.
 *
 * <p>SVC-003: Service에서 Domain 객체 직접 생성 금지 → Factory에 위임.
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class PermissionEndpointCommandFactory {

    private final TimeProvider timeProvider;

    public PermissionEndpointCommandFactory(TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    // ==================== Domain 객체 생성 ====================

    /**
     * CreatePermissionEndpointCommand로부터 PermissionEndpoint 도메인 객체 생성
     *
     * @param command 생성 Command
     * @return PermissionEndpoint 도메인 객체
     */
    public PermissionEndpoint create(CreatePermissionEndpointCommand command) {
        return PermissionEndpoint.create(
                PermissionId.of(command.permissionId()),
                command.urlPattern(),
                HttpMethod.from(command.httpMethod()),
                command.description(),
                timeProvider.now());
    }

    // ==================== Update Context 생성 ====================

    /**
     * UpdatePermissionEndpointCommand로부터 UpdateContext 생성
     *
     * @param command 수정 Command
     * @return UpdateContext
     */
    public UpdateContext<PermissionEndpointId, PermissionEndpointUpdateData> createUpdateContext(
            UpdatePermissionEndpointCommand command) {
        PermissionEndpointId id = PermissionEndpointId.of(command.permissionEndpointId());
        PermissionEndpointUpdateData updateData =
                PermissionEndpointUpdateData.of(
                        command.urlPattern(), command.httpMethod(), command.description());
        return new UpdateContext<>(id, updateData, timeProvider.now());
    }

    // ==================== Status Change Context 생성 ====================

    /**
     * DeletePermissionEndpointCommand로부터 StatusChangeContext 생성
     *
     * @param command 삭제 Command
     * @return StatusChangeContext
     */
    public StatusChangeContext<PermissionEndpointId> createDeleteContext(
            DeletePermissionEndpointCommand command) {
        PermissionEndpointId id = PermissionEndpointId.of(command.permissionEndpointId());
        return new StatusChangeContext<>(id, timeProvider.now());
    }
}
