package com.ryuqq.authhub.domain.user.exception;

import com.ryuqq.authhub.domain.common.exception.DomainException;
import java.util.Map;

/**
 * UserNotFoundException - User를 찾을 수 없을 때 발생
 *
 * <p>특정 ID의 User가 존재하지 않거나 조회할 수 없을 때 발생하는 예외입니다.
 *
 * <p><strong>발생 시나리오:</strong>
 *
 * <ul>
 *   <li>존재하지 않는 User ID로 조회
 *   <li>삭제된 User 조회
 *   <li>권한 없는 User 접근
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public class UserNotFoundException extends DomainException {

    /**
     * Constructor - userId로 예외 생성
     *
     * @param userId 조회 시도한 User ID
     * @author development-team
     * @since 1.0.0
     */
    public UserNotFoundException(Long userId) {
        super(
                UserErrorCode.USER_NOT_FOUND.getCode(),
                UserErrorCode.USER_NOT_FOUND.getMessage(),
                Map.of("userId", userId));
    }
}
