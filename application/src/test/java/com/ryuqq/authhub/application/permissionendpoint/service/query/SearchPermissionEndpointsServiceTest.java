package com.ryuqq.authhub.application.permissionendpoint.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.authhub.application.permissionendpoint.assembler.PermissionEndpointAssembler;
import com.ryuqq.authhub.application.permissionendpoint.dto.query.PermissionEndpointSearchParams;
import com.ryuqq.authhub.application.permissionendpoint.dto.response.PermissionEndpointPageResult;
import com.ryuqq.authhub.application.permissionendpoint.factory.PermissionEndpointQueryFactory;
import com.ryuqq.authhub.application.permissionendpoint.fixture.PermissionEndpointQueryFixtures;
import com.ryuqq.authhub.application.permissionendpoint.manager.PermissionEndpointReadManager;
import com.ryuqq.authhub.domain.permissionendpoint.aggregate.PermissionEndpoint;
import com.ryuqq.authhub.domain.permissionendpoint.fixture.PermissionEndpointFixture;
import com.ryuqq.authhub.domain.permissionendpoint.query.criteria.PermissionEndpointSearchCriteria;
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
 * SearchPermissionEndpointsService 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("SearchPermissionEndpointsService 단위 테스트")
class SearchPermissionEndpointsServiceTest {

    @Mock private PermissionEndpointQueryFactory queryFactory;
    @Mock private PermissionEndpointReadManager readManager;
    @Mock private PermissionEndpointAssembler assembler;

    private SearchPermissionEndpointsService sut;

    @BeforeEach
    void setUp() {
        sut = new SearchPermissionEndpointsService(queryFactory, readManager, assembler);
    }

    @Nested
    @DisplayName("search 메서드")
    class Search {

        @Test
        @DisplayName("성공: Factory → ReadManager → Assembler 순서로 PageResult 반환")
        void shouldReturnPageResult() {
            PermissionEndpointSearchParams params = PermissionEndpointQueryFixtures.searchParams();
            PermissionEndpointSearchCriteria criteria =
                    PermissionEndpointSearchCriteria.forPermission(
                            PermissionEndpointFixture.defaultPermissionIdValue(), 0, 20);
            List<PermissionEndpoint> permissionEndpoints =
                    List.of(PermissionEndpointFixture.create());
            PermissionEndpointPageResult expected =
                    PermissionEndpointPageResult.of(List.of(), 0, 20, 1L);

            given(queryFactory.toCriteria(params)).willReturn(criteria);
            given(readManager.findAllBySearchCriteria(criteria)).willReturn(permissionEndpoints);
            given(readManager.countBySearchCriteria(criteria)).willReturn(1L);
            given(assembler.toPageResult(permissionEndpoints, 0, 20, 1L)).willReturn(expected);

            PermissionEndpointPageResult result = sut.search(params);

            assertThat(result).isEqualTo(expected);
            then(queryFactory).should().toCriteria(params);
            then(readManager).should().findAllBySearchCriteria(criteria);
            then(readManager).should().countBySearchCriteria(criteria);
            then(assembler).should().toPageResult(permissionEndpoints, 0, 20, 1L);
        }

        @Test
        @DisplayName("성공: 결과가 없으면 빈 PageResult 반환")
        void shouldReturnEmptyPageResult_WhenNoResults() {
            PermissionEndpointSearchParams params = PermissionEndpointQueryFixtures.searchParams();
            PermissionEndpointSearchCriteria criteria =
                    PermissionEndpointSearchCriteria.forPermission(1L, 0, 20);
            List<PermissionEndpoint> emptyList = List.of();
            PermissionEndpointPageResult expected =
                    PermissionEndpointPageResult.of(List.of(), 0, 20, 0L);

            given(queryFactory.toCriteria(params)).willReturn(criteria);
            given(readManager.findAllBySearchCriteria(criteria)).willReturn(emptyList);
            given(readManager.countBySearchCriteria(criteria)).willReturn(0L);
            given(assembler.toPageResult(emptyList, 0, 20, 0L)).willReturn(expected);

            PermissionEndpointPageResult result = sut.search(params);

            assertThat(result.content()).isEmpty();
            assertThat(result.pageMeta().totalElements()).isEqualTo(0L);
        }
    }
}
