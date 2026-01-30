package com.ryuqq.authhub.domain.user.exception;

import com.ryuqq.authhub.domain.common.exception.DomainException;
import com.ryuqq.authhub.domain.user.id.UserId;
import com.ryuqq.authhub.domain.user.vo.Identifier;
import java.util.Map;

/**
 * UserNotFoundException - 사용자를 찾을 수 없을 때 발생하는 예외
 *
 * @author development-team
 * @since 1.0.0
 */
public class UserNotFoundException extends DomainException {

    public UserNotFoundException(String userId) {
        super(UserErrorCode.USER_NOT_FOUND, Map.of("userId", userId));
    }

    public UserNotFoundException(UserId userId) {
        this(userId.value());
    }

    public UserNotFoundException(Identifier identifier) {
        super(UserErrorCode.USER_NOT_FOUND, Map.of("identifier", identifier.value()));
    }
}
