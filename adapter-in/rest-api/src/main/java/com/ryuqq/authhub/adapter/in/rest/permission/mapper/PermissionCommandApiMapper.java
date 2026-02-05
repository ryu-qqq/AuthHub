package com.ryuqq.authhub.adapter.in.rest.permission.mapper;

import com.ryuqq.authhub.adapter.in.rest.permission.dto.request.CreatePermissionApiRequest;
import com.ryuqq.authhub.adapter.in.rest.permission.dto.request.UpdatePermissionApiRequest;
import com.ryuqq.authhub.application.permission.dto.command.CreatePermissionCommand;
import com.ryuqq.authhub.application.permission.dto.command.DeletePermissionCommand;
import com.ryuqq.authhub.application.permission.dto.command.UpdatePermissionCommand;
import org.springframework.stereotype.Component;

/**
 * PermissionCommandApiMapper - Permission Command API 변환 매퍼 (Global Only)
 *
 * <p>API Request와 Application Command 간 변환을 담당합니다.
 *
 * <p><strong>Global Only 설계:</strong>
 *
 * <ul>
 *   <li>모든 Permission은 전체 시스템에서 공유됩니다
 *   <li>테넌트 관련 변환 로직이 제거되었습니다
 * </ul>
 *
 * <p>MAPPER-001: Mapper는 @Component로 등록.
 *
 * <p>MAPPER-002: API Request -> Application Command 변환.
 *
 * <p>MAPPER-004: Domain 타입 직접 의존 금지.
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class PermissionCommandApiMapper {

    /**
     * CreatePermissionApiRequest -> CreatePermissionCommand 변환
     *
     * <p>CUSTOM 권한만 REST API로 생성 가능 (SYSTEM 권한은 내부에서만 생성).
     *
     * @param request API 요청 DTO
     * @return Application Command DTO
     */
    public CreatePermissionCommand toCommand(CreatePermissionApiRequest request) {
        return new CreatePermissionCommand(
                request.serviceId(),
                request.resource(),
                request.action(),
                request.description(),
                false // REST API를 통한 생성은 항상 CUSTOM 권한
                );
    }

    /**
     * UpdatePermissionApiRequest + PathVariable ID -> UpdatePermissionCommand 변환
     *
     * <p>ADTO-004: Update Request에 ID 포함 금지 -> PathVariable에서 전달.
     *
     * @param permissionId Permission ID (PathVariable)
     * @param request API 요청 DTO
     * @return Application Command DTO
     */
    public UpdatePermissionCommand toCommand(
            Long permissionId, UpdatePermissionApiRequest request) {
        return new UpdatePermissionCommand(permissionId, request.description());
    }

    /**
     * PathVariable ID -> DeletePermissionCommand 변환
     *
     * @param permissionId Permission ID (PathVariable)
     * @return Application Command DTO
     */
    public DeletePermissionCommand toDeleteCommand(Long permissionId) {
        return new DeletePermissionCommand(permissionId);
    }
}
