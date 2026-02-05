package com.ryuqq.authhub.adapter.in.rest.internal.controller;

import com.ryuqq.authhub.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.authhub.adapter.in.rest.internal.InternalApiEndpoints;
import com.ryuqq.authhub.adapter.in.rest.internal.dto.command.OnboardingApiRequest;
import com.ryuqq.authhub.adapter.in.rest.internal.dto.response.OnboardingResultApiResponse;
import com.ryuqq.authhub.adapter.in.rest.internal.mapper.InternalOnboardingApiMapper;
import com.ryuqq.authhub.application.tenant.dto.command.OnboardingCommand;
import com.ryuqq.authhub.application.tenant.dto.response.OnboardingResult;
import com.ryuqq.authhub.application.tenant.port.in.command.OnboardingUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * InternalOnboardingController - Internal 온보딩 Controller
 *
 * <p>테넌트와 조직을 한 번에 생성하는 온보딩 API를 제공합니다.
 *
 * <p><strong>보안 참고:</strong>
 *
 * <ul>
 *   <li>서비스 토큰 인증으로 보호됩니다
 *   <li>내부 네트워크에서만 접근 가능해야 합니다
 * </ul>
 *
 * <p><strong>멱등키 지원:</strong>
 *
 * <ul>
 *   <li>X-Idempotency-Key 헤더로 멱등키 전송
 *   <li>동일한 멱등키로 요청 시 캐시된 응답 반환 (24시간 유지)
 *   <li>네트워크 오류 등으로 인한 재시도 시 중복 생성 방지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@RestController
@RequestMapping(InternalApiEndpoints.ONBOARDING)
@Tag(name = "Internal - Onboarding", description = "Internal Onboarding API")
public class InternalOnboardingController {

    private static final String IDEMPOTENCY_KEY_HEADER = "X-Idempotency-Key";

    private final OnboardingUseCase onboardingUseCase;
    private final InternalOnboardingApiMapper mapper;

    public InternalOnboardingController(
            OnboardingUseCase onboardingUseCase, InternalOnboardingApiMapper mapper) {
        this.onboardingUseCase = onboardingUseCase;
        this.mapper = mapper;
    }

    /**
     * 온보딩 (테넌트 + 조직 한 번에 생성)
     *
     * <p>테넌트와 조직을 하나의 트랜잭션으로 생성합니다.
     *
     * <p>X-Idempotency-Key 헤더를 통해 멱등성이 보장됩니다.
     *
     * @param idempotencyKey 멱등키 (필수, UUID 권장)
     * @param request 온보딩 요청 (tenantName, organizationName)
     * @return 생성된 tenantId, organizationId
     */
    @PostMapping
    @Operation(summary = "온보딩", description = "테넌트와 조직을 한 번에 생성합니다. X-Idempotency-Key 헤더는 필수입니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "201",
                description = "온보딩 성공")
    })
    public ResponseEntity<ApiResponse<OnboardingResultApiResponse>> onboarding(
            @Parameter(description = "멱등키 (필수, UUID 권장, 24시간 유효)")
                    @RequestHeader(value = IDEMPOTENCY_KEY_HEADER)
                    String idempotencyKey,
            @Valid @RequestBody OnboardingApiRequest request) {
        OnboardingCommand command = mapper.toCommand(request, idempotencyKey);
        OnboardingResult result = onboardingUseCase.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ofSuccess(mapper.toApiResponse(result)));
    }
}
