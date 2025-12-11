package com.ryuqq.authhub.application.role.port.in.command;

import com.ryuqq.authhub.application.role.dto.command.GrantRolePermissionCommand;
import com.ryuqq.authhub.application.role.dto.response.RolePermissionResponse;

/**
 * GrantRolePermissionUseCase - 역할에 권한 부여 UseCase (Port-In)
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
public interface GrantRolePermissionUseCase {

    /**
     * 역할에 권한을 부여합니다.
     *
     * @param command 권한 부여 커맨드
     * @return 부여된 역할 권한 정보
     */
    RolePermissionResponse execute(GrantRolePermissionCommand command);
}
