package com.ryuqq.authhub.domain.organization.exception;

import com.ryuqq.authhub.domain.common.exception.DomainException;
import com.ryuqq.authhub.domain.organization.id.OrganizationId;
import java.util.Map;

/**
 * OrganizationNotFoundException - 조직을 찾을 수 없을 때 발생하는 예외
 *
 * @author development-team
 * @since 1.0.0
 */
public class OrganizationNotFoundException extends DomainException {

    public OrganizationNotFoundException(String organizationId) {
        super(
                OrganizationErrorCode.ORGANIZATION_NOT_FOUND,
                Map.of("organizationId", organizationId));
    }

    public OrganizationNotFoundException(OrganizationId organizationId) {
        this(organizationId.value());
    }

    public OrganizationNotFoundException(String tenantId, String organizationName) {
        super(
                OrganizationErrorCode.ORGANIZATION_NOT_FOUND,
                Map.of("tenantId", tenantId, "organizationName", organizationName));
    }
}
