package com.ryuqq.authhub.application.organization.dto.query;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * SearchOrganizationsQuery - 조직 목록 조회 Query DTO
 *
 * <p>기본 조회와 Admin 조회를 모두 지원합니다.
 *
 * <p><strong>검색 필터:</strong>
 *
 * <ul>
 *   <li>tenantId - 테넌트 ID (선택 - Admin은 전체 조회 가능)
 *   <li>name - 조직 이름 검색 (선택)
 *   <li>searchType - 이름 검색 방식 (CONTAINS_LIKE/PREFIX_LIKE/MATCH_AGAINST)
 *   <li>statuses - 다중 상태 필터 (선택)
 * </ul>
 *
 * <p><strong>Admin 확장 필터:</strong>
 *
 * <ul>
 *   <li>createdFrom, createdTo - 생성일시 범위 필터
 *   <li>sortBy, sortDirection - 정렬 옵션
 * </ul>
 *
 * @param tenantId 테넌트 ID (선택 - Admin은 전체 조회 가능)
 * @param name 조직 이름 (선택 - 부분 검색)
 * @param searchType 이름 검색 방식 (선택 - CONTAINS_LIKE/PREFIX_LIKE/MATCH_AGAINST)
 * @param statuses 조직 상태 목록 (선택 - 다중 선택)
 * @param createdFrom 생성일시 시작 (Admin용 확장 필터)
 * @param createdTo 생성일시 종료 (Admin용 확장 필터)
 * @param sortBy 정렬 기준 필드 (Admin용 확장 필터)
 * @param sortDirection 정렬 방향 ASC/DESC (Admin용 확장 필터)
 * @param page 페이지 번호 (0부터 시작)
 * @param size 페이지 크기
 * @author development-team
 * @since 1.0.0
 */
public record SearchOrganizationsQuery(
        UUID tenantId,
        String name,
        String searchType,
        List<String> statuses,
        Instant createdFrom,
        Instant createdTo,
        String sortBy,
        String sortDirection,
        int page,
        int size) {

    /** 기본 페이지 크기 */
    public static final int DEFAULT_PAGE_SIZE = 20;

    /** 기본 정렬 필드 */
    public static final String DEFAULT_SORT_BY = "createdAt";

    /** 기본 정렬 방향 */
    public static final String DEFAULT_SORT_DIRECTION = "DESC";

    /** 기본 생성자 (페이지/사이즈 검증) */
    public SearchOrganizationsQuery {
        if (page < 0) {
            page = 0;
        }
        if (size <= 0 || size > 100) {
            size = DEFAULT_PAGE_SIZE;
        }
    }

    /**
     * 기본 조회용 생성자 (하위 호환성 유지)
     *
     * @param tenantId 테넌트 ID
     * @param name 조직 이름
     * @param status 조직 상태 (단일)
     * @param page 페이지 번호
     * @param size 페이지 크기
     */
    public SearchOrganizationsQuery(UUID tenantId, String name, String status, int page, int size) {
        this(
                tenantId,
                name,
                null,
                status != null ? List.of(status) : null,
                null,
                null,
                DEFAULT_SORT_BY,
                DEFAULT_SORT_DIRECTION,
                page,
                size);
    }

    public static SearchOrganizationsQuery of(UUID tenantId) {
        return new SearchOrganizationsQuery(tenantId, null, null, 0, DEFAULT_PAGE_SIZE);
    }

    public static SearchOrganizationsQuery of(UUID tenantId, int page, int size) {
        return new SearchOrganizationsQuery(tenantId, null, null, page, size);
    }

    /**
     * 일반 API용 팩토리 메서드 (날짜 범위 필터 포함)
     *
     * @param tenantId 테넌트 ID (필수)
     * @param name 조직 이름 (선택)
     * @param statuses 상태 필터 목록 (선택)
     * @param createdFrom 생성일시 시작 (필수)
     * @param createdTo 생성일시 종료 (필수)
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @return SearchOrganizationsQuery
     */
    public static SearchOrganizationsQuery of(
            UUID tenantId,
            String name,
            List<String> statuses,
            Instant createdFrom,
            Instant createdTo,
            int page,
            int size) {
        return new SearchOrganizationsQuery(
                tenantId,
                name,
                null,
                statuses,
                createdFrom,
                createdTo,
                DEFAULT_SORT_BY,
                DEFAULT_SORT_DIRECTION,
                page,
                size);
    }

    /**
     * Admin API용 팩토리 메서드 (확장 필터 포함)
     *
     * @param tenantId 테넌트 ID (선택 - Admin은 전체 조회 가능)
     * @param name 조직 이름 (선택)
     * @param searchType 검색 방식 (선택)
     * @param statuses 상태 필터 목록 (선택)
     * @param createdFrom 생성일시 시작 (선택)
     * @param createdTo 생성일시 종료 (선택)
     * @param sortBy 정렬 필드 (선택)
     * @param sortDirection 정렬 방향 (선택)
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @return SearchOrganizationsQuery
     */
    public static SearchOrganizationsQuery ofAdmin(
            UUID tenantId,
            String name,
            String searchType,
            List<String> statuses,
            Instant createdFrom,
            Instant createdTo,
            String sortBy,
            String sortDirection,
            int page,
            int size) {
        return new SearchOrganizationsQuery(
                tenantId,
                name,
                searchType,
                statuses,
                createdFrom,
                createdTo,
                sortBy != null ? sortBy : DEFAULT_SORT_BY,
                sortDirection != null ? sortDirection : DEFAULT_SORT_DIRECTION,
                page,
                size);
    }
}
