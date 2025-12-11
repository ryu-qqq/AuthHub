package com.ryuqq.authhub.application.user.port.in.command;

import com.ryuqq.authhub.application.user.dto.command.AssignUserRoleCommand;
import com.ryuqq.authhub.application.user.dto.response.UserRoleResponse;

/**
 * AssignUserRoleUseCase - 사용자 역할 할당 Port-In
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>단일 execute 메서드
 *   <li>Command → Response 변환
 *   <li>Lombok 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface AssignUserRoleUseCase {

    /**
     * 사용자에게 역할을 할당합니다.
     *
     * @param command 역할 할당 커맨드
     * @return 할당된 역할 정보
     */
    UserRoleResponse execute(AssignUserRoleCommand command);
}
