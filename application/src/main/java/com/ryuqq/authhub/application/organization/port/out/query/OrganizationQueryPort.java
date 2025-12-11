package com.ryuqq.authhub.application.organization.port.out.query;

import com.ryuqq.authhub.domain.organization.aggregate.Organization;
import com.ryuqq.authhub.domain.organization.identifier.OrganizationId;
import com.ryuqq.authhub.domain.organization.vo.OrganizationName;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import java.util.List;
import java.util.Optional;

/**
 * OrganizationQueryPort - Organization Aggregate 조회 포트 (Query)
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
public interface OrganizationQueryPort {

    /**
     * ID로 Organization 단건 조회
     *
     * @param id Organization ID (Value Object)
     * @return Organization Domain (Optional)
     */
    Optional<Organization> findById(OrganizationId id);

    /**
     * ID로 Organization 존재 여부 확인
     *
     * @param id Organization ID (Value Object)
     * @return 존재 여부
     */
    boolean existsById(OrganizationId id);

    /**
     * 테넌트 내 이름 중복 확인
     *
     * @param tenantId Tenant ID (Value Object)
     * @param name Organization 이름 (Value Object)
     * @return 존재 여부
     */
    boolean existsByTenantIdAndName(TenantId tenantId, OrganizationName name);

    /**
     * 테넌트 범위 내 조직 목록 조회 (페이징)
     *
     * @param tenantId Tenant ID (Value Object)
     * @param name Organization 이름 필터 (null 허용, 부분 검색)
     * @param status Organization 상태 필터 (null 허용)
     * @param offset 시작 위치
     * @param limit 조회 개수
     * @return Organization Domain 목록
     */
    List<Organization> findAllByTenantIdAndCriteria(
            TenantId tenantId, String name, String status, int offset, int limit);

    /**
     * 테넌트 범위 내 조직 개수 조회
     *
     * @param tenantId Tenant ID (Value Object)
     * @param name Organization 이름 필터 (null 허용, 부분 검색)
     * @param status Organization 상태 필터 (null 허용)
     * @return 조건에 맞는 Organization 총 개수
     */
    long countByTenantIdAndCriteria(TenantId tenantId, String name, String status);
}
