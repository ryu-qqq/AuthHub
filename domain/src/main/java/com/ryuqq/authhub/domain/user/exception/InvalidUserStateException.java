package com.ryuqq.authhub.domain.user.exception;

import com.ryuqq.authhub.domain.common.exception.DomainException;
import com.ryuqq.authhub.domain.user.vo.UserStatus;

import java.util.Map;
import java.util.UUID;

/**
 * InvalidUserStateException - 유효하지 않은 User 상태 예외
 *
 * <p>User 상태 전이가 유효하지 않을 때 발생하는 예외입니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public class InvalidUserStateException extends DomainException {

    public InvalidUserStateException(UserStatus currentStatus, UserStatus targetStatus) {
        super(
                UserErrorCode.INVALID_USER_STATUS,
                Map.of(
                        "currentStatus", currentStatus != null ? currentStatus.name() : "null",
                        "targetStatus", targetStatus != null ? targetStatus.name() : "null"
                )
        );
    }

    public InvalidUserStateException(UUID userId, String reason) {
        super(
                UserErrorCode.INVALID_USER_STATUS,
                Map.of(
                        "userId", userId,
                        "reason", reason
                )
        );
    }

    public InvalidUserStateException(String message) {
        super(UserErrorCode.INVALID_USER_STATUS.getCode(), message);
    }
}
