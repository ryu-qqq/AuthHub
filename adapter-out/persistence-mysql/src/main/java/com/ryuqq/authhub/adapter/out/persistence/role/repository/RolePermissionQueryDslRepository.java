package com.ryuqq.authhub.adapter.out.persistence.role.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.authhub.adapter.out.persistence.role.entity.QRolePermissionJpaEntity;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 * RolePermissionQueryDslRepository - Role-Permission 매핑 QueryDSL Repository (Query)
 *
 * <p>Query 작업 전용 Repository입니다.
 *
 * <p><strong>표준 메서드:</strong>
 *
 * <ul>
 *   <li>findPermissionIdsByRoleId(roleId) - 역할 ID로 권한 ID 목록 조회
 *   <li>findPermissionIdsByRoleIds(roleIds) - 역할 ID 목록으로 권한 ID 목록 조회
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>JOIN 절대 금지 (Long FK 전략)
 *   <li>Permission 정보 필요 시 별도 쿼리로 분리
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@Repository
public class RolePermissionQueryDslRepository {

    private final JPAQueryFactory queryFactory;
    private static final QRolePermissionJpaEntity rolePermission =
            QRolePermissionJpaEntity.rolePermissionJpaEntity;

    public RolePermissionQueryDslRepository(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    /**
     * 역할 ID로 Permission ID 목록 조회
     *
     * @param roleId 역할 ID
     * @return Permission ID 목록
     */
    public List<Long> findPermissionIdsByRoleId(Long roleId) {
        return queryFactory
                .select(rolePermission.permissionId)
                .from(rolePermission)
                .where(rolePermission.roleId.eq(roleId))
                .fetch();
    }

    /**
     * 역할 ID 목록으로 Permission ID 목록 조회
     *
     * <p>여러 Role의 Permission을 한 번에 조회할 때 사용
     *
     * @param roleIds 역할 ID 목록
     * @return Permission ID 목록 (중복 가능)
     */
    public List<Long> findPermissionIdsByRoleIds(List<Long> roleIds) {
        return queryFactory
                .select(rolePermission.permissionId)
                .from(rolePermission)
                .where(rolePermission.roleId.in(roleIds))
                .fetch();
    }
}
