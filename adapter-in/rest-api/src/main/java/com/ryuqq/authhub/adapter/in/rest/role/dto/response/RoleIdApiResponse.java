package com.ryuqq.authhub.adapter.in.rest.role.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * RoleIdApiResponse - Role ID 응답 DTO
 *
 * <p>Role 생성 후 ID만 반환하는 REST API 응답 DTO입니다.
 *
 * <p>ADTO-001: API Response DTO는 Record로 정의.
 *
 * <p>ADTO-005: *ApiResponse 네이밍.
 *
 * @param roleId 생성된 Role ID
 * @author development-team
 * @since 1.0.0
 */
@Schema(description = "Role ID 응답")
public record RoleIdApiResponse(@Schema(description = "Role ID", example = "1") Long roleId) {

    /**
     * RoleIdApiResponse 생성 팩토리 메서드
     *
     * @param roleId Role ID
     * @return RoleIdApiResponse
     */
    public static RoleIdApiResponse of(Long roleId) {
        return new RoleIdApiResponse(roleId);
    }
}
