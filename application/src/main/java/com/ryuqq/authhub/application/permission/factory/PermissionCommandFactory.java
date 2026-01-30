package com.ryuqq.authhub.application.permission.factory;

import com.ryuqq.authhub.application.common.dto.command.StatusChangeContext;
import com.ryuqq.authhub.application.common.dto.command.UpdateContext;
import com.ryuqq.authhub.application.common.time.TimeProvider;
import com.ryuqq.authhub.application.permission.dto.command.CreatePermissionCommand;
import com.ryuqq.authhub.application.permission.dto.command.DeletePermissionCommand;
import com.ryuqq.authhub.application.permission.dto.command.UpdatePermissionCommand;
import com.ryuqq.authhub.domain.permission.aggregate.Permission;
import com.ryuqq.authhub.domain.permission.id.PermissionId;
import com.ryuqq.authhub.domain.permission.vo.PermissionUpdateData;
import org.springframework.stereotype.Component;

/**
 * PermissionCommandFactory - Permission Command → Domain 변환 Factory (Global Only)
 *
 * <p>Command DTO를 Domain 객체로 변환합니다.
 *
 * <p><strong>Global Only 설계:</strong>
 *
 * <ul>
 *   <li>모든 Permission은 전체 시스템에서 공유됩니다
 *   <li>테넌트 관련 변환 로직이 제거되었습니다
 * </ul>
 *
 * <p>C-006: 시간/ID 생성은 Factory에서만 허용됩니다.
 *
 * <p>SVC-003: Service에서 Domain 객체 직접 생성 금지 → Factory에 위임.
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class PermissionCommandFactory {

    private final TimeProvider timeProvider;

    public PermissionCommandFactory(TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    // ==================== Domain 객체 생성 ====================

    /**
     * CreatePermissionCommand로부터 Permission 도메인 객체 생성
     *
     * <p>isSystem 판단은 Permission 도메인이 수행합니다.
     *
     * @param command 생성 Command
     * @return Permission 도메인 객체
     */
    public Permission create(CreatePermissionCommand command) {
        return Permission.create(
                command.resource(),
                command.action(),
                command.description(),
                command.isSystem(),
                timeProvider.now());
    }

    // ==================== Update Context 생성 ====================

    /**
     * UpdatePermissionCommand로부터 UpdateContext 생성
     *
     * <p>업데이트에 필요한 ID, UpdateData, 변경 시간을 한 번에 생성합니다.
     *
     * @param command 수정 Command
     * @return UpdateContext (id, updateData, changedAt)
     */
    public UpdateContext<PermissionId, PermissionUpdateData> createUpdateContext(
            UpdatePermissionCommand command) {
        PermissionId id = PermissionId.of(command.permissionId());
        PermissionUpdateData updateData = PermissionUpdateData.of(command.description());
        return new UpdateContext<>(id, updateData, timeProvider.now());
    }

    // ==================== Status Change Context 생성 ====================

    /**
     * DeletePermissionCommand로부터 StatusChangeContext 생성
     *
     * <p>삭제에 필요한 ID와 변경 시간을 한 번에 생성합니다.
     *
     * @param command 삭제 Command
     * @return StatusChangeContext (id, changedAt)
     */
    public StatusChangeContext<PermissionId> createDeleteContext(DeletePermissionCommand command) {
        PermissionId id = PermissionId.of(command.permissionId());
        return new StatusChangeContext<>(id, timeProvider.now());
    }
}
