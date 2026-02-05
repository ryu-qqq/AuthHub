package com.ryuqq.authhub.application.role.manager;

import com.ryuqq.authhub.application.role.port.out.query.RoleQueryPort;
import com.ryuqq.authhub.domain.role.aggregate.Role;
import com.ryuqq.authhub.domain.role.exception.RoleNotFoundException;
import com.ryuqq.authhub.domain.role.id.RoleId;
import com.ryuqq.authhub.domain.role.query.criteria.RoleSearchCriteria;
import com.ryuqq.authhub.domain.role.vo.RoleName;
import com.ryuqq.authhub.domain.service.id.ServiceId;
import com.ryuqq.authhub.domain.tenant.id.TenantId;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * RoleReadManager - 단일 Port 조회 관리
 *
 * <p>QueryPort에 대한 조회를 관리합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code @Component} 어노테이션 ({@code @Service} 아님)
 *   <li>{@code @Transactional(readOnly = true)} 메서드 단위
 *   <li>{@code find*()} 메서드 네이밍
 *   <li>QueryPort만 의존
 *   <li>비즈니스 로직 금지
 *   <li>Lombok 금지
 *   <li>Criteria 기반 조회
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class RoleReadManager {

    private final RoleQueryPort queryPort;

    public RoleReadManager(RoleQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    /**
     * ID로 Role 조회 (필수)
     *
     * @param id Role ID
     * @return Role Domain
     * @throws RoleNotFoundException 존재하지 않는 경우
     */
    @Transactional(readOnly = true)
    public Role findById(RoleId id) {
        return queryPort.findById(id).orElseThrow(() -> new RoleNotFoundException(id));
    }

    /**
     * ID로 Role 존재 여부 확인
     *
     * @param id Role ID
     * @return 존재 여부
     */
    @Transactional(readOnly = true)
    public boolean existsById(RoleId id) {
        return queryPort.existsById(id);
    }

    /**
     * 테넌트 + 서비스 범위 내 역할 이름으로 존재 여부 확인
     *
     * @param tenantId 테넌트 ID (null이면 Global)
     * @param serviceId 서비스 ID (null이면 서비스 무관)
     * @param name 역할 이름
     * @return 존재 여부
     */
    @Transactional(readOnly = true)
    public boolean existsByTenantIdAndServiceIdAndName(
            TenantId tenantId, ServiceId serviceId, RoleName name) {
        return queryPort.existsByTenantIdAndServiceIdAndName(tenantId, serviceId, name);
    }

    /**
     * 테넌트 + 서비스 범위 내 역할 이름으로 Role 조회 (필수)
     *
     * @param tenantId 테넌트 ID (null이면 Global)
     * @param serviceId 서비스 ID (null이면 서비스 무관)
     * @param name 역할 이름
     * @return Role Domain
     * @throws RoleNotFoundException 존재하지 않는 경우
     */
    @Transactional(readOnly = true)
    public Role findByTenantIdAndServiceIdAndName(
            TenantId tenantId, ServiceId serviceId, RoleName name) {
        return queryPort
                .findByTenantIdAndServiceIdAndName(tenantId, serviceId, name)
                .orElseThrow(() -> new RoleNotFoundException(name));
    }

    /**
     * 테넌트 + 서비스 범위 내 역할 이름으로 Role 조회 (Optional)
     *
     * <p>Role이 존재하지 않아도 예외를 던지지 않으므로 트랜잭션 rollback-only 마킹이 발생하지 않습니다.
     *
     * @param tenantId 테넌트 ID (null이면 Global)
     * @param serviceId 서비스 ID (null이면 서비스 무관)
     * @param name 역할 이름
     * @return Role Domain (Optional)
     */
    @Transactional(readOnly = true)
    public Optional<Role> findOptionalByTenantIdAndServiceIdAndName(
            TenantId tenantId, ServiceId serviceId, RoleName name) {
        return queryPort.findByTenantIdAndServiceIdAndName(tenantId, serviceId, name);
    }

    /**
     * 조건에 맞는 역할 목록 조회 (SearchCriteria 기반)
     *
     * @param criteria 검색 조건 (RoleSearchCriteria)
     * @return Role Domain 목록
     */
    @Transactional(readOnly = true)
    public List<Role> findAllBySearchCriteria(RoleSearchCriteria criteria) {
        return queryPort.findAllBySearchCriteria(criteria);
    }

    /**
     * 조건에 맞는 역할 개수 조회 (SearchCriteria 기반)
     *
     * @param criteria 검색 조건 (RoleSearchCriteria)
     * @return 조건에 맞는 Role 총 개수
     */
    @Transactional(readOnly = true)
    public long countBySearchCriteria(RoleSearchCriteria criteria) {
        return queryPort.countBySearchCriteria(criteria);
    }

    /**
     * ID 목록으로 Role 다건 조회 (IN절 조회)
     *
     * @param ids Role ID 목록
     * @return Role Domain 목록
     */
    @Transactional(readOnly = true)
    public List<Role> findAllByIds(List<RoleId> ids) {
        return queryPort.findAllByIds(ids);
    }
}
