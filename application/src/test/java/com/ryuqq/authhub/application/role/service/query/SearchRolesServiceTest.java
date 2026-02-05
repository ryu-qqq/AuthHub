package com.ryuqq.authhub.application.role.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.authhub.application.role.assembler.RoleAssembler;
import com.ryuqq.authhub.application.role.dto.query.RoleSearchParams;
import com.ryuqq.authhub.application.role.dto.response.RolePageResult;
import com.ryuqq.authhub.application.role.factory.RoleQueryFactory;
import com.ryuqq.authhub.application.role.fixture.RoleQueryFixtures;
import com.ryuqq.authhub.application.role.manager.RoleReadManager;
import com.ryuqq.authhub.domain.common.vo.DateRange;
import com.ryuqq.authhub.domain.role.aggregate.Role;
import com.ryuqq.authhub.domain.role.fixture.RoleFixture;
import com.ryuqq.authhub.domain.role.query.criteria.RoleSearchCriteria;
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
 * SearchRolesService 단위 테스트
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
@DisplayName("SearchRolesService 단위 테스트")
class SearchRolesServiceTest {

    @Mock private RoleQueryFactory queryFactory;
    @Mock private RoleReadManager readManager;
    @Mock private RoleAssembler assembler;

    private SearchRolesService sut;

    @BeforeEach
    void setUp() {
        sut = new SearchRolesService(queryFactory, readManager, assembler);
    }

    @Nested
    @DisplayName("execute 메서드")
    class Execute {

        @Test
        @DisplayName("성공: Factory → Manager → Assembler 순서로 호출하고 PageResult 반환")
        void shouldOrchestrate_FactoryThenManagerThenAssembler_AndReturnPageResult() {
            // given
            RoleSearchParams params = RoleQueryFixtures.searchParams();
            RoleSearchCriteria criteria =
                    RoleSearchCriteria.ofGlobal(
                            null, Collections.emptyList(), DateRange.of(null, null), 0, 20);
            List<Role> roles = List.of(RoleFixture.create());
            long totalCount = 1L;
            RolePageResult expectedResult = RolePageResult.empty(20);

            given(queryFactory.toCriteria(params)).willReturn(criteria);
            given(readManager.findAllBySearchCriteria(criteria)).willReturn(roles);
            given(readManager.countBySearchCriteria(criteria)).willReturn(totalCount);
            given(assembler.toPageResult(roles, criteria.pageNumber(), criteria.size(), totalCount))
                    .willReturn(expectedResult);

            // when
            RolePageResult result = sut.execute(params);

            // then
            assertThat(result).isEqualTo(expectedResult);

            then(queryFactory).should().toCriteria(params);
            then(readManager).should().findAllBySearchCriteria(criteria);
            then(readManager).should().countBySearchCriteria(criteria);
            then(assembler)
                    .should()
                    .toPageResult(roles, criteria.pageNumber(), criteria.size(), totalCount);
        }

        @Test
        @DisplayName("조회 결과가 없으면 빈 PageResult 반환")
        void shouldReturnEmptyPageResult_WhenNoResults() {
            // given
            RoleSearchParams params = RoleQueryFixtures.searchParams();
            RoleSearchCriteria criteria =
                    RoleSearchCriteria.ofGlobal(
                            null, Collections.emptyList(), DateRange.of(null, null), 0, 20);
            List<Role> emptyList = Collections.emptyList();
            long totalCount = 0L;
            RolePageResult emptyResult = RolePageResult.empty(20);

            given(queryFactory.toCriteria(params)).willReturn(criteria);
            given(readManager.findAllBySearchCriteria(criteria)).willReturn(emptyList);
            given(readManager.countBySearchCriteria(criteria)).willReturn(totalCount);
            given(
                            assembler.toPageResult(
                                    emptyList, criteria.pageNumber(), criteria.size(), totalCount))
                    .willReturn(emptyResult);

            // when
            RolePageResult result = sut.execute(params);

            // then
            assertThat(result).isEqualTo(emptyResult);
        }
    }
}
