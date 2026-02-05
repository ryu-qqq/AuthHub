package com.ryuqq.authhub.domain.tenantservice.query.criteria;

import com.ryuqq.authhub.domain.common.vo.DateRange;
import com.ryuqq.authhub.domain.common.vo.PageRequest;
import com.ryuqq.authhub.domain.common.vo.QueryContext;
import com.ryuqq.authhub.domain.common.vo.SortDirection;
import com.ryuqq.authhub.domain.tenantservice.vo.TenantServiceSortKey;
import com.ryuqq.authhub.domain.tenantservice.vo.TenantServiceStatus;
import java.time.Instant;
import java.util.List;

/**
 * TenantServiceSearchCriteria - 테넌트-서비스 구독 검색 조건 Criteria
 *
 * <p>테넌트-서비스 구독 목록 조회 시 사용하는 검색 조건을 정의합니다.
 *
 * <p><strong>검색 조건:</strong>
 *
 * <ul>
 *   <li>{@code tenantId}: 테넌트 ID 필터 (선택)
 *   <li>{@code serviceId}: 서비스 ID 필터 (선택)
 *   <li>{@code statuses}: 상태 필터 - 다중 선택 (선택)
 *   <li>{@code dateRange}: 구독일시 범위 필터 (필수)
 *   <li>{@code queryContext}: 정렬 + 페이징 컨텍스트
 * </ul>
 *
 * @param tenantId 테넌트 ID (null 허용)
 * @param serviceId 서비스 ID (null 허용)
 * @param statuses 상태 필터 목록 (null 또는 빈 목록 시 전체)
 * @param dateRange 구독일시 범위 (필수)
 * @param queryContext 정렬 + 페이징 컨텍스트 (필수)
 * @author development-team
 * @since 1.0.0
 */
public record TenantServiceSearchCriteria(
        String tenantId,
        Long serviceId,
        List<TenantServiceStatus> statuses,
        DateRange dateRange,
        QueryContext<TenantServiceSortKey> queryContext) {

    /**
     * 팩토리 메서드
     *
     * @param tenantId 테넌트 ID
     * @param serviceId 서비스 ID
     * @param statuses 상태 필터 목록
     * @param dateRange 구독일시 범위
     * @param sortKey 정렬 기준
     * @param sortDirection 정렬 방향
     * @param pageRequest 페이지 요청
     * @return TenantServiceSearchCriteria
     */
    public static TenantServiceSearchCriteria of(
            String tenantId,
            Long serviceId,
            List<TenantServiceStatus> statuses,
            DateRange dateRange,
            TenantServiceSortKey sortKey,
            SortDirection sortDirection,
            PageRequest pageRequest) {
        QueryContext<TenantServiceSortKey> context =
                QueryContext.of(sortKey, sortDirection, pageRequest);
        return new TenantServiceSearchCriteria(tenantId, serviceId, statuses, dateRange, context);
    }

    /**
     * 테넌트 ID 필터가 존재하는지 확인
     *
     * @return 테넌트 ID가 있으면 true
     */
    public boolean hasTenantId() {
        return tenantId != null && !tenantId.isBlank();
    }

    /**
     * 서비스 ID 필터가 존재하는지 확인
     *
     * @return 서비스 ID가 있으면 true
     */
    public boolean hasServiceId() {
        return serviceId != null;
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
    public TenantServiceSortKey sortKey() {
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
