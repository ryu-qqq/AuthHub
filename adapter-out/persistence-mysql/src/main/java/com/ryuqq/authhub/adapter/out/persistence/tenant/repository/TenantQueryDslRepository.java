package com.ryuqq.authhub.adapter.out.persistence.tenant.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.authhub.adapter.out.persistence.tenant.entity.QTenantJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.tenant.entity.TenantJpaEntity;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 * TenantQueryDslRepository - Tenant QueryDSL Repository (Query)
 *
 * <p>Query 작업 전용 Repository입니다. 4가지 표준 메서드만 제공합니다.
 *
 * <p><strong>표준 메서드:</strong>
 *
 * <ul>
 *   <li>findById(id) - ID로 단일 조회
 *   <li>existsById(id) - 존재 여부 확인
 *   <li>findByCriteria(criteria) - 조건 검색 (필요 시 추가)
 *   <li>countByCriteria(criteria) - 개수 조회 (필요 시 추가)
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
public class TenantQueryDslRepository {

    private final JPAQueryFactory queryFactory;
    private static final QTenantJpaEntity tenant = QTenantJpaEntity.tenantJpaEntity;

    public TenantQueryDslRepository(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    /**
     * ID로 Tenant 조회
     *
     * @param id Tenant ID
     * @return TenantJpaEntity (Optional)
     */
    public Optional<TenantJpaEntity> findById(Long id) {
        TenantJpaEntity result = queryFactory.selectFrom(tenant).where(tenant.id.eq(id)).fetchOne();
        return Optional.ofNullable(result);
    }

    /**
     * ID 존재 여부 확인
     *
     * @param id Tenant ID
     * @return 존재 여부
     */
    public boolean existsById(Long id) {
        Integer result = queryFactory.selectOne().from(tenant).where(tenant.id.eq(id)).fetchFirst();
        return result != null;
    }
}
