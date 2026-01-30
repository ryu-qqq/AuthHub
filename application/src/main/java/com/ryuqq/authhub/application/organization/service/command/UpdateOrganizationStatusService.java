package com.ryuqq.authhub.application.organization.service.command;

import com.ryuqq.authhub.application.common.dto.command.StatusChangeContext;
import com.ryuqq.authhub.application.organization.dto.command.UpdateOrganizationStatusCommand;
import com.ryuqq.authhub.application.organization.factory.OrganizationCommandFactory;
import com.ryuqq.authhub.application.organization.manager.OrganizationCommandManager;
import com.ryuqq.authhub.application.organization.port.in.command.UpdateOrganizationStatusUseCase;
import com.ryuqq.authhub.application.organization.validator.OrganizationValidator;
import com.ryuqq.authhub.domain.organization.aggregate.Organization;
import com.ryuqq.authhub.domain.organization.id.OrganizationId;
import com.ryuqq.authhub.domain.organization.vo.OrganizationStatus;
import org.springframework.stereotype.Service;

/**
 * UpdateOrganizationStatusService - 조직 상태 변경 Service
 *
 * <p>UpdateOrganizationStatusUseCase를 구현합니다.
 *
 * <p>SVC-001: @Service 어노테이션 필수.
 *
 * <p>SVC-002: UseCase(Port-In) 인터페이스 구현 필수.
 *
 * <p>SVC-006: @Transactional 금지 → Manager에서 처리.
 *
 * <p>SVC-007: Service에 비즈니스 로직 금지 → 오케스트레이션만.
 *
 * <p>SVC-008: Port(Out) 직접 주입 금지 → Manager 사용.
 *
 * <p>APP-VAL-001: Validator의 findExistingOrThrow 메서드로 Domain 객체를 조회합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
@Service
public class UpdateOrganizationStatusService implements UpdateOrganizationStatusUseCase {

    private final OrganizationValidator validator;
    private final OrganizationCommandFactory commandFactory;
    private final OrganizationCommandManager commandManager;

    public UpdateOrganizationStatusService(
            OrganizationValidator validator,
            OrganizationCommandFactory commandFactory,
            OrganizationCommandManager commandManager) {
        this.validator = validator;
        this.commandFactory = commandFactory;
        this.commandManager = commandManager;
    }

    @Override
    public void execute(UpdateOrganizationStatusCommand command) {
        // 1. Factory: StatusChangeContext 생성 (id, changedAt 번들)
        StatusChangeContext<OrganizationId> context =
                commandFactory.createStatusChangeContext(command);

        // 2. VO: 대상 상태 변환
        OrganizationStatus targetStatus = OrganizationStatus.valueOf(command.status());

        // 3. Validator: 기존 엔티티 조회 (없으면 예외)
        Organization organization = validator.findExistingOrThrow(context.id());

        // 4. Domain: 상태 변경 적용
        organization.changeStatus(targetStatus, context.changedAt());

        // 5. Manager: 영속화
        commandManager.persist(organization);
    }
}
