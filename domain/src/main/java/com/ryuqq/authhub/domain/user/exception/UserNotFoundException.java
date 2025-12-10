package com.ryuqq.authhub.domain.user.exception;

import com.ryuqq.authhub.domain.common.exception.DomainException;
import com.ryuqq.authhub.domain.user.identifier.UserId;
import java.util.Map;
import java.util.UUID;

/**
 * UserNotFoundException - 사용자 찾을 수 없음 예외
 *
 * <p>존재하지 않는 사용자 조회 시 발생합니다.
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
public class UserNotFoundException extends DomainException {

    public UserNotFoundException(UserId userId) {
        super(UserErrorCode.USER_NOT_FOUND, Map.of("userId", userId.value()));
    }

    public UserNotFoundException(UUID userId) {
        super(UserErrorCode.USER_NOT_FOUND, Map.of("userId", userId));
    }

    public UserNotFoundException(String identifier) {
        super(UserErrorCode.USER_NOT_FOUND, Map.of("identifier", identifier));
    }
}
