package com.ryuqq.authhub.application.service.port.in.command;

import com.ryuqq.authhub.application.service.dto.command.CreateServiceCommand;

/**
 * CreateServiceUseCase - 서비스 생성 UseCase (Port-In)
 *
 * <p>서비스 생성 기능을 정의합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code {Action}{Bc}UseCase} 네이밍
 *   <li>{@code execute()} 메서드 시그니처
 *   <li>Command DTO 파라미터, ID 반환 (Long 원시 타입)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface CreateServiceUseCase {

    /**
     * 서비스 생성 실행
     *
     * @param command 서비스 생성 Command
     * @return 생성된 서비스 ID (Long)
     */
    Long execute(CreateServiceCommand command);
}
