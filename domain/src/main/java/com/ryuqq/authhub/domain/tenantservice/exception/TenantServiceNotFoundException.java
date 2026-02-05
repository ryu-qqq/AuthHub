package com.ryuqq.authhub.domain.tenantservice.exception;

import com.ryuqq.authhub.domain.common.exception.DomainException;
import com.ryuqq.authhub.domain.tenantservice.id.TenantServiceId;
import java.util.Map;

/**
 * TenantServiceNotFoundException - 테넌트-서비스 구독을 찾을 수 없을 때 발생하는 예외
 *
 * @author development-team
 * @since 1.0.0
 */
public class TenantServiceNotFoundException extends DomainException {

    public TenantServiceNotFoundException(TenantServiceId tenantServiceId) {
        super(
                TenantServiceErrorCode.TENANT_SERVICE_NOT_FOUND,
                Map.of("tenantServiceId", tenantServiceId.value()));
    }

    public TenantServiceNotFoundException(String tenantId, Long serviceId) {
        super(
                TenantServiceErrorCode.TENANT_SERVICE_NOT_FOUND,
                Map.of("tenantId", tenantId, "serviceId", serviceId));
    }
}
