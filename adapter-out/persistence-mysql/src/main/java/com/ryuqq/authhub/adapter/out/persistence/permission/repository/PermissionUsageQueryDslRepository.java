package com.ryuqq.authhub.adapter.out.persistence.permission.repository;

import static com.ryuqq.authhub.adapter.out.persistence.permission.entity.QPermissionUsageJpaEntity.permissionUsageJpaEntity;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.authhub.adapter.out.persistence.permission.entity.PermissionUsageJpaEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 * PermissionUsageQueryDslRepository - 권한 사용 이력 QueryDSL Repository (Query 전용)
 *
 * <p>QueryDSL 기반 조회 작업을 담당합니다.
 *
 * <p><strong>책임:</strong>
 *
 * <ul>
 *   <li>findByKeyAndService() - 권한 키 + 서비스명으로 단건 조회
 *   <li>findAllByPermissionKey() - 권한 키로 전체 사용 이력 조회
 *   <li>findAllByServiceName() - 서비스명으로 전체 사용 이력 조회
 * </ul>
 *
 * <p><strong>CQRS 패턴:</strong>
 *
 * <ul>
 *   <li>Command: PermissionUsageJpaRepository (JPA)
 *   <li>Query: PermissionUsageQueryDslRepository (QueryDSL)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Repository
public class PermissionUsageQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    public PermissionUsageQueryDslRepository(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    /**
     * 권한 키 + 서비스명으로 사용 이력 단건 조회
     *
     * @param permissionKey 권한 키
     * @param serviceName 서비스명
     * @return Optional<PermissionUsageJpaEntity>
     */
    public Optional<PermissionUsageJpaEntity> findByKeyAndService(
            String permissionKey, String serviceName) {
        PermissionUsageJpaEntity result =
                queryFactory
                        .selectFrom(permissionUsageJpaEntity)
                        .where(
                                permissionUsageJpaEntity.permissionKey.eq(permissionKey),
                                permissionUsageJpaEntity.serviceName.eq(serviceName))
                        .fetchOne();
        return Optional.ofNullable(result);
    }

    /**
     * 권한 키로 전체 사용 이력 조회
     *
     * @param permissionKey 권한 키
     * @return PermissionUsageJpaEntity 목록
     */
    public List<PermissionUsageJpaEntity> findAllByPermissionKey(String permissionKey) {
        return queryFactory
                .selectFrom(permissionUsageJpaEntity)
                .where(permissionUsageJpaEntity.permissionKey.eq(permissionKey))
                .orderBy(permissionUsageJpaEntity.serviceName.asc())
                .fetch();
    }

    /**
     * 서비스명으로 전체 사용 이력 조회
     *
     * @param serviceName 서비스명
     * @return PermissionUsageJpaEntity 목록
     */
    public List<PermissionUsageJpaEntity> findAllByServiceName(String serviceName) {
        return queryFactory
                .selectFrom(permissionUsageJpaEntity)
                .where(permissionUsageJpaEntity.serviceName.eq(serviceName))
                .orderBy(permissionUsageJpaEntity.permissionKey.asc())
                .fetch();
    }
}
