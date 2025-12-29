package com.ryuqq.authhub.adapter.in.rest.role.dto.query;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.Instant;
import java.util.List;

/**
 * SearchRolesAdminApiRequest - 역할 목록 검색 요청 DTO (Admin용)
 *
 * <p>어드민 화면용 확장된 필터를 지원하는 검색 요청입니다.
 *
 * <p><strong>확장 필터:</strong>
 *
 * <ul>
 *   <li>tenantId 선택적 (전체 테넌트 조회 가능)
 *   <li>searchType 추가 (CONTAINS_LIKE/PREFIX_LIKE/MATCH_AGAINST)
 *   <li>scopes, types 다중 선택 가능
 *   <li>날짜 범위 필터 (createdFrom, createdTo) - 필수
 *   <li>정렬 옵션 (sortBy, sortDirection)
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>Record 타입 필수
 *   <li>*ApiRequest 네이밍 규칙
 *   <li>Lombok 금지
 *   <li>Jackson 어노테이션 금지
 *   <li>Domain 변환 메서드 금지
 * </ul>
 *
 * @param tenantId 테넌트 ID 필터 (선택)
 * @param name 역할 이름 필터 (선택)
 * @param searchType 이름 검색 방식 (CONTAINS_LIKE/PREFIX_LIKE/MATCH_AGAINST)
 * @param scopes 역할 범위 필터 - 다중 선택 (선택)
 * @param types 역할 유형 필터 - 다중 선택 (선택)
 * @param createdFrom 생성일시 시작 (필수)
 * @param createdTo 생성일시 종료 (필수)
 * @param sortBy 정렬 기준 (기본: createdAt)
 * @param sortDirection 정렬 방향 (기본: DESC)
 * @param page 페이지 번호 (기본: 0)
 * @param size 페이지 크기 (기본: 20)
 * @author development-team
 * @since 1.0.0
 * @see SearchRolesApiRequest 기본 검색 요청 DTO
 */
@Schema(description = "역할 검색 조건 (Admin용)")
public record SearchRolesAdminApiRequest(
        @Schema(description = "테넌트 ID 필터") String tenantId,
        @Schema(description = "역할 이름 필터 (부분 일치)") String name,
        @Schema(
                        description = "이름 검색 방식 (CONTAINS_LIKE/PREFIX_LIKE/MATCH_AGAINST)",
                        defaultValue = "CONTAINS_LIKE")
                @Pattern(
                        regexp = "CONTAINS_LIKE|PREFIX_LIKE|MATCH_AGAINST",
                        message = "유효하지 않은 검색 방식입니다")
                String searchType,
        @Schema(
                        description = "역할 범위 필터 - 다중 선택 (TENANT/ORGANIZATION/SYSTEM)",
                        allowableValues = {"TENANT", "ORGANIZATION", "SYSTEM"})
                List<String> scopes,
        @Schema(
                        description = "역할 유형 필터 - 다중 선택 (SYSTEM/CUSTOM)",
                        allowableValues = {"SYSTEM", "CUSTOM"})
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
        @Schema(
                        description = "정렬 기준",
                        allowableValues = {"createdAt", "updatedAt", "name"},
                        defaultValue = "createdAt")
                String sortBy,
        @Schema(
                        description = "정렬 방향",
                        allowableValues = {"ASC", "DESC"},
                        defaultValue = "DESC")
                @Pattern(regexp = "ASC|DESC", message = "정렬 방향은 ASC 또는 DESC여야 합니다")
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
