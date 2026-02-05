package com.ryuqq.authhub.application.service.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.authhub.application.service.assembler.ServiceAssembler;
import com.ryuqq.authhub.application.service.dto.query.ServiceSearchParams;
import com.ryuqq.authhub.application.service.dto.response.ServicePageResult;
import com.ryuqq.authhub.application.service.factory.ServiceQueryFactory;
import com.ryuqq.authhub.application.service.fixture.ServiceQueryFixtures;
import com.ryuqq.authhub.application.service.manager.ServiceReadManager;
import com.ryuqq.authhub.domain.common.vo.DateRange;
import com.ryuqq.authhub.domain.service.aggregate.Service;
import com.ryuqq.authhub.domain.service.fixture.ServiceFixture;
import com.ryuqq.authhub.domain.service.query.criteria.ServiceSearchCriteria;
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
 * SearchServicesService 단위 테스트
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
@DisplayName("SearchServicesService 단위 테스트")
class SearchServicesServiceTest {

    @Mock private ServiceQueryFactory queryFactory;

    @Mock private ServiceReadManager readManager;

    @Mock private ServiceAssembler assembler;

    private SearchServicesService sut;

    @BeforeEach
    void setUp() {
        sut = new SearchServicesService(queryFactory, readManager, assembler);
    }

    @Nested
    @DisplayName("execute 메서드")
    class Execute {

        @Test
        @DisplayName("성공: Factory → Manager → Assembler 순서로 호출하고 PageResult 반환")
        void shouldOrchestrate_FactoryThenManagerThenAssembler_AndReturnPageResult() {
            // given
            ServiceSearchParams params = ServiceQueryFixtures.searchParams();
            ServiceSearchCriteria criteria =
                    ServiceSearchCriteria.ofSimple(
                            null, Collections.emptyList(), DateRange.of(null, null), 0, 20);
            List<Service> services = List.of(ServiceFixture.create());
            long totalCount = 1L;
            ServicePageResult expectedResult = ServicePageResult.empty(20);

            given(queryFactory.toCriteria(params)).willReturn(criteria);
            given(readManager.findAllByCriteria(criteria)).willReturn(services);
            given(readManager.countByCriteria(criteria)).willReturn(totalCount);
            given(
                            assembler.toPageResult(
                                    services, criteria.pageNumber(), criteria.size(), totalCount))
                    .willReturn(expectedResult);

            // when
            ServicePageResult result = sut.execute(params);

            // then
            assertThat(result).isEqualTo(expectedResult);

            then(queryFactory).should().toCriteria(params);
            then(readManager).should().findAllByCriteria(criteria);
            then(readManager).should().countByCriteria(criteria);
            then(assembler)
                    .should()
                    .toPageResult(services, criteria.pageNumber(), criteria.size(), totalCount);
        }

        @Test
        @DisplayName("조회 결과가 없으면 빈 PageResult 반환")
        void shouldReturnEmptyPageResult_WhenNoResults() {
            // given
            ServiceSearchParams params = ServiceQueryFixtures.searchParams();
            ServiceSearchCriteria criteria =
                    ServiceSearchCriteria.ofSimple(
                            null, Collections.emptyList(), DateRange.of(null, null), 0, 20);
            List<Service> emptyList = Collections.emptyList();
            long totalCount = 0L;
            ServicePageResult emptyResult = ServicePageResult.empty(20);

            given(queryFactory.toCriteria(params)).willReturn(criteria);
            given(readManager.findAllByCriteria(criteria)).willReturn(emptyList);
            given(readManager.countByCriteria(criteria)).willReturn(totalCount);
            given(
                            assembler.toPageResult(
                                    emptyList, criteria.pageNumber(), criteria.size(), totalCount))
                    .willReturn(emptyResult);

            // when
            ServicePageResult result = sut.execute(params);

            // then
            assertThat(result).isEqualTo(emptyResult);
        }
    }
}
