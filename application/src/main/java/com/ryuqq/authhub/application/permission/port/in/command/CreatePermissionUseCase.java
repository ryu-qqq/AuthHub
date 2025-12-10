package com.ryuqq.authhub.application.permission.port.in.command;

import com.ryuqq.authhub.application.permission.dto.command.CreatePermissionCommand;
import com.ryuqq.authhub.application.permission.dto.response.PermissionResponse;

/**
 * CreatePermissionUseCase - 권한 생성 UseCase (Port-In)
 *
 * <p>권한 생성 기능을 정의합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code {Action}{Bc}UseCase} 네이밍
 *   <li>{@code execute()} 메서드 시그니처
 *   <li>Command DTO 파라미터, Response DTO 반환
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface CreatePermissionUseCase {

    /**
     * 권한 생성 실행
     *
     * @param command 권한 생성 Command
     * @return 생성된 권한 Response
     */
    PermissionResponse execute(CreatePermissionCommand command);
}
