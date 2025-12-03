package com.ryuqq.authhub.domain.organization.exception;

import com.ryuqq.authhub.domain.common.exception.DomainException;
import java.util.Map;

/**
 * CannotDeactivateWithActiveUsersException - Organization 비활성화 불가 예외
 *
 * <p>Organization 비활성화 시 활성 상태의 User가 존재하는 경우 발생합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public class CannotDeactivateWithActiveUsersException extends DomainException {

    public CannotDeactivateWithActiveUsersException(Long organizationId) {
        super(OrganizationErrorCode.ACTIVE_USERS_EXIST, Map.of("organizationId", organizationId));
    }
}
