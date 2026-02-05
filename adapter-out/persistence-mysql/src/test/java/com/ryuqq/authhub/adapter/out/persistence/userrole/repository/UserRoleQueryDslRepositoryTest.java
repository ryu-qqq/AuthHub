package com.ryuqq.authhub.adapter.out.persistence.userrole.repository;

import static com.ryuqq.authhub.adapter.out.persistence.userrole.entity.QUserRoleJpaEntity.userRoleJpaEntity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.authhub.adapter.out.persistence.userrole.condition.UserRoleConditionBuilder;
import com.ryuqq.authhub.adapter.out.persistence.userrole.entity.UserRoleJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.userrole.fixture.UserRoleJpaEntityFixture;
import com.ryuqq.authhub.domain.common.vo.PageRequest;
import com.ryuqq.authhub.domain.common.vo.SortDirection;
import com.ryuqq.authhub.domain.user.id.UserId;
import com.ryuqq.authhub.domain.userrole.query.criteria.UserRoleSearchCriteria;
import com.ryuqq.authhub.domain.userrole.vo.UserRoleSortKey;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

/**
 * UserRoleQueryDslRepository 단위 테스트
 *
 * <p><strong>테스트 설계 원칙:</strong>
 *
 * <ul>
 *   <li>JPAQueryFactory + JPAQuery 체인 Mock
 *   <li>UserRoleConditionBuilder는 실제 사용 (순수 로직)
 *   <li>Repository가 conditionBuilder를 올바르게 호출하는지 검증
 *   <li>Hard Delete (notDeleted 조건 없음) 검증
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("UserRoleQueryDslRepository 단위 테스트")
class UserRoleQueryDslRepositoryTest {

    private static final String USER_ID = "01941234-5678-7000-8000-123456789001";
    private static final Long ROLE_ID = 1L;

    @Mock private JPAQueryFactory queryFactory;

    @Mock private JPAQuery<UserRoleJpaEntity> selectFromQuery;

    @Mock private JPAQuery<Integer> selectOneQuery;

    @Mock private JPAQuery<Long> countQuery;

    @Mock private JPAQuery<Long> selectRoleIdQuery;

    private UserRoleConditionBuilder conditionBuilder;
    private UserRoleQueryDslRepository sut;

    @BeforeEach
    void setUp() {
        conditionBuilder = new UserRoleConditionBuilder();
        sut = new UserRoleQueryDslRepository(queryFactory, conditionBuilder);
    }

    @Nested
    @DisplayName("exists 메서드")
    class Exists {

        @Test
        @DisplayName("존재하면 true 반환")
        void shouldReturnTrue_WhenExists() {
            // given
            given(queryFactory.selectOne()).willReturn(selectOneQuery);
            given(selectOneQuery.from(userRoleJpaEntity)).willReturn(selectOneQuery);
            given(selectOneQuery.where(any(BooleanExpression.class), any(BooleanExpression.class)))
                    .willReturn(selectOneQuery);
            given(selectOneQuery.fetchFirst()).willReturn(1);

            // when
            boolean result = sut.exists(USER_ID, ROLE_ID);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("존재하지 않으면 false 반환")
        void shouldReturnFalse_WhenNotExists() {
            // given
            given(queryFactory.selectOne()).willReturn(selectOneQuery);
            given(selectOneQuery.from(userRoleJpaEntity)).willReturn(selectOneQuery);
            given(selectOneQuery.where(any(BooleanExpression.class), any(BooleanExpression.class)))
                    .willReturn(selectOneQuery);
            given(selectOneQuery.fetchFirst()).willReturn(null);

            // when
            boolean result = sut.exists(USER_ID, 999L);

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("findByUserIdAndRoleId 메서드")
    class FindByUserIdAndRoleId {

        @Test
        @DisplayName("성공: Entity가 있으면 Optional에 담아 반환")
        void shouldReturnOptionalWithEntity_WhenExists() {
            // given
            UserRoleJpaEntity entity = UserRoleJpaEntityFixture.create();
            given(queryFactory.selectFrom(userRoleJpaEntity)).willReturn(selectFromQuery);
            given(selectFromQuery.where(any(BooleanExpression.class), any(BooleanExpression.class)))
                    .willReturn(selectFromQuery);
            given(selectFromQuery.fetchOne()).willReturn(entity);

            // when
            Optional<UserRoleJpaEntity> result = sut.findByUserIdAndRoleId(USER_ID, ROLE_ID);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(entity);
        }

        @Test
        @DisplayName("Entity가 없으면 빈 Optional 반환")
        void shouldReturnEmpty_WhenNotFound() {
            // given
            given(queryFactory.selectFrom(userRoleJpaEntity)).willReturn(selectFromQuery);
            given(selectFromQuery.where(any(BooleanExpression.class), any(BooleanExpression.class)))
                    .willReturn(selectFromQuery);
            given(selectFromQuery.fetchOne()).willReturn(null);

            // when
            Optional<UserRoleJpaEntity> result = sut.findByUserIdAndRoleId(USER_ID, 999L);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findAllByUserId 메서드")
    class FindAllByUserId {

        @Test
        @DisplayName("성공: Entity 목록 반환")
        void shouldReturnEntityList() {
            // given
            UserRoleJpaEntity entity = UserRoleJpaEntityFixture.create();
            given(queryFactory.selectFrom(userRoleJpaEntity)).willReturn(selectFromQuery);
            given(selectFromQuery.where(any(BooleanExpression.class))).willReturn(selectFromQuery);
            given(selectFromQuery.orderBy(any(OrderSpecifier.class))).willReturn(selectFromQuery);
            given(selectFromQuery.fetch()).willReturn(List.of(entity));

            // when
            List<UserRoleJpaEntity> result = sut.findAllByUserId(USER_ID);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0)).isEqualTo(entity);
        }

        @Test
        @DisplayName("결과가 없으면 빈 목록 반환")
        void shouldReturnEmptyList_WhenNoResults() {
            // given
            given(queryFactory.selectFrom(userRoleJpaEntity)).willReturn(selectFromQuery);
            given(selectFromQuery.where(any(BooleanExpression.class))).willReturn(selectFromQuery);
            given(selectFromQuery.orderBy(any(OrderSpecifier.class))).willReturn(selectFromQuery);
            given(selectFromQuery.fetch()).willReturn(List.of());

            // when
            List<UserRoleJpaEntity> result = sut.findAllByUserId(USER_ID);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findAssignedRoleIds 메서드")
    class FindAssignedRoleIds {

        @Test
        @DisplayName("성공: 이미 할당된 역할 ID 목록 반환")
        void shouldReturnAssignedRoleIds() {
            // given
            List<Long> roleIds = List.of(1L, 2L);
            given(queryFactory.select(userRoleJpaEntity.roleId)).willReturn(selectRoleIdQuery);
            given(selectRoleIdQuery.from(userRoleJpaEntity)).willReturn(selectRoleIdQuery);
            given(
                            selectRoleIdQuery.where(
                                    any(BooleanExpression.class), any(BooleanExpression.class)))
                    .willReturn(selectRoleIdQuery);
            given(selectRoleIdQuery.fetch()).willReturn(List.of(1L));

            // when
            List<Long> result = sut.findAssignedRoleIds(USER_ID, roleIds);

            // then
            assertThat(result).containsExactly(1L);
        }

        @Test
        @DisplayName("빈 roleIds 목록이면 빈 목록 반환")
        void shouldReturnEmptyList_WhenRoleIdsEmpty() {
            // given - repository still runs query with empty in()-clause; stub chain
            given(queryFactory.select(userRoleJpaEntity.roleId)).willReturn(selectRoleIdQuery);
            given(selectRoleIdQuery.from(userRoleJpaEntity)).willReturn(selectRoleIdQuery);
            given(
                            selectRoleIdQuery.where(
                                    any(BooleanExpression.class), any(BooleanExpression.class)))
                    .willReturn(selectRoleIdQuery);
            given(selectRoleIdQuery.fetch()).willReturn(List.of());

            // when
            List<Long> result = sut.findAssignedRoleIds(USER_ID, List.of());

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findAllByCriteria 메서드")
    class FindAllByCriteria {

        @Test
        @DisplayName("성공: 조건에 맞는 Entity 목록 반환")
        void shouldReturnEntityList_WhenCriteriaMatch() {
            // given
            UserRoleSearchCriteria criteria =
                    UserRoleSearchCriteria.ofUserId(UserId.of(USER_ID), 0, 10);
            UserRoleJpaEntity entity = UserRoleJpaEntityFixture.create();
            given(queryFactory.selectFrom(userRoleJpaEntity)).willReturn(selectFromQuery);
            given(selectFromQuery.where(any(Predicate.class))).willReturn(selectFromQuery);
            given(selectFromQuery.orderBy(any(OrderSpecifier.class))).willReturn(selectFromQuery);
            given(selectFromQuery.offset(anyLong())).willReturn(selectFromQuery);
            given(selectFromQuery.limit(anyLong())).willReturn(selectFromQuery);
            given(selectFromQuery.fetch()).willReturn(List.of(entity));

            // when
            List<UserRoleJpaEntity> result = sut.findAllByCriteria(criteria);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0)).isEqualTo(entity);
        }
    }

    @Nested
    @DisplayName("countByCriteria 메서드")
    class CountByCriteria {

        @Test
        @DisplayName("성공: 조건에 맞는 개수 반환")
        void shouldReturnCount_WhenCriteriaMatch() {
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
            given(queryFactory.select(userRoleJpaEntity.count())).willReturn(countQuery);
            given(countQuery.from(userRoleJpaEntity)).willReturn(countQuery);
            given(countQuery.where(any(Predicate.class))).willReturn(countQuery);
            given(countQuery.fetchOne()).willReturn(5L);

            // when
            long result = sut.countByCriteria(criteria);

            // then
            assertThat(result).isEqualTo(5L);
        }

        @Test
        @DisplayName("결과가 없으면 0 반환")
        void shouldReturnZero_WhenNoResults() {
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
            given(queryFactory.select(userRoleJpaEntity.count())).willReturn(countQuery);
            given(countQuery.from(userRoleJpaEntity)).willReturn(countQuery);
            given(countQuery.where(any(Predicate.class))).willReturn(countQuery);
            given(countQuery.fetchOne()).willReturn(null);

            // when
            long result = sut.countByCriteria(criteria);

            // then
            assertThat(result).isZero();
        }
    }
}
