package com.ryuqq.authhub.application.role.service.command;

import com.ryuqq.authhub.application.role.dto.command.DeleteRoleCommand;
import com.ryuqq.authhub.application.role.internal.RoleDeleteCoordinator;
import com.ryuqq.authhub.application.role.port.in.command.DeleteRoleUseCase;
import org.springframework.stereotype.Service;

/**
 * DeleteRoleService - 역할 삭제 Service
 *
 * <p>DeleteRoleUseCase를 구현합니다.
 *
 * <p>삭제 전 검증:
 *
 * <ul>
 *   <li>SYSTEM 역할은 삭제 불가 (Domain에서 예외 발생)
 *   <li>User에 할당된 역할은 삭제 불가 (Coordinator에서 처리)
 * </ul>
 *
 * <p>SVC-001: @Service 어노테이션 필수.
 *
 * <p>SVC-002: UseCase(Port-In) 인터페이스 구현 필수.
 *
 * <p>SVC-006: @Transactional 금지 → Manager에서 처리.
 *
 * <p>SVC-007: Service에 비즈니스 로직 금지 → Coordinator에 위임.
 *
 * @author development-team
 * @since 1.0.0
 */
@Service
public class DeleteRoleService implements DeleteRoleUseCase {

    private final RoleDeleteCoordinator deleteCoordinator;

    public DeleteRoleService(RoleDeleteCoordinator deleteCoordinator) {
        this.deleteCoordinator = deleteCoordinator;
    }

    @Override
    public void execute(DeleteRoleCommand command) {
        deleteCoordinator.execute(command);
    }
}
