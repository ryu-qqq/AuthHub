package com.ryuqq.authhub.adapter.in.rest.internal.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * OnboardingResultApiResponse - 온보딩 결과 API 응답 DTO
 *
 * @param tenantId 생성된 테넌트 ID (UUIDv7)
 * @param organizationId 생성된 조직 ID (UUIDv7)
 * @author development-team
 * @since 1.0.0
 */
@Schema(description = "온보딩 결과")
public record OnboardingResultApiResponse(
        @Schema(description = "생성된 테넌트 ID", example = "01933abc-1234-7000-8000-000000000001")
                String tenantId,
        @Schema(description = "생성된 조직 ID", example = "01933abc-1234-7000-8000-000000000002")
                String organizationId) {}
