package com.ryuqq.authhub.adapter.in.rest.organization.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

/**
 * OrganizationUserApiResponse - 조직 소속 사용자 API 응답 DTO
 *
 * <p>조직별 사용자 조회 API의 응답 형식입니다.
 *
 * @param userId 사용자 ID (UUID)
 * @param email 사용자 이메일
 * @param tenantId 테넌트 ID
 * @param createdAt 사용자 생성 일시
 * @author development-team
 * @since 1.0.0
 */
@Schema(description = "조직 소속 사용자 API 응답")
public record OrganizationUserApiResponse(
        @Schema(description = "사용자 ID (UUID)", example = "550e8400-e29b-41d4-a716-446655440000")
                String userId,
        @Schema(description = "사용자 이메일", example = "user@example.com") String email,
        @Schema(description = "테넌트 ID", example = "550e8400-e29b-41d4-a716-446655440001")
                String tenantId,
        @Schema(description = "사용자 생성 일시") Instant createdAt) {

    /**
     * 정적 팩토리 메서드
     *
     * @param userId 사용자 ID
     * @param email 사용자 이메일
     * @param tenantId 테넌트 ID
     * @param createdAt 사용자 생성 일시
     * @return OrganizationUserApiResponse
     */
    public static OrganizationUserApiResponse of(
            String userId, String email, String tenantId, Instant createdAt) {
        return new OrganizationUserApiResponse(userId, email, tenantId, createdAt);
    }
}
