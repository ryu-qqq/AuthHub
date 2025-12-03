package com.ryuqq.authhub.application.tenant.port.in;

import com.ryuqq.authhub.application.tenant.dto.command.CreateTenantCommand;
import com.ryuqq.authhub.application.tenant.dto.response.CreateTenantResponse;

/**
 * CreateTenantUseCase - 테넌트 생성 유스케이스
 *
 * <p>새로운 테넌트를 생성합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public interface CreateTenantUseCase {

    /**
     * 테넌트 생성
     *
     * @param command 테넌트 생성 커맨드
     * @return 생성된 테넌트 ID를 포함한 응답
     */
    CreateTenantResponse execute(CreateTenantCommand command);
}
