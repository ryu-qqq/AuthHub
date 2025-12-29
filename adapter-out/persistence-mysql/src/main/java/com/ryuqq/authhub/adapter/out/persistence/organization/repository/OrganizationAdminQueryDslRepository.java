package com.ryuqq.authhub.adapter.out.persistence.organization.repository;

import static com.ryuqq.authhub.adapter.out.persistence.organization.entity.QOrganizationJpaEntity.organizationJpaEntity;
import static com.ryuqq.authhub.adapter.out.persistence.tenant.entity.QTenantJpaEntity.tenantJpaEntity;
import static com.ryuqq.authhub.adapter.out.persistence.user.entity.QUserJpaEntity.userJpaEntity;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.authhub.application.organization.dto.response.OrganizationDetailResponse;
import com.ryuqq.authhub.application.organization.dto.response.OrganizationDetailResponse.OrganizationUserSummary;
import com.ryuqq.authhub.application.organization.dto.response.OrganizationSummaryResponse;
import com.ryuqq.authhub.domain.common.vo.SearchType;
import com.ryuqq.authhub.domain.common.vo.SortDirection;
import com.ryuqq.authhub.domain.organization.query.criteria.OrganizationCriteria;
import com.ryuqq.authhub.domain.organization.vo.OrganizationSortKey;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

/**
 * OrganizationAdminQueryDslRepository - Admin 조직 QueryDSL Repository
 *
 * <p>어드민 친화적 조직 조회를 위한 QueryDSL Repository입니다. DTO Projection을 통해 연관 데이터를 직접 조회합니다.
 *
 * <p><strong>OrganizationQueryDslRepository와의 차이점:</strong>
 *
 * <ul>
 *   <li>DTO 직접 반환 (Entity 변환 불필요)
 *   <li>Join 허용 (tenantName, userCount 조회)
 *   <li>확장 필터 지원 (날짜 범위, 정렬)
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code @Repository} 어노테이션 필수
 *   <li>JPAQueryFactory 주입
 *   <li>Projections.constructor 사용
 *   <li>Criteria 기반 조회 (Query DTO 금지)
 *   <li>Lombok 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Repository
public class OrganizationAdminQueryDslRepository {

    private static final int DEFAULT_USERS_LIMIT = 10;

    private final JPAQueryFactory queryFactory;

    public OrganizationAdminQueryDslRepository(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    /**
     * QueryDSL Projection용 내부 DTO (Admin용)
     *
     * <p>JPA Entity의 LocalDateTime과 Integer 타입을 그대로 받기 위한 중간 DTO입니다. Application
     * DTO(OrganizationSummaryResponse)로 변환 시 Instant와 int로 변환합니다.
     */
    public record OrganizationAdminSummaryProjection(
            UUID organizationId,
            UUID tenantId,
            String tenantName,
            String name,
            String status,
            Integer userCount,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {

        /**
         * Application DTO로 변환
         *
         * @return OrganizationSummaryResponse
         */
        OrganizationSummaryResponse toResponse() {
            return new OrganizationSummaryResponse(
                    organizationId,
                    tenantId,
                    tenantName,
                    name,
                    status,
                    userCount != null ? userCount : 0,
                    createdAt != null ? createdAt.toInstant(ZoneOffset.UTC) : null,
                    updatedAt != null ? updatedAt.toInstant(ZoneOffset.UTC) : null);
        }
    }

    /**
     * QueryDSL Projection용 내부 DTO (Admin UserSummary)
     *
     * <p>JPA Entity의 LocalDateTime 타입을 그대로 받기 위한 중간 DTO입니다.
     */
    public record OrganizationAdminUserProjection(
            UUID userId, String email, LocalDateTime createdAt) {

        /**
         * Application DTO로 변환
         *
         * @return OrganizationUserSummary
         */
        OrganizationUserSummary toResponse() {
            return new OrganizationUserSummary(
                    userId, email, createdAt != null ? createdAt.toInstant(ZoneOffset.UTC) : null);
        }
    }

    /**
     * Admin 목록 검색 (Criteria 기반 DTO Projection)
     *
     * <p>tenantName, userCount를 포함한 Summary DTO를 직접 반환합니다.
     *
     * @param criteria 검색 조건 (OrganizationCriteria)
     * @return 조직 Summary 목록
     */
    public List<OrganizationSummaryResponse> searchOrganizations(OrganizationCriteria criteria) {
        BooleanBuilder builder = buildCondition(criteria);
        OrderSpecifier<?> orderSpecifier = buildOrderSpecifier(criteria);
        int offset = criteria.page().page() * criteria.page().size();
        int limit = criteria.page().size();

        return queryFactory
                .select(
                        Projections.constructor(
                                OrganizationAdminSummaryProjection.class,
                                organizationJpaEntity.organizationId,
                                organizationJpaEntity.tenantId,
                                tenantJpaEntity.name,
                                organizationJpaEntity.name,
                                organizationJpaEntity.status.stringValue(),
                                JPAExpressions.select(userJpaEntity.count().intValue())
                                        .from(userJpaEntity)
                                        .where(
                                                userJpaEntity.organizationId.eq(
                                                        organizationJpaEntity.organizationId)),
                                organizationJpaEntity.createdAt,
                                organizationJpaEntity.updatedAt))
                .from(organizationJpaEntity)
                .leftJoin(tenantJpaEntity)
                .on(tenantJpaEntity.tenantId.eq(organizationJpaEntity.tenantId))
                .where(builder)
                .orderBy(orderSpecifier)
                .offset(offset)
                .limit(limit)
                .fetch()
                .stream()
                .map(OrganizationAdminSummaryProjection::toResponse)
                .toList();
    }

    /**
     * Admin 목록 검색 카운트 (Criteria 기반)
     *
     * @param criteria 검색 조건 (OrganizationCriteria)
     * @return 총 개수
     */
    public long countOrganizations(OrganizationCriteria criteria) {
        BooleanBuilder builder = buildCondition(criteria);

        Long count =
                queryFactory
                        .select(organizationJpaEntity.count())
                        .from(organizationJpaEntity)
                        .where(builder)
                        .fetchOne();
        return count != null ? count : 0L;
    }

    /**
     * Admin 상세 조회 (DTO Projection)
     *
     * <p>tenantName, userCount를 포함한 Detail DTO를 직접 반환합니다.
     *
     * @param organizationId 조직 UUID
     * @return 조직 Detail (Optional)
     */
    public Optional<OrganizationDetailResponse> findOrganizationDetail(UUID organizationId) {
        // 기본 정보 조회 (tenant join)
        OrganizationAdminSummaryProjection projection =
                queryFactory
                        .select(
                                Projections.constructor(
                                        OrganizationAdminSummaryProjection.class,
                                        organizationJpaEntity.organizationId,
                                        organizationJpaEntity.tenantId,
                                        tenantJpaEntity.name,
                                        organizationJpaEntity.name,
                                        organizationJpaEntity.status.stringValue(),
                                        JPAExpressions.select(userJpaEntity.count().intValue())
                                                .from(userJpaEntity)
                                                .where(
                                                        userJpaEntity.organizationId.eq(
                                                                organizationJpaEntity
                                                                        .organizationId)),
                                        organizationJpaEntity.createdAt,
                                        organizationJpaEntity.updatedAt))
                        .from(organizationJpaEntity)
                        .leftJoin(tenantJpaEntity)
                        .on(tenantJpaEntity.tenantId.eq(organizationJpaEntity.tenantId))
                        .where(organizationJpaEntity.organizationId.eq(organizationId))
                        .fetchOne();

        if (projection == null) {
            return Optional.empty();
        }

        // 소속 사용자 목록 조회 (최근 N명)
        List<OrganizationUserSummary> users = findOrganizationUsers(organizationId);

        OrganizationSummaryResponse summary = projection.toResponse();

        return Optional.of(
                new OrganizationDetailResponse(
                        summary.organizationId(),
                        summary.tenantId(),
                        summary.tenantName(),
                        summary.name(),
                        summary.status(),
                        users,
                        summary.userCount(),
                        summary.createdAt(),
                        summary.updatedAt()));
    }

    /**
     * 조직에 소속된 사용자 목록 조회 (최근 N명)
     *
     * @param organizationId 조직 UUID
     * @return 사용자 Summary 목록
     */
    private List<OrganizationUserSummary> findOrganizationUsers(UUID organizationId) {
        return queryFactory
                .select(
                        Projections.constructor(
                                OrganizationAdminUserProjection.class,
                                userJpaEntity.userId,
                                userJpaEntity.identifier,
                                userJpaEntity.createdAt))
                .from(userJpaEntity)
                .where(userJpaEntity.organizationId.eq(organizationId))
                .orderBy(userJpaEntity.createdAt.desc())
                .limit(DEFAULT_USERS_LIMIT)
                .fetch()
                .stream()
                .map(OrganizationAdminUserProjection::toResponse)
                .toList();
    }

    /**
     * 검색 조건 빌더 생성 (Criteria 기반)
     *
     * @param criteria 검색 조건
     * @return BooleanBuilder
     */
    private BooleanBuilder buildCondition(OrganizationCriteria criteria) {
        BooleanBuilder builder = new BooleanBuilder();

        // 테넌트 ID 필터 (선택)
        if (criteria.hasTenantFilter()) {
            builder.and(organizationJpaEntity.tenantId.eq(criteria.tenantId().value()));
        }

        // 이름 필터 (SearchType 기반)
        if (criteria.hasNameFilter()) {
            builder.and(buildNameCondition(criteria.name(), criteria.nameSearchType()));
        }

        // 다중 상태 필터
        if (criteria.hasStatusFilter()) {
            builder.and(organizationJpaEntity.status.in(criteria.statuses()));
        }

        // 생성일시 범위 필터
        if (criteria.dateRange() != null) {
            if (criteria.dateRange().startDate() != null) {
                LocalDateTime from = criteria.dateRange().startDate().atStartOfDay();
                builder.and(organizationJpaEntity.createdAt.goe(from));
            }
            if (criteria.dateRange().endDate() != null) {
                LocalDateTime to = criteria.dateRange().endDate().atTime(23, 59, 59);
                builder.and(organizationJpaEntity.createdAt.loe(to));
            }
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
    private BooleanExpression buildNameCondition(String name, SearchType searchType) {
        SearchType type = searchType != null ? searchType : SearchType.CONTAINS_LIKE;

        return switch (type) {
            case PREFIX_LIKE -> organizationJpaEntity.name.startsWithIgnoreCase(name);
            case MATCH_AGAINST -> organizationJpaEntity.name.containsIgnoreCase(name);
            case CONTAINS_LIKE -> organizationJpaEntity.name.containsIgnoreCase(name);
        };
    }

    /**
     * 정렬 조건 생성 (Criteria 기반)
     *
     * @param criteria 검색 조건
     * @return OrderSpecifier
     */
    private OrderSpecifier<?> buildOrderSpecifier(OrganizationCriteria criteria) {
        OrganizationSortKey sortKey =
                criteria.sortKey() != null ? criteria.sortKey() : OrganizationSortKey.defaultKey();
        SortDirection direction =
                criteria.sortDirection() != null
                        ? criteria.sortDirection()
                        : SortDirection.defaultDirection();
        boolean isAsc = direction == SortDirection.ASC;

        return switch (sortKey) {
            case NAME ->
                    isAsc ? organizationJpaEntity.name.asc() : organizationJpaEntity.name.desc();
            case STATUS ->
                    isAsc
                            ? organizationJpaEntity.status.asc()
                            : organizationJpaEntity.status.desc();
            case UPDATED_AT ->
                    isAsc
                            ? organizationJpaEntity.updatedAt.asc()
                            : organizationJpaEntity.updatedAt.desc();
            case CREATED_AT ->
                    isAsc
                            ? organizationJpaEntity.createdAt.asc()
                            : organizationJpaEntity.createdAt.desc();
        };
    }
}
