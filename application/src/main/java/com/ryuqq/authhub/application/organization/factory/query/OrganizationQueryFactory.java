package com.ryuqq.authhub.application.organization.factory.query;

import com.ryuqq.authhub.application.organization.dto.query.SearchOrganizationsQuery;
import com.ryuqq.authhub.domain.common.vo.DateRange;
import com.ryuqq.authhub.domain.common.vo.PageRequest;
import com.ryuqq.authhub.domain.common.vo.SearchType;
import com.ryuqq.authhub.domain.common.vo.SortDirection;
import com.ryuqq.authhub.domain.organization.query.criteria.OrganizationCriteria;
import com.ryuqq.authhub.domain.organization.vo.OrganizationSortKey;
import com.ryuqq.authhub.domain.organization.vo.OrganizationStatus;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Component;

/**
 * OrganizationQueryFactory - 조직 Query → Criteria 변환 Factory
 *
 * <p>Application Layer의 Query DTO를 Domain Layer의 Criteria로 변환합니다.
 *
 * <p><strong>변환 책임:</strong>
 *
 * <ul>
 *   <li>tenantId (UUID) → TenantId (Value Object)
 *   <li>statuses (List&lt;String&gt;) → List&lt;OrganizationStatus&gt;
 *   <li>createdFrom/To (Instant) → DateRange (LocalDate)
 *   <li>searchType (String) → SearchType enum
 *   <li>sortBy (String) → OrganizationSortKey enum
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
public class OrganizationQueryFactory {

    /**
     * SearchOrganizationsQuery → OrganizationCriteria 변환
     *
     * @param query Application Query DTO
     * @return Domain Criteria
     */
    public OrganizationCriteria toCriteria(SearchOrganizationsQuery query) {
        return OrganizationCriteria.of(
                parseTenantId(query.tenantId()),
                query.name(),
                parseSearchType(query.searchType()),
                parseStatuses(query.statuses()),
                parseDateRange(query.createdFrom(), query.createdTo()),
                parseSortKey(query.sortBy()),
                parseSortDirection(query.sortDirection()),
                PageRequest.of(query.page(), query.size()));
    }

    /**
     * UUID → TenantId 변환
     *
     * @param tenantId 테넌트 UUID
     * @return TenantId (null이면 null 반환)
     */
    private TenantId parseTenantId(UUID tenantId) {
        return tenantId != null ? TenantId.of(tenantId) : null;
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
     * 상태 문자열 목록 → OrganizationStatus 목록 변환
     *
     * @param statuses 상태 문자열 목록
     * @return OrganizationStatus 목록 (null이나 빈 목록이면 null 반환)
     */
    private List<OrganizationStatus> parseStatuses(List<String> statuses) {
        if (statuses == null || statuses.isEmpty()) {
            return null;
        }
        return statuses.stream().map(OrganizationStatus::valueOf).toList();
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
     * 문자열 → OrganizationSortKey 변환
     *
     * @param sortBy 정렬 기준 문자열
     * @return OrganizationSortKey enum (null이면 기본값)
     */
    private OrganizationSortKey parseSortKey(String sortBy) {
        return OrganizationSortKey.fromString(sortBy);
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
