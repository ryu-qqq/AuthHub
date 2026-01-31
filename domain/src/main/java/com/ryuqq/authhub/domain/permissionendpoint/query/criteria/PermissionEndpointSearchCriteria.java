package com.ryuqq.authhub.domain.permissionendpoint.query.criteria;

import com.ryuqq.authhub.domain.common.vo.DateRange;
import com.ryuqq.authhub.domain.common.vo.PageRequest;
import com.ryuqq.authhub.domain.common.vo.QueryContext;
import com.ryuqq.authhub.domain.common.vo.SortDirection;
import com.ryuqq.authhub.domain.permissionendpoint.vo.HttpMethod;
import com.ryuqq.authhub.domain.permissionendpoint.vo.PermissionEndpointSearchField;
import com.ryuqq.authhub.domain.permissionendpoint.vo.PermissionEndpointSortKey;
import java.time.Instant;
import java.util.List;

/**
 * PermissionEndpointSearchCriteria - PermissionEndpoint 검색 조건 Criteria
 *
 * <p>PermissionEndpoint 목록 조회 시 사용하는 검색 조건을 정의합니다.
 *
 * <p><strong>검색 조건:</strong>
 *
 * <ul>
 *   <li>{@code permissionIds}: 여러 권한의 엔드포인트 조회 (선택)
 *   <li>{@code searchWord}: 검색어 (선택)
 *   <li>{@code searchField}: 검색 필드 - URL_PATTERN, HTTP_METHOD, DESCRIPTION (필수)
 *   <li>{@code httpMethods}: HTTP 메서드 필터 (선택)
 *   <li>{@code dateRange}: 생성일시 범위 필터 (필수)
 *   <li>{@code queryContext}: 정렬 + 페이징 컨텍스트
 * </ul>
 *
 * @param permissionIds 권한 ID 목록 (복수 조회 시)
 * @param searchWord 검색어 (null 허용)
 * @param searchField 검색 필드 (필수)
 * @param httpMethods HTTP 메서드 필터 목록 (null 또는 빈 목록 시 전체)
 * @param dateRange 생성일시 범위 (필수)
 * @param queryContext 정렬 + 페이징 컨텍스트 (필수)
 * @author development-team
 * @since 1.0.0
 */
public record PermissionEndpointSearchCriteria(
        List<Long> permissionIds,
        String searchWord,
        PermissionEndpointSearchField searchField,
        List<HttpMethod> httpMethods,
        DateRange dateRange,
        QueryContext<PermissionEndpointSortKey> queryContext) {

    /**
     * 팩토리 메서드
     *
     * @param permissionIds 권한 ID 목록
     * @param searchWord 검색어
     * @param searchField 검색 필드 (enum)
     * @param httpMethods HTTP 메서드 필터 목록
     * @param dateRange 생성일시 범위
     * @param sortKey 정렬 기준
     * @param sortDirection 정렬 방향
     * @param pageRequest 페이지 요청
     * @return PermissionEndpointSearchCriteria
     */
    public static PermissionEndpointSearchCriteria of(
            List<Long> permissionIds,
            String searchWord,
            PermissionEndpointSearchField searchField,
            List<HttpMethod> httpMethods,
            DateRange dateRange,
            PermissionEndpointSortKey sortKey,
            SortDirection sortDirection,
            PageRequest pageRequest) {
        QueryContext<PermissionEndpointSortKey> context =
                QueryContext.of(sortKey, sortDirection, pageRequest);
        return new PermissionEndpointSearchCriteria(
                permissionIds, searchWord, searchField, httpMethods, dateRange, context);
    }

    /**
     * 특정 권한의 엔드포인트 조회용 팩토리 메서드
     *
     * @param permissionId 권한 ID
     * @param pageNumber 페이지 번호
     * @param pageSize 페이지 크기
     * @return PermissionEndpointSearchCriteria
     */
    public static PermissionEndpointSearchCriteria forPermission(
            Long permissionId, int pageNumber, int pageSize) {
        QueryContext<PermissionEndpointSortKey> context =
                QueryContext.of(
                        PermissionEndpointSortKey.CREATED_AT,
                        SortDirection.DESC,
                        PageRequest.of(pageNumber, pageSize));
        return new PermissionEndpointSearchCriteria(
                List.of(permissionId),
                null,
                PermissionEndpointSearchField.defaultField(),
                null,
                null,
                context);
    }

    /**
     * Gateway 조회용 팩토리 메서드 (URL 패턴과 HTTP 메서드로 검색)
     *
     * @param urlPattern URL 패턴
     * @param httpMethod HTTP 메서드
     * @return PermissionEndpointSearchCriteria
     */
    public static PermissionEndpointSearchCriteria forGateway(
            String urlPattern, HttpMethod httpMethod) {
        QueryContext<PermissionEndpointSortKey> context =
                QueryContext.of(
                        PermissionEndpointSortKey.CREATED_AT,
                        SortDirection.DESC,
                        PageRequest.of(0, 100));
        return new PermissionEndpointSearchCriteria(
                null,
                urlPattern,
                PermissionEndpointSearchField.URL_PATTERN,
                httpMethod != null ? List.of(httpMethod) : null,
                null,
                context);
    }

    /**
     * 권한 ID 목록 필터가 존재하는지 확인
     *
     * @return 권한 ID 목록이 있으면 true
     */
    public boolean hasPermissionIds() {
        return permissionIds != null && !permissionIds.isEmpty();
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
     * HTTP 메서드 필터가 존재하는지 확인
     *
     * @return HTTP 메서드 필터가 있으면 true
     */
    public boolean hasHttpMethodFilter() {
        return httpMethods != null && !httpMethods.isEmpty();
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
    public PermissionEndpointSortKey sortKey() {
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
