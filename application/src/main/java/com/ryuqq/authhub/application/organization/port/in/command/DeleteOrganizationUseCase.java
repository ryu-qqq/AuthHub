package com.ryuqq.authhub.application.organization.port.in.command;

import com.ryuqq.authhub.application.organization.dto.command.DeleteOrganizationCommand;

/**
 * DeleteOrganizationUseCase - 조직 삭제 UseCase (Port-In)
 *
 * <p>조직 삭제(Soft Delete) 기능을 정의합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code {Action}{Bc}UseCase} 네이밍
 *   <li>{@code execute()} 메서드 시그니처
 *   <li>Command DTO 파라미터
 *   <li>void 반환 (삭제는 결과 없음)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface DeleteOrganizationUseCase {

    /**
     * 조직 삭제 실행
     *
     * @param command 조직 삭제 Command
     */
    void execute(DeleteOrganizationCommand command);
}
