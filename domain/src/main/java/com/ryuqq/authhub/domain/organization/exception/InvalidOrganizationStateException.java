package com.ryuqq.authhub.domain.organization.exception;

import com.ryuqq.authhub.domain.common.exception.DomainException;
import com.ryuqq.authhub.domain.organization.vo.OrganizationStatus;

import java.util.Map;

/**
 * InvalidOrganizationStateException - 유효하지 않은 Organization 상태 예외
 *
 * <p>Organization 상태 전이가 유효하지 않을 때 발생하는 예외입니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public class InvalidOrganizationStateException extends DomainException {

    public InvalidOrganizationStateException(OrganizationStatus currentStatus, OrganizationStatus targetStatus) {
        super(
                OrganizationErrorCode.INVALID_ORGANIZATION_STATUS,
                Map.of(
                        "currentStatus", currentStatus.name(),
                        "targetStatus", targetStatus.name()
                )
        );
    }

    public InvalidOrganizationStateException(Long organizationId, String reason) {
        super(
                OrganizationErrorCode.INVALID_ORGANIZATION_STATUS,
                Map.of(
                        "organizationId", organizationId,
                        "reason", reason
                )
        );
    }

    public InvalidOrganizationStateException(String message) {
        super(OrganizationErrorCode.INVALID_ORGANIZATION_STATUS.getCode(), message);
    }
}
