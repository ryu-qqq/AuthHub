package com.ryuqq.authhub.application.user.component;

import com.ryuqq.authhub.application.tenant.port.out.query.TenantQueryPort;
import com.ryuqq.authhub.domain.tenant.aggregate.Tenant;
import com.ryuqq.authhub.domain.tenant.exception.InvalidTenantStateException;
import com.ryuqq.authhub.domain.tenant.exception.TenantNotFoundException;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import org.springframework.stereotype.Component;

/**
 * TenantValidator - Tenant 검증 컴포넌트
 *
 * <p>Tenant의 존재 여부 및 상태를 검증합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class TenantValidator {

    private final TenantQueryPort tenantQueryPort;

    public TenantValidator(TenantQueryPort tenantQueryPort) {
        this.tenantQueryPort = tenantQueryPort;
    }

    /**
     * Tenant 검증 (존재 + 활성 상태)
     *
     * @param tenantId 검증할 TenantId
     * @return 검증된 Tenant
     * @throws TenantNotFoundException Tenant가 존재하지 않는 경우
     * @throws InvalidTenantStateException Tenant가 비활성 상태인 경우
     */
    public Tenant validate(TenantId tenantId) {
        Tenant tenant =
                tenantQueryPort
                        .findById(tenantId)
                        .orElseThrow(() -> new TenantNotFoundException(tenantId.value()));

        if (!tenant.isActive()) {
            throw new InvalidTenantStateException(tenantId.value(), "Tenant가 비활성 상태입니다");
        }

        return tenant;
    }
}
