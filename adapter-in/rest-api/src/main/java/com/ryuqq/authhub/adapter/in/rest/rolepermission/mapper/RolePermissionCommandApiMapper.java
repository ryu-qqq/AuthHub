package com.ryuqq.authhub.adapter.in.rest.rolepermission.mapper;

import com.ryuqq.authhub.adapter.in.rest.rolepermission.dto.request.GrantRolePermissionApiRequest;
import com.ryuqq.authhub.adapter.in.rest.rolepermission.dto.request.RevokeRolePermissionApiRequest;
import com.ryuqq.authhub.application.rolepermission.dto.command.GrantRolePermissionCommand;
import com.ryuqq.authhub.application.rolepermission.dto.command.RevokeRolePermissionCommand;
import org.springframework.stereotype.Component;

/**
 * RolePermissionCommandApiMapper - RolePermission API 요청 → Application Command 변환 Mapper
 *
 * <p>역할-권한 관계 관련 API 요청을 Application 레이어의 Command로 변환합니다.
 *
 * <p>CTR-007: Controller 비즈니스 로직 금지 → Mapper에서 처리.
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class RolePermissionCommandApiMapper {

    /**
     * 권한 부여 요청 → Command 변환
     *
     * @param roleId 역할 ID
     * @param request API 요청 DTO
     * @return GrantRolePermissionCommand
     */
    public GrantRolePermissionCommand toGrantCommand(
            Long roleId, GrantRolePermissionApiRequest request) {
        return new GrantRolePermissionCommand(roleId, request.permissionIds());
    }

    /**
     * 권한 제거 요청 → Command 변환
     *
     * @param roleId 역할 ID
     * @param request API 요청 DTO
     * @return RevokeRolePermissionCommand
     */
    public RevokeRolePermissionCommand toRevokeCommand(
            Long roleId, RevokeRolePermissionApiRequest request) {
        return new RevokeRolePermissionCommand(roleId, request.permissionIds());
    }
}
