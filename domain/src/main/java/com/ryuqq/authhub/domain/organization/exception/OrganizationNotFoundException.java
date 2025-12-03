package com.ryuqq.authhub.domain.organization.exception;

import com.ryuqq.authhub.domain.common.exception.DomainException;
import java.util.Map;

/**
 * OrganizationNotFoundException - Organization을 찾을 수 없는 예외
 *
 * <p>요청한 Organization이 존재하지 않을 때 발생합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public class OrganizationNotFoundException extends DomainException {

    public OrganizationNotFoundException(Long organizationId) {
        super(
                OrganizationErrorCode.ORGANIZATION_NOT_FOUND,
                Map.of("organizationId", organizationId));
    }

    public OrganizationNotFoundException(String identifier) {
        super(OrganizationErrorCode.ORGANIZATION_NOT_FOUND, Map.of("identifier", identifier));
    }
}
