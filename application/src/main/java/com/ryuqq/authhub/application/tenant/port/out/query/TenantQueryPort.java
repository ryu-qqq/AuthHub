package com.ryuqq.authhub.application.tenant.port.out.query;

import com.ryuqq.authhub.domain.tenant.aggregate.Tenant;
import com.ryuqq.authhub.domain.tenant.id.TenantId;
import com.ryuqq.authhub.domain.tenant.query.criteria.TenantSearchCriteria;
import com.ryuqq.authhub.domain.tenant.vo.TenantName;
import java.util.List;
import java.util.Optional;

/**
 * TenantQueryPort - Tenant Aggregate 조회 포트 (Query)
 *
 * <p>Domain Aggregate를 조회하는 읽기 전용 Port입니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>조회 메서드만 제공 (findById, existsById)
 *   <li>저장/수정/삭제 메서드 금지 (PersistencePort로 분리)
 *   <li>Value Object 파라미터 (원시 타입 금지)
 *   <li>Domain 반환 (DTO/Entity 반환 금지)
 *   <li>Optional 반환 (단건 조회 시 null 방지)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface TenantQueryPort {

    /**
     * ID로 Tenant 단건 조회
     *
     * @param id Tenant ID (Value Object)
     * @return Tenant Domain (Optional)
     */
    Optional<Tenant> findById(TenantId id);

    /**
     * ID로 Tenant 존재 여부 확인
     *
     * @param id Tenant ID (Value Object)
     * @return 존재 여부
     */
    boolean existsById(TenantId id);

    /**
     * 이름으로 Tenant 존재 여부 확인
     *
     * @param name Tenant 이름 (Value Object)
     * @return 존재 여부
     */
    boolean existsByName(TenantName name);

    /**
     * 이름으로 Tenant 존재 여부 확인 (특정 ID 제외)
     *
     * <p>수정 시 자기 자신을 제외하고 중복 검증할 때 사용합니다.
     *
     * @param name Tenant 이름 (Value Object)
     * @param excludeId 제외할 Tenant ID (Value Object)
     * @return 존재 여부
     */
    boolean existsByNameAndIdNot(TenantName name, TenantId excludeId);

    /**
     * 조건에 맞는 Tenant 목록 조회 (페이징)
     *
     * @param criteria 검색 조건 (TenantSearchCriteria)
     * @return Tenant Domain 목록
     */
    List<Tenant> findAllByCriteria(TenantSearchCriteria criteria);

    /**
     * 조건에 맞는 Tenant 개수 조회
     *
     * @param criteria 검색 조건 (TenantSearchCriteria)
     * @return 조건에 맞는 Tenant 총 개수
     */
    long countByCriteria(TenantSearchCriteria criteria);
}
