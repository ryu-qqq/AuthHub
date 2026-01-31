package com.ryuqq.authhub.application.user.port.in.command;

import com.ryuqq.authhub.application.user.dto.command.CreateUserCommand;

/**
 * CreateUserUseCase - 사용자 생성 UseCase (Port-In)
 *
 * <p>사용자 생성 기능을 정의합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code {Action}{Bc}UseCase} 네이밍
 *   <li>{@code execute()} 메서드 시그니처
 *   <li>Command DTO 파라미터, ID 반환 (String - UUIDv7)
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
     * @return 생성된 사용자 ID (String - UUIDv7)
     */
    String execute(CreateUserCommand command);
}
