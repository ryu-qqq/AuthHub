package com.ryuqq.authhub.adapter.in.rest.role.dto.response;

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
public record RoleUserApiResponse(
        String userId, String email, String tenantId, String organizationId, Instant assignedAt) {

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
