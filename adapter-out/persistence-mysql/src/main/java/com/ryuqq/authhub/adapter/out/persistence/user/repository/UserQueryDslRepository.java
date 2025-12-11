package com.ryuqq.authhub.adapter.out.persistence.user.repository;

import static com.ryuqq.authhub.adapter.out.persistence.user.entity.QUserJpaEntity.userJpaEntity;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.authhub.adapter.out.persistence.user.entity.UserJpaEntity;
import com.ryuqq.authhub.domain.user.vo.UserStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

/**
 * UserQueryDslRepository - 사용자 QueryDSL Repository (Query 전용)
 *
 * <p>QueryDSL 기반 조회 작업을 담당합니다.
 *
 * <p><strong>책임:</strong>
 *
 * <ul>
 *   <li>findByUserId() - UUID로 단건 조회
 *   <li>findByTenantIdAndOrganizationIdAndIdentifier() - 식별자로 조회
 *   <li>existsByTenantIdAndOrganizationIdAndIdentifier() - 식별자 존재 여부 확인
 *   <li>search() - 조건 검색
 *   <li>count() - 조건 개수 조회
 * </ul>
 *
 * <p><strong>CQRS 패턴:</strong>
 *
 * <ul>
 *   <li>Command: UserJpaRepository (JPA)
 *   <li>Query: UserQueryDslRepository (QueryDSL)
 * </ul>
 *
 * <p><strong>규칙:</strong>
 *
 * <ul>
 *   <li>Entity 반환 (Domain 변환은 Adapter에서)
 *   <li>Join 금지 (N+1 해결은 Application Layer에서)
 *   <li>비즈니스 로직 금지
 *   <li>DELETED 상태 사용자는 제외하지 않음 (Application에서 판단)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Repository
public class UserQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    public UserQueryDslRepository(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    /**
     * UUID로 사용자 단건 조회
     *
     * @param userId 사용자 UUID
     * @return Optional<UserJpaEntity>
     */
    public Optional<UserJpaEntity> findByUserId(UUID userId) {
        UserJpaEntity result =
                queryFactory
                        .selectFrom(userJpaEntity)
                        .where(userJpaEntity.userId.eq(userId))
                        .fetchOne();
        return Optional.ofNullable(result);
    }

    /**
     * 테넌트 내 식별자로 사용자 단건 조회
     *
     * @param tenantId 테넌트 UUID
     * @param identifier 사용자 식별자
     * @return Optional<UserJpaEntity>
     */
    public Optional<UserJpaEntity> findByTenantIdAndIdentifier(UUID tenantId, String identifier) {
        UserJpaEntity result =
                queryFactory
                        .selectFrom(userJpaEntity)
                        .where(
                                userJpaEntity.tenantId.eq(tenantId),
                                userJpaEntity.identifier.eq(identifier))
                        .fetchOne();
        return Optional.ofNullable(result);
    }

    /**
     * 식별자로 사용자 단건 조회
     *
     * <p>테넌트와 무관하게 식별자로 사용자를 조회합니다. 로그인 시 사용됩니다.
     *
     * @param identifier 사용자 식별자 (이메일 또는 사용자명)
     * @return Optional<UserJpaEntity>
     */
    public Optional<UserJpaEntity> findByIdentifier(String identifier) {
        UserJpaEntity result =
                queryFactory
                        .selectFrom(userJpaEntity)
                        .where(userJpaEntity.identifier.eq(identifier))
                        .fetchOne();
        return Optional.ofNullable(result);
    }

    /**
     * 테넌트/조직 내 식별자로 사용자 단건 조회
     *
     * @param tenantId 테넌트 UUID
     * @param organizationId 조직 UUID
     * @param identifier 사용자 식별자
     * @return Optional<UserJpaEntity>
     */
    public Optional<UserJpaEntity> findByTenantIdAndOrganizationIdAndIdentifier(
            UUID tenantId, UUID organizationId, String identifier) {
        UserJpaEntity result =
                queryFactory
                        .selectFrom(userJpaEntity)
                        .where(
                                userJpaEntity.tenantId.eq(tenantId),
                                userJpaEntity.organizationId.eq(organizationId),
                                userJpaEntity.identifier.eq(identifier))
                        .fetchOne();
        return Optional.ofNullable(result);
    }

    /**
     * 테넌트/조직 내 식별자 존재 여부 확인
     *
     * @param tenantId 테넌트 UUID
     * @param organizationId 조직 UUID
     * @param identifier 사용자 식별자
     * @return 존재 여부
     */
    public boolean existsByTenantIdAndOrganizationIdAndIdentifier(
            UUID tenantId, UUID organizationId, String identifier) {
        Integer result =
                queryFactory
                        .selectOne()
                        .from(userJpaEntity)
                        .where(
                                userJpaEntity.tenantId.eq(tenantId),
                                userJpaEntity.organizationId.eq(organizationId),
                                userJpaEntity.identifier.eq(identifier))
                        .fetchFirst();
        return result != null;
    }

    /**
     * 사용자 검색 (페이징)
     *
     * @param tenantId 테넌트 UUID 필터 (null 허용)
     * @param organizationId 조직 UUID 필터 (null 허용)
     * @param identifier 식별자 필터 (null 허용, 부분 검색)
     * @param status 상태 필터 (null 허용)
     * @param offset 시작 위치
     * @param limit 조회 개수
     * @return UserJpaEntity 목록
     */
    public List<UserJpaEntity> search(
            UUID tenantId,
            UUID organizationId,
            String identifier,
            UserStatus status,
            int offset,
            int limit) {
        BooleanBuilder builder = buildSearchCondition(tenantId, organizationId, identifier, status);

        return queryFactory
                .selectFrom(userJpaEntity)
                .where(builder)
                .orderBy(userJpaEntity.createdAt.desc())
                .offset(offset)
                .limit(limit)
                .fetch();
    }

    /**
     * 사용자 검색 개수 조회
     *
     * @param tenantId 테넌트 UUID 필터 (null 허용)
     * @param organizationId 조직 UUID 필터 (null 허용)
     * @param identifier 식별자 필터 (null 허용, 부분 검색)
     * @param status 상태 필터 (null 허용)
     * @return 조건에 맞는 사용자 총 개수
     */
    public long count(UUID tenantId, UUID organizationId, String identifier, UserStatus status) {
        BooleanBuilder builder = buildSearchCondition(tenantId, organizationId, identifier, status);

        Long count =
                queryFactory
                        .select(userJpaEntity.count())
                        .from(userJpaEntity)
                        .where(builder)
                        .fetchOne();
        return count != null ? count : 0L;
    }

    /**
     * 검색 조건 빌더 생성
     *
     * @param tenantId 테넌트 UUID 필터
     * @param organizationId 조직 UUID 필터
     * @param identifier 식별자 필터
     * @param status 상태 필터
     * @return BooleanBuilder
     */
    private BooleanBuilder buildSearchCondition(
            UUID tenantId, UUID organizationId, String identifier, UserStatus status) {
        BooleanBuilder builder = new BooleanBuilder();

        if (tenantId != null) {
            builder.and(userJpaEntity.tenantId.eq(tenantId));
        }
        if (organizationId != null) {
            builder.and(userJpaEntity.organizationId.eq(organizationId));
        }
        if (identifier != null && !identifier.isBlank()) {
            builder.and(userJpaEntity.identifier.containsIgnoreCase(identifier));
        }
        if (status != null) {
            builder.and(userJpaEntity.status.eq(status));
        }
        return builder;
    }
}
