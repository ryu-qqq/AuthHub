package com.ryuqq.authhub.domain.user.exception;

import com.ryuqq.authhub.domain.common.exception.DomainException;
import java.util.Map;

/**
 * InvalidPasswordException - 잘못된 비밀번호 예외
 *
 * <p>비밀번호 검증 실패 시 발생합니다.
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
public class InvalidPasswordException extends DomainException {

    public InvalidPasswordException() {
        super(UserErrorCode.INVALID_USER_STATE, Map.of());
    }
}
