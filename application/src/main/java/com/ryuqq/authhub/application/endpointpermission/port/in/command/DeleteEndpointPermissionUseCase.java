package com.ryuqq.authhub.application.endpointpermission.port.in.command;

import com.ryuqq.authhub.application.endpointpermission.dto.command.DeleteEndpointPermissionCommand;

/**
 * DeleteEndpointPermissionUseCase - 엔드포인트 권한 삭제 UseCase (Port-In)
 *
 * <p>엔드포인트 권한 삭제 기능을 정의합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code {Action}{Bc}UseCase} 네이밍
 *   <li>{@code execute()} 메서드 시그니처
 *   <li>Command DTO 파라미터, void 반환 (CQRS 순수 패턴)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface DeleteEndpointPermissionUseCase {

    /**
     * 엔드포인트 권한 삭제 실행
     *
     * @param command 엔드포인트 권한 삭제 Command
     */
    void execute(DeleteEndpointPermissionCommand command);
}
