package com.ryuqq.authhub.domain.organization.query.criteria;

import com.ryuqq.authhub.domain.common.vo.DateRange;
import com.ryuqq.authhub.domain.common.vo.PageRequest;
import com.ryuqq.authhub.domain.common.vo.SearchType;
import com.ryuqq.authhub.domain.common.vo.SortDirection;
import com.ryuqq.authhub.domain.organization.vo.OrganizationSortKey;
import com.ryuqq.authhub.domain.organization.vo.OrganizationStatus;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import java.util.List;

/**
 * OrganizationCriteria - 조직 검색 조건 Criteria
 *
 * <p>조직 목록 조회 시 사용하는 검색 조건을 정의합니다.
 *
 * <p><strong>검색 조건:</strong>
 *
 * <ul>
 *   <li>{@code tenantId}: 테넌트 ID (선택 - Admin은 전체 조회)
 *   <li>{@code name}: 조직 이름 검색 (선택)
 *   <li>{@code nameSearchType}: 이름 검색 방식 (기본: CONTAINS_LIKE)
 *   <li>{@code statuses}: 상태 필터 - 다중 선택 (선택)
 *   <li>{@code dateRange}: 생성일시 범위 필터 (선택)
 * </ul>
 *
 * <p><strong>정렬/페이징:</strong>
 *
 * <ul>
 *   <li>{@code sortKey}: 정렬 기준 (기본: CREATED_AT)
 *   <li>{@code sortDirection}: 정렬 방향 (기본: DESC)
 *   <li>{@code page}: 페이지 요청 (page, size 포함)
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>Record 타입 필수
 *   <li>of() 정적 팩토리 메서드 필수
 *   <li>Lombok 금지
 *   <li>JPA/Spring 어노테이션 금지
 * </ul>
 *
 * @param tenantId 테넌트 ID (null 시 전체 조회 - Admin용)
 * @param name 조직 이름 검색어 (null 허용)
 * @param nameSearchType 이름 검색 방식 (null 시 CONTAINS_LIKE)
 * @param statuses 상태 필터 목록 (null 또는 빈 목록 시 전체)
 * @param dateRange 생성일시 범위 (null 허용)
 * @param sortKey 정렬 기준 (null 시 CREATED_AT)
 * @param sortDirection 정렬 방향 (null 시 DESC)
 * @param page 페이지 요청 (page, size 포함)
 * @author development-team
 * @since 1.0.0
 */
public record OrganizationCriteria(
        TenantId tenantId,
        String name,
        SearchType nameSearchType,
        List<OrganizationStatus> statuses,
        DateRange dateRange,
        OrganizationSortKey sortKey,
        SortDirection sortDirection,
        PageRequest page) {

    /** 기본 페이지 크기 */
    public static final int DEFAULT_PAGE_SIZE = 20;

    /**
     * 팩토리 메서드 - 기본값 적용
     *
     * @param tenantId 테넌트 ID
     * @param name 조직 이름 검색어
     * @param nameSearchType 이름 검색 방식
     * @param statuses 상태 필터 목록
     * @param dateRange 생성일시 범위
     * @param sortKey 정렬 기준
     * @param sortDirection 정렬 방향
     * @param page 페이지 요청
     * @return OrganizationCriteria
     */
    public static OrganizationCriteria of(
            TenantId tenantId,
            String name,
            SearchType nameSearchType,
            List<OrganizationStatus> statuses,
            DateRange dateRange,
            OrganizationSortKey sortKey,
            SortDirection sortDirection,
            PageRequest page) {
        return new OrganizationCriteria(
                tenantId,
                name,
                nameSearchType != null ? nameSearchType : SearchType.defaultType(),
                statuses,
                dateRange,
                sortKey != null ? sortKey : OrganizationSortKey.defaultKey(),
                sortDirection != null ? sortDirection : SortDirection.defaultDirection(),
                page != null ? page : PageRequest.defaultPage());
    }

    /**
     * 간소화된 팩토리 메서드 - 테넌트 범위 조회용
     *
     * @param tenantId 테넌트 ID
     * @param name 조직 이름 검색어
     * @param statuses 상태 필터 목록
     * @param pageNumber 페이지 번호
     * @param pageSize 페이지 크기
     * @return OrganizationCriteria
     */
    public static OrganizationCriteria ofSimple(
            TenantId tenantId,
            String name,
            List<OrganizationStatus> statuses,
            int pageNumber,
            int pageSize) {
        return of(
                tenantId,
                name,
                SearchType.defaultType(),
                statuses,
                null,
                OrganizationSortKey.defaultKey(),
                SortDirection.defaultDirection(),
                PageRequest.of(pageNumber, pageSize));
    }

    /**
     * 테넌트 ID 필터가 존재하는지 확인
     *
     * @return 테넌트 ID가 있으면 true
     */
    public boolean hasTenantFilter() {
        return tenantId != null;
    }

    /**
     * 이름 검색어가 존재하는지 확인
     *
     * @return 이름 검색어가 있으면 true
     */
    public boolean hasNameFilter() {
        return name != null && !name.isBlank();
    }

    /**
     * 상태 필터가 존재하는지 확인
     *
     * @return 상태 필터가 있으면 true
     */
    public boolean hasStatusFilter() {
        return statuses != null && !statuses.isEmpty();
    }

    /**
     * 오프셋 계산 (QueryDSL 페이징용)
     *
     * @return offset 값
     */
    public long offset() {
        return page.offset();
    }

    /**
     * 페이지 크기 반환
     *
     * @return 페이지 크기
     */
    public int size() {
        return page.size();
    }

    /**
     * 페이지 번호 반환
     *
     * @return 페이지 번호
     */
    public int pageNumber() {
        return page.page();
    }
}
