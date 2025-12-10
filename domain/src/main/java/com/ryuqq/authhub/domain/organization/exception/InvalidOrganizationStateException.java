package com.ryuqq.authhub.domain.organization.exception;

import com.ryuqq.authhub.domain.common.exception.DomainException;
import com.ryuqq.authhub.domain.organization.vo.OrganizationStatus;
import java.util.Map;

/**
 * InvalidOrganizationStateException - 유효하지 않은 조직 상태 전이 시 발생하는 예외
 *
 * @author development-team
 * @since 1.0.0
 */
public class InvalidOrganizationStateException extends DomainException {

    public InvalidOrganizationStateException(OrganizationStatus currentStatus, String reason) {
        super(
                OrganizationErrorCode.INVALID_ORGANIZATION_STATE,
                Map.of("currentStatus", currentStatus.name(), "reason", reason));
    }

    public InvalidOrganizationStateException(
            OrganizationStatus currentStatus, OrganizationStatus targetStatus) {
        super(
                OrganizationErrorCode.INVALID_ORGANIZATION_STATE,
                Map.of(
                        "currentStatus", currentStatus.name(),
                        "targetStatus", targetStatus.name()));
    }
}
