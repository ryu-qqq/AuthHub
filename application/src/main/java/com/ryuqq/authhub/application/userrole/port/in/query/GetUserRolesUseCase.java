package com.ryuqq.authhub.application.userrole.port.in.query;

import com.ryuqq.authhub.application.userrole.dto.response.UserRolesResponse;

/**
 * GetUserRolesUseCase - 사용자 역할/권한 조회 Use Case
 *
 * <p>사용자에게 할당된 역할 및 권한 목록을 조회하는 비즈니스 유스케이스입니다.
 *
 * <p><strong>사용처:</strong>
 *
 * <ul>
 *   <li>JWT 토큰 발급 시 TokenManager에서 호출
 *   <li>사용자 역할/권한 정보 조회 API
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface GetUserRolesUseCase {

    /**
     * 사용자의 역할 및 권한 조회
     *
     * @param userId 사용자 ID
     * @return 역할/권한 응답 (roles, permissions)
     */
    UserRolesResponse execute(String userId);
}
