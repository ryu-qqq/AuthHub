package com.ryuqq.authhub.domain.rolepermission.query.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.domain.common.vo.PageRequest;
import com.ryuqq.authhub.domain.common.vo.QueryContext;
import com.ryuqq.authhub.domain.common.vo.SortDirection;
import com.ryuqq.authhub.domain.permission.id.PermissionId;
import com.ryuqq.authhub.domain.role.id.RoleId;
import com.ryuqq.authhub.domain.rolepermission.vo.RolePermissionSortKey;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * RolePermissionSearchCriteria Query Criteria 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("RolePermissionSearchCriteria 테스트")
class RolePermissionSearchCriteriaTest {

    private static final RoleId ROLE_ID = RoleId.of(1L);
    private static final PermissionId PERMISSION_ID = PermissionId.of(1L);
    private static final PageRequest PAGE_REQUEST = PageRequest.of(0, 20);

    @Nested
    @DisplayName("RolePermissionSearchCriteria 생성 테스트")
    class CreateTests {

        @Test
        @DisplayName("of() 팩토리 메서드로 생성한다")
        void shouldCreateViaFactoryMethod() {
            // given
            QueryContext<RolePermissionSortKey> queryContext =
                    QueryContext.of(
                            RolePermissionSortKey.CREATED_AT, SortDirection.DESC, PAGE_REQUEST);

            // when
            RolePermissionSearchCriteria criteria =
                    RolePermissionSearchCriteria.of(
                            ROLE_ID,
                            null,
                            PERMISSION_ID,
                            null,
                            RolePermissionSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PAGE_REQUEST);

            // then
            assertThat(criteria.roleId()).isEqualTo(ROLE_ID);
            assertThat(criteria.permissionId()).isEqualTo(PERMISSION_ID);
            assertThat(criteria.roleIds()).isNull();
            assertThat(criteria.permissionIds()).isNull();
        }

        @Test
        @DisplayName("ofRoleId()로 생성한다")
        void shouldCreateViaOfRoleId() {
            // when
            RolePermissionSearchCriteria criteria =
                    RolePermissionSearchCriteria.ofRoleId(ROLE_ID, 0, 20);

            // then
            assertThat(criteria.roleId()).isEqualTo(ROLE_ID);
            assertThat(criteria.roleIds()).isNull();
            assertThat(criteria.permissionId()).isNull();
            assertThat(criteria.permissionIds()).isNull();
            assertThat(criteria.sortKey()).isEqualTo(RolePermissionSortKey.CREATED_AT);
            assertThat(criteria.sortDirection()).isEqualTo(SortDirection.DESC);
            assertThat(criteria.pageNumber()).isEqualTo(0);
            assertThat(criteria.size()).isEqualTo(20);
        }

        @Test
        @DisplayName("ofPermissionId()로 생성한다")
        void shouldCreateViaOfPermissionId() {
            // when
            RolePermissionSearchCriteria criteria =
                    RolePermissionSearchCriteria.ofPermissionId(PERMISSION_ID, 0, 20);

            // then
            assertThat(criteria.permissionId()).isEqualTo(PERMISSION_ID);
            assertThat(criteria.roleId()).isNull();
            assertThat(criteria.roleIds()).isNull();
            assertThat(criteria.permissionIds()).isNull();
            assertThat(criteria.sortKey()).isEqualTo(RolePermissionSortKey.CREATED_AT);
            assertThat(criteria.sortDirection()).isEqualTo(SortDirection.DESC);
        }

        @Test
        @DisplayName("ofRoleIds()로 생성한다")
        void shouldCreateViaOfRoleIds() {
            // given
            List<RoleId> roleIds = List.of(RoleId.of(1L), RoleId.of(2L));

            // when
            RolePermissionSearchCriteria criteria =
                    RolePermissionSearchCriteria.ofRoleIds(roleIds, 0, 20);

            // then
            assertThat(criteria.roleIds()).containsExactly(RoleId.of(1L), RoleId.of(2L));
            assertThat(criteria.roleId()).isNull();
            assertThat(criteria.permissionId()).isNull();
            assertThat(criteria.permissionIds()).isNull();
        }

        @Test
        @DisplayName("null roleId로 생성할 수 있다")
        void shouldCreateWithNullRoleId() {
            // when
            RolePermissionSearchCriteria criteria =
                    RolePermissionSearchCriteria.of(
                            null,
                            null,
                            PERMISSION_ID,
                            null,
                            RolePermissionSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PAGE_REQUEST);

            // then
            assertThat(criteria.roleId()).isNull();
        }

        @Test
        @DisplayName("null permissionId로 생성할 수 있다")
        void shouldCreateWithNullPermissionId() {
            // when
            RolePermissionSearchCriteria criteria =
                    RolePermissionSearchCriteria.of(
                            ROLE_ID,
                            null,
                            null,
                            null,
                            RolePermissionSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PAGE_REQUEST);

            // then
            assertThat(criteria.permissionId()).isNull();
        }
    }

    @Nested
    @DisplayName("RolePermissionSearchCriteria Query 메서드 테스트")
    class QueryMethodTests {

        @Test
        @DisplayName("hasRoleIdFilter()는 roleId가 있으면 true를 반환한다")
        void hasRoleIdFilterShouldReturnTrueWhenRoleIdExists() {
            // given
            RolePermissionSearchCriteria criteria =
                    RolePermissionSearchCriteria.of(
                            ROLE_ID,
                            null,
                            null,
                            null,
                            RolePermissionSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PAGE_REQUEST);

            // then
            assertThat(criteria.hasRoleIdFilter()).isTrue();
        }

        @Test
        @DisplayName("hasRoleIdFilter()는 roleId가 null이면 false를 반환한다")
        void hasRoleIdFilterShouldReturnFalseWhenRoleIdIsNull() {
            // given
            RolePermissionSearchCriteria criteria =
                    RolePermissionSearchCriteria.of(
                            null,
                            null,
                            PERMISSION_ID,
                            null,
                            RolePermissionSortKey.CREATED_AT,
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
            RolePermissionSearchCriteria criteria =
                    RolePermissionSearchCriteria.of(
                            null,
                            roleIds,
                            null,
                            null,
                            RolePermissionSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PAGE_REQUEST);

            // then
            assertThat(criteria.hasRoleIdsFilter()).isTrue();
        }

        @Test
        @DisplayName("hasRoleIdsFilter()는 roleIds가 null이면 false를 반환한다")
        void hasRoleIdsFilterShouldReturnFalseWhenRoleIdsIsNull() {
            // given
            RolePermissionSearchCriteria criteria =
                    RolePermissionSearchCriteria.of(
                            ROLE_ID,
                            null,
                            null,
                            null,
                            RolePermissionSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PAGE_REQUEST);

            // then
            assertThat(criteria.hasRoleIdsFilter()).isFalse();
        }

        @Test
        @DisplayName("hasRoleIdsFilter()는 roleIds가 빈 목록이면 false를 반환한다")
        void hasRoleIdsFilterShouldReturnFalseWhenRoleIdsIsEmpty() {
            // given
            RolePermissionSearchCriteria criteria =
                    RolePermissionSearchCriteria.of(
                            null,
                            List.of(),
                            null,
                            null,
                            RolePermissionSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PAGE_REQUEST);

            // then
            assertThat(criteria.hasRoleIdsFilter()).isFalse();
        }

        @Test
        @DisplayName("hasPermissionIdFilter()는 permissionId가 있으면 true를 반환한다")
        void hasPermissionIdFilterShouldReturnTrueWhenPermissionIdExists() {
            // given
            RolePermissionSearchCriteria criteria =
                    RolePermissionSearchCriteria.of(
                            null,
                            null,
                            PERMISSION_ID,
                            null,
                            RolePermissionSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PAGE_REQUEST);

            // then
            assertThat(criteria.hasPermissionIdFilter()).isTrue();
        }

        @Test
        @DisplayName("hasPermissionIdFilter()는 permissionId가 null이면 false를 반환한다")
        void hasPermissionIdFilterShouldReturnFalseWhenPermissionIdIsNull() {
            // given
            RolePermissionSearchCriteria criteria =
                    RolePermissionSearchCriteria.of(
                            ROLE_ID,
                            null,
                            null,
                            null,
                            RolePermissionSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PAGE_REQUEST);

            // then
            assertThat(criteria.hasPermissionIdFilter()).isFalse();
        }

        @Test
        @DisplayName("hasPermissionIdsFilter()는 permissionIds가 있으면 true를 반환한다")
        void hasPermissionIdsFilterShouldReturnTrueWhenPermissionIdsExist() {
            // given
            List<PermissionId> permissionIds = List.of(PermissionId.of(1L), PermissionId.of(2L));
            RolePermissionSearchCriteria criteria =
                    RolePermissionSearchCriteria.of(
                            null,
                            null,
                            null,
                            permissionIds,
                            RolePermissionSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PAGE_REQUEST);

            // then
            assertThat(criteria.hasPermissionIdsFilter()).isTrue();
        }

        @Test
        @DisplayName("hasPermissionIdsFilter()는 permissionIds가 null이면 false를 반환한다")
        void hasPermissionIdsFilterShouldReturnFalseWhenPermissionIdsIsNull() {
            // given
            RolePermissionSearchCriteria criteria =
                    RolePermissionSearchCriteria.of(
                            ROLE_ID,
                            null,
                            null,
                            null,
                            RolePermissionSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PAGE_REQUEST);

            // then
            assertThat(criteria.hasPermissionIdsFilter()).isFalse();
        }

        @Test
        @DisplayName("hasPermissionIdsFilter()는 permissionIds가 빈 목록이면 false를 반환한다")
        void hasPermissionIdsFilterShouldReturnFalseWhenPermissionIdsIsEmpty() {
            // given
            RolePermissionSearchCriteria criteria =
                    RolePermissionSearchCriteria.of(
                            null,
                            null,
                            null,
                            List.of(),
                            RolePermissionSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PAGE_REQUEST);

            // then
            assertThat(criteria.hasPermissionIdsFilter()).isFalse();
        }

        @Test
        @DisplayName("pageNumber()는 queryContext의 page를 반환한다")
        void pageNumberShouldReturnQueryContextPage() {
            // given
            RolePermissionSearchCriteria criteria =
                    RolePermissionSearchCriteria.of(
                            ROLE_ID,
                            null,
                            null,
                            null,
                            RolePermissionSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PAGE_REQUEST);

            // then
            assertThat(criteria.pageNumber()).isEqualTo(0);
        }

        @Test
        @DisplayName("size()는 queryContext의 size를 반환한다")
        void sizeShouldReturnQueryContextSize() {
            // given
            RolePermissionSearchCriteria criteria =
                    RolePermissionSearchCriteria.of(
                            ROLE_ID,
                            null,
                            null,
                            null,
                            RolePermissionSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PAGE_REQUEST);

            // then
            assertThat(criteria.size()).isEqualTo(20);
        }

        @Test
        @DisplayName("offset()는 queryContext의 offset을 반환한다")
        void offsetShouldReturnQueryContextOffset() {
            // given
            RolePermissionSearchCriteria criteria =
                    RolePermissionSearchCriteria.of(
                            ROLE_ID,
                            null,
                            null,
                            null,
                            RolePermissionSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PAGE_REQUEST);

            // then
            assertThat(criteria.offset()).isEqualTo(0L);
        }

        @Test
        @DisplayName("sortKey()는 queryContext의 sortKey를 반환한다")
        void sortKeyShouldReturnQueryContextSortKey() {
            // given
            RolePermissionSearchCriteria criteria =
                    RolePermissionSearchCriteria.of(
                            ROLE_ID,
                            null,
                            null,
                            null,
                            RolePermissionSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PAGE_REQUEST);

            // then
            assertThat(criteria.sortKey()).isEqualTo(RolePermissionSortKey.CREATED_AT);
        }

        @Test
        @DisplayName("sortDirection()는 queryContext의 sortDirection을 반환한다")
        void sortDirectionShouldReturnQueryContextSortDirection() {
            // given
            RolePermissionSearchCriteria criteria =
                    RolePermissionSearchCriteria.of(
                            ROLE_ID,
                            null,
                            null,
                            null,
                            RolePermissionSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PAGE_REQUEST);

            // then
            assertThat(criteria.sortDirection()).isEqualTo(SortDirection.DESC);
        }
    }
}
