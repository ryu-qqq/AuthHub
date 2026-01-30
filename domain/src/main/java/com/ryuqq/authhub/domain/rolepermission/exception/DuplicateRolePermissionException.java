package com.ryuqq.authhub.domain.rolepermission.exception;

import com.ryuqq.authhub.domain.common.exception.DomainException;
import com.ryuqq.authhub.domain.permission.id.PermissionId;
import com.ryuqq.authhub.domain.role.id.RoleId;
import java.util.Map;

/**
 * DuplicateRolePermissionException - 중복된 역할-권한 관계 예외
 *
 * <p>이미 부여된 권한을 다시 부여하려 할 때 발생합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public class DuplicateRolePermissionException extends DomainException {

    public DuplicateRolePermissionException(RoleId roleId, PermissionId permissionId) {
        super(
                RolePermissionErrorCode.DUPLICATE_ROLE_PERMISSION,
                Map.of("roleId", roleId.value(), "permissionId", permissionId.value()));
    }

    public DuplicateRolePermissionException(Long roleId, Long permissionId) {
        super(
                RolePermissionErrorCode.DUPLICATE_ROLE_PERMISSION,
                Map.of("roleId", roleId, "permissionId", permissionId));
    }
}
