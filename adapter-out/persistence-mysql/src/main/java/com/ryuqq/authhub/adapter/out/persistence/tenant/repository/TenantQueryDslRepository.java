package com.ryuqq.authhub.adapter.out.persistence.tenant.repository;

import static com.ryuqq.authhub.adapter.out.persistence.tenant.entity.QTenantJpaEntity.tenantJpaEntity;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.authhub.adapter.out.persistence.tenant.entity.TenantJpaEntity;
import com.ryuqq.authhub.domain.tenant.vo.TenantStatus;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

/**
 * TenantQueryDslRepository - 테넌트 QueryDSL Repository (Query 전용)
 *
 * <p>QueryDSL 기반 조회 작업을 담당합니다.
 *
 * <p><strong>책임:</strong>
 *
 * <ul>
 *   <li>findById() - ID로 단건 조회
 *   <li>findByName() - 이름으로 단건 조회
 *   <li>existsByName() - 이름 존재 여부 확인
 *   <li>findByCriteria() - 조건 검색 (추후 확장)
 * </ul>
 *
 * <p><strong>CQRS 패턴:</strong>
 *
 * <ul>
 *   <li>Command: TenantJpaRepository (JPA)
 *   <li>Query: TenantQueryDslRepository (QueryDSL)
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
public class TenantQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    public TenantQueryDslRepository(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    /**
     * UUID로 테넌트 단건 조회
     *
     * @param tenantId 테넌트 UUID
     * @return Optional<TenantJpaEntity>
     */
    public Optional<TenantJpaEntity> findByTenantId(UUID tenantId) {
        TenantJpaEntity result =
                queryFactory
                        .selectFrom(tenantJpaEntity)
                        .where(tenantJpaEntity.tenantId.eq(tenantId))
                        .fetchOne();
        return Optional.ofNullable(result);
    }

    /**
     * 이름으로 테넌트 단건 조회
     *
     * @param name 테넌트 이름
     * @return Optional<TenantJpaEntity>
     */
    public Optional<TenantJpaEntity> findByName(String name) {
        TenantJpaEntity result =
                queryFactory
                        .selectFrom(tenantJpaEntity)
                        .where(tenantJpaEntity.name.eq(name))
                        .fetchOne();
        return Optional.ofNullable(result);
    }

    /**
     * 이름 존재 여부 확인
     *
     * @param name 테넌트 이름
     * @return 존재 여부
     */
    public boolean existsByName(String name) {
        Integer result =
                queryFactory
                        .selectOne()
                        .from(tenantJpaEntity)
                        .where(tenantJpaEntity.name.eq(name))
                        .fetchFirst();
        return result != null;
    }

    /**
     * UUID 존재 여부 확인
     *
     * @param tenantId 테넌트 UUID
     * @return 존재 여부
     */
    public boolean existsByTenantId(UUID tenantId) {
        Integer result =
                queryFactory
                        .selectOne()
                        .from(tenantJpaEntity)
                        .where(tenantJpaEntity.tenantId.eq(tenantId))
                        .fetchFirst();
        return result != null;
    }

    /**
     * 조건에 맞는 테넌트 목록 조회 (페이징)
     *
     * @param name 테넌트 이름 필터 (null 허용, 부분 검색)
     * @param status 테넌트 상태 필터 (null 허용)
     * @param offset 시작 위치
     * @param limit 조회 개수
     * @return TenantJpaEntity 목록
     */
    public List<TenantJpaEntity> findAllByCriteria(
            String name, String status, int offset, int limit) {
        BooleanBuilder builder = buildCondition(name, status);

        return queryFactory
                .selectFrom(tenantJpaEntity)
                .where(builder)
                .orderBy(tenantJpaEntity.createdAt.desc())
                .offset(offset)
                .limit(limit)
                .fetch();
    }

    /**
     * 조건에 맞는 테넌트 개수 조회
     *
     * @param name 테넌트 이름 필터 (null 허용, 부분 검색)
     * @param status 테넌트 상태 필터 (null 허용)
     * @return 조건에 맞는 테넌트 총 개수
     */
    public long countAll(String name, String status) {
        BooleanBuilder builder = buildCondition(name, status);

        Long count =
                queryFactory
                        .select(tenantJpaEntity.count())
                        .from(tenantJpaEntity)
                        .where(builder)
                        .fetchOne();
        return count != null ? count : 0L;
    }

    /**
     * 검색 조건 빌더 생성
     *
     * @param name 이름 필터
     * @param status 상태 필터
     * @return BooleanBuilder
     */
    private BooleanBuilder buildCondition(String name, String status) {
        BooleanBuilder builder = new BooleanBuilder();

        if (name != null && !name.isBlank()) {
            builder.and(tenantJpaEntity.name.containsIgnoreCase(name));
        }
        if (status != null && !status.isBlank()) {
            TenantStatus tenantStatus = parseStatus(status);
            if (tenantStatus != null) {
                builder.and(tenantJpaEntity.status.eq(tenantStatus));
            }
        }
        return builder;
    }

    /**
     * 문자열을 TenantStatus로 변환
     *
     * @param status 상태 문자열
     * @return TenantStatus (유효하지 않으면 null)
     */
    private TenantStatus parseStatus(String status) {
        try {
            return TenantStatus.valueOf(status.toUpperCase(Locale.ENGLISH));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
