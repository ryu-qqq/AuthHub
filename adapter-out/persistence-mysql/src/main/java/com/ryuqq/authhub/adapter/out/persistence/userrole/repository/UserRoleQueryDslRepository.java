package com.ryuqq.authhub.adapter.out.persistence.userrole.repository;

import static com.ryuqq.authhub.adapter.out.persistence.userrole.entity.QUserRoleJpaEntity.userRoleJpaEntity;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.authhub.adapter.out.persistence.userrole.condition.UserRoleConditionBuilder;
import com.ryuqq.authhub.adapter.out.persistence.userrole.entity.UserRoleJpaEntity;
import com.ryuqq.authhub.domain.userrole.query.criteria.UserRoleSearchCriteria;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 * UserRoleQueryDslRepository - 사용자-역할 관계 QueryDSL Repository (Query 전용)
 *
 * <p>QueryDSL 기반 조회 작업을 담당합니다.
 *
 * <p><strong>CQRS 패턴:</strong>
 *
 * <ul>
 *   <li>Command: UserRoleJpaRepository (JPA)
 *   <li>Query: UserRoleQueryDslRepository (QueryDSL)
 * </ul>
 *
 * <p><strong>Hard Delete:</strong>
 *
 * <ul>
 *   <li>관계 테이블이므로 notDeleted() 조건 없음
 *   <li>물리적 삭제만 존재
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Repository
public class UserRoleQueryDslRepository {

    private final JPAQueryFactory queryFactory;
    private final UserRoleConditionBuilder conditionBuilder;

    public UserRoleQueryDslRepository(
            JPAQueryFactory queryFactory, UserRoleConditionBuilder conditionBuilder) {
        this.queryFactory = queryFactory;
        this.conditionBuilder = conditionBuilder;
    }

    /**
     * 사용자-역할 관계 존재 여부 확인
     *
     * @param userId 사용자 ID (String)
     * @param roleId 역할 ID (Long)
     * @return 존재 여부
     */
    public boolean exists(String userId, Long roleId) {
        Integer result =
                queryFactory
                        .selectOne()
                        .from(userRoleJpaEntity)
                        .where(
                                userRoleJpaEntity.userId.eq(userId),
                                userRoleJpaEntity.roleId.eq(roleId))
                        .fetchFirst();
        return result != null;
    }

    /**
     * 사용자-역할 관계 조회
     *
     * @param userId 사용자 ID (String)
     * @param roleId 역할 ID (Long)
     * @return Optional<UserRoleJpaEntity>
     */
    public Optional<UserRoleJpaEntity> findByUserIdAndRoleId(String userId, Long roleId) {
        UserRoleJpaEntity result =
                queryFactory
                        .selectFrom(userRoleJpaEntity)
                        .where(
                                userRoleJpaEntity.userId.eq(userId),
                                userRoleJpaEntity.roleId.eq(roleId))
                        .fetchOne();
        return Optional.ofNullable(result);
    }

    /**
     * 사용자의 역할 목록 조회
     *
     * @param userId 사용자 ID (String)
     * @return UserRoleJpaEntity 목록
     */
    public List<UserRoleJpaEntity> findAllByUserId(String userId) {
        return queryFactory
                .selectFrom(userRoleJpaEntity)
                .where(userRoleJpaEntity.userId.eq(userId))
                .orderBy(userRoleJpaEntity.createdAt.desc())
                .fetch();
    }

    /**
     * 역할이 할당된 사용자 목록 조회
     *
     * @param roleId 역할 ID (Long)
     * @return UserRoleJpaEntity 목록
     */
    public List<UserRoleJpaEntity> findAllByRoleId(Long roleId) {
        return queryFactory
                .selectFrom(userRoleJpaEntity)
                .where(userRoleJpaEntity.roleId.eq(roleId))
                .orderBy(userRoleJpaEntity.createdAt.desc())
                .fetch();
    }

    /**
     * 역할이 어떤 사용자에게라도 할당되어 있는지 확인
     *
     * @param roleId 역할 ID (Long)
     * @return 사용 중 여부
     */
    public boolean existsByRoleId(Long roleId) {
        Integer result =
                queryFactory
                        .selectOne()
                        .from(userRoleJpaEntity)
                        .where(userRoleJpaEntity.roleId.eq(roleId))
                        .fetchFirst();
        return result != null;
    }

    /**
     * SearchCriteria 기반 목록 조회 (페이징)
     *
     * @param criteria 검색 조건
     * @return UserRoleJpaEntity 목록
     */
    public List<UserRoleJpaEntity> findAllByCriteria(UserRoleSearchCriteria criteria) {
        BooleanBuilder condition = conditionBuilder.buildCondition(criteria);
        OrderSpecifier<?> orderSpecifier = conditionBuilder.buildOrderSpecifier(criteria);

        long offset = criteria.offset();
        int limit = criteria.size();

        return queryFactory
                .selectFrom(userRoleJpaEntity)
                .where(condition)
                .orderBy(orderSpecifier)
                .offset(offset)
                .limit(limit)
                .fetch();
    }

    /**
     * SearchCriteria 기반 개수 조회
     *
     * @param criteria 검색 조건
     * @return 총 개수
     */
    public long countByCriteria(UserRoleSearchCriteria criteria) {
        BooleanBuilder condition = conditionBuilder.buildCondition(criteria);

        Long count =
                queryFactory
                        .select(userRoleJpaEntity.count())
                        .from(userRoleJpaEntity)
                        .where(condition)
                        .fetchOne();
        return count != null ? count : 0L;
    }

    /**
     * 사용자에게 이미 할당된 역할 ID 목록 조회 (요청된 roleIds 중에서)
     *
     * @param userId 사용자 ID (String)
     * @param roleIds 확인할 역할 ID 목록 (Long)
     * @return 이미 할당된 역할 ID 목록
     */
    public List<Long> findAssignedRoleIds(String userId, List<Long> roleIds) {
        return queryFactory
                .select(userRoleJpaEntity.roleId)
                .from(userRoleJpaEntity)
                .where(userRoleJpaEntity.userId.eq(userId), userRoleJpaEntity.roleId.in(roleIds))
                .fetch();
    }
}
