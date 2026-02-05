package com.ryuqq.authhub.application.organization.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.authhub.application.organization.assembler.OrganizationAssembler;
import com.ryuqq.authhub.application.organization.dto.query.OrganizationSearchParams;
import com.ryuqq.authhub.application.organization.factory.OrganizationQueryFactory;
import com.ryuqq.authhub.application.organization.fixture.OrganizationQueryFixtures;
import com.ryuqq.authhub.application.organization.manager.OrganizationReadManager;
import com.ryuqq.authhub.domain.common.vo.DateRange;
import com.ryuqq.authhub.domain.organization.aggregate.Organization;
import com.ryuqq.authhub.domain.organization.fixture.OrganizationFixture;
import com.ryuqq.authhub.domain.organization.query.criteria.OrganizationSearchCriteria;
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
 * SearchOrganizationsByOffsetService 단위 테스트
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
@DisplayName("SearchOrganizationsByOffsetService 단위 테스트")
class SearchOrganizationsByOffsetServiceTest {

    @Mock private OrganizationQueryFactory queryFactory;

    @Mock private OrganizationReadManager readManager;

    @Mock private OrganizationAssembler assembler;

    private SearchOrganizationsByOffsetService sut;

    @BeforeEach
    void setUp() {
        sut = new SearchOrganizationsByOffsetService(queryFactory, readManager, assembler);
    }

    @Nested
    @DisplayName("execute 메서드")
    class Execute {

        @Test
        @DisplayName("성공: Factory → ReadManager → Assembler 순서로 호출하고 PageResult 반환")
        void shouldOrchestrate_FactoryThenReadManagerThenAssembler_AndReturnPageResult() {
            // given
            OrganizationSearchParams params = OrganizationQueryFixtures.searchParams();
            OrganizationSearchCriteria criteria =
                    OrganizationSearchCriteria.ofSimple(
                            List.of(OrganizationFixture.defaultTenantId()),
                            null,
                            Collections.emptyList(),
                            DateRange.of(null, null),
                            0,
                            10);
            List<Organization> organizations =
                    List.of(
                            OrganizationFixture.create(),
                            OrganizationFixture.createWithName("Org 2"));
            long totalCount = 2L;
            var expectedResult =
                    OrganizationQueryFixtures.pageResult(
                            List.of(
                                    OrganizationQueryFixtures.organizationResult(),
                                    OrganizationQueryFixtures.organizationResultWithName("Org 2")),
                            0,
                            10,
                            totalCount);

            given(queryFactory.toCriteria(params)).willReturn(criteria);
            given(readManager.findAllBySearchCriteria(criteria)).willReturn(organizations);
            given(readManager.countBySearchCriteria(criteria)).willReturn(totalCount);
            given(assembler.toPageResult(organizations, 0, 10, totalCount))
                    .willReturn(expectedResult);

            // when
            var result = sut.execute(params);

            // then
            assertThat(result).isEqualTo(expectedResult);
            assertThat(result.content()).hasSize(2);
            assertThat(result.pageMeta().totalElements()).isEqualTo(totalCount);

            then(queryFactory).should().toCriteria(params);
            then(readManager).should().findAllBySearchCriteria(criteria);
            then(readManager).should().countBySearchCriteria(criteria);
            then(assembler).should().toPageResult(organizations, 0, 10, totalCount);
        }

        @Test
        @DisplayName("조회 결과가 없으면 빈 PageResult 반환")
        void shouldReturnEmptyPageResult_WhenNoResults() {
            // given
            OrganizationSearchParams params = OrganizationQueryFixtures.searchParams();
            OrganizationSearchCriteria criteria =
                    OrganizationSearchCriteria.ofSimple(
                            null, null, Collections.emptyList(), DateRange.of(null, null), 0, 10);
            List<Organization> emptyList = Collections.emptyList();
            var emptyResult = OrganizationQueryFixtures.emptyPageResult();

            given(queryFactory.toCriteria(params)).willReturn(criteria);
            given(readManager.findAllBySearchCriteria(criteria)).willReturn(emptyList);
            given(readManager.countBySearchCriteria(criteria)).willReturn(0L);
            given(assembler.toPageResult(emptyList, 0, 10, 0L)).willReturn(emptyResult);

            // when
            var result = sut.execute(params);

            // then
            assertThat(result.content()).isEmpty();
            assertThat(result.pageMeta().totalElements()).isZero();
        }

        @Test
        @DisplayName("Factory를 통해 SearchParams를 Criteria로 변환")
        void shouldConvertParams_ThroughFactory() {
            // given
            OrganizationSearchParams params = OrganizationQueryFixtures.searchParams();
            OrganizationSearchCriteria criteria =
                    OrganizationSearchCriteria.ofSimple(
                            null, null, Collections.emptyList(), DateRange.of(null, null), 0, 10);

            given(queryFactory.toCriteria(params)).willReturn(criteria);
            given(readManager.findAllBySearchCriteria(criteria))
                    .willReturn(Collections.emptyList());
            given(readManager.countBySearchCriteria(criteria)).willReturn(0L);
            given(assembler.toPageResult(Collections.emptyList(), 0, 10, 0L))
                    .willReturn(OrganizationQueryFixtures.emptyPageResult());

            // when
            sut.execute(params);

            // then
            then(queryFactory).should().toCriteria(params);
        }
    }
}
