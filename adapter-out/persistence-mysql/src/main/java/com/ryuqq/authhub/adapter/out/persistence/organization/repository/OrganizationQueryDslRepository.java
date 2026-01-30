package com.ryuqq.authhub.adapter.out.persistence.organization.repository;

import static com.ryuqq.authhub.adapter.out.persistence.organization.entity.QOrganizationJpaEntity.organizationJpaEntity;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.authhub.adapter.out.persistence.organization.condition.OrganizationConditionBuilder;
import com.ryuqq.authhub.adapter.out.persistence.organization.entity.OrganizationJpaEntity;
import com.ryuqq.authhub.domain.organization.query.criteria.OrganizationSearchCriteria;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 * OrganizationQueryDslRepository - 조직 QueryDSL Repository (Query 전용)
 *
 * <p>QueryDSL 기반 조회 작업을 담당합니다.
 *
 * <p><strong>책임:</strong>
 *
 * <ul>
 *   <li>findByOrganizationId() - ID로 단건 조회
 *   <li>existsByOrganizationId() - ID 존재 여부 확인
 *   <li>existsByTenantIdAndName() - 테넌트 내 이름 중복 확인
 *   <li>findAllByCriteria() - SearchCriteria 기반 조건 검색
 *   <li>countByCriteria() - SearchCriteria 기반 개수 조회
 * </ul>
 *
 * <p><strong>CQRS 패턴:</strong>
 *
 * <ul>
 *   <li>Command: OrganizationJpaRepository (JPA)
 *   <li>Query: OrganizationQueryDslRepository (QueryDSL)
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
public class OrganizationQueryDslRepository {

    private final JPAQueryFactory queryFactory;
    private final OrganizationConditionBuilder conditionBuilder;

    public OrganizationQueryDslRepository(
            JPAQueryFactory queryFactory, OrganizationConditionBuilder conditionBuilder) {
        this.queryFactory = queryFactory;
        this.conditionBuilder = conditionBuilder;
    }

    /**
     * ID로 조직 단건 조회
     *
     * @param organizationId 조직 ID (String)
     * @return Optional<OrganizationJpaEntity>
     */
    public Optional<OrganizationJpaEntity> findByOrganizationId(String organizationId) {
        OrganizationJpaEntity result =
                queryFactory
                        .selectFrom(organizationJpaEntity)
                        .where(organizationJpaEntity.organizationId.eq(organizationId))
                        .fetchOne();
        return Optional.ofNullable(result);
    }

    /**
     * ID 존재 여부 확인
     *
     * @param organizationId 조직 ID (String)
     * @return 존재 여부
     */
    public boolean existsByOrganizationId(String organizationId) {
        Integer result =
                queryFactory
                        .selectOne()
                        .from(organizationJpaEntity)
                        .where(organizationJpaEntity.organizationId.eq(organizationId))
                        .fetchFirst();
        return result != null;
    }

    /**
     * 테넌트 내 이름 중복 확인
     *
     * @param tenantId 테넌트 ID (String)
     * @param name 조직 이름
     * @return 존재 여부
     */
    public boolean existsByTenantIdAndName(String tenantId, String name) {
        Integer result =
                queryFactory
                        .selectOne()
                        .from(organizationJpaEntity)
                        .where(
                                organizationJpaEntity.tenantId.eq(tenantId),
                                organizationJpaEntity.name.eq(name))
                        .fetchFirst();
        return result != null;
    }

    /**
     * SearchCriteria 기반 조직 목록 조회 (페이징)
     *
     * <p>OrganizationConditionBuilder를 사용하여 조건을 생성합니다.
     *
     * @param criteria 검색 조건 (OrganizationSearchCriteria)
     * @return OrganizationJpaEntity 목록
     */
    public List<OrganizationJpaEntity> findAllByCriteria(OrganizationSearchCriteria criteria) {
        BooleanBuilder condition = conditionBuilder.buildCondition(criteria);
        OrderSpecifier<?> orderSpecifier = conditionBuilder.buildOrderSpecifier(criteria);

        long offset = criteria.offset();
        int limit = criteria.size();

        return queryFactory
                .selectFrom(organizationJpaEntity)
                .where(condition)
                .orderBy(orderSpecifier)
                .offset(offset)
                .limit(limit)
                .fetch();
    }

    /**
     * SearchCriteria 기반 조직 개수 조회
     *
     * @param criteria 검색 조건 (OrganizationSearchCriteria)
     * @return 조건에 맞는 조직 총 개수
     */
    public long countByCriteria(OrganizationSearchCriteria criteria) {
        BooleanBuilder condition = conditionBuilder.buildCondition(criteria);

        Long count =
                queryFactory
                        .select(organizationJpaEntity.count())
                        .from(organizationJpaEntity)
                        .where(condition)
                        .fetchOne();
        return count != null ? count : 0L;
    }
}
