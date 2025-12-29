package com.ryuqq.authhub.application.organization.service.command;

import com.ryuqq.authhub.application.organization.assembler.OrganizationAssembler;
import com.ryuqq.authhub.application.organization.dto.command.UpdateOrganizationStatusCommand;
import com.ryuqq.authhub.application.organization.dto.response.OrganizationResponse;
import com.ryuqq.authhub.application.organization.factory.command.OrganizationCommandFactory;
import com.ryuqq.authhub.application.organization.manager.command.OrganizationTransactionManager;
import com.ryuqq.authhub.application.organization.manager.query.OrganizationReadManager;
import com.ryuqq.authhub.application.organization.port.in.command.UpdateOrganizationStatusUseCase;
import com.ryuqq.authhub.domain.organization.aggregate.Organization;
import com.ryuqq.authhub.domain.organization.identifier.OrganizationId;
import org.springframework.stereotype.Service;

/**
 * UpdateOrganizationStatusService - 조직 상태 변경 Service
 *
 * <p>UpdateOrganizationStatusUseCase를 구현합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code @Service} 어노테이션
 *   <li>{@code @Transactional} 직접 사용 금지 (Manager/Facade 책임)
 *   <li>ReadManager → Factory → TransactionManager → Assembler 흐름
 *   <li>Port 직접 호출 금지
 *   <li>Lombok 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Service
public class UpdateOrganizationStatusService implements UpdateOrganizationStatusUseCase {

    private final OrganizationReadManager readManager;
    private final OrganizationCommandFactory commandFactory;
    private final OrganizationTransactionManager transactionManager;
    private final OrganizationAssembler assembler;

    public UpdateOrganizationStatusService(
            OrganizationReadManager readManager,
            OrganizationCommandFactory commandFactory,
            OrganizationTransactionManager transactionManager,
            OrganizationAssembler assembler) {
        this.readManager = readManager;
        this.commandFactory = commandFactory;
        this.transactionManager = transactionManager;
        this.assembler = assembler;
    }

    @Override
    public OrganizationResponse execute(UpdateOrganizationStatusCommand command) {
        // 1. 기존 조직 조회
        OrganizationId organizationId = OrganizationId.of(command.organizationId());
        Organization organization = readManager.findById(organizationId);

        // 2. Factory: 상태 변경 적용
        Organization updatedOrganization =
                commandFactory.applyStatusChange(organization, command.status());

        // 3. 영속화
        Organization savedOrganization = transactionManager.persist(updatedOrganization);

        // 4. 응답 변환
        return assembler.toResponse(savedOrganization);
    }
}
