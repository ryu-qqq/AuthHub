package com.ryuqq.authhub.adapter.out.persistence.user.repository;

import static com.ryuqq.authhub.adapter.out.persistence.user.entity.QUserJpaEntity.userJpaEntity;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.authhub.adapter.out.persistence.user.entity.UserJpaEntity;
import com.ryuqq.authhub.domain.common.vo.SearchType;
import com.ryuqq.authhub.domain.common.vo.SortDirection;
import com.ryuqq.authhub.domain.user.query.criteria.UserCriteria;
import com.ryuqq.authhub.domain.user.vo.UserSortKey;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

/**
 * UserQueryDslRepository - 사용자 QueryDSL Repository (Query 전용)
 *
 * <p>QueryDSL 기반 조회 작업을 담당합니다.
 *
 * <p><strong>책임:</strong>
 *
 * <ul>
 *   <li>findByUserId() - UUID로 단건 조회
 *   <li>findByTenantIdAndOrganizationIdAndIdentifier() - 식별자로 조회
 *   <li>existsByTenantIdAndOrganizationIdAndIdentifier() - 식별자 존재 여부 확인
 *   <li>findAllByCriteria() - 조건 검색
 *   <li>countByCriteria() - 조건 개수 조회
 * </ul>
 *
 * <p><strong>CQRS 패턴:</strong>
 *
 * <ul>
 *   <li>Command: UserJpaRepository (JPA)
 *   <li>Query: UserQueryDslRepository (QueryDSL)
 * </ul>
 *
 * <p><strong>규칙:</strong>
 *
 * <ul>
 *   <li>Entity 반환 (Domain 변환은 Adapter에서)
 *   <li>Join 금지 (N+1 해결은 Application Layer에서)
 *   <li>비즈니스 로직 금지
 *   <li>DELETED 상태 사용자는 제외하지 않음 (Application에서 판단)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Repository
public class UserQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    public UserQueryDslRepository(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    /**
     * UUID로 사용자 단건 조회
     *
     * @param userId 사용자 UUID
     * @return Optional<UserJpaEntity>
     */
    public Optional<UserJpaEntity> findByUserId(UUID userId) {
        UserJpaEntity result =
                queryFactory
                        .selectFrom(userJpaEntity)
                        .where(userJpaEntity.userId.eq(userId))
                        .fetchOne();
        return Optional.ofNullable(result);
    }

    /**
     * 테넌트 내 식별자로 사용자 단건 조회
     *
     * @param tenantId 테넌트 UUID
     * @param identifier 사용자 식별자
     * @return Optional<UserJpaEntity>
     */
    public Optional<UserJpaEntity> findByTenantIdAndIdentifier(UUID tenantId, String identifier) {
        UserJpaEntity result =
                queryFactory
                        .selectFrom(userJpaEntity)
                        .where(
                                userJpaEntity.tenantId.eq(tenantId),
                                userJpaEntity.identifier.eq(identifier))
                        .fetchOne();
        return Optional.ofNullable(result);
    }

    /**
     * 식별자로 사용자 단건 조회
     *
     * <p>테넌트와 무관하게 식별자로 사용자를 조회합니다. 로그인 시 사용됩니다.
     *
     * @param identifier 사용자 식별자 (이메일 또는 사용자명)
     * @return Optional<UserJpaEntity>
     */
    public Optional<UserJpaEntity> findByIdentifier(String identifier) {
        UserJpaEntity result =
                queryFactory
                        .selectFrom(userJpaEntity)
                        .where(userJpaEntity.identifier.eq(identifier))
                        .fetchOne();
        return Optional.ofNullable(result);
    }

    /**
     * 테넌트/조직 내 식별자로 사용자 단건 조회
     *
     * @param tenantId 테넌트 UUID
     * @param organizationId 조직 UUID
     * @param identifier 사용자 식별자
     * @return Optional<UserJpaEntity>
     */
    public Optional<UserJpaEntity> findByTenantIdAndOrganizationIdAndIdentifier(
            UUID tenantId, UUID organizationId, String identifier) {
        UserJpaEntity result =
                queryFactory
                        .selectFrom(userJpaEntity)
                        .where(
                                userJpaEntity.tenantId.eq(tenantId),
                                userJpaEntity.organizationId.eq(organizationId),
                                userJpaEntity.identifier.eq(identifier))
                        .fetchOne();
        return Optional.ofNullable(result);
    }

    /**
     * 테넌트/조직 내 식별자 존재 여부 확인
     *
     * @param tenantId 테넌트 UUID
     * @param organizationId 조직 UUID
     * @param identifier 사용자 식별자
     * @return 존재 여부
     */
    public boolean existsByTenantIdAndOrganizationIdAndIdentifier(
            UUID tenantId, UUID organizationId, String identifier) {
        Integer result =
                queryFactory
                        .selectOne()
                        .from(userJpaEntity)
                        .where(
                                userJpaEntity.tenantId.eq(tenantId),
                                userJpaEntity.organizationId.eq(organizationId),
                                userJpaEntity.identifier.eq(identifier))
                        .fetchFirst();
        return result != null;
    }

    /**
     * 테넌트 내 핸드폰 번호 존재 여부 확인
     *
     * @param tenantId 테넌트 UUID
     * @param phoneNumber 핸드폰 번호
     * @return 존재 여부
     */
    public boolean existsByTenantIdAndPhoneNumber(UUID tenantId, String phoneNumber) {
        Integer result =
                queryFactory
                        .selectOne()
                        .from(userJpaEntity)
                        .where(
                                userJpaEntity.tenantId.eq(tenantId),
                                userJpaEntity.phoneNumber.eq(phoneNumber))
                        .fetchFirst();
        return result != null;
    }

    /**
     * 조건에 맞는 사용자 목록 조회 (페이징)
     *
     * @param criteria 검색 조건 (UserCriteria)
     * @return UserJpaEntity 목록
     */
    public List<UserJpaEntity> findAllByCriteria(UserCriteria criteria) {
        BooleanBuilder condition = buildCondition(criteria);
        OrderSpecifier<?> orderSpecifier = buildOrderSpecifier(criteria);

        int offset = criteria.page().page() * criteria.page().size();
        int limit = criteria.page().size();

        return queryFactory
                .selectFrom(userJpaEntity)
                .where(condition)
                .orderBy(orderSpecifier)
                .offset(offset)
                .limit(limit)
                .fetch();
    }

    /**
     * 조건에 맞는 사용자 개수 조회
     *
     * @param criteria 검색 조건 (UserCriteria)
     * @return 조건에 맞는 사용자 총 개수
     */
    public long countByCriteria(UserCriteria criteria) {
        BooleanBuilder condition = buildCondition(criteria);

        Long count =
                queryFactory
                        .select(userJpaEntity.count())
                        .from(userJpaEntity)
                        .where(condition)
                        .fetchOne();
        return count != null ? count : 0L;
    }

    /**
     * 검색 조건 빌더 생성
     *
     * @param criteria 검색 조건
     * @return BooleanBuilder
     */
    private BooleanBuilder buildCondition(UserCriteria criteria) {
        BooleanBuilder builder = new BooleanBuilder();

        // 테넌트 필터
        if (criteria.hasTenantFilter()) {
            builder.and(userJpaEntity.tenantId.eq(criteria.tenantId()));
        }

        // 조직 필터
        if (criteria.hasOrganizationFilter()) {
            builder.and(userJpaEntity.organizationId.eq(criteria.organizationId()));
        }

        // 식별자 검색 (SearchType에 따라)
        if (criteria.hasIdentifierFilter()) {
            SearchType searchType =
                    criteria.identifierSearchType() != null
                            ? criteria.identifierSearchType()
                            : SearchType.CONTAINS_LIKE;
            builder.and(applyIdentifierSearch(criteria.identifier(), searchType));
        }

        // 상태 필터
        if (criteria.hasStatusFilter()) {
            builder.and(userJpaEntity.status.eq(criteria.status()));
        }

        // 날짜 범위 필터
        if (criteria.dateRange() != null) {
            if (criteria.dateRange().startDate() != null) {
                LocalDateTime fromDateTime = criteria.dateRange().startDate().atStartOfDay();
                builder.and(userJpaEntity.createdAt.goe(fromDateTime));
            }
            if (criteria.dateRange().endDate() != null) {
                LocalDateTime toDateTime = criteria.dateRange().endDate().atTime(23, 59, 59);
                builder.and(userJpaEntity.createdAt.loe(toDateTime));
            }
        }

        return builder;
    }

    /**
     * SearchType에 따른 식별자 검색 조건 생성
     *
     * @param identifier 검색 식별자
     * @param searchType 검색 타입
     * @return BooleanExpression
     */
    private BooleanExpression applyIdentifierSearch(String identifier, SearchType searchType) {
        return switch (searchType) {
            case PREFIX_LIKE -> userJpaEntity.identifier.startsWithIgnoreCase(identifier);
            case CONTAINS_LIKE -> userJpaEntity.identifier.containsIgnoreCase(identifier);
            case MATCH_AGAINST ->
                    userJpaEntity.identifier.containsIgnoreCase(
                            identifier); // Fallback, 실제 구현은 NativeQuery
        };
    }

    /**
     * 정렬 조건 빌더 생성
     *
     * @param criteria 검색 조건
     * @return OrderSpecifier
     */
    private OrderSpecifier<?> buildOrderSpecifier(UserCriteria criteria) {
        UserSortKey sortKey =
                criteria.sortKey() != null ? criteria.sortKey() : UserSortKey.CREATED_AT;
        boolean isAsc = criteria.sortDirection() == SortDirection.ASC;

        return switch (sortKey) {
            case IDENTIFIER ->
                    isAsc ? userJpaEntity.identifier.asc() : userJpaEntity.identifier.desc();
            case STATUS -> isAsc ? userJpaEntity.status.asc() : userJpaEntity.status.desc();
            case UPDATED_AT ->
                    isAsc ? userJpaEntity.updatedAt.asc() : userJpaEntity.updatedAt.desc();
            case CREATED_AT ->
                    isAsc ? userJpaEntity.createdAt.asc() : userJpaEntity.createdAt.desc();
        };
    }
}
