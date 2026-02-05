package com.ryuqq.authhub.adapter.in.rest.service.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * CreateServiceApiRequest - Service 생성 API Request
 *
 * <p>Service 생성 REST API 요청 DTO입니다.
 *
 * <p>ADTO-001: API Request DTO는 Record로 정의.
 *
 * <p>ADTO-002: *ApiRequest 네이밍.
 *
 * <p>ADTO-003: Validation 어노테이션은 API Request에만 적용.
 *
 * @param serviceCode 서비스 코드 (UPPER_SNAKE_CASE, 예: SVC_STORE)
 * @param name 서비스 이름
 * @param description 서비스 설명
 * @author development-team
 * @since 1.0.0
 */
@Schema(description = "Service 생성 요청 DTO")
public record CreateServiceApiRequest(
        @Schema(description = "서비스 코드 (UPPER_SNAKE_CASE)", example = "SVC_STORE")
                @NotBlank(message = "serviceCode는 필수입니다")
                @Size(min = 2, max = 50, message = "serviceCode는 2자 이상 50자 이하여야 합니다")
                @Pattern(
                        regexp = "^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$",
                        message = "serviceCode는 UPPER_SNAKE_CASE 형식이어야 합니다")
                String serviceCode,
        @Schema(description = "서비스 이름", example = "자사몰")
                @NotBlank(message = "name은 필수입니다")
                @Size(min = 2, max = 100, message = "name은 2자 이상 100자 이하여야 합니다")
                String name,
        @Schema(description = "서비스 설명", example = "자사몰 서비스")
                @Size(max = 500, message = "description은 500자 이하여야 합니다")
                String description) {}
