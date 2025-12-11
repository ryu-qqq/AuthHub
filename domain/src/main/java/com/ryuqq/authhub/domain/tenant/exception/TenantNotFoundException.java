package com.ryuqq.authhub.domain.tenant.exception;

import com.ryuqq.authhub.domain.common.exception.DomainException;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import java.util.Map;
import java.util.UUID;

/**
 * TenantNotFoundException - 테넌트를 찾을 수 없을 때 발생하는 예외
 *
 * @author development-team
 * @since 1.0.0
 */
public class TenantNotFoundException extends DomainException {

    public TenantNotFoundException(TenantId tenantId) {
        super(TenantErrorCode.TENANT_NOT_FOUND, Map.of("tenantId", tenantId.value()));
    }

    public TenantNotFoundException(UUID tenantId) {
        super(TenantErrorCode.TENANT_NOT_FOUND, Map.of("tenantId", tenantId));
    }

    public TenantNotFoundException(String identifier) {
        super(TenantErrorCode.TENANT_NOT_FOUND, Map.of("identifier", identifier));
    }
}
