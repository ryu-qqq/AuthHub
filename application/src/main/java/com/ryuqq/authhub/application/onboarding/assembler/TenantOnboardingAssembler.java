package com.ryuqq.authhub.application.onboarding.assembler;

import com.ryuqq.authhub.application.onboarding.dto.bundle.TenantOnboardingPersistBundle;
import com.ryuqq.authhub.application.onboarding.dto.response.TenantOnboardingResponse;
import org.springframework.stereotype.Component;

/**
 * TenantOnboardingAssembler - 테넌트 온보딩 Assembler
 *
 * <p>영속화된 Bundle을 Response DTO로 변환합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code @Component} 어노테이션
 *   <li>{@code toResponse()} 메서드 네이밍
 *   <li>순수 변환만 수행 (비즈니스 로직 금지)
 *   <li>Lombok 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class TenantOnboardingAssembler {

    /**
     * Bundle → Response 변환
     *
     * <p>영속화 완료된 Bundle에서 Response DTO를 생성합니다.
     *
     * @param bundle 영속화 완료된 Bundle (모든 ID 할당됨)
     * @return 응답 DTO
     */
    public TenantOnboardingResponse toResponse(TenantOnboardingPersistBundle bundle) {
        return new TenantOnboardingResponse(
                bundle.tenant().tenantIdValue(),
                bundle.organization().organizationIdValue(),
                bundle.user().userIdValue(),
                bundle.temporaryPassword());
    }
}
