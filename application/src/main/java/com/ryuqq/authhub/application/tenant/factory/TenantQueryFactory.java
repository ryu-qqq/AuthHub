package com.ryuqq.authhub.application.tenant.factory;

import com.ryuqq.authhub.application.common.factory.CommonVoFactory;
import com.ryuqq.authhub.application.tenant.dto.query.TenantSearchParams;
import com.ryuqq.authhub.domain.common.vo.DateRange;
import com.ryuqq.authhub.domain.common.vo.QueryContext;
import com.ryuqq.authhub.domain.tenant.query.criteria.TenantSearchCriteria;
import com.ryuqq.authhub.domain.tenant.vo.TenantSearchField;
import com.ryuqq.authhub.domain.tenant.vo.TenantSortKey;
import com.ryuqq.authhub.domain.tenant.vo.TenantStatus;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * TenantQueryFactory - 테넌트 SearchParams → Criteria 변환 Factory
 *
 * <p>Application Layer의 SearchParams DTO를 Domain Layer의 Criteria로 변환합니다.
 *
 * <p>DOM-CRI-010: Criteria는 DateRange, QueryContext 등 공통 VO 사용 권장.
 *
 * <p><strong>변환 책임:</strong>
 *
 * <ul>
 *   <li>searchField (String) → TenantSearchField enum
 *   <li>statuses (List&lt;String&gt;) → List&lt;TenantStatus&gt;
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
public class TenantQueryFactory {

    private final CommonVoFactory commonVoFactory;

    public TenantQueryFactory(CommonVoFactory commonVoFactory) {
        this.commonVoFactory = commonVoFactory;
    }

    /**
     * TenantSearchParams → TenantSearchCriteria 변환
     *
     * <p>CommonSearchParams.toQueryContext()를 사용하여 QueryContext 생성.
     *
     * <p>CommonVoFactory.createDateRange()를 사용하여 DateRange 생성.
     *
     * @param params Application SearchParams DTO
     * @return Domain Criteria
     */
    public TenantSearchCriteria toCriteria(TenantSearchParams params) {
        // CommonVoFactory를 사용하여 DateRange 생성
        DateRange dateRange = commonVoFactory.createDateRange(params.startDate(), params.endDate());

        // CommonSearchParams.toQueryContext()를 사용하여 QueryContext 생성
        QueryContext<TenantSortKey> queryContext =
                params.searchParams().toQueryContext(TenantSortKey.class);

        return new TenantSearchCriteria(
                params.searchWord(),
                parseSearchField(params.searchField()),
                parseStatuses(params.statuses()),
                dateRange,
                queryContext);
    }

    /**
     * 검색 필드 문자열 → TenantSearchField enum 변환
     *
     * <p>입력된 문자열을 TenantSearchField enum으로 변환합니다.
     *
     * @param searchField 검색 필드 문자열
     * @return TenantSearchField enum (기본: NAME)
     */
    private TenantSearchField parseSearchField(String searchField) {
        return TenantSearchField.fromString(searchField);
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
}
