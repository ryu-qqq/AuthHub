package com.ryuqq.authhub.application.organization.port.out.command;

import com.ryuqq.authhub.domain.organization.aggregate.Organization;
import com.ryuqq.authhub.domain.organization.identifier.OrganizationId;

/**
 * OrganizationPersistencePort - Organization Aggregate 영속화 포트 (Command)
 *
 * <p>Domain Aggregate를 영속화하는 Command 전용 Port입니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>단일 persist() 메서드 (INSERT/UPDATE 자동 판단 - JPA merge 패턴)
 *   <li>save/update/delete 개별 메서드 금지
 *   <li>Domain 파라미터 (DTO/Entity 금지)
 *   <li>Value Object 반환 (OrganizationId)
 *   <li>조회 메서드 금지 (QueryPort로 분리)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface OrganizationPersistencePort {

    /**
     * Organization 영속화 (INSERT 또는 UPDATE)
     *
     * <p>Domain의 isNew() 상태에 따라 INSERT/UPDATE 자동 판단
     *
     * @param organization Organization Domain Aggregate
     * @return 영속화된 Organization의 ID (Value Object)
     */
    OrganizationId persist(Organization organization);
}
