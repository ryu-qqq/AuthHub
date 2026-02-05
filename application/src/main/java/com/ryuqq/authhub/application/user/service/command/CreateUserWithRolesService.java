package com.ryuqq.authhub.application.user.service.command;

import com.ryuqq.authhub.application.user.dto.command.CreateUserWithRolesCommand;
import com.ryuqq.authhub.application.user.dto.response.CreateUserWithRolesResult;
import com.ryuqq.authhub.application.user.factory.CreateUserWithRolesCommandFactory;
import com.ryuqq.authhub.application.user.internal.CreateUserWithRolesBundle;
import com.ryuqq.authhub.application.user.internal.CreateUserWithRolesCoordinator;
import com.ryuqq.authhub.application.user.port.in.command.CreateUserWithRolesUseCase;
import org.springframework.stereotype.Service;

/**
 * CreateUserWithRolesService - 사용자 생성 + 역할 할당 Service
 *
 * <p>CreateUserWithRolesUseCase를 구현합니다.
 *
 * <p><strong>처리 흐름:</strong>
 *
 * <ol>
 *   <li>Factory로 Bundle 생성 (User + 빈 UserRole + serviceCode + roleNames)
 *   <li>Coordinator에 Bundle 전달 (검증 → 역할 해석 → 영속화)
 * </ol>
 *
 * <p>SVC-001: @Service 어노테이션 필수.
 *
 * <p>SVC-002: UseCase(Port-In) 인터페이스 구현 필수.
 *
 * <p>SVC-006: @Transactional 금지 → Facade에서 처리.
 *
 * <p>SVC-007: Service에 비즈니스 로직 금지 → Coordinator에 위임.
 *
 * @author development-team
 * @since 1.0.0
 */
@Service
public class CreateUserWithRolesService implements CreateUserWithRolesUseCase {

    private final CreateUserWithRolesCommandFactory createUserWithRolesCommandFactory;
    private final CreateUserWithRolesCoordinator coordinator;

    public CreateUserWithRolesService(
            CreateUserWithRolesCommandFactory createUserWithRolesCommandFactory,
            CreateUserWithRolesCoordinator coordinator) {
        this.createUserWithRolesCommandFactory = createUserWithRolesCommandFactory;
        this.coordinator = coordinator;
    }

    @Override
    public CreateUserWithRolesResult execute(CreateUserWithRolesCommand command) {
        CreateUserWithRolesBundle bundle = createUserWithRolesCommandFactory.create(command);
        return coordinator.coordinate(bundle);
    }
}
