package com.ryuqq.authhub.adapter.in.rest.auth.dto.response;

/**
 * Login API Response DTO - 로그인 응답 데이터 전송 객체.
 *
 * <p>로그인 성공 시 클라이언트에게 반환하는 JWT 토큰 정보를 담는 API Layer의 DTO입니다.
 * Access Token과 Refresh Token을 모두 포함합니다.</p>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>✅ API Response DTO는 "ApiResponse" suffix 필수</li>
 *   <li>✅ Lombok 금지 - Plain Java getter/setter 사용</li>
 *   <li>✅ Javadoc 완비 - 모든 필드와 메서드에 문서화</li>
 *   <li>✅ API DTO는 Application Layer의 Response와 분리</li>
 * </ul>
 *
 * <p><strong>응답 필드:</strong></p>
 * <ul>
 *   <li>accessToken: Access Token (JWT) - 짧은 유효 기간 (예: 15분)</li>
 *   <li>refreshToken: Refresh Token (JWT) - 긴 유효 기간 (예: 7일)</li>
 *   <li>tokenType: Token 타입 (항상 "Bearer")</li>
 *   <li>expiresIn: Access Token 만료까지 남은 시간 (초 단위)</li>
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
public class LoginApiResponse {

    /**
     * Access Token (JWT) - 짧은 유효 기간.
     */
    private String accessToken;

    /**
     * Refresh Token (JWT) - 긴 유효 기간.
     */
    private String refreshToken;

    /**
     * Token 타입 (항상 "Bearer").
     */
    private String tokenType;

    /**
     * Access Token 만료까지 남은 시간 (초 단위).
     */
    private int expiresIn;

    /**
     * 기본 생성자 (Jackson serialization용).
     */
    public LoginApiResponse() {
    }

    /**
     * 전체 필드 생성자.
     *
     * @param accessToken Access Token
     * @param refreshToken Refresh Token
     * @param tokenType Token 타입
     * @param expiresIn Access Token 만료 시간 (초)
     * @author AuthHub Team
     * @since 1.0.0
     */
    public LoginApiResponse(
            final String accessToken,
            final String refreshToken,
            final String tokenType,
            final int expiresIn
    ) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.tokenType = tokenType;
        this.expiresIn = expiresIn;
    }

    /**
     * accessToken 값을 반환합니다.
     *
     * @return accessToken
     */
    public String getAccessToken() {
        return accessToken;
    }

    /**
     * accessToken 값을 설정합니다.
     *
     * @param accessToken Access Token
     */
    public void setAccessToken(final String accessToken) {
        this.accessToken = accessToken;
    }

    /**
     * refreshToken 값을 반환합니다.
     *
     * @return refreshToken
     */
    public String getRefreshToken() {
        return refreshToken;
    }

    /**
     * refreshToken 값을 설정합니다.
     *
     * @param refreshToken Refresh Token
     */
    public void setRefreshToken(final String refreshToken) {
        this.refreshToken = refreshToken;
    }

    /**
     * tokenType 값을 반환합니다.
     *
     * @return tokenType
     */
    public String getTokenType() {
        return tokenType;
    }

    /**
     * tokenType 값을 설정합니다.
     *
     * @param tokenType Token 타입
     */
    public void setTokenType(final String tokenType) {
        this.tokenType = tokenType;
    }

    /**
     * expiresIn 값을 반환합니다.
     *
     * @return expiresIn
     */
    public int getExpiresIn() {
        return expiresIn;
    }

    /**
     * expiresIn 값을 설정합니다.
     *
     * @param expiresIn Access Token 만료 시간 (초)
     */
    public void setExpiresIn(final int expiresIn) {
        this.expiresIn = expiresIn;
    }
}
