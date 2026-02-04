package com.ryuqq.authhub.application.tenantservice.validator;

import com.ryuqq.authhub.application.tenantservice.manager.TenantServiceReadManager;
import com.ryuqq.authhub.domain.service.id.ServiceId;
import com.ryuqq.authhub.domain.tenant.id.TenantId;
import com.ryuqq.authhub.domain.tenantservice.aggregate.TenantService;
import com.ryuqq.authhub.domain.tenantservice.exception.DuplicateTenantServiceException;
import com.ryuqq.authhub.domain.tenantservice.exception.TenantServiceNotFoundException;
import com.ryuqq.authhub.domain.tenantservice.id.TenantServiceId;
import org.springframework.stereotype.Component;

/**
 * TenantServiceValidator - 테넌트-서비스 구독 비즈니스 규칙 검증
 *
 * <p>VAL-001: Validator는 @Component 어노테이션 사용.
 *
 * <p>VAL-002: Validator는 {Domain}Validator 네이밍 사용.
 *
 * <p>VAL-003: Validator는 ReadManager만 의존.
 *
 * <p>VAL-004: Validator는 void 반환, 실패 시 DomainException.
 *
 * <p>VAL-005: Validator 메서드는 validateXxx() 또는 checkXxx() 사용.
 *
 * <p>MGR-001: 파라미터는 원시타입 대신 VO를 사용합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class TenantServiceValidator {

    private final TenantServiceReadManager readManager;

    public TenantServiceValidator(TenantServiceReadManager readManager) {
        this.readManager = readManager;
    }

    /**
     * TenantService 존재 여부 검증
     *
     * @param id TenantService ID (VO)
     * @throws TenantServiceNotFoundException 존재하지 않는 경우
     */
    public void validateExists(TenantServiceId id) {
        if (!readManager.existsById(id)) {
            throw new TenantServiceNotFoundException(id);
        }
    }

    /**
     * TenantService 조회 및 존재 여부 검증
     *
     * <p>APP-VAL-001: 검증 성공 시 조회한 Domain 객체를 반환합니다.
     *
     * @param id TenantService ID (VO)
     * @return TenantService 조회된 도메인 객체
     * @throws TenantServiceNotFoundException 존재하지 않는 경우
     */
    public TenantService findExistingOrThrow(TenantServiceId id) {
        return readManager.findById(id);
    }

    /**
     * 테넌트-서비스 구독 중복 검증
     *
     * @param tenantId 테넌트 ID (VO)
     * @param serviceId 서비스 ID (VO)
     * @throws DuplicateTenantServiceException 이미 구독 중인 경우
     */
    public void validateNotDuplicated(TenantId tenantId, ServiceId serviceId) {
        if (readManager.existsByTenantIdAndServiceId(tenantId, serviceId)) {
            throw new DuplicateTenantServiceException(tenantId.value(), serviceId.value());
        }
    }
}
