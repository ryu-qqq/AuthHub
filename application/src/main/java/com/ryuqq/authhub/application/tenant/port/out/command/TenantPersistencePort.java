package com.ryuqq.authhub.application.tenant.port.out.command;

import com.ryuqq.authhub.domain.tenant.aggregate.Tenant;

/**
 * TenantPersistencePort - Tenant Aggregate 영속화 포트 (Command)
 *
 * <p>Domain Aggregate를 저장하는 명령 전용 Port입니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>persist() 메서드만 제공 (save/update/delete 분리 금지)
 *   <li>Domain Aggregate 파라미터 (Entity/DTO 금지)
 *   <li>Value Object 반환 (원시 타입 금지)
 *   <li>조회 메서드 금지 (QueryPort로 분리)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface TenantPersistencePort {

    /**
     * Tenant Aggregate 영속화 (생성/수정/삭제 통합)
     *
     * <p>Domain의 상태에 따라 적절한 영속화 작업을 수행합니다.
     *
     * <ul>
     *   <li>isNew() == true → INSERT
     *   <li>isNew() == false → UPDATE
     *   <li>isDeleted() == true → Soft DELETE (상태 변경)
     * </ul>
     *
     * @param tenant Tenant Aggregate
     * @return 영속화된 Tenant (ID 할당됨)
     */
    Tenant persist(Tenant tenant);
}
