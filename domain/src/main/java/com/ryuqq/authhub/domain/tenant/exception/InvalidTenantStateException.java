package com.ryuqq.authhub.domain.tenant.exception;

import com.ryuqq.authhub.domain.common.exception.DomainException;
import java.util.Map;

/**
 * InvalidTenantStateException - Tenant가 유효하지 않은 상태일 때 발생
 *
 * <p>Tenant가 비즈니스 규칙에 위배되는 상태이거나 허용되지 않는 작업을 시도할 때 발생하는 예외입니다.
 *
 * <p><strong>발생 시나리오:</strong>
 *
 * <ul>
 *   <li>삭제된 Tenant에 대한 작업 시도
 *   <li>비활성화된 Tenant 사용
 *   <li>만료된 Tenant 접근
 *   <li>상태 전환 규칙 위반
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public class InvalidTenantStateException extends DomainException {

    /**
     * Constructor - tenantId와 사유로 예외 생성
     *
     * @param tenantId 상태가 유효하지 않은 Tenant ID
     * @param reason 상태 유효성 위반 사유
     * @author development-team
     * @since 1.0.0
     */
    public InvalidTenantStateException(Long tenantId, String reason) {
        super(
                TenantErrorCode.INVALID_TENANT_STATUS.getCode(),
                TenantErrorCode.INVALID_TENANT_STATUS.getMessage(),
                Map.of(
                        "tenantId", tenantId,
                        "reason", reason));
    }
}
