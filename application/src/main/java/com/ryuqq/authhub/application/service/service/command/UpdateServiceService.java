package com.ryuqq.authhub.application.service.service.command;

import com.ryuqq.authhub.application.common.dto.command.UpdateContext;
import com.ryuqq.authhub.application.service.dto.command.UpdateServiceCommand;
import com.ryuqq.authhub.application.service.factory.ServiceCommandFactory;
import com.ryuqq.authhub.application.service.manager.ServiceCommandManager;
import com.ryuqq.authhub.application.service.port.in.command.UpdateServiceUseCase;
import com.ryuqq.authhub.application.service.validator.ServiceValidator;
import com.ryuqq.authhub.domain.service.aggregate.Service;
import com.ryuqq.authhub.domain.service.aggregate.ServiceUpdateData;
import com.ryuqq.authhub.domain.service.id.ServiceId;

/**
 * UpdateServiceService - 서비스 수정 Service
 *
 * <p>UpdateServiceUseCase를 구현합니다.
 *
 * <p><strong>처리 흐름:</strong>
 *
 * <ol>
 *   <li>Factory: Command → UpdateContext (ServiceId + ServiceUpdateData + changedAt)
 *   <li>Validator: 존재 여부 검증 및 조회
 *   <li>Domain: service.update(updateData, changedAt)
 *   <li>Manager: 영속화
 * </ol>
 *
 * <p>SVC-001: @Service 어노테이션 필수.
 *
 * <p>SVC-002: UseCase(Port-In) 인터페이스 구현 필수.
 *
 * <p>SVC-006: @Transactional 금지 → Manager에서 처리.
 *
 * <p>SVC-007: Service에 비즈니스 로직 금지 → 오케스트레이션만.
 *
 * @author development-team
 * @since 1.0.0
 */
@org.springframework.stereotype.Service
public class UpdateServiceService implements UpdateServiceUseCase {

    private final ServiceValidator validator;
    private final ServiceCommandFactory commandFactory;
    private final ServiceCommandManager commandManager;

    public UpdateServiceService(
            ServiceValidator validator,
            ServiceCommandFactory commandFactory,
            ServiceCommandManager commandManager) {
        this.validator = validator;
        this.commandFactory = commandFactory;
        this.commandManager = commandManager;
    }

    @Override
    public void execute(UpdateServiceCommand command) {
        // 1. Factory: Command → UpdateContext
        UpdateContext<ServiceId, ServiceUpdateData> context =
                commandFactory.createUpdateContext(command);

        // 2. Validator: 존재 여부 검증 및 조회
        Service service = validator.findExistingOrThrow(context.id());

        // 3. Domain: 비즈니스 로직 수행
        service.update(context.updateData(), context.changedAt());

        // 4. Manager: 영속화
        commandManager.persist(service);
    }
}
