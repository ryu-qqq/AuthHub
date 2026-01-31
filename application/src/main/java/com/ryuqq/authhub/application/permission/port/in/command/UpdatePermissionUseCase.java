package com.ryuqq.authhub.application.permission.port.in.command;

import com.ryuqq.authhub.application.permission.dto.command.UpdatePermissionCommand;

/**
 * UpdatePermissionUseCase - 권한 수정 UseCase (Port-In)
 *
 * <p>권한 수정 기능을 정의합니다. CUSTOM 권한만 수정 가능합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code {Action}{Bc}UseCase} 네이밍
 *   <li>{@code execute()} 메서드 시그니처
 *   <li>Command DTO 파라미터, void 반환
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface UpdatePermissionUseCase {

    /**
     * 권한 수정 실행
     *
     * @param command 권한 수정 Command
     */
    void execute(UpdatePermissionCommand command);
}
