package com.ryuqq.authhub.adapter.out.persistence.tenantservice.condition;

import static com.ryuqq.authhub.adapter.out.persistence.tenantservice.entity.QTenantServiceJpaEntity.tenantServiceJpaEntity;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.ryuqq.authhub.domain.common.vo.SortDirection;
import com.ryuqq.authhub.domain.tenantservice.query.criteria.TenantServiceSearchCriteria;
import com.ryuqq.authhub.domain.tenantservice.vo.TenantServiceSortKey;
import com.ryuqq.authhub.domain.tenantservice.vo.TenantServiceStatus;
import java.time.Instant;
import org.springframework.stereotype.Component;

/**
 * TenantServiceConditionBuilder - 테넌트-서비스 QueryDSL 조건 빌더
 *
 * <p>QueryDSL 조건 생성 로직을 분리하여 Repository의 책임을 단순화합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class TenantServiceConditionBuilder {

    /**
     * 검색 조건 빌더 생성
     *
     * @param criteria 검색 조건
     * @return BooleanBuilder
     */
    public BooleanBuilder buildCondition(TenantServiceSearchCriteria criteria) {
        BooleanBuilder builder = new BooleanBuilder();

        // 테넌트 ID 필터
        if (criteria.hasTenantId()) {
            builder.and(tenantIdEquals(criteria.tenantId()));
        }

        // 서비스 ID 필터
        if (criteria.hasServiceId()) {
            builder.and(serviceIdEquals(criteria.serviceId()));
        }

        // 다중 상태 필터
        if (criteria.hasStatusFilter()) {
            builder.and(tenantServiceJpaEntity.status.in(criteria.statuses()));
        }

        // 날짜 범위 필터
        builder.and(subscribedAtGoe(criteria.startInstant()));
        builder.and(subscribedAtLoe(criteria.endInstant()));

        return builder;
    }

    /**
     * 정렬 조건 빌더 생성
     *
     * @param criteria 검색 조건
     * @return OrderSpecifier
     */
    public OrderSpecifier<?> buildOrderSpecifier(TenantServiceSearchCriteria criteria) {
        TenantServiceSortKey sortKey =
                criteria.sortKey() != null
                        ? criteria.sortKey()
                        : TenantServiceSortKey.SUBSCRIBED_AT;
        boolean isAsc = criteria.sortDirection() == SortDirection.ASC;

        return switch (sortKey) {
            case SUBSCRIBED_AT ->
                    isAsc
                            ? tenantServiceJpaEntity.subscribedAt.asc()
                            : tenantServiceJpaEntity.subscribedAt.desc();
            case CREATED_AT ->
                    isAsc
                            ? tenantServiceJpaEntity.createdAt.asc()
                            : tenantServiceJpaEntity.createdAt.desc();
        };
    }

    /**
     * 테넌트 ID 일치 조건
     *
     * @param tenantId 테넌트 ID (String)
     * @return BooleanExpression (null 허용)
     */
    public BooleanExpression tenantIdEquals(String tenantId) {
        return tenantId != null ? tenantServiceJpaEntity.tenantId.eq(tenantId) : null;
    }

    /**
     * 서비스 ID 일치 조건
     *
     * @param serviceId 서비스 ID (Long)
     * @return BooleanExpression (null 허용)
     */
    public BooleanExpression serviceIdEquals(Long serviceId) {
        return serviceId != null ? tenantServiceJpaEntity.serviceId.eq(serviceId) : null;
    }

    /**
     * 활성 상태 조건
     *
     * @return BooleanExpression
     */
    public BooleanExpression statusActive() {
        return tenantServiceJpaEntity.status.eq(TenantServiceStatus.ACTIVE);
    }

    /**
     * 구독일시 이상 조건
     *
     * @param startInstant 시작 일시
     * @return BooleanExpression (null 허용)
     */
    public BooleanExpression subscribedAtGoe(Instant startInstant) {
        return startInstant != null ? tenantServiceJpaEntity.subscribedAt.goe(startInstant) : null;
    }

    /**
     * 구독일시 이하 조건
     *
     * @param endInstant 종료 일시
     * @return BooleanExpression (null 허용)
     */
    public BooleanExpression subscribedAtLoe(Instant endInstant) {
        return endInstant != null ? tenantServiceJpaEntity.subscribedAt.loe(endInstant) : null;
    }
}
