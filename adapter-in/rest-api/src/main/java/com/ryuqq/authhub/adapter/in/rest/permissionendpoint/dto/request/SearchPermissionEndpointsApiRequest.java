package com.ryuqq.authhub.adapter.in.rest.permissionendpoint.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.time.LocalDate;
import java.util.List;

/**
 * SearchPermissionEndpointsApiRequest - PermissionEndpoint 검색 API Request
 *
 * <p>PermissionEndpoint 목록 조회 REST API 요청 DTO입니다.
 *
 * @param permissionIds 권한 ID 필터 목록
 * @param searchWord 검색어
 * @param searchField 검색 필드 (URL_PATTERN, HTTP_METHOD, DESCRIPTION)
 * @param httpMethods HTTP 메서드 필터 목록
 * @param startDate 조회 시작일
 * @param endDate 조회 종료일
 * @param page 페이지 번호
 * @param size 페이지 크기
 * @author development-team
 * @since 1.0.0
 */
@Schema(description = "PermissionEndpoint 검색 요청 DTO")
public record SearchPermissionEndpointsApiRequest(
        @Schema(description = "권한 ID 필터 목록") List<Long> permissionIds,
        @Schema(description = "검색어", example = "users") String searchWord,
        @Schema(description = "검색 필드", example = "URL_PATTERN") String searchField,
        @Schema(description = "HTTP 메서드 필터 목록", example = "[\"GET\", \"POST\"]")
                List<String> httpMethods,
        @Schema(description = "조회 시작일", example = "2024-01-01") LocalDate startDate,
        @Schema(description = "조회 종료일", example = "2024-12-31") LocalDate endDate,
        @Schema(description = "페이지 번호", example = "0", defaultValue = "0")
                @Min(value = 0, message = "page는 0 이상이어야 합니다")
                Integer page,
        @Schema(description = "페이지 크기", example = "20", defaultValue = "20")
                @Min(value = 1, message = "size는 1 이상이어야 합니다")
                @Max(value = 100, message = "size는 100 이하여야 합니다")
                Integer size) {}
