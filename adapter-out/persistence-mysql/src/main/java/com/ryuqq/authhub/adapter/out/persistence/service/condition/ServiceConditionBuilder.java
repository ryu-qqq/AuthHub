package com.ryuqq.authhub.adapter.out.persistence.service.condition;

import static com.ryuqq.authhub.adapter.out.persistence.service.entity.QServiceJpaEntity.serviceJpaEntity;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.ryuqq.authhub.domain.common.vo.SortDirection;
import com.ryuqq.authhub.domain.service.query.criteria.ServiceSearchCriteria;
import com.ryuqq.authhub.domain.service.vo.ServiceSearchField;
import com.ryuqq.authhub.domain.service.vo.ServiceSortKey;
import com.ryuqq.authhub.domain.service.vo.ServiceStatus;
import java.time.Instant;
import org.springframework.stereotype.Component;

/**
 * ServiceConditionBuilder - 서비스 QueryDSL 조건 빌더
 *
 * <p>QueryDSL 조건 생성 로직을 분리하여 Repository의 책임을 단순화합니다.
 *
 * <p><strong>책임:</strong>
 *
 * <ul>
 *   <li>buildCondition() - 검색 조건 BooleanBuilder 생성
 *   <li>buildOrderSpecifier() - 정렬 조건 OrderSpecifier 생성
 *   <li>searchByField() - SearchField 기반 검색 조건 생성
 * </ul>
 *
 * <p><strong>규칙:</strong>
 *
 * <ul>
 *   <li>Entity 직접 의존 금지 (Q-Class만 사용)
 *   <li>비즈니스 로직 금지 (조건 생성만)
 *   <li>null-safe 조건 처리
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class ServiceConditionBuilder {

    /**
     * 검색 조건 빌더 생성
     *
     * <p>ServiceSearchCriteria 기반으로 BooleanBuilder를 생성합니다.
     *
     * @param criteria 검색 조건
     * @return BooleanBuilder
     */
    public BooleanBuilder buildCondition(ServiceSearchCriteria criteria) {
        BooleanBuilder builder = new BooleanBuilder();

        // 검색어 + 검색 필드 조건
        builder.and(searchByField(criteria.searchField(), criteria.searchWord()));

        // 다중 상태 필터
        if (criteria.hasStatusFilter()) {
            builder.and(serviceJpaEntity.status.in(criteria.statuses()));
        }

        // 날짜 범위 필터 (Law of Demeter 준수 - 편의 메서드 사용)
        builder.and(createdAtGoe(criteria.startInstant()));
        builder.and(createdAtLoe(criteria.endInstant()));

        return builder;
    }

    /**
     * 정렬 조건 빌더 생성
     *
     * @param criteria 검색 조건
     * @return OrderSpecifier
     */
    public OrderSpecifier<?> buildOrderSpecifier(ServiceSearchCriteria criteria) {
        ServiceSortKey sortKey =
                criteria.sortKey() != null ? criteria.sortKey() : ServiceSortKey.CREATED_AT;
        boolean isAsc = criteria.sortDirection() == SortDirection.ASC;

        return switch (sortKey) {
            case UPDATED_AT ->
                    isAsc ? serviceJpaEntity.updatedAt.asc() : serviceJpaEntity.updatedAt.desc();
            case NAME -> isAsc ? serviceJpaEntity.name.asc() : serviceJpaEntity.name.desc();
            case CREATED_AT ->
                    isAsc ? serviceJpaEntity.createdAt.asc() : serviceJpaEntity.createdAt.desc();
        };
    }

    /**
     * SearchField 기반 검색 조건 생성
     *
     * <p>검색 필드(ServiceSearchField)에 따라 해당 컬럼에 검색어를 적용합니다.
     *
     * @param searchField 검색 필드 (ServiceSearchField enum)
     * @param searchWord 검색어
     * @return BooleanExpression (null 허용 - 조건 없음)
     */
    public BooleanExpression searchByField(ServiceSearchField searchField, String searchWord) {
        if (searchField == null || searchWord == null || searchWord.isBlank()) {
            return null;
        }

        return switch (searchField) {
            case SERVICE_CODE -> serviceJpaEntity.serviceCode.containsIgnoreCase(searchWord);
            case NAME -> serviceJpaEntity.name.containsIgnoreCase(searchWord);
        };
    }

    /**
     * 서비스 ID 일치 조건 (Long PK)
     *
     * @param serviceId 서비스 ID (Long)
     * @return BooleanExpression (null 허용)
     */
    public BooleanExpression serviceIdEquals(Long serviceId) {
        return serviceId != null ? serviceJpaEntity.serviceId.eq(serviceId) : null;
    }

    /**
     * 서비스 코드 일치 조건
     *
     * @param serviceCode 서비스 코드 (String)
     * @return BooleanExpression (null 허용)
     */
    public BooleanExpression serviceCodeEquals(String serviceCode) {
        return serviceCode != null ? serviceJpaEntity.serviceCode.eq(serviceCode) : null;
    }

    /**
     * 활성 상태 조건
     *
     * @return BooleanExpression
     */
    public BooleanExpression statusActive() {
        return serviceJpaEntity.status.eq(ServiceStatus.ACTIVE);
    }

    /**
     * 생성일시 이상 조건
     *
     * @param startInstant 시작 일시 (Instant, UTC)
     * @return BooleanExpression (null 허용)
     */
    public BooleanExpression createdAtGoe(Instant startInstant) {
        return startInstant != null ? serviceJpaEntity.createdAt.goe(startInstant) : null;
    }

    /**
     * 생성일시 이하 조건
     *
     * @param endInstant 종료 일시 (Instant, UTC)
     * @return BooleanExpression (null 허용)
     */
    public BooleanExpression createdAtLoe(Instant endInstant) {
        return endInstant != null ? serviceJpaEntity.createdAt.loe(endInstant) : null;
    }
}
