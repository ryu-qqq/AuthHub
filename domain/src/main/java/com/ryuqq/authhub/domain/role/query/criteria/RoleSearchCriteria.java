package com.ryuqq.authhub.domain.role.query.criteria;

import com.ryuqq.authhub.domain.common.vo.DateRange;
import com.ryuqq.authhub.domain.common.vo.PageRequest;
import com.ryuqq.authhub.domain.common.vo.QueryContext;
import com.ryuqq.authhub.domain.common.vo.SortDirection;
import com.ryuqq.authhub.domain.role.vo.RoleSearchField;
import com.ryuqq.authhub.domain.role.vo.RoleSortKey;
import com.ryuqq.authhub.domain.role.vo.RoleType;
import com.ryuqq.authhub.domain.tenant.id.TenantId;
import java.time.Instant;
import java.util.List;

/**
 * RoleSearchCriteria - 역할 검색 조건 Criteria
 *
 * <p>역할 목록 조회 시 사용하는 검색 조건을 정의합니다.
 *
 * <p><strong>검색 조건:</strong>
 *
 * <ul>
 *   <li>{@code tenantId}: 테넌트 필터 (null이면 Global 역할만 조회)
 *   <li>{@code searchWord}: 검색어 (선택)
 *   <li>{@code searchField}: 검색 필드 - NAME, DISPLAY_NAME, DESCRIPTION (필수)
 *   <li>{@code types}: 역할 유형 필터 - SYSTEM, CUSTOM (선택)
 *   <li>{@code dateRange}: 생성일시 범위 필터 (필수)
 *   <li>{@code queryContext}: 정렬 + 페이징 컨텍스트
 * </ul>
 *
 * <p><strong>설계 원칙:</strong>
 *
 * <ul>
 *   <li>tenantId가 null이면 Global 역할만 조회
 *   <li>tenantId가 있으면 해당 테넌트 역할 + Global 역할 조회
 *   <li>Domain Criteria는 null 체크/기본값 로직을 포함하지 않음
 *   <li>변환 책임은 Application Factory에서 수행
 *   <li>타입 안전성을 위해 searchField는 enum 타입 사용
 * </ul>
 *
 * @param tenantId 테넌트 ID (null이면 Global만 조회)
 * @param searchWord 검색어 (null 허용)
 * @param searchField 검색 필드 (필수, enum 타입)
 * @param types 역할 유형 필터 목록 (null 또는 빈 목록 시 전체)
 * @param dateRange 생성일시 범위 (필수)
 * @param queryContext 정렬 + 페이징 컨텍스트 (필수)
 * @author development-team
 * @since 1.0.0
 */
public record RoleSearchCriteria(
        TenantId tenantId,
        String searchWord,
        RoleSearchField searchField,
        List<RoleType> types,
        DateRange dateRange,
        QueryContext<RoleSortKey> queryContext) {

    /**
     * 팩토리 메서드
     *
     * @param tenantId 테넌트 ID (null이면 Global만)
     * @param searchWord 검색어
     * @param searchField 검색 필드 (enum)
     * @param types 역할 유형 필터 목록
     * @param dateRange 생성일시 범위
     * @param sortKey 정렬 기준
     * @param sortDirection 정렬 방향
     * @param pageRequest 페이지 요청
     * @return RoleSearchCriteria
     */
    public static RoleSearchCriteria of(
            TenantId tenantId,
            String searchWord,
            RoleSearchField searchField,
            List<RoleType> types,
            DateRange dateRange,
            RoleSortKey sortKey,
            SortDirection sortDirection,
            PageRequest pageRequest) {
        QueryContext<RoleSortKey> context = QueryContext.of(sortKey, sortDirection, pageRequest);
        return new RoleSearchCriteria(tenantId, searchWord, searchField, types, dateRange, context);
    }

    /**
     * Global 역할 조회용 팩토리 메서드
     *
     * @param searchWord 검색어
     * @param types 역할 유형 필터 목록
     * @param dateRange 생성일시 범위
     * @param pageNumber 페이지 번호
     * @param pageSize 페이지 크기
     * @return RoleSearchCriteria (tenantId = null)
     */
    public static RoleSearchCriteria ofGlobal(
            String searchWord,
            List<RoleType> types,
            DateRange dateRange,
            int pageNumber,
            int pageSize) {
        QueryContext<RoleSortKey> context =
                QueryContext.of(
                        RoleSortKey.CREATED_AT,
                        SortDirection.DESC,
                        PageRequest.of(pageNumber, pageSize));
        return new RoleSearchCriteria(
                null, searchWord, RoleSearchField.defaultField(), types, dateRange, context);
    }

    /**
     * 테넌트 역할 조회용 팩토리 메서드 (테넌트 + Global 역할 모두 조회)
     *
     * @param tenantId 테넌트 ID
     * @param searchWord 검색어
     * @param types 역할 유형 필터 목록
     * @param dateRange 생성일시 범위
     * @param pageNumber 페이지 번호
     * @param pageSize 페이지 크기
     * @return RoleSearchCriteria
     */
    public static RoleSearchCriteria ofTenant(
            TenantId tenantId,
            String searchWord,
            List<RoleType> types,
            DateRange dateRange,
            int pageNumber,
            int pageSize) {
        QueryContext<RoleSortKey> context =
                QueryContext.of(
                        RoleSortKey.CREATED_AT,
                        SortDirection.DESC,
                        PageRequest.of(pageNumber, pageSize));
        return new RoleSearchCriteria(
                tenantId, searchWord, RoleSearchField.defaultField(), types, dateRange, context);
    }

    /**
     * 테넌트 필터가 존재하는지 확인
     *
     * @return 테넌트 ID가 있으면 true
     */
    public boolean hasTenantFilter() {
        return tenantId != null;
    }

    /**
     * Global 역할만 조회하는지 확인
     *
     * @return 테넌트 ID가 없으면 true (Global만)
     */
    public boolean isGlobalOnly() {
        return tenantId == null;
    }

    /**
     * 검색어가 존재하는지 확인
     *
     * @return 검색어가 있으면 true
     */
    public boolean hasSearchWord() {
        return searchWord != null && !searchWord.isBlank();
    }

    /**
     * 역할 유형 필터가 존재하는지 확인
     *
     * @return 역할 유형 필터가 있으면 true
     */
    public boolean hasTypeFilter() {
        return types != null && !types.isEmpty();
    }

    /**
     * 오프셋 계산 (QueryDSL 페이징용)
     *
     * @return offset 값
     */
    public long offset() {
        return queryContext.offset();
    }

    /**
     * 페이지 크기 반환
     *
     * @return 페이지 크기
     */
    public int size() {
        return queryContext.size();
    }

    /**
     * 페이지 번호 반환
     *
     * @return 페이지 번호
     */
    public int pageNumber() {
        return queryContext.page();
    }

    /**
     * 정렬 키 반환
     *
     * @return 정렬 키
     */
    public RoleSortKey sortKey() {
        return queryContext.sortKey();
    }

    /**
     * 정렬 방향 반환
     *
     * @return 정렬 방향
     */
    public SortDirection sortDirection() {
        return queryContext.sortDirection();
    }

    /**
     * 삭제된 항목 포함 여부
     *
     * @return 삭제 포함 시 true
     */
    public boolean includeDeleted() {
        return queryContext.includeDeleted();
    }

    // ===== DateRange 편의 메서드 (Law of Demeter 준수) =====

    /**
     * 날짜 범위 필터가 존재하는지 확인
     *
     * @return 날짜 범위가 있으면 true
     */
    public boolean hasDateRange() {
        return dateRange != null && !dateRange.isEmpty();
    }

    /**
     * 시작 일시를 Instant로 반환
     *
     * @return 시작 일시 (null 허용)
     */
    public Instant startInstant() {
        return dateRange != null ? dateRange.startInstant() : null;
    }

    /**
     * 종료 일시를 Instant로 반환
     *
     * @return 종료 일시 (null 허용)
     */
    public Instant endInstant() {
        return dateRange != null ? dateRange.endInstant() : null;
    }
}
