package com.ryuqq.authhub.application.service.service.command;

import com.ryuqq.authhub.application.service.dto.command.CreateServiceCommand;
import com.ryuqq.authhub.application.service.factory.ServiceCommandFactory;
import com.ryuqq.authhub.application.service.manager.ServiceCommandManager;
import com.ryuqq.authhub.application.service.port.in.command.CreateServiceUseCase;
import com.ryuqq.authhub.application.service.validator.ServiceValidator;
import com.ryuqq.authhub.domain.service.aggregate.Service;
import com.ryuqq.authhub.domain.service.vo.ServiceCode;

/**
 * CreateServiceService - 서비스 생성 Service
 *
 * <p>CreateServiceUseCase를 구현합니다.
 *
 * <p>SVC-001: @Service 어노테이션 필수.
 *
 * <p>SVC-002: UseCase(Port-In) 인터페이스 구현 필수.
 *
 * <p>SVC-003: Domain 객체 직접 생성 금지 → Factory 사용.
 *
 * <p>SVC-006: @Transactional 금지 → Manager에서 처리.
 *
 * <p>SVC-007: Service에 비즈니스 로직 금지 → 오케스트레이션만.
 *
 * <p>SVC-008: Port(Out) 직접 주입 금지 → Manager 사용.
 *
 * <p>MGR-001: 파라미터는 원시타입 대신 VO를 사용합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
@org.springframework.stereotype.Service
public class CreateServiceService implements CreateServiceUseCase {

    private final ServiceValidator validator;
    private final ServiceCommandFactory commandFactory;
    private final ServiceCommandManager commandManager;

    public CreateServiceService(
            ServiceValidator validator,
            ServiceCommandFactory commandFactory,
            ServiceCommandManager commandManager) {
        this.validator = validator;
        this.commandFactory = commandFactory;
        this.commandManager = commandManager;
    }

    @Override
    public Long execute(CreateServiceCommand command) {
        // 1. Validator: 중복 코드 검증
        validator.validateCodeNotDuplicated(ServiceCode.of(command.serviceCode()));

        // 2. Factory: Command → Domain
        Service service = commandFactory.create(command);

        // 3. Manager: 영속화 → ID 반환
        return commandManager.persist(service);
    }
}
