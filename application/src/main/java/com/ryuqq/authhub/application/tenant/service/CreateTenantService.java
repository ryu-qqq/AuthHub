package com.ryuqq.authhub.application.tenant.service;

import com.ryuqq.authhub.application.tenant.assembler.TenantCommandAssembler;
import com.ryuqq.authhub.application.tenant.dto.command.CreateTenantCommand;
import com.ryuqq.authhub.application.tenant.dto.response.CreateTenantResponse;
import com.ryuqq.authhub.application.tenant.manager.TenantManager;
import com.ryuqq.authhub.application.tenant.port.in.CreateTenantUseCase;
import com.ryuqq.authhub.domain.tenant.aggregate.Tenant;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import org.springframework.stereotype.Service;

/**
 * CreateTenantService - 테넌트 생성 UseCase 구현체
 *
 * <p>새로운 테넌트를 생성합니다.
 *
 * <p><strong>흐름:</strong>
 *
 * <ol>
 *   <li>CommandAssembler로 Command → Domain 변환
 *   <li>Manager로 영속화
 *   <li>QueryAssembler로 Response 변환 및 반환
 * </ol>
 *
 * @author development-team
 * @since 1.0.0
 */
@Service
public class CreateTenantService implements CreateTenantUseCase {

    private final TenantCommandAssembler tenantCommandAssembler;
    private final TenantManager tenantManager;

    public CreateTenantService(
            TenantCommandAssembler tenantCommandAssembler, TenantManager tenantManager) {
        this.tenantCommandAssembler = tenantCommandAssembler;
        this.tenantManager = tenantManager;
    }

    @Override
    public CreateTenantResponse execute(CreateTenantCommand command) {
        // 1. Command → Domain 변환
        Tenant tenant = tenantCommandAssembler.toTenant(command);
        // 2. 영속화
        TenantId tenantId = tenantManager.persist(tenant);

        return tenantCommandAssembler.toCreateResponse(tenantId);
    }
}
