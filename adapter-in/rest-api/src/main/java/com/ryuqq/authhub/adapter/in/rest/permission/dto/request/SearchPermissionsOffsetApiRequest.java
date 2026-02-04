package com.ryuqq.authhub.adapter.in.rest.permission.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.time.LocalDate;
import java.util.List;

/**
 * SearchPermissionsOffsetApiRequest - Permission 목록 조회 API Request (Offset 기반, Global Only)
 *
 * <p>Permission 목록 조회 REST API 요청 DTO입니다.
 *
 * <p><strong>Global Only 설계:</strong>
 *
 * <ul>
 *   <li>모든 Permission은 전체 시스템에서 공유됩니다
 *   <li>테넌트 관련 필드가 제거되었습니다
 * </ul>
 *
 * <p>ADTO-001: API Request DTO는 Record로 정의.
 *
 * <p>ADTO-002: *ApiRequest 네이밍.
 *
 * @param serviceId 서비스 ID 필터 (null이면 전체)
 * @param searchWord 검색어
 * @param searchField 검색 필드 (PERMISSION_KEY, RESOURCE, ACTION, DESCRIPTION)
 * @param types 권한 유형 필터 (SYSTEM, CUSTOM)
 * @param resources 리소스 필터
 * @param startDate 조회 시작일
 * @param endDate 조회 종료일
 * @param page 페이지 번호 (0부터 시작)
 * @param size 페이지 크기
 * @author development-team
 * @since 1.0.0
 */
@Schema(description = "Permission 목록 조회 요청 DTO (Offset 기반)")
public record SearchPermissionsOffsetApiRequest(
        @Schema(description = "서비스 ID 필터", example = "1") Long serviceId,
        @Schema(description = "검색어", example = "user") String searchWord,
        @Schema(description = "검색 필드", example = "RESOURCE") String searchField,
        @Schema(description = "권한 유형 필터", example = "[\"SYSTEM\", \"CUSTOM\"]") List<String> types,
        @Schema(description = "리소스 필터", example = "[\"user\", \"role\"]") List<String> resources,
        @Schema(description = "조회 시작일", example = "2024-01-01") LocalDate startDate,
        @Schema(description = "조회 종료일", example = "2024-12-31") LocalDate endDate,
        @Schema(description = "페이지 번호 (0부터 시작)", example = "0", defaultValue = "0")
                @Min(value = 0, message = "page는 0 이상이어야 합니다")
                Integer page,
        @Schema(description = "페이지 크기", example = "20", defaultValue = "20")
                @Min(value = 1, message = "size는 1 이상이어야 합니다")
                @Max(value = 100, message = "size는 100 이하여야 합니다")
                Integer size) {}
