package com.ryuqq.authhub.application.endpointpermission.port.in.command;

import com.ryuqq.authhub.application.endpointpermission.dto.command.CreateEndpointPermissionCommand;
import com.ryuqq.authhub.application.endpointpermission.dto.response.EndpointPermissionResponse;

/**
 * CreateEndpointPermissionUseCase - 엔드포인트 권한 생성 UseCase (Port-In)
 *
 * <p>엔드포인트 권한 생성 기능을 정의합니다.
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
public interface CreateEndpointPermissionUseCase {

    /**
     * 엔드포인트 권한 생성 실행
     *
     * @param command 엔드포인트 권한 생성 Command
     * @return 생성된 엔드포인트 권한 Response
     */
    EndpointPermissionResponse execute(CreateEndpointPermissionCommand command);
}
