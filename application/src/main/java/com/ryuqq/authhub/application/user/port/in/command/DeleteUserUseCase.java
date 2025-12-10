package com.ryuqq.authhub.application.user.port.in.command;

import com.ryuqq.authhub.application.user.dto.command.DeleteUserCommand;

/**
 * DeleteUserUseCase - 사용자 삭제 Port-In
 *
 * <p>사용자 삭제 비즈니스 로직의 진입점입니다.
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
public interface DeleteUserUseCase {

    /**
     * 사용자 삭제 실행
     *
     * @param command 삭제 Command
     */
    void execute(DeleteUserCommand command);
}
