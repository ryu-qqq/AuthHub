package com.ryuqq.authhub.adapter.out.persistence.role.condition;

import static com.ryuqq.authhub.adapter.out.persistence.role.entity.QRoleJpaEntity.roleJpaEntity;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.ryuqq.authhub.domain.common.vo.SortDirection;
import com.ryuqq.authhub.domain.role.query.criteria.RoleSearchCriteria;
import com.ryuqq.authhub.domain.role.vo.RoleSearchField;
import com.ryuqq.authhub.domain.role.vo.RoleSortKey;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * RoleConditionBuilder - 역할 QueryDSL 조건 빌더
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
 * <p><strong>사용 위치:</strong>
 *
 * <ul>
 *   <li>RoleQueryDslRepository
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
public class RoleConditionBuilder {

    /**
     * 검색 조건 빌더 생성
     *
     * <p>RoleSearchCriteria 기반으로 BooleanBuilder를 생성합니다.
     *
     * <p><strong>테넌트 조회 로직:</strong>
     *
     * <ul>
     *   <li>tenantId가 null이면 Global 역할만 조회 (tenantId IS NULL)
     *   <li>tenantId가 있으면 해당 테넌트 역할 + Global 역할 조회
     * </ul>
     *
     * @param criteria 검색 조건
     * @return BooleanBuilder
     */
    public BooleanBuilder buildCondition(RoleSearchCriteria criteria) {
        BooleanBuilder builder = new BooleanBuilder();

        // 삭제되지 않은 데이터만 조회 (includeDeleted가 false인 경우)
        if (!criteria.includeDeleted()) {
            builder.and(notDeleted());
        }

        // 테넌트 필터
        builder.and(tenantCondition(criteria));

        // 검색어 + 검색 필드 조건
        builder.and(searchByField(criteria.searchField(), criteria.searchWord()));

        // 역할 유형 필터
        if (criteria.hasTypeFilter()) {
            builder.and(roleJpaEntity.type.in(criteria.types()));
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
    public OrderSpecifier<?> buildOrderSpecifier(RoleSearchCriteria criteria) {
        RoleSortKey sortKey =
                criteria.sortKey() != null ? criteria.sortKey() : RoleSortKey.CREATED_AT;
        boolean isAsc = criteria.sortDirection() == SortDirection.ASC;

        return switch (sortKey) {
            case ROLE_ID -> isAsc ? roleJpaEntity.roleId.asc() : roleJpaEntity.roleId.desc();
            case NAME -> isAsc ? roleJpaEntity.name.asc() : roleJpaEntity.name.desc();
            case DISPLAY_NAME ->
                    isAsc ? roleJpaEntity.displayName.asc() : roleJpaEntity.displayName.desc();
            case UPDATED_AT ->
                    isAsc ? roleJpaEntity.updatedAt.asc() : roleJpaEntity.updatedAt.desc();
            case CREATED_AT ->
                    isAsc ? roleJpaEntity.createdAt.asc() : roleJpaEntity.createdAt.desc();
        };
    }

    /**
     * 테넌트 조건 생성
     *
     * <p>tenantId가 null이면 Global 역할만 조회, 있으면 해당 테넌트 + Global 역할 조회.
     *
     * @param criteria 검색 조건
     * @return BooleanExpression (null 허용)
     */
    public BooleanExpression tenantCondition(RoleSearchCriteria criteria) {
        if (criteria.isGlobalOnly()) {
            // Global 역할만 조회 (tenantId IS NULL)
            return roleJpaEntity.tenantId.isNull();
        }
        // 해당 테넌트 + Global 역할 조회
        return roleJpaEntity
                .tenantId
                .eq(criteria.tenantId().value())
                .or(roleJpaEntity.tenantId.isNull());
    }

    /**
     * SearchField 기반 검색 조건 생성
     *
     * <p>검색 필드(RoleSearchField)에 따라 해당 컬럼에 검색어를 적용합니다.
     *
     * @param searchField 검색 필드 (RoleSearchField enum)
     * @param searchWord 검색어
     * @return BooleanExpression (null 허용 - 조건 없음)
     */
    public BooleanExpression searchByField(RoleSearchField searchField, String searchWord) {
        if (searchField == null || searchWord == null || searchWord.isBlank()) {
            return null;
        }

        return switch (searchField) {
            case NAME -> roleJpaEntity.name.containsIgnoreCase(searchWord);
            case DISPLAY_NAME -> roleJpaEntity.displayName.containsIgnoreCase(searchWord);
            case DESCRIPTION -> roleJpaEntity.description.containsIgnoreCase(searchWord);
        };
    }

    /**
     * 삭제되지 않은 데이터 조건
     *
     * @return BooleanExpression
     */
    public BooleanExpression notDeleted() {
        return roleJpaEntity.deletedAt.isNull();
    }

    /**
     * 역할 ID 일치 조건
     *
     * @param roleId 역할 ID (Long)
     * @return BooleanExpression (null 허용)
     */
    public BooleanExpression roleIdEquals(Long roleId) {
        return roleId != null ? roleJpaEntity.roleId.eq(roleId) : null;
    }

    /**
     * 역할 이름 일치 조건
     *
     * @param name 역할 이름
     * @return BooleanExpression (null 허용)
     */
    public BooleanExpression nameEquals(String name) {
        return name != null ? roleJpaEntity.name.eq(name) : null;
    }

    /**
     * 테넌트 ID 일치 조건 (nullable)
     *
     * @param tenantId 테넌트 ID (null이면 IS NULL 조건)
     * @return BooleanExpression
     */
    public BooleanExpression tenantIdEquals(String tenantId) {
        return tenantId != null
                ? roleJpaEntity.tenantId.eq(tenantId)
                : roleJpaEntity.tenantId.isNull();
    }

    /**
     * 역할 ID 목록 포함 조건
     *
     * @param roleIds 역할 ID 목록
     * @return BooleanExpression (null 허용)
     */
    public BooleanExpression roleIdIn(List<Long> roleIds) {
        return roleIds != null && !roleIds.isEmpty() ? roleJpaEntity.roleId.in(roleIds) : null;
    }

    /**
     * 생성일시 이상 조건
     *
     * @param startInstant 시작 일시 (Instant, UTC)
     * @return BooleanExpression (null 허용)
     */
    public BooleanExpression createdAtGoe(Instant startInstant) {
        return startInstant != null ? roleJpaEntity.createdAt.goe(startInstant) : null;
    }

    /**
     * 생성일시 이하 조건
     *
     * @param endInstant 종료 일시 (Instant, UTC)
     * @return BooleanExpression (null 허용)
     */
    public BooleanExpression createdAtLoe(Instant endInstant) {
        return endInstant != null ? roleJpaEntity.createdAt.loe(endInstant) : null;
    }
}
