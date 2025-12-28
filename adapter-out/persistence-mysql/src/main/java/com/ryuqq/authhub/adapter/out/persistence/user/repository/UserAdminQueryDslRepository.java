package com.ryuqq.authhub.adapter.out.persistence.user.repository;

import static com.ryuqq.authhub.adapter.out.persistence.organization.entity.QOrganizationJpaEntity.organizationJpaEntity;
import static com.ryuqq.authhub.adapter.out.persistence.role.entity.QRoleJpaEntity.roleJpaEntity;
import static com.ryuqq.authhub.adapter.out.persistence.tenant.entity.QTenantJpaEntity.tenantJpaEntity;
import static com.ryuqq.authhub.adapter.out.persistence.user.entity.QUserJpaEntity.userJpaEntity;
import static com.ryuqq.authhub.adapter.out.persistence.user.entity.QUserRoleJpaEntity.userRoleJpaEntity;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.authhub.application.user.dto.query.SearchUsersQuery;
import com.ryuqq.authhub.application.user.dto.response.UserDetailResponse;
import com.ryuqq.authhub.application.user.dto.response.UserDetailResponse.UserRoleSummary;
import com.ryuqq.authhub.application.user.dto.response.UserSummaryResponse;
import com.ryuqq.authhub.domain.user.vo.UserStatus;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

/**
 * UserAdminQueryDslRepository - 사용자 Admin QueryDSL Repository
 *
 * <p>어드민 화면을 위한 확장된 사용자 조회를 담당합니다. Join을 통해 관련 엔티티 정보를 포함한 DTO Projection을 반환합니다.
 *
 * <p><strong>일반 QueryDslRepository와 차이점:</strong>
 *
 * <ul>
 *   <li>✅ Join 허용 (Tenant, Organization 조인)
 *   <li>✅ DTO Projection 직접 반환
 *   <li>✅ 서브쿼리를 통한 집계 정보 포함 (roleCount)
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
public class UserAdminQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    public UserAdminQueryDslRepository(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    /**
     * 사용자 목록 검색 (Admin용 - Summary Projection)
     *
     * <p>Tenant, Organization 조인 및 roleCount 서브쿼리를 포함합니다.
     *
     * @param query 검색 조건
     * @return UserSummaryResponse 목록
     */
    public List<UserSummaryResponse> searchUsers(SearchUsersQuery query) {
        BooleanBuilder builder = buildSearchCondition(query);
        OrderSpecifier<?> orderSpecifier =
                buildOrderSpecifier(query.sortBy(), query.sortDirection());

        return queryFactory
                .select(
                        Projections.constructor(
                                UserSummaryResponse.class,
                                userJpaEntity.userId,
                                userJpaEntity.tenantId,
                                tenantJpaEntity.name,
                                userJpaEntity.organizationId,
                                organizationJpaEntity.name,
                                userJpaEntity.identifier,
                                userJpaEntity.status.stringValue(),
                                Expressions.asNumber(
                                        queryFactory
                                                .select(userRoleJpaEntity.count())
                                                .from(userRoleJpaEntity)
                                                .where(
                                                        userRoleJpaEntity.userId.eq(
                                                                userJpaEntity.userId))),
                                userJpaEntity.createdAt,
                                userJpaEntity.updatedAt))
                .from(userJpaEntity)
                .leftJoin(tenantJpaEntity)
                .on(tenantJpaEntity.tenantId.eq(userJpaEntity.tenantId))
                .leftJoin(organizationJpaEntity)
                .on(organizationJpaEntity.organizationId.eq(userJpaEntity.organizationId))
                .where(builder)
                .orderBy(orderSpecifier)
                .offset((long) query.page() * query.size())
                .limit(query.size())
                .fetch();
    }

    /**
     * 사용자 수 조회
     *
     * @param query 검색 조건
     * @return 사용자 수
     */
    public long countUsers(SearchUsersQuery query) {
        BooleanBuilder builder = buildSearchCondition(query);

        Long count =
                queryFactory
                        .select(userJpaEntity.count())
                        .from(userJpaEntity)
                        .where(builder)
                        .fetchOne();
        return count != null ? count : 0L;
    }

    /**
     * 사용자 상세 조회 (Admin용 - Detail Projection)
     *
     * <p>기본 정보와 할당된 역할 목록을 함께 조회합니다.
     *
     * @param userId 사용자 ID
     * @return UserDetailResponse Optional
     */
    public Optional<UserDetailResponse> findUserDetail(UUID userId) {
        // 기본 정보 조회
        var basicInfo =
                queryFactory
                        .select(
                                Projections.fields(
                                        UserBasicInfo.class,
                                        userJpaEntity.userId.as("userId"),
                                        userJpaEntity.tenantId.as("tenantId"),
                                        tenantJpaEntity.name.as("tenantName"),
                                        userJpaEntity.organizationId.as("organizationId"),
                                        organizationJpaEntity.name.as("organizationName"),
                                        userJpaEntity.identifier.as("identifier"),
                                        userJpaEntity.status.stringValue().as("status"),
                                        userJpaEntity.createdAt.as("createdAt"),
                                        userJpaEntity.updatedAt.as("updatedAt")))
                        .from(userJpaEntity)
                        .leftJoin(tenantJpaEntity)
                        .on(tenantJpaEntity.tenantId.eq(userJpaEntity.tenantId))
                        .leftJoin(organizationJpaEntity)
                        .on(organizationJpaEntity.organizationId.eq(userJpaEntity.organizationId))
                        .where(userJpaEntity.userId.eq(userId))
                        .fetchOne();

        if (basicInfo == null) {
            return Optional.empty();
        }

        // 역할 목록 조회
        List<UserRoleSummary> roles =
                queryFactory
                        .select(
                                Projections.constructor(
                                        UserRoleSummary.class,
                                        roleJpaEntity.roleId,
                                        roleJpaEntity.name,
                                        roleJpaEntity.description,
                                        roleJpaEntity.scope.stringValue(),
                                        roleJpaEntity.type.stringValue()))
                        .from(userRoleJpaEntity)
                        .join(roleJpaEntity)
                        .on(roleJpaEntity.roleId.eq(userRoleJpaEntity.roleId))
                        .where(userRoleJpaEntity.userId.eq(userId), roleJpaEntity.deleted.eq(false))
                        .fetch();

        return Optional.of(
                new UserDetailResponse(
                        basicInfo.userId,
                        basicInfo.tenantId,
                        basicInfo.tenantName,
                        basicInfo.organizationId,
                        basicInfo.organizationName,
                        basicInfo.identifier,
                        basicInfo.status,
                        roles,
                        toInstant(basicInfo.createdAt),
                        toInstant(basicInfo.updatedAt)));
    }

    /** 검색 조건 빌더 생성 */
    private BooleanBuilder buildSearchCondition(SearchUsersQuery query) {
        BooleanBuilder builder = new BooleanBuilder();

        if (query.tenantId() != null) {
            builder.and(userJpaEntity.tenantId.eq(query.tenantId()));
        }
        if (query.organizationId() != null) {
            builder.and(userJpaEntity.organizationId.eq(query.organizationId()));
        }
        if (query.identifier() != null && !query.identifier().isBlank()) {
            builder.and(userJpaEntity.identifier.containsIgnoreCase(query.identifier()));
        }
        if (query.status() != null && !query.status().isBlank()) {
            builder.and(userJpaEntity.status.eq(UserStatus.valueOf(query.status())));
        }
        if (query.roleId() != null) {
            builder.and(
                    userJpaEntity.userId.in(
                            queryFactory
                                    .select(userRoleJpaEntity.userId)
                                    .from(userRoleJpaEntity)
                                    .where(userRoleJpaEntity.roleId.eq(query.roleId()))));
        }
        if (query.createdFrom() != null) {
            builder.and(userJpaEntity.createdAt.goe(toLocalDateTime(query.createdFrom())));
        }
        if (query.createdTo() != null) {
            builder.and(userJpaEntity.createdAt.loe(toLocalDateTime(query.createdTo())));
        }

        return builder;
    }

    /** 정렬 조건 생성 */
    private OrderSpecifier<?> buildOrderSpecifier(String sortBy, String sortDirection) {
        boolean isAsc = "ASC".equalsIgnoreCase(sortDirection);

        return switch (sortBy != null ? sortBy : "createdAt") {
            case "identifier" ->
                    isAsc ? userJpaEntity.identifier.asc() : userJpaEntity.identifier.desc();
            case "updatedAt" ->
                    isAsc ? userJpaEntity.updatedAt.asc() : userJpaEntity.updatedAt.desc();
            default -> isAsc ? userJpaEntity.createdAt.asc() : userJpaEntity.createdAt.desc();
        };
    }

    /** LocalDateTime → Instant 변환 */
    private Instant toInstant(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant();
    }

    /** Instant → LocalDateTime 변환 */
    private LocalDateTime toLocalDateTime(Instant instant) {
        if (instant == null) {
            return null;
        }
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    /** 기본 정보 내부 클래스 (Projection용) */
    private static class UserBasicInfo {
        UUID userId;
        UUID tenantId;
        String tenantName;
        UUID organizationId;
        String organizationName;
        String identifier;
        String status;
        LocalDateTime createdAt;
        LocalDateTime updatedAt;
    }
}
