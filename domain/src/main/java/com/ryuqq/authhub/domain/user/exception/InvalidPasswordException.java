package com.ryuqq.authhub.domain.user.exception;

import com.ryuqq.authhub.domain.common.exception.DomainException;
import java.util.Map;
import java.util.UUID;

/**
 * InvalidPasswordException - 유효하지 않은 비밀번호 예외
 *
 * <p>비밀번호 검증 실패 시 발생하는 예외입니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public class InvalidPasswordException extends DomainException {

    public InvalidPasswordException(UUID userId) {
        super(UserErrorCode.INVALID_PASSWORD, Map.of("userId", userId));
    }

    public InvalidPasswordException(String message) {
        super(UserErrorCode.INVALID_PASSWORD.getCode(), message);
    }
}
