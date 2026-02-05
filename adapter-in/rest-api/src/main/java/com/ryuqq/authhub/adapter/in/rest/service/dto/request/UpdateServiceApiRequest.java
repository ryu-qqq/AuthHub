package com.ryuqq.authhub.adapter.in.rest.service.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * UpdateServiceApiRequest - Service 수정 API Request
 *
 * <p>Service 수정 REST API 요청 DTO입니다.
 *
 * <p>ADTO-001: API Request DTO는 Record로 정의.
 *
 * <p>ADTO-002: *ApiRequest 네이밍.
 *
 * <p>ADTO-004: Update Request에 ID 포함 금지 -> PathVariable에서 전달.
 *
 * <p><strong>컨벤션:</strong> 수정 API에서 모든 필드는 NOT NULL (전체 업데이트 방식)
 *
 * @param name 서비스 이름 (필수)
 * @param description 서비스 설명 (필수, 빈 문자열 허용)
 * @param status 서비스 상태 (필수, ACTIVE 또는 INACTIVE)
 * @author development-team
 * @since 1.0.0
 */
@Schema(description = "Service 수정 요청 DTO")
public record UpdateServiceApiRequest(
        @Schema(description = "서비스 이름", example = "자사몰")
                @NotBlank(message = "name은 필수입니다")
                @Size(min = 2, max = 100, message = "name은 2자 이상 100자 이하여야 합니다")
                String name,
        @Schema(description = "서비스 설명", example = "자사몰 서비스")
                @NotNull(message = "description은 필수입니다")
                @Size(max = 500, message = "description은 500자 이하여야 합니다")
                String description,
        @Schema(description = "서비스 상태 (ACTIVE, INACTIVE)", example = "ACTIVE")
                @NotBlank(message = "status는 필수입니다")
                String status) {}
