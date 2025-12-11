package com.ryuqq.authhub.application.role.port.out.query;

import com.ryuqq.authhub.application.role.dto.query.SearchRolesQuery;
import com.ryuqq.authhub.domain.role.aggregate.Role;
import com.ryuqq.authhub.domain.role.identifier.RoleId;
import com.ryuqq.authhub.domain.role.vo.RoleName;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import java.util.List;
import java.util.Optional;

/**
 * RoleQueryPort - 역할 조회 Port (Port-Out)
 *
 * <p>역할 조회 기능을 정의합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code {Bc}QueryPort} 네이밍
 *   <li>Domain Aggregate/VO 파라미터/반환
 *   <li>구현은 Adapter 책임
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface RoleQueryPort {

    /**
     * ID로 Role 조회
     *
     * @param roleId 역할 ID
     * @return Optional Role
     */
    Optional<Role> findById(RoleId roleId);

    /**
     * 테넌트 내 역할 이름으로 Role 조회
     *
     * @param tenantId 테넌트 ID (null일 경우 GLOBAL 범위)
     * @param name 역할 이름
     * @return Optional Role
     */
    Optional<Role> findByTenantIdAndName(TenantId tenantId, RoleName name);

    /**
     * 테넌트 내 역할 이름 존재 여부 확인
     *
     * @param tenantId 테넌트 ID (null일 경우 GLOBAL 범위)
     * @param name 역할 이름
     * @return 존재 여부
     */
    boolean existsByTenantIdAndName(TenantId tenantId, RoleName name);

    /**
     * 역할 검색
     *
     * @param query 검색 조건
     * @return Role 목록
     */
    List<Role> search(SearchRolesQuery query);

    /**
     * 역할 검색 총 개수
     *
     * @param query 검색 조건
     * @return 총 개수
     */
    long count(SearchRolesQuery query);

    /**
     * 여러 ID로 역할 목록 조회
     *
     * @param roleIds 역할 ID Set
     * @return Role 목록
     */
    List<Role> findAllByIds(java.util.Set<RoleId> roleIds);
}
