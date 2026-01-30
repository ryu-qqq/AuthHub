package com.ryuqq.authhub.adapter.out.persistence.user.condition;

import static com.ryuqq.authhub.adapter.out.persistence.user.entity.QUserJpaEntity.userJpaEntity;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.ryuqq.authhub.domain.common.vo.SortDirection;
import com.ryuqq.authhub.domain.organization.id.OrganizationId;
import com.ryuqq.authhub.domain.user.query.criteria.UserSearchCriteria;
import com.ryuqq.authhub.domain.user.vo.UserSearchField;
import com.ryuqq.authhub.domain.user.vo.UserSortKey;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * UserConditionBuilder - 사용자 QueryDSL 조건 빌더
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
public class UserConditionBuilder {

    /**
     * 검색 조건 빌더 생성
     *
     * <p>UserSearchCriteria 기반으로 BooleanBuilder를 생성합니다.
     *
     * @param criteria 검색 조건
     * @return BooleanBuilder
     */
    public BooleanBuilder buildCondition(UserSearchCriteria criteria) {
        BooleanBuilder builder = new BooleanBuilder();

        // 조직 ID 목록 필터
        builder.and(organizationIdsIn(criteria.organizationIds()));

        // 검색어 + 검색 필드 조건
        builder.and(searchByField(criteria.searchField(), criteria.searchWord()));

        // 다중 상태 필터
        if (criteria.hasStatusFilter()) {
            builder.and(userJpaEntity.status.in(criteria.statuses()));
        }

        // 날짜 범위 필터 (Law of Demeter 준수 - 편의 메서드 사용)
        builder.and(createdAtGoe(criteria.startInstant()));
        builder.and(createdAtLoe(criteria.endInstant()));

        // 삭제된 항목 제외 (기본값)
        if (!criteria.includeDeleted()) {
            builder.and(userJpaEntity.deletedAt.isNull());
        }

        return builder;
    }

    /**
     * 정렬 조건 빌더 생성
     *
     * @param criteria 검색 조건
     * @return OrderSpecifier
     */
    public OrderSpecifier<?> buildOrderSpecifier(UserSearchCriteria criteria) {
        UserSortKey sortKey =
                criteria.sortKey() != null ? criteria.sortKey() : UserSortKey.CREATED_AT;
        boolean isAsc = criteria.sortDirection() == SortDirection.ASC;

        return switch (sortKey) {
            case UPDATED_AT ->
                    isAsc ? userJpaEntity.updatedAt.asc() : userJpaEntity.updatedAt.desc();
            case CREATED_AT ->
                    isAsc ? userJpaEntity.createdAt.asc() : userJpaEntity.createdAt.desc();
        };
    }

    /**
     * 조직 ID 목록 포함 조건
     *
     * @param organizationIds 조직 ID 목록
     * @return BooleanExpression (null 허용)
     */
    public BooleanExpression organizationIdsIn(List<OrganizationId> organizationIds) {
        if (organizationIds == null || organizationIds.isEmpty()) {
            return null;
        }
        List<String> ids = organizationIds.stream().map(OrganizationId::value).toList();
        return userJpaEntity.organizationId.in(ids);
    }

    /**
     * SearchField 기반 검색 조건 생성
     *
     * <p>검색 필드(UserSearchField)에 따라 해당 컬럼에 검색어를 적용합니다.
     *
     * @param searchField 검색 필드 (UserSearchField enum)
     * @param searchWord 검색어
     * @return BooleanExpression (null 허용 - 조건 없음)
     */
    public BooleanExpression searchByField(UserSearchField searchField, String searchWord) {
        if (searchField == null || searchWord == null || searchWord.isBlank()) {
            return null;
        }

        return switch (searchField) {
            case IDENTIFIER -> userJpaEntity.identifier.containsIgnoreCase(searchWord);
            case PHONE_NUMBER -> userJpaEntity.phoneNumber.containsIgnoreCase(searchWord);
        };
    }

    /**
     * 사용자 ID 일치 조건
     *
     * @param userId 사용자 ID (String)
     * @return BooleanExpression (null 허용)
     */
    public BooleanExpression userIdEquals(String userId) {
        return userId != null ? userJpaEntity.userId.eq(userId) : null;
    }

    /**
     * 조직 ID 일치 조건
     *
     * @param organizationId 조직 ID (String)
     * @return BooleanExpression (null 허용)
     */
    public BooleanExpression organizationIdEquals(String organizationId) {
        return organizationId != null ? userJpaEntity.organizationId.eq(organizationId) : null;
    }

    /**
     * 식별자 일치 조건
     *
     * @param identifier 로그인 식별자
     * @return BooleanExpression (null 허용)
     */
    public BooleanExpression identifierEquals(String identifier) {
        return identifier != null ? userJpaEntity.identifier.eq(identifier) : null;
    }

    /**
     * 전화번호 일치 조건
     *
     * @param phoneNumber 전화번호
     * @return BooleanExpression (null 허용)
     */
    public BooleanExpression phoneNumberEquals(String phoneNumber) {
        return phoneNumber != null ? userJpaEntity.phoneNumber.eq(phoneNumber) : null;
    }

    /**
     * 생성일시 이상 조건
     *
     * @param startInstant 시작 일시 (Instant, UTC)
     * @return BooleanExpression (null 허용)
     */
    public BooleanExpression createdAtGoe(Instant startInstant) {
        return startInstant != null ? userJpaEntity.createdAt.goe(startInstant) : null;
    }

    /**
     * 생성일시 이하 조건
     *
     * @param endInstant 종료 일시 (Instant, UTC)
     * @return BooleanExpression (null 허용)
     */
    public BooleanExpression createdAtLoe(Instant endInstant) {
        return endInstant != null ? userJpaEntity.createdAt.loe(endInstant) : null;
    }

    /**
     * 삭제되지 않은 항목만 조건
     *
     * @return BooleanExpression
     */
    public BooleanExpression notDeleted() {
        return userJpaEntity.deletedAt.isNull();
    }
}
