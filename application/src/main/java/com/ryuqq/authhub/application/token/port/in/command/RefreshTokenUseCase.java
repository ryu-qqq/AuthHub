package com.ryuqq.authhub.application.token.port.in.command;

import com.ryuqq.authhub.application.token.dto.command.RefreshTokenCommand;
import com.ryuqq.authhub.application.token.dto.response.TokenResponse;

/**
 * RefreshTokenUseCase - 토큰 갱신 UseCase 인터페이스
 *
 * <p>Refresh Token을 사용한 토큰 갱신 기능을 정의합니다.
 *
 * <p><strong>흐름:</strong>
 *
 * <ol>
 *   <li>Refresh Token 유효성 검증
 *   <li>기존 토큰 무효화
 *   <li>새로운 토큰 쌍 발급
 * </ol>
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>Command UseCase 인터페이스
 *   <li>Command DTO 입력, Response DTO 반환
 *   <li>Lombok 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface RefreshTokenUseCase {

    /**
     * 토큰 갱신 수행
     *
     * @param command Refresh Token 커맨드
     * @return 새로운 토큰 응답 (accessToken, refreshToken)
     * @throws com.ryuqq.authhub.domain.auth.exception.InvalidRefreshTokenException 유효하지 않은 Refresh
     *     Token인 경우
     */
    TokenResponse execute(RefreshTokenCommand command);
}
