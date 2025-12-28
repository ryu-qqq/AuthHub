package com.ryuqq.authhub.adapter.out.persistence.tenant.repository;

import static com.ryuqq.authhub.adapter.out.persistence.organization.entity.QOrganizationJpaEntity.organizationJpaEntity;
import static com.ryuqq.authhub.adapter.out.persistence.tenant.entity.QTenantJpaEntity.tenantJpaEntity;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.authhub.application.tenant.dto.query.SearchTenantsQuery;
import com.ryuqq.authhub.application.tenant.dto.response.TenantDetailResponse;
import com.ryuqq.authhub.application.tenant.dto.response.TenantDetailResponse.TenantOrganizationSummary;
import com.ryuqq.authhub.application.tenant.dto.response.TenantSummaryResponse;
import com.ryuqq.authhub.domain.tenant.vo.TenantStatus;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

/**
 * TenantAdminQueryDslRepository - Admin 테넌트 QueryDSL Repository (DTO Projection)
 *
 * <p>어드민 친화적 조회를 위한 QueryDSL Repository입니다. DTO Projection을 통해 organizationCount를 포함한 데이터를 직접
 * 조회합니다.
 *
 * <p><strong>TenantQueryDslRepository와의 차이점:</strong>
 *
 * <ul>
 *   <li>DTO 직접 반환 (Entity 반환 금지)
 *   <li>JPAExpressions 서브쿼리로 organizationCount 조회
 *   <li>확장 필터 지원 (날짜 범위, 정렬)
 * </ul>
 *
 * <p><strong>허용된 Join/Subquery:</strong>
 *
 * <ul>
 *   <li>organizationCount: organizations 테이블 서브쿼리
 *   <li>organizations 목록: 상세 조회 시 조직 정보 조회
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code @Repository} 어노테이션 필수
 *   <li>비즈니스 로직 금지
 *   <li>Lombok 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 * @see TenantQueryDslRepository Domain 조회 Repository
 */
@Repository
public class TenantAdminQueryDslRepository {

    private static final int MAX_ORGANIZATIONS_IN_DETAIL = 10;

    private final JPAQueryFactory queryFactory;

    public TenantAdminQueryDslRepository(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    /**
     * QueryDSL Projection용 내부 DTO (Admin Summary용)
     *
     * <p>JPA Entity의 LocalDateTime과 Integer 타입을 그대로 받기 위한 중간 DTO입니다. Application
     * DTO(TenantSummaryResponse)로 변환 시 Instant와 int로 변환합니다.
     */
    public record TenantAdminSummaryProjection(
            UUID tenantId,
            String name,
            String status,
            Integer organizationCount,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {

        /**
         * Application DTO로 변환
         *
         * @return TenantSummaryResponse
         */
        TenantSummaryResponse toResponse() {
            return new TenantSummaryResponse(
                    tenantId,
                    name,
                    status,
                    organizationCount != null ? organizationCount : 0,
                    createdAt != null ? createdAt.toInstant(ZoneOffset.UTC) : null,
                    updatedAt != null ? updatedAt.toInstant(ZoneOffset.UTC) : null);
        }
    }

    /**
     * QueryDSL Projection용 내부 DTO (Admin Organization Summary용)
     *
     * <p>JPA Entity의 LocalDateTime 타입을 그대로 받기 위한 중간 DTO입니다.
     */
    public record TenantAdminOrganizationProjection(
            UUID organizationId, String name, String status, LocalDateTime createdAt) {

        /**
         * Application DTO로 변환
         *
         * @return TenantOrganizationSummary
         */
        TenantOrganizationSummary toResponse() {
            return new TenantOrganizationSummary(
                    organizationId,
                    name,
                    status,
                    createdAt != null ? createdAt.toInstant(ZoneOffset.UTC) : null);
        }
    }

    /**
     * Admin 테넌트 목록 검색 (확장 필터 + organizationCount)
     *
     * <p>organizationCount 서브쿼리를 포함한 Summary DTO를 직접 조회합니다.
     *
     * @param query 검색 조건 (확장 필터 포함)
     * @return TenantSummaryResponse 목록
     */
    public List<TenantSummaryResponse> searchTenants(SearchTenantsQuery query) {
        BooleanBuilder condition = buildCondition(query);
        OrderSpecifier<?> orderSpecifier = buildOrderSpecifier(query);

        return queryFactory
                .select(
                        Projections.constructor(
                                TenantAdminSummaryProjection.class,
                                tenantJpaEntity.tenantId,
                                tenantJpaEntity.name,
                                tenantJpaEntity.status.stringValue(),
                                JPAExpressions.select(organizationJpaEntity.count().intValue())
                                        .from(organizationJpaEntity)
                                        .where(
                                                organizationJpaEntity.tenantId.eq(
                                                        tenantJpaEntity.tenantId)),
                                tenantJpaEntity.createdAt,
                                tenantJpaEntity.updatedAt))
                .from(tenantJpaEntity)
                .where(condition)
                .orderBy(orderSpecifier)
                .offset((long) query.page() * query.size())
                .limit(query.size())
                .fetch()
                .stream()
                .map(TenantAdminSummaryProjection::toResponse)
                .toList();
    }

    /**
     * Admin 테넌트 개수 조회 (확장 필터)
     *
     * @param query 검색 조건 (확장 필터 포함)
     * @return 조건에 맞는 테넌트 총 개수
     */
    public long countTenants(SearchTenantsQuery query) {
        BooleanBuilder condition = buildCondition(query);

        Long count =
                queryFactory
                        .select(tenantJpaEntity.count())
                        .from(tenantJpaEntity)
                        .where(condition)
                        .fetchOne();
        return count != null ? count : 0L;
    }

    /**
     * Admin 테넌트 상세 조회 (organizations 목록 포함)
     *
     * <p>테넌트 기본 정보와 소속 조직 목록을 함께 조회합니다.
     *
     * @param tenantId 테넌트 ID
     * @return TenantDetailResponse (Optional)
     */
    public Optional<TenantDetailResponse> findTenantDetail(UUID tenantId) {
        // 1. 테넌트 기본 정보 조회
        TenantAdminSummaryProjection summaryProjection =
                queryFactory
                        .select(
                                Projections.constructor(
                                        TenantAdminSummaryProjection.class,
                                        tenantJpaEntity.tenantId,
                                        tenantJpaEntity.name,
                                        tenantJpaEntity.status.stringValue(),
                                        JPAExpressions.select(
                                                        organizationJpaEntity.count().intValue())
                                                .from(organizationJpaEntity)
                                                .where(
                                                        organizationJpaEntity.tenantId.eq(
                                                                tenantJpaEntity.tenantId)),
                                        tenantJpaEntity.createdAt,
                                        tenantJpaEntity.updatedAt))
                        .from(tenantJpaEntity)
                        .where(tenantJpaEntity.tenantId.eq(tenantId))
                        .fetchOne();

        if (summaryProjection == null) {
            return Optional.empty();
        }

        // 2. 소속 조직 목록 조회 (최근 N개)
        List<TenantOrganizationSummary> organizations =
                queryFactory
                        .select(
                                Projections.constructor(
                                        TenantAdminOrganizationProjection.class,
                                        organizationJpaEntity.organizationId,
                                        organizationJpaEntity.name,
                                        organizationJpaEntity.status.stringValue(),
                                        organizationJpaEntity.createdAt))
                        .from(organizationJpaEntity)
                        .where(organizationJpaEntity.tenantId.eq(tenantId))
                        .orderBy(organizationJpaEntity.createdAt.desc())
                        .limit(MAX_ORGANIZATIONS_IN_DETAIL)
                        .fetch()
                        .stream()
                        .map(TenantAdminOrganizationProjection::toResponse)
                        .toList();

        // 3. Detail DTO 조합
        TenantSummaryResponse summary = summaryProjection.toResponse();
        return Optional.of(
                new TenantDetailResponse(
                        summary.tenantId(),
                        summary.name(),
                        summary.status(),
                        organizations,
                        summary.organizationCount(),
                        summary.createdAt(),
                        summary.updatedAt()));
    }

    /**
     * 검색 조건 빌더 생성
     *
     * @param query 검색 조건
     * @return BooleanBuilder
     */
    private BooleanBuilder buildCondition(SearchTenantsQuery query) {
        BooleanBuilder builder = new BooleanBuilder();

        if (query.name() != null && !query.name().isBlank()) {
            builder.and(tenantJpaEntity.name.containsIgnoreCase(query.name()));
        }
        if (query.status() != null && !query.status().isBlank()) {
            TenantStatus status = parseStatus(query.status());
            if (status != null) {
                builder.and(tenantJpaEntity.status.eq(status));
            }
        }
        if (query.createdFrom() != null) {
            LocalDateTime fromDateTime =
                    LocalDateTime.ofInstant(query.createdFrom(), ZoneId.systemDefault());
            builder.and(tenantJpaEntity.createdAt.goe(fromDateTime));
        }
        if (query.createdTo() != null) {
            LocalDateTime toDateTime =
                    LocalDateTime.ofInstant(query.createdTo(), ZoneId.systemDefault());
            builder.and(tenantJpaEntity.createdAt.loe(toDateTime));
        }

        return builder;
    }

    /**
     * 정렬 조건 빌더 생성
     *
     * @param query 검색 조건
     * @return OrderSpecifier
     */
    private OrderSpecifier<?> buildOrderSpecifier(SearchTenantsQuery query) {
        String sortBy = query.sortBy() != null ? query.sortBy() : "createdAt";
        boolean isAsc = "ASC".equalsIgnoreCase(query.sortDirection());

        return switch (sortBy.toLowerCase(Locale.ENGLISH)) {
            case "name" -> isAsc ? tenantJpaEntity.name.asc() : tenantJpaEntity.name.desc();
            case "status" -> isAsc ? tenantJpaEntity.status.asc() : tenantJpaEntity.status.desc();
            case "updatedat" ->
                    isAsc ? tenantJpaEntity.updatedAt.asc() : tenantJpaEntity.updatedAt.desc();
            default -> isAsc ? tenantJpaEntity.createdAt.asc() : tenantJpaEntity.createdAt.desc();
        };
    }

    /**
     * 문자열을 TenantStatus로 변환
     *
     * @param status 상태 문자열
     * @return TenantStatus (유효하지 않으면 null)
     */
    private TenantStatus parseStatus(String status) {
        try {
            return TenantStatus.valueOf(status.toUpperCase(Locale.ENGLISH));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
