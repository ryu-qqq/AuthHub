package com.ryuqq.authhub.application.user.port.in.command;

import com.ryuqq.authhub.application.user.dto.command.CreateUserCommand;
import com.ryuqq.authhub.application.user.dto.response.UserResponse;

/**
 * CreateUserUseCase - 사용자 생성 Port-In
 *
 * <p>사용자 생성 비즈니스 로직의 진입점입니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>*UseCase 네이밍 규칙
 *   <li>단일 메서드 (execute)
 *   <li>Command DTO 입력, Response DTO 출력
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface CreateUserUseCase {

    /**
     * 사용자 생성 실행
     *
     * @param command 사용자 생성 Command
     * @return 생성된 사용자 응답
     */
    UserResponse execute(CreateUserCommand command);
}
