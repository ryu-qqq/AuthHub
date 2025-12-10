package com.ryuqq.authhub.application.tenant.port.in.command;

import com.ryuqq.authhub.application.tenant.dto.command.UpdateTenantStatusCommand;
import com.ryuqq.authhub.application.tenant.dto.response.TenantResponse;

/**
 * UpdateTenantStatusUseCase - 테넌트 상태 변경 UseCase (Port-In)
 *
 * <p>테넌트 상태 변경 기능을 정의합니다 (활성화/비활성화).
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code {Action}{Bc}UseCase} 네이밍
 *   <li>{@code execute()} 메서드 시그니처
 *   <li>Command DTO 파라미터, Response DTO 반환
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface UpdateTenantStatusUseCase {

    /**
     * 테넌트 상태 변경 실행
     *
     * @param command 테넌트 상태 변경 Command
     * @return 변경된 테넌트 Response
     */
    TenantResponse execute(UpdateTenantStatusCommand command);
}
