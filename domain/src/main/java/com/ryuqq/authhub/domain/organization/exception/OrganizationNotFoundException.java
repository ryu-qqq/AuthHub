package com.ryuqq.authhub.domain.organization.exception;

import com.ryuqq.authhub.domain.common.exception.DomainException;
import com.ryuqq.authhub.domain.organization.identifier.OrganizationId;
import java.util.Map;
import java.util.UUID;

/**
 * OrganizationNotFoundException - 조직을 찾을 수 없을 때 발생하는 예외
 *
 * @author development-team
 * @since 1.0.0
 */
public class OrganizationNotFoundException extends DomainException {

    public OrganizationNotFoundException(UUID organizationId) {
        super(
                OrganizationErrorCode.ORGANIZATION_NOT_FOUND,
                Map.of("organizationId", organizationId));
    }

    public OrganizationNotFoundException(OrganizationId organizationId) {
        this(organizationId.value());
    }

    public OrganizationNotFoundException(UUID tenantId, String organizationName) {
        super(
                OrganizationErrorCode.ORGANIZATION_NOT_FOUND,
                Map.of("tenantId", tenantId, "organizationName", organizationName));
    }
}
