package com.ryuqq.authhub.adapter.out.persistence.role.repository;

import static com.ryuqq.authhub.adapter.out.persistence.permission.entity.QPermissionJpaEntity.permissionJpaEntity;
import static com.ryuqq.authhub.adapter.out.persistence.role.entity.QRoleJpaEntity.roleJpaEntity;
import static com.ryuqq.authhub.adapter.out.persistence.role.entity.QRolePermissionJpaEntity.rolePermissionJpaEntity;
import static com.ryuqq.authhub.adapter.out.persistence.tenant.entity.QTenantJpaEntity.tenantJpaEntity;
import static com.ryuqq.authhub.adapter.out.persistence.user.entity.QUserRoleJpaEntity.userRoleJpaEntity;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.authhub.application.role.dto.query.SearchRolesQuery;
import com.ryuqq.authhub.application.role.dto.response.RoleDetailResponse;
import com.ryuqq.authhub.application.role.dto.response.RoleDetailResponse.RolePermissionSummary;
import com.ryuqq.authhub.application.role.dto.response.RoleSummaryResponse;
import com.ryuqq.authhub.domain.role.vo.RoleScope;
import com.ryuqq.authhub.domain.role.vo.RoleType;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

/**
 * RoleAdminQueryDslRepository - 역할 Admin QueryDSL Repository
 *
 * <p>어드민 화면을 위한 확장된 역할 조회를 담당합니다. Join을 통해 관련 엔티티 정보를 포함한 DTO Projection을 반환합니다.
 *
 * <p><strong>일반 QueryDslRepository와 차이점:</strong>
 *
 * <ul>
 *   <li>✅ Join 허용 (Tenant 조인)
 *   <li>✅ DTO Projection 직접 반환
 *   <li>✅ 서브쿼리를 통한 집계 정보 포함 (permissionCount, userCount)
 *   <li>✅ 확장 필터 지원 (날짜 범위, 정렬)
 * </ul>
 *
 * <p><strong>규칙:</strong>
 *
 * <ul>
 *   <li>DTO Projection 반환 (Entity 아님)
 *   <li>Join을 통한 관련 정보 조회
 *   <li>비즈니스 로직 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Repository
public class RoleAdminQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    public RoleAdminQueryDslRepository(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    /**
     * QueryDSL Projection용 내부 DTO (Admin용)
     *
     * <p>JPA Entity의 LocalDateTime과 Long 타입을 그대로 받기 위한 중간 DTO입니다. Application
     * DTO(RoleSummaryResponse)로 변환 시 Instant와 int로 변환합니다.
     */
    public record RoleAdminSummaryProjection(
            UUID roleId,
            UUID tenantId,
            String tenantName,
            String name,
            String description,
            String scope,
            String type,
            Long permissionCount,
            Long userCount,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {

        /**
         * Application DTO로 변환
         *
         * @return RoleSummaryResponse
         */
        RoleSummaryResponse toResponse() {
            return new RoleSummaryResponse(
                    roleId,
                    tenantId,
                    tenantName,
                    name,
                    description,
                    scope,
                    type,
                    permissionCount != null ? permissionCount.intValue() : 0,
                    userCount != null ? userCount.intValue() : 0,
                    createdAt != null ? createdAt.toInstant(ZoneOffset.UTC) : null,
                    updatedAt != null ? updatedAt.toInstant(ZoneOffset.UTC) : null);
        }
    }

    /**
     * 역할 목록 검색 (Admin용 - Summary Projection)
     *
     * <p>Tenant 조인 및 permissionCount, userCount 서브쿼리를 포함합니다.
     *
     * @param query 검색 조건
     * @return RoleSummaryResponse 목록
     */
    public List<RoleSummaryResponse> searchRoles(SearchRolesQuery query) {
        BooleanBuilder builder = buildSearchCondition(query);
        OrderSpecifier<?> orderSpecifier =
                buildOrderSpecifier(query.sortBy(), query.sortDirection());

        return queryFactory
                .select(
                        Projections.constructor(
                                RoleAdminSummaryProjection.class,
                                roleJpaEntity.roleId,
                                roleJpaEntity.tenantId,
                                tenantJpaEntity.name,
                                roleJpaEntity.name,
                                roleJpaEntity.description,
                                roleJpaEntity.scope.stringValue(),
                                roleJpaEntity.type.stringValue(),
                                JPAExpressions.select(rolePermissionJpaEntity.count())
                                        .from(rolePermissionJpaEntity)
                                        .where(
                                                rolePermissionJpaEntity.roleId.eq(
                                                        roleJpaEntity.roleId)),
                                JPAExpressions.select(userRoleJpaEntity.count())
                                        .from(userRoleJpaEntity)
                                        .where(userRoleJpaEntity.roleId.eq(roleJpaEntity.roleId)),
                                roleJpaEntity.createdAt,
                                roleJpaEntity.updatedAt))
                .from(roleJpaEntity)
                .leftJoin(tenantJpaEntity)
                .on(tenantJpaEntity.tenantId.eq(roleJpaEntity.tenantId))
                .where(builder)
                .orderBy(orderSpecifier)
                .offset((long) query.page() * query.size())
                .limit(query.size())
                .fetch()
                .stream()
                .map(RoleAdminSummaryProjection::toResponse)
                .toList();
    }

    /**
     * 역할 수 조회
     *
     * @param query 검색 조건
     * @return 역할 수
     */
    public long countRoles(SearchRolesQuery query) {
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
     * 역할 상세 조회 (Admin용 - Detail Projection)
     *
     * <p>기본 정보와 할당된 권한 목록을 함께 조회합니다.
     *
     * @param roleId 역할 ID
     * @return RoleDetailResponse Optional
     */
    public Optional<RoleDetailResponse> findRoleDetail(UUID roleId) {
        // 기본 정보 조회
        var basicInfo =
                queryFactory
                        .select(
                                Projections.fields(
                                        RoleAdminBasicInfo.class,
                                        roleJpaEntity.roleId.as("roleId"),
                                        roleJpaEntity.tenantId.as("tenantId"),
                                        tenantJpaEntity.name.as("tenantName"),
                                        roleJpaEntity.name.as("name"),
                                        roleJpaEntity.description.as("description"),
                                        roleJpaEntity.scope.stringValue().as("scope"),
                                        roleJpaEntity.type.stringValue().as("type"),
                                        roleJpaEntity.createdAt.as("createdAt"),
                                        roleJpaEntity.updatedAt.as("updatedAt")))
                        .from(roleJpaEntity)
                        .leftJoin(tenantJpaEntity)
                        .on(tenantJpaEntity.tenantId.eq(roleJpaEntity.tenantId))
                        .where(roleJpaEntity.roleId.eq(roleId), roleJpaEntity.deleted.eq(false))
                        .fetchOne();

        if (basicInfo == null) {
            return Optional.empty();
        }

        // 권한 목록 조회
        List<RolePermissionSummary> permissions =
                queryFactory
                        .select(
                                Projections.constructor(
                                        RolePermissionSummary.class,
                                        permissionJpaEntity.permissionId,
                                        permissionJpaEntity.permissionKey,
                                        permissionJpaEntity.description,
                                        permissionJpaEntity.resource,
                                        permissionJpaEntity.action))
                        .from(rolePermissionJpaEntity)
                        .join(permissionJpaEntity)
                        .on(
                                permissionJpaEntity.permissionId.eq(
                                        rolePermissionJpaEntity.permissionId))
                        .where(
                                rolePermissionJpaEntity.roleId.eq(roleId),
                                permissionJpaEntity.deleted.eq(false))
                        .fetch();

        // 사용자 수 조회
        Long userCount =
                queryFactory
                        .select(userRoleJpaEntity.count())
                        .from(userRoleJpaEntity)
                        .where(userRoleJpaEntity.roleId.eq(roleId))
                        .fetchOne();

        return Optional.of(
                new RoleDetailResponse(
                        basicInfo.roleId,
                        basicInfo.tenantId,
                        basicInfo.tenantName,
                        basicInfo.name,
                        basicInfo.description,
                        basicInfo.scope,
                        basicInfo.type,
                        permissions,
                        userCount != null ? userCount.intValue() : 0,
                        toInstant(basicInfo.createdAt),
                        toInstant(basicInfo.updatedAt)));
    }

    /** 검색 조건 빌더 생성 */
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

    /** 정렬 조건 생성 */
    private OrderSpecifier<?> buildOrderSpecifier(String sortBy, String sortDirection) {
        boolean isAsc = "ASC".equalsIgnoreCase(sortDirection);

        return switch (sortBy != null ? sortBy : "createdAt") {
            case "name" -> isAsc ? roleJpaEntity.name.asc() : roleJpaEntity.name.desc();
            case "updatedAt" ->
                    isAsc ? roleJpaEntity.updatedAt.asc() : roleJpaEntity.updatedAt.desc();
            default -> isAsc ? roleJpaEntity.createdAt.asc() : roleJpaEntity.createdAt.desc();
        };
    }

    /** LocalDateTime → Instant 변환 */
    private Instant toInstant(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.toInstant(ZoneOffset.UTC);
    }

    /** Instant → LocalDateTime 변환 */
    private LocalDateTime toLocalDateTime(Instant instant) {
        if (instant == null) {
            return null;
        }
        return LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
    }

    /** 기본 정보 내부 클래스 (Projection용) */
    @SuppressWarnings({"PMD.DataClass", "PMD.TooManyFields"})
    public static class RoleAdminBasicInfo {
        public UUID roleId;
        public UUID tenantId;
        public String tenantName;
        public String name;
        public String description;
        public String scope;
        public String type;
        public LocalDateTime createdAt;
        public LocalDateTime updatedAt;
    }
}
