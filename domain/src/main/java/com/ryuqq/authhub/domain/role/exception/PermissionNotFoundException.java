package com.ryuqq.authhub.domain.role.exception;

import com.ryuqq.authhub.domain.common.exception.DomainException;
import java.util.Map;

/**
 * PermissionNotFoundException - 권한을 찾을 수 없는 예외
 *
 * <p>요청한 권한이 존재하지 않을 때 발생합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public class PermissionNotFoundException extends DomainException {

    public PermissionNotFoundException(Long permissionId) {
        super(RoleErrorCode.PERMISSION_NOT_FOUND, Map.of("permissionId", permissionId));
    }

    public PermissionNotFoundException(String permissionCode) {
        super(RoleErrorCode.PERMISSION_NOT_FOUND, Map.of("permissionCode", permissionCode));
    }
}
