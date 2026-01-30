package com.ryuqq.authhub.adapter.out.persistence.organization.condition;

import static com.ryuqq.authhub.adapter.out.persistence.organization.entity.QOrganizationJpaEntity.organizationJpaEntity;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.ryuqq.authhub.domain.common.vo.SortDirection;
import com.ryuqq.authhub.domain.organization.query.criteria.OrganizationSearchCriteria;
import com.ryuqq.authhub.domain.organization.vo.OrganizationSearchField;
import com.ryuqq.authhub.domain.organization.vo.OrganizationSortKey;
import com.ryuqq.authhub.domain.tenant.id.TenantId;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * OrganizationConditionBuilder - 조직 QueryDSL 조건 빌더
 *
 * <p>QueryDSL 조건 생성 로직을 분리하여 Repository의 책임을 단순화합니다.
 *
 * <p><strong>책임:</strong>
 *
 * <ul>
 *   <li>buildCondition() - 검색 조건 BooleanBuilder 생성
 *   <li>buildOrderSpecifier() - 정렬 조건 OrderSpecifier 생성
 *   <li>searchByField() - SearchField 기반 검색 조건 생성
 * </ul>
 *
 * <p><strong>규칙:</strong>
 *
 * <ul>
 *   <li>Entity 직접 의존 금지 (Q-Class만 사용)
 *   <li>비즈니스 로직 금지 (조건 생성만)
 *   <li>null-safe 조건 처리
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class OrganizationConditionBuilder {

    /**
     * 검색 조건 빌더 생성
     *
     * <p>OrganizationSearchCriteria 기반으로 BooleanBuilder를 생성합니다.
     *
     * @param criteria 검색 조건
     * @return BooleanBuilder
     */
    public BooleanBuilder buildCondition(OrganizationSearchCriteria criteria) {
        BooleanBuilder builder = new BooleanBuilder();

        // 테넌트 ID 목록 필터
        builder.and(tenantIdsIn(criteria.tenantIds()));

        // 검색어 + 검색 필드 조건
        builder.and(searchByField(criteria.searchField(), criteria.searchWord()));

        // 다중 상태 필터
        if (criteria.hasStatusFilter()) {
            builder.and(organizationJpaEntity.status.in(criteria.statuses()));
        }

        // 날짜 범위 필터 (Law of Demeter 준수 - 편의 메서드 사용)
        builder.and(createdAtGoe(criteria.startInstant()));
        builder.and(createdAtLoe(criteria.endInstant()));

        return builder;
    }

    /**
     * 정렬 조건 빌더 생성
     *
     * @param criteria 검색 조건
     * @return OrderSpecifier
     */
    public OrderSpecifier<?> buildOrderSpecifier(OrganizationSearchCriteria criteria) {
        OrganizationSortKey sortKey =
                criteria.sortKey() != null ? criteria.sortKey() : OrganizationSortKey.CREATED_AT;
        boolean isAsc = criteria.sortDirection() == SortDirection.ASC;

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

    /**
     * 테넌트 ID 목록 포함 조건
     *
     * @param tenantIds 테넌트 ID 목록
     * @return BooleanExpression (null 허용)
     */
    public BooleanExpression tenantIdsIn(List<TenantId> tenantIds) {
        if (tenantIds == null || tenantIds.isEmpty()) {
            return null;
        }
        List<String> ids = tenantIds.stream().map(TenantId::value).toList();
        return organizationJpaEntity.tenantId.in(ids);
    }

    /**
     * SearchField 기반 검색 조건 생성
     *
     * <p>검색 필드(OrganizationSearchField)에 따라 해당 컬럼에 검색어를 적용합니다.
     *
     * @param searchField 검색 필드 (OrganizationSearchField enum)
     * @param searchWord 검색어
     * @return BooleanExpression (null 허용 - 조건 없음)
     */
    public BooleanExpression searchByField(OrganizationSearchField searchField, String searchWord) {
        if (searchField == null || searchWord == null || searchWord.isBlank()) {
            return null;
        }

        return switch (searchField) {
            case NAME -> organizationJpaEntity.name.containsIgnoreCase(searchWord);
        };
    }

    /**
     * 조직 ID 일치 조건
     *
     * @param organizationId 조직 ID (String)
     * @return BooleanExpression (null 허용)
     */
    public BooleanExpression organizationIdEquals(String organizationId) {
        return organizationId != null
                ? organizationJpaEntity.organizationId.eq(organizationId)
                : null;
    }

    /**
     * 테넌트 ID 일치 조건
     *
     * @param tenantId 테넌트 ID (String)
     * @return BooleanExpression (null 허용)
     */
    public BooleanExpression tenantIdEquals(String tenantId) {
        return tenantId != null ? organizationJpaEntity.tenantId.eq(tenantId) : null;
    }

    /**
     * 이름 일치 조건
     *
     * @param name 조직 이름
     * @return BooleanExpression (null 허용)
     */
    public BooleanExpression nameEquals(String name) {
        return name != null ? organizationJpaEntity.name.eq(name) : null;
    }

    /**
     * 생성일시 이상 조건
     *
     * @param startInstant 시작 일시 (Instant, UTC)
     * @return BooleanExpression (null 허용)
     */
    public BooleanExpression createdAtGoe(Instant startInstant) {
        return startInstant != null ? organizationJpaEntity.createdAt.goe(startInstant) : null;
    }

    /**
     * 생성일시 이하 조건
     *
     * @param endInstant 종료 일시 (Instant, UTC)
     * @return BooleanExpression (null 허용)
     */
    public BooleanExpression createdAtLoe(Instant endInstant) {
        return endInstant != null ? organizationJpaEntity.createdAt.loe(endInstant) : null;
    }
}
