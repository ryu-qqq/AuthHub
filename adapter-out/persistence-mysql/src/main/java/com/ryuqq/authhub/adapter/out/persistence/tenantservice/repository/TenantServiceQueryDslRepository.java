package com.ryuqq.authhub.adapter.out.persistence.tenantservice.repository;

import static com.ryuqq.authhub.adapter.out.persistence.tenantservice.entity.QTenantServiceJpaEntity.tenantServiceJpaEntity;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.authhub.adapter.out.persistence.tenantservice.condition.TenantServiceConditionBuilder;
import com.ryuqq.authhub.adapter.out.persistence.tenantservice.entity.TenantServiceJpaEntity;
import com.ryuqq.authhub.domain.tenantservice.query.criteria.TenantServiceSearchCriteria;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 * TenantServiceQueryDslRepository - 테넌트-서비스 QueryDSL Repository (Query 전용)
 *
 * <p>QueryDSL 기반 조회 작업을 담당합니다.
 *
 * <p><strong>CQRS 패턴:</strong>
 *
 * <ul>
 *   <li>Command: TenantServiceJpaRepository (JPA)
 *   <li>Query: TenantServiceQueryDslRepository (QueryDSL)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Repository
public class TenantServiceQueryDslRepository {

    private final JPAQueryFactory queryFactory;
    private final TenantServiceConditionBuilder conditionBuilder;

    public TenantServiceQueryDslRepository(
            JPAQueryFactory queryFactory, TenantServiceConditionBuilder conditionBuilder) {
        this.queryFactory = queryFactory;
        this.conditionBuilder = conditionBuilder;
    }

    /**
     * ID(Long)로 단건 조회
     *
     * @param id 테넌트-서비스 ID (Long)
     * @return Optional<TenantServiceJpaEntity>
     */
    public Optional<TenantServiceJpaEntity> findById(Long id) {
        TenantServiceJpaEntity result =
                queryFactory
                        .selectFrom(tenantServiceJpaEntity)
                        .where(tenantServiceJpaEntity.id.eq(id))
                        .fetchOne();
        return Optional.ofNullable(result);
    }

    /**
     * ID(Long) 존재 여부 확인
     *
     * @param id 테넌트-서비스 ID (Long)
     * @return 존재 여부
     */
    public boolean existsById(Long id) {
        Integer result =
                queryFactory
                        .selectOne()
                        .from(tenantServiceJpaEntity)
                        .where(tenantServiceJpaEntity.id.eq(id))
                        .fetchFirst();
        return result != null;
    }

    /**
     * 테넌트 ID + 서비스 ID 존재 여부 확인
     *
     * @param tenantId 테넌트 ID (String)
     * @param serviceId 서비스 ID (Long)
     * @return 존재 여부
     */
    public boolean existsByTenantIdAndServiceId(String tenantId, Long serviceId) {
        Integer result =
                queryFactory
                        .selectOne()
                        .from(tenantServiceJpaEntity)
                        .where(
                                conditionBuilder.tenantIdEquals(tenantId),
                                conditionBuilder.serviceIdEquals(serviceId))
                        .fetchFirst();
        return result != null;
    }

    /**
     * 테넌트 ID + 서비스 ID로 단건 조회
     *
     * @param tenantId 테넌트 ID (String)
     * @param serviceId 서비스 ID (Long)
     * @return Optional<TenantServiceJpaEntity>
     */
    public Optional<TenantServiceJpaEntity> findByTenantIdAndServiceId(
            String tenantId, Long serviceId) {
        TenantServiceJpaEntity result =
                queryFactory
                        .selectFrom(tenantServiceJpaEntity)
                        .where(
                                conditionBuilder.tenantIdEquals(tenantId),
                                conditionBuilder.serviceIdEquals(serviceId))
                        .fetchOne();
        return Optional.ofNullable(result);
    }

    /**
     * 조건에 맞는 목록 조회 (페이징)
     *
     * @param criteria 검색 조건
     * @return TenantServiceJpaEntity 목록
     */
    public List<TenantServiceJpaEntity> findAllByCriteria(TenantServiceSearchCriteria criteria) {
        BooleanBuilder condition = conditionBuilder.buildCondition(criteria);
        OrderSpecifier<?> orderSpecifier = conditionBuilder.buildOrderSpecifier(criteria);

        long offset = criteria.offset();
        int limit = criteria.size();

        return queryFactory
                .selectFrom(tenantServiceJpaEntity)
                .where(condition)
                .orderBy(orderSpecifier)
                .offset(offset)
                .limit(limit)
                .fetch();
    }

    /**
     * 조건에 맞는 개수 조회
     *
     * @param criteria 검색 조건
     * @return 조건에 맞는 총 개수
     */
    public long countByCriteria(TenantServiceSearchCriteria criteria) {
        BooleanBuilder condition = conditionBuilder.buildCondition(criteria);

        Long count =
                queryFactory
                        .select(tenantServiceJpaEntity.count())
                        .from(tenantServiceJpaEntity)
                        .where(condition)
                        .fetchOne();
        return count != null ? count : 0L;
    }
}
