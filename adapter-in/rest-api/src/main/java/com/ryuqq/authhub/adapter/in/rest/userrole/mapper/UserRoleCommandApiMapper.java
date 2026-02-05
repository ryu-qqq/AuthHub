package com.ryuqq.authhub.adapter.in.rest.userrole.mapper;

import com.ryuqq.authhub.adapter.in.rest.userrole.dto.request.AssignUserRoleApiRequest;
import com.ryuqq.authhub.adapter.in.rest.userrole.dto.request.RevokeUserRoleApiRequest;
import com.ryuqq.authhub.application.userrole.dto.command.AssignUserRoleCommand;
import com.ryuqq.authhub.application.userrole.dto.command.RevokeUserRoleCommand;
import java.util.Objects;
import org.springframework.stereotype.Component;

/**
 * UserRoleCommandApiMapper - UserRole Command API 변환 매퍼
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
public class UserRoleCommandApiMapper {

    /**
     * AssignUserRoleApiRequest + PathVariable ID -> AssignUserRoleCommand 변환
     *
     * <p>ADTO-004: Update Request에 ID 포함 금지 -> PathVariable에서 전달.
     *
     * @param userId User ID (PathVariable)
     * @param request API 요청 DTO
     * @return Application Command DTO
     */
    public AssignUserRoleCommand toAssignCommand(String userId, AssignUserRoleApiRequest request) {
        Objects.requireNonNull(userId, "userId must not be null");
        return new AssignUserRoleCommand(userId, request.roleIds());
    }

    /**
     * RevokeUserRoleApiRequest + PathVariable ID -> RevokeUserRoleCommand 변환
     *
     * <p>ADTO-004: Update Request에 ID 포함 금지 -> PathVariable에서 전달.
     *
     * @param userId User ID (PathVariable)
     * @param request API 요청 DTO
     * @return Application Command DTO
     */
    public RevokeUserRoleCommand toRevokeCommand(String userId, RevokeUserRoleApiRequest request) {
        Objects.requireNonNull(userId, "userId must not be null");
        return new RevokeUserRoleCommand(userId, request.roleIds());
    }
}
