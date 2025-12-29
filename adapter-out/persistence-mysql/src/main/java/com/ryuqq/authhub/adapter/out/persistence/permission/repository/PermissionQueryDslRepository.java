package com.ryuqq.authhub.adapter.out.persistence.permission.repository;

import static com.ryuqq.authhub.adapter.out.persistence.permission.entity.QPermissionJpaEntity.permissionJpaEntity;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.authhub.adapter.out.persistence.permission.entity.PermissionJpaEntity;
import com.ryuqq.authhub.application.permission.dto.query.SearchPermissionsQuery;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

/**
 * PermissionQueryDslRepository - 권한 QueryDSL Repository (Query 전용)
 *
 * <p>QueryDSL 기반 조회 작업을 담당합니다.
 *
 * <p><strong>책임:</strong>
 *
 * <ul>
 *   <li>findByPermissionId() - ID로 단건 조회
 *   <li>findByKey() - 권한 키로 단건 조회
 *   <li>existsByKey() - 권한 키 존재 여부 확인
 *   <li>searchByQuery() - Query 기반 조건 검색
 *   <li>countByQuery() - Query 기반 개수 조회
 * </ul>
 *
 * <p><strong>CQRS 패턴:</strong>
 *
 * <ul>
 *   <li>Command: PermissionJpaRepository (JPA)
 *   <li>Query: PermissionQueryDslRepository (QueryDSL)
 * </ul>
 *
 * <p><strong>규칙:</strong>
 *
 * <ul>
 *   <li>Entity 반환 (Domain 변환은 Adapter에서)
 *   <li>Join 금지 (N+1 해결은 Application Layer에서)
 *   <li>비즈니스 로직 금지
 *   <li>삭제되지 않은 권한만 조회 (deleted = false)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Repository
public class PermissionQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    public PermissionQueryDslRepository(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    /**
     * UUID로 권한 단건 조회
     *
     * @param permissionId 권한 UUID
     * @return Optional<PermissionJpaEntity>
     */
    public Optional<PermissionJpaEntity> findByPermissionId(UUID permissionId) {
        PermissionJpaEntity result =
                queryFactory
                        .selectFrom(permissionJpaEntity)
                        .where(
                                permissionJpaEntity.permissionId.eq(permissionId),
                                permissionJpaEntity.deleted.eq(false))
                        .fetchOne();
        return Optional.ofNullable(result);
    }

    /**
     * 권한 키로 권한 단건 조회
     *
     * @param key 권한 키 ("{resource}:{action}" 형식)
     * @return Optional<PermissionJpaEntity>
     */
    public Optional<PermissionJpaEntity> findByKey(String key) {
        PermissionJpaEntity result =
                queryFactory
                        .selectFrom(permissionJpaEntity)
                        .where(
                                permissionJpaEntity.permissionKey.eq(key),
                                permissionJpaEntity.deleted.eq(false))
                        .fetchOne();
        return Optional.ofNullable(result);
    }

    /**
     * 권한 키 존재 여부 확인
     *
     * @param key 권한 키 ("{resource}:{action}" 형식)
     * @return 존재 여부
     */
    public boolean existsByKey(String key) {
        Integer result =
                queryFactory
                        .selectOne()
                        .from(permissionJpaEntity)
                        .where(
                                permissionJpaEntity.permissionKey.eq(key),
                                permissionJpaEntity.deleted.eq(false))
                        .fetchFirst();
        return result != null;
    }

    /**
     * Query 기반 권한 검색 (페이징)
     *
     * @param query 검색 조건 (SearchPermissionsQuery)
     * @return PermissionJpaEntity 목록
     */
    public List<PermissionJpaEntity> searchByQuery(SearchPermissionsQuery query) {
        BooleanBuilder builder = PermissionSearchConditionBuilder.buildSearchCondition(query);
        OrderSpecifier<?> orderSpecifier =
                PermissionSearchConditionBuilder.buildOrderSpecifier(query);
        int offset = query.page() * query.size();

        return queryFactory
                .selectFrom(permissionJpaEntity)
                .where(builder)
                .orderBy(orderSpecifier)
                .offset(offset)
                .limit(query.size())
                .fetch();
    }

    /**
     * Query 기반 권한 개수 조회
     *
     * @param query 검색 조건 (SearchPermissionsQuery)
     * @return 조건에 맞는 권한 총 개수
     */
    public long countByQuery(SearchPermissionsQuery query) {
        BooleanBuilder builder = PermissionSearchConditionBuilder.buildSearchCondition(query);

        Long count =
                queryFactory
                        .select(permissionJpaEntity.count())
                        .from(permissionJpaEntity)
                        .where(builder)
                        .fetchOne();
        return count != null ? count : 0L;
    }

    /**
     * 여러 ID로 권한 목록 조회
     *
     * @param permissionIds 권한 UUID 컬렉션
     * @return PermissionJpaEntity 목록
     */
    public List<PermissionJpaEntity> findAllByIds(Collection<UUID> permissionIds) {
        if (permissionIds == null || permissionIds.isEmpty()) {
            return List.of();
        }
        return queryFactory
                .selectFrom(permissionJpaEntity)
                .where(
                        permissionJpaEntity.permissionId.in(permissionIds),
                        permissionJpaEntity.deleted.eq(false))
                .fetch();
    }

    /**
     * 여러 권한 키로 권한 목록 조회 (Bulk 조회)
     *
     * <p>CI/CD 권한 검증에서 사용됩니다.
     *
     * @param keys 권한 키 컬렉션 ("{resource}:{action}" 형식)
     * @return PermissionJpaEntity 목록 (존재하는 권한만)
     */
    public List<PermissionJpaEntity> findAllByKeys(Collection<String> keys) {
        if (keys == null || keys.isEmpty()) {
            return List.of();
        }
        return queryFactory
                .selectFrom(permissionJpaEntity)
                .where(
                        permissionJpaEntity.permissionKey.in(keys),
                        permissionJpaEntity.deleted.eq(false))
                .fetch();
    }
}
