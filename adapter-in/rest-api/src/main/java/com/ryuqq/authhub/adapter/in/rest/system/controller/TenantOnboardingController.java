package com.ryuqq.authhub.adapter.in.rest.system.controller;

import com.ryuqq.authhub.adapter.in.rest.auth.paths.ApiPaths;
import com.ryuqq.authhub.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.authhub.adapter.in.rest.system.dto.command.TenantOnboardingApiRequest;
import com.ryuqq.authhub.adapter.in.rest.system.dto.response.TenantOnboardingApiResponse;
import com.ryuqq.authhub.adapter.in.rest.system.mapper.TenantOnboardingApiMapper;
import com.ryuqq.authhub.application.onboarding.dto.response.TenantOnboardingResponse;
import com.ryuqq.authhub.application.onboarding.port.in.command.TenantOnboardingUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * TenantOnboardingController - 테넌트 온보딩 API 컨트롤러 (System)
 *
 * <p>Service Token 인증을 통한 서버 간 통신용 테넌트 온보딩 API입니다.
 *
 * <p><strong>API 경로:</strong> POST /api/v1/auth/system/tenants/onboarding
 *
 * <p><strong>인증:</strong> X-Service-Token 헤더 (UserContextFilter에서 처리)
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code @RestController} + {@code @RequestMapping} 필수
 *   <li>{@code @Valid} 필수
 *   <li>UseCase 단일 의존
 *   <li>Thin Controller (비즈니스 로직 금지)
 *   <li>Lombok 금지
 *   <li>{@code @Transactional} 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag(name = "System", description = "시스템 API (Service Token 인증)")
@RestController
@RequestMapping(ApiPaths.System.BASE)
public class TenantOnboardingController {

    private final TenantOnboardingUseCase tenantOnboardingUseCase;
    private final TenantOnboardingApiMapper mapper;

    public TenantOnboardingController(
            TenantOnboardingUseCase tenantOnboardingUseCase, TenantOnboardingApiMapper mapper) {
        this.tenantOnboardingUseCase = tenantOnboardingUseCase;
        this.mapper = mapper;
    }

    /**
     * 테넌트 온보딩 (Tenant + Organization + User 일괄 생성)
     *
     * <p>POST /api/v1/auth/system/tenants/onboarding
     *
     * <p>입점 승인 시 호출되어 테넌트, 기본 조직, 마스터 사용자를 일괄 생성합니다.
     *
     * @param request 온보딩 요청
     * @return 201 Created + 생성된 리소스 정보 및 임시 비밀번호
     */
    @Operation(
            summary = "테넌트 온보딩",
            description =
                    """
                    입점 승인 시 Tenant + Organization + User를 일괄 생성합니다.

                    **인증**: X-Service-Token 헤더 필수

                    **생성되는 리소스**:
                    - Tenant: 테넌트(회사)
                    - Organization: 기본 조직
                    - User: 마스터 관리자 (ADMIN 역할 자동 부여)

                    **응답 정보**:
                    - 생성된 각 리소스의 ID
                    - 임시 비밀번호 (호출자가 이메일로 발송해야 함)
                    """)
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "201",
                description = "온보딩 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "401",
                description = "Service Token 인증 실패"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "409",
                description = "중복된 이메일")
    })
    @PostMapping(ApiPaths.System.ONBOARDING)
    public ResponseEntity<ApiResponse<TenantOnboardingApiResponse>> onboard(
            @Valid @RequestBody TenantOnboardingApiRequest request) {
        TenantOnboardingResponse response =
                tenantOnboardingUseCase.execute(mapper.toCommand(request));
        TenantOnboardingApiResponse apiResponse = mapper.toApiResponse(response);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ofSuccess(apiResponse));
    }
}
