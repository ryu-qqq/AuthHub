package com.ryuqq.authhub.adapter.out.persistence.permission.repository;

import static com.ryuqq.authhub.adapter.out.persistence.permission.entity.QPermissionJpaEntity.permissionJpaEntity;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.ryuqq.authhub.application.permission.dto.query.SearchPermissionsQuery;
import com.ryuqq.authhub.domain.permission.vo.PermissionType;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

/**
 * PermissionSearchConditionBuilder - 권한 검색 조건 빌더
 *
 * <p>QueryDSL 검색 조건 및 정렬 조건 생성을 담당합니다.
 *
 * <p><strong>책임:</strong>
 *
 * <ul>
 *   <li>buildSearchCondition() - 검색 조건 BooleanBuilder 생성
 *   <li>buildOrderSpecifier() - 정렬 조건 OrderSpecifier 생성
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
class PermissionSearchConditionBuilder {

    private PermissionSearchConditionBuilder() {}

    /**
     * 검색 조건 빌더 생성 (Query 기반)
     *
     * @param query 검색 조건
     * @return BooleanBuilder
     */
    static BooleanBuilder buildSearchCondition(SearchPermissionsQuery query) {
        BooleanBuilder builder = new BooleanBuilder();

        builder.and(permissionJpaEntity.deleted.eq(false));

        if (query.resource() != null && !query.resource().isBlank()) {
            builder.and(buildResourceCondition(query.resource(), query.searchType()));
        }

        if (query.action() != null && !query.action().isBlank()) {
            builder.and(buildActionCondition(query.action(), query.searchType()));
        }

        if (query.types() != null && !query.types().isEmpty()) {
            List<PermissionType> typeEnums =
                    query.types().stream()
                            .map(PermissionSearchConditionBuilder::parseType)
                            .filter(t -> t != null)
                            .toList();
            if (!typeEnums.isEmpty()) {
                builder.and(permissionJpaEntity.type.in(typeEnums));
            }
        }

        if (query.createdFrom() != null) {
            LocalDateTime from = toLocalDateTime(query.createdFrom());
            builder.and(permissionJpaEntity.createdAt.goe(from));
        }
        if (query.createdTo() != null) {
            LocalDateTime to = toLocalDateTime(query.createdTo());
            builder.and(permissionJpaEntity.createdAt.loe(to));
        }

        return builder;
    }

    /**
     * 정렬 조건 생성 (Query 기반)
     *
     * @param query 검색 조건
     * @return OrderSpecifier
     */
    static OrderSpecifier<?> buildOrderSpecifier(SearchPermissionsQuery query) {
        String sortBy = query.sortBy() != null ? query.sortBy() : "createdAt";
        String direction = query.sortDirection() != null ? query.sortDirection() : "DESC";
        boolean isAsc = "ASC".equalsIgnoreCase(direction);

        return switch (sortBy.toLowerCase()) {
            case "resource" ->
                    isAsc
                            ? permissionJpaEntity.resource.asc()
                            : permissionJpaEntity.resource.desc();
            case "action" ->
                    isAsc ? permissionJpaEntity.action.asc() : permissionJpaEntity.action.desc();
            case "type" -> isAsc ? permissionJpaEntity.type.asc() : permissionJpaEntity.type.desc();
            case "updatedat" ->
                    isAsc
                            ? permissionJpaEntity.updatedAt.asc()
                            : permissionJpaEntity.updatedAt.desc();
            default ->
                    isAsc
                            ? permissionJpaEntity.createdAt.asc()
                            : permissionJpaEntity.createdAt.desc();
        };
    }

    private static BooleanExpression buildResourceCondition(String resource, String searchType) {
        String type = searchType != null ? searchType : "CONTAINS_LIKE";

        return switch (type) {
            case "PREFIX_LIKE" -> permissionJpaEntity.resource.startsWithIgnoreCase(resource);
            case "MATCH_AGAINST" -> permissionJpaEntity.resource.containsIgnoreCase(resource);
            default -> permissionJpaEntity.resource.containsIgnoreCase(resource);
        };
    }

    private static BooleanExpression buildActionCondition(String action, String searchType) {
        String type = searchType != null ? searchType : "CONTAINS_LIKE";

        return switch (type) {
            case "PREFIX_LIKE" -> permissionJpaEntity.action.startsWithIgnoreCase(action);
            case "MATCH_AGAINST" -> permissionJpaEntity.action.containsIgnoreCase(action);
            default -> permissionJpaEntity.action.containsIgnoreCase(action);
        };
    }

    private static PermissionType parseType(String type) {
        if (type == null || type.isBlank()) {
            return null;
        }
        try {
            return PermissionType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static LocalDateTime toLocalDateTime(Instant instant) {
        return instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}
