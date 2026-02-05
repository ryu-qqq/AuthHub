package com.ryuqq.authhub.application.permission.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.authhub.application.permission.assembler.PermissionAssembler;
import com.ryuqq.authhub.application.permission.dto.query.PermissionSearchParams;
import com.ryuqq.authhub.application.permission.dto.response.PermissionPageResult;
import com.ryuqq.authhub.application.permission.factory.PermissionQueryFactory;
import com.ryuqq.authhub.application.permission.fixture.PermissionQueryFixtures;
import com.ryuqq.authhub.application.permission.manager.PermissionReadManager;
import com.ryuqq.authhub.domain.common.vo.DateRange;
import com.ryuqq.authhub.domain.permission.aggregate.Permission;
import com.ryuqq.authhub.domain.permission.fixture.PermissionFixture;
import com.ryuqq.authhub.domain.permission.query.criteria.PermissionSearchCriteria;
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
 * SearchPermissionsService 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("SearchPermissionsService 단위 테스트")
class SearchPermissionsServiceTest {

    @Mock private PermissionQueryFactory queryFactory;
    @Mock private PermissionReadManager readManager;

    private PermissionAssembler assembler = new PermissionAssembler();

    private SearchPermissionsService sut;

    @BeforeEach
    void setUp() {
        sut = new SearchPermissionsService(queryFactory, readManager, assembler);
    }

    @Nested
    @DisplayName("execute 메서드")
    class Execute {

        @Test
        @DisplayName(
                "성공: QueryFactory → ReadManager(findAll/count) → Assembler 순서로 호출 후 PageResult 반환")
        void shouldReturnPageResult_WhenCriteriaMatches() {
            // given
            PermissionSearchParams params = PermissionQueryFixtures.searchParams();
            PermissionSearchCriteria criteria =
                    PermissionSearchCriteria.ofDefault(
                            null, null, null, DateRange.of(null, null), 0, 20);
            List<Permission> permissions = List.of(PermissionFixture.create());
            long totalElements = 1L;

            given(queryFactory.toCriteria(params)).willReturn(criteria);
            given(readManager.findAllBySearchCriteria(criteria)).willReturn(permissions);
            given(readManager.countBySearchCriteria(criteria)).willReturn(totalElements);

            // when
            PermissionPageResult result = sut.execute(params);

            // then
            assertThat(result).isNotNull();
            assertThat(result.content()).hasSize(1);
            assertThat(result.pageMeta().totalElements()).isEqualTo(1L);
            assertThat(result.pageMeta().page()).isEqualTo(0);
            assertThat(result.pageMeta().size()).isEqualTo(20);

            then(queryFactory).should().toCriteria(params);
            then(readManager).should().findAllBySearchCriteria(criteria);
            then(readManager).should().countBySearchCriteria(criteria);
        }

        @Test
        @DisplayName("조건에 맞는 권한이 없으면 빈 content와 totalElements 0인 PageResult 반환")
        void shouldReturnEmptyPageResult_WhenNoMatch() {
            // given
            PermissionSearchParams params = PermissionQueryFixtures.searchParams();
            PermissionSearchCriteria criteria =
                    PermissionSearchCriteria.ofDefault(
                            null, null, null, DateRange.of(null, null), 0, 20);

            given(queryFactory.toCriteria(params)).willReturn(criteria);
            given(readManager.findAllBySearchCriteria(criteria)).willReturn(List.of());
            given(readManager.countBySearchCriteria(criteria)).willReturn(0L);

            // when
            PermissionPageResult result = sut.execute(params);

            // then
            assertThat(result.content()).isEmpty();
            assertThat(result.pageMeta().totalElements()).isZero();

            then(queryFactory).should().toCriteria(params);
            then(readManager).should().findAllBySearchCriteria(criteria);
            then(readManager).should().countBySearchCriteria(criteria);
        }
    }
}
