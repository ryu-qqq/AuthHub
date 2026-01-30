package com.ryuqq.authhub.application.role.port.in.command;

import com.ryuqq.authhub.application.role.dto.command.DeleteRoleCommand;

/**
 * DeleteRoleUseCase - 역할 삭제 UseCase (Port-In)
 *
 * <p>역할 삭제 기능을 정의합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code {Action}{Bc}UseCase} 네이밍
 *   <li>{@code execute()} 메서드 시그니처
 *   <li>Command DTO 파라미터
 *   <li>void 반환 (삭제 후 ID 반환 불필요)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface DeleteRoleUseCase {

    /**
     * 역할 삭제 실행
     *
     * @param command 역할 삭제 Command
     */
    void execute(DeleteRoleCommand command);
}
