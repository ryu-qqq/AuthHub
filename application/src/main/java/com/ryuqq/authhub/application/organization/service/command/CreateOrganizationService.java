package com.ryuqq.authhub.application.organization.service.command;

import com.ryuqq.authhub.application.organization.dto.command.CreateOrganizationCommand;
import com.ryuqq.authhub.application.organization.factory.OrganizationCommandFactory;
import com.ryuqq.authhub.application.organization.manager.OrganizationCommandManager;
import com.ryuqq.authhub.application.organization.port.in.command.CreateOrganizationUseCase;
import com.ryuqq.authhub.application.organization.validator.OrganizationValidator;
import com.ryuqq.authhub.domain.organization.aggregate.Organization;
import com.ryuqq.authhub.domain.organization.vo.OrganizationName;
import com.ryuqq.authhub.domain.tenant.id.TenantId;
import org.springframework.stereotype.Service;

/**
 * CreateOrganizationService - 조직 생성 Service
 *
 * <p>CreateOrganizationUseCase를 구현합니다.
 *
 * <p>SVC-001: @Service 어노테이션 필수.
 *
 * <p>SVC-002: UseCase(Port-In) 인터페이스 구현 필수.
 *
 * <p>SVC-003: Domain 객체 직접 생성 금지 → Factory 사용.
 *
 * <p>SVC-006: @Transactional 금지 → Manager에서 처리.
 *
 * <p>SVC-007: Service에 비즈니스 로직 금지 → 오케스트레이션만.
 *
 * <p>SVC-008: Port(Out) 직접 주입 금지 → Manager 사용.
 *
 * <p>MGR-001: 파라미터는 원시타입 대신 VO를 사용합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
@Service
public class CreateOrganizationService implements CreateOrganizationUseCase {

    private final OrganizationValidator validator;
    private final OrganizationCommandFactory commandFactory;
    private final OrganizationCommandManager commandManager;

    public CreateOrganizationService(
            OrganizationValidator validator,
            OrganizationCommandFactory commandFactory,
            OrganizationCommandManager commandManager) {
        this.validator = validator;
        this.commandFactory = commandFactory;
        this.commandManager = commandManager;
    }

    @Override
    public String execute(CreateOrganizationCommand command) {
        // 1. Validator: 테넌트 내 중복 이름 검증
        TenantId tenantId = TenantId.of(command.tenantId());
        OrganizationName name = OrganizationName.of(command.name());
        validator.validateNameNotDuplicated(tenantId, name);

        // 2. Factory: Command → Domain
        Organization organization = commandFactory.create(command);

        // 3. Manager: 영속화 → ID 반환
        return commandManager.persist(organization);
    }
}
