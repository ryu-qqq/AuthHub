package com.ryuqq.authhub.domain.organization.exception;

import com.ryuqq.authhub.domain.common.exception.DomainException;
import com.ryuqq.authhub.domain.organization.vo.OrganizationName;
import com.ryuqq.authhub.domain.tenant.id.TenantId;
import java.util.Map;

/**
 * DuplicateOrganizationNameException - 동일 테넌트 내 조직 이름 중복 시 발생하는 예외
 *
 * @author development-team
 * @since 1.0.0
 */
public class DuplicateOrganizationNameException extends DomainException {

    public DuplicateOrganizationNameException(String tenantId, String organizationName) {
        super(
                OrganizationErrorCode.DUPLICATE_ORGANIZATION_NAME,
                Map.of("tenantId", tenantId, "organizationName", organizationName));
    }

    public DuplicateOrganizationNameException(TenantId tenantId, OrganizationName name) {
        this(tenantId.value(), name.value());
    }
}
