package com.ryuqq.authhub.application.service.manager;

import com.ryuqq.authhub.application.service.port.out.query.ServiceQueryPort;
import com.ryuqq.authhub.domain.service.aggregate.Service;
import com.ryuqq.authhub.domain.service.exception.ServiceNotFoundException;
import com.ryuqq.authhub.domain.service.id.ServiceId;
import com.ryuqq.authhub.domain.service.query.criteria.ServiceSearchCriteria;
import com.ryuqq.authhub.domain.service.vo.ServiceCode;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * ServiceReadManager - Service 조회 관리자
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
public class ServiceReadManager {

    private final ServiceQueryPort queryPort;

    public ServiceReadManager(ServiceQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    /**
     * ID로 Service 조회 (필수)
     *
     * @param id Service ID (VO)
     * @return Service Domain
     * @throws ServiceNotFoundException 존재하지 않는 경우
     */
    @Transactional(readOnly = true)
    public Service findById(ServiceId id) {
        return queryPort.findById(id).orElseThrow(() -> new ServiceNotFoundException(id));
    }

    /**
     * ID로 Service 조회 (Optional)
     *
     * <p>존재하지 않는 경우 빈 Optional 반환합니다.
     *
     * @param id Service ID (VO)
     * @return Service Domain (Optional)
     */
    @Transactional(readOnly = true)
    public Optional<Service> findByIdOptional(ServiceId id) {
        return queryPort.findById(id);
    }

    /**
     * ID로 Service 존재 여부 확인
     *
     * @param id Service ID (VO)
     * @return 존재 여부
     */
    @Transactional(readOnly = true)
    public boolean existsById(ServiceId id) {
        return queryPort.existsById(id);
    }

    /**
     * ServiceCode로 Service 존재 여부 확인
     *
     * @param serviceCode Service Code (VO)
     * @return 존재 여부
     */
    @Transactional(readOnly = true)
    public boolean existsByCode(ServiceCode serviceCode) {
        return queryPort.existsByCode(serviceCode);
    }

    /**
     * 조건에 맞는 Service 목록 조회 (페이징)
     *
     * @param criteria 검색 조건 (ServiceSearchCriteria)
     * @return Service Domain 목록
     */
    @Transactional(readOnly = true)
    public List<Service> findAllByCriteria(ServiceSearchCriteria criteria) {
        return queryPort.findAllByCriteria(criteria);
    }

    /**
     * 조건에 맞는 Service 개수 조회
     *
     * @param criteria 검색 조건 (ServiceSearchCriteria)
     * @return 조건에 맞는 Service 총 개수
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ServiceSearchCriteria criteria) {
        return queryPort.countByCriteria(criteria);
    }

    /**
     * 모든 활성 Service 목록 조회
     *
     * @return 활성 상태의 Service Domain 목록
     */
    @Transactional(readOnly = true)
    public List<Service> findAllActive() {
        return queryPort.findAllActive();
    }
}
