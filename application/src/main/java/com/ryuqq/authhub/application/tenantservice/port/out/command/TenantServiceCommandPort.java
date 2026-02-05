package com.ryuqq.authhub.application.tenantservice.port.out.command;

import com.ryuqq.authhub.domain.tenantservice.aggregate.TenantService;

/**
 * TenantServiceCommandPort - TenantService Aggregate 영속화 포트 (Command)
 *
 * <p>Domain Aggregate를 저장하는 명령 전용 Port입니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>persist() 메서드만 제공 (save/update/delete 분리 금지)
 *   <li>Domain Aggregate 파라미터 (Entity/DTO 금지)
 *   <li>ID 반환 (Long 원시 타입, Domain 객체 반환 금지)
 *   <li>조회 메서드 금지 (QueryPort로 분리)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface TenantServiceCommandPort {

    /**
     * TenantService Aggregate 영속화 (생성/수정 통합)
     *
     * @param tenantService TenantService Aggregate
     * @return 영속화된 TenantService ID (Long)
     */
    Long persist(TenantService tenantService);
}
