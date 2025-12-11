package com.ryuqq.authhub.application.role.port.in.command;

import com.ryuqq.authhub.application.role.dto.command.RevokeRolePermissionCommand;

/**
 * RevokeRolePermissionUseCase - 역할에서 권한 해제 UseCase (Port-In)
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>Command 타입 파라미터 필수
 *   <li>단일 execute 메서드
 *   <li>Lombok 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface RevokeRolePermissionUseCase {

    /**
     * 역할에서 권한을 해제합니다.
     *
     * @param command 권한 해제 커맨드
     */
    void execute(RevokeRolePermissionCommand command);
}
