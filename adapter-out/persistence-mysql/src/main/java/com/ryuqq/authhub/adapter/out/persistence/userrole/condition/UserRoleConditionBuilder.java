package com.ryuqq.authhub.adapter.out.persistence.userrole.condition;

import static com.ryuqq.authhub.adapter.out.persistence.userrole.entity.QUserRoleJpaEntity.userRoleJpaEntity;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.ryuqq.authhub.domain.common.vo.SortDirection;
import com.ryuqq.authhub.domain.userrole.query.criteria.UserRoleSearchCriteria;
import com.ryuqq.authhub.domain.userrole.vo.UserRoleSortKey;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * UserRoleConditionBuilder - 사용자-역할 관계 QueryDSL 조건 빌더
 *
 * <p>UserRoleSearchCriteria를 QueryDSL BooleanBuilder로 변환합니다.
 *
 * <p><strong>책임:</strong>
 *
 * <ul>
 *   <li>buildCondition() - 검색 조건 생성
 *   <li>buildOrderSpecifier() - 정렬 조건 생성
 * </ul>
 *
 * <p><strong>Hard Delete:</strong>
 *
 * <ul>
 *   <li>관계 테이블이므로 notDeleted() 조건 없음
 *   <li>물리적 삭제만 존재
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class UserRoleConditionBuilder {

    /**
     * SearchCriteria → BooleanBuilder 변환
     *
     * @param criteria 검색 조건
     * @return QueryDSL BooleanBuilder
     */
    public BooleanBuilder buildCondition(UserRoleSearchCriteria criteria) {
        BooleanBuilder builder = new BooleanBuilder();

        if (criteria.hasUserIdFilter()) {
            builder.and(userRoleJpaEntity.userId.eq(criteria.userId().value()));
        }

        if (criteria.hasUserIdsFilter()) {
            List<String> userIdValues =
                    criteria.userIds().stream().map(userId -> userId.value()).toList();
            builder.and(userRoleJpaEntity.userId.in(userIdValues));
        }

        if (criteria.hasRoleIdFilter()) {
            builder.and(userRoleJpaEntity.roleId.eq(criteria.roleId().value()));
        }

        if (criteria.hasRoleIdsFilter()) {
            List<Long> roleIdValues =
                    criteria.roleIds().stream().map(roleId -> roleId.value()).toList();
            builder.and(userRoleJpaEntity.roleId.in(roleIdValues));
        }

        return builder;
    }

    /**
     * 정렬 조건 생성
     *
     * @param criteria 검색 조건
     * @return OrderSpecifier
     */
    public OrderSpecifier<?> buildOrderSpecifier(UserRoleSearchCriteria criteria) {
        UserRoleSortKey sortKey = criteria.sortKey();
        SortDirection direction = criteria.sortDirection();

        if (sortKey == UserRoleSortKey.CREATED_AT) {
            return direction == SortDirection.ASC
                    ? userRoleJpaEntity.createdAt.asc()
                    : userRoleJpaEntity.createdAt.desc();
        }

        return userRoleJpaEntity.createdAt.desc();
    }
}
