package com.ryuqq.authhub.application.tenantservice.port.in.command;

import com.ryuqq.authhub.application.tenantservice.dto.command.SubscribeTenantServiceCommand;

/**
 * SubscribeTenantServiceUseCase - 테넌트 서비스 구독 UseCase (Port-In)
 *
 * <p>테넌트가 서비스에 구독하는 기능을 정의합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public interface SubscribeTenantServiceUseCase {

    /**
     * 테넌트 서비스 구독 실행
     *
     * @param command 구독 Command
     * @return 생성된 TenantService ID (Long)
     */
    Long execute(SubscribeTenantServiceCommand command);
}
