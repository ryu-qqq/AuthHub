package com.ryuqq.authhub.adapter.in.rest.internal.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * CreateUserWithRolesResultApiResponse - 사용자 생성 + 역할 할당 결과 API 응답 DTO
 *
 * @param userId 생성된 사용자 ID (UUIDv7)
 * @param assignedRoleCount 할당된 역할 수
 * @author development-team
 * @since 1.0.0
 */
@Schema(description = "사용자 등록 결과")
public record CreateUserWithRolesResultApiResponse(
        @Schema(description = "생성된 사용자 ID", example = "01933abc-1234-7000-8000-000000000001")
                String userId,
        @Schema(description = "할당된 역할 수", example = "1") int assignedRoleCount) {}
