package com.ryuqq.authhub.application.organization.factory;

import com.ryuqq.authhub.application.common.dto.command.StatusChangeContext;
import com.ryuqq.authhub.application.common.dto.command.UpdateContext;
import com.ryuqq.authhub.application.common.port.out.IdGeneratorPort;
import com.ryuqq.authhub.application.common.time.TimeProvider;
import com.ryuqq.authhub.application.organization.dto.command.CreateOrganizationCommand;
import com.ryuqq.authhub.application.organization.dto.command.UpdateOrganizationNameCommand;
import com.ryuqq.authhub.application.organization.dto.command.UpdateOrganizationStatusCommand;
import com.ryuqq.authhub.domain.organization.aggregate.Organization;
import com.ryuqq.authhub.domain.organization.id.OrganizationId;
import com.ryuqq.authhub.domain.organization.vo.OrganizationName;
import com.ryuqq.authhub.domain.tenant.id.TenantId;
import org.springframework.stereotype.Component;

/**
 * OrganizationCommandFactory - Organization Command → Domain 변환 Factory
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
public class OrganizationCommandFactory {

    private final TimeProvider timeProvider;
    private final IdGeneratorPort idGeneratorPort;

    public OrganizationCommandFactory(TimeProvider timeProvider, IdGeneratorPort idGeneratorPort) {
        this.timeProvider = timeProvider;
        this.idGeneratorPort = idGeneratorPort;
    }

    // ==================== Domain 객체 생성 ====================

    /**
     * CreateOrganizationCommand로부터 Organization 도메인 객체 생성
     *
     * @param command 생성 Command
     * @return Organization 도메인 객체
     */
    public Organization create(CreateOrganizationCommand command) {
        OrganizationId organizationId = OrganizationId.forNew(idGeneratorPort.generate());
        return Organization.create(
                organizationId,
                TenantId.of(command.tenantId()),
                OrganizationName.of(command.name()),
                timeProvider.now());
    }

    // ==================== Update Context 생성 ====================

    /**
     * UpdateOrganizationNameCommand로부터 UpdateContext 생성
     *
     * <p>업데이트에 필요한 ID, OrganizationName, 변경 시간을 한 번에 생성합니다.
     *
     * @param command 수정 Command
     * @return UpdateContext (id, updateData, changedAt)
     */
    public UpdateContext<OrganizationId, OrganizationName> createNameUpdateContext(
            UpdateOrganizationNameCommand command) {
        OrganizationId id = OrganizationId.of(command.organizationId());
        OrganizationName newName = OrganizationName.of(command.name());
        return new UpdateContext<>(id, newName, timeProvider.now());
    }

    // ==================== Status Change Context 생성 ====================

    /**
     * UpdateOrganizationStatusCommand로부터 StatusChangeContext 생성
     *
     * <p>상태 변경에 필요한 ID와 변경 시간을 한 번에 생성합니다.
     *
     * @param command 상태 변경 Command
     * @return StatusChangeContext (id, changedAt)
     */
    public StatusChangeContext<OrganizationId> createStatusChangeContext(
            UpdateOrganizationStatusCommand command) {
        OrganizationId id = OrganizationId.of(command.organizationId());
        return new StatusChangeContext<>(id, timeProvider.now());
    }
}
