package com.ryuqq.authhub.domain.rolepermission.exception;

import com.ryuqq.authhub.domain.common.exception.DomainException;
import com.ryuqq.authhub.domain.permission.id.PermissionId;
import java.util.Map;

/**
 * PermissionInUseException - 권한이 역할에 사용 중일 때 발생하는 예외
 *
 * <p>삭제하려는 권한이 하나 이상의 역할에 부여되어 있을 때 발생합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public class PermissionInUseException extends DomainException {

    public PermissionInUseException(PermissionId permissionId) {
        super(
                RolePermissionErrorCode.PERMISSION_IN_USE,
                Map.of("permissionId", permissionId.value()));
    }

    public PermissionInUseException(Long permissionId) {
        super(RolePermissionErrorCode.PERMISSION_IN_USE, Map.of("permissionId", permissionId));
    }
}
