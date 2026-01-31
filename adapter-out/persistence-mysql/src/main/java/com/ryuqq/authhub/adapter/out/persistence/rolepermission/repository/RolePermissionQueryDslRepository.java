package com.ryuqq.authhub.adapter.out.persistence.rolepermission.repository;

import static com.ryuqq.authhub.adapter.out.persistence.rolepermission.entity.QRolePermissionJpaEntity.rolePermissionJpaEntity;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.authhub.adapter.out.persistence.rolepermission.condition.RolePermissionConditionBuilder;
import com.ryuqq.authhub.adapter.out.persistence.rolepermission.entity.RolePermissionJpaEntity;
import com.ryuqq.authhub.domain.rolepermission.query.criteria.RolePermissionSearchCriteria;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 * RolePermissionQueryDslRepository - 역할-권한 관계 QueryDSL Repository (Query 전용)
 *
 * <p>QueryDSL 기반 조회 작업을 담당합니다.
 *
 * <p><strong>책임:</strong>
 *
 * <ul>
 *   <li>exists() - 관계 존재 여부 확인
 *   <li>findByRoleIdAndPermissionId() - roleId + permissionId로 단건 조회
 *   <li>findAllByRoleId() - 역할의 권한 목록 조회
 *   <li>findAllByPermissionId() - 권한이 부여된 역할 목록 조회
 *   <li>existsByPermissionId() - 권한 사용 여부 확인
 *   <li>findAllByCriteria() - 조건 검색
 *   <li>countByCriteria() - 조건 검색 개수
 * </ul>
 *
 * <p><strong>CQRS 패턴:</strong>
 *
 * <ul>
 *   <li>Command: RolePermissionJpaRepository (JPA)
 *   <li>Query: RolePermissionQueryDslRepository (QueryDSL)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Repository
public class RolePermissionQueryDslRepository {

    private final JPAQueryFactory queryFactory;
    private final RolePermissionConditionBuilder conditionBuilder;

    public RolePermissionQueryDslRepository(
            JPAQueryFactory queryFactory, RolePermissionConditionBuilder conditionBuilder) {
        this.queryFactory = queryFactory;
        this.conditionBuilder = conditionBuilder;
    }

    /**
     * 역할-권한 관계 존재 여부 확인
     *
     * @param roleId 역할 ID
     * @param permissionId 권한 ID
     * @return 존재 여부
     */
    public boolean exists(Long roleId, Long permissionId) {
        Integer result =
                queryFactory
                        .selectOne()
                        .from(rolePermissionJpaEntity)
                        .where(
                                conditionBuilder.roleIdEquals(roleId),
                                conditionBuilder.permissionIdEquals(permissionId))
                        .fetchFirst();
        return result != null;
    }

    /**
     * roleId + permissionId로 단건 조회
     *
     * @param roleId 역할 ID
     * @param permissionId 권한 ID
     * @return Optional<RolePermissionJpaEntity>
     */
    public Optional<RolePermissionJpaEntity> findByRoleIdAndPermissionId(
            Long roleId, Long permissionId) {
        RolePermissionJpaEntity result =
                queryFactory
                        .selectFrom(rolePermissionJpaEntity)
                        .where(
                                conditionBuilder.roleIdEquals(roleId),
                                conditionBuilder.permissionIdEquals(permissionId))
                        .fetchOne();
        return Optional.ofNullable(result);
    }

    /**
     * 역할의 권한 목록 조회
     *
     * @param roleId 역할 ID
     * @return RolePermissionJpaEntity 목록
     */
    public List<RolePermissionJpaEntity> findAllByRoleId(Long roleId) {
        return queryFactory
                .selectFrom(rolePermissionJpaEntity)
                .where(conditionBuilder.roleIdEquals(roleId))
                .orderBy(rolePermissionJpaEntity.createdAt.desc())
                .fetch();
    }

    /**
     * 권한이 부여된 역할 목록 조회
     *
     * @param permissionId 권한 ID
     * @return RolePermissionJpaEntity 목록
     */
    public List<RolePermissionJpaEntity> findAllByPermissionId(Long permissionId) {
        return queryFactory
                .selectFrom(rolePermissionJpaEntity)
                .where(conditionBuilder.permissionIdEquals(permissionId))
                .orderBy(rolePermissionJpaEntity.createdAt.desc())
                .fetch();
    }

    /**
     * 권한 사용 여부 확인 (Permission 삭제 검증용)
     *
     * @param permissionId 권한 ID
     * @return 사용 중 여부
     */
    public boolean existsByPermissionId(Long permissionId) {
        Integer result =
                queryFactory
                        .selectOne()
                        .from(rolePermissionJpaEntity)
                        .where(conditionBuilder.permissionIdEquals(permissionId))
                        .fetchFirst();
        return result != null;
    }

    /**
     * 조건에 맞는 역할-권한 관계 목록 조회 (페이징)
     *
     * @param criteria 검색 조건
     * @return RolePermissionJpaEntity 목록
     */
    public List<RolePermissionJpaEntity> findAllByCriteria(RolePermissionSearchCriteria criteria) {
        BooleanBuilder condition = conditionBuilder.buildCondition(criteria);
        OrderSpecifier<?> orderSpecifier = conditionBuilder.buildOrderSpecifier(criteria);

        long offset = criteria.offset();
        int limit = criteria.size();

        return queryFactory
                .selectFrom(rolePermissionJpaEntity)
                .where(condition)
                .orderBy(orderSpecifier)
                .offset(offset)
                .limit(limit)
                .fetch();
    }

    /**
     * 조건에 맞는 역할-권한 관계 개수 조회
     *
     * @param criteria 검색 조건
     * @return 총 개수
     */
    public long countByCriteria(RolePermissionSearchCriteria criteria) {
        BooleanBuilder condition = conditionBuilder.buildCondition(criteria);

        Long count =
                queryFactory
                        .select(rolePermissionJpaEntity.count())
                        .from(rolePermissionJpaEntity)
                        .where(condition)
                        .fetchOne();
        return count != null ? count : 0L;
    }

    /**
     * 역할에 이미 부여된 권한 ID 목록 조회 (요청된 permissionIds 중에서)
     *
     * <p>SELECT permission_id FROM role_permissions WHERE role_id = ? AND permission_id IN (?)
     *
     * @param roleId 역할 ID
     * @param permissionIds 확인할 권한 ID 목록
     * @return 이미 부여된 권한 ID 목록 (Long)
     */
    public List<Long> findGrantedPermissionIds(Long roleId, List<Long> permissionIds) {
        if (permissionIds == null || permissionIds.isEmpty()) {
            return List.of();
        }

        return queryFactory
                .select(rolePermissionJpaEntity.permissionId)
                .from(rolePermissionJpaEntity)
                .where(
                        conditionBuilder.roleIdEquals(roleId),
                        conditionBuilder.permissionIdIn(permissionIds))
                .fetch();
    }

    /**
     * 여러 역할의 권한 목록 조회 (IN절)
     *
     * @param roleIds 역할 ID 목록
     * @return RolePermissionJpaEntity 목록
     */
    public List<RolePermissionJpaEntity> findAllByRoleIds(List<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return List.of();
        }

        return queryFactory
                .selectFrom(rolePermissionJpaEntity)
                .where(conditionBuilder.roleIdIn(roleIds))
                .fetch();
    }
}
