package com.ryuqq.authhub.application.tenant.port.in.command;

import com.ryuqq.authhub.application.tenant.dto.command.OnboardingCommand;
import com.ryuqq.authhub.application.tenant.dto.response.OnboardingResult;

/**
 * OnboardingUseCase - 온보딩 UseCase (Port-In)
 *
 * <p>테넌트와 조직을 한 번에 생성하는 온보딩 기능을 정의합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code {Action}{Bc}UseCase} 네이밍
 *   <li>{@code execute()} 메서드 시그니처
 *   <li>Command DTO 파라미터, Result DTO 반환
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface OnboardingUseCase {

    /**
     * 온보딩 실행 (테넌트 + 조직 한 번에 생성)
     *
     * @param command 온보딩 Command (tenantName, organizationName)
     * @return OnboardingResult (tenantId, organizationId)
     */
    OnboardingResult execute(OnboardingCommand command);
}
