package com.ryuqq.authhub.domain.user.exception;

import com.ryuqq.authhub.domain.common.exception.DomainException;
import java.util.Map;

/**
 * InvalidPasswordException - 비밀번호가 일치하지 않을 때 발생하는 예외
 *
 * <p>로그인 시 비밀번호 검증 실패 시 발생합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public class InvalidPasswordException extends DomainException {

    public InvalidPasswordException() {
        super(UserErrorCode.INVALID_PASSWORD, Map.of());
    }

    public InvalidPasswordException(String identifier) {
        super(UserErrorCode.INVALID_PASSWORD, Map.of("identifier", identifier));
    }
}
