package com.ryuqq.authhub.application.organization.port.in.command;

import com.ryuqq.authhub.application.organization.dto.command.UpdateOrganizationStatusCommand;

/**
 * UpdateOrganizationStatusUseCase - 조직 상태 변경 UseCase (Port-In)
 *
 * <p>조직 상태 변경 기능을 정의합니다 (활성화/비활성화).
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
public interface UpdateOrganizationStatusUseCase {

    /**
     * 조직 상태 변경 실행
     *
     * @param command 조직 상태 변경 Command
     */
    void execute(UpdateOrganizationStatusCommand command);
}
