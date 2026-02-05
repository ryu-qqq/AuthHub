package com.ryuqq.authhub.adapter.in.rest.tenantservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * UpdateTenantServiceStatusApiRequest - 테넌트 서비스 상태 변경 API Request
 *
 * <p>ADTO-001: API Request DTO는 Record로 정의.
 *
 * <p>ADTO-003: Validation 어노테이션은 API Request에만 적용.
 *
 * @param status 변경할 상태 (ACTIVE, INACTIVE, SUSPENDED)
 * @author development-team
 * @since 1.0.0
 */
@Schema(description = "테넌트 서비스 상태 변경 요청 DTO")
public record UpdateTenantServiceStatusApiRequest(
        @Schema(description = "변경할 상태 (ACTIVE, INACTIVE, SUSPENDED)", example = "INACTIVE")
                @NotBlank(message = "status는 필수입니다")
                @Pattern(
                        regexp = "^(ACTIVE|INACTIVE|SUSPENDED)$",
                        message = "status는 ACTIVE, INACTIVE, SUSPENDED 중 하나여야 합니다")
                String status) {}
