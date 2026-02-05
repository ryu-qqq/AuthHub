package com.ryuqq.authhub.domain.tenantservice.exception;

import com.ryuqq.authhub.domain.common.exception.DomainException;
import java.util.Map;

/**
 * DuplicateTenantServiceException - 이미 구독 중인 테넌트-서비스일 때 발생하는 예외
 *
 * @author development-team
 * @since 1.0.0
 */
public class DuplicateTenantServiceException extends DomainException {

    public DuplicateTenantServiceException(String tenantId, Long serviceId) {
        super(
                TenantServiceErrorCode.DUPLICATE_TENANT_SERVICE,
                Map.of("tenantId", tenantId, "serviceId", serviceId));
    }
}
