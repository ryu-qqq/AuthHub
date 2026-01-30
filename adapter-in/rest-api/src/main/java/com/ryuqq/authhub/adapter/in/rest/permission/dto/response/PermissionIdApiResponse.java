package com.ryuqq.authhub.adapter.in.rest.permission.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * PermissionIdApiResponse - Permission ID 응답 DTO
 *
 * <p>Permission 생성 후 ID만 반환하는 REST API 응답 DTO입니다.
 *
 * <p>ADTO-001: API Response DTO는 Record로 정의.
 *
 * <p>ADTO-005: *ApiResponse 네이밍.
 *
 * @param permissionId 생성된 Permission ID
 * @author development-team
 * @since 1.0.0
 */
@Schema(description = "Permission ID 응답")
public record PermissionIdApiResponse(
        @Schema(description = "Permission ID", example = "1") Long permissionId) {

    /**
     * PermissionIdApiResponse 생성 팩토리 메서드
     *
     * @param permissionId Permission ID
     * @return PermissionIdApiResponse
     */
    public static PermissionIdApiResponse of(Long permissionId) {
        return new PermissionIdApiResponse(permissionId);
    }
}
