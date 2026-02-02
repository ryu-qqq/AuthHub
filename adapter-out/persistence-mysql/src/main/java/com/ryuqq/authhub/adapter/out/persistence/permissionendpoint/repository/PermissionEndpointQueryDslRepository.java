package com.ryuqq.authhub.adapter.out.persistence.permissionendpoint.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.authhub.adapter.out.persistence.permission.entity.QPermissionJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.permissionendpoint.condition.PermissionEndpointConditionBuilder;
import com.ryuqq.authhub.adapter.out.persistence.permissionendpoint.entity.PermissionEndpointJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.permissionendpoint.entity.QPermissionEndpointJpaEntity;
import com.ryuqq.authhub.application.permissionendpoint.dto.response.EndpointPermissionSpecResult;
import com.ryuqq.authhub.domain.permissionendpoint.query.criteria.PermissionEndpointSearchCriteria;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 * PermissionEndpointQueryDslRepository - PermissionEndpoint QueryDSL Repository
 *
 * <p>복잡한 검색 조건을 처리하는 QueryDSL 전용 Repository입니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>ConditionBuilder 의존 필수
 *   <li>Criteria 기반 조회만 허용
 *   <li>Entity 반환 (Domain 변환은 Adapter에서)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Repository
public class PermissionEndpointQueryDslRepository {

    private static final QPermissionEndpointJpaEntity permissionEndpoint =
            QPermissionEndpointJpaEntity.permissionEndpointJpaEntity;

    private final JPAQueryFactory queryFactory;
    private final PermissionEndpointConditionBuilder conditionBuilder;

    public PermissionEndpointQueryDslRepository(
            JPAQueryFactory queryFactory, PermissionEndpointConditionBuilder conditionBuilder) {
        this.queryFactory = queryFactory;
        this.conditionBuilder = conditionBuilder;
    }

    /**
     * SearchCriteria 기반 목록 조회
     *
     * @param criteria 검색 조건
     * @return Entity 목록
     */
    public List<PermissionEndpointJpaEntity> findAllBySearchCriteria(
            PermissionEndpointSearchCriteria criteria) {
        BooleanBuilder condition = conditionBuilder.buildCondition(criteria);

        return queryFactory
                .selectFrom(permissionEndpoint)
                .where(condition)
                .orderBy(conditionBuilder.buildOrderSpecifier(criteria))
                .offset(criteria.offset())
                .limit(criteria.size())
                .fetch();
    }

    /**
     * SearchCriteria 기반 개수 조회
     *
     * @param criteria 검색 조건
     * @return 조건에 맞는 총 개수
     */
    public long countBySearchCriteria(PermissionEndpointSearchCriteria criteria) {
        BooleanBuilder condition = conditionBuilder.buildCondition(criteria);

        Long count =
                queryFactory
                        .select(permissionEndpoint.count())
                        .from(permissionEndpoint)
                        .where(condition)
                        .fetchOne();

        return count != null ? count : 0L;
    }

    /**
     * URL 패턴 매칭으로 엔드포인트 조회 (Gateway용)
     *
     * <p>정확한 URL 패턴 매칭이 아닌 패턴 검색을 수행합니다.
     *
     * @param urlPattern URL 패턴
     * @return 매칭되는 엔드포인트 목록
     */
    public List<PermissionEndpointJpaEntity> findByUrlPatternLike(String urlPattern) {
        return queryFactory
                .selectFrom(permissionEndpoint)
                .where(
                        permissionEndpoint.deletedAt.isNull(),
                        permissionEndpoint.urlPattern.like("%" + urlPattern + "%"))
                .fetch();
    }

    /**
     * URL 패턴 목록으로 엔드포인트 다건 조회 (벌크 동기화용)
     *
     * @param urlPatterns URL 패턴 목록
     * @return 매칭되는 엔드포인트 목록
     */
    public List<PermissionEndpointJpaEntity> findAllByUrlPatterns(List<String> urlPatterns) {
        if (urlPatterns == null || urlPatterns.isEmpty()) {
            return List.of();
        }

        return queryFactory
                .selectFrom(permissionEndpoint)
                .where(
                        permissionEndpoint.deletedAt.isNull(),
                        permissionEndpoint.urlPattern.in(urlPatterns))
                .fetch();
    }

    /**
     * 모든 활성 엔드포인트-권한 스펙 조회 (Gateway용)
     *
     * <p>Permission과 조인하여 permissionKey를 함께 조회합니다.
     *
     * @return 엔드포인트-권한 스펙 목록
     */
    public List<EndpointPermissionSpecResult> findAllActiveSpecs() {
        QPermissionJpaEntity permission = QPermissionJpaEntity.permissionJpaEntity;

        return queryFactory
                .select(
                        Projections.constructor(
                                EndpointPermissionSpecResult.class,
                                permissionEndpoint.serviceName,
                                permissionEndpoint.urlPattern,
                                permissionEndpoint.httpMethod.stringValue(),
                                permission.permissionKey,
                                permissionEndpoint.isPublic,
                                permissionEndpoint.description))
                .from(permissionEndpoint)
                .join(permission)
                .on(permissionEndpoint.permissionId.eq(permission.permissionId))
                .where(permissionEndpoint.deletedAt.isNull(), permission.deletedAt.isNull())
                .fetch();
    }

    /**
     * 가장 최근에 수정된 활성 엔드포인트의 수정 시간 조회
     *
     * @return 가장 최근 수정 시간 (없으면 null)
     */
    public Instant findLatestUpdatedAt() {
        return queryFactory
                .select(permissionEndpoint.updatedAt.max())
                .from(permissionEndpoint)
                .where(permissionEndpoint.deletedAt.isNull())
                .fetchOne();
    }
}
