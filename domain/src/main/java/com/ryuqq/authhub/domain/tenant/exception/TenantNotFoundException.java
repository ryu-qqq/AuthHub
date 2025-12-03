package com.ryuqq.authhub.domain.tenant.exception;

import com.ryuqq.authhub.domain.common.exception.DomainException;
import java.util.Map;

/**
 * TenantNotFoundException - Tenant를 찾을 수 없는 예외
 *
 * <p>요청한 Tenant가 존재하지 않을 때 발생합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public class TenantNotFoundException extends DomainException {

    public TenantNotFoundException(Long tenantId) {
        super(TenantErrorCode.TENANT_NOT_FOUND, Map.of("tenantId", tenantId));
    }

    public TenantNotFoundException(String identifier) {
        super(TenantErrorCode.TENANT_NOT_FOUND, Map.of("identifier", identifier));
    }
}
