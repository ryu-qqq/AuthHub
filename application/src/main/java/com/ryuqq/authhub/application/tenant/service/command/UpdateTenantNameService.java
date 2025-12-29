package com.ryuqq.authhub.application.tenant.service.command;

import com.ryuqq.authhub.application.tenant.assembler.TenantAssembler;
import com.ryuqq.authhub.application.tenant.dto.command.UpdateTenantNameCommand;
import com.ryuqq.authhub.application.tenant.dto.response.TenantResponse;
import com.ryuqq.authhub.application.tenant.factory.command.TenantCommandFactory;
import com.ryuqq.authhub.application.tenant.manager.command.TenantTransactionManager;
import com.ryuqq.authhub.application.tenant.manager.query.TenantReadManager;
import com.ryuqq.authhub.application.tenant.port.in.command.UpdateTenantNameUseCase;
import com.ryuqq.authhub.application.tenant.validator.TenantValidator;
import com.ryuqq.authhub.domain.tenant.aggregate.Tenant;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import com.ryuqq.authhub.domain.tenant.vo.TenantName;
import org.springframework.stereotype.Service;

/**
 * UpdateTenantNameService - 테넌트 이름 변경 Service
 *
 * <p>UpdateTenantNameUseCase를 구현합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code @Service} 어노테이션
 *   <li>{@code @Transactional} 직접 사용 금지 (Manager/Facade 책임)
 *   <li>ReadManager → Validator → Domain → TransactionManager → Assembler 흐름
 *   <li>Port 직접 호출 금지
 *   <li>Lombok 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Service
public class UpdateTenantNameService implements UpdateTenantNameUseCase {

    private final TenantReadManager readManager;
    private final TenantValidator validator;
    private final TenantCommandFactory commandFactory;
    private final TenantTransactionManager transactionManager;
    private final TenantAssembler assembler;

    public UpdateTenantNameService(
            TenantReadManager readManager,
            TenantValidator validator,
            TenantCommandFactory commandFactory,
            TenantTransactionManager transactionManager,
            TenantAssembler assembler) {
        this.readManager = readManager;
        this.validator = validator;
        this.commandFactory = commandFactory;
        this.transactionManager = transactionManager;
        this.assembler = assembler;
    }

    @Override
    public TenantResponse execute(UpdateTenantNameCommand command) {
        // 1. 기존 테넌트 조회
        TenantId tenantId = TenantId.of(command.tenantId());
        Tenant tenant = readManager.findById(tenantId);

        // 2. Factory: String → VO 변환
        TenantName newName = commandFactory.toName(command.name());

        // 3. Validator: 중복 이름 검증 (자기 자신 제외)
        validator.validateNameNotDuplicatedExcluding(newName, tenant.getName());

        // 4. Factory: 이름 변경 적용
        Tenant updatedTenant = commandFactory.applyNameChange(tenant, newName);

        // 5. Manager: 영속화
        Tenant savedTenant = transactionManager.persist(updatedTenant);

        // 6. Assembler: Response 변환
        return assembler.toResponse(savedTenant);
    }
}
