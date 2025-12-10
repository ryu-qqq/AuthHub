package com.ryuqq.authhub.adapter.out.persistence.role.repository;

import static com.ryuqq.authhub.adapter.out.persistence.role.entity.QRoleJpaEntity.roleJpaEntity;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.authhub.adapter.out.persistence.role.entity.RoleJpaEntity;
import com.ryuqq.authhub.domain.role.vo.RoleScope;
import com.ryuqq.authhub.domain.role.vo.RoleType;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

/**
 * RoleQueryDslRepository - 역할 QueryDSL Repository (Query 전용)
 *
 * <p>QueryDSL 기반 조회 작업을 담당합니다.
 *
 * <p><strong>책임:</strong>
 *
 * <ul>
 *   <li>findByRoleId() - ID로 단건 조회
 *   <li>findByTenantIdAndName() - 테넌트 내 역할 이름으로 조회
 *   <li>existsByTenantIdAndName() - 테넌트 내 역할 이름 존재 여부 확인
 *   <li>search() - 조건 검색
 *   <li>count() - 조건 개수 조회
 * </ul>
 *
 * <p><strong>CQRS 패턴:</strong>
 *
 * <ul>
 *   <li>Command: RoleJpaRepository (JPA)
 *   <li>Query: RoleQueryDslRepository (QueryDSL)
 * </ul>
 *
 * <p><strong>규칙:</strong>
 *
 * <ul>
 *   <li>Entity 반환 (Domain 변환은 Adapter에서)
 *   <li>Join 금지 (N+1 해결은 Application Layer에서)
 *   <li>비즈니스 로직 금지
 *   <li>삭제되지 않은 역할만 조회 (deleted = false)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Repository
public class RoleQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    public RoleQueryDslRepository(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    /**
     * UUID로 역할 단건 조회
     *
     * @param roleId 역할 UUID
     * @return Optional<RoleJpaEntity>
     */
    public Optional<RoleJpaEntity> findByRoleId(UUID roleId) {
        RoleJpaEntity result =
                queryFactory
                        .selectFrom(roleJpaEntity)
                        .where(roleJpaEntity.roleId.eq(roleId), roleJpaEntity.deleted.eq(false))
                        .fetchOne();
        return Optional.ofNullable(result);
    }

    /**
     * 테넌트 내 역할 이름으로 역할 단건 조회
     *
     * @param tenantId 테넌트 UUID (null일 경우 GLOBAL 범위)
     * @param name 역할 이름
     * @return Optional<RoleJpaEntity>
     */
    public Optional<RoleJpaEntity> findByTenantIdAndName(UUID tenantId, String name) {
        RoleJpaEntity result =
                queryFactory
                        .selectFrom(roleJpaEntity)
                        .where(
                                buildTenantCondition(tenantId),
                                roleJpaEntity.name.eq(name),
                                roleJpaEntity.deleted.eq(false))
                        .fetchOne();
        return Optional.ofNullable(result);
    }

    /**
     * 테넌트 내 역할 이름 존재 여부 확인
     *
     * @param tenantId 테넌트 UUID (null일 경우 GLOBAL 범위)
     * @param name 역할 이름
     * @return 존재 여부
     */
    public boolean existsByTenantIdAndName(UUID tenantId, String name) {
        Integer result =
                queryFactory
                        .selectOne()
                        .from(roleJpaEntity)
                        .where(
                                buildTenantCondition(tenantId),
                                roleJpaEntity.name.eq(name),
                                roleJpaEntity.deleted.eq(false))
                        .fetchFirst();
        return result != null;
    }

    /**
     * 역할 검색 (페이징)
     *
     * @param tenantId 테넌트 UUID 필터 (null 허용)
     * @param name 역할 이름 필터 (null 허용, 부분 검색)
     * @param scope 역할 범위 필터 (null 허용)
     * @param type 역할 유형 필터 (null 허용)
     * @param offset 시작 위치
     * @param limit 조회 개수
     * @return RoleJpaEntity 목록
     */
    public List<RoleJpaEntity> search(
            UUID tenantId, String name, RoleScope scope, RoleType type, int offset, int limit) {
        BooleanBuilder builder = buildSearchCondition(tenantId, name, scope, type);

        return queryFactory
                .selectFrom(roleJpaEntity)
                .where(builder)
                .orderBy(roleJpaEntity.createdAt.desc())
                .offset(offset)
                .limit(limit)
                .fetch();
    }

    /**
     * 역할 검색 개수 조회
     *
     * @param tenantId 테넌트 UUID 필터 (null 허용)
     * @param name 역할 이름 필터 (null 허용, 부분 검색)
     * @param scope 역할 범위 필터 (null 허용)
     * @param type 역할 유형 필터 (null 허용)
     * @return 조건에 맞는 역할 총 개수
     */
    public long count(UUID tenantId, String name, RoleScope scope, RoleType type) {
        BooleanBuilder builder = buildSearchCondition(tenantId, name, scope, type);

        Long count =
                queryFactory
                        .select(roleJpaEntity.count())
                        .from(roleJpaEntity)
                        .where(builder)
                        .fetchOne();
        return count != null ? count : 0L;
    }

    /**
     * 테넌트 조건 빌더 (null 체크 포함)
     *
     * @param tenantId 테넌트 UUID
     * @return BooleanBuilder
     */
    private BooleanBuilder buildTenantCondition(UUID tenantId) {
        BooleanBuilder builder = new BooleanBuilder();
        if (tenantId != null) {
            builder.and(roleJpaEntity.tenantId.eq(tenantId));
        } else {
            builder.and(roleJpaEntity.tenantId.isNull());
        }
        return builder;
    }

    /**
     * 여러 ID로 역할 목록 조회
     *
     * @param roleIds 역할 UUID 컬렉션
     * @return RoleJpaEntity 목록
     */
    public List<RoleJpaEntity> findAllByIds(Collection<UUID> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return List.of();
        }
        return queryFactory
                .selectFrom(roleJpaEntity)
                .where(roleJpaEntity.roleId.in(roleIds), roleJpaEntity.deleted.eq(false))
                .fetch();
    }

    /**
     * 검색 조건 빌더 생성
     *
     * @param tenantId 테넌트 UUID 필터
     * @param name 역할 이름 필터
     * @param scope 역할 범위 필터
     * @param type 역할 유형 필터
     * @return BooleanBuilder
     */
    private BooleanBuilder buildSearchCondition(
            UUID tenantId, String name, RoleScope scope, RoleType type) {
        BooleanBuilder builder = new BooleanBuilder();

        // 삭제되지 않은 역할만 조회
        builder.and(roleJpaEntity.deleted.eq(false));

        if (tenantId != null) {
            builder.and(roleJpaEntity.tenantId.eq(tenantId));
        }
        if (name != null && !name.isBlank()) {
            builder.and(roleJpaEntity.name.containsIgnoreCase(name));
        }
        if (scope != null) {
            builder.and(roleJpaEntity.scope.eq(scope));
        }
        if (type != null) {
            builder.and(roleJpaEntity.type.eq(type));
        }
        return builder;
    }
}
