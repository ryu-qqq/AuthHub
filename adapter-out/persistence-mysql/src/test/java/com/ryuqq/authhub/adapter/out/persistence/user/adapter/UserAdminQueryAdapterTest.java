package com.ryuqq.authhub.adapter.out.persistence.user.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.ryuqq.authhub.adapter.out.persistence.user.repository.UserAdminQueryDslRepository;
import com.ryuqq.authhub.application.common.dto.response.PageResponse;
import com.ryuqq.authhub.application.user.dto.query.SearchUsersQuery;
import com.ryuqq.authhub.application.user.dto.response.UserDetailResponse;
import com.ryuqq.authhub.application.user.dto.response.UserSummaryResponse;
import com.ryuqq.authhub.domain.user.fixture.UserFixture;
import com.ryuqq.authhub.domain.user.identifier.UserId;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * UserAdminQueryAdapter 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("UserAdminQueryAdapter 단위 테스트")
class UserAdminQueryAdapterTest {

    @Mock private UserAdminQueryDslRepository repository;

    private UserAdminQueryAdapter adapter;

    private static final UUID USER_UUID = UserFixture.defaultUUID();
    private static final UUID TENANT_UUID = UserFixture.defaultTenantUUID();
    private static final UUID ORG_UUID = UserFixture.defaultOrganizationUUID();

    @BeforeEach
    void setUp() {
        adapter = new UserAdminQueryAdapter(repository);
    }

    @Nested
    @DisplayName("searchUsers 메서드")
    class SearchUsersTest {

        @Test
        @DisplayName("사용자 목록을 성공적으로 검색한다")
        void shouldSearchUsersSuccessfully() {
            // given
            SearchUsersQuery query = SearchUsersQuery.of(TENANT_UUID, ORG_UUID, null, null, 0, 20);
            UserSummaryResponse userSummary =
                    new UserSummaryResponse(
                            UUID.randomUUID(),
                            TENANT_UUID,
                            "테스트 테넌트",
                            ORG_UUID,
                            "테스트 조직",
                            "user@example.com",
                            "ACTIVE",
                            2,
                            Instant.now(),
                            Instant.now());

            given(repository.searchUsers(query)).willReturn(List.of(userSummary));
            given(repository.countUsers(query)).willReturn(1L);

            // when
            PageResponse<UserSummaryResponse> result = adapter.searchUsers(query);

            // then
            assertThat(result.content()).hasSize(1);
            assertThat(result.totalElements()).isEqualTo(1L);
            assertThat(result.page()).isZero();
            assertThat(result.size()).isEqualTo(20);
            verify(repository).searchUsers(query);
            verify(repository).countUsers(query);
        }

        @Test
        @DisplayName("검색 결과가 없으면 빈 페이지를 반환한다")
        void shouldReturnEmptyPageWhenNoResults() {
            // given
            SearchUsersQuery query =
                    SearchUsersQuery.of(TENANT_UUID, ORG_UUID, "nonexistent", null, 0, 20);

            given(repository.searchUsers(query)).willReturn(List.of());
            given(repository.countUsers(query)).willReturn(0L);

            // when
            PageResponse<UserSummaryResponse> result = adapter.searchUsers(query);

            // then
            assertThat(result.content()).isEmpty();
            assertThat(result.totalElements()).isZero();
        }
    }

    @Nested
    @DisplayName("findUserDetail 메서드")
    class FindUserDetailTest {

        @Test
        @DisplayName("사용자 상세 정보를 성공적으로 조회한다")
        void shouldFindUserDetailSuccessfully() {
            // given
            UserId userId = UserId.of(USER_UUID);
            UserDetailResponse detailResponse =
                    new UserDetailResponse(
                            USER_UUID,
                            TENANT_UUID,
                            "테스트 테넌트",
                            ORG_UUID,
                            "테스트 조직",
                            "user@example.com",
                            "ACTIVE",
                            List.of(),
                            Instant.now(),
                            Instant.now());

            given(repository.findUserDetail(USER_UUID)).willReturn(Optional.of(detailResponse));

            // when
            Optional<UserDetailResponse> result = adapter.findUserDetail(userId);

            // then
            assertThat(result).isPresent();
            assertThat(result.get().userId()).isEqualTo(USER_UUID);
            verify(repository).findUserDetail(USER_UUID);
        }

        @Test
        @DisplayName("존재하지 않는 사용자는 빈 Optional을 반환한다")
        void shouldReturnEmptyWhenUserNotFound() {
            // given
            UUID nonExistingUserId = UUID.randomUUID();
            UserId userId = UserId.of(nonExistingUserId);

            given(repository.findUserDetail(nonExistingUserId)).willReturn(Optional.empty());

            // when
            Optional<UserDetailResponse> result = adapter.findUserDetail(userId);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("countUsers 메서드")
    class CountUsersTest {

        @Test
        @DisplayName("사용자 수를 성공적으로 조회한다")
        void shouldCountUsersSuccessfully() {
            // given
            SearchUsersQuery query = SearchUsersQuery.of(TENANT_UUID, ORG_UUID, null, null, 0, 20);

            given(repository.countUsers(query)).willReturn(5L);

            // when
            long count = adapter.countUsers(query);

            // then
            assertThat(count).isEqualTo(5L);
            verify(repository).countUsers(query);
        }
    }
}
