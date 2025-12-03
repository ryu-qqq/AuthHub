package com.ryuqq.authhub.adapter.in.rest.auth.dto.response;

import com.ryuqq.authhub.application.auth.dto.response.TokenResponse;

/**
 * 토큰 갱신 API 응답 DTO
 *
 * <p>토큰 갱신 성공 시 반환되는 응답 데이터입니다.
 *
 * @param accessToken 액세스 토큰
 * @param refreshToken 리프레시 토큰
 * @param accessTokenExpiresIn 액세스 토큰 만료 시간(초)
 * @param refreshTokenExpiresIn 리프레시 토큰 만료 시간(초)
 * @param tokenType 토큰 타입
 * @author development-team
 * @since 1.0.0
 */
public record TokenApiResponse(
        String accessToken,
        String refreshToken,
        long accessTokenExpiresIn,
        long refreshTokenExpiresIn,
        String tokenType
) {
    /**
     * Application Layer Response에서 REST API Response로 변환
     *
     * @param appResponse Application Layer 응답 객체
     * @return REST API 응답 객체
     */
    public static TokenApiResponse from(TokenResponse appResponse) {
        return new TokenApiResponse(
                appResponse.accessToken(),
                appResponse.refreshToken(),
                appResponse.accessTokenExpiresIn(),
                appResponse.refreshTokenExpiresIn(),
                appResponse.tokenType()
        );
    }
}
