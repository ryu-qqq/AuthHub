package com.ryuqq.authhub.domain.auth.exception;

import com.ryuqq.authhub.domain.common.exception.DomainException;
import java.util.Map;
import java.util.UUID;

/**
 * InvalidCredentialsException - 인증 실패 예외
 *
 * <p>로그인 시 자격 증명(identifier/password)이 유효하지 않을 때 발생합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public class InvalidCredentialsException extends DomainException {

    public InvalidCredentialsException() {
        super(AuthErrorCode.INVALID_CREDENTIALS);
    }

    public InvalidCredentialsException(UUID tenantId, String identifier) {
        super(
                AuthErrorCode.INVALID_CREDENTIALS,
                Map.of(
                        "tenantId", tenantId,
                        "identifier", identifier));
    }
}
