package com.ryuqq.authhub.adapter.in.rest.system.controller;

import com.ryuqq.authhub.adapter.in.rest.auth.paths.ApiPaths;
import com.ryuqq.authhub.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.authhub.adapter.in.rest.system.dto.command.TenantOnboardingApiRequest;
import com.ryuqq.authhub.adapter.in.rest.system.dto.response.TenantOnboardingApiResponse;
import com.ryuqq.authhub.adapter.in.rest.system.mapper.SystemApiMapper;
import com.ryuqq.authhub.application.onboarding.dto.command.TenantOnboardingCommand;
import com.ryuqq.authhub.application.onboarding.dto.response.TenantOnboardingResponse;
import com.ryuqq.authhub.application.onboarding.port.in.TenantOnboardingUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * SystemTenantController - 시스템 테넌트 API 컨트롤러
 *
 * <p>입점 승인 시 Tenant, Organization, User를 일괄 생성하는 System API입니다.
 *
 * <p><strong>API 경로:</strong> /api/v1/auth/system/tenants
 *
 * <p><strong>인증:</strong> X-Service-Token 헤더 (서비스간 통신)
 *
 * <p><strong>사용 시나리오:</strong>
 *
 * <ol>
 *   <li>입점 서비스에서 입점 승인
 *   <li>AuthHub /system/tenants/onboarding API 호출
 *   <li>Tenant + Organization + User 일괄 생성
 *   <li>임시 비밀번호 반환 (입점 서비스가 이메일 발송)
 * </ol>
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code @RestController} + {@code @RequestMapping} 필수
 *   <li>UseCase 단일 의존
 *   <li>Thin Controller (비즈니스 로직 금지)
 *   <li>Lombok 금지
 *   <li>{@code @Transactional} 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag(name = "System - Tenant", description = "테넌트 온보딩 System API (입점 서비스용)")
@RestController
@RequestMapping(ApiPaths.System.BASE + ApiPaths.System.TENANTS)
public class SystemTenantController {

    private final TenantOnboardingUseCase tenantOnboardingUseCase;
    private final SystemApiMapper mapper;

    public SystemTenantController(
            TenantOnboardingUseCase tenantOnboardingUseCase, SystemApiMapper mapper) {
        this.tenantOnboardingUseCase = tenantOnboardingUseCase;
        this.mapper = mapper;
    }

    /**
     * 테넌트 온보딩 (입점 승인)
     *
     * <p>POST /api/v1/auth/system/tenants/onboarding
     *
     * <p>입점 승인 시 Tenant, Organization, User를 일괄 생성합니다.
     *
     * @param request 온보딩 요청 (tenantName, organizationName, masterEmail)
     * @return 생성된 리소스 정보 (tenantId, organizationId, userId, temporaryPassword)
     */
    @Operation(
            summary = "테넌트 온보딩",
            description =
                    """
입점 승인 시 Tenant, Organization, User를 일괄 생성합니다.

**인증**: X-Service-Token 헤더 필수

**처리 순서**:
1. Tenant 생성 (ACTIVE 상태)
2. 기본 Organization 생성
3. 임시 비밀번호 생성
4. 마스터 User 생성
5. TENANT_ADMIN 역할 할당

**사용 예시** (입점 서비스):
```bash
curl -X POST https://auth.example.com/api/v1/auth/system/tenants/onboarding \\
     -H "X-Service-Token: your-service-token" \\
     -H "Content-Type: application/json" \\
     -d '{
       "tenantName": "커넥틀리",
       "organizationName": "본사",
       "masterEmail": "admin@connectly.com"
     }'
```

**호출자 책임**:
- 반환된 temporaryPassword를 마스터 사용자에게 이메일로 발송
- 첫 로그인 시 비밀번호 변경 안내
""")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "201",
                description = "온보딩 완료"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청 (필수 필드 누락, 유효성 검증 실패)"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "401",
                description = "인증 실패 (Service Token 오류)"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "409",
                description = "충돌 (이미 존재하는 테넌트명 또는 이메일)")
    })
    @PreAuthorize("hasRole('SERVICE')")
    @PostMapping(ApiPaths.System.ONBOARDING)
    public ResponseEntity<ApiResponse<TenantOnboardingApiResponse>> onboarding(
            @Valid @RequestBody TenantOnboardingApiRequest request) {

        TenantOnboardingCommand command = mapper.toCommand(request);
        TenantOnboardingResponse response = tenantOnboardingUseCase.execute(command);
        TenantOnboardingApiResponse apiResponse = mapper.toApiResponse(response);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ofSuccess(apiResponse));
    }
}
