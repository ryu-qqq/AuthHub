package com.ryuqq.authhub.application.onboarding.port.in.command;

import com.ryuqq.authhub.application.onboarding.dto.command.TenantOnboardingCommand;
import com.ryuqq.authhub.application.onboarding.dto.response.TenantOnboardingResponse;

/**
 * TenantOnboardingUseCase - 테넌트 온보딩 Port-In
 *
 * <p>입점 승인 시 Tenant + Organization + User를 일괄 생성하는 UseCase입니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>*UseCase 네이밍 규칙
 *   <li>단일 메서드 (execute)
 *   <li>Command DTO 입력, Response DTO 출력
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface TenantOnboardingUseCase {

    /**
     * 테넌트 온보딩 실행
     *
     * <p>Tenant, Organization, User를 일괄 생성하고 ADMIN 역할을 부여합니다.
     *
     * @param command 온보딩 Command
     * @return 생성된 리소스 정보와 임시 비밀번호
     */
    TenantOnboardingResponse execute(TenantOnboardingCommand command);
}
