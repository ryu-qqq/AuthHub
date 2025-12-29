package com.ryuqq.authhub.adapter.out.persistence.role.repository;

import static com.ryuqq.authhub.adapter.out.persistence.role.entity.QRoleJpaEntity.roleJpaEntity;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.ryuqq.authhub.application.role.dto.query.SearchRolesQuery;
import com.ryuqq.authhub.domain.role.vo.RoleScope;
import com.ryuqq.authhub.domain.role.vo.RoleType;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

/**
 * RoleSearchConditionBuilder - 역할 검색 조건 빌더
 *
 * <p>QueryDSL 검색 조건 및 정렬 조건 생성을 담당합니다.
 *
 * <p><strong>책임:</strong>
 *
 * <ul>
 *   <li>buildSearchCondition() - 검색 조건 BooleanBuilder 생성
 *   <li>buildOrderSpecifier() - 정렬 조건 OrderSpecifier 생성
 *   <li>buildTenantCondition() - 테넌트 조건 BooleanBuilder 생성
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
class RoleSearchConditionBuilder {

    private RoleSearchConditionBuilder() {}

    /**
     * 검색 조건 빌더 생성 (Query 기반)
     *
     * @param query 검색 조건
     * @return BooleanBuilder
     */
    static BooleanBuilder buildSearchCondition(SearchRolesQuery query) {
        BooleanBuilder builder = new BooleanBuilder();

        builder.and(roleJpaEntity.deleted.eq(false));

        if (query.tenantId() != null) {
            builder.and(roleJpaEntity.tenantId.eq(query.tenantId()));
        }

        if (query.name() != null && !query.name().isBlank()) {
            builder.and(buildNameCondition(query.name(), query.searchType()));
        }

        if (query.scopes() != null && !query.scopes().isEmpty()) {
            List<RoleScope> scopeEnums =
                    query.scopes().stream()
                            .map(RoleSearchConditionBuilder::parseScope)
                            .filter(s -> s != null)
                            .toList();
            if (!scopeEnums.isEmpty()) {
                builder.and(roleJpaEntity.scope.in(scopeEnums));
            }
        }

        if (query.types() != null && !query.types().isEmpty()) {
            List<RoleType> typeEnums =
                    query.types().stream()
                            .map(RoleSearchConditionBuilder::parseType)
                            .filter(t -> t != null)
                            .toList();
            if (!typeEnums.isEmpty()) {
                builder.and(roleJpaEntity.type.in(typeEnums));
            }
        }

        if (query.createdFrom() != null) {
            LocalDateTime from = toLocalDateTime(query.createdFrom());
            builder.and(roleJpaEntity.createdAt.goe(from));
        }
        if (query.createdTo() != null) {
            LocalDateTime to = toLocalDateTime(query.createdTo());
            builder.and(roleJpaEntity.createdAt.loe(to));
        }

        return builder;
    }

    /**
     * 정렬 조건 생성 (Query 기반)
     *
     * @param query 검색 조건
     * @return OrderSpecifier
     */
    static OrderSpecifier<?> buildOrderSpecifier(SearchRolesQuery query) {
        String sortBy = query.sortBy() != null ? query.sortBy() : "createdAt";
        String direction = query.sortDirection() != null ? query.sortDirection() : "DESC";
        boolean isAsc = "ASC".equalsIgnoreCase(direction);

        return switch (sortBy.toLowerCase()) {
            case "name" -> isAsc ? roleJpaEntity.name.asc() : roleJpaEntity.name.desc();
            case "scope" -> isAsc ? roleJpaEntity.scope.asc() : roleJpaEntity.scope.desc();
            case "type" -> isAsc ? roleJpaEntity.type.asc() : roleJpaEntity.type.desc();
            case "updatedat" ->
                    isAsc ? roleJpaEntity.updatedAt.asc() : roleJpaEntity.updatedAt.desc();
            default -> isAsc ? roleJpaEntity.createdAt.asc() : roleJpaEntity.createdAt.desc();
        };
    }

    /**
     * 테넌트 조건 빌더 (null 체크 포함)
     *
     * @param tenantId 테넌트 UUID
     * @return BooleanBuilder
     */
    static BooleanBuilder buildTenantCondition(UUID tenantId) {
        BooleanBuilder builder = new BooleanBuilder();
        if (tenantId != null) {
            builder.and(roleJpaEntity.tenantId.eq(tenantId));
        } else {
            builder.and(roleJpaEntity.tenantId.isNull());
        }
        return builder;
    }

    private static BooleanExpression buildNameCondition(String name, String searchType) {
        String type = searchType != null ? searchType : "CONTAINS_LIKE";

        return switch (type) {
            case "PREFIX_LIKE" -> roleJpaEntity.name.startsWithIgnoreCase(name);
            case "MATCH_AGAINST" -> roleJpaEntity.name.containsIgnoreCase(name);
            default -> roleJpaEntity.name.containsIgnoreCase(name);
        };
    }

    private static RoleScope parseScope(String scope) {
        if (scope == null || scope.isBlank()) {
            return null;
        }
        try {
            return RoleScope.valueOf(scope.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static RoleType parseType(String type) {
        if (type == null || type.isBlank()) {
            return null;
        }
        try {
            return RoleType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static LocalDateTime toLocalDateTime(Instant instant) {
        return instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}
