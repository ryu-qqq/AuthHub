package com.ryuqq.authhub.domain.user.exception;

import com.ryuqq.authhub.domain.common.exception.DomainException;
import com.ryuqq.authhub.domain.user.vo.PhoneNumber;
import java.util.Map;

/**
 * DuplicateUserPhoneNumberException - 중복된 전화번호 예외
 *
 * <p>이미 존재하는 전화번호로 사용자를 생성하려 할 때 발생합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public class DuplicateUserPhoneNumberException extends DomainException {

    public DuplicateUserPhoneNumberException(String phoneNumber) {
        super(UserErrorCode.DUPLICATE_USER_PHONE_NUMBER, Map.of("phoneNumber", phoneNumber));
    }

    public DuplicateUserPhoneNumberException(PhoneNumber phoneNumber) {
        this(phoneNumber.value());
    }
}
