package com.ryuqq.authhub.application.user.port.in.command;

import com.ryuqq.authhub.application.user.dto.command.RevokeUserRoleCommand;

/**
 * RevokeUserRoleUseCase - 사용자 역할 해제 Port-In
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>단일 execute 메서드
 *   <li>Command → void (해제 작업)
 *   <li>Lombok 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface RevokeUserRoleUseCase {

    /**
     * 사용자의 역할을 해제합니다.
     *
     * @param command 역할 해제 커맨드
     */
    void execute(RevokeUserRoleCommand command);
}
