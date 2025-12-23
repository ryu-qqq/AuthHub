package com.ryuqq.authhub.application.permission.port.in.command;

import com.ryuqq.authhub.application.permission.dto.command.ValidatePermissionsCommand;
import com.ryuqq.authhub.application.permission.dto.response.ValidatePermissionsResult;

/**
 * ValidatePermissionsUseCase - 권한 검증 UseCase (Port-In)
 *
 * <p>CI/CD 파이프라인에서 서비스의 @PreAuthorize 권한들이 AuthHub에 등록되어 있는지 검증합니다.
 *
 * <p><strong>사용 시나리오:</strong>
 *
 * <ol>
 *   <li>CI/CD에서 PermissionScanner 실행 → permissions.json 생성
 *   <li>permissions.json을 AuthHub API로 전송하여 검증
 *   <li>누락된 권한이 있으면 배포 실패 또는 Slack 알림
 * </ol>
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code {Action}{Bc}UseCase} 네이밍
 *   <li>{@code execute()} 메서드 시그니처
 *   <li>Command DTO 파라미터, Result DTO 반환
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface ValidatePermissionsUseCase {

    /**
     * 권한 검증 실행
     *
     * <p>요청된 권한 목록이 AuthHub DB에 등록되어 있는지 확인합니다.
     *
     * @param command 권한 검증 Command (서비스명, 권한 목록)
     * @return 검증 결과 (valid, missing, existing 정보 포함)
     */
    ValidatePermissionsResult execute(ValidatePermissionsCommand command);
}
