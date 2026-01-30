package com.ryuqq.authhub.domain.user.exception;

import com.ryuqq.authhub.domain.common.exception.DomainException;
import com.ryuqq.authhub.domain.user.id.UserId;
import com.ryuqq.authhub.domain.user.vo.UserStatus;
import java.util.Map;

/**
 * UserNotActiveException - 사용자가 활성 상태가 아닐 때 발생하는 예외
 *
 * <p>비활성, 정지, 삭제된 사용자가 로그인 등 활성 상태에서만 가능한 작업을 시도할 때 발생합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public class UserNotActiveException extends DomainException {

    public UserNotActiveException(String userId, UserStatus status) {
        super(resolveErrorCode(status), Map.of("userId", userId, "status", status.name()));
    }

    public UserNotActiveException(UserId userId, UserStatus status) {
        this(userId.value(), status);
    }

    private static UserErrorCode resolveErrorCode(UserStatus status) {
        return switch (status) {
            case SUSPENDED -> UserErrorCode.USER_SUSPENDED;
            case INACTIVE -> UserErrorCode.USER_NOT_ACTIVE;
            default -> UserErrorCode.USER_NOT_ACTIVE;
        };
    }
}
