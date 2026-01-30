package com.ryuqq.authhub.application.permissionendpoint.factory;

import com.ryuqq.authhub.application.permissionendpoint.dto.query.PermissionEndpointSearchParams;
import com.ryuqq.authhub.domain.common.vo.DateRange;
import com.ryuqq.authhub.domain.common.vo.PageRequest;
import com.ryuqq.authhub.domain.common.vo.SortDirection;
import com.ryuqq.authhub.domain.permissionendpoint.query.criteria.PermissionEndpointSearchCriteria;
import com.ryuqq.authhub.domain.permissionendpoint.vo.HttpMethod;
import com.ryuqq.authhub.domain.permissionendpoint.vo.PermissionEndpointSearchField;
import com.ryuqq.authhub.domain.permissionendpoint.vo.PermissionEndpointSortKey;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * PermissionEndpointQueryFactory - PermissionEndpoint SearchParams → Criteria 변환 Factory
 *
 * <p>Application SearchParams를 Domain Criteria로 변환합니다.
 *
 * <p>F-002: Query Factory는 SearchParams → Criteria 변환 전용.
 *
 * <p>F-003: 기본값 처리 및 null 처리는 Factory에서 담당.
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class PermissionEndpointQueryFactory {

    // ==================== 기본값 상수 ====================
    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 20;
    private static final PermissionEndpointSortKey DEFAULT_SORT_KEY =
            PermissionEndpointSortKey.CREATED_AT;
    private static final SortDirection DEFAULT_SORT_DIRECTION = SortDirection.DESC;

    /**
     * SearchParams → SearchCriteria 변환
     *
     * @param params Application 검색 파라미터
     * @return Domain 검색 Criteria
     */
    public PermissionEndpointSearchCriteria toCriteria(PermissionEndpointSearchParams params) {
        return PermissionEndpointSearchCriteria.of(
                params.permissionIds(),
                params.searchWord(),
                parseSearchField(params.searchField()),
                parseHttpMethods(params.httpMethods()),
                parseDateRange(params),
                parseSortKey(params.sortKey()),
                parseSortDirection(params.sortDirection()),
                parsePageRequest(params));
    }

    // ==================== Private Methods ====================

    private PermissionEndpointSearchField parseSearchField(String searchField) {
        return PermissionEndpointSearchField.from(searchField);
    }

    private List<HttpMethod> parseHttpMethods(List<String> httpMethods) {
        if (httpMethods == null || httpMethods.isEmpty()) {
            return null;
        }
        return httpMethods.stream().map(HttpMethod::from).toList();
    }

    private DateRange parseDateRange(PermissionEndpointSearchParams params) {
        if (params.startDate() == null && params.endDate() == null) {
            return null;
        }
        return DateRange.of(params.startDate(), params.endDate());
    }

    private PermissionEndpointSortKey parseSortKey(String sortKey) {
        if (sortKey == null || sortKey.isBlank()) {
            return DEFAULT_SORT_KEY;
        }
        return PermissionEndpointSortKey.from(sortKey);
    }

    private SortDirection parseSortDirection(String sortDirection) {
        if (sortDirection == null || sortDirection.isBlank()) {
            return DEFAULT_SORT_DIRECTION;
        }
        return SortDirection.fromString(sortDirection);
    }

    private PageRequest parsePageRequest(PermissionEndpointSearchParams params) {
        int page = params.page() != null ? params.page() : DEFAULT_PAGE;
        int size = params.size() != null ? params.size() : DEFAULT_SIZE;
        return PageRequest.of(page, size);
    }
}
