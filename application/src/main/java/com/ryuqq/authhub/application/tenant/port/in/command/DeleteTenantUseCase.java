package com.ryuqq.authhub.application.tenant.port.in.command;

import com.ryuqq.authhub.application.tenant.dto.command.DeleteTenantCommand;

/**
 * DeleteTenantUseCase - 테넌트 삭제 UseCase (Port-In)
 *
 * <p>테넌트 삭제 기능을 정의합니다 (Soft Delete).
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code {Action}{Bc}UseCase} 네이밍
 *   <li>{@code execute()} 메서드 시그니처
 *   <li>Command DTO 파라미터, void 반환 (삭제 작업)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface DeleteTenantUseCase {

    /**
     * 테넌트 삭제 실행 (Soft Delete)
     *
     * @param command 테넌트 삭제 Command
     */
    void execute(DeleteTenantCommand command);
}
