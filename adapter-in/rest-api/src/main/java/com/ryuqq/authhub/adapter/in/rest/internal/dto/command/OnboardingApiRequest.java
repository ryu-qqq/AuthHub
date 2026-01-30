package com.ryuqq.authhub.adapter.in.rest.internal.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * OnboardingApiRequest - 온보딩 요청 API DTO
 *
 * <p>테넌트와 조직을 한 번에 생성하기 위한 요청입니다.
 *
 * @param tenantName 테넌트 이름 (필수)
 * @param organizationName 조직 이름 (필수)
 * @author development-team
 * @since 1.0.0
 */
@Schema(description = "온보딩 요청")
public record OnboardingApiRequest(
        @Schema(description = "테넌트 이름", example = "my-tenant") @NotBlank(message = "테넌트 이름은 필수입니다")
                String tenantName,
        @Schema(description = "조직 이름", example = "default-org") @NotBlank(message = "조직 이름은 필수입니다")
                String organizationName) {}
