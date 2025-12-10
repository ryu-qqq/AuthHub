package com.ryuqq.authhub.application.organization.port.in.command;

import com.ryuqq.authhub.application.organization.dto.command.UpdateOrganizationCommand;
import com.ryuqq.authhub.application.organization.dto.response.OrganizationResponse;

/**
 * UpdateOrganizationUseCase - 조직 수정 UseCase (Port-In)
 *
 * <p>조직 이름 수정 기능을 정의합니다.
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
public interface UpdateOrganizationUseCase {

    /**
     * 조직 수정 실행
     *
     * @param command 조직 수정 Command
     * @return 수정된 조직 Response
     */
    OrganizationResponse execute(UpdateOrganizationCommand command);
}
