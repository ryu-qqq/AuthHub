package com.ryuqq.authhub.adapter.in.rest.user.dto.response;

import java.time.Instant;
import java.util.UUID;

import com.ryuqq.authhub.application.user.dto.response.CreateUserResponse;

/**
 * 사용자 생성 API 응답 DTO
 *
 * <p>사용자 생성 결과를 클라이언트에게 반환합니다.
 *
 * @param userId 생성된 사용자 ID
 * @param createdAt 생성 시간
 * @author development-team
 * @since 1.0.0
 */
public record CreateUserApiResponse(
        UUID userId,
        Instant createdAt
) {
    /**
     * UseCase 응답으로부터 API 응답 생성
     *
     * @param response UseCase 응답
     * @return API 응답
     */
    public static CreateUserApiResponse from(CreateUserResponse response) {
        return new CreateUserApiResponse(
                response.userId(),
                response.createdAt()
        );
    }
}
