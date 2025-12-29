package com.ryuqq.authhub.adapter.out.persistence.permission.repository;

import static com.ryuqq.authhub.adapter.out.persistence.permission.entity.QPermissionJpaEntity.permissionJpaEntity;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.authhub.adapter.out.persistence.permission.entity.PermissionJpaEntity;
import com.ryuqq.authhub.application.permission.dto.query.SearchPermissionsQuery;
import com.ryuqq.authhub.domain.permission.vo.PermissionType;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
        BooleanBuilder builder = buildSearchCondition(query);
        OrderSpecifier<?> orderSpecifier = buildOrderSpecifier(query);
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
        BooleanBuilder builder = buildSearchCondition(query);

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

    /**
     * 검색 조건 빌더 생성 (Query 기반)
     *
     * @param query 검색 조건
     * @return BooleanBuilder
     */
    private BooleanBuilder buildSearchCondition(SearchPermissionsQuery query) {
        BooleanBuilder builder = new BooleanBuilder();

        // 삭제되지 않은 권한만 조회
        builder.and(permissionJpaEntity.deleted.eq(false));

        // 리소스 필터 (SearchType 기반)
        if (query.resource() != null && !query.resource().isBlank()) {
            builder.and(buildResourceCondition(query.resource(), query.searchType()));
        }

        // 액션 필터 (SearchType 기반)
        if (query.action() != null && !query.action().isBlank()) {
            builder.and(buildActionCondition(query.action(), query.searchType()));
        }

        // 다중 타입 필터
        if (query.types() != null && !query.types().isEmpty()) {
            List<PermissionType> typeEnums =
                    query.types().stream().map(this::parseType).filter(t -> t != null).toList();
            if (!typeEnums.isEmpty()) {
                builder.and(permissionJpaEntity.type.in(typeEnums));
            }
        }

        // 생성일시 범위 필터
        if (query.createdFrom() != null) {
            LocalDateTime from = toLocalDateTime(query.createdFrom());
            builder.and(permissionJpaEntity.createdAt.goe(from));
        }
        if (query.createdTo() != null) {
            LocalDateTime to = toLocalDateTime(query.createdTo());
            builder.and(permissionJpaEntity.createdAt.loe(to));
        }

        return builder;
    }

    /**
     * 리소스 검색 조건 생성 (SearchType 기반)
     *
     * @param resource 검색 리소스명
     * @param searchType 검색 타입
     * @return BooleanExpression
     */
    private BooleanExpression buildResourceCondition(String resource, String searchType) {
        String type = searchType != null ? searchType : "CONTAINS_LIKE";

        return switch (type) {
            case "PREFIX_LIKE" -> permissionJpaEntity.resource.startsWithIgnoreCase(resource);
            case "MATCH_AGAINST" -> permissionJpaEntity.resource.containsIgnoreCase(resource);
            default -> permissionJpaEntity.resource.containsIgnoreCase(resource);
        };
    }

    /**
     * 액션 검색 조건 생성 (SearchType 기반)
     *
     * @param action 검색 액션명
     * @param searchType 검색 타입
     * @return BooleanExpression
     */
    private BooleanExpression buildActionCondition(String action, String searchType) {
        String type = searchType != null ? searchType : "CONTAINS_LIKE";

        return switch (type) {
            case "PREFIX_LIKE" -> permissionJpaEntity.action.startsWithIgnoreCase(action);
            case "MATCH_AGAINST" -> permissionJpaEntity.action.containsIgnoreCase(action);
            default -> permissionJpaEntity.action.containsIgnoreCase(action);
        };
    }

    /**
     * 정렬 조건 생성 (Query 기반)
     *
     * @param query 검색 조건
     * @return OrderSpecifier
     */
    private OrderSpecifier<?> buildOrderSpecifier(SearchPermissionsQuery query) {
        String sortBy = query.sortBy() != null ? query.sortBy() : "createdAt";
        String direction = query.sortDirection() != null ? query.sortDirection() : "DESC";
        boolean isAsc = "ASC".equalsIgnoreCase(direction);

        return switch (sortBy.toLowerCase()) {
            case "resource" ->
                    isAsc
                            ? permissionJpaEntity.resource.asc()
                            : permissionJpaEntity.resource.desc();
            case "action" ->
                    isAsc ? permissionJpaEntity.action.asc() : permissionJpaEntity.action.desc();
            case "type" -> isAsc ? permissionJpaEntity.type.asc() : permissionJpaEntity.type.desc();
            case "updatedat" ->
                    isAsc
                            ? permissionJpaEntity.updatedAt.asc()
                            : permissionJpaEntity.updatedAt.desc();
            default ->
                    isAsc
                            ? permissionJpaEntity.createdAt.asc()
                            : permissionJpaEntity.createdAt.desc();
        };
    }

    /**
     * String을 PermissionType enum으로 변환
     *
     * @param type 타입 문자열
     * @return PermissionType (null if invalid)
     */
    private PermissionType parseType(String type) {
        if (type == null || type.isBlank()) {
            return null;
        }
        try {
            return PermissionType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Instant를 LocalDateTime으로 변환
     *
     * @param instant Instant
     * @return LocalDateTime
     */
    private LocalDateTime toLocalDateTime(Instant instant) {
        return instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}
