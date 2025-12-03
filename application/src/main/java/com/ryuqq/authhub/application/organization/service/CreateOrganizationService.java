package com.ryuqq.authhub.application.organization.service;

import com.ryuqq.authhub.application.organization.assembler.OrganizationCommandAssembler;
import com.ryuqq.authhub.application.organization.dto.command.CreateOrganizationCommand;
import com.ryuqq.authhub.application.organization.dto.response.CreateOrganizationResponse;
import com.ryuqq.authhub.application.organization.manager.OrganizationManager;
import com.ryuqq.authhub.application.organization.port.in.CreateOrganizationUseCase;
import com.ryuqq.authhub.application.user.component.TenantValidator;
import com.ryuqq.authhub.domain.organization.aggregate.Organization;
import com.ryuqq.authhub.domain.organization.identifier.OrganizationId;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import org.springframework.stereotype.Service;

/**
 * CreateOrganizationService - 조직 생성 UseCase 구현체
 *
 * <p>조직 생성을 orchestration합니다.
 *
 * <p><strong>구조:</strong>
 *
 * <ul>
 *   <li>Service → Manager → Port (트랜잭션은 Manager에서 관리)
 *   <li>TenantValidator로 검증 로직 위임
 *   <li>CommandAssembler로 변환 로직 위임
 *   <li>QueryAssembler로 Response 변환
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>Transaction 내 외부 API 호출 금지
 *   <li>Command null 체크 금지 (외부 레이어에서 검증됨)
 *   <li>비즈니스 로직 Service 노출 금지 (Domain에 위임)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Service
public class CreateOrganizationService implements CreateOrganizationUseCase {

    private final OrganizationManager organizationManager;
    private final TenantValidator tenantValidator;
    private final OrganizationCommandAssembler organizationCommandAssembler;

    public CreateOrganizationService(
            OrganizationManager organizationManager,
            TenantValidator tenantValidator,
            OrganizationCommandAssembler organizationCommandAssembler) {
        this.organizationManager = organizationManager;
        this.tenantValidator = tenantValidator;
        this.organizationCommandAssembler = organizationCommandAssembler;
    }

    @Override
    public CreateOrganizationResponse execute(CreateOrganizationCommand command) {
        // 1. Tenant 검증 (존재 + 활성 상태)
        tenantValidator.validate(TenantId.of(command.tenantId()));

        // 2. Organization Domain 생성 (CommandAssembler에서 Clock 처리)
        Organization organization = organizationCommandAssembler.toOrganization(command);

        // 3. 영속화 (Manager 경유 - 트랜잭션 관리)
        OrganizationId savedId = organizationManager.persist(organization);

        return organizationCommandAssembler.toCreateResponse(savedId);
    }
}
