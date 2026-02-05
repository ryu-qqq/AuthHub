package com.ryuqq.authhub.adapter.in.rest.tenantservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * SubscribeTenantServiceApiRequest - 테넌트 서비스 구독 API Request
 *
 * <p>ADTO-001: API Request DTO는 Record로 정의.
 *
 * <p>ADTO-003: Validation 어노테이션은 API Request에만 적용.
 *
 * @param tenantId 테넌트 ID (필수)
 * @param serviceId 서비스 ID (필수)
 * @author development-team
 * @since 1.0.0
 */
@Schema(description = "테넌트 서비스 구독 요청 DTO")
public record SubscribeTenantServiceApiRequest(
        @Schema(description = "테넌트 ID", example = "550e8400-e29b-41d4-a716-446655440000")
                @NotBlank(message = "tenantId는 필수입니다")
                String tenantId,
        @Schema(description = "서비스 ID", example = "1") @NotNull(message = "serviceId는 필수입니다")
                Long serviceId) {}
