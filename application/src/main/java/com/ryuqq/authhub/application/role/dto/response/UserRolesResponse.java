package com.ryuqq.authhub.application.role.dto.response;

import java.util.Set;
import java.util.UUID;

/**
 * UserRolesResponse - 사용자 역할/권한 응답 DTO
 *
 * @author development-team
 * @since 1.0.0
 */
public record UserRolesResponse(UUID userId, Set<String> roles, Set<String> permissions) {

    public static UserRolesResponse of(UUID userId, Set<String> roles, Set<String> permissions) {
        return new UserRolesResponse(userId, roles, permissions);
    }

    public static UserRolesResponse empty(UUID userId) {
        return new UserRolesResponse(userId, Set.of(), Set.of());
    }
}
