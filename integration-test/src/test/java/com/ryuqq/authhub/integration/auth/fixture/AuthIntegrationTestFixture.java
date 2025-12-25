package com.ryuqq.authhub.integration.auth.fixture;

import com.ryuqq.authhub.adapter.in.rest.auth.dto.command.LoginApiRequest;
import com.ryuqq.authhub.adapter.in.rest.auth.dto.command.LogoutApiRequest;
import com.ryuqq.authhub.adapter.in.rest.auth.dto.command.RefreshTokenApiRequest;
import java.util.UUID;

/**
 * 인증 통합 테스트 Fixture
 *
 * <p>API Request/Response 객체 생성 유틸리티
 *
 * @author Development Team
 * @since 1.0.0
 */
public final class AuthIntegrationTestFixture {

    public static final String DEFAULT_TEST_IDENTIFIER = "testuser@example.com";
    public static final String DEFAULT_TEST_PASSWORD = "Password123!";

    private AuthIntegrationTestFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    // ========================================
    // 로그인 요청
    // ========================================
    public static LoginApiRequest loginRequest() {
        return loginRequest(DEFAULT_TEST_IDENTIFIER, DEFAULT_TEST_PASSWORD);
    }

    public static LoginApiRequest loginRequest(String identifier, String password) {
        return new LoginApiRequest(identifier, password);
    }

    // ========================================
    // 로그아웃 요청
    // ========================================
    public static LogoutApiRequest logoutRequest(UUID userId) {
        return new LogoutApiRequest(userId);
    }

    // ========================================
    // 토큰 갱신 요청
    // ========================================
    public static RefreshTokenApiRequest refreshTokenRequest(String refreshToken) {
        return new RefreshTokenApiRequest(refreshToken);
    }

    // ========================================
    // 검증 실패용 Fixture
    // ========================================
    public static LoginApiRequest loginRequestWithEmptyIdentifier() {
        return new LoginApiRequest("", DEFAULT_TEST_PASSWORD);
    }

    public static LoginApiRequest loginRequestWithEmptyPassword() {
        return new LoginApiRequest(DEFAULT_TEST_IDENTIFIER, "");
    }

    public static LoginApiRequest loginRequestWithInvalidCredentials() {
        return new LoginApiRequest("invalid@example.com", "wrongpassword");
    }
}
