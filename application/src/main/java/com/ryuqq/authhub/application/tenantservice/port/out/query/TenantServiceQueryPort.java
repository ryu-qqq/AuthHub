package com.ryuqq.authhub.application.tenantservice.port.out.query;

import com.ryuqq.authhub.domain.service.id.ServiceId;
import com.ryuqq.authhub.domain.tenant.id.TenantId;
import com.ryuqq.authhub.domain.tenantservice.aggregate.TenantService;
import com.ryuqq.authhub.domain.tenantservice.id.TenantServiceId;
import com.ryuqq.authhub.domain.tenantservice.query.criteria.TenantServiceSearchCriteria;
import java.util.List;
import java.util.Optional;

/**
 * TenantServiceQueryPort - TenantService Aggregate 조회 포트 (Query)
 *
 * <p>Domain Aggregate를 조회하는 읽기 전용 Port입니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>조회 메서드만 제공
 *   <li>저장/수정/삭제 메서드 금지 (CommandPort로 분리)
 *   <li>Value Object 파라미터 (원시 타입 금지)
 *   <li>Domain 반환 (DTO/Entity 반환 금지)
 *   <li>Optional 반환 (단건 조회 시 null 방지)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface TenantServiceQueryPort {

    /**
     * ID로 TenantService 단건 조회
     *
     * @param id TenantService ID (Value Object)
     * @return TenantService Domain (Optional)
     */
    Optional<TenantService> findById(TenantServiceId id);

    /**
     * ID로 TenantService 존재 여부 확인
     *
     * @param id TenantService ID (Value Object)
     * @return 존재 여부
     */
    boolean existsById(TenantServiceId id);

    /**
     * 테넌트 ID + 서비스 ID로 구독 존재 여부 확인
     *
     * @param tenantId 테넌트 ID (Value Object)
     * @param serviceId 서비스 ID (Value Object)
     * @return 존재 여부
     */
    boolean existsByTenantIdAndServiceId(TenantId tenantId, ServiceId serviceId);

    /**
     * 테넌트 ID + 서비스 ID로 TenantService 조회
     *
     * @param tenantId 테넌트 ID (Value Object)
     * @param serviceId 서비스 ID (Value Object)
     * @return TenantService Domain (Optional)
     */
    Optional<TenantService> findByTenantIdAndServiceId(TenantId tenantId, ServiceId serviceId);

    /**
     * 조건에 맞는 TenantService 목록 조회 (페이징)
     *
     * @param criteria 검색 조건
     * @return TenantService Domain 목록
     */
    List<TenantService> findAllByCriteria(TenantServiceSearchCriteria criteria);

    /**
     * 조건에 맞는 TenantService 개수 조회
     *
     * @param criteria 검색 조건
     * @return 조건에 맞는 TenantService 총 개수
     */
    long countByCriteria(TenantServiceSearchCriteria criteria);
}
