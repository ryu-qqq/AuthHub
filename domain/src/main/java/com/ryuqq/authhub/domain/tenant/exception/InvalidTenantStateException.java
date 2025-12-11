package com.ryuqq.authhub.domain.tenant.exception;

import com.ryuqq.authhub.domain.common.exception.DomainException;
import com.ryuqq.authhub.domain.tenant.vo.TenantStatus;
import java.util.Map;

/**
 * InvalidTenantStateException - 유효하지 않은 테넌트 상태 전이 시 발생하는 예외
 *
 * @author development-team
 * @since 1.0.0
 */
public class InvalidTenantStateException extends DomainException {

    public InvalidTenantStateException(TenantStatus currentStatus, TenantStatus targetStatus) {
        super(
                TenantErrorCode.INVALID_TENANT_STATE,
                Map.of(
                        "currentStatus", currentStatus.name(),
                        "targetStatus", targetStatus.name()));
    }

    public InvalidTenantStateException(TenantStatus currentStatus, String reason) {
        super(
                TenantErrorCode.INVALID_TENANT_STATE,
                Map.of("currentStatus", currentStatus.name(), "reason", reason));
    }
}
