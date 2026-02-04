package com.ryuqq.authhub.application.service.factory;

import com.ryuqq.authhub.application.common.factory.CommonVoFactory;
import com.ryuqq.authhub.application.service.dto.query.ServiceSearchParams;
import com.ryuqq.authhub.domain.common.vo.DateRange;
import com.ryuqq.authhub.domain.common.vo.QueryContext;
import com.ryuqq.authhub.domain.service.query.criteria.ServiceSearchCriteria;
import com.ryuqq.authhub.domain.service.vo.ServiceSearchField;
import com.ryuqq.authhub.domain.service.vo.ServiceSortKey;
import com.ryuqq.authhub.domain.service.vo.ServiceStatus;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * ServiceQueryFactory - 서비스 SearchParams → Criteria 변환 Factory
 *
 * <p>Application Layer의 SearchParams DTO를 Domain Layer의 Criteria로 변환합니다.
 *
 * <p>DOM-CRI-010: Criteria는 DateRange, QueryContext 등 공통 VO 사용 권장.
 *
 * <p><strong>변환 책임:</strong>
 *
 * <ul>
 *   <li>searchField (String) → ServiceSearchField enum
 *   <li>statuses (List&lt;String&gt;) → List&lt;ServiceStatus&gt;
 *   <li>CommonSearchParams → DateRange (CommonVoFactory 사용)
 *   <li>CommonSearchParams → QueryContext (toQueryContext 사용)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class ServiceQueryFactory {

    private final CommonVoFactory commonVoFactory;

    public ServiceQueryFactory(CommonVoFactory commonVoFactory) {
        this.commonVoFactory = commonVoFactory;
    }

    /**
     * ServiceSearchParams → ServiceSearchCriteria 변환
     *
     * @param params Application SearchParams DTO
     * @return Domain Criteria
     */
    public ServiceSearchCriteria toCriteria(ServiceSearchParams params) {
        DateRange dateRange = commonVoFactory.createDateRange(params.startDate(), params.endDate());

        QueryContext<ServiceSortKey> queryContext =
                params.searchParams().toQueryContext(ServiceSortKey.class);

        return new ServiceSearchCriteria(
                params.searchWord(),
                ServiceSearchField.fromString(params.searchField()),
                parseStatuses(params.statuses()),
                dateRange,
                queryContext);
    }

    private List<ServiceStatus> parseStatuses(List<String> statuses) {
        if (statuses == null || statuses.isEmpty()) {
            return Collections.emptyList();
        }
        return statuses.stream().map(String::toUpperCase).map(ServiceStatus::valueOf).toList();
    }
}
