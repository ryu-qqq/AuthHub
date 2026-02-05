package com.ryuqq.authhub.domain.userrole.query.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.domain.common.vo.PageRequest;
import com.ryuqq.authhub.domain.common.vo.QueryContext;
import com.ryuqq.authhub.domain.common.vo.SortDirection;
import com.ryuqq.authhub.domain.role.id.RoleId;
import com.ryuqq.authhub.domain.user.id.UserId;
import com.ryuqq.authhub.domain.userrole.vo.UserRoleSortKey;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * UserRoleSearchCriteria Query Criteria 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("UserRoleSearchCriteria 테스트")
class UserRoleSearchCriteriaTest {

    private static final UserId USER_ID = UserId.of("01941234-5678-7000-8000-123456789001");
    private static final RoleId ROLE_ID = RoleId.of(1L);
    private static final PageRequest PAGE_REQUEST = PageRequest.of(0, 20);

    @Nested
    @DisplayName("UserRoleSearchCriteria 생성 테스트")
    class CreateTests {

        @Test
        @DisplayName("of() 팩토리 메서드로 생성한다")
        void shouldCreateViaFactoryMethod() {
            // given
            QueryContext<UserRoleSortKey> queryContext =
                    QueryContext.of(UserRoleSortKey.CREATED_AT, SortDirection.DESC, PAGE_REQUEST);

            // when
            UserRoleSearchCriteria criteria =
                    UserRoleSearchCriteria.of(
                            USER_ID,
                            null,
                            ROLE_ID,
                            null,
                            UserRoleSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PAGE_REQUEST);

            // then
            assertThat(criteria.userId()).isEqualTo(USER_ID);
            assertThat(criteria.roleId()).isEqualTo(ROLE_ID);
            assertThat(criteria.userIds()).isNull();
            assertThat(criteria.roleIds()).isNull();
        }

        @Test
        @DisplayName("ofUserId()로 생성한다")
        void shouldCreateViaOfUserId() {
            // when
            UserRoleSearchCriteria criteria = UserRoleSearchCriteria.ofUserId(USER_ID, 0, 20);

            // then
            assertThat(criteria.userId()).isEqualTo(USER_ID);
            assertThat(criteria.userIds()).isNull();
            assertThat(criteria.roleId()).isNull();
            assertThat(criteria.roleIds()).isNull();
            assertThat(criteria.sortKey()).isEqualTo(UserRoleSortKey.CREATED_AT);
            assertThat(criteria.sortDirection()).isEqualTo(SortDirection.DESC);
            assertThat(criteria.pageNumber()).isEqualTo(0);
            assertThat(criteria.size()).isEqualTo(20);
        }

        @Test
        @DisplayName("ofRoleId()로 생성한다")
        void shouldCreateViaOfRoleId() {
            // when
            UserRoleSearchCriteria criteria = UserRoleSearchCriteria.ofRoleId(ROLE_ID, 0, 20);

            // then
            assertThat(criteria.roleId()).isEqualTo(ROLE_ID);
            assertThat(criteria.userId()).isNull();
            assertThat(criteria.userIds()).isNull();
            assertThat(criteria.roleIds()).isNull();
            assertThat(criteria.sortKey()).isEqualTo(UserRoleSortKey.CREATED_AT);
            assertThat(criteria.sortDirection()).isEqualTo(SortDirection.DESC);
        }

        @Test
        @DisplayName("ofUserIds()로 생성한다")
        void shouldCreateViaOfUserIds() {
            // given
            List<UserId> userIds =
                    List.of(
                            UserId.of("01941234-5678-7000-8000-123456789001"),
                            UserId.of("01941234-5678-7000-8000-123456789002"));

            // when
            UserRoleSearchCriteria criteria = UserRoleSearchCriteria.ofUserIds(userIds, 0, 20);

            // then
            assertThat(criteria.userIds())
                    .containsExactly(
                            UserId.of("01941234-5678-7000-8000-123456789001"),
                            UserId.of("01941234-5678-7000-8000-123456789002"));
            assertThat(criteria.userId()).isNull();
            assertThat(criteria.roleId()).isNull();
            assertThat(criteria.roleIds()).isNull();
        }

        @Test
        @DisplayName("ofRoleIds()로 생성한다")
        void shouldCreateViaOfRoleIds() {
            // given
            List<RoleId> roleIds = List.of(RoleId.of(1L), RoleId.of(2L));

            // when
            UserRoleSearchCriteria criteria = UserRoleSearchCriteria.ofRoleIds(roleIds, 0, 20);

            // then
            assertThat(criteria.roleIds()).containsExactly(RoleId.of(1L), RoleId.of(2L));
            assertThat(criteria.userId()).isNull();
            assertThat(criteria.userIds()).isNull();
            assertThat(criteria.roleId()).isNull();
        }

        @Test
        @DisplayName("null userId로 생성할 수 있다")
        void shouldCreateWithNullUserId() {
            // when
            UserRoleSearchCriteria criteria =
                    UserRoleSearchCriteria.of(
                            null,
                            null,
                            ROLE_ID,
                            null,
                            UserRoleSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PAGE_REQUEST);

            // then
            assertThat(criteria.userId()).isNull();
        }

        @Test
        @DisplayName("null roleId로 생성할 수 있다")
        void shouldCreateWithNullRoleId() {
            // when
            UserRoleSearchCriteria criteria =
                    UserRoleSearchCriteria.of(
                            USER_ID,
                            null,
                            null,
                            null,
                            UserRoleSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PAGE_REQUEST);

            // then
            assertThat(criteria.roleId()).isNull();
        }
    }

    @Nested
    @DisplayName("UserRoleSearchCriteria Query 메서드 테스트")
    class QueryMethodTests {

        @Test
        @DisplayName("hasUserIdFilter()는 userId가 있으면 true를 반환한다")
        void hasUserIdFilterShouldReturnTrueWhenUserIdExists() {
            // given
            UserRoleSearchCriteria criteria =
                    UserRoleSearchCriteria.of(
                            USER_ID,
                            null,
                            null,
                            null,
                            UserRoleSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PAGE_REQUEST);

            // then
            assertThat(criteria.hasUserIdFilter()).isTrue();
        }

        @Test
        @DisplayName("hasUserIdFilter()는 userId가 null이면 false를 반환한다")
        void hasUserIdFilterShouldReturnFalseWhenUserIdIsNull() {
            // given
            UserRoleSearchCriteria criteria =
                    UserRoleSearchCriteria.of(
                            null,
                            null,
                            ROLE_ID,
                            null,
                            UserRoleSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PAGE_REQUEST);

            // then
            assertThat(criteria.hasUserIdFilter()).isFalse();
        }

        @Test
        @DisplayName("hasUserIdsFilter()는 userIds가 있으면 true를 반환한다")
        void hasUserIdsFilterShouldReturnTrueWhenUserIdsExist() {
            // given
            List<UserId> userIds = List.of(UserId.of("01941234-5678-7000-8000-123456789001"));
            UserRoleSearchCriteria criteria =
                    UserRoleSearchCriteria.of(
                            null,
                            userIds,
                            null,
                            null,
                            UserRoleSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PAGE_REQUEST);

            // then
            assertThat(criteria.hasUserIdsFilter()).isTrue();
        }

        @Test
        @DisplayName("hasUserIdsFilter()는 userIds가 null이면 false를 반환한다")
        void hasUserIdsFilterShouldReturnFalseWhenUserIdsIsNull() {
            // given
            UserRoleSearchCriteria criteria =
                    UserRoleSearchCriteria.of(
                            USER_ID,
                            null,
                            null,
                            null,
                            UserRoleSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PAGE_REQUEST);

            // then
            assertThat(criteria.hasUserIdsFilter()).isFalse();
        }

        @Test
        @DisplayName("hasUserIdsFilter()는 userIds가 빈 목록이면 false를 반환한다")
        void hasUserIdsFilterShouldReturnFalseWhenUserIdsIsEmpty() {
            // given
            UserRoleSearchCriteria criteria =
                    UserRoleSearchCriteria.of(
                            null,
                            List.of(),
                            null,
                            null,
                            UserRoleSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PAGE_REQUEST);

            // then
            assertThat(criteria.hasUserIdsFilter()).isFalse();
        }

        @Test
        @DisplayName("hasRoleIdFilter()는 roleId가 있으면 true를 반환한다")
        void hasRoleIdFilterShouldReturnTrueWhenRoleIdExists() {
            // given
            UserRoleSearchCriteria criteria =
                    UserRoleSearchCriteria.of(
                            null,
                            null,
                            ROLE_ID,
                            null,
                            UserRoleSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PAGE_REQUEST);

            // then
            assertThat(criteria.hasRoleIdFilter()).isTrue();
        }

        @Test
        @DisplayName("hasRoleIdFilter()는 roleId가 null이면 false를 반환한다")
        void hasRoleIdFilterShouldReturnFalseWhenRoleIdIsNull() {
            // given
            UserRoleSearchCriteria criteria =
                    UserRoleSearchCriteria.of(
                            USER_ID,
                            null,
                            null,
                            null,
                            UserRoleSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PAGE_REQUEST);

            // then
            assertThat(criteria.hasRoleIdFilter()).isFalse();
        }

        @Test
        @DisplayName("hasRoleIdsFilter()는 roleIds가 있으면 true를 반환한다")
        void hasRoleIdsFilterShouldReturnTrueWhenRoleIdsExist() {
            // given
            List<RoleId> roleIds = List.of(RoleId.of(1L), RoleId.of(2L));
            UserRoleSearchCriteria criteria =
                    UserRoleSearchCriteria.of(
                            null,
                            null,
                            null,
                            roleIds,
                            UserRoleSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PAGE_REQUEST);

            // then
            assertThat(criteria.hasRoleIdsFilter()).isTrue();
        }

        @Test
        @DisplayName("hasRoleIdsFilter()는 roleIds가 null이면 false를 반환한다")
        void hasRoleIdsFilterShouldReturnFalseWhenRoleIdsIsNull() {
            // given
            UserRoleSearchCriteria criteria =
                    UserRoleSearchCriteria.of(
                            USER_ID,
                            null,
                            null,
                            null,
                            UserRoleSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PAGE_REQUEST);

            // then
            assertThat(criteria.hasRoleIdsFilter()).isFalse();
        }

        @Test
        @DisplayName("hasRoleIdsFilter()는 roleIds가 빈 목록이면 false를 반환한다")
        void hasRoleIdsFilterShouldReturnFalseWhenRoleIdsIsEmpty() {
            // given
            UserRoleSearchCriteria criteria =
                    UserRoleSearchCriteria.of(
                            null,
                            null,
                            null,
                            List.of(),
                            UserRoleSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PAGE_REQUEST);

            // then
            assertThat(criteria.hasRoleIdsFilter()).isFalse();
        }

        @Test
        @DisplayName("pageNumber()는 queryContext의 page를 반환한다")
        void pageNumberShouldReturnQueryContextPage() {
            // given
            UserRoleSearchCriteria criteria =
                    UserRoleSearchCriteria.of(
                            USER_ID,
                            null,
                            null,
                            null,
                            UserRoleSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PAGE_REQUEST);

            // then
            assertThat(criteria.pageNumber()).isEqualTo(0);
        }

        @Test
        @DisplayName("size()는 queryContext의 size를 반환한다")
        void sizeShouldReturnQueryContextSize() {
            // given
            UserRoleSearchCriteria criteria =
                    UserRoleSearchCriteria.of(
                            USER_ID,
                            null,
                            null,
                            null,
                            UserRoleSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PAGE_REQUEST);

            // then
            assertThat(criteria.size()).isEqualTo(20);
        }

        @Test
        @DisplayName("offset()는 queryContext의 offset을 반환한다")
        void offsetShouldReturnQueryContextOffset() {
            // given
            UserRoleSearchCriteria criteria =
                    UserRoleSearchCriteria.of(
                            USER_ID,
                            null,
                            null,
                            null,
                            UserRoleSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PAGE_REQUEST);

            // then
            assertThat(criteria.offset()).isEqualTo(0L);
        }

        @Test
        @DisplayName("sortKey()는 queryContext의 sortKey를 반환한다")
        void sortKeyShouldReturnQueryContextSortKey() {
            // given
            UserRoleSearchCriteria criteria =
                    UserRoleSearchCriteria.of(
                            USER_ID,
                            null,
                            null,
                            null,
                            UserRoleSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PAGE_REQUEST);

            // then
            assertThat(criteria.sortKey()).isEqualTo(UserRoleSortKey.CREATED_AT);
        }

        @Test
        @DisplayName("sortDirection()는 queryContext의 sortDirection을 반환한다")
        void sortDirectionShouldReturnQueryContextSortDirection() {
            // given
            UserRoleSearchCriteria criteria =
                    UserRoleSearchCriteria.of(
                            USER_ID,
                            null,
                            null,
                            null,
                            UserRoleSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PAGE_REQUEST);

            // then
            assertThat(criteria.sortDirection()).isEqualTo(SortDirection.DESC);
        }
    }
}
