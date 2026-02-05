package com.ryuqq.authhub.adapter.out.persistence.permission.condition;

import static com.ryuqq.authhub.adapter.out.persistence.permission.entity.QPermissionJpaEntity.permissionJpaEntity;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.ryuqq.authhub.domain.common.vo.SortDirection;
import com.ryuqq.authhub.domain.permission.query.criteria.PermissionSearchCriteria;
import com.ryuqq.authhub.domain.permission.vo.PermissionSearchField;
import com.ryuqq.authhub.domain.permission.vo.PermissionSortKey;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * PermissionConditionBuilder - 권한 QueryDSL 조건 빌더 (Global Only)
 *
 * <p>QueryDSL 조건 생성 로직을 분리하여 Repository의 책임을 단순화합니다.
 *
 * <p><strong>Global Only 설계:</strong>
 *
 * <ul>
 *   <li>모든 Permission은 전체 시스템에서 공유됩니다
 *   <li>테넌트 관련 조건이 제거되었습니다
 * </ul>
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
 *   <li>PermissionQueryDslRepository
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
public class PermissionConditionBuilder {

    /**
     * 검색 조건 빌더 생성
     *
     * <p>PermissionSearchCriteria 기반으로 BooleanBuilder를 생성합니다.
     *
     * @param criteria 검색 조건
     * @return BooleanBuilder
     */
    public BooleanBuilder buildCondition(PermissionSearchCriteria criteria) {
        BooleanBuilder builder = new BooleanBuilder();

        // 삭제되지 않은 데이터만 조회 (includeDeleted가 false인 경우)
        if (!criteria.includeDeleted()) {
            builder.and(notDeleted());
        }

        // 서비스 ID 필터
        if (criteria.hasServiceIdFilter()) {
            builder.and(serviceIdEquals(criteria.serviceId()));
        }

        // 검색어 + 검색 필드 조건
        builder.and(searchByField(criteria.searchField(), criteria.searchWord()));

        // 권한 유형 필터
        if (criteria.hasTypeFilter()) {
            builder.and(permissionJpaEntity.type.in(criteria.types()));
        }

        // 리소스 필터
        if (criteria.hasResourceFilter()) {
            builder.and(permissionJpaEntity.resource.in(criteria.resources()));
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
    public OrderSpecifier<?> buildOrderSpecifier(PermissionSearchCriteria criteria) {
        PermissionSortKey sortKey =
                criteria.sortKey() != null ? criteria.sortKey() : PermissionSortKey.CREATED_AT;
        boolean isAsc = criteria.sortDirection() == SortDirection.ASC;

        return switch (sortKey) {
            case PERMISSION_ID ->
                    isAsc
                            ? permissionJpaEntity.permissionId.asc()
                            : permissionJpaEntity.permissionId.desc();
            case UPDATED_AT ->
                    isAsc
                            ? permissionJpaEntity.updatedAt.asc()
                            : permissionJpaEntity.updatedAt.desc();
            case PERMISSION_KEY ->
                    isAsc
                            ? permissionJpaEntity.permissionKey.asc()
                            : permissionJpaEntity.permissionKey.desc();
            case RESOURCE ->
                    isAsc
                            ? permissionJpaEntity.resource.asc()
                            : permissionJpaEntity.resource.desc();
            case CREATED_AT ->
                    isAsc
                            ? permissionJpaEntity.createdAt.asc()
                            : permissionJpaEntity.createdAt.desc();
        };
    }

    /**
     * SearchField 기반 검색 조건 생성
     *
     * <p>검색 필드(PermissionSearchField)에 따라 해당 컬럼에 검색어를 적용합니다.
     *
     * @param searchField 검색 필드 (PermissionSearchField enum)
     * @param searchWord 검색어
     * @return BooleanExpression (null 허용 - 조건 없음)
     */
    public BooleanExpression searchByField(PermissionSearchField searchField, String searchWord) {
        if (searchField == null || searchWord == null || searchWord.isBlank()) {
            return null;
        }

        return switch (searchField) {
            case PERMISSION_KEY -> permissionJpaEntity.permissionKey.containsIgnoreCase(searchWord);
            case RESOURCE -> permissionJpaEntity.resource.containsIgnoreCase(searchWord);
            case ACTION -> permissionJpaEntity.action.containsIgnoreCase(searchWord);
            case DESCRIPTION -> permissionJpaEntity.description.containsIgnoreCase(searchWord);
        };
    }

    /**
     * 삭제되지 않은 데이터 조건
     *
     * @return BooleanExpression
     */
    public BooleanExpression notDeleted() {
        return permissionJpaEntity.deletedAt.isNull();
    }

    /**
     * 권한 ID 일치 조건
     *
     * @param permissionId 권한 ID (Long)
     * @return BooleanExpression (null 허용)
     */
    public BooleanExpression permissionIdEquals(Long permissionId) {
        return permissionId != null ? permissionJpaEntity.permissionId.eq(permissionId) : null;
    }

    /**
     * 권한 키 일치 조건
     *
     * @param permissionKey 권한 키
     * @return BooleanExpression (null 허용)
     */
    public BooleanExpression permissionKeyEquals(String permissionKey) {
        return permissionKey != null ? permissionJpaEntity.permissionKey.eq(permissionKey) : null;
    }

    /**
     * 권한 ID 목록 포함 조건
     *
     * @param permissionIds 권한 ID 목록
     * @return BooleanExpression (null 허용)
     */
    public BooleanExpression permissionIdIn(List<Long> permissionIds) {
        return permissionIds != null && !permissionIds.isEmpty()
                ? permissionJpaEntity.permissionId.in(permissionIds)
                : null;
    }

    /**
     * 생성일시 이상 조건
     *
     * @param startInstant 시작 일시 (Instant, UTC)
     * @return BooleanExpression (null 허용)
     */
    public BooleanExpression createdAtGoe(Instant startInstant) {
        return startInstant != null ? permissionJpaEntity.createdAt.goe(startInstant) : null;
    }

    /**
     * 생성일시 이하 조건
     *
     * @param endInstant 종료 일시 (Instant, UTC)
     * @return BooleanExpression (null 허용)
     */
    public BooleanExpression createdAtLoe(Instant endInstant) {
        return endInstant != null ? permissionJpaEntity.createdAt.loe(endInstant) : null;
    }

    /**
     * 권한 키 목록 포함 조건
     *
     * @param permissionKeys 권한 키 목록
     * @return BooleanExpression (null 허용)
     */
    public BooleanExpression permissionKeyIn(List<String> permissionKeys) {
        return permissionKeys != null && !permissionKeys.isEmpty()
                ? permissionJpaEntity.permissionKey.in(permissionKeys)
                : null;
    }

    /**
     * 서비스 ID 일치 조건
     *
     * @param serviceId 서비스 ID (Long)
     * @return BooleanExpression (null 허용)
     */
    public BooleanExpression serviceIdEquals(Long serviceId) {
        return serviceId != null ? permissionJpaEntity.serviceId.eq(serviceId) : null;
    }

    /**
     * 서비스 ID 목록 포함 조건
     *
     * @param serviceIds 서비스 ID 목록
     * @return BooleanExpression (null 허용)
     */
    public BooleanExpression serviceIdIn(List<Long> serviceIds) {
        return serviceIds != null && !serviceIds.isEmpty()
                ? permissionJpaEntity.serviceId.in(serviceIds)
                : null;
    }
}
