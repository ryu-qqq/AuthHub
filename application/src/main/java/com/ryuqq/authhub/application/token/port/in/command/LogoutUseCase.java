package com.ryuqq.authhub.application.token.port.in.command;

import com.ryuqq.authhub.application.token.dto.command.LogoutCommand;

/**
 * LogoutUseCase - 로그아웃 UseCase 인터페이스
 *
 * <p>사용자 로그아웃 기능을 정의합니다.
 *
 * <p><strong>흐름:</strong>
 *
 * <ol>
 *   <li>사용자 ID로 저장된 모든 Refresh Token 무효화
 *   <li>Cache와 RDB 양쪽에서 삭제
 * </ol>
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>Command UseCase 인터페이스
 *   <li>Command DTO 입력, void 반환
 *   <li>Lombok 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface LogoutUseCase {

    /**
     * 로그아웃 수행
     *
     * @param command 로그아웃 커맨드 (userId)
     */
    void execute(LogoutCommand command);
}
