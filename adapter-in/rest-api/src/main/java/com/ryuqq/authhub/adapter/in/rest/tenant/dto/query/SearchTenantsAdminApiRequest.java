package com.ryuqq.authhub.adapter.in.rest.tenant.dto.query;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.List;

/**
 * SearchTenantsAdminApiRequest - Admin 테넌트 검색 요청 DTO
 *
 * <p>어드민 친화적 테넌트 목록 검색을 위한 요청 DTO입니다. 확장 필터(날짜 범위, 정렬, 검색 타입)를 지원합니다.
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
 *   <li>name: 이름 검색
 *   <li>searchType: 검색 방식 (PREFIX_LIKE, CONTAINS_LIKE)
 *   <li>statuses: 상태 필터 (다중 선택 가능)
 *   <li>sortBy, sortDirection: 정렬 옵션
 * </ul>
 *
 * <p><strong>SearchTenantsApiRequest와의 차이점:</strong>
 *
 * <ul>
 *   <li>searchType 추가 (검색 방식 지정)
 *   <li>sortBy, sortDirection 추가 (정렬 옵션)
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>Record 타입 필수
 *   <li>*ApiRequest 네이밍 규칙
 *   <li>Lombok 금지
 *   <li>Jackson 어노테이션 금지
 * </ul>
 *
 * @param name 테넌트 이름 검색어 (선택)
 * @param searchType 검색 방식 (PREFIX_LIKE, CONTAINS_LIKE, 기본: CONTAINS_LIKE)
 * @param statuses 상태 필터 목록 - 다중 선택 가능 (선택)
 * @param createdFrom 생성일시 시작 (필수)
 * @param createdTo 생성일시 종료 (필수)
 * @param sortBy 정렬 기준 (name, status, createdAt, updatedAt, 기본: createdAt)
 * @param sortDirection 정렬 방향 (ASC, DESC, 기본: DESC)
 * @param page 페이지 번호 (기본: 0)
 * @param size 페이지 크기 (기본: 20)
 * @author development-team
 * @since 1.0.0
 * @see SearchTenantsApiRequest 기본 검색 요청 DTO
 */
@Schema(description = "테넌트 검색 조건 (Admin용)")
public record SearchTenantsAdminApiRequest(
        @Schema(description = "테넌트 이름 검색어", example = "테넌트") String name,
        @Schema(
                        description = "검색 방식",
                        allowableValues = {"PREFIX_LIKE", "CONTAINS_LIKE"},
                        defaultValue = "CONTAINS_LIKE")
                String searchType,
        @Schema(
                        description = "상태 필터 (다중 선택 가능)",
                        allowableValues = {"ACTIVE", "INACTIVE", "DELETED"},
                        example = "[\"ACTIVE\", \"INACTIVE\"]")
                List<String> statuses,
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
        @Schema(
                        description = "정렬 기준",
                        allowableValues = {"name", "status", "createdAt", "updatedAt"},
                        defaultValue = "createdAt")
                String sortBy,
        @Schema(
                        description = "정렬 방향",
                        allowableValues = {"ASC", "DESC"},
                        defaultValue = "DESC")
                String sortDirection,
        @Schema(description = "페이지 번호", minimum = "0", defaultValue = "0")
                @Min(value = 0, message = "페이지 번호는 0 이상이어야 합니다")
                Integer page,
        @Schema(description = "페이지 크기", minimum = "1", maximum = "100", defaultValue = "20")
                @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다")
                @Max(value = 100, message = "페이지 크기는 100 이하여야 합니다")
                Integer size) {

    /** 기본 페이지 크기 */
    public static final int DEFAULT_PAGE_SIZE = 20;

    /** 기본 페이지 번호 */
    public static final int DEFAULT_PAGE = 0;

    /** 기본 정렬 기준 */
    public static final String DEFAULT_SORT_BY = "createdAt";

    /** 기본 정렬 방향 */
    public static final String DEFAULT_SORT_DIRECTION = "DESC";

    /** 기본 검색 타입 */
    public static final String DEFAULT_SEARCH_TYPE = "CONTAINS_LIKE";

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

    /**
     * 정렬 기준 반환 (null이면 기본값)
     *
     * @return 정렬 기준
     */
    public String sortByOrDefault() {
        return sortBy != null ? sortBy : DEFAULT_SORT_BY;
    }

    /**
     * 정렬 방향 반환 (null이면 기본값)
     *
     * @return 정렬 방향
     */
    public String sortDirectionOrDefault() {
        return sortDirection != null ? sortDirection : DEFAULT_SORT_DIRECTION;
    }

    /**
     * 검색 타입 반환 (null이면 기본값)
     *
     * @return 검색 타입
     */
    public String searchTypeOrDefault() {
        return searchType != null ? searchType : DEFAULT_SEARCH_TYPE;
    }
}
