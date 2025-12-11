package com.ryuqq.authhub.adapter.out.persistence.organization.repository;

import static com.ryuqq.authhub.adapter.out.persistence.organization.entity.QOrganizationJpaEntity.organizationJpaEntity;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.authhub.adapter.out.persistence.organization.entity.OrganizationJpaEntity;
import com.ryuqq.authhub.domain.organization.vo.OrganizationStatus;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

/**
 * OrganizationQueryDslRepository - 조직 QueryDSL Repository (Query 전용)
 *
 * <p>QueryDSL 기반 조회 작업을 담당합니다.
 *
 * <p><strong>책임:</strong>
 *
 * <ul>
 *   <li>findByOrganizationId() - ID로 단건 조회
 *   <li>existsByOrganizationId() - ID 존재 여부 확인
 *   <li>existsByTenantIdAndName() - 테넌트 내 이름 중복 확인
 *   <li>findAllByTenantIdAndCriteria() - 테넌트 범위 조건 검색
 * </ul>
 *
 * <p><strong>CQRS 패턴:</strong>
 *
 * <ul>
 *   <li>Command: OrganizationJpaRepository (JPA)
 *   <li>Query: OrganizationQueryDslRepository (QueryDSL)
 * </ul>
 *
 * <p><strong>규칙:</strong>
 *
 * <ul>
 *   <li>Entity 반환 (Domain 변환은 Adapter에서)
 *   <li>Join 금지 (N+1 해결은 Application Layer에서)
 *   <li>비즈니스 로직 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Repository
public class OrganizationQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    public OrganizationQueryDslRepository(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    /**
     * UUID로 조직 단건 조회
     *
     * @param organizationId 조직 UUID
     * @return Optional<OrganizationJpaEntity>
     */
    public Optional<OrganizationJpaEntity> findByOrganizationId(UUID organizationId) {
        OrganizationJpaEntity result =
                queryFactory
                        .selectFrom(organizationJpaEntity)
                        .where(organizationJpaEntity.organizationId.eq(organizationId))
                        .fetchOne();
        return Optional.ofNullable(result);
    }

    /**
     * UUID 존재 여부 확인
     *
     * @param organizationId 조직 UUID
     * @return 존재 여부
     */
    public boolean existsByOrganizationId(UUID organizationId) {
        Integer result =
                queryFactory
                        .selectOne()
                        .from(organizationJpaEntity)
                        .where(organizationJpaEntity.organizationId.eq(organizationId))
                        .fetchFirst();
        return result != null;
    }

    /**
     * 테넌트 내 이름 중복 확인
     *
     * @param tenantId 테넌트 UUID
     * @param name 조직 이름
     * @return 존재 여부
     */
    public boolean existsByTenantIdAndName(UUID tenantId, String name) {
        Integer result =
                queryFactory
                        .selectOne()
                        .from(organizationJpaEntity)
                        .where(
                                organizationJpaEntity.tenantId.eq(tenantId),
                                organizationJpaEntity.name.eq(name))
                        .fetchFirst();
        return result != null;
    }

    /**
     * 테넌트 범위 내 조직 목록 조회 (페이징)
     *
     * @param tenantId 테넌트 UUID
     * @param name 조직 이름 필터 (null 허용, 부분 검색)
     * @param status 조직 상태 필터 (null 허용)
     * @param offset 시작 위치
     * @param limit 조회 개수
     * @return OrganizationJpaEntity 목록
     */
    public List<OrganizationJpaEntity> findAllByTenantIdAndCriteria(
            UUID tenantId, String name, String status, int offset, int limit) {
        BooleanBuilder builder = buildCondition(tenantId, name, status);

        return queryFactory
                .selectFrom(organizationJpaEntity)
                .where(builder)
                .orderBy(organizationJpaEntity.createdAt.desc())
                .offset(offset)
                .limit(limit)
                .fetch();
    }

    /**
     * 테넌트 범위 내 조직 개수 조회
     *
     * @param tenantId 테넌트 UUID
     * @param name 조직 이름 필터 (null 허용, 부분 검색)
     * @param status 조직 상태 필터 (null 허용)
     * @return 조건에 맞는 조직 총 개수
     */
    public long countByTenantIdAndCriteria(UUID tenantId, String name, String status) {
        BooleanBuilder builder = buildCondition(tenantId, name, status);

        Long count =
                queryFactory
                        .select(organizationJpaEntity.count())
                        .from(organizationJpaEntity)
                        .where(builder)
                        .fetchOne();
        return count != null ? count : 0L;
    }

    /**
     * 검색 조건 빌더 생성
     *
     * @param tenantId 테넌트 UUID
     * @param name 이름 필터
     * @param status 상태 필터
     * @return BooleanBuilder
     */
    private BooleanBuilder buildCondition(UUID tenantId, String name, String status) {
        BooleanBuilder builder = new BooleanBuilder();

        // 테넌트 ID는 필수 조건
        builder.and(organizationJpaEntity.tenantId.eq(tenantId));

        if (name != null && !name.isBlank()) {
            builder.and(organizationJpaEntity.name.containsIgnoreCase(name));
        }
        if (status != null && !status.isBlank()) {
            OrganizationStatus organizationStatus = parseStatus(status);
            if (organizationStatus != null) {
                builder.and(organizationJpaEntity.status.eq(organizationStatus));
            }
        }
        return builder;
    }

    /**
     * 문자열을 OrganizationStatus로 변환
     *
     * @param status 상태 문자열
     * @return OrganizationStatus (유효하지 않으면 null)
     */
    private OrganizationStatus parseStatus(String status) {
        try {
            return OrganizationStatus.valueOf(status.toUpperCase(Locale.ENGLISH));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
