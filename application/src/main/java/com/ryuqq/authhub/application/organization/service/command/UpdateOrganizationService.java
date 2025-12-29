package com.ryuqq.authhub.application.organization.service.command;

import com.ryuqq.authhub.application.organization.assembler.OrganizationAssembler;
import com.ryuqq.authhub.application.organization.dto.command.UpdateOrganizationCommand;
import com.ryuqq.authhub.application.organization.dto.response.OrganizationResponse;
import com.ryuqq.authhub.application.organization.factory.command.OrganizationCommandFactory;
import com.ryuqq.authhub.application.organization.manager.command.OrganizationTransactionManager;
import com.ryuqq.authhub.application.organization.manager.query.OrganizationReadManager;
import com.ryuqq.authhub.application.organization.port.in.command.UpdateOrganizationUseCase;
import com.ryuqq.authhub.application.organization.validator.OrganizationValidator;
import com.ryuqq.authhub.domain.organization.aggregate.Organization;
import com.ryuqq.authhub.domain.organization.identifier.OrganizationId;
import com.ryuqq.authhub.domain.organization.vo.OrganizationName;
import org.springframework.stereotype.Service;

/**
 * UpdateOrganizationService - 조직 수정 Service
 *
 * <p>UpdateOrganizationUseCase를 구현합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code @Service} 어노테이션
 *   <li>{@code @Transactional} 직접 사용 금지 (Manager/Facade 책임)
 *   <li>ReadManager → Validator → Domain → Manager → Assembler 흐름
 *   <li>Port 직접 호출 금지
 *   <li>Lombok 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Service
public class UpdateOrganizationService implements UpdateOrganizationUseCase {

    private final OrganizationReadManager readManager;
    private final OrganizationValidator validator;
    private final OrganizationCommandFactory commandFactory;
    private final OrganizationTransactionManager transactionManager;
    private final OrganizationAssembler assembler;

    public UpdateOrganizationService(
            OrganizationReadManager readManager,
            OrganizationValidator validator,
            OrganizationCommandFactory commandFactory,
            OrganizationTransactionManager transactionManager,
            OrganizationAssembler assembler) {
        this.readManager = readManager;
        this.validator = validator;
        this.commandFactory = commandFactory;
        this.transactionManager = transactionManager;
        this.assembler = assembler;
    }

    @Override
    public OrganizationResponse execute(UpdateOrganizationCommand command) {
        // 1. ReadManager: 기존 조직 조회
        OrganizationId organizationId = OrganizationId.of(command.organizationId());
        Organization organization = readManager.findById(organizationId);

        // 2. Factory: String → VO 변환
        OrganizationName newName = commandFactory.toName(command.name());

        // 3. Validator: 테넌트 내 중복 이름 검증 (자기 자신 제외)
        validator.validateNameNotDuplicatedExcluding(
                organization.getTenantId(), newName, organization.getName());

        // 4. Factory: 이름 변경 적용
        Organization updated = commandFactory.applyNameChange(organization, newName);

        // 5. Manager: 영속화
        Organization saved = transactionManager.persist(updated);

        // 6. Assembler: Response 변환
        return assembler.toResponse(saved);
    }
}
