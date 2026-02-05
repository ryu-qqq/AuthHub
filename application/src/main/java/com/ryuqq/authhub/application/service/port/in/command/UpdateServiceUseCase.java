package com.ryuqq.authhub.application.service.port.in.command;

import com.ryuqq.authhub.application.service.dto.command.UpdateServiceCommand;

/**
 * UpdateServiceUseCase - 서비스 수정 UseCase (Port-In)
 *
 * <p>서비스 수정 기능을 정의합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code {Action}{Bc}UseCase} 네이밍
 *   <li>{@code execute()} 메서드 시그니처
 *   <li>Command DTO 파라미터, void 반환 (수정은 ID 반환 불필요)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface UpdateServiceUseCase {

    /**
     * 서비스 수정 실행
     *
     * @param command 서비스 수정 Command
     */
    void execute(UpdateServiceCommand command);
}
