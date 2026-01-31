package com.ryuqq.authhub.domain.token.exception;

import com.ryuqq.authhub.domain.common.exception.DomainException;

/**
 * InvalidRefreshTokenException - 유효하지 않은 Refresh Token 예외
 *
 * <p>Refresh Token이 유효하지 않거나 만료되었을 때 발생합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public class InvalidRefreshTokenException extends DomainException {

    public InvalidRefreshTokenException() {
        super(TokenErrorCode.INVALID_REFRESH_TOKEN);
    }
}
