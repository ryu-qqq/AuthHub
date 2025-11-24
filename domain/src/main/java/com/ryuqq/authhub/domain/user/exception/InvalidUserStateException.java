package com.ryuqq.authhub.domain.user.exception;

import com.ryuqq.authhub.domain.common.exception.DomainException;

import java.util.Map;
import java.util.UUID;

/**
 * InvalidUserStateException - User가 유효하지 않은 상태일 때 발생
 *
 * <p>User가 비즈니스 규칙에 위배되는 상태이거나 허용되지 않는 작업을 시도할 때 발생하는 예외입니다.
 *
 * <p><strong>발생 시나리오:</strong>
 * <ul>
 *   <li>삭제된 User에 대한 작업 시도</li>
 *   <li>비활성화된 User 사용</li>
 *   <li>잠긴 User 접근</li>
 *   <li>상태 전환 규칙 위반</li>
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public class InvalidUserStateException extends DomainException {

    /**
     * Constructor - userId와 사유로 예외 생성
     *
     * @param userId 상태가 유효하지 않은 User ID (UUID)
     * @param reason 상태 유효성 위반 사유
     * @author development-team
     * @since 1.0.0
     */
    public InvalidUserStateException(UUID userId, String reason) {
        super(
                UserErrorCode.INVALID_USER_STATUS.getCode(),
                UserErrorCode.INVALID_USER_STATUS.getMessage(),
                Map.of(
                        "userId", userId,
                        "reason", reason
                )
        );
    }
}
