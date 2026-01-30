package com.ryuqq.authhub.application.permission.service.command;

import com.ryuqq.authhub.application.permission.dto.command.DeletePermissionCommand;
import com.ryuqq.authhub.application.permission.internal.PermissionDeleteCoordinator;
import com.ryuqq.authhub.application.permission.port.in.command.DeletePermissionUseCase;
import org.springframework.stereotype.Service;

/**
 * DeletePermissionService - 권한 삭제 Service
 *
 * <p>DeletePermissionUseCase를 구현합니다.
 *
 * <p>삭제 전 검증:
 *
 * <ul>
 *   <li>SYSTEM 권한은 삭제 불가 (Domain에서 예외 발생)
 *   <li>Role에 할당된 권한은 삭제 불가 (Coordinator에서 처리)
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
public class DeletePermissionService implements DeletePermissionUseCase {

    private final PermissionDeleteCoordinator deleteCoordinator;

    public DeletePermissionService(PermissionDeleteCoordinator deleteCoordinator) {
        this.deleteCoordinator = deleteCoordinator;
    }

    @Override
    public void execute(DeletePermissionCommand command) {
        deleteCoordinator.execute(command);
    }
}
