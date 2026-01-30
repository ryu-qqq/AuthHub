package com.ryuqq.authhub.adapter.in.rest.token.fixture;

import com.ryuqq.authhub.adapter.in.rest.token.dto.command.LoginApiRequest;
import com.ryuqq.authhub.adapter.in.rest.token.dto.command.LogoutApiRequest;
import com.ryuqq.authhub.adapter.in.rest.token.dto.command.RefreshTokenApiRequest;
import com.ryuqq.authhub.adapter.in.rest.token.dto.response.LoginApiResponse;
import com.ryuqq.authhub.adapter.in.rest.token.dto.response.PublicKeyApiResponse;
import com.ryuqq.authhub.adapter.in.rest.token.dto.response.PublicKeysApiResponse;
import com.ryuqq.authhub.adapter.in.rest.token.dto.response.TokenApiResponse;
import java.util.List;

/**
 * Token API 테스트 픽스처
 *
 * <p>Token 관련 API 테스트에 사용되는 DTO 픽스처를 제공합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public final class TokenApiFixture {

    private static final String DEFAULT_USER_ID = "01941234-5678-7000-8000-123456789abc";
    private static final String DEFAULT_IDENTIFIER = "testuser@example.com";
    private static final String DEFAULT_PASSWORD = "password123!";
    private static final String DEFAULT_ACCESS_TOKEN =
            "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.test-access-token";
    private static final String DEFAULT_REFRESH_TOKEN =
            "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.test-refresh-token";
    private static final long DEFAULT_ACCESS_TOKEN_EXPIRES_IN = 3600L;
    private static final long DEFAULT_REFRESH_TOKEN_EXPIRES_IN = 604800L;
    private static final String DEFAULT_TOKEN_TYPE = "Bearer";

    private TokenApiFixture() {}

    // ========== LoginApiRequest ==========

    /** 기본 로그인 요청 */
    public static LoginApiRequest loginRequest() {
        return new LoginApiRequest(DEFAULT_IDENTIFIER, DEFAULT_PASSWORD);
    }

    /** 커스텀 식별자로 로그인 요청 */
    public static LoginApiRequest loginRequest(String identifier, String password) {
        return new LoginApiRequest(identifier, password);
    }

    // ========== LogoutApiRequest ==========

    /** 기본 로그아웃 요청 */
    public static LogoutApiRequest logoutRequest() {
        return new LogoutApiRequest(DEFAULT_USER_ID);
    }

    /** 커스텀 사용자 ID로 로그아웃 요청 */
    public static LogoutApiRequest logoutRequest(String userId) {
        return new LogoutApiRequest(userId);
    }

    // ========== RefreshTokenApiRequest ==========

    /** 기본 토큰 갱신 요청 */
    public static RefreshTokenApiRequest refreshTokenRequest() {
        return new RefreshTokenApiRequest(DEFAULT_REFRESH_TOKEN);
    }

    /** 커스텀 리프레시 토큰으로 갱신 요청 */
    public static RefreshTokenApiRequest refreshTokenRequest(String refreshToken) {
        return new RefreshTokenApiRequest(refreshToken);
    }

    // ========== LoginApiResponse ==========

    /** 기본 로그인 응답 */
    public static LoginApiResponse loginResponse() {
        return new LoginApiResponse(
                DEFAULT_USER_ID,
                DEFAULT_ACCESS_TOKEN,
                DEFAULT_REFRESH_TOKEN,
                DEFAULT_ACCESS_TOKEN_EXPIRES_IN,
                DEFAULT_TOKEN_TYPE);
    }

    /** 커스텀 로그인 응답 */
    public static LoginApiResponse loginResponse(
            String userId, String accessToken, String refreshToken) {
        return new LoginApiResponse(
                userId,
                accessToken,
                refreshToken,
                DEFAULT_ACCESS_TOKEN_EXPIRES_IN,
                DEFAULT_TOKEN_TYPE);
    }

    // ========== TokenApiResponse ==========

    /** 기본 토큰 갱신 응답 */
    public static TokenApiResponse tokenResponse() {
        return new TokenApiResponse(
                DEFAULT_ACCESS_TOKEN,
                DEFAULT_REFRESH_TOKEN,
                DEFAULT_ACCESS_TOKEN_EXPIRES_IN,
                DEFAULT_REFRESH_TOKEN_EXPIRES_IN,
                DEFAULT_TOKEN_TYPE);
    }

    /** 커스텀 토큰 갱신 응답 */
    public static TokenApiResponse tokenResponse(String accessToken, String refreshToken) {
        return new TokenApiResponse(
                accessToken,
                refreshToken,
                DEFAULT_ACCESS_TOKEN_EXPIRES_IN,
                DEFAULT_REFRESH_TOKEN_EXPIRES_IN,
                DEFAULT_TOKEN_TYPE);
    }

    // ========== PublicKeysApiResponse ==========

    /** 기본 공개키 목록 응답 */
    public static PublicKeysApiResponse publicKeysResponse() {
        return new PublicKeysApiResponse(List.of(publicKeyResponse()));
    }

    /** 빈 공개키 목록 응답 */
    public static PublicKeysApiResponse emptyPublicKeysResponse() {
        return new PublicKeysApiResponse(List.of());
    }

    // ========== PublicKeyApiResponse ==========

    /** 기본 공개키 응답 */
    public static PublicKeyApiResponse publicKeyResponse() {
        return new PublicKeyApiResponse(
                "key-id-1",
                "RSA",
                "sig",
                "RS256",
                "0vx7agoebGcQSuuPiLJXZptN9nndrQmbXEps2aiAFbWhM78LhWx4cbbfAAtVT86zwu1RK7aPFFxuhDR1L6tSoc_BJECPebWKRXjBZCiFV4n3oknjhMstn64tZ_2W-5JsGY4Hc5n9yBXArwl93lqt7_RN5w6Cf0h4QyQ5v-65YGjQR0_FDW2QvzqY368QQMicAtaSqzs8KJZgnYb9c7d0zgdAZHzu6qMQvRL5hajrn1n91CbOpbISD08qNLyrdkt-bFTWhAI4vMQFh6WeZu0fM4lFd2NcRwr3XPksINHaQ-G_xBniIqbw0Ls1jF44-csFCur-kEgU8awapJzKnqDKgw",
                "AQAB");
    }

    // ========== Default Values ==========

    public static String defaultUserId() {
        return DEFAULT_USER_ID;
    }

    public static String defaultIdentifier() {
        return DEFAULT_IDENTIFIER;
    }

    public static String defaultPassword() {
        return DEFAULT_PASSWORD;
    }

    public static String defaultAccessToken() {
        return DEFAULT_ACCESS_TOKEN;
    }

    public static String defaultRefreshToken() {
        return DEFAULT_REFRESH_TOKEN;
    }
}
