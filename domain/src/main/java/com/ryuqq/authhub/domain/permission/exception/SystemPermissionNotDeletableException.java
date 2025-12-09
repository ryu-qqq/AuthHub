package com.ryuqq.authhub.domain.permission.exception;

import com.ryuqq.authhub.domain.common.exception.DomainException;

import java.util.Map;

/**
 * SystemPermissionNotDeletableException - 시스템 권한 삭제 시도 시 발생하는 예외
 *
 * @author development-team
 * @since 1.0.0
 */
public class SystemPermissionNotDeletableException extends DomainException {

    public SystemPermissionNotDeletableException(String permissionKey) {
        super(PermissionErrorCode.SYSTEM_PERMISSION_NOT_DELETABLE, Map.of("permissionKey", permissionKey));
    }
}
