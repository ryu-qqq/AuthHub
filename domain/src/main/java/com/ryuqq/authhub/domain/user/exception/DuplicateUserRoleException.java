package com.ryuqq.authhub.domain.user.exception;

import com.ryuqq.authhub.domain.common.exception.DomainException;
import java.util.Map;
import java.util.UUID;

/**
 * DuplicateUserRoleException - 중복 사용자 역할 예외
 *
 * <p>이미 할당된 역할을 재할당하려 할 때 발생합니다.
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
public class DuplicateUserRoleException extends DomainException {

    public DuplicateUserRoleException(UUID userId, UUID roleId) {
        super(UserErrorCode.DUPLICATE_USER_ROLE, Map.of("userId", userId, "roleId", roleId));
    }
}
