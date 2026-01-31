package com.ryuqq.authhub.domain.rolepermission.exception;

import com.ryuqq.authhub.domain.common.exception.DomainException;
import com.ryuqq.authhub.domain.permission.id.PermissionId;
import com.ryuqq.authhub.domain.role.id.RoleId;
import java.util.Map;

/**
 * RolePermissionNotFoundException - 역할-권한 관계를 찾을 수 없을 때 발생하는 예외
 *
 * <p>존재하지 않는 역할-권한 관계를 조회하거나 삭제하려 할 때 발생합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public class RolePermissionNotFoundException extends DomainException {

    public RolePermissionNotFoundException(RoleId roleId, PermissionId permissionId) {
        super(
                RolePermissionErrorCode.ROLE_PERMISSION_NOT_FOUND,
                Map.of("roleId", roleId.value(), "permissionId", permissionId.value()));
    }

    public RolePermissionNotFoundException(Long roleId, Long permissionId) {
        super(
                RolePermissionErrorCode.ROLE_PERMISSION_NOT_FOUND,
                Map.of("roleId", roleId, "permissionId", permissionId));
    }
}
