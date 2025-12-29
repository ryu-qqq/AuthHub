package com.ryuqq.authhub.application.user.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.ryuqq.authhub.application.common.dto.response.PageResponse;
import com.ryuqq.authhub.application.user.assembler.UserAssembler;
import com.ryuqq.authhub.application.user.dto.query.SearchUsersQuery;
import com.ryuqq.authhub.application.user.dto.response.UserResponse;
import com.ryuqq.authhub.application.user.factory.query.UserQueryFactory;
import com.ryuqq.authhub.application.user.manager.query.UserReadManager;
import com.ryuqq.authhub.domain.user.aggregate.User;
import com.ryuqq.authhub.domain.user.fixture.UserFixture;
import com.ryuqq.authhub.domain.user.query.criteria.UserCriteria;
import java.time.Instant;
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

    private SearchUsersService service;

    @BeforeEach
    void setUp() {
        service = new SearchUsersService(queryFactory, readManager, assembler);
    }

    @Nested
    @DisplayName("execute 메서드")
    class ExecuteTest {

        @Test
        @DisplayName("사용자 목록을 성공적으로 검색한다")
        void shouldSearchUsersSuccessfully() {
            // given
            User user1 = UserFixture.create();
            User user2 = UserFixture.createWithIdentifier("another@example.com");
            List<User> users = List.of(user1, user2);

            Instant createdFrom = Instant.parse("2025-01-01T00:00:00Z");
            Instant createdTo = Instant.parse("2025-12-31T23:59:59Z");
            SearchUsersQuery query =
                    SearchUsersQuery.of(
                            UserFixture.defaultTenantUUID(),
                            null,
                            null,
                            null,
                            createdFrom,
                            createdTo,
                            0,
                            10);

            UserCriteria criteria =
                    UserCriteria.ofSimple(
                            UserFixture.defaultTenantUUID(), null, null, null, null, 0, 10);

            UserResponse response1 =
                    new UserResponse(
                            user1.userIdValue(),
                            user1.tenantIdValue(),
                            user1.organizationIdValue(),
                            user1.getIdentifier(),
                            user1.getPhoneNumber(),
                            user1.getUserStatus().name(),
                            user1.getCreatedAt(),
                            user1.getUpdatedAt());
            UserResponse response2 =
                    new UserResponse(
                            user2.userIdValue(),
                            user2.tenantIdValue(),
                            user2.organizationIdValue(),
                            user2.getIdentifier(),
                            user2.getPhoneNumber(),
                            user2.getUserStatus().name(),
                            user2.getCreatedAt(),
                            user2.getUpdatedAt());
            List<UserResponse> expectedResponses = List.of(response1, response2);

            given(queryFactory.toCriteria(query)).willReturn(criteria);
            given(readManager.findAllByCriteria(criteria)).willReturn(users);
            given(readManager.countByCriteria(criteria)).willReturn(2L);
            given(assembler.toResponseList(users)).willReturn(expectedResponses);

            // when
            PageResponse<UserResponse> pageResponse = service.execute(query);

            // then
            assertThat(pageResponse.content()).hasSize(2);
            assertThat(pageResponse.content()).isEqualTo(expectedResponses);
            assertThat(pageResponse.totalElements()).isEqualTo(2);
            assertThat(pageResponse.totalPages()).isEqualTo(1);
            assertThat(pageResponse.first()).isTrue();
            assertThat(pageResponse.last()).isTrue();
            verify(queryFactory).toCriteria(query);
            verify(readManager).findAllByCriteria(criteria);
            verify(readManager).countByCriteria(criteria);
            verify(assembler).toResponseList(users);
        }

        @Test
        @DisplayName("검색 결과가 없으면 빈 목록을 반환한다")
        void shouldReturnEmptyListWhenNoResults() {
            // given
            Instant createdFrom = Instant.parse("2025-01-01T00:00:00Z");
            Instant createdTo = Instant.parse("2025-12-31T23:59:59Z");
            SearchUsersQuery query =
                    SearchUsersQuery.of(
                            UserFixture.defaultTenantUUID(),
                            null,
                            "nonexistent",
                            null,
                            createdFrom,
                            createdTo,
                            0,
                            10);

            UserCriteria criteria =
                    UserCriteria.ofSimple(
                            UserFixture.defaultTenantUUID(),
                            null,
                            "nonexistent",
                            null,
                            null,
                            0,
                            10);

            List<User> emptyList = List.of();

            given(queryFactory.toCriteria(query)).willReturn(criteria);
            given(readManager.findAllByCriteria(criteria)).willReturn(emptyList);
            given(readManager.countByCriteria(criteria)).willReturn(0L);
            given(assembler.toResponseList(emptyList)).willReturn(List.of());

            // when
            PageResponse<UserResponse> pageResponse = service.execute(query);

            // then
            assertThat(pageResponse.content()).isEmpty();
            assertThat(pageResponse.totalElements()).isZero();
            assertThat(pageResponse.totalPages()).isZero();
            verify(queryFactory).toCriteria(query);
            verify(readManager).findAllByCriteria(criteria);
            verify(readManager).countByCriteria(criteria);
            verify(assembler).toResponseList(emptyList);
        }
    }
}
