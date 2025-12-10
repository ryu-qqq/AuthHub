package com.ryuqq.authhub.application.organization.service.command;

import com.ryuqq.authhub.application.organization.assembler.OrganizationAssembler;
import com.ryuqq.authhub.application.organization.dto.command.UpdateOrganizationStatusCommand;
import com.ryuqq.authhub.application.organization.dto.response.OrganizationResponse;
import com.ryuqq.authhub.application.organization.manager.command.OrganizationTransactionManager;
import com.ryuqq.authhub.application.organization.manager.query.OrganizationReadManager;
import com.ryuqq.authhub.application.organization.port.in.command.UpdateOrganizationStatusUseCase;
import com.ryuqq.authhub.domain.organization.aggregate.Organization;
import com.ryuqq.authhub.domain.organization.identifier.OrganizationId;
import com.ryuqq.authhub.domain.organization.vo.OrganizationStatus;
import java.time.Clock;
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
 *   <li>ReadManager → Domain → TransactionManager 흐름
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
    private final OrganizationTransactionManager transactionManager;
    private final OrganizationAssembler assembler;
    private final Clock clock;

    public UpdateOrganizationStatusService(
            OrganizationReadManager readManager,
            OrganizationTransactionManager transactionManager,
            OrganizationAssembler assembler,
            Clock clock) {
        this.readManager = readManager;
        this.transactionManager = transactionManager;
        this.assembler = assembler;
        this.clock = clock;
    }

    @Override
    public OrganizationResponse execute(UpdateOrganizationStatusCommand command) {
        // 1. 기존 조직 조회
        OrganizationId organizationId = OrganizationId.of(command.organizationId());
        Organization organization = readManager.findById(organizationId);

        // 2. 상태 변경 (도메인 로직 실행)
        OrganizationStatus targetStatus = OrganizationStatus.valueOf(command.status());
        Organization updatedOrganization = applyStatusTransition(organization, targetStatus);

        // 3. 영속화
        Organization savedOrganization = transactionManager.persist(updatedOrganization);

        // 4. 응답 변환
        return assembler.toResponse(savedOrganization);
    }

    private Organization applyStatusTransition(
            Organization organization, OrganizationStatus targetStatus) {
        return switch (targetStatus) {
            case ACTIVE -> organization.activate(clock);
            case INACTIVE -> organization.deactivate(clock);
            case DELETED ->
                    throw new IllegalArgumentException(
                            "DELETED 상태로의 변경은 DeleteOrganizationUseCase를 사용하세요");
        };
    }
}
