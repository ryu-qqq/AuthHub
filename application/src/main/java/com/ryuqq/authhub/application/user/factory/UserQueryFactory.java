package com.ryuqq.authhub.application.user.factory;

import com.ryuqq.authhub.application.common.factory.CommonVoFactory;
import com.ryuqq.authhub.application.user.dto.query.UserSearchParams;
import com.ryuqq.authhub.domain.common.vo.DateRange;
import com.ryuqq.authhub.domain.common.vo.QueryContext;
import com.ryuqq.authhub.domain.organization.id.OrganizationId;
import com.ryuqq.authhub.domain.user.query.criteria.UserSearchCriteria;
import com.ryuqq.authhub.domain.user.vo.UserSearchField;
import com.ryuqq.authhub.domain.user.vo.UserSortKey;
import com.ryuqq.authhub.domain.user.vo.UserStatus;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * UserQueryFactory - 사용자 SearchParams → Criteria 변환 Factory
 *
 * <p>Application Layer의 SearchParams DTO를 Domain Layer의 Criteria로 변환합니다.
 *
 * <p>DOM-CRI-010: Criteria는 DateRange, QueryContext 등 공통 VO 사용 권장.
 *
 * <p><strong>변환 책임:</strong>
 *
 * <ul>
 *   <li>organizationId (String) → List&lt;OrganizationId&gt;
 *   <li>searchField (String) → UserSearchField enum
 *   <li>statuses (List&lt;String&gt;) → List&lt;UserStatus&gt;
 *   <li>CommonSearchParams → DateRange (CommonVoFactory 사용)
 *   <li>CommonSearchParams → QueryContext (toQueryContext 사용)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class UserQueryFactory {

    private final CommonVoFactory commonVoFactory;

    public UserQueryFactory(CommonVoFactory commonVoFactory) {
        this.commonVoFactory = commonVoFactory;
    }

    /**
     * UserSearchParams → UserSearchCriteria 변환
     *
     * @param params Application SearchParams DTO
     * @return Domain Criteria
     */
    public UserSearchCriteria toCriteria(UserSearchParams params) {
        // CommonVoFactory를 사용하여 DateRange 생성
        DateRange dateRange = commonVoFactory.createDateRange(params.startDate(), params.endDate());

        // CommonSearchParams.toQueryContext()를 사용하여 QueryContext 생성
        QueryContext<UserSortKey> queryContext =
                params.searchParams().toQueryContext(UserSortKey.class);

        return new UserSearchCriteria(
                parseOrganizationIds(params.organizationId()),
                params.searchWord(),
                parseSearchField(params.searchField()),
                parseStatuses(params.statuses()),
                dateRange,
                queryContext);
    }

    private List<OrganizationId> parseOrganizationIds(String organizationId) {
        if (organizationId == null || organizationId.isBlank()) {
            return List.of();
        }
        return List.of(OrganizationId.of(organizationId));
    }

    private UserSearchField parseSearchField(String searchField) {
        if (searchField == null || searchField.isBlank()) {
            return UserSearchField.IDENTIFIER;
        }
        try {
            return UserSearchField.valueOf(searchField.toUpperCase());
        } catch (IllegalArgumentException e) {
            return UserSearchField.IDENTIFIER;
        }
    }

    private List<UserStatus> parseStatuses(List<String> statuses) {
        if (statuses == null || statuses.isEmpty()) {
            return List.of();
        }
        return statuses.stream().map(UserStatus::valueOf).toList();
    }
}
