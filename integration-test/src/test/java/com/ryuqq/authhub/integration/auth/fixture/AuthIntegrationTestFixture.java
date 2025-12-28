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

    /**
     * 기본 로그인 요청 생성
     *
     * @return 기본 테스트 자격증명을 사용한 로그인 요청
     */
    public static LoginApiRequest loginRequest() {
        return loginRequest(DEFAULT_TEST_IDENTIFIER, DEFAULT_TEST_PASSWORD);
    }

    /**
     * 커스텀 로그인 요청 생성
     *
     * @param identifier 사용자 식별자 (이메일)
     * @param password 비밀번호
     * @return 로그인 요청
     */
    public static LoginApiRequest loginRequest(String identifier, String password) {
        return new LoginApiRequest(identifier, password);
    }

    // ========================================
    // 로그아웃 요청
    // ========================================

    /**
     * 로그아웃 요청 생성
     *
     * @param userId 로그아웃할 사용자 ID
     * @return 로그아웃 요청
     */
    public static LogoutApiRequest logoutRequest(UUID userId) {
        return new LogoutApiRequest(userId);
    }

    // ========================================
    // 토큰 갱신 요청
    // ========================================

    /**
     * 토큰 갱신 요청 생성
     *
     * @param refreshToken 갱신에 사용할 리프레시 토큰
     * @return 토큰 갱신 요청
     */
    public static RefreshTokenApiRequest refreshTokenRequest(String refreshToken) {
        return new RefreshTokenApiRequest(refreshToken);
    }

    // ========================================
    // 검증 실패용 Fixture
    // ========================================

    /**
     * 빈 식별자를 가진 로그인 요청 (검증 실패용)
     *
     * <p>@NotBlank 검증 실패를 테스트하기 위한 Fixture입니다.
     *
     * @return identifier가 빈 문자열인 로그인 요청
     */
    public static LoginApiRequest loginRequestWithEmptyIdentifier() {
        return new LoginApiRequest("", DEFAULT_TEST_PASSWORD);
    }

    /**
     * 빈 비밀번호를 가진 로그인 요청 (검증 실패용)
     *
     * <p>@NotBlank 검증 실패를 테스트하기 위한 Fixture입니다.
     *
     * @return password가 빈 문자열인 로그인 요청
     */
    public static LoginApiRequest loginRequestWithEmptyPassword() {
        return new LoginApiRequest(DEFAULT_TEST_IDENTIFIER, "");
    }

    /**
     * 잘못된 자격증명을 가진 로그인 요청 (인증 실패용)
     *
     * <p>존재하지 않는 사용자 또는 잘못된 비밀번호로 인증 실패를 테스트합니다.
     *
     * @return 잘못된 자격증명을 가진 로그인 요청
     */
    public static LoginApiRequest loginRequestWithInvalidCredentials() {
        return new LoginApiRequest("invalid@example.com", "wrongpassword");
    }
}
