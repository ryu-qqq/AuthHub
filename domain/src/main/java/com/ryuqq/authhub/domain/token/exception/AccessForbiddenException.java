package com.ryuqq.authhub.domain.token.exception;

import com.ryuqq.authhub.domain.common.exception.DomainException;
import java.util.Map;

/**
 * AccessForbiddenException - 접근 금지 예외
 *
 * <p>인증은 되었지만 해당 리소스에 대한 권한이 없을 때 발생합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public class AccessForbiddenException extends DomainException {

    public AccessForbiddenException() {
        super(TokenErrorCode.FORBIDDEN);
    }

    public AccessForbiddenException(String resourceId) {
        super(TokenErrorCode.FORBIDDEN, Map.of("resourceId", resourceId));
    }
}
