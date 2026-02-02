package com.ryuqq.authhub.adapter.out.persistence.permissionendpoint.condition;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.ryuqq.authhub.adapter.out.persistence.permissionendpoint.entity.QPermissionEndpointJpaEntity;
import com.ryuqq.authhub.domain.common.vo.SortDirection;
import com.ryuqq.authhub.domain.permissionendpoint.query.criteria.PermissionEndpointSearchCriteria;
import com.ryuqq.authhub.domain.permissionendpoint.vo.HttpMethod;
import com.ryuqq.authhub.domain.permissionendpoint.vo.PermissionEndpointSearchField;
import com.ryuqq.authhub.domain.permissionendpoint.vo.PermissionEndpointSortKey;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * PermissionEndpointConditionBuilder - PermissionEndpoint QueryDSL 조건 빌더
 *
 * <p>SearchCriteria를 QueryDSL BooleanBuilder로 변환합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class PermissionEndpointConditionBuilder {

    private static final QPermissionEndpointJpaEntity permissionEndpoint =
            QPermissionEndpointJpaEntity.permissionEndpointJpaEntity;

    /**
     * SearchCriteria → BooleanBuilder 변환
     *
     * @param criteria 검색 조건
     * @return QueryDSL BooleanBuilder
     */
    public BooleanBuilder buildCondition(PermissionEndpointSearchCriteria criteria) {
        BooleanBuilder builder = new BooleanBuilder();

        // 삭제 필터
        if (!criteria.includeDeleted()) {
            builder.and(permissionEndpoint.deletedAt.isNull());
        }

        // Permission IDs 필터
        if (criteria.hasPermissionIds()) {
            builder.and(permissionEndpoint.permissionId.in(criteria.permissionIds()));
        }

        // 검색어 필터
        if (criteria.hasSearchWord()) {
            builder.and(buildSearchWordCondition(criteria.searchWord(), criteria.searchField()));
        }

        // HTTP 메서드 필터
        if (criteria.hasHttpMethodFilter()) {
            builder.and(httpMethodCondition(criteria.httpMethods()));
        }

        // 날짜 범위 필터
        if (criteria.hasDateRange()) {
            builder.and(dateRangeCondition(criteria.startInstant(), criteria.endInstant()));
        }

        return builder;
    }

    /**
     * 정렬 조건 생성
     *
     * @param criteria 검색 조건
     * @return OrderSpecifier
     */
    public OrderSpecifier<?> buildOrderSpecifier(PermissionEndpointSearchCriteria criteria) {
        PermissionEndpointSortKey sortKey = criteria.sortKey();
        SortDirection sortDirection = criteria.sortDirection();

        Order order = sortDirection == SortDirection.ASC ? Order.ASC : Order.DESC;
        ComparableExpressionBase<?> sortExpression = getSortExpression(sortKey);

        return new OrderSpecifier<>(order, sortExpression);
    }

    // ===== Private Helper Methods =====

    private BooleanBuilder buildSearchWordCondition(
            String searchWord, PermissionEndpointSearchField searchField) {
        BooleanBuilder searchBuilder = new BooleanBuilder();
        String pattern = "%" + searchWord + "%";

        switch (searchField) {
            case URL_PATTERN:
                searchBuilder.and(permissionEndpoint.urlPattern.like(pattern));
                break;
            case HTTP_METHOD:
                try {
                    HttpMethod method = HttpMethod.from(searchWord);
                    searchBuilder.and(permissionEndpoint.httpMethod.eq(method));
                } catch (IllegalArgumentException ignored) {
                    // 유효하지 않은 HTTP 메서드면 무시
                }
                break;
            case DESCRIPTION:
                searchBuilder.and(permissionEndpoint.description.like(pattern));
                break;
            default:
                searchBuilder.and(permissionEndpoint.urlPattern.like(pattern));
        }

        return searchBuilder;
    }

    private BooleanBuilder httpMethodCondition(List<HttpMethod> httpMethods) {
        return new BooleanBuilder().and(permissionEndpoint.httpMethod.in(httpMethods));
    }

    private BooleanBuilder dateRangeCondition(Instant startInstant, Instant endInstant) {
        BooleanBuilder dateBuilder = new BooleanBuilder();
        if (startInstant != null) {
            dateBuilder.and(permissionEndpoint.createdAt.goe(startInstant));
        }
        if (endInstant != null) {
            dateBuilder.and(permissionEndpoint.createdAt.loe(endInstant));
        }
        return dateBuilder;
    }

    private ComparableExpressionBase<?> getSortExpression(PermissionEndpointSortKey sortKey) {
        return switch (sortKey) {
            case CREATED_AT -> permissionEndpoint.createdAt;
            case UPDATED_AT -> permissionEndpoint.updatedAt;
            case URL_PATTERN -> permissionEndpoint.urlPattern;
            case HTTP_METHOD -> permissionEndpoint.httpMethod;
        };
    }
}
