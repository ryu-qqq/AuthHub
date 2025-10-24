package com.ryuqq.authhub.domain.auth.user.exception;

import com.ryuqq.authhub.domain.auth.user.UserId;

/**
 * User를 찾을 수 없을 때 발생하는 예외.
 *
 * <p>다음과 같은 경우 발생합니다:</p>
 * <ul>
 *   <li>주어진 UserId에 해당하는 User가 존재하지 않는 경우</li>
 *   <li>삭제되었거나 더 이상 유효하지 않은 User를 조회하는 경우</li>
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
public class UserNotFoundException extends UserDomainException {

    private final UserId userId;

    /**
     * 기본 생성자.
     *
     * @param message 예외 메시지
     * @author AuthHub Team
     * @since 1.0.0
     */
    public UserNotFoundException(final String message) {
        super(message);
        this.userId = null;
    }

    /**
     * UserId 정보를 포함한 생성자.
     *
     * @param message 예외 메시지
     * @param userId 찾을 수 없는 사용자 ID
     * @author AuthHub Team
     * @since 1.0.0
     */
    public UserNotFoundException(final String message, final UserId userId) {
        super(message);
        this.userId = userId;
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
     * 상세한 예외 정보를 문자열로 반환합니다.
     *
     * @return 예외 정보 문자열
     * @author AuthHub Team
     * @since 1.0.0
     */
    @Override
    public String toString() {
        String baseMessage = super.toString();
        if (userId != null) {
            return baseMessage + " [userId=" + userId + "]";
        }
        return baseMessage;
    }
}
