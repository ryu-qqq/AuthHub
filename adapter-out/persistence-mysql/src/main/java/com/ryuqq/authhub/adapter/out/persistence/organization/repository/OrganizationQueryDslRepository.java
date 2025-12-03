package com.ryuqq.authhub.adapter.out.persistence.organization.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.authhub.adapter.out.persistence.organization.entity.OrganizationJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.organization.entity.QOrganizationJpaEntity;
import com.ryuqq.authhub.domain.organization.vo.OrganizationStatus;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 * OrganizationQueryDslRepository - Organization QueryDSL Repository (Query)
 *
 * <p>Query 작업 전용 Repository입니다. 표준 메서드만 제공합니다.
 *
 * <p><strong>표준 메서드:</strong>
 *
 * <ul>
 *   <li>findById(id) - ID로 단일 조회
 *   <li>existsById(id) - 존재 여부 확인
 *   <li>existsByTenantId(tenantId) - Tenant별 존재 여부 확인
 *   <li>existsActiveByTenantId(tenantId) - Tenant별 활성 Organization 존재 여부 확인
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>JOIN 절대 금지 (Long FK 전략)
 *   <li>N+1 문제 발생 시 별도 쿼리로 분리
 *   <li>DTO Projection 사용 권장
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@Repository
public class OrganizationQueryDslRepository {

    private final JPAQueryFactory queryFactory;
    private static final QOrganizationJpaEntity organization =
            QOrganizationJpaEntity.organizationJpaEntity;

    public OrganizationQueryDslRepository(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    /**
     * ID로 Organization 조회
     *
     * @param id Organization ID
     * @return OrganizationJpaEntity (Optional)
     */
    public Optional<OrganizationJpaEntity> findById(Long id) {
        OrganizationJpaEntity result =
                queryFactory.selectFrom(organization).where(organization.id.eq(id)).fetchOne();
        return Optional.ofNullable(result);
    }

    /**
     * ID 존재 여부 확인
     *
     * @param id Organization ID
     * @return 존재 여부
     */
    public boolean existsById(Long id) {
        Integer result =
                queryFactory
                        .selectOne()
                        .from(organization)
                        .where(organization.id.eq(id))
                        .fetchFirst();
        return result != null;
    }

    /**
     * Tenant ID로 Organization 존재 여부 확인
     *
     * @param tenantId Tenant ID
     * @return 존재 여부
     */
    public boolean existsByTenantId(Long tenantId) {
        Integer result =
                queryFactory
                        .selectOne()
                        .from(organization)
                        .where(organization.tenantId.eq(tenantId))
                        .fetchFirst();
        return result != null;
    }

    /**
     * Tenant ID로 활성 Organization 존재 여부 확인
     *
     * @param tenantId Tenant ID
     * @return 활성 Organization 존재 여부
     */
    public boolean existsActiveByTenantId(Long tenantId) {
        Integer result =
                queryFactory
                        .selectOne()
                        .from(organization)
                        .where(
                                organization.tenantId.eq(tenantId),
                                organization.status.eq(OrganizationStatus.ACTIVE))
                        .fetchFirst();
        return result != null;
    }
}
