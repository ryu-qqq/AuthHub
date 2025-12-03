package com.ryuqq.authhub.adapter.out.persistence.role.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.authhub.adapter.out.persistence.role.entity.PermissionJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.role.entity.QPermissionJpaEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 * PermissionQueryDslRepository - Permission QueryDSL Repository (Query)
 *
 * <p>Query 작업 전용 Repository입니다.
 *
 * <p><strong>표준 메서드:</strong>
 *
 * <ul>
 *   <li>findById(id) - ID로 단일 조회
 *   <li>findByCode(code) - 코드로 조회
 *   <li>findByIds(ids) - ID 목록으로 다중 조회
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>JOIN 절대 금지 (Long FK 전략)
 *   <li>DTO Projection 사용 권장
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@Repository
public class PermissionQueryDslRepository {

    private final JPAQueryFactory queryFactory;
    private static final QPermissionJpaEntity permission = QPermissionJpaEntity.permissionJpaEntity;

    public PermissionQueryDslRepository(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    /**
     * ID로 Permission 조회
     *
     * @param id Permission ID
     * @return PermissionJpaEntity (Optional)
     */
    public Optional<PermissionJpaEntity> findById(Long id) {
        PermissionJpaEntity result =
                queryFactory.selectFrom(permission).where(permission.id.eq(id)).fetchOne();
        return Optional.ofNullable(result);
    }

    /**
     * 코드로 Permission 조회
     *
     * @param code Permission 코드 (예: user:read)
     * @return PermissionJpaEntity (Optional)
     */
    public Optional<PermissionJpaEntity> findByCode(String code) {
        PermissionJpaEntity result =
                queryFactory.selectFrom(permission).where(permission.code.eq(code)).fetchOne();
        return Optional.ofNullable(result);
    }

    /**
     * ID 목록으로 Permission 다중 조회
     *
     * @param ids Permission ID 목록
     * @return PermissionJpaEntity 목록
     */
    public List<PermissionJpaEntity> findByIds(List<Long> ids) {
        return queryFactory.selectFrom(permission).where(permission.id.in(ids)).fetch();
    }
}
