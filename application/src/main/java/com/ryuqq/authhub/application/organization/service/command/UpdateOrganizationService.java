package com.ryuqq.authhub.application.organization.service.command;

import com.ryuqq.authhub.application.organization.assembler.OrganizationAssembler;
import com.ryuqq.authhub.application.organization.dto.command.UpdateOrganizationCommand;
import com.ryuqq.authhub.application.organization.dto.response.OrganizationResponse;
import com.ryuqq.authhub.application.organization.manager.command.OrganizationTransactionManager;
import com.ryuqq.authhub.application.organization.manager.query.OrganizationReadManager;
import com.ryuqq.authhub.application.organization.port.in.command.UpdateOrganizationUseCase;
import com.ryuqq.authhub.domain.organization.aggregate.Organization;
import com.ryuqq.authhub.domain.organization.exception.DuplicateOrganizationNameException;
import com.ryuqq.authhub.domain.organization.identifier.OrganizationId;
import com.ryuqq.authhub.domain.organization.vo.OrganizationName;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import java.time.Clock;
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
 *   <li>Manager → Domain Method → Manager → Assembler 흐름
 *   <li>Port 직접 호출 금지
 *   <li>Lombok 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Service
public class UpdateOrganizationService implements UpdateOrganizationUseCase {

    private final OrganizationTransactionManager transactionManager;
    private final OrganizationReadManager readManager;
    private final OrganizationAssembler assembler;
    private final Clock clock;

    public UpdateOrganizationService(
            OrganizationTransactionManager transactionManager,
            OrganizationReadManager readManager,
            OrganizationAssembler assembler,
            Clock clock) {
        this.transactionManager = transactionManager;
        this.readManager = readManager;
        this.assembler = assembler;
        this.clock = clock;
    }

    @Override
    public OrganizationResponse execute(UpdateOrganizationCommand command) {
        // 1. ReadManager: 기존 조직 조회
        OrganizationId organizationId = OrganizationId.of(command.organizationId());
        Organization organization = readManager.findById(organizationId);

        // 2. 테넌트 내 중복 이름 검사 (다른 조직과 충돌 확인)
        OrganizationName newName = OrganizationName.of(command.name());
        TenantId tenantId = organization.getTenantId();
        if (!organization.nameValue().equals(command.name())
                && readManager.existsByTenantIdAndName(tenantId, newName)) {
            throw new DuplicateOrganizationNameException(tenantId, newName);
        }

        // 3. Domain: 이름 변경
        Organization updated = organization.changeName(newName, clock);

        // 4. Manager: 영속화
        Organization saved = transactionManager.persist(updated);

        // 5. Assembler: Response 변환
        return assembler.toResponse(saved);
    }
}
