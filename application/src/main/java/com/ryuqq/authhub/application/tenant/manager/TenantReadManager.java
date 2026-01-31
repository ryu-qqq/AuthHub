package com.ryuqq.authhub.application.tenant.manager;

import com.ryuqq.authhub.application.tenant.port.out.query.TenantQueryPort;
import com.ryuqq.authhub.domain.tenant.aggregate.Tenant;
import com.ryuqq.authhub.domain.tenant.exception.TenantNotFoundException;
import com.ryuqq.authhub.domain.tenant.id.TenantId;
import com.ryuqq.authhub.domain.tenant.query.criteria.TenantSearchCriteria;
import com.ryuqq.authhub.domain.tenant.vo.TenantName;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * TenantReadManager - Tenant 조회 관리자
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
public class TenantReadManager {

    private final TenantQueryPort queryPort;

    public TenantReadManager(TenantQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    /**
     * ID로 Tenant 조회 (필수)
     *
     * @param id Tenant ID (VO)
     * @return Tenant Domain
     * @throws TenantNotFoundException 존재하지 않는 경우
     */
    @Transactional(readOnly = true)
    public Tenant findById(TenantId id) {
        return queryPort.findById(id).orElseThrow(() -> new TenantNotFoundException(id));
    }

    /**
     * ID로 Tenant 조회 (Optional)
     *
     * <p>존재하지 않는 경우 빈 Optional 반환합니다.
     *
     * @param id Tenant ID (VO)
     * @return Tenant Domain (Optional)
     */
    @Transactional(readOnly = true)
    public Optional<Tenant> findByIdOptional(TenantId id) {
        return queryPort.findById(id);
    }

    /**
     * ID로 Tenant 존재 여부 확인
     *
     * @param id Tenant ID (VO)
     * @return 존재 여부
     */
    @Transactional(readOnly = true)
    public boolean existsById(TenantId id) {
        return queryPort.existsById(id);
    }

    /**
     * 이름으로 Tenant 존재 여부 확인
     *
     * @param name Tenant 이름 (VO)
     * @return 존재 여부
     */
    @Transactional(readOnly = true)
    public boolean existsByName(TenantName name) {
        return queryPort.existsByName(name);
    }

    /**
     * 이름으로 Tenant 존재 여부 확인 (특정 ID 제외)
     *
     * <p>수정 시 자기 자신을 제외하고 중복 검증할 때 사용합니다.
     *
     * @param name Tenant 이름 (VO)
     * @param excludeId 제외할 Tenant ID (VO)
     * @return 존재 여부
     */
    @Transactional(readOnly = true)
    public boolean existsByNameAndIdNot(TenantName name, TenantId excludeId) {
        return queryPort.existsByNameAndIdNot(name, excludeId);
    }

    /**
     * 조건에 맞는 Tenant 목록 조회 (페이징)
     *
     * @param criteria 검색 조건 (TenantSearchCriteria)
     * @return Tenant Domain 목록
     */
    @Transactional(readOnly = true)
    public List<Tenant> findAllByCriteria(TenantSearchCriteria criteria) {
        return queryPort.findAllByCriteria(criteria);
    }

    /**
     * 조건에 맞는 Tenant 개수 조회
     *
     * @param criteria 검색 조건 (TenantSearchCriteria)
     * @return 조건에 맞는 Tenant 총 개수
     */
    @Transactional(readOnly = true)
    public long countByCriteria(TenantSearchCriteria criteria) {
        return queryPort.countByCriteria(criteria);
    }
}
