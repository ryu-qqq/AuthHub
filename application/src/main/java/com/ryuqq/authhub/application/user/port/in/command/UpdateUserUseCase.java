package com.ryuqq.authhub.application.user.port.in.command;

import com.ryuqq.authhub.application.user.dto.command.UpdateUserCommand;

/**
 * UpdateUserUseCase - 사용자 정보 수정 UseCase (Port-In)
 *
 * <p>사용자 정보 수정 기능을 정의합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code {Action}{Bc}UseCase} 네이밍
 *   <li>{@code execute()} 메서드 시그니처
 *   <li>Command DTO 파라미터, void 반환
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface UpdateUserUseCase {

    /**
     * 사용자 정보 수정 실행
     *
     * @param command 사용자 수정 Command
     */
    void execute(UpdateUserCommand command);
}
