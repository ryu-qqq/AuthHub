package com.ryuqq.authhub.application.user.port.in.command;

import com.ryuqq.authhub.application.user.dto.command.CreateUserWithRolesCommand;
import com.ryuqq.authhub.application.user.dto.response.CreateUserWithRolesResult;

/**
 * CreateUserWithRolesUseCase - 사용자 생성 + 역할 할당 UseCase (Port-In)
 *
 * <p>사용자 생성과 SERVICE scope Role 할당을 하나의 트랜잭션으로 처리합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code {Action}{Bc}UseCase} 네이밍
 *   <li>{@code execute()} 메서드 시그니처
 *   <li>Command DTO 파라미터, Result 반환
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface CreateUserWithRolesUseCase {

    /**
     * 사용자 생성 + 역할 할당 실행
     *
     * @param command 사용자 생성 + 역할 할당 Command
     * @return 생성 결과 (userId, assignedRoleCount)
     */
    CreateUserWithRolesResult execute(CreateUserWithRolesCommand command);
}
