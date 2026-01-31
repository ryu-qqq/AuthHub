package com.ryuqq.authhub.adapter.in.rest.tenant.dto.request;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.time.LocalDate;
import java.util.List;

/**
 * SearchTenantsOffsetApiRequest - Tenant 복합 조건 조회 API Request (Offset 기반)
 *
 * <p>Tenant 복합 조건 조회 REST API 요청 DTO입니다.
 *
 * <p>ADTO-001: API Request DTO는 Record로 정의.
 *
 * <p>ADTO-002: *OffsetApiRequest 네이밍 (Offset 기반 조회).
 *
 * <p>ADTO-003: Validation 어노테이션은 API Request에만 적용.
 *
 * <p>기본값 처리는 Application Layer(CommonSearchParams)에서 수행합니다.
 *
 * @param searchWord 검색어 (선택)
 * @param searchField 검색 필드 (선택, 기본: NAME)
 * @param statuses 상태 필터 목록 - 다중 선택 가능 (선택)
 * @param startDate 조회 시작일 (선택, null이면 제한 없음)
 * @param endDate 조회 종료일 (선택, null이면 제한 없음)
 * @param page 페이지 번호 (null 허용 - Application에서 기본값 처리)
 * @param size 페이지 크기 (null 허용 - Application에서 기본값 처리)
 * @author development-team
 * @since 1.0.0
 */
@Schema(description = "Tenant 복합 조건 조회 요청 (Offset 기반)")
public record SearchTenantsOffsetApiRequest(
        @Parameter(description = "검색어", example = "테넌트")
                @Schema(description = "검색어", nullable = true)
                String searchWord,
        @Parameter(description = "검색 필드", example = "NAME")
                @Schema(
                        description = "검색 필드",
                        allowableValues = {"NAME"},
                        defaultValue = "NAME",
                        nullable = true)
                String searchField,
        @Parameter(description = "상태 필터 (복수 선택 가능)", example = "ACTIVE,INACTIVE")
                @Schema(
                        description = "상태 필터 목록",
                        allowableValues = {"ACTIVE", "INACTIVE", "DELETED"},
                        nullable = true)
                List<String> statuses,
        @Parameter(description = "조회 시작일", example = "2024-01-01")
                @Schema(description = "조회 시작일", nullable = true)
                LocalDate startDate,
        @Parameter(description = "조회 종료일", example = "2024-12-31")
                @Schema(description = "조회 종료일", nullable = true)
                LocalDate endDate,
        @Parameter(description = "페이지 번호", example = "0")
                @Schema(description = "페이지 번호", minimum = "0", defaultValue = "0", nullable = true)
                @Min(value = 0, message = "page는 0 이상이어야 합니다")
                Integer page,
        @Parameter(description = "페이지 크기", example = "20")
                @Schema(
                        description = "페이지 크기",
                        minimum = "1",
                        maximum = "100",
                        defaultValue = "20",
                        nullable = true)
                @Min(value = 1, message = "size는 1 이상이어야 합니다")
                @Max(value = 100, message = "size는 100 이하여야 합니다")
                Integer size) {}
