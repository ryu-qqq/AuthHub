package com.ryuqq.authhub.application.organization.service.command;

import com.ryuqq.authhub.application.organization.dto.command.DeleteOrganizationCommand;
import com.ryuqq.authhub.application.organization.manager.command.OrganizationTransactionManager;
import com.ryuqq.authhub.application.organization.manager.query.OrganizationReadManager;
import com.ryuqq.authhub.application.organization.port.in.command.DeleteOrganizationUseCase;
import com.ryuqq.authhub.domain.organization.aggregate.Organization;
import com.ryuqq.authhub.domain.organization.identifier.OrganizationId;
import java.time.Clock;
import org.springframework.stereotype.Service;

/**
 * DeleteOrganizationService - 조직 삭제 Service
 *
 * <p>DeleteOrganizationUseCase를 구현합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code @Service} 어노테이션
 *   <li>{@code @Transactional} 직접 사용 금지 (Manager/Facade 책임)
 *   <li>Manager → Domain Method → Manager 흐름
 *   <li>Port 직접 호출 금지
 *   <li>Lombok 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Service
public class DeleteOrganizationService implements DeleteOrganizationUseCase {

    private final OrganizationTransactionManager transactionManager;
    private final OrganizationReadManager readManager;
    private final Clock clock;

    public DeleteOrganizationService(
            OrganizationTransactionManager transactionManager,
            OrganizationReadManager readManager,
            Clock clock) {
        this.transactionManager = transactionManager;
        this.readManager = readManager;
        this.clock = clock;
    }

    @Override
    public void execute(DeleteOrganizationCommand command) {
        // 1. ReadManager: 기존 조직 조회
        OrganizationId organizationId = OrganizationId.of(command.organizationId());
        Organization organization = readManager.findById(organizationId);

        // 2. Domain: 삭제 (Soft Delete)
        Organization deleted = organization.delete(clock);

        // 3. Manager: 영속화
        transactionManager.persist(deleted);
    }
}
