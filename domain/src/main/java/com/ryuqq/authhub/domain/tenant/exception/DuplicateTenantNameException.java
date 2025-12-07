package com.ryuqq.authhub.domain.tenant.exception;

import com.ryuqq.authhub.domain.common.exception.DomainException;
import com.ryuqq.authhub.domain.tenant.vo.TenantName;
import java.util.Map;

/**
 * DuplicateTenantNameException - 중복된 테넌트 이름 시 발생하는 예외
 *
 * @author development-team
 * @since 1.0.0
 */
public class DuplicateTenantNameException extends DomainException {

    public DuplicateTenantNameException(TenantName tenantName) {
        super(TenantErrorCode.DUPLICATE_TENANT_NAME, Map.of("name", tenantName.value()));
    }

    public DuplicateTenantNameException(String name) {
        super(TenantErrorCode.DUPLICATE_TENANT_NAME, Map.of("name", name));
    }
}
