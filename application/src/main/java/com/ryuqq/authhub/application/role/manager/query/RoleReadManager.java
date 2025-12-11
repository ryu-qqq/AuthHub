package com.ryuqq.authhub.application.role.manager.query;

import com.ryuqq.authhub.application.role.dto.query.SearchRolesQuery;
import com.ryuqq.authhub.application.role.port.out.query.RoleQueryPort;
import com.ryuqq.authhub.domain.role.aggregate.Role;
import com.ryuqq.authhub.domain.role.exception.RoleNotFoundException;
import com.ryuqq.authhub.domain.role.identifier.RoleId;
import com.ryuqq.authhub.domain.role.vo.RoleName;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * RoleReadManager - 조회 전용 트랜잭션 관리
 *
 * <p>QueryPort에 대한 읽기 전용 트랜잭션을 관리합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code @Component} 어노테이션 ({@code @Service} 아님)
 *   <li>{@code @Transactional(readOnly = true)} 클래스 레벨
 *   <li>단일 QueryPort만 의존
 *   <li>비즈니스 로직 금지 (단순 조회 및 예외 변환만)
 *   <li>Lombok 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
@Transactional(readOnly = true)
public class RoleReadManager {

    private final RoleQueryPort queryPort;

    public RoleReadManager(RoleQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    /**
     * ID로 Role 조회 (없으면 예외)
     *
     * @param roleId 역할 ID
     * @return Role
     * @throws RoleNotFoundException 역할이 없는 경우
     */
    public Role getById(RoleId roleId) {
        return queryPort.findById(roleId).orElseThrow(() -> new RoleNotFoundException(roleId));
    }

    /**
     * 테넌트 내 역할 이름으로 Role 조회 (없으면 예외)
     *
     * @param tenantId 테넌트 ID (null일 경우 GLOBAL 범위)
     * @param name 역할 이름
     * @return Role
     * @throws RoleNotFoundException 역할이 없는 경우
     */
    public Role getByTenantIdAndName(TenantId tenantId, RoleName name) {
        return queryPort
                .findByTenantIdAndName(tenantId, name)
                .orElseThrow(() -> new RoleNotFoundException(name.value()));
    }

    /**
     * 테넌트 내 역할 이름 존재 여부 확인
     *
     * @param tenantId 테넌트 ID (null일 경우 GLOBAL 범위)
     * @param name 역할 이름
     * @return 존재 여부
     */
    public boolean existsByTenantIdAndName(TenantId tenantId, RoleName name) {
        return queryPort.existsByTenantIdAndName(tenantId, name);
    }

    /**
     * 역할 검색
     *
     * @param query 검색 조건
     * @return Role 목록
     */
    public List<Role> search(SearchRolesQuery query) {
        return queryPort.search(query);
    }

    /**
     * 역할 검색 총 개수
     *
     * @param query 검색 조건
     * @return 총 개수
     */
    public long count(SearchRolesQuery query) {
        return queryPort.count(query);
    }

    /**
     * 여러 ID로 역할 목록 조회
     *
     * @param roleIds 역할 ID Set
     * @return Role 목록
     */
    public List<Role> findAllByIds(Set<RoleId> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return List.of();
        }
        return queryPort.findAllByIds(roleIds);
    }
}
