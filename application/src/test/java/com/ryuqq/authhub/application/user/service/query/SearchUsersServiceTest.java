package com.ryuqq.authhub.application.user.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.ryuqq.authhub.application.user.assembler.UserAssembler;
import com.ryuqq.authhub.application.user.dto.query.SearchUsersQuery;
import com.ryuqq.authhub.application.user.dto.response.UserResponse;
import com.ryuqq.authhub.application.user.manager.query.UserReadManager;
import com.ryuqq.authhub.domain.user.aggregate.User;
import com.ryuqq.authhub.domain.user.fixture.UserFixture;
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

    @Mock private UserReadManager readManager;

    @Mock private UserAssembler assembler;

    private SearchUsersService service;

    @BeforeEach
    void setUp() {
        service = new SearchUsersService(readManager, assembler);
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

            SearchUsersQuery query =
                    new SearchUsersQuery(UserFixture.defaultTenantUUID(), null, null, null, 0, 10);

            UserResponse response1 =
                    new UserResponse(
                            user1.userIdValue(),
                            user1.tenantIdValue(),
                            user1.organizationIdValue(),
                            user1.getIdentifier(),
                            user1.getUserStatus().name(),
                            user1.getCreatedAt(),
                            user1.getUpdatedAt());
            UserResponse response2 =
                    new UserResponse(
                            user2.userIdValue(),
                            user2.tenantIdValue(),
                            user2.organizationIdValue(),
                            user2.getIdentifier(),
                            user2.getUserStatus().name(),
                            user2.getCreatedAt(),
                            user2.getUpdatedAt());
            List<UserResponse> expectedResponses = List.of(response1, response2);

            given(readManager.search(query)).willReturn(users);
            given(assembler.toResponseList(users)).willReturn(expectedResponses);

            // when
            List<UserResponse> responses = service.execute(query);

            // then
            assertThat(responses).hasSize(2);
            assertThat(responses).isEqualTo(expectedResponses);
            verify(readManager).search(query);
            verify(assembler).toResponseList(users);
        }

        @Test
        @DisplayName("검색 결과가 없으면 빈 목록을 반환한다")
        void shouldReturnEmptyListWhenNoResults() {
            // given
            SearchUsersQuery query =
                    new SearchUsersQuery(
                            UserFixture.defaultTenantUUID(), null, "nonexistent", null, 0, 10);
            List<User> emptyList = List.of();

            given(readManager.search(query)).willReturn(emptyList);
            given(assembler.toResponseList(emptyList)).willReturn(List.of());

            // when
            List<UserResponse> responses = service.execute(query);

            // then
            assertThat(responses).isEmpty();
            verify(readManager).search(query);
            verify(assembler).toResponseList(emptyList);
        }
    }
}
