package com.ryuqq.authhub.adapter.in.rest.permission.dto.query;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.List;

/**
 * SearchPermissionsApiRequest - 권한 검색 요청 DTO
 *
 * <p>권한 검색 API의 쿼리 파라미터를 표현합니다.
 *
 * <p><strong>필수 필터:</strong>
 *
 * <ul>
 *   <li>createdFrom, createdTo: 생성일시 범위 필터 (필수)
 * </ul>
 *
 * <p><strong>선택 필터:</strong>
 *
 * <ul>
 *   <li>resource: 리소스명 검색 (CONTAINS_LIKE 검색)
 *   <li>action: 액션명 검색 (CONTAINS_LIKE 검색)
 *   <li>types: 권한 유형 필터 (다중 선택 가능)
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>Record 타입 필수
 *   <li>*ApiRequest 네이밍 규칙
 *   <li>Bean Validation 어노테이션 사용
 *   <li>Lombok 금지
 *   <li>Jackson 어노테이션 금지
 *   <li>Domain 변환 메서드 금지
 * </ul>
 *
 * @param resource 리소스명 검색어 (선택)
 * @param action 액션명 검색어 (선택)
 * @param types 권한 유형 필터 목록 - 다중 선택 가능 (선택)
 * @param createdFrom 생성일시 시작 (필수)
 * @param createdTo 생성일시 종료 (필수)
 * @param page 페이지 번호 (기본: 0)
 * @param size 페이지 크기 (기본: 20)
 * @author development-team
 * @since 1.0.0
 */
@Schema(description = "권한 목록 검색 조건")
public record SearchPermissionsApiRequest(
        @Schema(description = "리소스명 검색어", example = "USER") String resource,
        @Schema(description = "액션명 검색어", example = "READ") String action,
        @Schema(
                        description = "권한 유형 필터 (다중 선택 가능)",
                        allowableValues = {"SYSTEM", "CUSTOM"},
                        example = "[\"SYSTEM\", \"CUSTOM\"]")
                List<String> types,
        @Schema(
                        description = "생성일시 시작 (필수)",
                        example = "2024-01-01T00:00:00Z",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @NotNull(message = "생성일시 시작은 필수입니다")
                Instant createdFrom,
        @Schema(
                        description = "생성일시 종료 (필수)",
                        example = "2024-12-31T23:59:59Z",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @NotNull(message = "생성일시 종료는 필수입니다")
                Instant createdTo,
        @Schema(description = "페이지 번호", minimum = "0", defaultValue = "0")
                @Min(value = 0, message = "페이지는 0 이상이어야 합니다")
                Integer page,
        @Schema(description = "페이지 크기", minimum = "1", maximum = "100", defaultValue = "20")
                @Min(value = 1, message = "사이즈는 1 이상이어야 합니다")
                @Max(value = 100, message = "사이즈는 100 이하여야 합니다")
                Integer size) {

    /** 기본 페이지 크기 */
    public static final int DEFAULT_PAGE_SIZE = 20;

    /** 기본 페이지 번호 */
    public static final int DEFAULT_PAGE = 0;

    /**
     * 페이지 번호 반환 (null이면 기본값)
     *
     * @return 페이지 번호
     */
    public int pageOrDefault() {
        return page != null ? page : DEFAULT_PAGE;
    }

    /**
     * 페이지 크기 반환 (null이면 기본값)
     *
     * @return 페이지 크기
     */
    public int sizeOrDefault() {
        return size != null ? size : DEFAULT_PAGE_SIZE;
    }
}
