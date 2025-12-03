package com.ryuqq.authhub.domain.user.exception;

import com.ryuqq.authhub.domain.common.exception.DomainException;
import java.util.Map;
import java.util.UUID;

/**
 * UserNotFoundException - 사용자를 찾을 수 없는 예외
 *
 * <p>요청한 사용자가 존재하지 않을 때 발생합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public class UserNotFoundException extends DomainException {

    public UserNotFoundException(Long userId) {
        super(UserErrorCode.USER_NOT_FOUND, Map.of("userId", userId));
    }

    public UserNotFoundException(UUID userId) {
        super(UserErrorCode.USER_NOT_FOUND, Map.of("userId", userId));
    }

    public UserNotFoundException(String identifier) {
        super(UserErrorCode.USER_NOT_FOUND, Map.of("identifier", identifier));
    }
}
