package com.ryuqq.authhub.application.permission.factory;

import com.ryuqq.authhub.application.common.factory.CommonVoFactory;
import com.ryuqq.authhub.application.permission.dto.query.PermissionSearchParams;
import com.ryuqq.authhub.domain.common.vo.DateRange;
import com.ryuqq.authhub.domain.common.vo.QueryContext;
import com.ryuqq.authhub.domain.permission.query.criteria.PermissionSearchCriteria;
import com.ryuqq.authhub.domain.permission.vo.PermissionSearchField;
import com.ryuqq.authhub.domain.permission.vo.PermissionSortKey;
import com.ryuqq.authhub.domain.permission.vo.PermissionType;
import org.springframework.stereotype.Component;

/**
 * PermissionQueryFactory - 권한 SearchParams → Criteria 변환 Factory (Global Only)
 *
 * <p>Application Layer의 SearchParams DTO를 Domain Layer의 Criteria로 변환합니다.
 *
 * <p><strong>Global Only 설계:</strong>
 *
 * <ul>
 *   <li>모든 Permission은 전체 시스템에서 공유됩니다
 *   <li>테넌트 관련 변환 로직이 제거되었습니다
 * </ul>
 *
 * <p>DOM-CRI-010: Criteria는 DateRange, QueryContext 등 공통 VO 사용 권장.
 *
 * <p><strong>변환 책임:</strong>
 *
 * <ul>
 *   <li>searchField (String) → PermissionSearchField enum
 *   <li>types (List&lt;String&gt;) → List&lt;PermissionType&gt;
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
public class PermissionQueryFactory {

    private final CommonVoFactory commonVoFactory;

    public PermissionQueryFactory(CommonVoFactory commonVoFactory) {
        this.commonVoFactory = commonVoFactory;
    }

    /**
     * PermissionSearchParams → PermissionSearchCriteria 변환
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
    public PermissionSearchCriteria toCriteria(PermissionSearchParams params) {
        // CommonVoFactory를 사용하여 DateRange 생성
        DateRange dateRange = commonVoFactory.createDateRange(params.startDate(), params.endDate());

        // CommonSearchParams.toQueryContext()를 사용하여 QueryContext 생성
        QueryContext<PermissionSortKey> queryContext =
                params.searchParams().toQueryContext(PermissionSortKey.class);

        return new PermissionSearchCriteria(
                params.searchWord(),
                PermissionSearchField.fromString(params.searchField()),
                PermissionType.parseList(params.types()),
                params.resources(),
                dateRange,
                queryContext);
    }
}
