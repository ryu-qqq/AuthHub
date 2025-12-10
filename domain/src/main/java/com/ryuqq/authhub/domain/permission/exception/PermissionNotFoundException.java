package com.ryuqq.authhub.domain.permission.exception;

import com.ryuqq.authhub.domain.common.exception.DomainException;
import com.ryuqq.authhub.domain.permission.identifier.PermissionId;
import java.util.Map;
import java.util.UUID;

/**
 * PermissionNotFoundException - 권한을 찾을 수 없을 때 발생하는 예외
 *
 * @author development-team
 * @since 1.0.0
 */
public class PermissionNotFoundException extends DomainException {

    public PermissionNotFoundException(UUID permissionId) {
        super(PermissionErrorCode.PERMISSION_NOT_FOUND, Map.of("permissionId", permissionId));
    }

    public PermissionNotFoundException(PermissionId permissionId) {
        this(permissionId.value());
    }

    public PermissionNotFoundException(String permissionKey) {
        super(PermissionErrorCode.PERMISSION_NOT_FOUND, Map.of("permissionKey", permissionKey));
    }
}
