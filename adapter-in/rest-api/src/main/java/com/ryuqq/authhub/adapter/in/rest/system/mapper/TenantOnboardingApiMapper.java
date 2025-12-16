package com.ryuqq.authhub.adapter.in.rest.system.mapper;

import com.ryuqq.authhub.adapter.in.rest.system.dto.command.TenantOnboardingApiRequest;
import com.ryuqq.authhub.adapter.in.rest.system.dto.response.TenantOnboardingApiResponse;
import com.ryuqq.authhub.application.onboarding.dto.command.TenantOnboardingCommand;
import com.ryuqq.authhub.application.onboarding.dto.response.TenantOnboardingResponse;
import org.springframework.stereotype.Component;

/**
 * TenantOnboardingApiMapper - API ↔ Application 변환
 *
 * <p>REST API Layer와 Application Layer 간의 DTO 변환을 담당합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code @Component} 어노테이션
 *   <li>순수 변환만 수행
 *   <li>비즈니스 로직 금지
 *   <li>Lombok 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class TenantOnboardingApiMapper {

    /**
     * API Request → Application Command 변환
     *
     * @param request API 요청 DTO
     * @return Application Command DTO
     */
    public TenantOnboardingCommand toCommand(TenantOnboardingApiRequest request) {
        return TenantOnboardingCommand.of(
                request.tenantName(),
                request.masterEmail(),
                request.masterName(),
                request.defaultOrgName());
    }

    /**
     * Application Response → API Response 변환
     *
     * @param response Application 결과 DTO
     * @return API 응답 DTO
     */
    public TenantOnboardingApiResponse toApiResponse(TenantOnboardingResponse response) {
        return TenantOnboardingApiResponse.of(
                response.tenantId(),
                response.organizationId(),
                response.userId(),
                response.temporaryPassword());
    }
}
