package com.ryuqq.authhub.application.tenant.factory.query;

import com.ryuqq.authhub.application.tenant.dto.query.SearchTenantsQuery;
import com.ryuqq.authhub.domain.common.vo.DateRange;
import com.ryuqq.authhub.domain.common.vo.PageRequest;
import com.ryuqq.authhub.domain.common.vo.SearchType;
import com.ryuqq.authhub.domain.common.vo.SortDirection;
import com.ryuqq.authhub.domain.tenant.query.criteria.TenantCriteria;
import com.ryuqq.authhub.domain.tenant.vo.TenantSortKey;
import com.ryuqq.authhub.domain.tenant.vo.TenantStatus;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * TenantQueryFactory - 테넌트 Query → Criteria 변환 Factory
 *
 * <p>Application Layer의 Query DTO를 Domain Layer의 Criteria로 변환합니다.
 *
 * <p><strong>변환 책임:</strong>
 *
 * <ul>
 *   <li>statuses (List&lt;String&gt;) → List&lt;TenantStatus&gt;
 *   <li>createdFrom/To (Instant) → DateRange (LocalDate)
 *   <li>searchType (String) → SearchType enum
 *   <li>sortBy (String) → TenantSortKey enum
 *   <li>sortDirection (String) → SortDirection enum
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code @Component} 어노테이션 필수
 *   <li>Lombok 금지
 *   <li>비즈니스 로직 금지 (단순 변환만)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class TenantQueryFactory {

    /**
     * SearchTenantsQuery → TenantCriteria 변환
     *
     * @param query Application Query DTO
     * @return Domain Criteria
     */
    public TenantCriteria toCriteria(SearchTenantsQuery query) {
        return TenantCriteria.of(
                query.name(),
                parseSearchType(query.searchType()),
                parseStatuses(query.statuses()),
                parseDateRange(query.createdFrom(), query.createdTo()),
                parseSortKey(query.sortBy()),
                parseSortDirection(query.sortDirection()),
                PageRequest.of(query.page(), query.size()));
    }

    /**
     * 문자열 → SearchType 변환
     *
     * @param searchType 검색 타입 문자열
     * @return SearchType enum (null이면 기본값)
     */
    private SearchType parseSearchType(String searchType) {
        return SearchType.fromString(searchType);
    }

    /**
     * 상태 문자열 목록 → TenantStatus 목록 변환
     *
     * @param statuses 상태 문자열 목록
     * @return TenantStatus 목록 (null이나 빈 목록이면 null 반환)
     */
    private List<TenantStatus> parseStatuses(List<String> statuses) {
        if (statuses == null || statuses.isEmpty()) {
            return null;
        }
        return statuses.stream().map(TenantStatus::valueOf).toList();
    }

    /**
     * Instant → DateRange 변환
     *
     * <p>Instant를 시스템 기본 ZoneId 기준 LocalDate로 변환합니다.
     *
     * @param from 시작 일시
     * @param to 종료 일시
     * @return DateRange
     */
    private DateRange parseDateRange(Instant from, Instant to) {
        LocalDate startDate =
                from != null ? from.atZone(ZoneId.systemDefault()).toLocalDate() : null;
        LocalDate endDate = to != null ? to.atZone(ZoneId.systemDefault()).toLocalDate() : null;
        return DateRange.of(startDate, endDate);
    }

    /**
     * 문자열 → TenantSortKey 변환
     *
     * @param sortBy 정렬 기준 문자열
     * @return TenantSortKey enum (null이면 기본값)
     */
    private TenantSortKey parseSortKey(String sortBy) {
        return TenantSortKey.fromString(sortBy);
    }

    /**
     * 문자열 → SortDirection 변환
     *
     * @param sortDirection 정렬 방향 문자열
     * @return SortDirection enum (null이면 기본값)
     */
    private SortDirection parseSortDirection(String sortDirection) {
        return SortDirection.fromString(sortDirection);
    }
}
