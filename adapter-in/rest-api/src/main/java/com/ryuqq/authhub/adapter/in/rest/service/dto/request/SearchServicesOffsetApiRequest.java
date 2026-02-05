package com.ryuqq.authhub.adapter.in.rest.service.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.time.LocalDate;
import java.util.List;

/**
 * SearchServicesOffsetApiRequest - Service 목록 조회 API Request (Offset 기반)
 *
 * <p>Service 목록 조회 REST API 요청 DTO입니다.
 *
 * <p>ADTO-001: API Request DTO는 Record로 정의.
 *
 * <p>ADTO-002: *ApiRequest 네이밍.
 *
 * @param searchWord 검색어
 * @param searchField 검색 필드 (SERVICE_CODE, NAME)
 * @param statuses 상태 필터 (ACTIVE, INACTIVE)
 * @param startDate 조회 시작일
 * @param endDate 조회 종료일
 * @param page 페이지 번호 (0부터 시작)
 * @param size 페이지 크기
 * @author development-team
 * @since 1.0.0
 */
@Schema(description = "Service 목록 조회 요청 DTO (Offset 기반)")
public record SearchServicesOffsetApiRequest(
        @Schema(description = "검색어", example = "STORE") String searchWord,
        @Schema(description = "검색 필드", example = "NAME") String searchField,
        @Schema(description = "상태 필터", example = "[\"ACTIVE\", \"INACTIVE\"]")
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
