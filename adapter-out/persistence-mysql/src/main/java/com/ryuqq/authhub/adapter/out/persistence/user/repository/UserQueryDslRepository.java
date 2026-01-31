package com.ryuqq.authhub.adapter.out.persistence.user.repository;

import static com.ryuqq.authhub.adapter.out.persistence.user.entity.QUserJpaEntity.userJpaEntity;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.authhub.adapter.out.persistence.user.condition.UserConditionBuilder;
import com.ryuqq.authhub.adapter.out.persistence.user.entity.UserJpaEntity;
import com.ryuqq.authhub.domain.user.query.criteria.UserSearchCriteria;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 * UserQueryDslRepository - 사용자 QueryDSL Repository (Query 전용)
 *
 * <p>QueryDSL 기반 조회 작업을 담당합니다.
 *
 * <p><strong>책임:</strong>
 *
 * <ul>
 *   <li>findByUserId() - ID로 단건 조회
 *   <li>existsByUserId() - ID 존재 여부 확인
 *   <li>existsByOrganizationIdAndIdentifier() - 조직 내 식별자 중복 확인
 *   <li>existsByOrganizationIdAndPhoneNumber() - 조직 내 전화번호 중복 확인
 *   <li>findByIdentifier() - 식별자로 단건 조회 (로그인용)
 *   <li>findAllByCriteria() - SearchCriteria 기반 조건 검색
 *   <li>countByCriteria() - SearchCriteria 기반 개수 조회
 *   <li>findAllByUserIds() - ID 목록으로 다건 조회
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
 *   <li>Criteria 기반 조회 (개별 파라미터 금지)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Repository
public class UserQueryDslRepository {

    private final JPAQueryFactory queryFactory;
    private final UserConditionBuilder conditionBuilder;

    public UserQueryDslRepository(
            JPAQueryFactory queryFactory, UserConditionBuilder conditionBuilder) {
        this.queryFactory = queryFactory;
        this.conditionBuilder = conditionBuilder;
    }

    /**
     * ID로 사용자 단건 조회
     *
     * @param userId 사용자 ID (String)
     * @return Optional<UserJpaEntity>
     */
    public Optional<UserJpaEntity> findByUserId(String userId) {
        UserJpaEntity result =
                queryFactory
                        .selectFrom(userJpaEntity)
                        .where(userJpaEntity.userId.eq(userId), conditionBuilder.notDeleted())
                        .fetchOne();
        return Optional.ofNullable(result);
    }

    /**
     * ID 존재 여부 확인
     *
     * @param userId 사용자 ID (String)
     * @return 존재 여부
     */
    public boolean existsByUserId(String userId) {
        Integer result =
                queryFactory
                        .selectOne()
                        .from(userJpaEntity)
                        .where(userJpaEntity.userId.eq(userId), conditionBuilder.notDeleted())
                        .fetchFirst();
        return result != null;
    }

    /**
     * 조직 내 식별자 중복 확인
     *
     * @param organizationId 조직 ID (String)
     * @param identifier 로그인 식별자
     * @return 존재 여부
     */
    public boolean existsByOrganizationIdAndIdentifier(String organizationId, String identifier) {
        Integer result =
                queryFactory
                        .selectOne()
                        .from(userJpaEntity)
                        .where(
                                userJpaEntity.organizationId.eq(organizationId),
                                userJpaEntity.identifier.eq(identifier),
                                conditionBuilder.notDeleted())
                        .fetchFirst();
        return result != null;
    }

    /**
     * 조직 내 전화번호 중복 확인
     *
     * @param organizationId 조직 ID (String)
     * @param phoneNumber 전화번호
     * @return 존재 여부
     */
    public boolean existsByOrganizationIdAndPhoneNumber(String organizationId, String phoneNumber) {
        Integer result =
                queryFactory
                        .selectOne()
                        .from(userJpaEntity)
                        .where(
                                userJpaEntity.organizationId.eq(organizationId),
                                userJpaEntity.phoneNumber.eq(phoneNumber),
                                conditionBuilder.notDeleted())
                        .fetchFirst();
        return result != null;
    }

    /**
     * 식별자로 사용자 단건 조회 (로그인용)
     *
     * @param identifier 로그인 식별자
     * @return Optional<UserJpaEntity>
     */
    public Optional<UserJpaEntity> findByIdentifier(String identifier) {
        UserJpaEntity result =
                queryFactory
                        .selectFrom(userJpaEntity)
                        .where(
                                userJpaEntity.identifier.eq(identifier),
                                conditionBuilder.notDeleted())
                        .fetchOne();
        return Optional.ofNullable(result);
    }

    /**
     * SearchCriteria 기반 사용자 목록 조회 (페이징)
     *
     * <p>UserConditionBuilder를 사용하여 조건을 생성합니다.
     *
     * @param criteria 검색 조건 (UserSearchCriteria)
     * @return UserJpaEntity 목록
     */
    public List<UserJpaEntity> findAllByCriteria(UserSearchCriteria criteria) {
        BooleanBuilder condition = conditionBuilder.buildCondition(criteria);
        OrderSpecifier<?> orderSpecifier = conditionBuilder.buildOrderSpecifier(criteria);

        long offset = criteria.offset();
        int limit = criteria.size();

        return queryFactory
                .selectFrom(userJpaEntity)
                .where(condition)
                .orderBy(orderSpecifier)
                .offset(offset)
                .limit(limit)
                .fetch();
    }

    /**
     * SearchCriteria 기반 사용자 개수 조회
     *
     * @param criteria 검색 조건 (UserSearchCriteria)
     * @return 조건에 맞는 사용자 총 개수
     */
    public long countByCriteria(UserSearchCriteria criteria) {
        BooleanBuilder condition = conditionBuilder.buildCondition(criteria);

        Long count =
                queryFactory
                        .select(userJpaEntity.count())
                        .from(userJpaEntity)
                        .where(condition)
                        .fetchOne();
        return count != null ? count : 0L;
    }
}
