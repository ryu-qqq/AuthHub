package com.ryuqq.authhub.application.role.port.out.query;

import com.ryuqq.authhub.domain.role.aggregate.Role;
import com.ryuqq.authhub.domain.role.id.RoleId;
import com.ryuqq.authhub.domain.role.query.criteria.RoleSearchCriteria;
import com.ryuqq.authhub.domain.role.vo.RoleName;
import com.ryuqq.authhub.domain.service.id.ServiceId;
import com.ryuqq.authhub.domain.tenant.id.TenantId;
import java.util.List;
import java.util.Optional;

/**
 * RoleQueryPort - Role Aggregate 조회 포트 (Query)
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
 *   <li>Criteria 기반 조회 (개별 파라미터 금지)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface RoleQueryPort {

    /**
     * ID로 Role 단건 조회
     *
     * @param id Role ID (Value Object)
     * @return Role Domain (Optional)
     */
    Optional<Role> findById(RoleId id);

    /**
     * ID로 Role 존재 여부 확인
     *
     * @param id Role ID (Value Object)
     * @return 존재 여부
     */
    boolean existsById(RoleId id);

    /**
     * 테넌트 + 서비스 범위 내 역할 이름으로 존재 여부 확인
     *
     * @param tenantId 테넌트 ID (null이면 Global)
     * @param serviceId 서비스 ID (null이면 서비스 무관)
     * @param name 역할 이름
     * @return 존재 여부
     */
    boolean existsByTenantIdAndServiceIdAndName(
            TenantId tenantId, ServiceId serviceId, RoleName name);

    /**
     * 테넌트 + 서비스 범위 내 역할 이름으로 Role 조회
     *
     * @param tenantId 테넌트 ID (null이면 Global)
     * @param serviceId 서비스 ID (null이면 서비스 무관)
     * @param name 역할 이름
     * @return Role Domain (Optional)
     */
    Optional<Role> findByTenantIdAndServiceIdAndName(
            TenantId tenantId, ServiceId serviceId, RoleName name);

    /**
     * 조건에 맞는 역할 목록 조회 (SearchCriteria 기반)
     *
     * @param criteria 검색 조건 (RoleSearchCriteria)
     * @return Role Domain 목록
     */
    List<Role> findAllBySearchCriteria(RoleSearchCriteria criteria);

    /**
     * 조건에 맞는 역할 개수 조회 (SearchCriteria 기반)
     *
     * @param criteria 검색 조건 (RoleSearchCriteria)
     * @return 조건에 맞는 Role 총 개수
     */
    long countBySearchCriteria(RoleSearchCriteria criteria);

    /**
     * ID 목록으로 Role 다건 조회
     *
     * @param ids Role ID 목록
     * @return Role Domain 목록
     */
    List<Role> findAllByIds(List<RoleId> ids);
}
