package com.ryuqq.authhub.domain.tenant.exception;

import com.ryuqq.authhub.domain.common.exception.DomainException;
import com.ryuqq.authhub.domain.tenant.vo.TenantStatus;
import java.util.Map;

/**
 * InvalidTenantStateException - 유효하지 않은 Tenant 상태 예외
 *
 * <p>Tenant 상태 전이가 유효하지 않을 때 발생하는 예외입니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public class InvalidTenantStateException extends DomainException {

    public InvalidTenantStateException(TenantStatus currentStatus, TenantStatus targetStatus) {
        super(
                TenantErrorCode.INVALID_TENANT_STATUS,
                Map.of(
                        "currentStatus", currentStatus.name(),
                        "targetStatus", targetStatus.name()));
    }

    public InvalidTenantStateException(Long tenantId, String reason) {
        super(
                TenantErrorCode.INVALID_TENANT_STATUS,
                Map.of(
                        "tenantId", tenantId,
                        "reason", reason));
    }

    public InvalidTenantStateException(String message) {
        super(TenantErrorCode.INVALID_TENANT_STATUS.getCode(), message);
    }
}
