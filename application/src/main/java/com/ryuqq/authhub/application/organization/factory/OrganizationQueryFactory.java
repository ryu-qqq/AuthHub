package com.ryuqq.authhub.application.organization.factory;

import com.ryuqq.authhub.application.common.factory.CommonVoFactory;
import com.ryuqq.authhub.application.organization.dto.query.OrganizationSearchParams;
import com.ryuqq.authhub.domain.common.vo.DateRange;
import com.ryuqq.authhub.domain.common.vo.QueryContext;
import com.ryuqq.authhub.domain.organization.query.criteria.OrganizationSearchCriteria;
import com.ryuqq.authhub.domain.organization.vo.OrganizationSearchField;
import com.ryuqq.authhub.domain.organization.vo.OrganizationSortKey;
import com.ryuqq.authhub.domain.organization.vo.OrganizationStatus;
import com.ryuqq.authhub.domain.tenant.id.TenantId;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * OrganizationQueryFactory - 조직 SearchParams → Criteria 변환 Factory
 *
 * <p>Application Layer의 SearchParams DTO를 Domain Layer의 Criteria로 변환합니다.
 *
 * <p>DOM-CRI-010: Criteria는 DateRange, QueryContext 등 공통 VO 사용 권장.
 *
 * <p><strong>변환 책임:</strong>
 *
 * <ul>
 *   <li>tenantIds (List&lt;String&gt;) → List&lt;TenantId&gt;
 *   <li>searchField (String) → OrganizationSearchField enum
 *   <li>statuses (List&lt;String&gt;) → List&lt;OrganizationStatus&gt;
 *   <li>CommonSearchParams → DateRange (CommonVoFactory 사용)
 *   <li>CommonSearchParams → QueryContext (toQueryContext 사용)
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

    private final CommonVoFactory commonVoFactory;

    public OrganizationQueryFactory(CommonVoFactory commonVoFactory) {
        this.commonVoFactory = commonVoFactory;
    }

    /**
     * OrganizationSearchParams → OrganizationSearchCriteria 변환
     *
     * <p>CommonSearchParams.toQueryContext()를 사용하여 QueryContext 생성.
     *
     * <p>CommonVoFactory.createDateRange()를 사용하여 DateRange 생성.
     *
     * @param params Application SearchParams DTO
     * @return Domain Criteria
     */
    public OrganizationSearchCriteria toCriteria(OrganizationSearchParams params) {
        // CommonVoFactory를 사용하여 DateRange 생성
        DateRange dateRange = commonVoFactory.createDateRange(params.startDate(), params.endDate());

        // CommonSearchParams.toQueryContext()를 사용하여 QueryContext 생성
        QueryContext<OrganizationSortKey> queryContext =
                params.searchParams().toQueryContext(OrganizationSortKey.class);

        return new OrganizationSearchCriteria(
                parseTenantIds(params.tenantIds()),
                params.searchWord(),
                parseSearchField(params.searchField()),
                parseStatuses(params.statuses()),
                dateRange,
                queryContext);
    }

    /**
     * 테넌트 ID 문자열 목록 → TenantId 목록 변환
     *
     * @param tenantIds 테넌트 ID 문자열 목록
     * @return TenantId 목록 (null이나 빈 목록이면 null 반환)
     */
    private List<TenantId> parseTenantIds(List<String> tenantIds) {
        if (tenantIds == null || tenantIds.isEmpty()) {
            return null;
        }
        return tenantIds.stream().map(TenantId::of).toList();
    }

    /**
     * 검색 필드 문자열 → OrganizationSearchField enum 변환
     *
     * @param searchField 검색 필드 문자열
     * @return OrganizationSearchField enum (기본: NAME)
     */
    private OrganizationSearchField parseSearchField(String searchField) {
        return OrganizationSearchField.fromString(searchField);
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
}
