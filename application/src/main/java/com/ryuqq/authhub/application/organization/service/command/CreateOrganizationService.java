package com.ryuqq.authhub.application.organization.service.command;

import com.ryuqq.authhub.application.organization.assembler.OrganizationAssembler;
import com.ryuqq.authhub.application.organization.dto.command.CreateOrganizationCommand;
import com.ryuqq.authhub.application.organization.dto.response.OrganizationResponse;
import com.ryuqq.authhub.application.organization.factory.command.OrganizationCommandFactory;
import com.ryuqq.authhub.application.organization.manager.command.OrganizationTransactionManager;
import com.ryuqq.authhub.application.organization.manager.query.OrganizationReadManager;
import com.ryuqq.authhub.application.organization.port.in.command.CreateOrganizationUseCase;
import com.ryuqq.authhub.domain.organization.aggregate.Organization;
import com.ryuqq.authhub.domain.organization.exception.DuplicateOrganizationNameException;
import com.ryuqq.authhub.domain.organization.vo.OrganizationName;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import org.springframework.stereotype.Service;

/**
 * CreateOrganizationService - 조직 생성 Service
 *
 * <p>CreateOrganizationUseCase를 구현합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code @Service} 어노테이션
 *   <li>{@code @Transactional} 직접 사용 금지 (Manager/Facade 책임)
 *   <li>Factory → Manager/Facade → Assembler 흐름
 *   <li>Port 직접 호출 금지
 *   <li>Lombok 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Service
public class CreateOrganizationService implements CreateOrganizationUseCase {

    private final OrganizationCommandFactory commandFactory;
    private final OrganizationTransactionManager transactionManager;
    private final OrganizationReadManager readManager;
    private final OrganizationAssembler assembler;

    public CreateOrganizationService(
            OrganizationCommandFactory commandFactory,
            OrganizationTransactionManager transactionManager,
            OrganizationReadManager readManager,
            OrganizationAssembler assembler) {
        this.commandFactory = commandFactory;
        this.transactionManager = transactionManager;
        this.readManager = readManager;
        this.assembler = assembler;
    }

    @Override
    public OrganizationResponse execute(CreateOrganizationCommand command) {
        // 1. 테넌트 내 중복 이름 검사
        TenantId tenantId = TenantId.of(command.tenantId());
        OrganizationName name = OrganizationName.of(command.name());
        if (readManager.existsByTenantIdAndName(tenantId, name)) {
            throw new DuplicateOrganizationNameException(tenantId, name);
        }

        // 2. Factory: Command → Domain
        Organization organization = commandFactory.create(command);

        // 3. Manager: 영속화
        Organization saved = transactionManager.persist(organization);

        // 4. Assembler: Response 변환
        return assembler.toResponse(saved);
    }
}
