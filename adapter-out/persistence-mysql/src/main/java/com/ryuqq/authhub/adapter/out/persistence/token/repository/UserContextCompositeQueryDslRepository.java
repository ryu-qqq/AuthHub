package com.ryuqq.authhub.adapter.out.persistence.token.repository;

import static com.ryuqq.authhub.adapter.out.persistence.organization.entity.QOrganizationJpaEntity.organizationJpaEntity;
import static com.ryuqq.authhub.adapter.out.persistence.tenant.entity.QTenantJpaEntity.tenantJpaEntity;
import static com.ryuqq.authhub.adapter.out.persistence.user.entity.QUserJpaEntity.userJpaEntity;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.authhub.adapter.out.persistence.token.condition.UserContextCompositeConditionBuilder;
import com.ryuqq.authhub.adapter.out.persistence.token.dto.UserContextProjection;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 * UserContextCompositeQueryDslRepository - 사용자 컨텍스트 조인 조회 Repository
 *
 * <p>User, Organization, Tenant를 조인하여 사용자 컨텍스트를 조회합니다.
 *
 * <p><strong>조인 전략:</strong>
 *
 * <ul>
 *   <li>User → Organization (organizationId)
 *   <li>Organization → Tenant (tenantId)
 *   <li>단일 쿼리로 모든 정보 조회 (N+1 방지)
 * </ul>
 *
 * <p><strong>규칙:</strong>
 *
 * <ul>
 *   <li>ConditionBuilder 의존 필수
 *   <li>비즈니스 로직 금지
 *   <li>Projection 반환
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Repository
public class UserContextCompositeQueryDslRepository {

    private final JPAQueryFactory queryFactory;
    private final UserContextCompositeConditionBuilder conditionBuilder;

    public UserContextCompositeQueryDslRepository(
            JPAQueryFactory queryFactory, UserContextCompositeConditionBuilder conditionBuilder) {
        this.queryFactory = queryFactory;
        this.conditionBuilder = conditionBuilder;
    }

    /**
     * 사용자 ID로 사용자 컨텍스트 조인 조회
     *
     * @param userId 사용자 ID (String)
     * @return Optional<UserContextProjection>
     */
    public Optional<UserContextProjection> findUserContextByUserId(String userId) {
        BooleanBuilder condition = conditionBuilder.buildConditionByUserId(userId);

        UserContextProjection result =
                queryFactory
                        .select(
                                Projections.constructor(
                                        UserContextProjection.class,
                                        userJpaEntity.userId,
                                        userJpaEntity.identifier,
                                        userJpaEntity.identifier,
                                        tenantJpaEntity.tenantId,
                                        tenantJpaEntity.name,
                                        organizationJpaEntity.organizationId,
                                        organizationJpaEntity.name))
                        .from(userJpaEntity)
                        .join(organizationJpaEntity)
                        .on(userJpaEntity.organizationId.eq(organizationJpaEntity.organizationId))
                        .join(tenantJpaEntity)
                        .on(organizationJpaEntity.tenantId.eq(tenantJpaEntity.tenantId))
                        .where(condition)
                        .fetchOne();

        return Optional.ofNullable(result);
    }
}
