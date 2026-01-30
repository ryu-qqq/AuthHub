package com.ryuqq.authhub.adapter.out.persistence.role.repository;

import static com.ryuqq.authhub.adapter.out.persistence.role.entity.QRoleJpaEntity.roleJpaEntity;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.authhub.adapter.out.persistence.role.condition.RoleConditionBuilder;
import com.ryuqq.authhub.adapter.out.persistence.role.entity.RoleJpaEntity;
import com.ryuqq.authhub.domain.role.query.criteria.RoleSearchCriteria;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 * RoleQueryDslRepository - 역할 QueryDSL Repository (Query 전용)
 *
 * <p>QueryDSL 기반 조회 작업을 담당합니다.
 *
 * <p><strong>책임:</strong>
 *
 * <ul>
 *   <li>findByRoleId() - ID로 단건 조회
 *   <li>existsByRoleId() - ID 존재 여부 확인
 *   <li>existsByTenantIdAndName() - 테넌트 내 역할 이름 존재 여부 확인
 *   <li>findByTenantIdAndName() - 테넌트 내 역할 이름으로 단건 조회
 *   <li>findAllByCriteria() - 조건 검색
 *   <li>countByCriteria() - 조건 검색 개수
 *   <li>findAllByIds() - ID 목록으로 다건 조회
 * </ul>
 *
 * <p><strong>CQRS 패턴:</strong>
 *
 * <ul>
 *   <li>Command: RoleJpaRepository (JPA)
 *   <li>Query: RoleQueryDslRepository (QueryDSL)
 * </ul>
 *
 * <p><strong>규칙:</strong>
 *
 * <ul>
 *   <li>Entity 반환 (Domain 변환은 Adapter에서)
 *   <li>Join 금지 (N+1 해결은 Application Layer에서)
 *   <li>비즈니스 로직 금지
 *   <li>ConditionBuilder를 사용하여 조건 생성
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Repository
public class RoleQueryDslRepository {

    private final JPAQueryFactory queryFactory;
    private final RoleConditionBuilder conditionBuilder;

    public RoleQueryDslRepository(
            JPAQueryFactory queryFactory, RoleConditionBuilder conditionBuilder) {
        this.queryFactory = queryFactory;
        this.conditionBuilder = conditionBuilder;
    }

    /**
     * 역할 ID로 단건 조회
     *
     * @param roleId 역할 ID (Long)
     * @return Optional<RoleJpaEntity>
     */
    public Optional<RoleJpaEntity> findByRoleId(Long roleId) {
        RoleJpaEntity result =
                queryFactory
                        .selectFrom(roleJpaEntity)
                        .where(conditionBuilder.roleIdEquals(roleId), conditionBuilder.notDeleted())
                        .fetchOne();
        return Optional.ofNullable(result);
    }

    /**
     * 역할 ID 존재 여부 확인
     *
     * @param roleId 역할 ID (Long)
     * @return 존재 여부
     */
    public boolean existsByRoleId(Long roleId) {
        Integer result =
                queryFactory
                        .selectOne()
                        .from(roleJpaEntity)
                        .where(conditionBuilder.roleIdEquals(roleId), conditionBuilder.notDeleted())
                        .fetchFirst();
        return result != null;
    }

    /**
     * 테넌트 내 역할 이름 존재 여부 확인
     *
     * <p>tenantId가 null이면 Global 역할 내에서 중복 확인합니다.
     *
     * @param tenantId 테넌트 ID (null이면 Global)
     * @param name 역할 이름
     * @return 존재 여부
     */
    public boolean existsByTenantIdAndName(String tenantId, String name) {
        Integer result =
                queryFactory
                        .selectOne()
                        .from(roleJpaEntity)
                        .where(
                                conditionBuilder.tenantIdEquals(tenantId),
                                conditionBuilder.nameEquals(name),
                                conditionBuilder.notDeleted())
                        .fetchFirst();
        return result != null;
    }

    /**
     * 테넌트 내 역할 이름으로 단건 조회
     *
     * <p>tenantId가 null이면 Global 역할 내에서 조회합니다.
     *
     * @param tenantId 테넌트 ID (null이면 Global)
     * @param name 역할 이름
     * @return Optional<RoleJpaEntity>
     */
    public Optional<RoleJpaEntity> findByTenantIdAndName(String tenantId, String name) {
        RoleJpaEntity result =
                queryFactory
                        .selectFrom(roleJpaEntity)
                        .where(
                                conditionBuilder.tenantIdEquals(tenantId),
                                conditionBuilder.nameEquals(name),
                                conditionBuilder.notDeleted())
                        .fetchOne();
        return Optional.ofNullable(result);
    }

    /**
     * 조건에 맞는 역할 목록 조회 (페이징)
     *
     * @param criteria 검색 조건 (RoleSearchCriteria)
     * @return RoleJpaEntity 목록
     */
    public List<RoleJpaEntity> findAllByCriteria(RoleSearchCriteria criteria) {
        BooleanBuilder condition = conditionBuilder.buildCondition(criteria);
        OrderSpecifier<?> orderSpecifier = conditionBuilder.buildOrderSpecifier(criteria);

        long offset = criteria.offset();
        int limit = criteria.size();

        return queryFactory
                .selectFrom(roleJpaEntity)
                .where(condition)
                .orderBy(orderSpecifier)
                .offset(offset)
                .limit(limit)
                .fetch();
    }

    /**
     * 조건에 맞는 역할 개수 조회
     *
     * @param criteria 검색 조건 (RoleSearchCriteria)
     * @return 조건에 맞는 역할 총 개수
     */
    public long countByCriteria(RoleSearchCriteria criteria) {
        BooleanBuilder condition = conditionBuilder.buildCondition(criteria);

        Long count =
                queryFactory
                        .select(roleJpaEntity.count())
                        .from(roleJpaEntity)
                        .where(condition)
                        .fetchOne();
        return count != null ? count : 0L;
    }

    /**
     * ID 목록으로 역할 다건 조회
     *
     * @param roleIds 역할 ID 목록
     * @return RoleJpaEntity 목록
     */
    public List<RoleJpaEntity> findAllByIds(List<Long> roleIds) {
        return queryFactory
                .selectFrom(roleJpaEntity)
                .where(conditionBuilder.roleIdIn(roleIds), conditionBuilder.notDeleted())
                .fetch();
    }
}
