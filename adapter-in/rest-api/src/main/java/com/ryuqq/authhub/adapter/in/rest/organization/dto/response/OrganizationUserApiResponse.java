package com.ryuqq.authhub.adapter.in.rest.organization.dto.response;

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
public record OrganizationUserApiResponse(
        String userId, String email, String tenantId, Instant createdAt) {

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
