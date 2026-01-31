package com.ryuqq.authhub.adapter.in.rest.token;

/**
 * TokenApiEndpoints - 토큰/인증 API 경로 상수
 *
 * <p>로그인, 로그아웃, 토큰 갱신, 공개키 목록 조회 등 토큰 관련 API 경로를 정의합니다.
 *
 * <p><strong>엔드포인트:</strong>
 *
 * <ul>
 *   <li>POST /api/v1/auth/login - 로그인
 *   <li>POST /api/v1/auth/logout - 로그아웃
 *   <li>POST /api/v1/auth/refresh - 토큰 갱신
 *   <li>GET /api/v1/auth/jwks - 공개키 목록 조회
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public final class TokenApiEndpoints {

    /** 토큰 API 기본 경로 */
    public static final String BASE = "/api/v1/auth";

    /** 로그인 */
    public static final String LOGIN = "/login";

    /** 로그아웃 */
    public static final String LOGOUT = "/logout";

    /** 토큰 갱신 */
    public static final String REFRESH = "/refresh";

    /** 공개키 목록 조회 (RFC 7517 JWKS 형식) */
    public static final String JWKS = "/jwks";

    /** 내 정보 조회 */
    public static final String ME = "/me";

    private TokenApiEndpoints() {}
}
