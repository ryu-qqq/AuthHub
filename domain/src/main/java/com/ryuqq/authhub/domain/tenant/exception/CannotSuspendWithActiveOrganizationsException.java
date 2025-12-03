package com.ryuqq.authhub.domain.tenant.exception;

import com.ryuqq.authhub.domain.common.exception.DomainException;
import java.util.Map;

/**
 * CannotSuspendWithActiveOrganizationsException - Tenant 정지 불가 예외
 *
 * <p>Tenant 정지 시 활성 상태의 Organization이 존재하는 경우 발생합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public class CannotSuspendWithActiveOrganizationsException extends DomainException {

    public CannotSuspendWithActiveOrganizationsException(Long tenantId) {
        super(TenantErrorCode.ACTIVE_ORGANIZATIONS_EXIST, Map.of("tenantId", tenantId));
    }
}
