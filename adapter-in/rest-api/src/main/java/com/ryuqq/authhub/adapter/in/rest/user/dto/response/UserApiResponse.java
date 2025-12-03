package com.ryuqq.authhub.adapter.in.rest.user.dto.response;

import java.time.Instant;
import java.util.UUID;

import com.ryuqq.authhub.application.user.dto.response.UserResponse;

/**
 * 사용자 정보 API 응답 DTO
 *
 * <p>사용자 정보를 클라이언트에게 반환합니다.
 *
 * @param userId 사용자 ID
 * @param tenantId 테넌트 ID
 * @param organizationId 조직 ID
 * @param userType 사용자 유형
 * @param status 사용자 상태
 * @param name 이름
 * @param phoneNumber 전화번호
 * @param createdAt 생성 시간
 * @param updatedAt 수정 시간
 * @author development-team
 * @since 1.0.0
 */
public record UserApiResponse(
        UUID userId,
        Long tenantId,
        Long organizationId,
        String userType,
        String status,
        String name,
        String phoneNumber,
        Instant createdAt,
        Instant updatedAt
) {
    /**
     * UseCase 응답으로부터 API 응답 생성
     *
     * @param response UseCase 응답
     * @return API 응답
     */
    public static UserApiResponse from(UserResponse response) {
        return new UserApiResponse(
                response.userId(),
                response.tenantId(),
                response.organizationId(),
                response.userType(),
                response.status(),
                response.name(),
                response.phoneNumber(),
                response.createdAt(),
                response.updatedAt()
        );
    }
}
