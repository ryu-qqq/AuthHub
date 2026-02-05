package com.ryuqq.authhub.adapter.in.rest.role.mapper;

import com.ryuqq.authhub.adapter.in.rest.role.dto.request.CreateRoleApiRequest;
import com.ryuqq.authhub.adapter.in.rest.role.dto.request.UpdateRoleApiRequest;
import com.ryuqq.authhub.application.role.dto.command.CreateRoleCommand;
import com.ryuqq.authhub.application.role.dto.command.DeleteRoleCommand;
import com.ryuqq.authhub.application.role.dto.command.UpdateRoleCommand;
import org.springframework.stereotype.Component;

/**
 * RoleCommandApiMapper - Role Command API 변환 매퍼
 *
 * <p>API Request와 Application Command 간 변환을 담당합니다.
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
public class RoleCommandApiMapper {

    /**
     * CreateRoleApiRequest -> CreateRoleCommand 변환
     *
     * <p>CUSTOM 역할만 REST API로 생성 가능 (SYSTEM 역할은 내부에서만 생성).
     *
     * @param request API 요청 DTO
     * @return Application Command DTO
     */
    public CreateRoleCommand toCommand(CreateRoleApiRequest request) {
        return new CreateRoleCommand(
                request.tenantId(),
                request.serviceId(),
                request.name(),
                request.displayName(),
                request.description(),
                false // REST API를 통한 생성은 항상 CUSTOM 역할
                );
    }

    /**
     * UpdateRoleApiRequest + PathVariable ID -> UpdateRoleCommand 변환
     *
     * <p>ADTO-004: Update Request에 ID 포함 금지 -> PathVariable에서 전달.
     *
     * @param roleId Role ID (PathVariable)
     * @param request API 요청 DTO
     * @return Application Command DTO
     */
    public UpdateRoleCommand toCommand(Long roleId, UpdateRoleApiRequest request) {
        return new UpdateRoleCommand(roleId, request.displayName(), request.description());
    }

    /**
     * PathVariable ID -> DeleteRoleCommand 변환
     *
     * @param roleId Role ID (PathVariable)
     * @return Application Command DTO
     */
    public DeleteRoleCommand toDeleteCommand(Long roleId) {
        return new DeleteRoleCommand(roleId);
    }
}
