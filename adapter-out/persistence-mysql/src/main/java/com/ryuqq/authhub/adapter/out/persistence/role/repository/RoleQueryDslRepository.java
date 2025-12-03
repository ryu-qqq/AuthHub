package com.ryuqq.authhub.adapter.out.persistence.role.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.authhub.adapter.out.persistence.role.entity.QRoleJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.role.entity.RoleJpaEntity;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 * RoleQueryDslRepository - Role QueryDSL Repository (Query)
 *
 * <p>Query 작업 전용 Repository입니다.
 *
 * <p><strong>표준 메서드:</strong>
 *
 * <ul>
 *   <li>findById(id) - ID로 단일 조회
 *   <li>existsById(id) - 존재 여부 확인
 *   <li>findByName(name) - 이름으로 조회
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
public class RoleQueryDslRepository {

    private final JPAQueryFactory queryFactory;
    private static final QRoleJpaEntity role = QRoleJpaEntity.roleJpaEntity;

    public RoleQueryDslRepository(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    /**
     * ID로 Role 조회
     *
     * @param id Role ID
     * @return RoleJpaEntity (Optional)
     */
    public Optional<RoleJpaEntity> findById(Long id) {
        RoleJpaEntity result = queryFactory.selectFrom(role).where(role.id.eq(id)).fetchOne();
        return Optional.ofNullable(result);
    }

    /**
     * ID로 Role 조회
     *
     * @param ids Role ID
     * @return RoleJpaEntity (Optional)
     */
    public List<RoleJpaEntity> findByIds(List<Long> ids) {
        return queryFactory.selectFrom(role).where(role.id.in(ids)).fetch();
    }

    /**
     * ID 존재 여부 확인
     *
     * @param id Role ID
     * @return 존재 여부
     */
    public boolean existsById(Long id) {
        Integer result = queryFactory.selectOne().from(role).where(role.id.eq(id)).fetchFirst();
        return result != null;
    }

    /**
     * 이름으로 Role 조회
     *
     * @param name Role 이름 (예: ROLE_ADMIN)
     * @return RoleJpaEntity (Optional)
     */
    public Optional<RoleJpaEntity> findByName(String name) {
        RoleJpaEntity result = queryFactory.selectFrom(role).where(role.name.eq(name)).fetchOne();
        return Optional.ofNullable(result);
    }
}
