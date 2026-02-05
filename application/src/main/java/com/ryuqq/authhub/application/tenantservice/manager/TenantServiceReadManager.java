package com.ryuqq.authhub.application.tenantservice.manager;

import com.ryuqq.authhub.application.tenantservice.port.out.query.TenantServiceQueryPort;
import com.ryuqq.authhub.domain.service.id.ServiceId;
import com.ryuqq.authhub.domain.tenant.id.TenantId;
import com.ryuqq.authhub.domain.tenantservice.aggregate.TenantService;
import com.ryuqq.authhub.domain.tenantservice.exception.TenantServiceNotFoundException;
import com.ryuqq.authhub.domain.tenantservice.id.TenantServiceId;
import com.ryuqq.authhub.domain.tenantservice.query.criteria.TenantServiceSearchCriteria;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * TenantServiceReadManager - TenantService 조회 관리자
 *
 * <p>QueryPort를 래핑하여 조회 작업을 관리합니다.
 *
 * <p>C-004: @Transactional(readOnly=true)는 Manager에서 메서드 단위로 사용합니다.
 *
 * <p>C-005: Port를 직접 노출하지 않고 Manager로 래핑합니다.
 *
 * <p>MGR-001: 파라미터는 원시타입 대신 VO를 사용합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class TenantServiceReadManager {

    private final TenantServiceQueryPort queryPort;

    public TenantServiceReadManager(TenantServiceQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    /**
     * ID로 TenantService 조회 (필수)
     *
     * @param id TenantService ID (VO)
     * @return TenantService Domain
     * @throws TenantServiceNotFoundException 존재하지 않는 경우
     */
    @Transactional(readOnly = true)
    public TenantService findById(TenantServiceId id) {
        return queryPort.findById(id).orElseThrow(() -> new TenantServiceNotFoundException(id));
    }

    /**
     * ID로 TenantService 조회 (Optional)
     *
     * @param id TenantService ID (VO)
     * @return TenantService Domain (Optional)
     */
    @Transactional(readOnly = true)
    public Optional<TenantService> findByIdOptional(TenantServiceId id) {
        return queryPort.findById(id);
    }

    /**
     * ID로 TenantService 존재 여부 확인
     *
     * @param id TenantService ID (VO)
     * @return 존재 여부
     */
    @Transactional(readOnly = true)
    public boolean existsById(TenantServiceId id) {
        return queryPort.existsById(id);
    }

    /**
     * 테넌트 ID + 서비스 ID로 구독 존재 여부 확인
     *
     * @param tenantId 테넌트 ID (VO)
     * @param serviceId 서비스 ID (VO)
     * @return 존재 여부
     */
    @Transactional(readOnly = true)
    public boolean existsByTenantIdAndServiceId(TenantId tenantId, ServiceId serviceId) {
        return queryPort.existsByTenantIdAndServiceId(tenantId, serviceId);
    }

    /**
     * 테넌트 ID + 서비스 ID로 TenantService 조회
     *
     * @param tenantId 테넌트 ID (VO)
     * @param serviceId 서비스 ID (VO)
     * @return TenantService Domain (Optional)
     */
    @Transactional(readOnly = true)
    public Optional<TenantService> findByTenantIdAndServiceId(
            TenantId tenantId, ServiceId serviceId) {
        return queryPort.findByTenantIdAndServiceId(tenantId, serviceId);
    }

    /**
     * 조건에 맞는 TenantService 목록 조회 (페이징)
     *
     * @param criteria 검색 조건
     * @return TenantService Domain 목록
     */
    @Transactional(readOnly = true)
    public List<TenantService> findAllByCriteria(TenantServiceSearchCriteria criteria) {
        return queryPort.findAllByCriteria(criteria);
    }

    /**
     * 조건에 맞는 TenantService 개수 조회
     *
     * @param criteria 검색 조건
     * @return 조건에 맞는 TenantService 총 개수
     */
    @Transactional(readOnly = true)
    public long countByCriteria(TenantServiceSearchCriteria criteria) {
        return queryPort.countByCriteria(criteria);
    }
}
