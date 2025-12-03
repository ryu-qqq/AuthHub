package com.ryuqq.authhub.application.tenant.port.out.query;

import com.ryuqq.authhub.domain.tenant.aggregate.Tenant;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
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
}
