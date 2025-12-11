package com.ryuqq.authhub.application.role.port.in;

import com.ryuqq.authhub.application.role.dto.response.UserRolesResponse;
import java.util.UUID;

/**
 * GetUserRolesUseCase - 사용자 역할/권한 조회 UseCase
 *
 * @author development-team
 * @since 1.0.0
 */
public interface GetUserRolesUseCase {

    /**
     * 사용자의 역할 및 권한 조회
     *
     * @param userId 사용자 ID
     * @return 사용자의 역할 및 권한 정보
     */
    UserRolesResponse execute(UUID userId);
}
