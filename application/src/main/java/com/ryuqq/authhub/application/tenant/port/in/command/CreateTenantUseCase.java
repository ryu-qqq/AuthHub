package com.ryuqq.authhub.application.tenant.port.in.command;

import com.ryuqq.authhub.application.tenant.dto.command.CreateTenantCommand;
import com.ryuqq.authhub.application.tenant.dto.response.TenantResponse;

/**
 * CreateTenantUseCase - 테넌트 생성 UseCase (Port-In)
 *
 * <p>테넌트 생성 기능을 정의합니다.
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
public interface CreateTenantUseCase {

    /**
     * 테넌트 생성 실행
     *
     * @param command 테넌트 생성 Command
     * @return 생성된 테넌트 Response
     */
    TenantResponse execute(CreateTenantCommand command);
}
