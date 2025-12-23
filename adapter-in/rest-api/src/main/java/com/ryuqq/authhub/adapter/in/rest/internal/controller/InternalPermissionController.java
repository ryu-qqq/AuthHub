package com.ryuqq.authhub.adapter.in.rest.internal.controller;

import com.ryuqq.authhub.adapter.in.rest.auth.paths.ApiPaths;
import com.ryuqq.authhub.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.authhub.adapter.in.rest.internal.dto.command.RegisterPermissionUsageApiRequest;
import com.ryuqq.authhub.adapter.in.rest.internal.dto.command.ValidatePermissionsApiRequest;
import com.ryuqq.authhub.adapter.in.rest.internal.dto.response.PermissionUsageApiResponse;
import com.ryuqq.authhub.adapter.in.rest.internal.dto.response.ValidatePermissionsApiResponse;
import com.ryuqq.authhub.adapter.in.rest.internal.mapper.InternalApiMapper;
import com.ryuqq.authhub.application.permission.dto.command.RegisterPermissionUsageCommand;
import com.ryuqq.authhub.application.permission.dto.command.ValidatePermissionsCommand;
import com.ryuqq.authhub.application.permission.dto.response.PermissionUsageResponse;
import com.ryuqq.authhub.application.permission.dto.response.ValidatePermissionsResult;
import com.ryuqq.authhub.application.permission.port.in.command.RegisterPermissionUsageUseCase;
import com.ryuqq.authhub.application.permission.port.in.command.ValidatePermissionsUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * InternalPermissionController - Internal 권한 API 컨트롤러
 *
 * <p>CI/CD 파이프라인에서 권한 검증을 위한 Internal API입니다.
 *
 * <p><strong>API 경로:</strong> /api/v1/auth/internal/permissions
 *
 * <p><strong>인증:</strong> X-Service-Token 헤더 (서비스간 통신)
 *
 * <p><strong>사용 시나리오:</strong>
 *
 * <ol>
 *   <li>CI/CD에서 PermissionScanner 실행 → permissions.json 생성
 *   <li>curl로 AuthHub /validate API 호출
 *   <li>누락된 권한이 있으면 배포 실패 또는 Slack 알림
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
@Tag(name = "Internal - Permission", description = "권한 검증 Internal API (CI/CD용)")
@RestController
@RequestMapping(ApiPaths.Internal.BASE + ApiPaths.Internal.PERMISSIONS)
public class InternalPermissionController {

    private final ValidatePermissionsUseCase validatePermissionsUseCase;
    private final RegisterPermissionUsageUseCase registerPermissionUsageUseCase;
    private final InternalApiMapper mapper;

    public InternalPermissionController(
            ValidatePermissionsUseCase validatePermissionsUseCase,
            RegisterPermissionUsageUseCase registerPermissionUsageUseCase,
            InternalApiMapper mapper) {
        this.validatePermissionsUseCase = validatePermissionsUseCase;
        this.registerPermissionUsageUseCase = registerPermissionUsageUseCase;
        this.mapper = mapper;
    }

    /**
     * 권한 검증 (CI/CD용)
     *
     * <p>POST /api/v1/auth/internal/permissions/validate
     *
     * <p>서비스의 @PreAuthorize 권한들이 AuthHub에 등록되어 있는지 검증합니다.
     *
     * @param request 권한 검증 요청 (serviceName, permissions)
     * @return 검증 결과 (valid, missing, existing)
     */
    @Operation(
            summary = "권한 검증",
            description =
                    """
CI/CD 파이프라인에서 서비스의 @PreAuthorize 권한들이 AuthHub에 등록되어 있는지 검증합니다.

**인증**: X-Service-Token 헤더 필수

**사용 예시** (CI/CD):
```bash
curl -X POST https://auth.example.com/api/v1/auth/internal/permissions/validate \\
     -H "X-Service-Token: your-service-token" \\
     -H "Content-Type: application/json" \\
     -d @build/permissions/permissions.json
```

**응답 해석**:
- `valid: true` → 모든 권한이 등록되어 있음 (배포 진행 가능)
- `valid: false` → 누락된 권한 있음 (missing 배열 확인)
""")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "검증 완료 (성공 여부는 valid 필드 확인)"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청 (필수 필드 누락)"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "401",
                description = "인증 실패 (Service Token 오류)")
    })
    @PreAuthorize("hasRole('SERVICE')")
    @PostMapping(ApiPaths.Internal.VALIDATE)
    public ResponseEntity<ApiResponse<ValidatePermissionsApiResponse>> validatePermissions(
            @Valid @RequestBody ValidatePermissionsApiRequest request) {

        ValidatePermissionsCommand command = mapper.toCommand(request);
        ValidatePermissionsResult result = validatePermissionsUseCase.execute(command);
        ValidatePermissionsApiResponse apiResponse = mapper.toApiResponse(result);

        return ResponseEntity.ok(ApiResponse.ofSuccess(apiResponse));
    }

    /**
     * 권한 사용 이력 등록 (n8n 승인 후 호출)
     *
     * <p>POST /api/v1/auth/internal/permissions/{permissionKey}/usages
     *
     * <p>n8n에서 승인 후 권한 사용 이력을 등록합니다. 동일 권한+서비스 조합이 이미 존재하면 업데이트 (UPSERT)합니다.
     *
     * @param permissionKey 권한 키 (Path Variable)
     * @param request 사용 이력 등록 요청 (serviceName, locations)
     * @return 등록된 사용 이력
     */
    @Operation(
            summary = "권한 사용 이력 등록",
            description =
                    """
n8n에서 승인 후 권한 사용 이력을 등록합니다.

**인증**: X-Service-Token 헤더 필수

**사용 예시** (n8n):
```bash
curl -X POST https://auth.example.com/api/v1/auth/internal/permissions/product:read/usages \\
     -H "X-Service-Token: your-service-token" \\
     -H "Content-Type: application/json" \\
     -d '{"serviceName": "product-service", "locations": ["ProductController.java:45"]}'
```

**UPSERT 동작**:
- 동일 권한+서비스 조합이 없으면 → 신규 생성
- 동일 권한+서비스 조합이 있으면 → 위치 및 스캔 시간 업데이트
""")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "등록/업데이트 완료"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청 (필수 필드 누락)"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "401",
                description = "인증 실패 (Service Token 오류)")
    })
    @PreAuthorize("hasRole('SERVICE')")
    @PostMapping("/{permissionKey}" + ApiPaths.Internal.USAGES)
    public ResponseEntity<ApiResponse<PermissionUsageApiResponse>> registerUsage(
            @Parameter(description = "권한 키", example = "product:read") @PathVariable
                    String permissionKey,
            @Valid @RequestBody RegisterPermissionUsageApiRequest request) {

        RegisterPermissionUsageCommand command = mapper.toCommand(permissionKey, request);
        PermissionUsageResponse result = registerPermissionUsageUseCase.execute(command);
        PermissionUsageApiResponse apiResponse = mapper.toApiResponse(result);

        return ResponseEntity.ok(ApiResponse.ofSuccess(apiResponse));
    }
}
