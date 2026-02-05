package com.ryuqq.authhub.application.user.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.authhub.application.user.assembler.UserAssembler;
import com.ryuqq.authhub.application.user.dto.query.UserSearchParams;
import com.ryuqq.authhub.application.user.dto.response.UserPageResult;
import com.ryuqq.authhub.application.user.dto.response.UserResult;
import com.ryuqq.authhub.application.user.factory.UserQueryFactory;
import com.ryuqq.authhub.application.user.fixture.UserQueryFixtures;
import com.ryuqq.authhub.application.user.manager.UserReadManager;
import com.ryuqq.authhub.domain.common.vo.DateRange;
import com.ryuqq.authhub.domain.user.aggregate.User;
import com.ryuqq.authhub.domain.user.fixture.UserFixture;
import com.ryuqq.authhub.domain.user.query.criteria.UserSearchCriteria;
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
 * SearchUsersService 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("SearchUsersService 단위 테스트")
class SearchUsersServiceTest {

    @Mock private UserQueryFactory queryFactory;
    @Mock private UserReadManager readManager;
    @Mock private UserAssembler assembler;

    private SearchUsersService sut;

    @BeforeEach
    void setUp() {
        sut = new SearchUsersService(queryFactory, readManager, assembler);
    }

    @Nested
    @DisplayName("execute 메서드")
    class Execute {

        @Test
        @DisplayName("성공: Factory → ReadManager → Assembler 순서로 호출하고 PageResult 반환")
        void shouldOrchestrate_FactoryThenManagerThenAssembler_AndReturnPageResult() {
            // given
            UserSearchParams params = UserQueryFixtures.searchParams();
            UserSearchCriteria criteria =
                    UserSearchCriteria.ofDefault(
                            List.of(UserFixture.defaultOrganizationId()),
                            null,
                            null,
                            DateRange.of(null, null),
                            0,
                            20);
            List<User> users = List.of(UserFixture.create());
            UserResult userResult =
                    new UserResult(
                            UserFixture.defaultIdString(),
                            UserFixture.defaultOrganizationIdString(),
                            UserFixture.defaultIdentifierString(),
                            null,
                            "ACTIVE",
                            java.time.Instant.now(),
                            java.time.Instant.now());
            UserPageResult expectedPage = UserPageResult.of(List.of(userResult), 0, 20, 1L);

            given(queryFactory.toCriteria(params)).willReturn(criteria);
            given(readManager.findAllBySearchCriteria(criteria)).willReturn(users);
            given(readManager.countBySearchCriteria(criteria)).willReturn(1L);
            given(assembler.toPageResult(users, 0, 20, 1L)).willReturn(expectedPage);

            // when
            UserPageResult result = sut.execute(params);

            // then
            assertThat(result).isEqualTo(expectedPage);
            assertThat(result.content()).hasSize(1);
            assertThat(result.pageMeta().totalElements()).isEqualTo(1L);
            then(queryFactory).should().toCriteria(params);
            then(readManager).should().findAllBySearchCriteria(criteria);
            then(readManager).should().countBySearchCriteria(criteria);
            then(assembler).should().toPageResult(users, 0, 20, 1L);
        }

        @Test
        @DisplayName("결과가 없으면 빈 PageResult 반환")
        void shouldReturnEmptyPageResult_WhenNoUsers() {
            // given
            UserSearchParams params = UserQueryFixtures.searchParams();
            UserSearchCriteria criteria =
                    UserSearchCriteria.ofDefault(
                            List.of(UserFixture.defaultOrganizationId()),
                            null,
                            null,
                            DateRange.of(null, null),
                            0,
                            20);
            UserPageResult emptyPage = UserPageResult.empty(20);

            given(queryFactory.toCriteria(params)).willReturn(criteria);
            given(readManager.findAllBySearchCriteria(criteria)).willReturn(List.of());
            given(readManager.countBySearchCriteria(criteria)).willReturn(0L);
            given(assembler.toPageResult(List.of(), 0, 20, 0L)).willReturn(emptyPage);

            // when
            UserPageResult result = sut.execute(params);

            // then
            assertThat(result).isEqualTo(emptyPage);
            assertThat(result.content()).isEmpty();
            assertThat(result.pageMeta().totalElements()).isEqualTo(0L);
        }
    }
}
