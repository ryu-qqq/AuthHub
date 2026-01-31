package com.ryuqq.authhub.application.token.port.in.command;

import com.ryuqq.authhub.application.token.dto.command.LoginCommand;
import com.ryuqq.authhub.application.token.dto.response.LoginResponse;

/**
 * LoginUseCase - 로그인 UseCase 인터페이스
 *
 * <p>사용자 로그인 기능을 정의합니다.
 *
 * <p><strong>흐름:</strong>
 *
 * <ol>
 *   <li>Tenant/Identifier로 사용자 조회
 *   <li>비밀번호 검증
 *   <li>사용자 상태 검증 (ACTIVE만 로그인 가능)
 *   <li>토큰 발급 및 반환
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
public interface LoginUseCase {

    /**
     * 로그인 수행
     *
     * @param command 로그인 커맨드 (tenantId, identifier, password)
     * @return 로그인 응답 (userId, accessToken, refreshToken)
     * @throws com.ryuqq.authhub.domain.auth.exception.InvalidCredentialsException 자격 증명 실패 시
     * @throws com.ryuqq.authhub.domain.user.exception.InvalidUserStateException 비활성 사용자 로그인 시도 시
     */
    LoginResponse execute(LoginCommand command);
}
