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
 *   <li>✅ Java 21 Record 사용 - Boilerplate 코드 제거, 불변성 보장</li>
 *   <li>✅ Javadoc 완비 - 모든 필드에 문서화</li>
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
 * @param accessToken Access Token (JWT) - 짧은 유효 기간
 * @param refreshToken Refresh Token (JWT) - 긴 유효 기간
 * @param tokenType Token 타입 (항상 "Bearer")
 * @param expiresIn Access Token 만료까지 남은 시간 (초 단위)
 * @author AuthHub Team
 * @since 1.0.0
 */
public record LoginApiResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        int expiresIn
) {
    // Record는 자동으로 다음을 생성합니다:
    // - Getter: accessToken(), refreshToken(), tokenType(), expiresIn()
    // - Constructor: new LoginApiResponse(accessToken, refreshToken, tokenType, expiresIn)
    // - equals(), hashCode(), toString()
    // - 모든 필드는 private final (불변)
}
