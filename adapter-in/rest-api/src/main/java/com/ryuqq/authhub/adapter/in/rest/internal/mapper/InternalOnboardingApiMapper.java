package com.ryuqq.authhub.adapter.in.rest.internal.mapper;

import com.ryuqq.authhub.adapter.in.rest.internal.dto.command.OnboardingApiRequest;
import com.ryuqq.authhub.adapter.in.rest.internal.dto.response.OnboardingResultApiResponse;
import com.ryuqq.authhub.application.tenant.dto.command.OnboardingCommand;
import com.ryuqq.authhub.application.tenant.dto.response.OnboardingResult;
import org.springframework.stereotype.Component;

/**
 * InternalOnboardingApiMapper - Internal 온보딩 API 매퍼
 *
 * <p>REST API 계층과 Application 계층 간의 DTO 변환을 담당합니다.
 *
 * <p>MAPPER-001: Mapper는 @Component로 등록.
 *
 * <p>MAPPER-003: Application Response -> API Response 변환.
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class InternalOnboardingApiMapper {

    /**
     * API 요청 → Application Command 변환
     *
     * @param request API 요청 DTO
     * @param idempotencyKey 멱등키 (필수, X-Idempotency-Key 헤더)
     * @return Application Command
     */
    public OnboardingCommand toCommand(OnboardingApiRequest request, String idempotencyKey) {
        return new OnboardingCommand(
                request.tenantName(), request.organizationName(), idempotencyKey);
    }

    /**
     * Application Result → API 응답 변환
     *
     * @param result Application 결과 DTO
     * @return API 응답 DTO
     */
    public OnboardingResultApiResponse toApiResponse(OnboardingResult result) {
        return new OnboardingResultApiResponse(result.tenantId(), result.organizationId());
    }
}
