package com.ryuqq.authhub.adapter.out.persistence.role.repository;

import static com.ryuqq.authhub.adapter.out.persistence.role.entity.QRoleJpaEntity.roleJpaEntity;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.authhub.adapter.out.persistence.role.entity.RoleJpaEntity;
import com.ryuqq.authhub.application.role.dto.query.SearchRolesQuery;
import com.ryuqq.authhub.domain.role.vo.RoleScope;
import com.ryuqq.authhub.domain.role.vo.RoleType;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

/**
 * RoleQueryDslRepository - 역할 QueryDSL Repository (Query 전용)
 *
 * <p>QueryDSL 기반 조회 작업을 담당합니다.
 *
 * <p><strong>책임:</strong>
 *
 * <ul>
 *   <li>findByRoleId() - ID로 단건 조회
 *   <li>findByTenantIdAndName() - 테넌트 내 역할 이름으로 조회
 *   <li>existsByTenantIdAndName() - 테넌트 내 역할 이름 존재 여부 확인
 *   <li>searchByQuery() - Query 기반 조건 검색
 *   <li>countByQuery() - Query 기반 개수 조회
 * </ul>
 *
 * <p><strong>CQRS 패턴:</strong>
 *
 * <ul>
 *   <li>Command: RoleJpaRepository (JPA)
 *   <li>Query: RoleQueryDslRepository (QueryDSL)
 * </ul>
 *
 * <p><strong>규칙:</strong>
 *
 * <ul>
 *   <li>Entity 반환 (Domain 변환은 Adapter에서)
 *   <li>Join 금지 (N+1 해결은 Application Layer에서)
 *   <li>비즈니스 로직 금지
 *   <li>삭제되지 않은 역할만 조회 (deleted = false)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Repository
public class RoleQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    public RoleQueryDslRepository(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    /**
     * UUID로 역할 단건 조회
     *
     * @param roleId 역할 UUID
     * @return Optional<RoleJpaEntity>
     */
    public Optional<RoleJpaEntity> findByRoleId(UUID roleId) {
        RoleJpaEntity result =
                queryFactory
                        .selectFrom(roleJpaEntity)
                        .where(roleJpaEntity.roleId.eq(roleId), roleJpaEntity.deleted.eq(false))
                        .fetchOne();
        return Optional.ofNullable(result);
    }

    /**
     * 테넌트 내 역할 이름으로 역할 단건 조회
     *
     * @param tenantId 테넌트 UUID (null일 경우 GLOBAL 범위)
     * @param name 역할 이름
     * @return Optional<RoleJpaEntity>
     */
    public Optional<RoleJpaEntity> findByTenantIdAndName(UUID tenantId, String name) {
        RoleJpaEntity result =
                queryFactory
                        .selectFrom(roleJpaEntity)
                        .where(
                                buildTenantCondition(tenantId),
                                roleJpaEntity.name.eq(name),
                                roleJpaEntity.deleted.eq(false))
                        .fetchOne();
        return Optional.ofNullable(result);
    }

    /**
     * 테넌트 내 역할 이름 존재 여부 확인
     *
     * @param tenantId 테넌트 UUID (null일 경우 GLOBAL 범위)
     * @param name 역할 이름
     * @return 존재 여부
     */
    public boolean existsByTenantIdAndName(UUID tenantId, String name) {
        Integer result =
                queryFactory
                        .selectOne()
                        .from(roleJpaEntity)
                        .where(
                                buildTenantCondition(tenantId),
                                roleJpaEntity.name.eq(name),
                                roleJpaEntity.deleted.eq(false))
                        .fetchFirst();
        return result != null;
    }

    /**
     * Query 기반 역할 검색 (페이징)
     *
     * @param query 검색 조건 (SearchRolesQuery)
     * @return RoleJpaEntity 목록
     */
    public List<RoleJpaEntity> searchByQuery(SearchRolesQuery query) {
        BooleanBuilder builder = buildSearchCondition(query);
        OrderSpecifier<?> orderSpecifier = buildOrderSpecifier(query);
        int offset = query.page() * query.size();

        return queryFactory
                .selectFrom(roleJpaEntity)
                .where(builder)
                .orderBy(orderSpecifier)
                .offset(offset)
                .limit(query.size())
                .fetch();
    }

    /**
     * Query 기반 역할 개수 조회
     *
     * @param query 검색 조건 (SearchRolesQuery)
     * @return 조건에 맞는 역할 총 개수
     */
    public long countByQuery(SearchRolesQuery query) {
        BooleanBuilder builder = buildSearchCondition(query);

        Long count =
                queryFactory
                        .select(roleJpaEntity.count())
                        .from(roleJpaEntity)
                        .where(builder)
                        .fetchOne();
        return count != null ? count : 0L;
    }

    /**
     * 테넌트 조건 빌더 (null 체크 포함)
     *
     * @param tenantId 테넌트 UUID
     * @return BooleanBuilder
     */
    private BooleanBuilder buildTenantCondition(UUID tenantId) {
        BooleanBuilder builder = new BooleanBuilder();
        if (tenantId != null) {
            builder.and(roleJpaEntity.tenantId.eq(tenantId));
        } else {
            builder.and(roleJpaEntity.tenantId.isNull());
        }
        return builder;
    }

    /**
     * 여러 ID로 역할 목록 조회
     *
     * @param roleIds 역할 UUID 컬렉션
     * @return RoleJpaEntity 목록
     */
    public List<RoleJpaEntity> findAllByIds(Collection<UUID> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return List.of();
        }
        return queryFactory
                .selectFrom(roleJpaEntity)
                .where(roleJpaEntity.roleId.in(roleIds), roleJpaEntity.deleted.eq(false))
                .fetch();
    }

    /**
     * 검색 조건 빌더 생성 (Query 기반)
     *
     * @param query 검색 조건
     * @return BooleanBuilder
     */
    private BooleanBuilder buildSearchCondition(SearchRolesQuery query) {
        BooleanBuilder builder = new BooleanBuilder();

        // 삭제되지 않은 역할만 조회
        builder.and(roleJpaEntity.deleted.eq(false));

        // 테넌트 ID 필터
        if (query.tenantId() != null) {
            builder.and(roleJpaEntity.tenantId.eq(query.tenantId()));
        }

        // 이름 필터 (SearchType 기반)
        if (query.name() != null && !query.name().isBlank()) {
            builder.and(buildNameCondition(query.name(), query.searchType()));
        }

        // 다중 스코프 필터
        if (query.scopes() != null && !query.scopes().isEmpty()) {
            List<RoleScope> scopeEnums =
                    query.scopes().stream().map(this::parseScope).filter(s -> s != null).toList();
            if (!scopeEnums.isEmpty()) {
                builder.and(roleJpaEntity.scope.in(scopeEnums));
            }
        }

        // 다중 타입 필터
        if (query.types() != null && !query.types().isEmpty()) {
            List<RoleType> typeEnums =
                    query.types().stream().map(this::parseType).filter(t -> t != null).toList();
            if (!typeEnums.isEmpty()) {
                builder.and(roleJpaEntity.type.in(typeEnums));
            }
        }

        // 생성일시 범위 필터
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
     * 이름 검색 조건 생성 (SearchType 기반)
     *
     * @param name 검색 이름
     * @param searchType 검색 타입
     * @return BooleanExpression
     */
    private BooleanExpression buildNameCondition(String name, String searchType) {
        String type = searchType != null ? searchType : "CONTAINS_LIKE";

        return switch (type) {
            case "PREFIX_LIKE" -> roleJpaEntity.name.startsWithIgnoreCase(name);
            case "MATCH_AGAINST" -> roleJpaEntity.name.containsIgnoreCase(name);
            default -> roleJpaEntity.name.containsIgnoreCase(name);
        };
    }

    /**
     * 정렬 조건 생성 (Query 기반)
     *
     * @param query 검색 조건
     * @return OrderSpecifier
     */
    private OrderSpecifier<?> buildOrderSpecifier(SearchRolesQuery query) {
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
     * String을 RoleScope enum으로 변환
     *
     * @param scope 스코프 문자열
     * @return RoleScope (null if invalid)
     */
    private RoleScope parseScope(String scope) {
        if (scope == null || scope.isBlank()) {
            return null;
        }
        try {
            return RoleScope.valueOf(scope.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * String을 RoleType enum으로 변환
     *
     * @param type 타입 문자열
     * @return RoleType (null if invalid)
     */
    private RoleType parseType(String type) {
        if (type == null || type.isBlank()) {
            return null;
        }
        try {
            return RoleType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Instant를 LocalDateTime으로 변환
     *
     * @param instant Instant
     * @return LocalDateTime
     */
    private LocalDateTime toLocalDateTime(Instant instant) {
        return instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}
