package com.ryuqq.authhub.adapter.out.persistence.service.repository;

import static com.ryuqq.authhub.adapter.out.persistence.service.entity.QServiceJpaEntity.serviceJpaEntity;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.authhub.adapter.out.persistence.service.condition.ServiceConditionBuilder;
import com.ryuqq.authhub.adapter.out.persistence.service.entity.ServiceJpaEntity;
import com.ryuqq.authhub.domain.service.query.criteria.ServiceSearchCriteria;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 * ServiceQueryDslRepository - 서비스 QueryDSL Repository (Query 전용)
 *
 * <p>QueryDSL 기반 조회 작업을 담당합니다.
 *
 * <p><strong>책임:</strong>
 *
 * <ul>
 *   <li>findByServiceId() - Long ID로 단건 조회
 *   <li>existsByServiceId() - Long ID 존재 여부 확인
 *   <li>existsByServiceCode() - 서비스 코드 존재 여부 확인
 *   <li>findAllByCriteria() - 조건 검색
 *   <li>countByCriteria() - 조건 검색 개수
 *   <li>findAllActive() - 활성 상태 전체 조회
 * </ul>
 *
 * <p><strong>CQRS 패턴:</strong>
 *
 * <ul>
 *   <li>Command: ServiceJpaRepository (JPA)
 *   <li>Query: ServiceQueryDslRepository (QueryDSL)
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
public class ServiceQueryDslRepository {

    private final JPAQueryFactory queryFactory;
    private final ServiceConditionBuilder conditionBuilder;

    public ServiceQueryDslRepository(
            JPAQueryFactory queryFactory, ServiceConditionBuilder conditionBuilder) {
        this.queryFactory = queryFactory;
        this.conditionBuilder = conditionBuilder;
    }

    /**
     * 서비스 ID(Long)로 단건 조회
     *
     * @param serviceId 서비스 ID (Long)
     * @return Optional<ServiceJpaEntity>
     */
    public Optional<ServiceJpaEntity> findByServiceId(Long serviceId) {
        ServiceJpaEntity result =
                queryFactory
                        .selectFrom(serviceJpaEntity)
                        .where(conditionBuilder.serviceIdEquals(serviceId))
                        .fetchOne();
        return Optional.ofNullable(result);
    }

    /**
     * 서비스 ID(Long) 존재 여부 확인
     *
     * @param serviceId 서비스 ID (Long)
     * @return 존재 여부
     */
    public boolean existsByServiceId(Long serviceId) {
        Integer result =
                queryFactory
                        .selectOne()
                        .from(serviceJpaEntity)
                        .where(conditionBuilder.serviceIdEquals(serviceId))
                        .fetchFirst();
        return result != null;
    }

    /**
     * 서비스 코드(String)로 단건 조회
     *
     * @param serviceCode 서비스 코드 (String)
     * @return Optional<ServiceJpaEntity>
     */
    public Optional<ServiceJpaEntity> findByServiceCode(String serviceCode) {
        ServiceJpaEntity result =
                queryFactory
                        .selectFrom(serviceJpaEntity)
                        .where(conditionBuilder.serviceCodeEquals(serviceCode))
                        .fetchOne();
        return Optional.ofNullable(result);
    }

    /**
     * 서비스 코드(String) 존재 여부 확인
     *
     * @param serviceCode 서비스 코드 (String)
     * @return 존재 여부
     */
    public boolean existsByServiceCode(String serviceCode) {
        Integer result =
                queryFactory
                        .selectOne()
                        .from(serviceJpaEntity)
                        .where(conditionBuilder.serviceCodeEquals(serviceCode))
                        .fetchFirst();
        return result != null;
    }

    /**
     * 조건에 맞는 서비스 목록 조회 (페이징)
     *
     * @param criteria 검색 조건 (ServiceSearchCriteria)
     * @return ServiceJpaEntity 목록
     */
    public List<ServiceJpaEntity> findAllByCriteria(ServiceSearchCriteria criteria) {
        BooleanBuilder condition = conditionBuilder.buildCondition(criteria);
        OrderSpecifier<?> orderSpecifier = conditionBuilder.buildOrderSpecifier(criteria);

        long offset = criteria.offset();
        int limit = criteria.size();

        return queryFactory
                .selectFrom(serviceJpaEntity)
                .where(condition)
                .orderBy(orderSpecifier)
                .offset(offset)
                .limit(limit)
                .fetch();
    }

    /**
     * 조건에 맞는 서비스 개수 조회
     *
     * @param criteria 검색 조건 (ServiceSearchCriteria)
     * @return 조건에 맞는 서비스 총 개수
     */
    public long countByCriteria(ServiceSearchCriteria criteria) {
        BooleanBuilder condition = conditionBuilder.buildCondition(criteria);

        Long count =
                queryFactory
                        .select(serviceJpaEntity.count())
                        .from(serviceJpaEntity)
                        .where(condition)
                        .fetchOne();
        return count != null ? count : 0L;
    }

    /**
     * 모든 활성 서비스 목록 조회
     *
     * @return 활성 상태의 ServiceJpaEntity 목록
     */
    public List<ServiceJpaEntity> findAllActive() {
        return queryFactory
                .selectFrom(serviceJpaEntity)
                .where(conditionBuilder.statusActive())
                .orderBy(serviceJpaEntity.name.asc())
                .fetch();
    }
}
