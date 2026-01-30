package com.ryuqq.authhub.application.role.factory;

import com.ryuqq.authhub.application.common.factory.CommonVoFactory;
import com.ryuqq.authhub.application.role.dto.query.RoleSearchParams;
import com.ryuqq.authhub.domain.common.vo.DateRange;
import com.ryuqq.authhub.domain.common.vo.QueryContext;
import com.ryuqq.authhub.domain.role.query.criteria.RoleSearchCriteria;
import com.ryuqq.authhub.domain.role.vo.RoleSearchField;
import com.ryuqq.authhub.domain.role.vo.RoleSortKey;
import com.ryuqq.authhub.domain.role.vo.RoleType;
import com.ryuqq.authhub.domain.tenant.id.TenantId;
import org.springframework.stereotype.Component;

/**
 * RoleQueryFactory - 역할 SearchParams → Criteria 변환 Factory
 *
 * <p>Application Layer의 SearchParams DTO를 Domain Layer의 Criteria로 변환합니다.
 *
 * <p>DOM-CRI-010: Criteria는 DateRange, QueryContext 등 공통 VO 사용 권장.
 *
 * <p><strong>변환 책임:</strong>
 *
 * <ul>
 *   <li>tenantId (String) → TenantId
 *   <li>searchField (String) → RoleSearchField enum
 *   <li>types (List&lt;String&gt;) → List&lt;RoleType&gt;
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
public class RoleQueryFactory {

    private final CommonVoFactory commonVoFactory;

    public RoleQueryFactory(CommonVoFactory commonVoFactory) {
        this.commonVoFactory = commonVoFactory;
    }

    /**
     * RoleSearchParams → RoleSearchCriteria 변환
     *
     * <p>CommonSearchParams.toQueryContext()를 사용하여 QueryContext 생성.
     *
     * <p>CommonVoFactory.createDateRange()를 사용하여 DateRange 생성.
     *
     * <p>파싱 로직은 각 도메인 객체가 담당합니다.
     *
     * @param params Application SearchParams DTO
     * @return Domain Criteria
     */
    public RoleSearchCriteria toCriteria(RoleSearchParams params) {
        // CommonVoFactory를 사용하여 DateRange 생성
        DateRange dateRange = commonVoFactory.createDateRange(params.startDate(), params.endDate());

        // CommonSearchParams.toQueryContext()를 사용하여 QueryContext 생성
        QueryContext<RoleSortKey> queryContext =
                params.searchParams().toQueryContext(RoleSortKey.class);

        return new RoleSearchCriteria(
                TenantId.fromNullable(params.tenantId()),
                params.searchWord(),
                RoleSearchField.fromString(params.searchField()),
                RoleType.parseList(params.types()),
                dateRange,
                queryContext);
    }
}
