package com.ryuqq.authhub.application.tenantservice.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.authhub.application.tenantservice.assembler.TenantServiceAssembler;
import com.ryuqq.authhub.application.tenantservice.dto.query.TenantServiceSearchParams;
import com.ryuqq.authhub.application.tenantservice.dto.response.TenantServicePageResult;
import com.ryuqq.authhub.application.tenantservice.factory.TenantServiceQueryFactory;
import com.ryuqq.authhub.application.tenantservice.fixture.TenantServiceQueryFixtures;
import com.ryuqq.authhub.application.tenantservice.manager.TenantServiceReadManager;
import com.ryuqq.authhub.domain.common.vo.DateRange;
import com.ryuqq.authhub.domain.common.vo.PageRequest;
import com.ryuqq.authhub.domain.common.vo.QueryContext;
import com.ryuqq.authhub.domain.common.vo.SortDirection;
import com.ryuqq.authhub.domain.tenantservice.aggregate.TenantService;
import com.ryuqq.authhub.domain.tenantservice.fixture.TenantServiceFixture;
import com.ryuqq.authhub.domain.tenantservice.query.criteria.TenantServiceSearchCriteria;
import com.ryuqq.authhub.domain.tenantservice.vo.TenantServiceSortKey;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * SearchTenantServicesService 단위 테스트
 *
 * <p><strong>테스트 설계 원칙:</strong>
 *
 * <ul>
 *   <li>Service는 오케스트레이션만 담당 → 협력 객체 호출 순서/조건 검증
 *   <li>비즈니스 로직은 Domain/Validator에서 테스트
 *   <li>BDDMockito 스타일로 Given-When-Then 구조 명확화
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("SearchTenantServicesService 단위 테스트")
class SearchTenantServicesServiceTest {

    @Mock private TenantServiceQueryFactory queryFactory;

    @Mock private TenantServiceReadManager readManager;

    @Mock private TenantServiceAssembler assembler;

    private SearchTenantServicesService sut;

    @BeforeEach
    void setUp() {
        sut = new SearchTenantServicesService(queryFactory, readManager, assembler);
    }

    @Nested
    @DisplayName("execute 메서드")
    class Execute {

        @Test
        @DisplayName("성공: Factory → Manager → Assembler 순서로 호출하고 PageResult 반환")
        void shouldOrchestrate_FactoryThenManagerThenAssembler_AndReturnPageResult() {
            // given
            TenantServiceSearchParams params = TenantServiceQueryFixtures.searchParams();
            TenantServiceSearchCriteria criteria =
                    new TenantServiceSearchCriteria(
                            null,
                            null,
                            Collections.emptyList(),
                            DateRange.of(null, null),
                            QueryContext.of(
                                    TenantServiceSortKey.CREATED_AT,
                                    SortDirection.DESC,
                                    PageRequest.of(0, 10)));
            List<TenantService> tenantServices =
                    List.of(
                            TenantServiceFixture.create(),
                            TenantServiceFixture.createWithStatus(
                                    com.ryuqq.authhub.domain.tenantservice.vo.TenantServiceStatus
                                            .INACTIVE));
            long totalCount = 2L;
            TenantServicePageResult expectedResult =
                    TenantServiceQueryFixtures.pageResult(
                            List.of(
                                    TenantServiceQueryFixtures.tenantServiceResult(),
                                    TenantServiceQueryFixtures.tenantServiceResultWithStatus(
                                            "INACTIVE")),
                            0,
                            10,
                            totalCount);

            given(queryFactory.toCriteria(params)).willReturn(criteria);
            given(readManager.findAllByCriteria(criteria)).willReturn(tenantServices);
            given(readManager.countByCriteria(criteria)).willReturn(totalCount);
            given(assembler.toPageResult(tenantServices, 0, 10, totalCount))
                    .willReturn(expectedResult);

            // when
            TenantServicePageResult result = sut.execute(params);

            // then
            assertThat(result).isEqualTo(expectedResult);
            assertThat(result.content()).hasSize(2);
            assertThat(result.pageMeta().totalElements()).isEqualTo(totalCount);

            then(queryFactory).should().toCriteria(params);
            then(readManager).should().findAllByCriteria(criteria);
            then(readManager).should().countByCriteria(criteria);
            then(assembler).should().toPageResult(tenantServices, 0, 10, totalCount);
        }

        @Test
        @DisplayName("조회 결과가 없으면 빈 PageResult 반환")
        void shouldReturnEmptyPageResult_WhenNoResults() {
            // given
            TenantServiceSearchParams params = TenantServiceQueryFixtures.searchParams();
            TenantServiceSearchCriteria criteria =
                    new TenantServiceSearchCriteria(
                            null,
                            null,
                            Collections.emptyList(),
                            DateRange.of(null, null),
                            QueryContext.of(
                                    TenantServiceSortKey.CREATED_AT,
                                    SortDirection.DESC,
                                    PageRequest.of(0, 10)));
            List<TenantService> emptyList = Collections.emptyList();
            TenantServicePageResult emptyResult = TenantServiceQueryFixtures.emptyPageResult();

            given(queryFactory.toCriteria(params)).willReturn(criteria);
            given(readManager.findAllByCriteria(criteria)).willReturn(emptyList);
            given(readManager.countByCriteria(criteria)).willReturn(0L);
            given(assembler.toPageResult(emptyList, 0, 10, 0L)).willReturn(emptyResult);

            // when
            TenantServicePageResult result = sut.execute(params);

            // then
            assertThat(result.content()).isEmpty();
            assertThat(result.pageMeta().totalElements()).isZero();
        }

        @Test
        @DisplayName("Factory를 통해 SearchParams를 Criteria로 변환")
        void shouldConvertParams_ThroughFactory() {
            // given
            TenantServiceSearchParams params = TenantServiceQueryFixtures.searchParams();
            TenantServiceSearchCriteria criteria =
                    new TenantServiceSearchCriteria(
                            null,
                            null,
                            Collections.emptyList(),
                            DateRange.of(null, null),
                            QueryContext.of(
                                    TenantServiceSortKey.CREATED_AT,
                                    SortDirection.DESC,
                                    PageRequest.of(0, 10)));

            given(queryFactory.toCriteria(params)).willReturn(criteria);
            given(readManager.findAllByCriteria(criteria)).willReturn(Collections.emptyList());
            given(readManager.countByCriteria(criteria)).willReturn(0L);

            // when
            sut.execute(params);

            // then
            then(queryFactory).should().toCriteria(params);
        }
    }
}
