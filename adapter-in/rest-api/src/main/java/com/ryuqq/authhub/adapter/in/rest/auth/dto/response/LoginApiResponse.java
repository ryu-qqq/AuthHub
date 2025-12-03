package com.ryuqq.authhub.adapter.in.rest.auth.dto.response;

import java.util.UUID;

import com.ryuqq.authhub.application.auth.dto.response.LoginResponse;

/**
 * 로그인 API 응답 DTO
 *
 * <p>로그인 성공 시 반환되는 응답 데이터입니다.
 *
 * @param userId 사용자 ID
 * @param accessToken 액세스 토큰
 * @param refreshToken 리프레시 토큰
 * @param expiresIn 액세스 토큰 만료 시간(초)
 * @param tokenType 토큰 타입
 * @author development-team
 * @since 1.0.0
 */
public record LoginApiResponse(
        UUID userId,
        String accessToken,
        String refreshToken,
        Long expiresIn,
        String tokenType
) {
    /**
     * Application Layer Response에서 REST API Response로 변환
     *
     * @param appResponse Application Layer 응답 객체
     * @return REST API 응답 객체
     */
    public static LoginApiResponse from(LoginResponse appResponse) {
        return new LoginApiResponse(
                appResponse.userId(),
                appResponse.accessToken(),
                appResponse.refreshToken(),
                appResponse.expiresIn(),
                appResponse.tokenType()
        );
    }
}
