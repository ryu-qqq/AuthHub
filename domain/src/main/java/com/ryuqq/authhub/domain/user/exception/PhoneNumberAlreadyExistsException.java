package com.ryuqq.authhub.domain.user.exception;

import com.ryuqq.authhub.domain.common.exception.DomainException;
import java.util.Map;

/**
 * PhoneNumberAlreadyExistsException - 전화번호 중복 예외
 *
 * <p>동일 Tenant 내에 중복된 전화번호가 존재할 때 발생합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public class PhoneNumberAlreadyExistsException extends DomainException {

    public PhoneNumberAlreadyExistsException(Long tenantId, String phoneNumber) {
        super(
                UserErrorCode.DUPLICATE_PHONE_NUMBER,
                Map.of(
                        "tenantId", tenantId,
                        "phoneNumber", phoneNumber));
    }
}
