package com.ryuqq.authhub.application.tenantservice.port.in.command;

import com.ryuqq.authhub.application.tenantservice.dto.command.UpdateTenantServiceStatusCommand;

/**
 * UpdateTenantServiceStatusUseCase - 테넌트 서비스 구독 상태 변경 UseCase (Port-In)
 *
 * <p>테넌트의 서비스 구독 상태를 변경하는 기능을 정의합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public interface UpdateTenantServiceStatusUseCase {

    /**
     * 테넌트 서비스 구독 상태 변경 실행
     *
     * @param command 상태 변경 Command
     */
    void execute(UpdateTenantServiceStatusCommand command);
}
