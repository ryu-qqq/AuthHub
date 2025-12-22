package com.ryuqq.authhub.adapter.in.rest.role.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

/**
 * RoleUserApiResponse - 역할에 할당된 사용자 API 응답 DTO
 *
 * <p>역할별 사용자 조회 API의 응답 형식입니다.
 *
 * @param userId 사용자 ID (UUID)
 * @param email 사용자 이메일
 * @param tenantId 테넌트 ID
 * @param organizationId 조직 ID
 * @param assignedAt 역할 할당 일시
 * @author development-team
 * @since 1.0.0
 */
@Schema(description = "역할에 할당된 사용자 API 응답")
public record RoleUserApiResponse(
        @Schema(description = "사용자 ID (UUID)", example = "550e8400-e29b-41d4-a716-446655440000")
                String userId,
        @Schema(description = "사용자 이메일", example = "user@example.com") String email,
        @Schema(description = "테넌트 ID", example = "550e8400-e29b-41d4-a716-446655440001")
                String tenantId,
        @Schema(description = "조직 ID", example = "550e8400-e29b-41d4-a716-446655440002")
                String organizationId,
        @Schema(description = "역할 할당 일시") Instant assignedAt) {

    /**
     * 정적 팩토리 메서드
     *
     * @param userId 사용자 ID
     * @param email 사용자 이메일
     * @param tenantId 테넌트 ID
     * @param organizationId 조직 ID
     * @param assignedAt 역할 할당 일시
     * @return RoleUserApiResponse
     */
    public static RoleUserApiResponse of(
            String userId,
            String email,
            String tenantId,
            String organizationId,
            Instant assignedAt) {
        return new RoleUserApiResponse(userId, email, tenantId, organizationId, assignedAt);
    }
}
