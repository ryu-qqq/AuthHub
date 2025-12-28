package com.ryuqq.authhub.application.onboarding.port.in;

import com.ryuqq.authhub.application.onboarding.dto.command.TenantOnboardingCommand;
import com.ryuqq.authhub.application.onboarding.dto.response.TenantOnboardingResponse;

/**
 * TenantOnboardingUseCase - 테넌트 온보딩 UseCase (Port-In)
 *
 * <p>입점 승인 시 Tenant, Organization, User를 일괄 생성합니다.
 *
 * <p><strong>사용 시나리오:</strong>
 *
 * <ol>
 *   <li>입점 서비스에서 입점 승인
 *   <li>AuthHub /system/tenants/onboarding API 호출
 *   <li>Tenant 생성 (ACTIVE 상태)
 *   <li>기본 Organization 생성
 *   <li>마스터 User 생성 (ADMIN 역할 부여)
 *   <li>임시 비밀번호 생성 및 반환
 * </ol>
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
public interface TenantOnboardingUseCase {

    /**
     * 테넌트 온보딩 실행
     *
     * @param command 온보딩 Command (테넌트명, 조직명, 마스터 이메일)
     * @return 생성된 리소스 정보 (테넌트ID, 조직ID, 사용자ID, 임시비밀번호)
     */
    TenantOnboardingResponse execute(TenantOnboardingCommand command);
}
