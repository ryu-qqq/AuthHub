package com.ryuqq.authhub.adapter.out.persistence.token.condition;

import static com.ryuqq.authhub.adapter.out.persistence.organization.entity.QOrganizationJpaEntity.organizationJpaEntity;
import static com.ryuqq.authhub.adapter.out.persistence.tenant.entity.QTenantJpaEntity.tenantJpaEntity;
import static com.ryuqq.authhub.adapter.out.persistence.user.entity.QUserJpaEntity.userJpaEntity;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.stereotype.Component;

/**
 * UserContextCompositeConditionBuilder - 사용자 컨텍스트 Composite QueryDSL 조건 빌더
 *
 * <p>User, Organization, Tenant 조인 쿼리를 위한 조건 빌더입니다.
 *
 * <p><strong>책임:</strong>
 *
 * <ul>
 *   <li>User 조건 생성
 *   <li>Organization 조건 생성
 *   <li>Tenant 조건 생성
 *   <li>삭제되지 않은 항목 필터링
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class UserContextCompositeConditionBuilder {

    /**
     * 사용자 ID로 조회하는 전체 조건 생성
     *
     * @param userId 사용자 ID (String)
     * @return BooleanBuilder
     */
    public BooleanBuilder buildConditionByUserId(String userId) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(userIdEquals(userId));
        builder.and(userNotDeleted());
        builder.and(organizationNotDeleted());
        builder.and(tenantNotDeleted());
        return builder;
    }

    /**
     * 사용자 ID 일치 조건
     *
     * @param userId 사용자 ID (String)
     * @return BooleanExpression (null 허용)
     */
    public BooleanExpression userIdEquals(String userId) {
        return userId != null ? userJpaEntity.userId.eq(userId) : null;
    }

    /**
     * User 삭제되지 않은 항목만 조건
     *
     * @return BooleanExpression
     */
    public BooleanExpression userNotDeleted() {
        return userJpaEntity.deletedAt.isNull();
    }

    /**
     * Organization 삭제되지 않은 항목만 조건
     *
     * @return BooleanExpression
     */
    public BooleanExpression organizationNotDeleted() {
        return organizationJpaEntity.deletedAt.isNull();
    }

    /**
     * Tenant 삭제되지 않은 항목만 조건
     *
     * @return BooleanExpression
     */
    public BooleanExpression tenantNotDeleted() {
        return tenantJpaEntity.deletedAt.isNull();
    }
}
