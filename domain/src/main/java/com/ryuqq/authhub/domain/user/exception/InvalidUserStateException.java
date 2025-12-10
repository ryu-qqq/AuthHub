package com.ryuqq.authhub.domain.user.exception;

import com.ryuqq.authhub.domain.common.exception.DomainException;
import com.ryuqq.authhub.domain.user.vo.UserStatus;
import java.util.Map;

/**
 * InvalidUserStateException - 유효하지 않은 사용자 상태 시 발생하는 예외
 *
 * @author development-team
 * @since 1.0.0
 */
public class InvalidUserStateException extends DomainException {

    public InvalidUserStateException(UserStatus currentStatus, String reason) {
        super(
                UserErrorCode.INVALID_USER_STATE,
                Map.of("currentStatus", currentStatus.name(), "reason", reason));
    }

    public InvalidUserStateException(UserStatus currentStatus, UserStatus targetStatus) {
        super(
                UserErrorCode.INVALID_USER_STATE,
                Map.of(
                        "currentStatus", currentStatus.name(),
                        "targetStatus", targetStatus.name()));
    }
}
