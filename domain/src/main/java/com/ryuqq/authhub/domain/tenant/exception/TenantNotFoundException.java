package com.ryuqq.authhub.domain.tenant.exception;

import com.ryuqq.authhub.domain.common.exception.DomainException;

import java.util.Map;

/**
 * TenantNotFoundException - Tenant를 찾을 수 없을 때 발생
 *
 * <p>특정 ID의 Tenant가 존재하지 않거나 조회할 수 없을 때 발생하는 예외입니다.
 *
 * <p><strong>발생 시나리오:</strong>
 * <ul>
 *   <li>존재하지 않는 Tenant ID로 조회</li>
 *   <li>삭제된 Tenant 조회</li>
 *   <li>권한 없는 Tenant 접근</li>
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public class TenantNotFoundException extends DomainException {

    /**
     * Constructor - tenantId로 예외 생성
     *
     * @param tenantId 조회 시도한 Tenant ID
     * @author development-team
     * @since 1.0.0
     */
    public TenantNotFoundException(Long tenantId) {
        super(
                TenantErrorCode.TENANT_NOT_FOUND.getCode(),
                TenantErrorCode.TENANT_NOT_FOUND.getMessage(),
                Map.of("tenantId", tenantId)
        );
    }
}
