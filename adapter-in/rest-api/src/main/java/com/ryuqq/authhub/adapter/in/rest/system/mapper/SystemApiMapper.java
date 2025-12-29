package com.ryuqq.authhub.adapter.in.rest.system.mapper;

import com.ryuqq.authhub.adapter.in.rest.system.dto.command.TenantOnboardingApiRequest;
import com.ryuqq.authhub.adapter.in.rest.system.dto.response.TenantOnboardingApiResponse;
import com.ryuqq.authhub.application.onboarding.dto.command.TenantOnboardingCommand;
import com.ryuqq.authhub.application.onboarding.dto.response.TenantOnboardingResponse;
import org.springframework.stereotype.Component;

/**
 * SystemApiMapper - System API DTO 변환기
 *
 * <p>System REST API DTO와 Application DTO 간의 변환을 담당합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code @Component} 어노테이션 필수
 *   <li>Lombok 금지
 *   <li>비즈니스 로직 금지
 *   <li>단순 변환만 수행
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class SystemApiMapper {

    /**
     * TenantOnboardingApiRequest → TenantOnboardingCommand 변환
     *
     * @param request API 요청 DTO
     * @return Application Command DTO
     */
    public TenantOnboardingCommand toCommand(TenantOnboardingApiRequest request) {
        return new TenantOnboardingCommand(
                request.tenantName(),
                request.organizationName(),
                request.masterEmail(),
                request.masterPhoneNumber());
    }

    /**
     * TenantOnboardingResponse → TenantOnboardingApiResponse 변환
     *
     * @param response Application Response DTO
     * @return API 응답 DTO
     */
    public TenantOnboardingApiResponse toApiResponse(TenantOnboardingResponse response) {
        return new TenantOnboardingApiResponse(
                response.tenantId().toString(),
                response.organizationId().toString(),
                response.userId().toString(),
                response.temporaryPassword());
    }
}
