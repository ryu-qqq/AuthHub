package com.ryuqq.authhub.domain.auth.user.exception;

import com.ryuqq.authhub.domain.auth.user.UserId;
import com.ryuqq.authhub.domain.auth.user.UserStatus;

/**
 * User 상태가 유효하지 않을 때 발생하는 예외.
 *
 * <p>다음과 같은 경우 발생합니다:</p>
 * <ul>
 *   <li>User가 ACTIVE 상태가 아닌 경우 (SUSPENDED, DELETED 등)</li>
 *   <li>시스템 사용이 불가능한 User 상태인 경우</li>
 *   <li>특정 작업에 필요한 User 상태 조건을 만족하지 못한 경우</li>
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
public class InvalidUserStatusException extends UserDomainException {

    private final UserId userId;
    private final UserStatus currentStatus;

    /**
     * 기본 생성자.
     *
     * @param message 예외 메시지
     * @author AuthHub Team
     * @since 1.0.0
     */
    public InvalidUserStatusException(final String message) {
        super(message);
        this.userId = null;
        this.currentStatus = null;
    }

    /**
     * UserId와 현재 상태 정보를 포함한 생성자.
     *
     * @param message 예외 메시지
     * @param userId 사용자 ID
     * @param currentStatus 현재 사용자 상태
     * @author AuthHub Team
     * @since 1.0.0
     */
    public InvalidUserStatusException(
            final String message,
            final UserId userId,
            final UserStatus currentStatus
    ) {
        super(message);
        this.userId = userId;
        this.currentStatus = currentStatus;
    }

    /**
     * 사용자 ID를 반환합니다.
     *
     * @return UserId (null 가능)
     * @author AuthHub Team
     * @since 1.0.0
     */
    public UserId getUserId() {
        return this.userId;
    }

    /**
     * 현재 사용자 상태를 반환합니다.
     *
     * @return UserStatus (null 가능)
     * @author AuthHub Team
     * @since 1.0.0
     */
    public UserStatus getCurrentStatus() {
        return this.currentStatus;
    }

    /**
     * 상세한 예외 정보를 문자열로 반환합니다.
     *
     * @return 예외 정보 문자열
     * @author AuthHub Team
     * @since 1.0.0
     */
    @Override
    public String toString() {
        String baseMessage = super.toString();
        if (userId != null && currentStatus != null) {
            return baseMessage + " [userId=" + userId + ", currentStatus=" + currentStatus + "]";
        }
        return baseMessage;
    }
}
