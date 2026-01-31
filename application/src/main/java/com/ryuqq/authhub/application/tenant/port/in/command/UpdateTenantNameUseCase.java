package com.ryuqq.authhub.application.tenant.port.in.command;

import com.ryuqq.authhub.application.tenant.dto.command.UpdateTenantNameCommand;

/**
 * UpdateTenantNameUseCase - 테넌트 이름 변경 UseCase (Port-In)
 *
 * <p>테넌트 이름 변경 기능을 정의합니다.
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
public interface UpdateTenantNameUseCase {

    /**
     * 테넌트 이름 변경 실행
     *
     * @param command 테넌트 이름 변경 Command
     */
    void execute(UpdateTenantNameCommand command);
}
