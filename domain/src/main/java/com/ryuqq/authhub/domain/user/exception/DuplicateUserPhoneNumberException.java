package com.ryuqq.authhub.domain.user.exception;

import com.ryuqq.authhub.domain.common.exception.DomainException;
import java.util.Map;
import java.util.UUID;

/**
 * DuplicateUserPhoneNumberException - 중복 핸드폰 번호 예외
 *
 * <p>동일 테넌트 내에서 이미 존재하는 핸드폰 번호로 사용자 생성 시도 시 발생합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>DomainException 상속 필수
 *   <li>ErrorCode 사용 필수
 *   <li>Lombok 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public class DuplicateUserPhoneNumberException extends DomainException {

    public DuplicateUserPhoneNumberException(UUID tenantId, String phoneNumber) {
        super(
                UserErrorCode.DUPLICATE_USER_PHONE_NUMBER,
                Map.of("tenantId", tenantId, "phoneNumber", phoneNumber));
    }

    public DuplicateUserPhoneNumberException(String phoneNumber) {
        super(UserErrorCode.DUPLICATE_USER_PHONE_NUMBER, Map.of("phoneNumber", phoneNumber));
    }
}
