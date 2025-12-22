package com.ryuqq.authhub.adapter.out.persistence.role.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.authhub.adapter.out.persistence.role.dto.RoleUserProjection;
import com.ryuqq.authhub.adapter.out.persistence.user.entity.QUserJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.user.entity.QUserRoleJpaEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Repository;

/**
 * RoleUserQueryDslRepository - 역할-사용자 관계 QueryDSL Repository
 *
 * <p>역할에 할당된 사용자 목록을 조회하는 QueryDSL 기반 Repository입니다.
 *
 * <p><strong>DTO Projection:</strong>
 *
 * <ul>
 *   <li>User + UserRole 조인
 *   <li>RoleUserProjection DTO로 Projection
 *   <li>Application DTO 변환은 Adapter에서 수행
 *   <li>N+1 문제 없음
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Repository
public class RoleUserQueryDslRepository {

    private static final QUserJpaEntity USER = QUserJpaEntity.userJpaEntity;
    private static final QUserRoleJpaEntity USER_ROLE = QUserRoleJpaEntity.userRoleJpaEntity;

    private final JPAQueryFactory queryFactory;

    public RoleUserQueryDslRepository(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    /**
     * 역할에 할당된 사용자 목록 조회 (Persistence DTO Projection)
     *
     * @param roleId 역할 UUID
     * @param offset 조회 시작 위치
     * @param limit 조회 건수
     * @return RoleUserProjection 목록
     */
    public List<RoleUserProjection> searchUsersByRoleId(UUID roleId, long offset, int limit) {
        return queryFactory
                .select(
                        Projections.constructor(
                                RoleUserProjection.class,
                                USER.userId,
                                USER.identifier,
                                USER.tenantId,
                                USER.organizationId,
                                USER_ROLE.assignedAt))
                .from(USER_ROLE)
                .innerJoin(USER)
                .on(USER_ROLE.userId.eq(USER.userId))
                .where(USER_ROLE.roleId.eq(roleId))
                .orderBy(USER_ROLE.assignedAt.desc())
                .offset(offset)
                .limit(limit)
                .fetch();
    }

    /**
     * 역할에 할당된 사용자 수 조회
     *
     * @param roleId 역할 UUID
     * @return 사용자 수
     */
    public long countUsersByRoleId(UUID roleId) {
        Long count =
                queryFactory
                        .select(USER_ROLE.count())
                        .from(USER_ROLE)
                        .where(USER_ROLE.roleId.eq(roleId))
                        .fetchOne();
        return count != null ? count : 0L;
    }
}
