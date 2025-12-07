package com.ryuqq.authhub.application.tenant.service.command;

import com.ryuqq.authhub.application.tenant.assembler.TenantAssembler;
import com.ryuqq.authhub.application.tenant.dto.command.CreateTenantCommand;
import com.ryuqq.authhub.application.tenant.dto.response.TenantResponse;
import com.ryuqq.authhub.application.tenant.factory.command.TenantCommandFactory;
import com.ryuqq.authhub.application.tenant.manager.command.TenantTransactionManager;
import com.ryuqq.authhub.application.tenant.manager.query.TenantReadManager;
import com.ryuqq.authhub.application.tenant.port.in.command.CreateTenantUseCase;
import com.ryuqq.authhub.domain.tenant.aggregate.Tenant;
import com.ryuqq.authhub.domain.tenant.exception.DuplicateTenantNameException;
import com.ryuqq.authhub.domain.tenant.vo.TenantName;
import org.springframework.stereotype.Service;

/**
 * CreateTenantService - 테넌트 생성 Service
 *
 * <p>CreateTenantUseCase를 구현합니다.
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
public class CreateTenantService implements CreateTenantUseCase {

    private final TenantCommandFactory commandFactory;
    private final TenantTransactionManager transactionManager;
    private final TenantReadManager readManager;
    private final TenantAssembler assembler;

    public CreateTenantService(
            TenantCommandFactory commandFactory,
            TenantTransactionManager transactionManager,
            TenantReadManager readManager,
            TenantAssembler assembler) {
        this.commandFactory = commandFactory;
        this.transactionManager = transactionManager;
        this.readManager = readManager;
        this.assembler = assembler;
    }

    @Override
    public TenantResponse execute(CreateTenantCommand command) {
        // 1. 중복 이름 검사
        TenantName name = TenantName.of(command.name());
        if (readManager.existsByName(name)) {
            throw new DuplicateTenantNameException(name);
        }

        // 2. Factory: Command → Domain
        Tenant tenant = commandFactory.create(command);

        // 3. Manager: 영속화
        Tenant saved = transactionManager.persist(tenant);

        // 4. Assembler: Response 변환
        return assembler.toResponse(saved);
    }
}
