package com.ryuqq.authhub.domain.user.exception;

import com.ryuqq.authhub.domain.common.exception.DomainException;
import com.ryuqq.authhub.domain.user.vo.Identifier;
import java.util.Map;

/**
 * DuplicateUserIdentifierException - 중복된 사용자 식별자 예외
 *
 * <p>이미 존재하는 식별자(로그인 ID)로 사용자를 생성하려 할 때 발생합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public class DuplicateUserIdentifierException extends DomainException {

    public DuplicateUserIdentifierException(String identifier) {
        super(UserErrorCode.DUPLICATE_USER_IDENTIFIER, Map.of("identifier", identifier));
    }

    public DuplicateUserIdentifierException(Identifier identifier) {
        this(identifier.value());
    }
}
