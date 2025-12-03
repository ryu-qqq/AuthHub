package com.ryuqq.authhub.adapter.in.rest.role.dto.response;

import java.util.Set;
import java.util.UUID;

import com.ryuqq.authhub.application.role.dto.response.UserRolesResponse;

/**
 * 사용자 권한 정보 API 응답 DTO
 *
 * <p>REST API로 사용자의 역할 및 권한 정보를 반환할 때 사용되는 불변 DTO입니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public record UserRolesApiResponse(
        UUID userId,
        Set<String> roles,
        Set<String> permissions
) {

    /**
     * Application Layer Response로부터 API Response 생성
     *
     * @param response UseCase 응답
     * @return API 응답 DTO
     */
    public static UserRolesApiResponse from(UserRolesResponse response) {
        return new UserRolesApiResponse(
                response.userId(),
                response.roles(),
                response.permissions()
        );
    }
}
