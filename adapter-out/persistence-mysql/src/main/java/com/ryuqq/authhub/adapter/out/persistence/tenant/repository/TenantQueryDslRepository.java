package com.ryuqq.authhub.adapter.out.persistence.tenant.repository;

import static com.ryuqq.authhub.adapter.out.persistence.tenant.entity.QTenantJpaEntity.tenantJpaEntity;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.authhub.adapter.out.persistence.tenant.condition.TenantConditionBuilder;
import com.ryuqq.authhub.adapter.out.persistence.tenant.entity.TenantJpaEntity;
import com.ryuqq.authhub.domain.tenant.query.criteria.TenantSearchCriteria;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 * TenantQueryDslRepository - 테넌트 QueryDSL Repository (Query 전용)
 *
 * <p>QueryDSL 기반 조회 작업을 담당합니다.
 *
 * <p><strong>책임:</strong>
 *
 * <ul>
 *   <li>findByTenantId() - ID로 단건 조회
 *   <li>existsByName() - 이름 존재 여부 확인
 *   <li>findAllByCriteria() - 조건 검색
 *   <li>countByCriteria() - 조건 검색 개수
 * </ul>
 *
 * <p><strong>CQRS 패턴:</strong>
 *
 * <ul>
 *   <li>Command: TenantJpaRepository (JPA)
 *   <li>Query: TenantQueryDslRepository (QueryDSL)
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
public class TenantQueryDslRepository {

    private final JPAQueryFactory queryFactory;
    private final TenantConditionBuilder conditionBuilder;

    public TenantQueryDslRepository(
            JPAQueryFactory queryFactory, TenantConditionBuilder conditionBuilder) {
        this.queryFactory = queryFactory;
        this.conditionBuilder = conditionBuilder;
    }

    /**
     * 테넌트 ID로 단건 조회
     *
     * @param tenantId 테넌트 ID (String)
     * @return Optional<TenantJpaEntity>
     */
    public Optional<TenantJpaEntity> findByTenantId(String tenantId) {
        TenantJpaEntity result =
                queryFactory
                        .selectFrom(tenantJpaEntity)
                        .where(conditionBuilder.tenantIdEquals(tenantId))
                        .fetchOne();
        return Optional.ofNullable(result);
    }

    /**
     * 이름 존재 여부 확인
     *
     * @param name 테넌트 이름
     * @return 존재 여부
     */
    public boolean existsByName(String name) {
        Integer result =
                queryFactory
                        .selectOne()
                        .from(tenantJpaEntity)
                        .where(conditionBuilder.nameEquals(name))
                        .fetchFirst();
        return result != null;
    }

    /**
     * 테넌트 ID 존재 여부 확인
     *
     * @param tenantId 테넌트 ID (String)
     * @return 존재 여부
     */
    public boolean existsByTenantId(String tenantId) {
        Integer result =
                queryFactory
                        .selectOne()
                        .from(tenantJpaEntity)
                        .where(conditionBuilder.tenantIdEquals(tenantId))
                        .fetchFirst();
        return result != null;
    }

    /**
     * 이름 존재 여부 확인 (특정 ID 제외)
     *
     * <p>이름 중복 검증 시 자기 자신은 제외합니다.
     *
     * @param name 테넌트 이름
     * @param excludeId 제외할 테넌트 ID (String)
     * @return 존재 여부
     */
    public boolean existsByNameAndIdNot(String name, String excludeId) {
        Integer result =
                queryFactory
                        .selectOne()
                        .from(tenantJpaEntity)
                        .where(
                                conditionBuilder.nameEquals(name),
                                conditionBuilder.tenantIdNotEquals(excludeId))
                        .fetchFirst();
        return result != null;
    }

    /**
     * 조건에 맞는 테넌트 목록 조회 (페이징)
     *
     * @param criteria 검색 조건 (TenantSearchCriteria)
     * @return TenantJpaEntity 목록
     */
    public List<TenantJpaEntity> findAllByCriteria(TenantSearchCriteria criteria) {
        BooleanBuilder condition = conditionBuilder.buildCondition(criteria);
        OrderSpecifier<?> orderSpecifier = conditionBuilder.buildOrderSpecifier(criteria);

        long offset = criteria.offset();
        int limit = criteria.size();

        return queryFactory
                .selectFrom(tenantJpaEntity)
                .where(condition)
                .orderBy(orderSpecifier)
                .offset(offset)
                .limit(limit)
                .fetch();
    }

    /**
     * 조건에 맞는 테넌트 개수 조회
     *
     * @param criteria 검색 조건 (TenantSearchCriteria)
     * @return 조건에 맞는 테넌트 총 개수
     */
    public long countByCriteria(TenantSearchCriteria criteria) {
        BooleanBuilder condition = conditionBuilder.buildCondition(criteria);

        Long count =
                queryFactory
                        .select(tenantJpaEntity.count())
                        .from(tenantJpaEntity)
                        .where(condition)
                        .fetchOne();
        return count != null ? count : 0L;
    }
}
