package com.ryuqq.authhub.adapter.out.persistence.permission.repository;

import static com.ryuqq.authhub.adapter.out.persistence.permission.entity.QPermissionJpaEntity.permissionJpaEntity;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.authhub.adapter.out.persistence.permission.condition.PermissionConditionBuilder;
import com.ryuqq.authhub.adapter.out.persistence.permission.entity.PermissionJpaEntity;
import com.ryuqq.authhub.domain.permission.query.criteria.PermissionSearchCriteria;
import java.util.List;
import java.util.Optional;
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
 *   <li>existsByPermissionId() - ID 존재 여부 확인
 *   <li>existsByPermissionKey() - 권한 키 존재 여부 확인
 *   <li>findByPermissionKey() - 권한 키로 단건 조회
 *   <li>findAllByCriteria() - 조건 검색
 *   <li>countByCriteria() - 조건 검색 개수
 *   <li>findAllByIds() - ID 목록으로 다건 조회
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
 *   <li>ConditionBuilder를 사용하여 조건 생성
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Repository
public class PermissionQueryDslRepository {

    private final JPAQueryFactory queryFactory;
    private final PermissionConditionBuilder conditionBuilder;

    public PermissionQueryDslRepository(
            JPAQueryFactory queryFactory, PermissionConditionBuilder conditionBuilder) {
        this.queryFactory = queryFactory;
        this.conditionBuilder = conditionBuilder;
    }

    /**
     * 권한 ID로 단건 조회
     *
     * @param permissionId 권한 ID (Long)
     * @return Optional<PermissionJpaEntity>
     */
    public Optional<PermissionJpaEntity> findByPermissionId(Long permissionId) {
        PermissionJpaEntity result =
                queryFactory
                        .selectFrom(permissionJpaEntity)
                        .where(
                                conditionBuilder.permissionIdEquals(permissionId),
                                conditionBuilder.notDeleted())
                        .fetchOne();
        return Optional.ofNullable(result);
    }

    /**
     * 권한 ID 존재 여부 확인
     *
     * @param permissionId 권한 ID (Long)
     * @return 존재 여부
     */
    public boolean existsByPermissionId(Long permissionId) {
        Integer result =
                queryFactory
                        .selectOne()
                        .from(permissionJpaEntity)
                        .where(
                                conditionBuilder.permissionIdEquals(permissionId),
                                conditionBuilder.notDeleted())
                        .fetchFirst();
        return result != null;
    }

    /**
     * 권한 키 존재 여부 확인 (Global 전역)
     *
     * <p>tenantId와 관계없이 전역적으로 permissionKey 존재 여부를 확인합니다.
     *
     * @param permissionKey 권한 키
     * @return 존재 여부
     */
    public boolean existsByPermissionKey(String permissionKey) {
        Integer result =
                queryFactory
                        .selectOne()
                        .from(permissionJpaEntity)
                        .where(
                                conditionBuilder.permissionKeyEquals(permissionKey),
                                conditionBuilder.notDeleted())
                        .fetchFirst();
        return result != null;
    }

    /**
     * 권한 키로 단건 조회
     *
     * @param permissionKey 권한 키
     * @return Optional<PermissionJpaEntity>
     */
    public Optional<PermissionJpaEntity> findByPermissionKey(String permissionKey) {
        PermissionJpaEntity result =
                queryFactory
                        .selectFrom(permissionJpaEntity)
                        .where(
                                conditionBuilder.permissionKeyEquals(permissionKey),
                                conditionBuilder.notDeleted())
                        .fetchOne();
        return Optional.ofNullable(result);
    }

    /**
     * 조건에 맞는 권한 목록 조회 (페이징)
     *
     * @param criteria 검색 조건 (PermissionSearchCriteria)
     * @return PermissionJpaEntity 목록
     */
    public List<PermissionJpaEntity> findAllByCriteria(PermissionSearchCriteria criteria) {
        BooleanBuilder condition = conditionBuilder.buildCondition(criteria);
        OrderSpecifier<?> orderSpecifier = conditionBuilder.buildOrderSpecifier(criteria);

        long offset = criteria.offset();
        int limit = criteria.size();

        return queryFactory
                .selectFrom(permissionJpaEntity)
                .where(condition)
                .orderBy(orderSpecifier)
                .offset(offset)
                .limit(limit)
                .fetch();
    }

    /**
     * 조건에 맞는 권한 개수 조회
     *
     * @param criteria 검색 조건 (PermissionSearchCriteria)
     * @return 조건에 맞는 권한 총 개수
     */
    public long countByCriteria(PermissionSearchCriteria criteria) {
        BooleanBuilder condition = conditionBuilder.buildCondition(criteria);

        Long count =
                queryFactory
                        .select(permissionJpaEntity.count())
                        .from(permissionJpaEntity)
                        .where(condition)
                        .fetchOne();
        return count != null ? count : 0L;
    }

    /**
     * ID 목록으로 권한 다건 조회
     *
     * @param permissionIds 권한 ID 목록
     * @return PermissionJpaEntity 목록
     */
    public List<PermissionJpaEntity> findAllByIds(List<Long> permissionIds) {
        return queryFactory
                .selectFrom(permissionJpaEntity)
                .where(
                        conditionBuilder.permissionIdIn(permissionIds),
                        conditionBuilder.notDeleted())
                .fetch();
    }

    /**
     * permissionKey 목록으로 권한 다건 조회
     *
     * <p>벌크 동기화 시 기존 Permission을 한 번에 조회합니다.
     *
     * @param permissionKeys 권한 키 목록
     * @return PermissionJpaEntity 목록
     */
    public List<PermissionJpaEntity> findAllByPermissionKeys(List<String> permissionKeys) {
        return queryFactory
                .selectFrom(permissionJpaEntity)
                .where(
                        conditionBuilder.permissionKeyIn(permissionKeys),
                        conditionBuilder.notDeleted())
                .fetch();
    }
}
