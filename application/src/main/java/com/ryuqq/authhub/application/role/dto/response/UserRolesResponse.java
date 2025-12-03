package com.ryuqq.authhub.application.role.dto.response;

import java.util.Set;
import java.util.UUID;

/**
 * UserRolesResponse - 사용자 권한 정보 응답 DTO
 *
 * <p>JWT 토큰에 포함될 사용자의 역할 및 권한 정보입니다.
 *
 * @param userId 사용자 ID
 * @param roles 역할 이름 목록 (예: ["ROLE_USER", "ROLE_ADMIN"])
 * @param permissions 권한 코드 목록 (예: ["user:read", "user:write"])
 * @author development-team
 * @since 1.0.0
 */
public record UserRolesResponse(UUID userId, Set<String> roles, Set<String> permissions) {

    public UserRolesResponse {
        if (userId == null) {
            throw new IllegalArgumentException("userId는 null일 수 없습니다");
        }
        if (roles == null) {
            throw new IllegalArgumentException("roles는 null일 수 없습니다");
        }
        if (permissions == null) {
            throw new IllegalArgumentException("permissions는 null일 수 없습니다");
        }
    }
}
