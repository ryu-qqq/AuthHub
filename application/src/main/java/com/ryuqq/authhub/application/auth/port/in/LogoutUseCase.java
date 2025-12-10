package com.ryuqq.authhub.application.auth.port.in;

import com.ryuqq.authhub.application.auth.dto.command.LogoutCommand;

/**
 * LogoutUseCase - 로그아웃 유스케이스 인터페이스
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
