package com.ryuqq.authhub.application.permission.port.in.command;

import com.ryuqq.authhub.application.permission.dto.command.DeletePermissionCommand;

/**
 * DeletePermissionUseCase - 권한 삭제 UseCase (Port-In)
 *
 * <p>CUSTOM 권한의 삭제 기능을 정의합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code {Action}{Bc}UseCase} 네이밍
 *   <li>{@code execute()} 메서드 시그니처
 *   <li>Command DTO 파라미터, void 반환 (삭제)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface DeletePermissionUseCase {

    /**
     * 권한 삭제 실행
     *
     * @param command 권한 삭제 Command
     */
    void execute(DeletePermissionCommand command);
}
