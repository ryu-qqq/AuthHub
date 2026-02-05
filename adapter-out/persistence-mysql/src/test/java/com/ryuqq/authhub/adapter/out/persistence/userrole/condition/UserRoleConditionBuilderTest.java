package com.ryuqq.authhub.adapter.out.persistence.userrole.condition;

import static com.ryuqq.authhub.adapter.out.persistence.userrole.entity.QUserRoleJpaEntity.userRoleJpaEntity;
import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.ryuqq.authhub.domain.common.vo.PageRequest;
import com.ryuqq.authhub.domain.common.vo.SortDirection;
import com.ryuqq.authhub.domain.role.id.RoleId;
import com.ryuqq.authhub.domain.user.id.UserId;
import com.ryuqq.authhub.domain.userrole.query.criteria.UserRoleSearchCriteria;
import com.ryuqq.authhub.domain.userrole.vo.UserRoleSortKey;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * UserRoleConditionBuilder 단위 테스트
 *
 * <p><strong>테스트 설계 원칙:</strong>
 *
 * <ul>
 *   <li>ConditionBuilder는 순수 조건 생성 로직 → Mock 불필요
 *   <li>buildCondition: userId/userIds/roleId/roleIds 필터 검증, null 제외
 *   <li>buildOrderSpecifier: CREATED_AT ASC/DESC, 기본값 검증
 *   <li>Hard Delete (notDeleted 조건 없음) 검증
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("UserRoleConditionBuilder 단위 테스트")
class UserRoleConditionBuilderTest {

    private UserRoleConditionBuilder sut;

    @BeforeEach
    void setUp() {
        sut = new UserRoleConditionBuilder();
    }

    @Nested
    @DisplayName("buildCondition 메서드")
    class BuildCondition {

        @Test
        @DisplayName("성공: userId 필터가 있으면 조건 포함")
        void shouldIncludeUserId_WhenHasUserIdFilter() {
            // given
            UserId userId = UserId.of("01941234-5678-7000-8000-123456789001");
            UserRoleSearchCriteria criteria =
                    UserRoleSearchCriteria.of(
                            userId,
                            null,
                            null,
                            null,
                            UserRoleSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PageRequest.of(0, 10));

            // when
            BooleanBuilder result = sut.buildCondition(criteria);

            // then
            assertThat(result).isNotNull();
            assertThat(result.hasValue()).isTrue();
        }

        @Test
        @DisplayName("성공: userIds 필터가 있으면 조건 포함")
        void shouldIncludeUserIds_WhenHasUserIdsFilter() {
            // given
            List<UserId> userIds =
                    List.of(
                            UserId.of("01941234-5678-7000-8000-123456789001"),
                            UserId.of("01941234-5678-7000-8000-123456789002"));
            UserRoleSearchCriteria criteria = UserRoleSearchCriteria.ofUserIds(userIds, 0, 10);

            // when
            BooleanBuilder result = sut.buildCondition(criteria);

            // then
            assertThat(result).isNotNull();
            assertThat(result.hasValue()).isTrue();
        }

        @Test
        @DisplayName("성공: roleId 필터가 있으면 조건 포함")
        void shouldIncludeRoleId_WhenHasRoleIdFilter() {
            // given
            UserRoleSearchCriteria criteria = UserRoleSearchCriteria.ofRoleId(RoleId.of(1L), 0, 10);

            // when
            BooleanBuilder result = sut.buildCondition(criteria);

            // then
            assertThat(result).isNotNull();
            assertThat(result.hasValue()).isTrue();
        }

        @Test
        @DisplayName("성공: roleIds 필터가 있으면 조건 포함")
        void shouldIncludeRoleIds_WhenHasRoleIdsFilter() {
            // given
            List<RoleId> roleIds = List.of(RoleId.of(1L), RoleId.of(2L));
            UserRoleSearchCriteria criteria = UserRoleSearchCriteria.ofRoleIds(roleIds, 0, 10);

            // when
            BooleanBuilder result = sut.buildCondition(criteria);

            // then
            assertThat(result).isNotNull();
            assertThat(result.hasValue()).isTrue();
        }

        @Test
        @DisplayName("필터가 없으면 빈 BooleanBuilder 반환")
        void shouldReturnEmptyBuilder_WhenNoFilters() {
            // given
            UserRoleSearchCriteria criteria =
                    UserRoleSearchCriteria.of(
                            null,
                            null,
                            null,
                            null,
                            UserRoleSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PageRequest.of(0, 10));

            // when
            BooleanBuilder result = sut.buildCondition(criteria);

            // then
            assertThat(result).isNotNull();
            assertThat(result.hasValue()).isFalse();
        }
    }

    @Nested
    @DisplayName("buildOrderSpecifier 메서드")
    class BuildOrderSpecifier {

        @Test
        @DisplayName("CREATED_AT ASC 정렬 조건 생성")
        void shouldBuildOrderSpecifier_WithCreatedAtAsc() {
            // given
            UserRoleSearchCriteria criteria =
                    UserRoleSearchCriteria.ofUserId(
                            UserId.of("01941234-5678-7000-8000-123456789001"), 0, 10);
            // ofUserId uses CREATED_AT DESC by default; we need criteria with ASC
            UserRoleSearchCriteria criteriaAsc =
                    UserRoleSearchCriteria.of(
                            null,
                            null,
                            null,
                            null,
                            UserRoleSortKey.CREATED_AT,
                            SortDirection.ASC,
                            PageRequest.of(0, 10));

            // when
            OrderSpecifier<?> result = sut.buildOrderSpecifier(criteriaAsc);

            // then
            assertThat(result).isEqualTo(userRoleJpaEntity.createdAt.asc());
        }

        @Test
        @DisplayName("CREATED_AT DESC 정렬 조건 생성")
        void shouldBuildOrderSpecifier_WithCreatedAtDesc() {
            // given
            UserRoleSearchCriteria criteria =
                    UserRoleSearchCriteria.ofUserId(
                            UserId.of("01941234-5678-7000-8000-123456789001"), 0, 10);

            // when
            OrderSpecifier<?> result = sut.buildOrderSpecifier(criteria);

            // then
            assertThat(result).isEqualTo(userRoleJpaEntity.createdAt.desc());
        }

        @Test
        @DisplayName("기본값은 CREATED_AT desc")
        void shouldUseCreatedAtDesc_AsDefault() {
            // given
            UserRoleSearchCriteria criteria =
                    UserRoleSearchCriteria.of(
                            null,
                            null,
                            null,
                            null,
                            UserRoleSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PageRequest.of(0, 10));

            // when
            OrderSpecifier<?> result = sut.buildOrderSpecifier(criteria);

            // then
            assertThat(result).isEqualTo(userRoleJpaEntity.createdAt.desc());
        }
    }
}
