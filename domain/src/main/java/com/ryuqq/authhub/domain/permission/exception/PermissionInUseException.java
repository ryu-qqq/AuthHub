package com.ryuqq.authhub.domain.permission.exception;

import com.ryuqq.authhub.domain.common.exception.DomainException;
import com.ryuqq.authhub.domain.permission.id.PermissionId;
import java.util.Map;

/**
 * PermissionInUseException - 권한이 사용 중일 때 발생하는 예외
 *
 * <p>Role에 할당되어 사용 중인 권한을 삭제하려고 할 때 발생합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public class PermissionInUseException extends DomainException {

    public PermissionInUseException(Long permissionId) {
        super(PermissionErrorCode.PERMISSION_IN_USE, Map.of("permissionId", permissionId));
    }

    public PermissionInUseException(PermissionId permissionId) {
        this(permissionId.value());
    }

    public PermissionInUseException(String permissionKey) {
        super(PermissionErrorCode.PERMISSION_IN_USE, Map.of("permissionKey", permissionKey));
    }
}
