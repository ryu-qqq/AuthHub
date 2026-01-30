package com.ryuqq.authhub.adapter.in.rest.tenant.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * UpdateTenantStatusApiRequest - Tenant 상태 수정 API Request
 *
 * <p>Tenant 상태 수정 REST API 요청 DTO입니다.
 *
 * <p>ADTO-001: API Request DTO는 Record로 정의.
 *
 * <p>ADTO-002: *ApiRequest 네이밍.
 *
 * <p>ADTO-003: Validation 어노테이션은 API Request에만 적용.
 *
 * <p>ADTO-004: Update Request에 ID 포함 금지 -> PathVariable에서 전달.
 *
 * @param status 변경할 테넌트 상태 (ACTIVE, INACTIVE)
 * @author development-team
 * @since 1.0.0
 */
@Schema(description = "Tenant 상태 수정 요청 DTO")
public record UpdateTenantStatusApiRequest(
        @Schema(
                        description = "변경할 상태",
                        example = "ACTIVE",
                        allowableValues = {"ACTIVE", "INACTIVE"})
                @NotBlank(message = "status는 필수입니다")
                @Pattern(regexp = "ACTIVE|INACTIVE", message = "status는 ACTIVE 또는 INACTIVE만 가능합니다")
                String status) {}
