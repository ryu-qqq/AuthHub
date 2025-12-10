package com.ryuqq.authhub.adapter.out.persistence.role.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.authhub.adapter.out.persistence.role.entity.QRolePermissionJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.role.entity.RolePermissionJpaEntity;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

/**
 * RolePermissionQueryDslRepository - 역할 권한 QueryDSL Repository (Query 전용)
 *
 * <p>역할-권한 연결 테이블의 QueryDSL 기반 조회 작업을 담당합니다.
 *
 * <p><strong>책임:</strong>
 *
 * <ul>
 *   <li>findByRoleIdAndPermissionId() - 역할 ID와 권한 ID로 단건 조회
 *   <li>findAllByRoleId() - 역할 ID로 부여된 모든 권한 조회
 *   <li>existsByRoleIdAndPermissionId() - 역할-권한 연결 존재 여부 확인
 * </ul>
 *
 * <p><strong>CQRS 패턴:</strong>
 *
 * <ul>
 *   <li>Command: RolePermissionJpaRepository (JPA)
 *   <li>Query: RolePermissionQueryDslRepository (QueryDSL)
 * </ul>
 *
 * <p><strong>규칙:</strong>
 *
 * <ul>
 *   <li>Entity 반환 (Domain 변환은 Adapter에서)
 *   <li>Join 금지
 *   <li>비즈니스 로직 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Repository
public class RolePermissionQueryDslRepository {

    private static final QRolePermissionJpaEntity ROLE_PERMISSION =
            QRolePermissionJpaEntity.rolePermissionJpaEntity;

    private final JPAQueryFactory queryFactory;

    public RolePermissionQueryDslRepository(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    /**
     * 역할 ID와 권한 ID로 단건 조회
     *
     * @param roleId 역할 UUID
     * @param permissionId 권한 UUID
     * @return Optional<RolePermissionJpaEntity>
     */
    public Optional<RolePermissionJpaEntity> findByRoleIdAndPermissionId(
            UUID roleId, UUID permissionId) {
        RolePermissionJpaEntity result =
                queryFactory
                        .selectFrom(ROLE_PERMISSION)
                        .where(
                                ROLE_PERMISSION.roleId.eq(roleId),
                                ROLE_PERMISSION.permissionId.eq(permissionId))
                        .fetchOne();
        return Optional.ofNullable(result);
    }

    /**
     * 역할 ID로 부여된 모든 권한 조회
     *
     * @param roleId 역할 UUID
     * @return RolePermissionJpaEntity 목록
     */
    public List<RolePermissionJpaEntity> findAllByRoleId(UUID roleId) {
        return queryFactory
                .selectFrom(ROLE_PERMISSION)
                .where(ROLE_PERMISSION.roleId.eq(roleId))
                .orderBy(ROLE_PERMISSION.grantedAt.desc())
                .fetch();
    }

    /**
     * 역할-권한 연결 존재 여부 확인
     *
     * @param roleId 역할 UUID
     * @param permissionId 권한 UUID
     * @return 존재 여부
     */
    public boolean existsByRoleIdAndPermissionId(UUID roleId, UUID permissionId) {
        Integer result =
                queryFactory
                        .selectOne()
                        .from(ROLE_PERMISSION)
                        .where(
                                ROLE_PERMISSION.roleId.eq(roleId),
                                ROLE_PERMISSION.permissionId.eq(permissionId))
                        .fetchFirst();
        return result != null;
    }

    /**
     * 여러 역할 ID로 부여된 모든 권한 조회
     *
     * @param roleIds 역할 UUID 컬렉션
     * @return RolePermissionJpaEntity 목록
     */
    public List<RolePermissionJpaEntity> findAllByRoleIds(Collection<UUID> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return List.of();
        }
        return queryFactory
                .selectFrom(ROLE_PERMISSION)
                .where(ROLE_PERMISSION.roleId.in(roleIds))
                .fetch();
    }
}
