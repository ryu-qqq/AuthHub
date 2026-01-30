package com.ryuqq.authhub.application.organization.port.out.command;

import com.ryuqq.authhub.domain.organization.aggregate.Organization;

/**
 * OrganizationPersistencePort - Organization Aggregate 영속화 포트 (Command)
 *
 * <p>Domain Aggregate를 저장하는 명령 전용 Port입니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>persist() 메서드만 제공 (save/update/delete 분리 금지)
 *   <li>Domain Aggregate 파라미터 (Entity/DTO 금지)
 *   <li>ID 반환 (String 원시 타입, Domain 객체 반환 금지)
 *   <li>조회 메서드 금지 (QueryPort로 분리)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface OrganizationCommandPort {

    /**
     * Organization Aggregate 영속화 (생성/수정/삭제 통합)
     *
     * <p>Domain의 상태에 따라 적절한 영속화 작업을 수행합니다.
     *
     * <ul>
     *   <li>isNew() == true → INSERT
     *   <li>isNew() == false → UPDATE
     *   <li>isDeleted() == true → Soft DELETE (상태 변경)
     * </ul>
     *
     * @param organization Organization Aggregate
     * @return 영속화된 Organization ID (String)
     */
    String persist(Organization organization);
}
