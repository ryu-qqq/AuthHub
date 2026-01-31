package com.ryuqq.authhub.adapter.in.rest.token.dto.response;

import com.ryuqq.authhub.application.token.dto.response.LoginResponse;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 로그인 API 응답 DTO
 *
 * <p>로그인 성공 시 반환되는 응답 데이터입니다.
 *
 * @param userId 사용자 ID (UUIDv7 문자열)
 * @param accessToken 액세스 토큰
 * @param refreshToken 리프레시 토큰
 * @param expiresIn 액세스 토큰 만료 시간(초)
 * @param tokenType 토큰 타입
 * @author development-team
 * @since 1.0.0
 */
@Schema(description = "로그인 응답")
public record LoginApiResponse(
        @Schema(description = "사용자 ID") String userId,
        @Schema(description = "액세스 토큰") String accessToken,
        @Schema(description = "리프레시 토큰") String refreshToken,
        @Schema(description = "액세스 토큰 만료 시간(초)") Long expiresIn,
        @Schema(description = "토큰 타입", example = "Bearer") String tokenType) {
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
                appResponse.tokenType());
    }
}
