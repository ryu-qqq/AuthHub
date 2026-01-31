package com.ryuqq.authhub.adapter.out.persistence.rolepermission.condition;

import static com.ryuqq.authhub.adapter.out.persistence.rolepermission.entity.QRolePermissionJpaEntity.rolePermissionJpaEntity;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.ryuqq.authhub.domain.common.vo.SortDirection;
import com.ryuqq.authhub.domain.rolepermission.query.criteria.RolePermissionSearchCriteria;
import com.ryuqq.authhub.domain.rolepermission.vo.RolePermissionSortKey;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * RolePermissionConditionBuilder - 역할-권한 관계 QueryDSL 조건 빌더
 *
 * <p>역할-권한 관계 조회 시 사용하는 QueryDSL 조건을 생성합니다.
 *
 * <p><strong>책임:</strong>
 *
 * <ul>
 *   <li>단일 조건 생성 메서드 (roleIdEquals, permissionIdEquals 등)
 *   <li>복합 조건 생성 메서드 (buildCondition)
 *   <li>정렬 조건 생성 메서드 (buildOrderSpecifier)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class RolePermissionConditionBuilder {

    // ========== 단일 조건 메서드 ==========

    /**
     * 관계 ID 일치 조건
     *
     * @param rolePermissionId 관계 ID
     * @return BooleanExpression
     */
    public BooleanExpression rolePermissionIdEquals(Long rolePermissionId) {
        return rolePermissionId != null
                ? rolePermissionJpaEntity.rolePermissionId.eq(rolePermissionId)
                : null;
    }

    /**
     * 역할 ID 일치 조건
     *
     * @param roleId 역할 ID
     * @return BooleanExpression
     */
    public BooleanExpression roleIdEquals(Long roleId) {
        return roleId != null ? rolePermissionJpaEntity.roleId.eq(roleId) : null;
    }

    /**
     * 역할 ID 목록 조건
     *
     * @param roleIds 역할 ID 목록
     * @return BooleanExpression
     */
    public BooleanExpression roleIdIn(List<Long> roleIds) {
        return roleIds != null && !roleIds.isEmpty()
                ? rolePermissionJpaEntity.roleId.in(roleIds)
                : null;
    }

    /**
     * 권한 ID 일치 조건
     *
     * @param permissionId 권한 ID
     * @return BooleanExpression
     */
    public BooleanExpression permissionIdEquals(Long permissionId) {
        return permissionId != null ? rolePermissionJpaEntity.permissionId.eq(permissionId) : null;
    }

    /**
     * 권한 ID 목록 조건
     *
     * @param permissionIds 권한 ID 목록
     * @return BooleanExpression
     */
    public BooleanExpression permissionIdIn(List<Long> permissionIds) {
        return permissionIds != null && !permissionIds.isEmpty()
                ? rolePermissionJpaEntity.permissionId.in(permissionIds)
                : null;
    }

    // ========== 복합 조건 메서드 ==========

    /**
     * RolePermissionSearchCriteria로부터 복합 조건 생성
     *
     * @param criteria 검색 조건
     * @return BooleanBuilder
     */
    public BooleanBuilder buildCondition(RolePermissionSearchCriteria criteria) {
        BooleanBuilder builder = new BooleanBuilder();

        // 역할 ID 필터 (단건)
        if (criteria.hasRoleIdFilter()) {
            builder.and(roleIdEquals(criteria.roleId().value()));
        }

        // 역할 ID 필터 (다건)
        if (criteria.hasRoleIdsFilter()) {
            List<Long> roleIds = criteria.roleIds().stream().map(roleId -> roleId.value()).toList();
            builder.and(roleIdIn(roleIds));
        }

        // 권한 ID 필터 (단건)
        if (criteria.hasPermissionIdFilter()) {
            builder.and(permissionIdEquals(criteria.permissionId().value()));
        }

        // 권한 ID 필터 (다건)
        if (criteria.hasPermissionIdsFilter()) {
            List<Long> permissionIds =
                    criteria.permissionIds().stream()
                            .map(permissionId -> permissionId.value())
                            .toList();
            builder.and(permissionIdIn(permissionIds));
        }

        return builder;
    }

    // ========== 정렬 조건 메서드 ==========

    /**
     * 정렬 조건 생성
     *
     * @param criteria 검색 조건 (정렬 정보 포함)
     * @return OrderSpecifier
     */
    public OrderSpecifier<?> buildOrderSpecifier(RolePermissionSearchCriteria criteria) {
        RolePermissionSortKey sortKey = criteria.sortKey();
        SortDirection direction = criteria.sortDirection();
        boolean isAscending = direction.isAscending();

        return switch (sortKey) {
            case ROLE_PERMISSION_ID ->
                    isAscending
                            ? rolePermissionJpaEntity.rolePermissionId.asc()
                            : rolePermissionJpaEntity.rolePermissionId.desc();
            case ROLE_ID ->
                    isAscending
                            ? rolePermissionJpaEntity.roleId.asc()
                            : rolePermissionJpaEntity.roleId.desc();
            case PERMISSION_ID ->
                    isAscending
                            ? rolePermissionJpaEntity.permissionId.asc()
                            : rolePermissionJpaEntity.permissionId.desc();
            case CREATED_AT ->
                    isAscending
                            ? rolePermissionJpaEntity.createdAt.asc()
                            : rolePermissionJpaEntity.createdAt.desc();
        };
    }
}
