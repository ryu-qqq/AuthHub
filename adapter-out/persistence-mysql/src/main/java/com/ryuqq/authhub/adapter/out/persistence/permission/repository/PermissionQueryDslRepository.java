package com.ryuqq.authhub.adapter.out.persistence.permission.repository;

import static com.ryuqq.authhub.adapter.out.persistence.permission.entity.QPermissionJpaEntity.permissionJpaEntity;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.authhub.adapter.out.persistence.permission.entity.PermissionJpaEntity;
import com.ryuqq.authhub.domain.permission.vo.PermissionType;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
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
 *   <li>search() - 조건 검색
 *   <li>count() - 조건 개수 조회
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
     * 권한 검색 (페이징)
     *
     * @param resource 리소스 필터 (null 허용, 부분 검색)
     * @param action 액션 필터 (null 허용, 부분 검색)
     * @param type 권한 유형 필터 (null 허용)
     * @param offset 시작 위치
     * @param limit 조회 개수
     * @return PermissionJpaEntity 목록
     */
    public List<PermissionJpaEntity> search(
            String resource, String action, String type, int offset, int limit) {
        BooleanBuilder builder = buildCondition(resource, action, type);

        return queryFactory
                .selectFrom(permissionJpaEntity)
                .where(builder)
                .orderBy(permissionJpaEntity.createdAt.desc())
                .offset(offset)
                .limit(limit)
                .fetch();
    }

    /**
     * 권한 검색 개수 조회
     *
     * @param resource 리소스 필터 (null 허용, 부분 검색)
     * @param action 액션 필터 (null 허용, 부분 검색)
     * @param type 권한 유형 필터 (null 허용)
     * @return 조건에 맞는 권한 총 개수
     */
    public long count(String resource, String action, String type) {
        BooleanBuilder builder = buildCondition(resource, action, type);

        Long count =
                queryFactory
                        .select(permissionJpaEntity.count())
                        .from(permissionJpaEntity)
                        .where(builder)
                        .fetchOne();
        return count != null ? count : 0L;
    }

    /**
     * 검색 조건 빌더 생성
     *
     * @param resource 리소스 필터
     * @param action 액션 필터
     * @param type 타입 필터
     * @return BooleanBuilder
     */
    private BooleanBuilder buildCondition(String resource, String action, String type) {
        BooleanBuilder builder = new BooleanBuilder();

        // 삭제되지 않은 권한만 조회
        builder.and(permissionJpaEntity.deleted.eq(false));

        if (resource != null && !resource.isBlank()) {
            builder.and(permissionJpaEntity.resource.containsIgnoreCase(resource));
        }
        if (action != null && !action.isBlank()) {
            builder.and(permissionJpaEntity.action.containsIgnoreCase(action));
        }
        if (type != null && !type.isBlank()) {
            PermissionType permissionType = parseType(type);
            if (permissionType != null) {
                builder.and(permissionJpaEntity.type.eq(permissionType));
            }
        }
        return builder;
    }

    /**
     * 문자열을 PermissionType으로 변환
     *
     * @param type 타입 문자열
     * @return PermissionType (유효하지 않으면 null)
     */
    private PermissionType parseType(String type) {
        try {
            return PermissionType.valueOf(type.toUpperCase(Locale.ENGLISH));
        } catch (IllegalArgumentException e) {
            return null;
        }
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
