package com.ryuqq.authhub.adapter.in.rest.tenantservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.time.LocalDate;
import java.util.List;

/**
 * SearchTenantServicesOffsetApiRequest - 테넌트-서비스 구독 목록 조회 API Request (Offset 기반)
 *
 * <p>ADTO-001: API Request DTO는 Record로 정의.
 *
 * @param tenantId 테넌트 ID 필터
 * @param serviceId 서비스 ID 필터
 * @param statuses 상태 필터 (ACTIVE, INACTIVE, SUSPENDED)
 * @param startDate 조회 시작일
 * @param endDate 조회 종료일
 * @param page 페이지 번호 (0부터 시작)
 * @param size 페이지 크기
 * @author development-team
 * @since 1.0.0
 */
@Schema(description = "테넌트-서비스 구독 목록 조회 요청 DTO (Offset 기반)")
public record SearchTenantServicesOffsetApiRequest(
        @Schema(description = "테넌트 ID 필터", example = "550e8400-e29b-41d4-a716-446655440000")
                String tenantId,
        @Schema(description = "서비스 ID 필터", example = "1") Long serviceId,
        @Schema(description = "상태 필터", example = "[\"ACTIVE\", \"INACTIVE\", \"SUSPENDED\"]")
                List<String> statuses,
        @Schema(description = "조회 시작일", example = "2024-01-01") LocalDate startDate,
        @Schema(description = "조회 종료일", example = "2024-12-31") LocalDate endDate,
        @Schema(description = "페이지 번호 (0부터 시작)", example = "0", defaultValue = "0")
                @Min(value = 0, message = "page는 0 이상이어야 합니다")
                Integer page,
        @Schema(description = "페이지 크기", example = "20", defaultValue = "20")
                @Min(value = 1, message = "size는 1 이상이어야 합니다")
                @Max(value = 100, message = "size는 100 이하여야 합니다")
                Integer size) {}
