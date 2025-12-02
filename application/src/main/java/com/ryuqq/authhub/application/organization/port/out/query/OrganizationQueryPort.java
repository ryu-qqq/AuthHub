package com.ryuqq.authhub.application.organization.port.out.query;

import com.ryuqq.authhub.domain.organization.aggregate.Organization;
import com.ryuqq.authhub.domain.organization.identifier.OrganizationId;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import java.util.Optional;

/**
 * OrganizationQueryPort - Organization Aggregate 조회 포트 (Query)
 *
 * <p>Domain Aggregate를 조회하는 읽기 전용 Port입니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 * <ul>
 *   <li>조회 메서드만 제공 (findById, existsById, existsByTenantId)</li>
 *   <li>저장/수정/삭제 메서드 금지 (PersistencePort로 분리)</li>
 *   <li>Value Object 파라미터 (원시 타입 금지)</li>
 *   <li>Domain 반환 (DTO/Entity 반환 금지)</li>
 *   <li>Optional 반환 (단건 조회 시 null 방지)</li>
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
     * TenantId로 Organization 존재 여부 확인
     *
     * <p>Tenant 삭제 전 참조 무결성 검증에 사용
     *
     * @param tenantId Tenant ID (Value Object)
     * @return 해당 Tenant에 속한 Organization 존재 여부
     */
    boolean existsByTenantId(TenantId tenantId);
}
