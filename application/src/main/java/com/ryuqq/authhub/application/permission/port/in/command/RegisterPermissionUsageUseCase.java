package com.ryuqq.authhub.application.permission.port.in.command;

import com.ryuqq.authhub.application.permission.dto.command.RegisterPermissionUsageCommand;
import com.ryuqq.authhub.application.permission.dto.response.PermissionUsageResponse;

/**
 * RegisterPermissionUsageUseCase - 권한 사용 이력 등록 UseCase (Port-In)
 *
 * <p>권한 사용 이력 등록 기능을 정의합니다.
 *
 * <p><strong>사용 시나리오:</strong>
 *
 * <ul>
 *   <li>n8n에서 승인 후 권한 사용 이력 자동 등록
 *   <li>동일 권한+서비스 조합이 이미 존재하면 업데이트 (UPSERT)
 * </ul>
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
public interface RegisterPermissionUsageUseCase {

    /**
     * 권한 사용 이력 등록/업데이트 실행
     *
     * @param command 권한 사용 이력 등록 Command
     * @return 등록/업데이트된 사용 이력 Response
     */
    PermissionUsageResponse execute(RegisterPermissionUsageCommand command);
}
