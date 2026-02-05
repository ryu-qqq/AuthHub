package com.ryuqq.authhub.application.tenant.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.authhub.application.tenant.assembler.TenantAssembler;
import com.ryuqq.authhub.application.tenant.dto.query.TenantSearchParams;
import com.ryuqq.authhub.application.tenant.dto.response.TenantPageResult;
import com.ryuqq.authhub.application.tenant.dto.response.TenantResult;
import com.ryuqq.authhub.application.tenant.factory.TenantQueryFactory;
import com.ryuqq.authhub.application.tenant.fixture.TenantQueryFixtures;
import com.ryuqq.authhub.application.tenant.manager.TenantReadManager;
import com.ryuqq.authhub.domain.common.vo.DateRange;
import com.ryuqq.authhub.domain.tenant.aggregate.Tenant;
import com.ryuqq.authhub.domain.tenant.fixture.TenantFixture;
import com.ryuqq.authhub.domain.tenant.query.criteria.TenantSearchCriteria;
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
 * SearchTenantsByOffsetService 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("SearchTenantsByOffsetService 단위 테스트")
class SearchTenantsByOffsetServiceTest {

    @Mock private TenantQueryFactory queryFactory;

    @Mock private TenantReadManager readManager;

    @Mock private TenantAssembler assembler;

    private SearchTenantsByOffsetService sut;

    @BeforeEach
    void setUp() {
        sut = new SearchTenantsByOffsetService(queryFactory, readManager, assembler);
    }

    @Nested
    @DisplayName("execute 메서드")
    class Execute {

        @Test
        @DisplayName("성공: Factory → ReadManager → Assembler 순서로 호출하고 PageResult 반환")
        void shouldOrchestrate_FactoryThenManagerThenAssembler_AndReturnPageResult() {
            // given
            TenantSearchParams params = TenantQueryFixtures.searchParams();
            TenantSearchCriteria criteria =
                    TenantSearchCriteria.ofSimple(null, null, DateRange.of(null, null), 0, 20);
            List<Tenant> tenants = List.of(TenantFixture.create());
            TenantPageResult expectedPage =
                    TenantPageResult.of(
                            List.of(
                                    new TenantResult(
                                            TenantFixture.defaultIdString(),
                                            "Test",
                                            "ACTIVE",
                                            null,
                                            null)),
                            0,
                            20,
                            1L);

            given(queryFactory.toCriteria(params)).willReturn(criteria);
            given(readManager.findAllByCriteria(criteria)).willReturn(tenants);
            given(readManager.countByCriteria(criteria)).willReturn(1L);
            given(assembler.toPageResult(tenants, 0, 20, 1L)).willReturn(expectedPage);

            // when
            TenantPageResult result = sut.execute(params);

            // then
            assertThat(result).isEqualTo(expectedPage);
            then(queryFactory).should().toCriteria(params);
            then(readManager).should().findAllByCriteria(criteria);
            then(readManager).should().countByCriteria(criteria);
            then(assembler).should().toPageResult(tenants, 0, 20, 1L);
        }

        @Test
        @DisplayName("빈 결과: 목록이 비어있고 totalElements 0")
        void shouldReturnEmptyPage_WhenNoTenants() {
            // given
            TenantSearchParams params = TenantQueryFixtures.searchParams();
            TenantSearchCriteria criteria =
                    TenantSearchCriteria.ofSimple(null, null, DateRange.of(null, null), 0, 20);
            TenantPageResult emptyPage = TenantPageResult.empty(20);

            given(queryFactory.toCriteria(params)).willReturn(criteria);
            given(readManager.findAllByCriteria(criteria)).willReturn(Collections.emptyList());
            given(readManager.countByCriteria(criteria)).willReturn(0L);
            given(assembler.toPageResult(Collections.emptyList(), 0, 20, 0L)).willReturn(emptyPage);

            // when
            TenantPageResult result = sut.execute(params);

            // then
            assertThat(result).isEqualTo(emptyPage);
            assertThat(result.content()).isEmpty();
            assertThat(result.pageMeta().totalElements()).isZero();
        }
    }
}
