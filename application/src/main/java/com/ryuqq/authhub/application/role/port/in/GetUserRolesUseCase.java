package com.ryuqq.authhub.application.role.port.in;

import com.ryuqq.authhub.application.role.dto.response.UserRolesResponse;
import java.util.UUID;

/**
 * GetUserRolesUseCase - 사용자 권한 조회 UseCase
 *
 * <p>사용자에게 할당된 Role과 Permission을 조회합니다.
 *
 * <p><strong>사용처:</strong>
 *
 * <ul>
 *   <li>로그인 시 JWT 토큰에 권한 정보 포함
 *   <li>권한 확인 API
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface GetUserRolesUseCase {

    /**
     * 사용자의 Role 및 Permission 조회
     *
     * @param userId 사용자 ID
     * @return UserRolesResponse (역할 및 권한 정보)
     */
    UserRolesResponse execute(UUID userId);
}
