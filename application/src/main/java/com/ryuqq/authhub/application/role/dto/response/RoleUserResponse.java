package com.ryuqq.authhub.application.role.dto.response;

import java.time.Instant;

/**
 * RoleUserResponse - 역할에 할당된 사용자 응답 DTO
 *
 * <p>특정 역할에 할당된 사용자 정보를 담는 응답 DTO입니다.
 *
 * @param userId 사용자 ID (UUID)
 * @param email 사용자 이메일 (identifier)
 * @param tenantId 테넌트 ID
 * @param organizationId 조직 ID
 * @param assignedAt 역할 할당 일시
 * @author development-team
 * @since 1.0.0
 */
public record RoleUserResponse(
        String userId, String email, String tenantId, String organizationId, Instant assignedAt) {

    /**
     * 정적 팩토리 메서드
     *
     * @param userId 사용자 ID
     * @param email 사용자 이메일
     * @param tenantId 테넌트 ID
     * @param organizationId 조직 ID
     * @param assignedAt 역할 할당 일시
     * @return RoleUserResponse
     */
    public static RoleUserResponse of(
            String userId,
            String email,
            String tenantId,
            String organizationId,
            Instant assignedAt) {
        return new RoleUserResponse(userId, email, tenantId, organizationId, assignedAt);
    }
}
