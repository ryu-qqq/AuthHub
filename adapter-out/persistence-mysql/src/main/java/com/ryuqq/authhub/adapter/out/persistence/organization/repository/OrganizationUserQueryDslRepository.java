package com.ryuqq.authhub.adapter.out.persistence.organization.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.authhub.adapter.out.persistence.organization.dto.OrganizationUserProjection;
import com.ryuqq.authhub.adapter.out.persistence.user.entity.QUserJpaEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Repository;

/**
 * OrganizationUserQueryDslRepository - 조직별 사용자 QueryDSL Repository
 *
 * <p>조직에 소속된 사용자 목록을 조회하는 QueryDSL 기반 Repository입니다.
 *
 * <p><strong>DTO Projection:</strong>
 *
 * <ul>
 *   <li>User 테이블에서 organizationId 필터링
 *   <li>OrganizationUserProjection DTO로 Projection
 *   <li>Application DTO 변환은 Adapter에서 수행
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Repository
public class OrganizationUserQueryDslRepository {

    private static final QUserJpaEntity USER = QUserJpaEntity.userJpaEntity;

    private final JPAQueryFactory queryFactory;

    public OrganizationUserQueryDslRepository(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    /**
     * 조직에 소속된 사용자 목록 조회 (Persistence DTO Projection)
     *
     * @param organizationId 조직 UUID
     * @param offset 조회 시작 위치
     * @param limit 조회 건수
     * @return OrganizationUserProjection 목록
     */
    public List<OrganizationUserProjection> searchUsersByOrganizationId(
            UUID organizationId, long offset, int limit) {
        return queryFactory
                .select(
                        Projections.constructor(
                                OrganizationUserProjection.class,
                                USER.userId,
                                USER.identifier,
                                USER.tenantId,
                                USER.createdAt))
                .from(USER)
                .where(USER.organizationId.eq(organizationId))
                .orderBy(USER.createdAt.desc())
                .offset(offset)
                .limit(limit)
                .fetch();
    }

    /**
     * 조직에 소속된 사용자 수 조회
     *
     * @param organizationId 조직 UUID
     * @return 사용자 수
     */
    public long countUsersByOrganizationId(UUID organizationId) {
        Long count =
                queryFactory
                        .select(USER.count())
                        .from(USER)
                        .where(USER.organizationId.eq(organizationId))
                        .fetchOne();
        return count != null ? count : 0L;
    }
}
