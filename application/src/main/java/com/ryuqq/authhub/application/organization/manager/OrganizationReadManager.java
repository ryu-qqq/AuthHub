package com.ryuqq.authhub.application.organization.manager;

import com.ryuqq.authhub.application.organization.port.out.query.OrganizationQueryPort;
import com.ryuqq.authhub.domain.organization.aggregate.Organization;
import com.ryuqq.authhub.domain.organization.exception.OrganizationNotFoundException;
import com.ryuqq.authhub.domain.organization.id.OrganizationId;
import com.ryuqq.authhub.domain.organization.query.criteria.OrganizationSearchCriteria;
import com.ryuqq.authhub.domain.organization.vo.OrganizationName;
import com.ryuqq.authhub.domain.tenant.id.TenantId;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * OrganizationReadManager - 단일 Port 조회 관리
 *
 * <p>단일 QueryPort에 대한 조회를 관리합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code @Component} 어노테이션 ({@code @Service} 아님)
 *   <li>{@code @Transactional(readOnly = true)} 메서드 단위
 *   <li>{@code find*()} 메서드 네이밍
 *   <li>단일 QueryPort만 의존
 *   <li>비즈니스 로직 금지
 *   <li>Lombok 금지
 *   <li>Criteria 기반 조회
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class OrganizationReadManager {

    private final OrganizationQueryPort queryPort;

    public OrganizationReadManager(OrganizationQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    /**
     * ID로 Organization 조회 (필수)
     *
     * @param id Organization ID
     * @return Organization Domain
     * @throws OrganizationNotFoundException 존재하지 않는 경우
     */
    @Transactional(readOnly = true)
    public Organization findById(OrganizationId id) {
        return queryPort.findById(id).orElseThrow(() -> new OrganizationNotFoundException(id));
    }

    /**
     * ID로 Organization 존재 여부 확인
     *
     * @param id Organization ID
     * @return 존재 여부
     */
    @Transactional(readOnly = true)
    public boolean existsById(OrganizationId id) {
        return queryPort.existsById(id);
    }

    /**
     * 테넌트 내 이름 중복 확인
     *
     * @param tenantId Tenant ID
     * @param name Organization 이름
     * @return 존재 여부
     */
    @Transactional(readOnly = true)
    public boolean existsByTenantIdAndName(TenantId tenantId, OrganizationName name) {
        return queryPort.existsByTenantIdAndName(tenantId, name);
    }

    /**
     * 조건에 맞는 조직 목록 조회 (SearchCriteria 기반)
     *
     * @param criteria 검색 조건 (OrganizationSearchCriteria)
     * @return Organization Domain 목록
     */
    @Transactional(readOnly = true)
    public List<Organization> findAllBySearchCriteria(OrganizationSearchCriteria criteria) {
        return queryPort.findAllBySearchCriteria(criteria);
    }

    /**
     * 조건에 맞는 조직 개수 조회 (SearchCriteria 기반)
     *
     * @param criteria 검색 조건 (OrganizationSearchCriteria)
     * @return 조건에 맞는 Organization 총 개수
     */
    @Transactional(readOnly = true)
    public long countBySearchCriteria(OrganizationSearchCriteria criteria) {
        return queryPort.countBySearchCriteria(criteria);
    }
}
