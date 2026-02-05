package com.ryuqq.authhub.application.role.factory;

import com.ryuqq.authhub.application.common.dto.command.StatusChangeContext;
import com.ryuqq.authhub.application.common.dto.command.UpdateContext;
import com.ryuqq.authhub.application.common.time.TimeProvider;
import com.ryuqq.authhub.application.role.dto.command.CreateRoleCommand;
import com.ryuqq.authhub.application.role.dto.command.DeleteRoleCommand;
import com.ryuqq.authhub.application.role.dto.command.UpdateRoleCommand;
import com.ryuqq.authhub.domain.role.aggregate.Role;
import com.ryuqq.authhub.domain.role.aggregate.RoleUpdateData;
import com.ryuqq.authhub.domain.role.id.RoleId;
import com.ryuqq.authhub.domain.role.vo.RoleName;
import com.ryuqq.authhub.domain.service.id.ServiceId;
import com.ryuqq.authhub.domain.tenant.id.TenantId;
import org.springframework.stereotype.Component;

/**
 * RoleCommandFactory - Role Command → Domain 변환 Factory
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
public class RoleCommandFactory {

    private final TimeProvider timeProvider;

    public RoleCommandFactory(TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    // ==================== Domain 객체 생성 ====================

    /**
     * CreateRoleCommand로부터 Role 도메인 객체 생성
     *
     * <p>tenantId와 isSystem 판단은 Role 도메인이 수행합니다.
     *
     * @param command 생성 Command
     * @return Role 도메인 객체
     */
    public Role create(CreateRoleCommand command) {
        return Role.create(
                TenantId.fromNullable(command.tenantId()),
                ServiceId.fromNullable(command.serviceId()),
                RoleName.of(command.name()),
                command.displayName(),
                command.description(),
                command.isSystem(),
                timeProvider.now());
    }

    // ==================== Update Context 생성 ====================

    /**
     * UpdateRoleCommand로부터 UpdateContext 생성
     *
     * <p>업데이트에 필요한 ID, UpdateData, 변경 시간을 한 번에 생성합니다.
     *
     * @param command 수정 Command
     * @return UpdateContext (id, updateData, changedAt)
     */
    public UpdateContext<RoleId, RoleUpdateData> createUpdateContext(UpdateRoleCommand command) {
        RoleId id = RoleId.of(command.roleId());
        RoleUpdateData updateData = RoleUpdateData.of(command.displayName(), command.description());
        return new UpdateContext<>(id, updateData, timeProvider.now());
    }

    // ==================== Status Change Context 생성 ====================

    /**
     * DeleteRoleCommand로부터 StatusChangeContext 생성
     *
     * <p>삭제에 필요한 ID와 변경 시간을 한 번에 생성합니다.
     *
     * @param command 삭제 Command
     * @return StatusChangeContext (id, changedAt)
     */
    public StatusChangeContext<RoleId> createDeleteContext(DeleteRoleCommand command) {
        RoleId id = RoleId.of(command.roleId());
        return new StatusChangeContext<>(id, timeProvider.now());
    }
}
