package com.ryuqq.authhub.adapter.in.rest.tenant.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * CreateTenantApiRequest - Tenant 생성 API Request
 *
 * <p>Tenant 생성 REST API 요청 DTO입니다.
 *
 * <p>ADTO-001: API Request DTO는 Record로 정의.
 *
 * <p>ADTO-002: *ApiRequest 네이밍.
 *
 * <p>ADTO-003: Validation 어노테이션은 API Request에만 적용.
 *
 * @param name 테넌트 이름
 * @author development-team
 * @since 1.0.0
 */
@Schema(description = "Tenant 생성 요청 DTO")
public record CreateTenantApiRequest(
        @Schema(description = "테넌트 이름", example = "테넌트A")
                @NotBlank(message = "name은 필수입니다")
                @Size(min = 2, max = 100, message = "name은 2자 이상 100자 이하여야 합니다")
                String name) {}
