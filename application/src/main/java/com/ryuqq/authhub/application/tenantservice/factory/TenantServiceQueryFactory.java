package com.ryuqq.authhub.application.tenantservice.factory;

import com.ryuqq.authhub.application.common.factory.CommonVoFactory;
import com.ryuqq.authhub.application.tenantservice.dto.query.TenantServiceSearchParams;
import com.ryuqq.authhub.domain.common.vo.DateRange;
import com.ryuqq.authhub.domain.common.vo.QueryContext;
import com.ryuqq.authhub.domain.tenantservice.query.criteria.TenantServiceSearchCriteria;
import com.ryuqq.authhub.domain.tenantservice.vo.TenantServiceSortKey;
import com.ryuqq.authhub.domain.tenantservice.vo.TenantServiceStatus;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * TenantServiceQueryFactory - TenantService SearchParams -> Criteria 변환 Factory
 *
 * <p>Application Layer의 SearchParams DTO를 Domain Layer의 Criteria로 변환합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class TenantServiceQueryFactory {

    private final CommonVoFactory commonVoFactory;

    public TenantServiceQueryFactory(CommonVoFactory commonVoFactory) {
        this.commonVoFactory = commonVoFactory;
    }

    /**
     * TenantServiceSearchParams -> TenantServiceSearchCriteria 변환
     *
     * @param params Application SearchParams DTO
     * @return Domain Criteria
     */
    public TenantServiceSearchCriteria toCriteria(TenantServiceSearchParams params) {
        DateRange dateRange = commonVoFactory.createDateRange(params.startDate(), params.endDate());

        QueryContext<TenantServiceSortKey> queryContext =
                params.searchParams().toQueryContext(TenantServiceSortKey.class);

        return new TenantServiceSearchCriteria(
                params.tenantId(),
                params.serviceId(),
                parseStatuses(params.statuses()),
                dateRange,
                queryContext);
    }

    private List<TenantServiceStatus> parseStatuses(List<String> statuses) {
        if (statuses == null || statuses.isEmpty()) {
            return Collections.emptyList();
        }
        return statuses.stream()
                .map(String::toUpperCase)
                .map(TenantServiceStatus::valueOf)
                .toList();
    }
}
