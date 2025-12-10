package com.ryuqq.authhub.application.user.port.in.command;

import com.ryuqq.authhub.application.user.dto.command.UpdateUserPasswordCommand;

/**
 * UpdateUserPasswordUseCase - 비밀번호 변경 Port-In
 *
 * <p>비밀번호 변경 비즈니스 로직의 진입점입니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>*UseCase 네이밍 규칙
 *   <li>단일 메서드 (execute)
 *   <li>Command DTO 입력
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface UpdateUserPasswordUseCase {

    /**
     * 비밀번호 변경 실행
     *
     * @param command 비밀번호 변경 Command
     */
    void execute(UpdateUserPasswordCommand command);
}
